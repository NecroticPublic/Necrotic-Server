package com.ruse.world.content.combat.strategy.impl;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.model.Projectile;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;

public class Brandon implements CombatStrategy {

	public static NPC Brandon;
	private static final Animation attack_anim1 = new Animation(426); 
	private static final Animation attack_anim2 = new Animation(4973);
	private static final Animation attack_anim3 = new Animation(1978);
	private static final Graphic graphic1 = new Graphic(1114);
	private static final Graphic graphic2 = new Graphic(1014);
	private static final Graphic graphic3 = new Graphic(2146);
	private static final Graphic projectile1 = new Graphic(1120);


	public static void spawn() {


		Brandon = new NPC(199, new Position(3023, 3735));
	}

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
		NPC Brandon = (NPC)entity;
		if(Brandon.isChargingAttack()) {
			return true;
		}
		int random = Misc.getRandom(10);
		if(random <= 8 && Locations.goodDistance(Brandon.getPosition().getX(), Brandon.getPosition().getY(), victim.getPosition().getX(), victim.getPosition().getY(), 3)) {
			Brandon.performAnimation(attack_anim1);
			Brandon.getCombatBuilder().setContainer(new CombatContainer(Brandon, victim, 1, CombatType.MELEE, true));
			new Projectile(Brandon, victim, projectile1.getId(), 44, 3, 43, 31, 0).sendProjectile();
		} else if(random <= 4 || !Locations.goodDistance(Brandon.getPosition().getX(), Brandon.getPosition().getY(), victim.getPosition().getX(), victim.getPosition().getY(), 8)) {
			Brandon.getCombatBuilder().setContainer(new CombatContainer(Brandon, victim, 1, 3, CombatType.MAGIC, true));
			Brandon.performAnimation(attack_anim3);
			Brandon.setChargingAttack(true);
			TaskManager.submit(new Task(2, Brandon, false) {
				int tick = 0;
				@Override
				public void execute() {
					switch(tick) {
					case 1:
						victim.performGraphic(graphic3);
						Brandon.setChargingAttack(false);
						stop();
						break;
					}
					tick++;
				}
			});
		} else {
			Brandon.getCombatBuilder().setContainer(new CombatContainer(Brandon, victim, 1, CombatType.RANGED, true));
			Brandon.performAnimation(attack_anim2);
			Brandon.setChargingAttack(true);
			TaskManager.submit(new Task(2, Brandon, false) {
				@Override
				public void execute() {
					victim.performGraphic(graphic2);
					Brandon.setChargingAttack(false);
					stop();
				}
			});
		}
		return true;
	}
	
	
	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 20;
	}

	
	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}

