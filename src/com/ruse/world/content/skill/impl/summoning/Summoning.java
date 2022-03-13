package com.ruse.world.content.skill.impl.summoning;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.FamiliarSpawnTask;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.Locations.Location;
import com.ruse.model.container.impl.BeastOfBurden;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.movement.MovementQueue;
import com.ruse.world.World;
import com.ruse.world.content.skill.impl.summoning.BossPets.BossPet;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * The summoning skill is based upon creating pouches that contain
 * certain 'familiars', which you can then summon and use their abilities
 * as a form of 'assistance'.
 * 
 * @author Gabriel Hannason
 */

public class Summoning {

	Player player;

	public Summoning(Player p) {
		this.player = p;
	}

	public void summon(final FamiliarData familiar, boolean renew, boolean login) {
		if(familiar == null)
			return;
		if(!player.getLocation().isSummoningAllowed()) {
			if(!renew && !login) {
				player.getPacketSender().sendMessage("You cannot summon familiars here.");
			} else {
				player.getPacketSender().sendMessage("Your familiar will spawn one you've left this area.");
			}
			return;
		}
		if(!login && !player.getLastSummon().elapsed(1000))
			return;
		if(getFamiliar() != null && !renew && !login) {
			player.getPacketSender().sendMessage("You already have a familiar.");
			return;
		}
		if (login || player.getSkillManager().getMaxLevel(Skill.SUMMONING) >= familiar.levelRequired) {

			if(!login) {
				if (player.getSkillManager().getCurrentLevel(Skill.SUMMONING) >= familiar.summoningPointsRequired) {
					player.getSkillManager().setCurrentLevel(Skill.SUMMONING, player.getSkillManager().getCurrentLevel(Skill.SUMMONING) - familiar.summoningPointsRequired);
					player.getInventory().delete(familiar.getPouchId(), 1);
					if(renew && getFamiliar() != null) {
						player.getPacketSender().sendMessage("You have renewed your familiar.");
						getFamiliar().setDeathTimer(SummoningData.getFollowerTimer(familiar.npcId));
						getFamiliar().getSummonNpc().performGraphic(new Graphic(1315));
						getFamiliar().getSummonNpc().setConstitution(getFamiliar().getSummonNpc().getDefaultConstitution());
						return;
					}
				} else {
					player.getPacketSender().sendMessage("You do not have enough Summoning points to summon this familiar.");
					player.getPacketSender().sendMessage("You can recharge your Summoning points at an obelisk.");
					return;
				}
			}

			int deathTime = login && getFamiliar() != null && getFamiliar().getDeathTimer() > 0 ? getFamiliar().getDeathTimer() : SummoningData.getFollowerTimer(familiar.npcId);

			unsummon(true, false);

			NPC foll = new NPC(familiar.npcId, new Position(player.getPosition().getX(), player.getPosition().getY() + 1, player.getPosition().getZ()));
			foll.performGraphic(new Graphic(1315));
			foll.setPositionToFace(player.getPosition());
			foll.setSummoningNpc(true);
			foll.setEntityInteraction(player);
			foll.getMovementQueue().setFollowCharacter(player);
			World.register(foll);

			setFamiliar(new Familiar(player, foll, deathTime));

			int store = SummoningData.getStoreAmount(foll.getId());
			if(bob == null || bob.capacity() < store) {
				if(store > 0)
					this.bob = new BeastOfBurden(player, store);
			}
			processFamiliar();

			player.getPacketSender().sendString(54028, ""+familiar.name().replaceAll("_", " "));
			player.getPacketSender().sendString(54045, " "+player.getSkillManager().getCurrentLevel(Skill.SUMMONING)+"/"+player.getSkillManager().getMaxLevel(Skill.SUMMONING));
			player.getPacketSender().sendNpcHeadOnInterface(player.getSummoning().getFamiliar().getSummonNpc().getId(), 54021); // 60 = invisable head to remove it
			player.getPacketSender().sendString(0, "[SUMMOtrue");

			player.getLastSummon().reset();
		} else {
			player.getPacketSender().sendMessage("You need a Summoning level of at least " + familiar.levelRequired + " to summon this familiar.");
		}
	}

	public void summonPet(BossPet bossPet, boolean login) {
		if(!login && !player.getInventory().contains(bossPet.itemId))
			return;
		if(!player.getLocation().isSummoningAllowed()) {
			player.getPacketSender().sendMessage("You cannot summon familiars here.");
			return;
		}
		if(!login && !player.getLastSummon().elapsed(1000))
			return;
		if(getFamiliar() != null && !login) {
			player.getPacketSender().sendMessage("You already have a familiar.");
			return;
		}
		if(!login) {
			unsummon(true, false);
			player.getInventory().delete(bossPet.itemId, 1);
		}
		NPC foll = new NPC(bossPet.spawnNpcId, new Position(player.getPosition().getX(), player.getPosition().getY() + 1, player.getPosition().getZ()));
		for (int i = 0; i < BossPet.values().length; i++) {
			if (BossPet.values()[i].spawnNpcId == bossPet.spawnNpcId) {
				//player.getPacketSender().sendMessage("HEY IT WORKED U FUCKIN RETARD. INDEX: "+i);
				if (!player.getBossPet(i)) {
					player.getPacketSender().sendMessage("You've unlocked "+ItemDefinition.forId(BossPet.values()[i].itemId).getName()+", it can be re-purchased from the Zoo Keeper if lost.");
					player.setBossPet(i, true);
				}
				break;
			}
		}
		foll.performGraphic(new Graphic(1315));
		foll.setPositionToFace(player.getPosition());
		foll.setSummoningNpc(true);
		foll.setEntityInteraction(player);
		foll.getMovementQueue().setFollowCharacter(player);
		World.register(foll);
		setFamiliar(new Familiar(player, foll));
		processFamiliar();
		player.getPacketSender().sendString(54028, ""+bossPet.name().replaceAll("_", " "));
		player.getPacketSender().sendString(54045, " "+player.getSkillManager().getCurrentLevel(Skill.SUMMONING)+"/"+player.getSkillManager().getMaxLevel(Skill.SUMMONING));
		player.getPacketSender().sendString(0, "[SUMMOtrue");
		player.getPacketSender().sendString(54043, "");
		player.getPacketSender().sendNpcHeadOnInterface(bossPet.npcId, 54021); // 60 = invisable head to remove it
	}

	public void unsummon(boolean full, boolean dropItems) {
		if(dropItems) {
			if(bob != null) {
				if(bob.getValidItems().size() > 0) {
					for(Item t : bob.getValidItems()) {
						GroundItemManager.spawnGroundItem(player, new GroundItem(t, getFamiliar().getSummonNpc().getPosition().copy(), player.getUsername(), player.getHostAddress(), false, 120, true, 80));
					}
					player.getPacketSender().sendMessage("Your familiar has dropped its carried items on the floor.");
				}
				bob = null;
			}
		}
		if(getFamiliar() != null && getFamiliar().getSummonNpc() != null) {
			final int spawnId = getFamiliar().getSummonNpc().getId();
			World.deregister(getFamiliar().getSummonNpc());
			if(full) {
				if(BossPet.forSpawnId(spawnId) != null) {
					player.getInventory().add(BossPet.forSpawnId(spawnId).itemId, 1);
				}
			}
		}
		if(full) {
			setFamiliar(null);
			clearInterface();
			player.getPacketSender().sendString(0, "[SUMMOfalse");
		}
	}

	public void processFamiliar() {

		final NPC n = familiar.getSummonNpc();
		TaskManager.submit(new Task(1, n, true) {
			int clockTimer = 2;
			@Override
			protected void execute() {

				if(familiar == null || n == null || n.getConstitution() <= 0 || !n.isRegistered() || player.getConstitution() <= 0 || !player.isRegistered()) {
					unsummon(true, true);
					stop();
					return;
				}

				boolean underAttack = player.getCombatBuilder().isBeingAttacked() && player.getCombatBuilder().getLastAttacker() != null && player.getCombatBuilder().getLastAttacker().getCombatBuilder().getVictim() != null && player.getCombatBuilder().getLastAttacker().getCombatBuilder().getVictim() == player;
				boolean attacking = player.getCombatBuilder().isAttacking(); 
				if(!familiar.isPet() && n.getDefinition().isAttackable() && (underAttack || attacking)) {
					if(n.getLocation() != Location.WILDERNESS) {
						n.setSummoningCombat(true);
						n.getCombatBuilder().attack(attacking ? player.getCombatBuilder().getVictim() : player.getCombatBuilder().getLastAttacker());
						n.setEntityInteraction(n.getCombatBuilder().getVictim());
					}
				} else {
					if(n.getCombatBuilder().isAttacking()) {
						n.getCombatBuilder().reset(true);
					}
					n.setSummoningCombat(false);
					n.setEntityInteraction(player);
					n.getMovementQueue().setFollowCharacter(player);
				}
				if(!familiar.isPet()) {
					if(clockTimer >= 2) {
						if(familiar.getDeathTimer() > 0) {
							familiar.setDeathTimer(getFamiliar().getDeathTimer() - 1);
							if(familiar.getDeathTimer() == 60)
								player.getPacketSender().sendMessage("@red@Warning: Your familiar will fade away in one minute.");
							player.getPacketSender().sendString(54043, ""+Summoning.getTimer(familiar.getDeathTimer() > 0 ? familiar.getDeathTimer() : 0));
							//	player.getPacketSender().sendString(54024, ""+SummoningData.calculateScrolls(player));
						} else {
							unsummon(true, true);
							stop();
							player.getPacketSender().sendMessage("Your summoning familiar has died.");
						}
						clockTimer = 0;
					}
					clockTimer++;
				}
			}
		});
	}

	public void store() {
		if(player.busy()) {
			player.getPacketSender().sendMessage("Please finish what you're doing first.");
			return;
		}
		if(getFamiliar() != null && getFamiliar().getSummonNpc() != null && bob != null)
			bob.open();
		else
			player.getPacketSender().sendMessage("You do not have a familiar which can hold items.");
	}

	public void toInventory() {
		if(player == null)
			return;
		if(player.getInventory().getFreeSlots() <= 0) {
			player.getPacketSender().sendMessage("You do not have any free space in your inventory.");
			return;
		}
		if((!player.busy() || player.getInterfaceId() == BeastOfBurden.INTERFACE_ID) && player.getLocation().isSummoningAllowed()) {
			if(getFamiliar() == null || !SummoningData.beastOfBurden(getFamiliar().getSummonNpc().getId()) || bob == null) {
				player.getPacketSender().sendMessage("You do not have a Beast of Burden.");
				return;
			}
			if(bob.getValidItems().size() == 0) {
				player.getPacketSender().sendMessage("Your familiar is not currently holding any items for you.");
				return;
			}
			player.performAnimation(new Animation(827));
			player.setInterfaceId(-BeastOfBurden.INTERFACE_ID);
			bob.moveItems(player.getInventory(), false, true);
			bob.refreshItems();
			player.getPacketSender().sendInterfaceRemoval();
		} else
			player.getPacketSender().sendMessage("You cannot do this right now.");
	}

	public static String getTimer(int seconds) {
		int minuteCounter = 0;
		int secondCounter = 0;
		for(int j = seconds; j > 0; j--) {
			if(secondCounter >= 59) {
				minuteCounter++;
				secondCounter = 0;
			} else
				secondCounter++;
		}
		if(minuteCounter == 0 && secondCounter == 0)
			return "";
		String secondString = ""+secondCounter;
		if(secondCounter < 10)
			secondString = "0"+secondCounter+"";
		return " "+minuteCounter+":"+secondString;
	}

	public void moveFollower(boolean forced) {
		if(getFamiliar() != null && getFamiliar().getSummonNpc() != null) {
			final Position movePos = new Position(player.getPosition().getX(), player.getPosition().getY() + 1, player.getPosition().getZ());
			if(forced || canSpawn(getFamiliar().getSummonNpc(), movePos)) {
				getFamiliar().getSummonNpc().moveTo(movePos);
				getFamiliar().getSummonNpc().performGraphic(new Graphic(1315));
				getFamiliar().getSummonNpc().setLocation(Location.getLocation(getFamiliar().getSummonNpc()));
				player.getLastSummon().reset();
				if(!forced) {
					player.getPacketSender().sendMessage("You've called your familiar.");
				}
			} else {
				player.getPacketSender().sendMessage("Your familiar cannot fit in this area.");
			}
		}
	}

	public static boolean canSpawn(NPC n, Position pos) {
		return MovementQueue.canWalk(n.getPosition(), pos, n.getSize());
	}

	public void login() {
		clearInterface();
		if(spawnTask != null) {
			TaskManager.submit(spawnTask);
		}
		spawnTask = null;
	}

	public void clearInterface() {
		player.getPacketSender().sendString(54045, "");
		player.getPacketSender().sendString(54043, "");
		player.getPacketSender().sendString(54028, "");
		player.getPacketSender().sendString(54024, "0");
		player.getPacketSender().sendNpcHeadOnInterface(60, 54021); // 60 = invisable head to remove it
		player.getPacketSender().sendString(18045, player.getSkillManager().getMaxLevel(Skill.SUMMONING) < 10 ? "   "+player.getSkillManager().getCurrentLevel(Skill.SUMMONING)+"/"+player.getSkillManager().getMaxLevel(Skill.SUMMONING) :  " "+player.getSkillManager().getCurrentLevel(Skill.SUMMONING)+"/"+player.getSkillManager().getMaxLevel(Skill.SUMMONING));
	}

	private FamiliarSpawnTask spawnTask;
	private Familiar familiar;
	private BeastOfBurden bob;

	public FamiliarSpawnTask getSpawnTask() {
		return spawnTask;
	}

	public FamiliarSpawnTask setFamiliarSpawnTask(FamiliarSpawnTask spawnTask) {
		this.spawnTask = spawnTask;
		return this.spawnTask;
	}

	public Familiar getFamiliar() {
		return this.familiar;
	}

	public void setFamiliar(Familiar familiar) {
		this.familiar = familiar;
	}

	public BeastOfBurden getBeastOfBurden() {
		return bob;
	}

	private int[] charmImpConfigs = new int[] {0, 0, 0, 0};

	public void setCharmImpConfig(int index, int config) {
		this.charmImpConfigs[index] = config;
	}

	public void setCharmimpConfig(int[] charmImpConfig) {
		this.charmImpConfigs = charmImpConfig;
	}

	public int getCharmImpConfig(int index) {
		return charmImpConfigs[index];
	}

	public int[] getCharmImpConfigs() {
		return charmImpConfigs;
	}
}
