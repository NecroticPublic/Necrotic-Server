package com.ruse.world.content.minigames.impl;

import java.util.ArrayList;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Graphic;
import com.ruse.model.Locations.Location;
import com.ruse.model.Position;
import com.ruse.model.RegionInstance;
import com.ruse.model.RegionInstance.RegionInstanceType;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class TheSix {

	public static void enter(Player player, boolean clan) {
		player.getPacketSender().sendInterfaceRemoval();
		
		if(clan) {
			if(player.getCurrentClanChat() == null) {
				player.getPacketSender().sendMessage("You must be in a clan to fight the six.");
				return;
			}
			if(player.getCurrentClanChat().doingClanBarrows()) {
				player.getPacketSender().sendMessage("Your clan is already playing a game of The Six.");
				return;
			}
		}

		int z = (player.getIndex()+1)*4;
		final Position orig = player.getPosition().copy();
		final Position pos = new Position(2384, 4721, z);

		if (player.getCurrentClanChat() == null || player.getCurrentClanChat().getName() == null) {
			player.getPacketSender().sendMessage("You need to be in a clan chat, make or join one.");
			return;
		}
		
		ArrayList<Player> close_clan_members = player.getCurrentClanChat().getClosePlayers(orig);
		
		if(clan && close_clan_members.size() <= 1) {
			player.getPacketSender().sendMessage("You do not have any clan members near you.");
			return;
		} 
			if(!clan) {
				Barrows.resetBarrows(player);
				player.setDoingClanBarrows(clan);
				player.setBarrowsKilled(0);
				player.getPacketSender().sendInterfaceRemoval();
				player.moveTo(pos);
			}
	 
		
		Barrows.resetBarrows(player);
		player.setDoingClanBarrows(clan);
		player.setBarrowsKilled(0);
		player.getPacketSender().sendInterfaceRemoval();
		player.moveTo(pos);
		
		if(clan) {
			player.getCurrentClanChat().setHeight(z);
			player.getCurrentClanChat().setDoingClanBarrows(true);
			player.getCurrentClanChat().setRegionInstance(new RegionInstance(player, RegionInstanceType.THE_SIX));
			player.getCurrentClanChat().getRegionInstance().getPlayersList().add(player);
			for(Player p : close_clan_members) {
				if(p == null || p == player)
					continue;
				p.getPacketSender().sendInterfaceRemoval();
				p.setDialogueActionId(81);
				DialogueManager.start(p, 134);
			}
		} else {
			player.setRegionInstance(new RegionInstance(player, RegionInstanceType.THE_SIX));
		}

		spawn(player, clan);
	}
	
	public static void joinClan(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		if(player.getCurrentClanChat() != null && player.getCurrentClanChat().doingClanBarrows()) {
			player.setDoingClanBarrows(true);
			player.setBarrowsKilled(0);
			Barrows.resetBarrows(player);
			player.moveTo(new Position(2384, 4721, player.getCurrentClanChat().getHeight()));
			player.getCurrentClanChat().getRegionInstance().getPlayersList().add(player);
		}
	}

	public static void leave(Player player, boolean move) {

		final int killcount = player.getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount();
		if(killcount > 0) {
			int points = player.doingClanBarrows() ? 1 * killcount : 2 * killcount;
			player.getPointsHandler().setBarrowsPoints(points, true);
			player.getPacketSender().sendMessage("You've received "+points+" Barrows points.");
		}
		
		Barrows.resetBarrows(player);

		if(move) {
			player.moveTo(new Position(3562, 3311));
		}

		if(player.doingClanBarrows()) {
			if(player.getCurrentClanChat() != null && player.getCurrentClanChat().getRegionInstance() != null) {
				player.getCurrentClanChat().getRegionInstance().getPlayersList().remove(player);
				if(player.getCurrentClanChat().getRegionInstance().getPlayersList().size() <= 0) {
					player.getCurrentClanChat().getRegionInstance().destruct();
					player.getCurrentClanChat().setRegionInstance(null);
					player.getCurrentClanChat().setDoingClanBarrows(false);
				}
			}
		}


		player.setDoingClanBarrows(false);
	}

	public static boolean allKilled(Player player) {
		if(player.getBarrowsKilled() >= 6) {
			player.setBarrowsKilled(0);
			for(int i = 0; i < player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData().length; i++)
				player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[i][1] = 0;
			Barrows.updateInterface(player);
			return true;
		}
		return false;
	}

	public static void spawn(Player player, boolean clan) {
		final int z = player.getPosition().getZ();
		//CustomObjects.spawnGlobalObjectWithinDistance(new GameObject(2273, new Position(2384, 4715, z), 10, 1));
		//CustomObjects.spawnGlobalObjectWithinDistance(new GameObject(1864, new Position(2383, 4715, z), 10, 1));
		//CustomObjects.spawnGlobalObjectWithinDistance(new GameObject(1864, new Position(2382, 4715, z), 10, 1));
		TaskManager.submit(new Task(3, player, false) {
			int tick = 0;
			@Override
			protected void execute() {
				if(player.getLocation() != Location.THE_SIX || clan && (player.getCurrentClanChat() == null || player.getCurrentClanChat().getRegionInstance() == null) || !clan && player.getRegionInstance() == null) {
					leave(player, false);
					stop();
					return;
				}
				Position pos = null;
				NPC npc = null;
				switch(tick) {
				case 0:
					pos = new Position(2385, 4717, z);
					npc = new NPC(2030, pos);
					break;
				case 1:
					pos = new Position(2384, 4723, z);
					npc = new NPC(2026, pos);
					break;
				case 2:
					pos = new Position(2388, 4720, z);
					npc = new NPC(2025, pos);
					break;
				case 3:
					pos = new Position(2379, 4720, z);
					npc = new NPC(2028, pos);
					break;
				case 4:
					pos = new Position(2382, 4723, z);
					npc = new NPC(2029, pos);
					break;
				case 5:
					pos = new Position(2387, 4722, z);
					npc = new NPC(2027, pos);
					break;
				case 6:
					stop();
					break;
				}
				if(npc != null && pos != null) {
					World.register(npc);
					npc.performGraphic(new Graphic(354));
					Player target = player;
					if(clan) {
						ArrayList<Player> LIST = new ArrayList<Player>();
						for(Player p : player.getCurrentClanChat().getMembers()) {
							if(p == null || !p.doingClanBarrows()) {
								continue;
							}
							LIST.add(p);
						}
						target = LIST.get(Misc.getRandom(LIST.size() - 1));
					}
					npc.getCombatBuilder().attack(target);
					if(clan) {
						player.getCurrentClanChat().getRegionInstance().getNpcsList().add(npc);
					} else {
						player.getRegionInstance().getNpcsList().add(npc);
					}
				}
				tick++;
			}
		});
	}
}
