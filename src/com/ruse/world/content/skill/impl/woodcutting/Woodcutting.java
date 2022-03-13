package com.ruse.world.content.skill.impl.woodcutting;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.GameObject;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Equipment;
import com.ruse.util.Misc;
import com.ruse.world.content.Achievements;
import com.ruse.world.content.Achievements.AchievementData;
import com.ruse.world.content.CustomObjects;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.content.randomevents.EvilTree;
import com.ruse.world.content.randomevents.EvilTree.EvilTreeDef;
import com.ruse.world.content.skill.impl.firemaking.Logdata;
import com.ruse.world.content.skill.impl.firemaking.Logdata.logData;
import com.ruse.world.content.skill.impl.woodcutting.WoodcuttingData.Hatchet;
import com.ruse.world.content.skill.impl.woodcutting.WoodcuttingData.Trees;
import com.ruse.world.entity.impl.player.Player;

public class Woodcutting {

	public static void cutWood(final Player player, final GameObject object, boolean restarting) {
		if(!restarting)
			player.getSkillManager().stopSkilling();
		if(player.getInventory().getFreeSlots() == 0) {
			player.getPacketSender().sendMessage("You don't have enough free inventory space.");
			return;
		}
		player.setPositionToFace(object.getPosition());
		final int objId = object.getId();
		final Hatchet h = Hatchet.forId(WoodcuttingData.getHatchet(player));
		if (h != null) {
			if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) >= h.getRequiredLevel()) {
				final Trees t = Trees.forId(objId);
				final EvilTreeDef t2 = EvilTreeDef.forId(objId);
				final boolean isEvilTree = t2 != null;
				
				if (isEvilTree) {
					//player.getPacketSender().sendMessage("Evil tree method.");
					EvilTree.handleCutWood(player, object, h, t2);
					return;
				}
				
				if (t != null) {
					player.setEntityInteraction(object);
					if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) >= (isEvilTree ? t2.getWoodcuttingLevel() : t.getReq())) {
						player.performAnimation(new Animation(h.getAnim()));
						int delay = Misc.getRandom(t.getTicks() - WoodcuttingData.getChopTimer(player, h)) +1;
						player.setCurrentTask(new Task(1, player, false) {
							int cycle = 0, reqCycle = delay >= 2 ? delay : Misc.getRandom(1) + 1;
							@Override
							public void execute() {
								if(player.getInventory().getFreeSlots() == 0) {
									player.performAnimation(new Animation(65535));
									player.getPacketSender().sendMessage("You don't have enough free inventory space.");
									this.stop();
									return;
								}
								if (cycle != reqCycle) {
									cycle++;
									player.performAnimation(new Animation(h.getAnim()));
								} else if (cycle >= reqCycle) {
									int xp = isEvilTree ? t2.getWoodcuttingXp() : t.getXp();
									if(lumberJack(player))
										xp *= 1.5;
									player.getSkillManager().addExperience(Skill.WOODCUTTING, (int) (xp));
									cycle = 0;
									BirdNests.dropNest(player);
									this.stop();
									int cutDownRandom = Misc.getRandom(100);
								//	player.getPacketSender().sendMessage("Random: " + cutDownRandom);
									if (!isEvilTree && (!t.isMulti() || (player.getSkillManager().skillCape(Skill.WOODCUTTING) && cutDownRandom >= 88) || (!player.getSkillManager().skillCape(Skill.WOODCUTTING) && cutDownRandom >= 82))) {//82
										//player.getPacketSender().sendMessage("You rolled a: "+cutDownRandom);
										player.getInventory().add(isEvilTree ? t2.getLog() : t.getReward(), 1);
										treeRespawn(player, object);
										player.getPacketSender().sendMessage("You've chopped the tree down.");
										player.performAnimation(new Animation(65535));
									} else { //if they didn't cut down the tree
										cutWood(player, object, true);
										if(player.getSkillManager().skillCape(Skill.WOODCUTTING) && cutDownRandom >= 82 && cutDownRandom < 87) {
											player.getPacketSender().sendMessage("Your cape helps keep the tree alive a little longer.");
										}
										if(infernoAdze(player)) { //if they do not have an adze equipped
											if(Misc.getRandom(10) <= 6) {
												logData fmLog = Logdata.getLogData(player, isEvilTree ? t2.getLog() : t.getReward());
												if(fmLog != null) { //if their their logdata is not null...
													player.getSkillManager().addExperience(Skill.FIREMAKING, fmLog.getXp());
													player.getPacketSender().sendMessage("You chop a log, and your Inferno Adze burns it into ash.");
													if(fmLog == Logdata.logData.OAK) {
														Achievements.finishAchievement(player, AchievementData.BURN_AN_OAK_LOG);
													} else if(fmLog == Logdata.logData.MAGIC) {
														Achievements.doProgress(player, AchievementData.BURN_100_MAGIC_LOGS);
														Achievements.doProgress(player, AchievementData.BURN_2500_MAGIC_LOGS);
													}
												} else { //if the fmLog data is null
													player.getPacketSender().sendMessage("<col=b40404>The game thinks you have an adze, but are burning nothing.").sendMessage("<col=b40404>Please contact Crimson and report this bug.");
												}
											} else {
												player.getInventory().add(t.getReward(), 1);
												player.getPacketSender().sendMessage("You get some logs...");
											}
										} else { //if they player doesn't have an adze, do this.
											player.getInventory().add(t.getReward(), 1);
											player.getPacketSender().sendMessage("You get some logs...");
										}
									}
									Sounds.sendSound(player, Sound.WOODCUT);
									if(t != null && t == Trees.OAK) {
										Achievements.finishAchievement(player, AchievementData.CUT_AN_OAK_TREE);
									} else if(t != null && t == Trees.MAGIC) {
										Achievements.doProgress(player, AchievementData.CUT_100_MAGIC_LOGS);
										Achievements.doProgress(player, AchievementData.CUT_5000_MAGIC_LOGS);
									}
								}
							}
						});
						TaskManager.submit(player.getCurrentTask());
					} else {
						player.getPacketSender().sendMessage("You need a Woodcutting level of at least "+t.getReq()+" to cut this tree.");
					}
				}
			} else {
				player.getPacketSender().sendMessage("You do not have a hatchet which you have the required Woodcutting level to use.");
			}
		} else {
			player.getPacketSender().sendMessage("You do not have a hatchet that you can use.");
		}
	}
	
	public static boolean lumberJack(Player player) {
		return player.getEquipment().get(Equipment.HEAD_SLOT).getId() == 10941 && player.getEquipment().get(Equipment.BODY_SLOT).getId() == 10939 && player.getEquipment().get(Equipment.LEG_SLOT).getId() == 10940 && player.getEquipment().get(Equipment.FEET_SLOT).getId() == 10933; 
	}
	
	public static boolean infernoAdze(Player player) {
		return player.getEquipment().get(Equipment.WEAPON_SLOT).getId() == 13661;
	}

	public static void treeRespawn(final Player player, final GameObject oldTree) {
		if(oldTree == null || oldTree.getPickAmount() >= 10)
			return;
		oldTree.setPickAmount(10);
		for(Player players : player.getLocalPlayers()) {
			if(players == null)
				continue;
			if(players.getInteractingObject() != null && players.getInteractingObject().getPosition().equals(player.getInteractingObject().getPosition().copy())) {
				players.getSkillManager().stopSkilling();
				players.getPacketSender().sendClientRightClickRemoval();
			}
		}
		player.getPacketSender().sendClientRightClickRemoval();
		player.getSkillManager().stopSkilling();
		CustomObjects.globalObjectRespawnTask(new GameObject(1343, oldTree.getPosition().copy(), 10, 0), oldTree, 20 + Misc.getRandom(10));
	}

}
