package com.ruse.model;

import com.ruse.GameSettings;
import com.ruse.model.RegionInstance.RegionInstanceType;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPunishment.Jail;
import com.ruse.world.content.Zulrah;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.pvp.BountyHunter;
import com.ruse.world.content.combat.strategy.impl.Scorpia;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.minigames.impl.Barrows;
import com.ruse.world.content.minigames.impl.FightCave;
import com.ruse.world.content.minigames.impl.FightPit;
import com.ruse.world.content.minigames.impl.Graveyard;
import com.ruse.world.content.minigames.impl.Nomad;
import com.ruse.world.content.minigames.impl.PestControl;
import com.ruse.world.content.minigames.impl.RecipeForDisaster;
import com.ruse.world.content.minigames.impl.TheSix;
import com.ruse.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;
public class Locations {

	public static void login(Player player) {
		player.setLocation(Location.getLocation(player));
		player.getLocation().login(player);
		player.getLocation().enter(player);
	}

	public static void logout(Player player) {
		player.getLocation().logout(player);
		if(player.getRegionInstance() != null)
			player.getRegionInstance().destruct();
		if(player.getLocation() != Location.GODWARS_DUNGEON) {
			player.getLocation().leave(player);
		}
	}

	public static int PLAYERS_IN_WILD;
	public static int PLAYERS_IN_DUEL_ARENA;

	public enum Location { 
		//Location(int[] x, int[] y, boolean multi, boolean summonAllowed, boolean followingAllowed, boolean cannonAllowed, boolean firemakingAllowed, boolean aidingAllowed) {
		MAGEBANK_SAFE(new int[]{2525, 2550}, new int[]{4707, 4727}, true, true, true, false, false, false) {
		},
		ZULRAH(new int[]{3395, 3453}, new int[]{2751, 2785}, false, false, true, false, false, false) {
		
			@Override
			public void leave(Player player) {
				if(player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstanceType.ZULRAH)) {
					player.getRegionInstance().destruct();
				}
				player.getPacketSender().sendCameraNeutrality();
				player.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
			}

			@Override
			public void enter(Player player) {
				Zulrah.enter(player);
				player.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
			}

			@Override
			public void onDeath(Player player) {
				if(player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstanceType.ZULRAH)) {
					player.getRegionInstance().destruct();
				}
				player.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				killer.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
				return false;
			}
			
			@Override
			public void logout(Player player) {
				if(player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstanceType.ZULRAH)) {
					player.getRegionInstance().destruct();
				}
				player.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
				player.moveTo(GameSettings.DEFAULT_POSITION);
			}
			
			@Override
			public void login(Player player) {
				if(player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstanceType.ZULRAH)) {
					player.getRegionInstance().destruct();
				}
				player.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
				player.moveTo(GameSettings.DEFAULT_POSITION);
			}
		
			
		},
		DOOM(new int[]{2302, 2369}, new int[]{5182, 5250}, true, true, true, false, false, false) {
		},
		XMASEVENT2016(new int[]{2747, 2821}, new int[]{3707, 3877}, false, true, true, false, true, true) {
			
			@Override
			public void process(Player player) {
				if (player.getWalkableInterfaceId() != 11877) {
					player.getPacketSender().sendWalkableInterface(11877);
				}
			}
			
		},
		DUNGEONEERING(new int[]{3433, 3459, 2421, 2499}, new int[]{3694, 3729, 4915, 4990}, true, false, true, false, true, false) {
			@Override
			public void login(Player player) {
				player.getPacketSender().sendDungeoneeringTabIcon(true).sendTabInterface(GameSettings.QUESTS_TAB, 27224).sendTab(GameSettings.QUESTS_TAB);
			}

			@Override
			public void leave(Player player) {
				Dungeoneering.leave(player, true, true);
			}

			@Override
			public void enter(Player player) {
				player.getPacketSender().sendDungeoneeringTabIcon(true).sendTabInterface(GameSettings.QUESTS_TAB, 27224).sendTab(GameSettings.QUESTS_TAB);
				if (player.isInDung() == false) {
					DialogueManager.start(player, 104);
				}
			}

			@Override
			public void onDeath(Player player) {
				if(Dungeoneering.doingDungeoneering(player)) {
					Dungeoneering.handlePlayerDeath(player);
				}
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				if(Dungeoneering.doingDungeoneering(killer)) {
					Dungeoneering.handleNpcDeath(killer, npc);
					return true;
				}
				return false;
			}

			@Override
			public void process(Player player) {
				if(Dungeoneering.doingDungeoneering(player)) {
					if(player.getWalkableInterfaceId() != 37500) {
						player.getPacketSender().sendWalkableInterface(37500);
					}
				} else if(player.getWalkableInterfaceId() == 37500) {
					player.getPacketSender().sendWalkableInterface(-1);
				}
			}
		},
		//Location(int[] x, int[] y, boolean multi, boolean summonAllowed, boolean followingAllowed, boolean cannonAllowed, boolean firemakingAllowed, boolean aidingAllowed) {
		ZULRAH_WAITING(new int[]{3401, 3414}, new int[]{2789, 2801}, false, true, true, false, true, true) {
			@Override
			public void enter(Player player) {
				if(player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager().getMaxLevel(Skill.CONSTITUTION)) {
					player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
					player.getPacketSender().sendMessage("The astounding power of the old pillars heals you.");
				}
				if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager().getMaxLevel(Skill.PRAYER)) {
					player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER));
					player.getPacketSender().sendMessage("The mystique aura of the pillars restores your prayer.");
				}
			}
			@Override
			public void leave(Player player) {
				if(player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager().getMaxLevel(Skill.CONSTITUTION)) {
					player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
					player.getPacketSender().sendMessage("The astounding power of the old pillars heals you.");
				}
				if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager().getMaxLevel(Skill.PRAYER)) {
					player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER));
					player.getPacketSender().sendMessage("The mystique aura of the pillars restores your prayer.");
				}
			}
		},
		JAIL(new int[]{2505, 2535}, new int[] {9310, 9330}, false, false, false, false, false, false) {
			@Override
			public boolean canTeleport(Player player) {
				if (player.getRights().isStaff()) {
					player.getPacketSender().sendMessage("Staff can leave at any time.");
					return true;
				} else {
					player.getPacketSender().sendMessage("That'd be convenient, wouldn't it?");
					return false;
				}
			}
			/*@Override
			public void process(Player player) {
				//player.getPacketSender().sendInteractionOption(null, 4, false);
				if(player.getTrading().inTrade()){
					player.getTrading().declineTrade(true);
					player.getPacketSender().sendMessage("You can't trade in jail!");
				}
			}*/
		},
		MEMBER_ZONE(new int[]{3415, 3435}, new int[]{2900, 2926}, false, true, true, false, false, true) {
		},
		HOME_BANK(new int[]{3661, 3674}, new int[]{2975, 2985}, false, true, true, false, true, true) {
			@Override
			public void enter(Player player) {
				if(player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager().getMaxLevel(Skill.CONSTITUTION)) {
					player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
					player.getPacketSender().sendMessage("As you enter the home bank, your health regenerates to full.");
				}
				if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager().getMaxLevel(Skill.PRAYER)) {
					player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER));
					player.getPacketSender().sendMessage("As you enter the home bank, the gods restore your prayer.");
				}
			}
			@Override
			public void leave(Player player) {
				if(player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager().getMaxLevel(Skill.CONSTITUTION)) {
					player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
					player.getPacketSender().sendMessage("As you leave the home bank, your health regenerates to full.");
				}
				if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager().getMaxLevel(Skill.PRAYER)) {
					player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER));
					player.getPacketSender().sendMessage("As you leave the home bank, the gods restore your prayer.");
				}
			}
		},
		NEW_MEMBER_ZONE(new int[]{2792, 2877}, new int[]{3319, 3396}, false, true, true, false, true, true) {
			@Override
			public void process(Player player) {
				if (!player.getRights().isMember() && !player.newPlayer()) {
					player.getPacketSender().sendMessage("This area is for Members only.");
					player.moveTo(GameSettings.HOME_CORDS);
				}
			}
			
			@Override
			public void enter(Player player) {
				if(player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager().getMaxLevel(Skill.CONSTITUTION)) {
					player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
					player.getPacketSender().sendMessage("As you enter the Member Zone, your health regenerates to full.");
				}
				if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager().getMaxLevel(Skill.PRAYER)) {
					player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER));
					player.getPacketSender().sendMessage("As you enter the Member Zone, the gods restore your prayer.");
				}
			}
		},
		TRIO_ZONE(new int[]{3008, 3039}, new int[] {5216, 5247}, false, false, false, false, false, false) {
		},
		VARROCK(new int[]{3167, 3272}, new int[]{3263, 3504}, false, true, true, true, true, true) {
		},
		/*BANK(new int[]{3090, 3099, 3089, 3090, 3248, 3258, 3179, 3191, 2944, 2948, 2942, 2948, 2944, 2950, 3008, 3019, 3017, 3022, 3203, 3213, 3212, 3215, 3215, 3220, 3220, 3227, 3227, 3230, 3226, 3228, 3227, 3229}, new int[]{3487, 3500, 3492, 3498, 3413, 3428, 3432, 3448, 3365, 3374, 3367, 3374, 3365, 3370, 3352, 3359, 3352, 3357, 3200, 3237, 3200, 3235, 3202, 3235, 3202, 3229, 3208, 3226, 3230, 3211, 3208, 3226}, false, true, true, false, false, true) {
		},*/
		EDGEVILLE(new int[]{3073, 3134}, new int[]{3457, 3518}, false, false, true, false, false, true) {
		},
		LUMBRIDGE(new int[]{3175, 3238}, new int[]{3179, 3302}, false, true, true, true, true, true) {
		},
		KING_BLACK_DRAGON(new int[]{2251, 2292}, new int[]{4673, 4717}, true, true, true, true, true, true) {
		},
		SCORPIA(new int[]{2845, 2864}, new int[]{9621, 9649}, true, true, true, true, true, true) {
			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				if(npc.getId() == 109) {
					Scorpia.killedBaby();
					return true;
				}
				return false;
			}
		},
		KRAKEN(new int[]{3672, 3690}, new int[]{9875, 9899}, true, true, true, true, true, true) {
			@Override
			public void leave(Player player) {
				if(player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstanceType.KRAKEN)) {
					player.getRegionInstance().destruct();
				}
				player.getPacketSender().sendCameraNeutrality();
			}
		},
		SLASH_BASH(new int[]{2504, 2561}, new int[]{9401, 9473}, true, true, true, true, true, true) {
		},
		BANDOS_AVATAR(new int[]{2340, 2396}, new int[]{4929, 4985}, true, true, true, true, true, true) {
		},
		KALPHITE_QUEEN(new int[]{3464, 3500}, new int[]{9478, 9523}, true, true, true, true, true, true) {
		},
		PHOENIX(new int[]{2824, 2862}, new int[]{9545, 9594}, true, true, true, true, true, true) {
		},
		//BANDIT_CAMP(new int[]{3020, 3150, 3055, 3195}, new int[]{3684, 3711, 2958, 3003}, true, true, true, true, true, true) {
		//},
		ROCK_CRABS(new int[]{2689, 2727}, new int[]{3691, 3730}, true, true, true, true, true, true) {
		},
		ARMOURED_ZOMBIES(new int[]{3077, 3132}, new int[]{9657, 9680}, true, true, true, true, true, true) {
		},
		CORPOREAL_BEAST(new int[]{2879, 2962}, new int[]{4368, 4413}, true, true, true, false, true, true) {	
			@Override
			public void process(Player player) {
				int x1 = 2889;
				int x2 = 2908;
				int y1 = 4381;
				int y2 = 4403;
				int currentx = player.getPosition().getX();
				int currenty = player.getPosition().getY();
				
				boolean safe = currentx >= x1 && currentx <= x2 && currenty >= y1 && currenty <= y2;
				if (safe) {
					//player.getPacketSender().sendMessage("You are safe");
					player.getPacketSender().sendWalkableInterface(-1);//.sendMessage("sendwalkint-1"); 
					/*player.setWalkableInterfaceId(-1); 
					player.getPacketSender().sendMessage("setwalkint-1");
					player.getPacketSender().sendInterfaceRemoval().sendMessage("sendintremoval");
					player.getPacketSender().sendInterfaceReset().sendMessage("sendintreset");
					*/
				} else {
					//player.getPacketSender().sendMessage("Get out of the gas!");
					player.dealDamage(new Hit(Misc.getRandom(15)*10, Hitmask.DARK_PURPLE, CombatIcon.CANNON));
					if (player.getWalkableInterfaceId() != 16152){
						player.getPacketSender().sendWalkableInterface(16152); 
					}
					//player.setWalkableInterfaceId(16152);
				}
			}
			
		},
		DAGANNOTH_DUNGEON(new int[]{2886, 2938}, new int[]{4431, 4477}, true, true, true, false, true, true) {
		},
		WILDERNESS(new int[]{2940, 3392, 2986, 3012, 3653, 3720, 3650, 3653, 3150, 3199, 2994, 3041}, new int[]{3523, 3968, 10338, 10366, 3441, 3538, 3457, 3472, 3796, 3869, 3733, 3790}, false, true, true, true, true, true) {
			@Override
			public void process(Player player) {
				int x = player.getPosition().getX();
				int y = player.getPosition().getY();
				boolean ghostTown = x >= 3650 && y <= 3538;
				//boolean magebank = (x >= 3090 && x <= 3092 && y >= 3955 && y <= 3958);
				//System.out.println("magebank "+magebank+", "+player.getPosition());
				if (player.isFlying()) {
					player.getPacketSender().sendMessage("You cannot fly in the Wilderness.");
					player.setFlying(false);
					player.newStance();
				}
				if (player.isGhostWalking()) {
					player.getPacketSender().sendMessage("You cannot ghost walk in the Wilderness.");
					player.setGhostWalking(false);
					player.newStance();
				}
				/*boolean banditCampA = x >= 3020 && x <= 3150 && y >= 3684 && y <= 3711;
				boolean banditCampB = x >= 3055 && x <= 3195 && y >= 2958 && y <= 3003;
				if(banditCampA || banditCampB) {
				}*/
				if(ghostTown) {
					player.setWildernessLevel(60);

				} else {
					player.setWildernessLevel(((((y > 6400 ? y - 6400 : y) - 3520) / 8)+1));
				}
				player.getPacketSender().sendString(42023, ""+player.getWildernessLevel());
				//player.getPacketSender().sendString(25355, "Levels: "+CombatFactory.getLevelDifference(player, false) +" - "+CombatFactory.getLevelDifference(player, true));
				BountyHunter.process(player);
			}

			@Override
			public void leave(Player player) {
				if(player.getLocation() != this) {
					player.getPacketSender().sendString(19000, "Combat level: " + player.getSkillManager().getCombatLevel());
					player.getUpdateFlag().flag(Flag.APPEARANCE);
				}
				PLAYERS_IN_WILD--;
			}

			@Override
			public void enter(Player player) {
				player.getPacketSender().sendInteractionOption("Attack", 2, true);
				player.getPacketSender().sendWalkableInterface(42020);
				player.getPacketSender().sendString(19000, "Combat level: " + player.getSkillManager().getCombatLevel());
				player.getUpdateFlag().flag(Flag.APPEARANCE);
				PLAYERS_IN_WILD++;
			}

			@Override
			public boolean canTeleport(Player player) {
				if(Jail.isJailed(player)) {
					player.getPacketSender().sendMessage("That'd be convenient.");
					return false;
				}
				if(player.getWildernessLevel() > 20) {
					if(player.getRights() == PlayerRights.MODERATOR || player.getRights() == PlayerRights.ADMINISTRATOR || player.getRights() == PlayerRights.OWNER || player.getRights() == PlayerRights.DEVELOPER) {
						player.getPacketSender().sendMessage("@red@You've teleported out of deep Wilderness, logs have been written.");
						PlayerLogs.log(player.getUsername(), " teleported out of level "+player.getWildernessLevel()+" wildy. Were in combat? "+player.getCombatBuilder().isBeingAttacked());
						return true;
					}
					player.getPacketSender().sendMessage("Teleport spells are blocked in this level of Wilderness.");
					player.getPacketSender().sendMessage("You must be below level 20 of Wilderness to use teleportation spells.");
					return false;
				}
				return true;
			}

			@Override
			public void login(Player player) {
				player.performGraphic(new Graphic(2000, 8));
			}

			@Override
			public boolean canAttack(Player player, Player target) {
				int combatDifference = CombatFactory.combatLevelDifference(player.getSkillManager().getCombatLevel(), target.getSkillManager().getCombatLevel());
				if (combatDifference > player.getWildernessLevel() +  5|| combatDifference > target.getWildernessLevel() + 5) {
					player.getPacketSender().sendMessage("Your combat level difference is too great to attack that player here.");
					player.getMovementQueue().reset();
					return false;
				}
				if(target.getLocation() != Location.WILDERNESS) {
					player.getPacketSender().sendMessage("That player cannot be attacked, because they are not in the Wilderness.");
					player.getMovementQueue().reset();
					return false;
				}
				if(Jail.isJailed(player)) {
					player.getPacketSender().sendMessage("You cannot do that right now.");
					return false;
				}
				if(Jail.isJailed(target)) {
					player.getPacketSender().sendMessage("That player cannot be attacked right now.");
					return false;
				}
				/*if(Misc.getMinutesPlayed(player) < 20) {
					player.getPacketSender().sendMessage("You must have played for at least 20 minutes in order to attack someone.");
					return false;
				}
				if(Misc.getMinutesPlayed(target) < 20) {
					player.getPacketSender().sendMessage("This player is a new player and can therefore not be attacked yet.");
					return false;
				}*/
				return true;
			}
		},
		BARROWS(new int[] {3520, 3598, 3543, 3584, 3543, 3560}, new int[] {9653, 9750, 3265, 3314, 9685, 9702}, false, true, true, true, true, true) {
			@Override
			public void process(Player player) {
				if(player.getWalkableInterfaceId() != 37200)
					player.getPacketSender().sendWalkableInterface(37200);
			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void logout(Player player) {

			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				Barrows.killBarrowsNpc(killer, npc, true);
				return true;
			}
		},
		THE_SIX(new int[] {2376, 2395}, new int[] {4711, 4731}, true, true, true, true, true, true) {
			@Override
			public void process(Player player) {
				if(player.getWalkableInterfaceId() != 37200)
					player.getPacketSender().sendWalkableInterface(37200);
			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void leave(Player player) {
				if(!player.doingClanBarrows()) {
					if(player.getRegionInstance() != null) {
						player.getRegionInstance().destruct();
					}
					TheSix.leave(player, false);
				} else if(player.getCurrentClanChat() != null && player.getCurrentClanChat().doingClanBarrows()) {
					TheSix.leave(player, false);
				}
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				boolean respawn = false;
				
				if(!killer.doingClanBarrows()) {
					Barrows.killBarrowsNpc(killer, npc, true);
					if(TheSix.allKilled(killer)) {
						respawn = true;
					}
				} else if(killer.getCurrentClanChat() != null && killer.getCurrentClanChat().doingClanBarrows()) {
					for(Player p : killer.getCurrentClanChat().getMembers()) {
						if(p == null || !p.doingClanBarrows()) {
							continue;
						}
						Barrows.killBarrowsNpc(p, npc, true);
						if(TheSix.allKilled(p)) {
							respawn = true;
						}
					}
				}
				 
				if(respawn) {
					TheSix.spawn(killer, killer.doingClanBarrows());
				}
				
				return true;
			}
		},
        INVADING_GAME(new int[]{2216, 2223}, new int[]{4936, 4943}, true, true, true, false, true, true){
            @Override
            public void process(Player player) {
				if(player.getWalkableInterfaceId() != 21005)
					player.getPacketSender().sendWalkableInterface(21005);
			}
        },
		PEST_CONTROL_GAME(new int[]{2624, 2690}, new int[]{2550, 2619}, true, true, true, false, true, true) {
			@Override
			public void process(Player player) {
				if(player.getWalkableInterfaceId() != 21100)
					player.getPacketSender().sendWalkableInterface(21100);
			}
			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked on this island. Wait for the game to finish!");
				return false;
			}

			@Override
			public void leave(Player player) {
				PestControl.leave(player, true);
			}

			@Override
			public void logout(Player player) {
				PestControl.leave(player, true);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC n) {
				return true;
			}

			@Override
			public void onDeath(Player player) {
				player.moveTo(new Position(2657, 2612, 0));
			}
		},
		PEST_CONTROL_BOAT(new int[]{2660, 2663}, new int[]{2638, 2643}, false, false, false, false, false, true) {
			@Override
			public void process(Player player) {
				if(player.getWalkableInterfaceId() != 21005)
					player.getPacketSender().sendWalkableInterface(21005);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("You must leave the boat before teleporting.");
				return false;
			}

			@Override
			public void leave(Player player) {
				if(player.getLocation() != PEST_CONTROL_GAME) {
					PestControl.leave(player, true);
				}
			}

			@Override
			public void logout(Player player) {
				PestControl.leave(player, true);
			}
		},
		SOULWARS(new int[]{-1, -1}, new int[]{-1, -1}, true, true, true, false, true, true) {
			@Override
			public void process(Player player) {

			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("If you wish to leave, you must use the portal in your team's lobby.");
				return false;
			}

			@Override
			public void logout(Player player) {

			}

			@Override
			public void onDeath(Player player) {

			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {

				return false;
			}

		},
		SOULWARS_WAIT(new int[]{-1, -1}, new int[]{-1, -1}, false, false, false, false, false, true) {
			@Override
			public void process(Player player) {}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("You must leave the waiting room before being able to teleport.");
				return false;
			}

			@Override
			public void logout(Player player) {}
		},
		FIGHT_CAVES(new int[]{2360, 2445}, new int[]{5045, 5125}, true, true, false, false, false, false) {
			@Override
			public void process(Player player) {}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here. If you'd like to leave, use the north-east exit.");
				return false;
			}

			@Override
			public void login(Player player) {}

			@Override
			public void leave(Player player) {
				player.getCombatBuilder().reset(true);
				if(player.getRegionInstance() != null) {
					player.getRegionInstance().destruct();
				}
				player.moveTo(new Position(2439, 5169));
			}

			@Override
			public void onDeath(Player player) {
				FightCave.leaveCave(player, true);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				FightCave.handleJadDeath(killer, npc);
				return true;
			}
		},
		GRAVEYARD(new int[]{3485, 3517}, new int[]{3559, 3580}, true, true, false, true, false, false) {
			@Override
			public void process(Player player) {
			}

			@Override
			public boolean canTeleport(Player player) {
				if(player.getMinigameAttributes().getGraveyardAttributes().hasEntered()) {
					player.getPacketSender().sendInterfaceRemoval().sendMessage("A spell teleports you out of the graveyard.");
					Graveyard.leave(player);
					return false;
				}
				return true;
			}

			@Override
			public void logout(Player player) {
				if(player.getMinigameAttributes().getGraveyardAttributes().hasEntered()) {
					Graveyard.leave(player);
				}
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				return killer.getMinigameAttributes().getGraveyardAttributes().hasEntered() && Graveyard.handleDeath(killer, npc);
			}

			@Override
			public void onDeath(Player player) {
				Graveyard.leave(player);
			}
		},
		FIGHT_PITS(new int[]{2370, 2425}, new int[]{5133, 5167}, true, true, true, false, false, true) {
			@Override
			public void process(Player player) {
				if(FightPit.inFightPits(player)) {
					FightPit.updateGame(player);
					if(player.getPlayerInteractingOption() != PlayerInteractingOption.ATTACK)
						player.getPacketSender().sendInteractionOption("Attack", 2, true);
				}
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here. If you'd like to leave, use the northern exit.");
				return false;
			}

			@Override
			public void logout(Player player) {
				FightPit.removePlayer(player, "leave game");
			}

			@Override
			public void leave(Player player) {
				onDeath(player);
			}

			@Override
			public void onDeath(Player player) {
				if(FightPit.getState(player) != null) {
					FightPit.removePlayer(player, "death");
				}
			}

			@Override
			public boolean canAttack(Player player, Player target) {
				String state1 = FightPit.getState(player);
				String state2 = FightPit.getState(target);
				return state1 != null && state1.equals("PLAYING") && state2 != null && state2.equals("PLAYING");
			}
		},
		FIGHT_PITS_WAIT_ROOM(new int[]{2393, 2404}, new int[]{5168, 5176}, false, false, false, false, false, true) {
			@Override
			public void process(Player player) {
				FightPit.updateWaitingRoom(player);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here. If you'd like to leave, use the northern exit.");
				return false;
			}

			@Override
			public void logout(Player player) {
				FightPit.removePlayer(player, "leave room");
			}

			@Override
			public void leave(Player player) {
				if(player.getLocation() != FIGHT_PITS) {
					FightPit.removePlayer(player, "leave room");
				}
			}

		},
		DUEL_ARENA(new int[]{3322, 3394, 3311, 3323, 3331, 3391}, new int[]{3195, 3291, 3223, 3248, 3242, 3260}, false, false, false, false, false, false) {
			@Override
			public void process(Player player) {
				if(player.getWalkableInterfaceId() != 201)
					player.getPacketSender().sendWalkableInterface(201);
				if(player.getDueling().duelingStatus == 0) {
					if(player.getPlayerInteractingOption() != PlayerInteractingOption.CHALLENGE)
						player.getPacketSender().sendInteractionOption("Challenge", 2, false);
				} else if(player.getPlayerInteractingOption() != PlayerInteractingOption.ATTACK)
					player.getPacketSender().sendInteractionOption("Attack", 2, true);
			}

			@Override
			public void enter(Player player) {
				PLAYERS_IN_DUEL_ARENA++;
				player.getPacketSender().sendMessage("<img=10> <col=996633>Warning! Do not stake items which you are not willing to lose.");
			}

			@Override
			public boolean canTeleport(Player player) {
				if(player.getDueling().duelingStatus == 5) {
					player.getPacketSender().sendMessage("To forfiet a duel, run to the west and use the trapdoor.");
					return false;
				}
				return true;
			}

			@Override
			public void logout(Player player) {
				boolean dc = false;
				if(player.getDueling().inDuelScreen && player.getDueling().duelingStatus != 5) {
					player.getDueling().declineDuel(player.getDueling().duelingWith > 0 ? true : false);
				} else if(player.getDueling().duelingStatus == 5) {
					if(player.getDueling().duelingWith > -1) {
						Player duelEnemy = World.getPlayers().get(player.getDueling().duelingWith);
						if(duelEnemy != null) {
							duelEnemy.getDueling().duelVictory();
						} else {
							dc = true;
						}
					}
				}
				player.moveTo(new Position(3368, 3268));
				if(dc) {
					World.getPlayers().remove(player);
				}
			}

			@Override
			public void leave(Player player) {
				if(player.getDueling().duelingStatus == 5) {
					onDeath(player);
				}
				PLAYERS_IN_DUEL_ARENA--;
			}

			@Override
			public void onDeath(Player player) {
				if(player.getDueling().duelingStatus == 5) {
					if(player.getDueling().duelingWith > -1) {
						Player duelEnemy = World.getPlayers().get(player.getDueling().duelingWith);
						if(duelEnemy != null) {
							duelEnemy.getDueling().duelVictory();
							duelEnemy.getPacketSender().sendMessage("You won the duel! Congratulations!");
						}
					}
					PlayerLogs.log(player.getUsername(), "Has lost their duel.");
					player.getPacketSender().sendMessage("You've lost the duel.");
					player.getDueling().arenaStats[1]++; player.getDueling().reset();
				}
				player.moveTo(new Position(3368 + Misc.getRandom(5), 3267+ Misc.getRandom(3)));
				player.getDueling().reset();
			}

			@Override
			public boolean canAttack(Player player, Player target) {
				if(target.getIndex() != player.getDueling().duelingWith) {
					player.getPacketSender().sendMessage("That player is not your opponent!");
					return false;
				}
				if(player.getDueling().timer != -1) {
					player.getPacketSender().sendMessage("You cannot attack yet!");
					return false;
				}
				return player.getDueling().duelingStatus == 5 && target.getDueling().duelingStatus == 5;
			}
		},
		GODWARS_DUNGEON(new int[]{2800, 2950, 2858, 2943}, new int[]{5200, 5400, 5180, 5230}, true, true, true, false, true, true) {
			@Override
			public void process(Player player) {

				if ((player.getPosition().getX() == 2842 && player.getPosition().getY() == 5308) //ARMADYL
						|| (player.getPosition().getX() == 2876 && player.getPosition().getY() == 5369) // BANDOS
						|| (player.getPosition().getX() == 2936 && player.getPosition().getY() == 5331) // ZAMMY
						|| (player.getPosition().getX() == 2907 && player.getPosition().getY() == 5272)) { //NORTH EAST, saradomin
					player.moveTo(new Position(player.getPosition().getX() - 1, player.getPosition().getY() - 1, player.getPosition().getZ()));
					player.getMovementQueue().reset();
				}
				if ((player.getPosition().getX() == 2842 && player.getPosition().getY() == 5296) //ARMADYL
						|| (player.getPosition().getX() == 2876 && player.getPosition().getY() == 5351) //BANDOS
						|| (player.getPosition().getX() == 2936 && player.getPosition().getY() == 5318) //ZAMMY
						|| (player.getPosition().getX() == 2907 && player.getPosition().getY() == 5258)) { // saradomin, SOUTH EAST
					player.moveTo(new Position(player.getPosition().getX() - 1, player.getPosition().getY() + 1, player.getPosition().getZ()));
					player.getMovementQueue().reset();
				}
				if ((player.getPosition().getX() == 2824 && player.getPosition().getY() == 5296) //ARMADYL
						|| (player.getPosition().getX() == 2864 && player.getPosition().getY() == 5351) //BANDOS
						|| (player.getPosition().getX() == 2918 && player.getPosition().getY() == 5318) //ZAMMY
						|| (player.getPosition().getX() == 2895 && player.getPosition().getY() == 5258)) { // saradomin, SOUTH WEST
					player.moveTo(new Position(player.getPosition().getX() + 1, player.getPosition().getY() + 1, player.getPosition().getZ()));
					player.getMovementQueue().reset();
				}
				if ((player.getPosition().getX() == 2824 && player.getPosition().getY() == 5308) //ARMADYL 
						|| (player.getPosition().getX() == 2864 && player.getPosition().getY() == 5369) //BANDOS
						|| (player.getPosition().getX() == 2918 && player.getPosition().getY() == 5331) //ZAMMY
						|| (player.getPosition().getX() == 2895 && player.getPosition().getY() == 5272)) { // saradomin, NORTH WEST
					player.moveTo(new Position(player.getPosition().getX() + 1, player.getPosition().getY() - 1, player.getPosition().getZ()));
					player.getMovementQueue().reset();
				}
				
				if(player.getWalkableInterfaceId() != 16210)
					player.getPacketSender().sendWalkableInterface(16210);
			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void onDeath(Player player) {
				leave(player);
			}

			@Override
			public void leave(Player p) {
				for(int i = 0; i < p.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount().length; i++) {
					p.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[i] = 0;
					p.getPacketSender().sendString((16216+i), "0");
				}
				p.getMinigameAttributes().getGodwarsDungeonAttributes().setAltarDelay(0).setHasEnteredRoom(false);
				p.getPacketSender().sendMessage("Your Godwars dungeon progress has been reset.");
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC n) {
				int index = -1;
				int npc = n.getId();
				if(npc == 6246 || npc == 6229 || npc == 6230 || npc == 6231) //Armadyl
					index = 0;
				else if(npc == 102 || npc == 3583 || npc == 115 || npc == 113 || npc == 6273 || npc == 6276 || npc == 6277 || npc == 6288) //Bandos
					index = 1;
				else if(npc == 6258 || npc == 6259 || npc == 6254 || npc == 6255 || npc == 6257 || npc == 6256) //Saradomin
					index = 2;
				else if(npc == 10216 || npc == 6216 || npc == 1220 || npc == 6007 || npc == 6219 ||npc == 6220 || npc == 6221 || npc == 49 || npc == 4418) //Zamorak
					index = 3;
				if(index != -1) {
					killer.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[index]++;
					killer.getPacketSender().sendString((16216+index), ""+killer.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[index]);
				}
				return false;
			}
		},
		NOMAD(new int[]{3342, 3377}, new int[]{5839, 5877}, true, true, false, true, false, true) {
			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here. If you'd like to leave, use the southern exit.");
				return false;
			}

			@Override
			public void leave(Player player) {
				if(player.getRegionInstance() != null)
					player.getRegionInstance().destruct();
				player.moveTo(new Position(1890, 3177));
				player.restart();
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				if(npc.getId() == 8528) {
					Nomad.endFight(killer, true);
					return true;
				}
				return false;
			}
		},
		RECIPE_FOR_DISASTER(new int[]{1885, 1913}, new int[]{5340, 5369}, true, true, false, false, false, false) {

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here. If you'd like to leave, use a portal.");
				return false;
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				RecipeForDisaster.handleNPCDeath(killer, npc);
				return true;
			}

			@Override
			public void leave(Player player) {
				if(player.getRegionInstance() != null)
					player.getRegionInstance().destruct();
				player.moveTo(new Position(3081, 3500));
			}

			@Override
			public void onDeath(Player player) {
				leave(player);
			}
		},
		FREE_FOR_ALL_ARENA(new int[]{2755, 2876}, new int[]{5512, 5627}, true, true, true, false, false, true) {
			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here, if you wish to teleport, use the portal.");
				return false;
			}

			@Override
			public void onDeath(Player player) {
				player.moveTo(new Position(2815, 5511));
			}

			@Override
			public boolean canAttack(Player player, Player target) {
				if(target.getLocation() != FREE_FOR_ALL_ARENA) {
					player.getPacketSender().sendMessage("That player has not entered the dangerous zone yet.");
					player.getMovementQueue().reset();
					return false;
				}
				return true;
			}

			@Override
			public void enter(Player player) {
				if(player.getPlayerInteractingOption() != PlayerInteractingOption.ATTACK) {
					player.getPacketSender().sendInteractionOption("Attack", 2, true);
				}
			}

		},
		FREE_FOR_ALL_WAIT(new int[]{2755, 2876}, new int[]{5507, 5627}, false, false, true, false, false, true) {
			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here, if you wish to teleport, use the portal.");
				return false;
			}

			@Override
			public void onDeath(Player player) {
				player.moveTo(new Position(2815, 5511));
			}
		},
		WARRIORS_GUILD(new int[]{2833, 2879}, new int[]{3531, 3559}, false, true, true, false, false, true) {
			@Override
			public void logout(Player player) {
				if(player.getMinigameAttributes().getWarriorsGuildAttributes().enteredTokenRoom()) {
					player.moveTo(new Position(2844, 3540, 2));
				}
			}
			@Override
			public void leave(Player player) {
				player.getMinigameAttributes().getWarriorsGuildAttributes().setEnteredTokenRoom(false);
			}
		},
		PURO_PURO(new int[]{2556, 2630}, new int[]{4281, 4354}, false, true, true, false, false, true) {
		},
		FLESH_CRAWLERS(new int[]{2033, 2049}, new int[]{5178, 5197}, false, true, true, false, true, true) {
		},
		RUNESPAN(new int[]{2122, 2159}, new int[]{5517, 5556}, false, false, true, true, true, false) {
		},
		DEFAULT(null, null, false, true, true, true, true, true) {
		};

		Location(int[] x, int[] y, boolean multi, boolean summonAllowed, boolean followingAllowed, boolean cannonAllowed, boolean firemakingAllowed, boolean aidingAllowed) {
			this.x = x;
			this.y = y;
			this.multi = multi;
			this.summonAllowed = summonAllowed;
			this.followingAllowed = followingAllowed;
			this.cannonAllowed = cannonAllowed;
			this.firemakingAllowed = firemakingAllowed;
			this.aidingAllowed = aidingAllowed;
		}
		private int[] x, y;
		private boolean multi;
		private boolean summonAllowed;
		private boolean followingAllowed;
		private boolean cannonAllowed;
		private boolean firemakingAllowed;
		private boolean aidingAllowed;

		public int[] getX() {
			return x;
		}

		public int[] getY() {
			return y;
		}
		/**
		MB_WYLDYWYRM(new int[]{3052, 3083}, new int[]{3929, 3963}, true, true, true, false, false, false) {},
		RC_WYLDYWYRM(new int[]{3294, 3315}, new int[]{3919, 3961}, true, true, true, false, false, false) {},
		CA_WYLDYWYRM(new int[]{3214, 3253}, new int[]{3594, 3639}, true, true, true, false, false, false) {},
		DR_WYLDYWYRM(new int[]{3266, 3306}, new int[]{3868, 3903}, true, true, true, false, false, false) {},
		 */
		public static boolean inMulti(Character gc) {
			if(gc.getLocation() == WILDERNESS) {
				int x = gc.getPosition().getX(), y = gc.getPosition().getY();
				if(x >= 3250 && x <= 3302 && y >= 3905 && y <= 3925 || x >= 3020 && x <= 3055 && y >= 3684 && y <= 3711 || x >= 3150 && x <= 3195 && y >= 2958 && y <= 3003 || x >= 3645 && x <= 3715 && y >= 3454 && y <= 3550 || x>= 3150 && x <= 3199 && y >= 3796 && y <= 3869 || x >= 2994 && x <= 3041 && y >= 3733 && y <= 3790)
					return true;
				if(x >= 3336 && x <= 3371 && y >= 3792 && y <= 3819) //zulrah pinnensula
					return true;
				//wyrm multi handler
					if( x >= 3052 && x <= 3083 && y >= 3929 && y <= 3963 ||
						x >= 3294 && x <= 3315 && y >= 3919 && y <= 3961 ||
						x >= 3214 && x <= 3253 && y >= 3594 && y <= 3639 ||
						x >= 3266 && x <= 3306 && y >= 3868 && y <= 3903 ||
						x >= 3169 && x <= 3221 && y >= 3651 && y <= 3700 ||
						x >= 3152 && x <= 3190 && y >= 3776 && y <= 3817)
					return true;
				//z x1: 3336, x2: 3371, y1: 3819, y2: 3792 
			}
			return gc.getLocation().multi;
		}

		public boolean isSummoningAllowed() {
			return summonAllowed;
		}

		public boolean isFollowingAllowed() {
			return followingAllowed;
		}

		public boolean isCannonAllowed() {
			return cannonAllowed;
		}

		public boolean isFiremakingAllowed() {
			return firemakingAllowed;
		}

		public boolean isAidingAllowed() {
			return aidingAllowed;
		}

		public static Location getLocation(Entity gc) {
			for(Location location : Location.values()) {
				if(location != Location.DEFAULT)
					if(inLocation(gc, location))
						return location;
			}
			return Location.DEFAULT;
		}

		public static boolean inLocation(Entity gc, Location location) {
			if(location == Location.DEFAULT) {
				if(getLocation(gc) == Location.DEFAULT)
					return true;
				else
					return false;
			}
			/*if(gc instanceof Player) {
				Player p = (Player)gc;
				if(location == Location.TRAWLER_GAME) {
					String state = FishingTrawler.getState(p);
					return (state != null && state.equals("PLAYING"));
				} else if(location == FIGHT_PITS_WAIT_ROOM || location == FIGHT_PITS) {
					String state = FightPit.getState(p), needed = (location == FIGHT_PITS_WAIT_ROOM) ? "WAITING" : "PLAYING";
					return (state != null && state.equals(needed));
				} else if(location == Location.SOULWARS) {
					return (SoulWars.redTeam.contains(p) || SoulWars.blueTeam.contains(p) && SoulWars.gameRunning);
				} else if(location == Location.SOULWARS_WAIT) {
					return SoulWars.isWithin(SoulWars.BLUE_LOBBY, p) || SoulWars.isWithin(SoulWars.RED_LOBBY, p);
				}
			}
			 */
			return inLocation(gc.getPosition().getX(), gc.getPosition().getY(), location);
		}

		public static boolean inLocation(int absX, int absY, Location location) {
			int checks = location.getX().length - 1;
			for(int i = 0; i <= checks; i+=2) {
				if(absX >= location.getX()[i] && absX <= location.getX()[i + 1]) {
					if(absY >= location.getY()[i] && absY <= location.getY()[i + 1]) {
						return true;
					}
				}
			}
			return false;
		}

		public void process(Player player) {

		}

		public boolean canTeleport(Player player) {
			return true;
		}

		public void login(Player player) {

		}

		public void enter(Player player) {

		}

		public void leave(Player player) {

		}

		public void logout(Player player) {

		}

		public void onDeath(Player player) {

		}

		public boolean handleKilledNPC(Player killer, NPC npc) {
			return false;
		}

		public boolean canAttack(Player player, Player target) {
			return false;
		}

		/** SHOULD AN ENTITY FOLLOW ANOTHER ENTITY NO MATTER THE DISTANCE BETWEEN THEM? **/
		public static boolean ignoreFollowDistance(Character character) {
			Location location = character.getLocation();
			return location == Location.FIGHT_CAVES || location == Location.RECIPE_FOR_DISASTER || location == Location.NOMAD;
		}
	}

	public static void process(Character gc) {
		Location newLocation = Location.getLocation(gc);
		if(gc.getLocation() == newLocation) {
			if(gc.isPlayer()) {
				Player player = (Player) gc;
				gc.getLocation().process(player);
				if(Location.inMulti(player)) {
					if(player.getMultiIcon() != 1)
						player.getPacketSender().sendMultiIcon(1);
				} else if(player.getMultiIcon() == 1)
					player.getPacketSender().sendMultiIcon(0);
			}
		} else {
			Location prev = gc.getLocation();
			if(gc.isPlayer()) {
				Player player = (Player) gc;
				if(player.getMultiIcon() > 0)
					player.getPacketSender().sendMultiIcon(0);
				if(player.getWalkableInterfaceId() > 0 && player.getWalkableInterfaceId() != 37400 && player.getWalkableInterfaceId() != 50000)
					player.getPacketSender().sendWalkableInterface(-1);
				if(player.getPlayerInteractingOption() != PlayerInteractingOption.NONE)
					player.getPacketSender().sendInteractionOption("null", 2, true);
			}
			gc.setLocation(newLocation);
			if(gc.isPlayer()) {
				prev.leave(((Player)gc));
				gc.getLocation().enter(((Player)gc));
			}
		}
	}

	public static boolean goodDistance(int objectX, int objectY, int playerX,
			int playerY, int distance) {
		if (playerX == objectX && playerY == objectY)
			return true;
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == playerX
						&& ((objectY + j) == playerY
						|| (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if ((objectX - i) == playerX
						&& ((objectY + j) == playerY
						|| (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if (objectX == playerX
						&& ((objectY + j) == playerY
						|| (objectY - j) == playerY || objectY == playerY)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean goodDistance(Position pos1, Position pos2, int distanceReq) {
		if(pos1.getZ() != pos2.getZ())
			return false;
		return goodDistance(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY(), distanceReq);
	}

	public static int distanceTo(Position position, Position destination,
			int size) {
		final int x = position.getX();
		final int y = position.getY();
		final int otherX = destination.getX();
		final int otherY = destination.getY();
		int distX, distY;
		if (x < otherX)
			distX = otherX - x;
		else if (x > otherX + size)
			distX = x - (otherX + size);
		else
			distX = 0;
		if (y < otherY)
			distY = otherY - y;
		else if (y > otherY + size)
			distY = y - (otherY + size);
		else
			distY = 0;
		if (distX == distY)
			return distX + 1;
		return distX > distY ? distX : distY;
	}
}
