package com.ruse.world.content.skill.impl.mining;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.GameMode;
import com.ruse.model.GameObject;
import com.ruse.model.Graphic;
import com.ruse.model.Locations;
import com.ruse.model.Skill;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.Achievements;
import com.ruse.world.content.Achievements.AchievementData;
import com.ruse.world.content.CustomObjects;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.content.randomevents.ShootingStar;
import com.ruse.world.content.skill.impl.mining.MiningData.Ores;
import com.ruse.world.content.skill.impl.mining.MiningData.Pickaxe;
import com.ruse.world.content.skill.impl.smithing.Smelting;
import com.ruse.world.content.skill.impl.smithing.SmithingData;
import com.ruse.world.entity.impl.player.Player;

public class Mining {

	public static void startMining(final Player player, final GameObject oreObject) {
		player.getSkillManager().stopSkilling();
		player.getPacketSender().sendInterfaceRemoval();
		if(!Locations.goodDistance(player.getPosition().copy(), oreObject.getPosition(), 1) && oreObject.getId() != 24444  && oreObject.getId() != 24445 && oreObject.getId() != 38660)
			return;
		if(player.busy() || player.getCombatBuilder().isBeingAttacked() || player.getCombatBuilder().isAttacking()) {
			player.getPacketSender().sendMessage("You cannot do that right now.");
			return;
		}
		if(player.getInventory().getFreeSlots() == 0) {
			player.getPacketSender().sendMessage("You do not have any free inventory space left.");
			return;
		}
		player.setInteractingObject(oreObject);
		player.setPositionToFace(oreObject.getPosition());
		final Ores o = MiningData.forRock(oreObject.getId());
		final boolean giveGem = o != Ores.Rune_essence && o != Ores.Pure_essence;
		final int reqCycle = o == Ores.Runite ? 6 + Misc.getRandom(2) : Misc.getRandom(o.getTicks() - 1);
		if (o != null) {
			final int pickaxe = MiningData.getPickaxe(player);
			final int miningLevel = player.getSkillManager().getCurrentLevel(Skill.MINING);
			if (pickaxe > 0) {
				if (miningLevel >= o.getLevelReq()) {
					final MiningData.Pickaxe p = MiningData.forPick(pickaxe);
					if (miningLevel >= p.getReq()) {
						if (MiningData.isHoldingPickaxe(player)) {
							player.performAnimation(new Animation(12003));
						} else {
							player.performAnimation(new Animation(p.getAnim()));
						}
						final int delay = o.getTicks() - MiningData.getReducedTimer(player, p);
						player.setCurrentTask(new Task(delay >= 2 ? delay : 1, player, false) {
							int cycle = 0;
							@Override
							public void execute() {
								if(player.getInteractingObject() == null || player.getInteractingObject().getId() != oreObject.getId()) {
									player.getSkillManager().stopSkilling();
									player.performAnimation(new Animation(65535));
									stop();
									return;
								}
								if(player.getInventory().getFreeSlots() == 0) {
									player.performAnimation(new Animation(65535));
									stop();
									player.getPacketSender().sendMessage("You do not have any free inventory space left.");
									return;
								}
								if (cycle != reqCycle) {
									cycle++;
									if (MiningData.isHoldingPickaxe(player)) {
										player.performAnimation(new Animation(12003));
									} else {
										player.performAnimation(new Animation(p.getAnim()));
									}
								}
								if(giveGem) {
									boolean onyx = (o == Ores.Runite || o == Ores.CRASHED_STAR) && Misc.getRandom(o == Ores.CRASHED_STAR ? 20000 : 5000) == 1;
									if(onyx || Misc.getRandom(o == Ores.CRASHED_STAR ? 35 : 50) == 15) {
										int gemId = onyx ? 6571 : MiningData.RANDOM_GEMS[(int)(MiningData.RANDOM_GEMS.length * Math.random())];
										if(player.getSkillManager().skillCape(Skill.MINING) && player.getGameMode() != GameMode.ULTIMATE_IRONMAN){
												if (player.getBank(0).isFull()) {
													player.getPacketSender().sendMessage("Your cape failed at trying to bank your gem, because your bank was full.");
													player.getInventory().add(gemId, 1);
												} else {
													player.getPacketSender().sendMessage("Your cape banked a gem while you were mining!");
													player.getBank(0).add(gemId, 1);
												}
										} else {
											if (player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
												player.getInventory().add(gemId+1, 1);
												//player.getPacketSender().sendMessage("As a UIM, your cape won't bank gems, but here's a noted one.");
											} else {
												player.getInventory().add(gemId, 1);
												player.getPacketSender().sendMessage("You've found a gem!");
											}
										}
										if(gemId == 6571) {
											String s = o == Ores.Runite ? "Runite ore" : "Crashed star";
											World.sendMessage("<img=10><col=009966><shad=0> "+player.getUsername()+" has just received an Uncut Onyx from mining a "+s+"!");
										}
									}
								}
								if (cycle == reqCycle) {
									if(o == Ores.Iron) {
										Achievements.finishAchievement(player, AchievementData.MINE_SOME_IRON);
									} else if(o == Ores.Runite) {
										Achievements.doProgress(player, AchievementData.MINE_25_RUNITE_ORES);
										Achievements.doProgress(player, AchievementData.MINE_2000_RUNITE_ORES);
									}
									if(o.getItemId() != -1) {
										if (o == Ores.Coal && player.getSkillManager().skillCape(Skill.MINING) && Misc.getRandom(3) == 1) {
											player.getInventory().add(o.getItemId(), 2);
											player.getPacketSender().sendMessage("Your cape allows you to mine an additional coal.");
										} else {
											player.getInventory().add(o.getItemId(), 1);
										}
									}
									player.getSkillManager().addExperience(Skill.MINING, (int) (o.getXpAmount()));
									if(o == Ores.CRASHED_STAR) {
										player.getPacketSender().sendMessage("You mine the crashed star..");
									} else {
										player.getPacketSender().sendMessage("You mine some ore.");
									}
									
									/** ADZE EFFECT **/
									if(pickaxe == Pickaxe.Adze.getId()){
										if(Misc.getRandom(100) >= 75){
											switch(o) {
											case Adamantite:
												handleAdze(player, oreObject, 2361);
												break;
											case Gold:
												handleAdze(player, oreObject, 2357);
												break;
											case Iron:
												handleAdze(player, oreObject, 2351);
												break;
											case Mithril:
												handleAdze(player, oreObject, 2359);
												break;
											case Runite:
												handleAdze(player, oreObject, 2363);
												break;
											case Silver:
												handleAdze(player, oreObject, 2355);
												break;
											case Tin:
											case Copper:
												handleAdze(player, oreObject, 2349);
												break;
											}
										}
									}
									
									Sounds.sendSound(player, Sound.MINE_ITEM);

									cycle = 0;
									this.stop();
									if(o.getRespawn() > 0) {
										player.performAnimation(new Animation(65535));
										oreRespawn(player, oreObject, o);
									} else {
										if(oreObject.getId() == 38660) {
											if(ShootingStar.CRASHED_STAR == null || ShootingStar.CRASHED_STAR.getStarObject().getPickAmount() >= ShootingStar.MAXIMUM_MINING_AMOUNT) {
												player.getPacketSender().sendClientRightClickRemoval();
												player.getSkillManager().stopSkilling();
												return;
											} else {
												ShootingStar.CRASHED_STAR.getStarObject().incrementPickAmount();
											}
										} else {
											player.performAnimation(new Animation(65535));
										}
										startMining(player, oreObject);
									}
								}
							}
						});
						TaskManager.submit(player.getCurrentTask());
					} else {
						player.getPacketSender().sendMessage("You need a Mining level of at least "+p.getReq()+" to use this pickaxe.");
					}
				} else {
					player.getPacketSender().sendMessage("You need a Mining level of at least "+o.getLevelReq()+" to mine this rock.");
				}
			} else {
				player.getPacketSender().sendMessage("You don't have a pickaxe to mine this rock with.");
			}
		}
	}
	
	public static void handleAdze(final Player player, final GameObject oreObject, final int barId) {
		if(SmithingData.hasOres(player, barId)) {
			Smelting.handleBarCreation(barId, player);
			player.getPacketSender().sendMessage("The heat from your Inferno adze immediately ignites the ore and smelts it.");
			player.setInteractingObject(oreObject);
			player.setPositionToFace(oreObject.getPosition());
			oreObject.performGraphic(new Graphic(453));
			player.setInteractingObject(oreObject);
			player.setPositionToFace(oreObject.getPosition());
		}
	}

	public static void oreRespawn(final Player player, final GameObject oldOre, Ores o) {
		if(oldOre == null || oldOre.getPickAmount() >= 1)
			return;
		oldOre.setPickAmount(1);
		for(Player players : player.getLocalPlayers()) {
			if(players == null)
				continue;
			if(players.getInteractingObject() != null && players.getInteractingObject().getPosition().equals(player.getInteractingObject().getPosition().copy())) {
				players.getPacketSender().sendClientRightClickRemoval();
				players.getSkillManager().stopSkilling();
			}
		}
		player.getPacketSender().sendClientRightClickRemoval();
		player.getSkillManager().stopSkilling();
		CustomObjects.globalObjectRespawnTask(new GameObject(452, oldOre.getPosition().copy(), 10, 0), oldOre, o.getRespawn());
	}
}
