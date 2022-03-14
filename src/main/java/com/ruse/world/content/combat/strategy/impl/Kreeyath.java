package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Locations;
import com.ruse.model.Projectile;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class Kreeyath implements CombatStrategy {

	private static final Animation attack_anim = new Animation(69);
	private static final Graphic graphic1 = new Graphic(1212);
	private static final Graphic graphic2 = new Graphic(1213);
	
	@Override
	public boolean canAttack(Character entity, Character victim) {
		return victim.isPlayer() && ((Player)victim).getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom();
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		NPC kreeyath = (NPC)entity;
		if(victim.getConstitution() <= 0) {
			return true;
		}
		if(kreeyath.isChargingAttack()) {
			kreeyath.getCombatBuilder().setAttackTimer(4);
			return true;
		}
		if(Locations.goodDistance(kreeyath.getPosition().copy(), victim.getPosition().copy(), 1) && Misc.getRandom(5) <= 3) {
			kreeyath.performAnimation(new Animation(kreeyath.getDefinition().getAttackAnimation()));
			kreeyath.getCombatBuilder().setContainer(new CombatContainer(kreeyath, victim, 1, 1, CombatType.MELEE, true));
		} else {
			kreeyath.setChargingAttack(true);
			kreeyath.performAnimation(attack_anim);
			kreeyath.performGraphic(graphic1);
			kreeyath.getCombatBuilder().setContainer(new CombatContainer(kreeyath, victim, 1, 3, CombatType.MAGIC, true));
			TaskManager.submit(new Task(1, kreeyath, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick == 1) {
						new Projectile(kreeyath, victim, graphic2.getId(), 44, 3, 43, 43, 0).sendProjectile();
						kreeyath.setChargingAttack(false);
						stop();
						
					}
					tick++;
				}
			});
		}
		return true;
	}

	public static int getAnimation(int npc) {
		int anim = 12259;
		if(npc == 50)
			anim = 81;
		else if(npc == 5363 || npc == 1590 || npc == 1591 || npc == 1592)
			anim = 14246;
		else if(npc == 51)
			anim = 13152;
		return anim;
	}


	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 5;
	}
	
	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}
