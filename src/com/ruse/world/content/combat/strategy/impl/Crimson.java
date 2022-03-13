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

public class Crimson implements CombatStrategy {

	public static NPC Crimson;
	private static final Animation attack_anim1 = new Animation(401); 
	private static final Animation attack_anim2 = new Animation(2555);
	private static final Animation attack_anim3 = new Animation(10546);
	private static final Graphic graphic1 = new Graphic(1154);
	private static final Graphic graphic2 = new Graphic(1166);
	private static final Graphic graphic3 = new Graphic(1333);
	private static final Graphic StormGFX = new Graphic(457);


	public static void spawn() {


		Crimson = new NPC(200, new Position(3023, 3735));
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
		NPC Crimson = (NPC)entity;
		if(Crimson.isChargingAttack()) {
			return true;
		}
		int random = Misc.getRandom(10);
		if(random <= 8 && Locations.goodDistance(Crimson.getPosition().getX(), Crimson.getPosition().getY(), victim.getPosition().getX(), victim.getPosition().getY(), 3)) {
			Crimson.performAnimation(attack_anim1);
			Crimson.getCombatBuilder().setContainer(new CombatContainer(Crimson, victim, 1, CombatType.MELEE, true));
		} else if(random <= 4 || !Locations.goodDistance(Crimson.getPosition().getX(), Crimson.getPosition().getY(), victim.getPosition().getX(), victim.getPosition().getY(), 8)) {
			Crimson.getCombatBuilder().setContainer(new CombatContainer(Crimson, victim, 1, 3, CombatType.MAGIC, true));
			Crimson.performAnimation(attack_anim3);
			Crimson.performGraphic(StormGFX);
			Crimson.setChargingAttack(true);
			Crimson.forceChat("I've banned people for less.");	
			TaskManager.submit(new Task(2, Crimson, false) {
				int tick = 0;
				@Override
				public void execute() {
					switch(tick) {
					case 1:
						new Projectile(Crimson, victim, graphic3.getId(), 44, 0, 0, 0, 0).sendProjectile();
						Crimson.setChargingAttack(false);
						stop();
						break;
					}
					tick++;
				}
			});
		} else {
			Crimson.getCombatBuilder().setContainer(new CombatContainer(Crimson, victim, 1, CombatType.RANGED, true));
			Crimson.performAnimation(attack_anim2);
			new Projectile(Crimson, victim, graphic2.getId(), 44, 0, 0, 0, 0).sendProjectile();
			Crimson.setChargingAttack(true);
			TaskManager.submit(new Task(2, Crimson, false) {
				@Override
				public void execute() {
					victim.performGraphic(graphic1);
					Crimson.setChargingAttack(false);
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

