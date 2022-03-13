package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Locations;
import com.ruse.model.Projectile;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;

public class Aviansie implements CombatStrategy {

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return true;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		NPC aviansie = (NPC)entity;
		if(aviansie.isChargingAttack() || victim.getConstitution() <= 0) {
			return true;
		}
		if(Locations.goodDistance(aviansie.getPosition().copy(), victim.getPosition().copy(), 1) && Misc.getRandom(5) <= 3) {
			aviansie.performAnimation(new Animation(aviansie.getDefinition().getAttackAnimation()));
			aviansie.getCombatBuilder().setContainer(new CombatContainer(aviansie, victim, 1, 1, CombatType.MELEE, true));
		} else {
			aviansie.setChargingAttack(true);
			aviansie.performAnimation(new Animation(aviansie.getDefinition().getAttackAnimation()));
			aviansie.getCombatBuilder().setContainer(new CombatContainer(aviansie, victim, 1, 3, aviansie.getId() == 6231 ? CombatType.MAGIC : CombatType.RANGED, true));
			TaskManager.submit(new Task(1, aviansie, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick == 0) {
						new Projectile(aviansie, victim, getGfx(aviansie.getId()), 44, 3, 43, 43, 0).sendProjectile();
					} else if(tick == 1) {
						aviansie.setChargingAttack(false);
						stop();
					}
					tick++;
				}
			});
		}
		return true;
	}

	public static int getGfx(int npc) {
		switch(npc) {
		case 6230:
			return 1837;
		case 6231:
			return 2729;
		}
		return 37;
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 4;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}
