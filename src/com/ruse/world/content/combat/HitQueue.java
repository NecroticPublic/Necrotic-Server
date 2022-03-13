package com.ruse.world.content.combat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.GraphicHeight;
import com.ruse.model.Locations.Location;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.WeaponAnimations;
import com.ruse.util.Misc;
import com.ruse.world.content.Achievements;
import com.ruse.world.content.Achievements.AchievementData;
import com.ruse.world.content.Kraken;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.combat.strategy.impl.Nex;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.npc.NPCMovementCoordinator.CoordinateState;
import com.ruse.world.entity.impl.player.Player;

public class HitQueue {

	public final CopyOnWriteArrayList<CombatHit> combat_hits = new CopyOnWriteArrayList<CombatHit>();

	public void append(CombatHit c) {
		if(c == null) {
			return;
		}
		if(c.initialRun()) {
			c.handleAttack();
		} else {
			combat_hits.add(c);
		}
	}

	public void process() {
		for(CombatHit c : combat_hits) {
			if(c == null) {
				combat_hits.remove(c);
				continue;
			}
			if(c.delay > 0) {
				c.delay--;
			} else {
				c.handleAttack();
				combat_hits.remove(c);
			}
		}
	}

	public static class CombatHit {

		/** The attacker instance. */
		private Character attacker;

		/** The victim instance. */
		private Character victim;

		/** The attacker's combat builder attached to this task. */
		private CombatBuilder builder;

		/** The attacker's combat container that will be used. */
		private CombatContainer container;

		/** The total damage dealt during this hit. */
		private int damage;

		private int initialDelay;
		private int delay;


		public CombatHit(CombatBuilder builder, CombatContainer container) {
			this.builder = builder;
			this.container = container;
			this.attacker = builder.getCharacter();
			this.victim = builder.getVictim();
		}

		public CombatHit(CombatBuilder builder, CombatContainer container, int delay) {
			this.builder = builder;
			this.container = container;
			this.attacker = builder.getCharacter();
			this.victim = builder.getVictim();
			this.delay = initialDelay = delay;
		}

		public void handleAttack() {
			if (attacker.getConstitution() <= 0 || !attacker.isRegistered()) {
				return;
			}
			if(victim == null) {
				return;
			}
			// Do any hit modifications to the container here first.

			if(attacker.isPlayer() && victim.isNpc()) {
				NPC npc = (NPC)victim;
				if(Kraken.isWhirpool(npc)) {
					Kraken.attackPool(((Player)attacker), npc);
					return;
				}
			}

			if(container.getModifiedDamage() > 0) {
				container.allHits(context -> {
					context.getHit().setDamage(container.getModifiedDamage());
					context.setAccurate(true);
				});
			}

			// Now we send the hitsplats if needed! We can't send the hitsplats
			// there are none to send, or if we're using magic and it splashed.
			if (container.getHits().length != 0 && container.getCombatType() != CombatType.MAGIC || container.isAccurate()) {

				/** PRAYERS **/
				CombatFactory.applyPrayerProtection(container, builder);

				this.damage = container.getDamage();
				victim.getCombatBuilder().addDamage(attacker, damage);
				container.dealDamage();

				/** MISC **/
				if(attacker.isPlayer()) {
					Player p = (Player)attacker;
					if(damage > 0) {
						if(p.getLocation() == Location.PEST_CONTROL_GAME) {
							p.getMinigameAttributes().getPestControlAttributes().incrementDamageDealt(damage);
						} else if(p.getLocation() == Location.DUNGEONEERING) {
							p.getMinigameAttributes().getDungeoneeringAttributes().incrementDamageDealt(damage);
						}
						/** ACHIEVEMENTS **/
						if(container.getCombatType() == CombatType.MELEE) {
							Achievements.doProgress(p, AchievementData.DEAL_EASY_DAMAGE_USING_MELEE, damage);
							Achievements.doProgress(p, AchievementData.DEAL_MEDIUM_DAMAGE_USING_MELEE, damage);
							Achievements.doProgress(p, AchievementData.DEAL_HARD_DAMAGE_USING_MELEE, damage);
						} else if(container.getCombatType() == CombatType.RANGED) {
							Achievements.doProgress(p, AchievementData.DEAL_EASY_DAMAGE_USING_RANGED, damage);
							Achievements.doProgress(p, AchievementData.DEAL_MEDIUM_DAMAGE_USING_RANGED, damage);
							Achievements.doProgress(p, AchievementData.DEAL_HARD_DAMAGE_USING_RANGED, damage);
						} else if(container.getCombatType() == CombatType.MAGIC) {
							Achievements.doProgress(p, AchievementData.DEAL_EASY_DAMAGE_USING_MAGIC, damage);
							Achievements.doProgress(p, AchievementData.DEAL_MEDIUM_DAMAGE_USING_MAGIC, damage);
							Achievements.doProgress(p, AchievementData.DEAL_HARD_DAMAGE_USING_MAGIC, damage);
						}
						if(victim.isPlayer()) {
							Achievements.finishAchievement(p, AchievementData.FIGHT_ANOTHER_PLAYER);
						}
					}
				} else {
					if(victim.isPlayer() && container.getCombatType() == CombatType.DRAGON_FIRE) {
						Player p = (Player)victim;
						if(Misc.getRandom(4) <= 3 && p.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 11283) {
							p.setPositionToFace(attacker.getPosition().copy());
							CombatFactory.chargeDragonFireShield(p);
						}
						if(p.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 1540 || p.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 13655) {
							p.setPositionToFace(attacker.getPosition().copy());
							CombatFactory.sendFireMessage(p);
					}
						if(damage >= 160) {
							((Player)victim).getPacketSender().sendMessage("You are badly burnt by the dragon's fire!");
						}
					}
				}
			}


			// Give experience based on the hits.
			CombatFactory.giveExperience(builder, container, damage);

			if (!container.isAccurate()) {
				if (container.getCombatType() == CombatType.MAGIC && attacker.getCurrentlyCasting() != null) {
					victim.performGraphic(new Graphic(85, GraphicHeight.MIDDLE));
					attacker.getCurrentlyCasting().finishCast(attacker, victim, false, 0);
					attacker.setCurrentlyCasting(null);
				}
			} else if (container.isAccurate()) {

				CombatFactory.handleArmorEffects(attacker, victim, damage, container.getCombatType());
				CombatFactory.handlePrayerEffects(attacker, victim, damage, container.getCombatType());
				CombatFactory.handleSpellEffects(attacker, victim, damage, container.getCombatType());

				attacker.poisonVictim(victim, container.getCombatType());

				// Finish the magic spell with the correct end graphic.
				if (container.getCombatType() == CombatType.MAGIC && attacker.getCurrentlyCasting() != null) {
					attacker.getCurrentlyCasting().endGraphic().ifPresent(victim::performGraphic);
					attacker.getCurrentlyCasting().finishCast(attacker, victim, true, damage);
					attacker.setCurrentlyCasting(null);
				}
			}
			
			// Degrade items that need to be degraded
			if (victim.isPlayer()) {
				CombatFactory.handleDegradingArmor((Player)victim);
			}
			if (attacker.isPlayer()) {
				CombatFactory.handleDegradingWeapons((Player)attacker);
			}

			// Send the defensive animations.
			if(victim.getCombatBuilder().getAttackTimer() <= 2) {
				if (victim.isPlayer()) {
					victim.performAnimation(new Animation(WeaponAnimations.getBlockAnimation(((Player)victim))));
					if(((Player)victim).getInterfaceId() > 0)
						((Player)victim).getPacketSender().sendInterfaceRemoval();
				} else if (victim.isNpc()) {
					if(!(((NPC)victim).getId() >= 6142 && ((NPC)victim).getId() <= 6145))
						victim.performAnimation(new Animation(((NPC) victim).getDefinition().getDefenceAnimation()));
				}
			}

			// Fire the container's dynamic hit method.
			container.onHit(damage, container.isAccurate());

			// And finally auto-retaliate if needed.
			if(!victim.getCombatBuilder().isAttacking() || victim.getCombatBuilder().isCooldown() || victim.isNpc() && ((NPC)victim).findNewTarget()) {
				if(shouldRetaliate()) {
					if(initialDelay == 0) {
						TaskManager.submit(new Task(1, victim, false) {
							@Override
							protected void execute() {
								if(shouldRetaliate()) {
									retaliate();
								}
								stop();
							}
						});
					} else {
						retaliate();
					}
				}
			}

			if(attacker.isNpc() && victim.isPlayer()) {
				NPC npc = (NPC)attacker;
				Player p = (Player)victim;
				if(npc.switchesVictim() && Misc.getRandom(6) <= 1) {
					if(npc.getDefinition().isAggressive()) {
						npc.setFindNewTarget(true);
					} else {
						if(p.getLocalPlayers().size() >= 1) {
							List<Player> list = p.getLocalPlayers();
							Player c = list.get(Misc.getRandom(list.size() - 1));
							npc.getCombatBuilder().attack(c);
						}
					}
				}

				Sounds.sendSound(p, Sounds.getPlayerBlockSounds(p.getEquipment().get(Equipment.WEAPON_SLOT).getId()));
				/** CUSTOM ON DAMAGE STUFF **/
				if(victim.isPlayer() && npc.getId() == 13447) {
					Nex.dealtDamage(((Player)victim), damage);
				}

			} else if(attacker.isPlayer()) {
				Player player = (Player)attacker;

				player.getPacketSender().sendCombatBoxData(victim);

				/** SKULLS **/
				if(player.getLocation() == Location.WILDERNESS && victim.isPlayer()) {
					boolean didRetaliate = player.getCombatBuilder().didAutoRetaliate();
					if(!didRetaliate) {
						boolean soloRetaliate = !player.getCombatBuilder().isBeingAttacked();
						boolean multiRetaliate = player.getCombatBuilder().isBeingAttacked() && player.getCombatBuilder().getLastAttacker() != victim && Location.inMulti(player);
						if (soloRetaliate || multiRetaliate) {
							CombatFactory.skullPlayer(player);
						}
					}
				}

				player.setLastCombatType(container.getCombatType());

				Sounds.sendSound(player, Sounds.getPlayerAttackSound(player));

				/** CUSTOM ON DAMAGE STUFF **/
				if(victim.isNpc()) {
					if(((NPC)victim).getId() == 13447) {
						Nex.takeDamage(player, damage);
					}
				} else {
					Sounds.sendSound((Player)victim, Sounds.getPlayerBlockSounds(((Player)victim).getEquipment().get(Equipment.WEAPON_SLOT).getId()));
				}
			}
		}

		public boolean shouldRetaliate() {
			if(victim.isPlayer()) {
				if(attacker.isNpc()) {
					if(!((NPC)attacker).getDefinition().isAttackable()) {
						return false;
					}
				}
				return victim.isPlayer() && ((Player)victim).isAutoRetaliate() && !victim.getMovementQueue().isMoving() && ((Player)victim).getWalkToTask() == null;
			} else if(!(attacker.isNpc() && ((NPC)attacker).isSummoningNpc())) {
				NPC npc = (NPC)victim;
				return npc.getMovementCoordinator().getCoordinateState() == CoordinateState.HOME && npc.getLocation() != Location.PEST_CONTROL_GAME;
			}
			return false;
		}

		public void retaliate() {
			if (victim.isPlayer()) {
				victim.getCombatBuilder().setDidAutoRetaliate(true);
				victim.getCombatBuilder().attack(attacker);
			} else if(victim.isNpc()) {
				NPC npc = (NPC)victim;
				npc.getCombatBuilder().attack(attacker);
				npc.setFindNewTarget(false);
			}
		}

		private boolean initialRun() {
			return this.delay == 0;
		}
	}
}
