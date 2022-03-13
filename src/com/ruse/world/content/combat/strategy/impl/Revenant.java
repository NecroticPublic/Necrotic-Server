package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Locations;
import com.ruse.model.Projectile;
import com.ruse.model.Locations.Location;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;

public class Revenant implements CombatStrategy {

	enum REVENANT_DATA {

		REVENANT_IMP(13465, new Animation(7500), new Animation(7501)),
		REVENANT_GOBLIN(13469, new Animation(7499), new Animation(7513)),
		REVENANT_WEREWOLF(13474, new Animation(7496), new Animation(7521)),
		REVENANT_ORK(13478, new Animation(7505), new Animation(7518)),
		REVENANT_DARK_BEAST(13479, new Animation(7502), new Animation(7514));

		REVENANT_DATA(int npc, Animation magicAttack, Animation rangedAttack) {
			this.npc = npc;
			this.magicAttack = magicAttack;
			this.rangedAttack = rangedAttack;
		}

		private int npc;
		public Animation magicAttack, rangedAttack;

		public static REVENANT_DATA getData(int npc) {
			for(REVENANT_DATA data : REVENANT_DATA.values()) {
				if(data != null && data.npc == npc) {
					return data;
				}
			}
			return null;
		}
	}

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return victim.getLocation() == Location.WILDERNESS;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		NPC revenant = (NPC)entity;
		if(revenant.isChargingAttack() || victim.getConstitution() <= 0) {
			return true;
		}
		final CombatType attkType = Misc.getRandom(5) <= 2 && Locations.goodDistance(revenant.getPosition(), revenant.getPosition(), 2) ? CombatType.MELEE : Misc.getRandom(10) <= 5 ? CombatType.MAGIC : CombatType.RANGED;
		switch(attkType) {
		case MELEE:
			revenant.performAnimation(new Animation(revenant.getDefinition().getAttackAnimation()));
			revenant.getCombatBuilder().setContainer(new CombatContainer(revenant, victim, 1, 1, CombatType.MELEE, true));
			break;
		case MAGIC:
		case RANGED:
			final REVENANT_DATA revData = REVENANT_DATA.getData(revenant.getId());
			revenant.setChargingAttack(true);
			revenant.performAnimation(attkType == CombatType.MAGIC ? revData.magicAttack : revData.rangedAttack);
			revenant.getCombatBuilder().setContainer(new CombatContainer(revenant, victim, 1, 2, attkType, true));
			TaskManager.submit(new Task(1, revenant, false) {
				int tick = 0;
				@Override
				public void execute() {
					switch(tick) {
					case 1:
						new Projectile(revenant, victim, (attkType == CombatType.RANGED ? 970 : 280), 44, 3, 43, 43, 0).sendProjectile();
						break;
					case 3:
						revenant.setChargingAttack(false);
						stop();
						break;
					}
					tick++;
				}
			});
			break;
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
}
