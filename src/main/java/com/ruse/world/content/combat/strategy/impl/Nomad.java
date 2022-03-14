package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.GraphicHeight;
import com.ruse.model.Locations;
import com.ruse.model.Projectile;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;

public class Nomad implements CombatStrategy {

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return true;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character attacker, Character target) {
		int randomNomad = Misc.getRandom(30);
		NPC nomad = (NPC)attacker;
		if(target.getConstitution() <= 0) {
			nomad.forceChat("Muhahaha, easy!");
			return true;
		}
		if(nomad.isChargingAttack())
			return true;

		if (randomNomad >= 0 && randomNomad <= 15) {
			boolean meleeDistance = Locations.goodDistance(nomad.getPosition(), target.getPosition(), 2);
			boolean heal = !nomad.hasHealed() && nomad.getConstitution() < 4000;
			if(meleeDistance) {
				if (nomad.getConstitution() > 0 && !heal) {
					nomad.performAnimation(anim2);
					nomad.getCombatBuilder().setContainer(new CombatContainer(nomad, target, 1, 1, CombatType.MELEE, true));
				} else {
					nomad.setHealed(true);
					nomad.performGraphic(gfx2);
					nomad.performAnimation(anim3);
					nomad.getMovementQueue().setLockMovement(true);
					nomad.forceChat("Zamorak.. Aid me..");
					nomad.setChargingAttack(true);
					TaskManager.submit(new Task(1, nomad, false) {
						int ticks = 0;
						@Override
						public void execute() {
							nomad.setConstitution(nomad.getConstitution() + 600);
							ticks++;
							if(ticks >= 5) {
								nomad.forceChat("Zamorak, I am in your favor.");
								nomad.getMovementQueue().setLockMovement(false);
								nomad.setChargingAttack(false);
								stop();
							}
						}
					});
				}
			} else if (randomNomad >= 23 && randomNomad <= 29) {
				nomad.setChargingAttack(true);
				nomad.getMovementQueue().setLockMovement(true);
				TaskManager.submit(new Task(1, nomad, false) {
					int ticks = 0;
					@Override
					public void execute() {
						if (ticks == 0 || ticks == 4) {
							nomad.performGraphic(gfx2);
							nomad.performAnimation(anim3);
						}
						if(ticks == 7)
							nomad.forceChat("Almost.. Almost there..");
						if(ticks == 9 || ticks == 11 || ticks == 13) {
							nomad.forceChat("Die!");
							nomad.performAnimation(new Animation(12697));
							nomad.performGraphic(new Graphic(65565));
						}
						if (ticks == 10 || ticks == 12 || ticks == 14) {
							nomad.performAnimation(new Animation(12697));
							new Projectile(nomad, target, 2283, 44, 3, 43, 31, 0).sendProjectile();
							nomad.getCombatBuilder().setContainer(new CombatContainer(nomad, target, 1, 1, CombatType.MAGIC, true));
						} else if(ticks == 16) {
							nomad.getMovementQueue().setLockMovement(false);
							nomad.setChargingAttack(false);
							this.stop();
						}
						ticks++;
					}
				});

			} else if (randomNomad >= 16 && randomNomad <= 19) {
				nomad.setChargingAttack(true);
				nomad.getMovementQueue().reset().setLockMovement(true);
				TaskManager.submit(new Task(1, nomad, false) {
					int ticks = 0;
					@Override
					public void execute() {
						if (ticks == 0) {
							target.getMovementQueue().freeze(15);
							target.performGraphic(gfx3);
							nomad.forceChat("Freeze!");
							nomad.performAnimation(new Animation(12697));
							nomad.getCombatBuilder().setContainer(new CombatContainer(nomad, target, 1, CombatType.MAGIC, true));

						} else if(ticks == 1 || ticks == 4 || ticks == 5) {
							nomad.performGraphic(gfx2);
							nomad.performAnimation(anim3);
						}
						if(ticks == 5)
							nomad.forceChat("Zamorak, please! Allow me to me channel your power!");
						if(ticks == 10)
							nomad.forceChat("Adventurer, prepare to be blown away!");
						if(ticks == 18)
							nomad.forceChat("I call upon you, Zamorak!");
						if(ticks == 20)
							nomad.performAnimation(new Animation(12697));
						if(ticks == 23) 
							new Projectile(nomad, target, 2001, 44, 3, 43, 31, 0).sendProjectile();
						if(ticks == 24)
							target.performGraphic(new Graphic(2004));
						if (ticks == 25) {
							nomad.getCombatBuilder().setContainer(new CombatContainer(nomad, target, 1, 1, CombatType.MAGIC, false));
							target.getMovementQueue().freeze(0);
							nomad.getMovementQueue().setLockMovement(false);
							nomad.setChargingAttack(false);
							stop();
						}
						ticks++;
					}
				});
			} else {
				if(meleeDistance) {
					nomad.performAnimation(anim2);
					nomad.forceChat("You shall fall!");
					nomad.getCombatBuilder().setContainer(new CombatContainer(nomad, target, 1, 1, CombatType.MELEE, false));
				} else {
					target.getMovementQueue().freeze(15);
					target.performGraphic(gfx3);
					nomad.forceChat("Freeze!");
					nomad.performAnimation(new Animation(12697));
					nomad.getCombatBuilder().setContainer(new CombatContainer(nomad, target, 1, 1, CombatType.MAGIC, true));
				}
			}
		}
		return true;
	}


	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 8;
	}
	
	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}

	private static final Animation anim2 = new Animation(12696);
	private static final Animation anim3 = new Animation(12698);
	private static final Graphic gfx2 = new Graphic(2281, GraphicHeight.LOW);
	private static final Graphic gfx3 = new Graphic(369, GraphicHeight.LOW);
}
