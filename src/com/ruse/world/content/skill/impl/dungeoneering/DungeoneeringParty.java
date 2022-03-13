package com.ruse.world.content.skill.impl.dungeoneering;

import java.util.concurrent.CopyOnWriteArrayList;

import com.ruse.GameSettings;
import com.ruse.model.Flag;
import com.ruse.model.GroundItem;
import com.ruse.model.Locations.Location;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.impl.DungPartyInvitation;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * @author Gabriel Hannason
 */
public class DungeoneeringParty {

	public DungeoneeringParty(Player owner) {
		this.owner = owner;
		player_members = new CopyOnWriteArrayList<Player>();
		player_members.add(owner);
	}

	private Player owner;
	private DungeoneeringFloor floor;
	private int complexity = -1;
	private CopyOnWriteArrayList<Player> player_members;
	private CopyOnWriteArrayList<NPC> npc_members = new CopyOnWriteArrayList<NPC>();
	private CopyOnWriteArrayList<GroundItem> ground_items = new CopyOnWriteArrayList<GroundItem>();
	private Position gatestonePosition;
	private boolean hasEnteredDungeon;
	private int kills, deaths;
	private boolean killedBoss;

	public void invite(Player p) {
		if(getOwner() == null || p == getOwner())
			return;
		if(hasEnteredDungeon) {
			getOwner().getPacketSender().sendMessage("You cannot invite anyone right now.");
			return;
		}
		if(player_members.size() >= 5) {
			getOwner().getPacketSender().sendMessage("Your party is full.");
			return;
		}
		if(p.getLocation() != Location.DUNGEONEERING || p.isTeleporting()) {
			getOwner().getPacketSender().sendMessage("That player is not in Deamonheim.");
			return;
		}
		if(player_members.contains(p)) {
			getOwner().getPacketSender().sendMessage("That player is already in your party.");
			return;
		}
		if(p.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
			getOwner().getPacketSender().sendMessage("That player is currently in another party.");
			return;
		}
		if(p.getRights() != PlayerRights.DEVELOPER && System.currentTimeMillis() - getOwner().getMinigameAttributes().getDungeoneeringAttributes().getLastInvitation() < 2000) {
			getOwner().getPacketSender().sendMessage("You must wait 2 seconds between each party invitation.");
			return;
		}
		if(p.busy()) {
			getOwner().getPacketSender().sendMessage("That player is currently busy.");
			return;
		}
		getOwner().getMinigameAttributes().getDungeoneeringAttributes().setLastInvitation(System.currentTimeMillis());
		DialogueManager.start(p, new DungPartyInvitation(getOwner(), p));
		getOwner().getPacketSender().sendMessage("An invitation has been sent to "+p.getUsername()+".");
	}

	public void add(Player p) {
		if(player_members.size() >= 5) {
			p.getPacketSender().sendMessage("That party is already full.");
			return;
		}
		if(hasEnteredDungeon) {
			p.getPacketSender().sendMessage("This party has already entered a dungeon.");
			return;
		}
		if(p.getLocation() != Location.DUNGEONEERING || p.isTeleporting()) {
			return;
		}
		sendMessage(""+p.getUsername()+" has joined the party.");
		p.getPacketSender().sendMessage("You've joined "+getOwner().getUsername()+"'s party.");
		player_members.add(p);
		p.getMinigameAttributes().getDungeoneeringAttributes().setParty(owner.getMinigameAttributes().getDungeoneeringAttributes().getParty());
		p.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, Dungeoneering.PARTY_INTERFACE);
		p.getPacketSender().sendDungeoneeringTabIcon(true);
		p.getPacketSender().sendTab(GameSettings.QUESTS_TAB);
		refreshInterface();
	}

	public void remove(Player p, boolean resetTab, boolean fromParty) {
		if(fromParty) {
			player_members.remove(p);
			if(resetTab) {
				p.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, p.isKillsTrackerOpen() ? 55000 : 639);
				p.getPacketSender().sendDungeoneeringTabIcon(false);
				p.getPacketSender().sendTab(GameSettings.QUESTS_TAB);
			} else {
				p.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, Dungeoneering.FORM_PARTY_INTERFACE);
				p.getPacketSender().sendDungeoneeringTabIcon(true);
				p.getPacketSender().sendTab(GameSettings.QUESTS_TAB);
			}
		}
		p.getPacketSender().sendInterfaceRemoval();
		if(p == owner) {
			for(Player member : player_members) {
				if(member != null && member.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null && member.getMinigameAttributes().getDungeoneeringAttributes().getParty() == this) {
					if(member == owner)
						continue;
					if(fromParty) {
						member.getPacketSender().sendMessage("Your party has been deleted by the party's leader.");
						remove(member, false, true);
						member.setInDung(false);
						} else {
						remove(member, false, false);
						member.setInDung(false);
					}
				}
			}
			if(hasEnteredDungeon) {
				for(NPC npc : p.getMinigameAttributes().getDungeoneeringAttributes().getParty().getNpcs()) {
					if(npc != null && npc.getPosition().getZ() == p.getPosition().getZ())
						World.deregister(npc);
				}
				for(GroundItem groundItem : p.getMinigameAttributes().getDungeoneeringAttributes().getParty().getGroundItems()) {
					if(groundItem != null)
						GroundItemManager.remove(groundItem, true);
				}
			}
		} else {
			if(fromParty) {
				sendMessage(p.getUsername()+" has left the party.");
				if(hasEnteredDungeon) {
					if(p.getInventory().contains(Dungeoneering.DUNGEONEERING_GATESTONE_ID)) {
						p.getInventory().delete(Dungeoneering.DUNGEONEERING_GATESTONE_ID, 1);
						getOwner().getInventory().add(Dungeoneering.DUNGEONEERING_GATESTONE_ID, 1);
					}
				}
			}
		}
		if(hasEnteredDungeon) {
			p.getEquipment().resetItems().refreshItems();
			p.getInventory().resetItems().refreshItems();
			p.restart();
			p.getUpdateFlag().flag(Flag.APPEARANCE);
			p.moveTo(new Position(3450, 3715));
			int damage = p.getMinigameAttributes().getDungeoneeringAttributes().getDamageDealt();
			int deaths = p.getMinigameAttributes().getDungeoneeringAttributes().getDeaths();
			int exp = damage/100 * p.getSkillManager().getCurrentLevel(Skill.DUNGEONEERING) * complexity;
			if (killedBoss) {
				exp = damage/90 * p.getSkillManager().getCurrentLevel(Skill.DUNGEONEERING) * (complexity+2);
			}
			if (p.getSkillManager().getCurrentLevel(Skill.DUNGEONEERING) < 15) {
				exp *= 25;
			}
			if (deaths > 1) {
				exp *= (1 - (0.05*deaths));
			}
			int tokens = exp / 10;
			//World.sendMessage("YOU'VE DONE "+damage+" DMG");
			if(exp > 0 && tokens > 0) {
				/* if(player_members.size() == 1) {
					exp = (int) (exp * 0.7);
					tokens = (int) (tokens * 0.7);
				} */
				if (damage > 2000 || p.getSkillManager().getCurrentLevel(Skill.DUNGEONEERING) < 15) {
					int count = exp * 2;
					p.getPacketSender().sendMessage("<img=10> <col=660000>You've recieved "+Misc.format(count)+" floor experience, and "+Misc.format(tokens)+" tokens.");
					if (p.getMinutesBonusExp() != -1){
						count *= 1.3;
						p.getPacketSender().sendMessage("<col=660000>Your bonus EXP INCREASED the amount to "+Misc.format(count)+".");
					}
					if (Misc.isWeekend()) {
						count *= 2;
						p.getPacketSender().sendMessage("<col=660000>The bonus EXP weekend DOUBLED the amount to "+Misc.format(count)+".");
					}
					p.getPacketSender().sendMessage("<col=660000>You "+ (killedBoss ? "killed the boss" : "didn't kill the boss") + ", and dealt "+Misc.format(damage)+" damage.");
					p.getSkillManager().addExperience(Skill.DUNGEONEERING, exp);
					p.getPointsHandler().setDungeoneeringTokens(tokens, true);
				} else {
					p.getPacketSender().sendMessage("You must do more damage to recieve experience and tokens.");
				}
				//p.getPacketSender().sendMessage("DMG: "+damage);
				PlayerPanel.refreshPanel(p);
			}
			if(p == owner) {
				hasEnteredDungeon = killedBoss = false;
				kills = deaths = 0;
				setGatestonePosition(null);
			}
		}
		if(fromParty) {
			p.getMinigameAttributes().getDungeoneeringAttributes().setParty(null);
			refreshInterface();
		}
		p.getPacketSender().sendInterfaceRemoval();
	}

	public void refreshInterface() {
		for(Player member : getPlayers()) {
			if(member != null) {
				for(int s = 26236; s < 26240; s++)
					member.getPacketSender().sendString(s, "");
				member.getPacketSender().sendString(26235, owner.getUsername()+"'s Party");
				member.getPacketSender().sendString(26240, floor == null ? "-" : ""+(floor.ordinal()+1));
				member.getPacketSender().sendString(26241, complexity == -1 ? "-" : ""+complexity+"");
				for(int i = 0; i < getPlayers().size(); i++) {
					Player p = getPlayers().get(i);
					if(p != null) {
						if(p == getOwner())
							continue;
						member.getPacketSender().sendString(26235+i, p.getUsername());
					}
				}
			}
		}
	}

	public void sendMessage(String message) {
		for(Player member : getPlayers()) {
			if(member != null) {
				member.getPacketSender().sendMessage("<img=10> <col=660000>"+message);
			}
		}
	}

	public void sendFrame(int frame, String string) {
		for(Player member : getPlayers()) {
			if(member != null) {
				member.getPacketSender().sendString(frame, string);
			}
		}
	}

	public static void create(Player p) {
		if(p.getLocation() != Location.DUNGEONEERING) {
			p.getPacketSender().sendMessage("You must be in Daemonheim to create a party.");
			return;
		}
		if(p.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
			p.getPacketSender().sendMessage("You are already in a Dungeoneering party.");
			return;
		}
		if(p.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null)
			p.getMinigameAttributes().getDungeoneeringAttributes().setParty(new DungeoneeringParty(p));
		p.getMinigameAttributes().getDungeoneeringAttributes().getParty().setDungeoneeringFloor(DungeoneeringFloor.FIRST_FLOOR);
		p.getMinigameAttributes().getDungeoneeringAttributes().getParty().setComplexity(1);
		p.getPacketSender().sendMessage("<img=10> <col=660000>You've created a Dungeoneering party. Perhaps you should invite a few players?");
		p.getMinigameAttributes().getDungeoneeringAttributes().getParty().refreshInterface();
		p.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, Dungeoneering.PARTY_INTERFACE);
		p.getPacketSender().sendDungeoneeringTabIcon(true);
		p.getPacketSender().sendTab(GameSettings.QUESTS_TAB).sendInterfaceRemoval();
	}

	public DungeoneeringFloor getDungeoneeringFloor() {
		return floor;
	}

	public void setDungeoneeringFloor(DungeoneeringFloor floor) {
		this.floor = floor;
	}

	public Player getOwner() {
		return owner;
	}

	public CopyOnWriteArrayList<Player> getPlayers() {
		return player_members;
	}

	public boolean hasEnteredDungeon() {
		return hasEnteredDungeon;
	}

	public void enteredDungeon(boolean hasEnteredDungeon) {
		this.hasEnteredDungeon = hasEnteredDungeon;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public CopyOnWriteArrayList<NPC> getNpcs() {
		return npc_members;
	}

	public CopyOnWriteArrayList<GroundItem> getGroundItems() {
		return ground_items;
	}

	public Position getGatestonePosition() {
		return gatestonePosition;
	}

	public void setGatestonePosition(Position gatestonePosition) {
		this.gatestonePosition = gatestonePosition;
	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public void setKilledBoss(boolean killedBoss) {
		this.killedBoss = killedBoss;
	}
}
