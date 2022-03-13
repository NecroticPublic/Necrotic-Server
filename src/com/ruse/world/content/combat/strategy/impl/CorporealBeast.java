package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Locations;
import com.ruse.model.Projectile;
import com.ruse.model.Skill;
import com.ruse.model.Locations.Location;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.HitQueue.CombatHit;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class CorporealBeast implements CombatStrategy {

	private static final Animation attack_anim = new Animation(10496);
	private static final Animation attack_anim2 = new Animation(10410);
	private static final Graphic attack_graphic = new Graphic(1834);

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return victim.isPlayer();
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		NPC cB = (NPC)entity;
		if(cB.isChargingAttack() || cB.getConstitution() <= 0) {
			return true;
		}
		Player target = (Player)victim;
		boolean stomp = false;
		for (Player t : Misc.getCombinedPlayerList(target)) {
			if(t == null || t.getLocation() != Location.CORPOREAL_BEAST)
				continue;
			if (Locations.goodDistance(t.getPosition(), cB.getPosition(), 1)) {
				stomp = true;
				cB.getCombatBuilder().setVictim(t);
				new CombatHit(cB.getCombatBuilder(), new CombatContainer(cB, t, 1, CombatType.MAGIC, true)).handleAttack();
			}
		}
		if (stomp) {
			cB.performAnimation(attack_anim);
			cB.performGraphic(attack_graphic);
		}

		int attackStyle = Misc.getRandom(4);
		if (attackStyle == 0 || attackStyle == 1) { // melee
			int distanceX = target.getPosition().getX() - cB.getPosition().getX();
			int distanceY = target.getPosition().getY() - cB.getPosition().getY();
			if (distanceX > 4 || distanceX < -1 || distanceY > 4 || distanceY < -1)
				attackStyle = 4;
			else {

				cB.performAnimation(new Animation(attackStyle == 0 ? 10057 : 10058));
				if(target.getLocation() == Location.CORPOREAL_BEAST)
					cB.getCombatBuilder().setContainer(new CombatContainer(cB, target, 1, 1, CombatType.MELEE, true));
				return true;
			}
		} else if (attackStyle == 2) { // powerfull mage spiky ball
			cB.performAnimation(attack_anim2);
			cB.getCombatBuilder().setContainer(new CombatContainer(cB, target, 1, 2, CombatType.MAGIC, true));
			new Projectile(cB, target, 1825, 44, 3, 43, 43, 0).sendProjectile();
		} else if (attackStyle == 3) { // translucent ball of energy
			cB.performAnimation(attack_anim2);
			if(target.getLocation() == Location.CORPOREAL_BEAST)
				cB.getCombatBuilder().setContainer(new CombatContainer(cB, target, 1, 2, CombatType.MAGIC, true));
			new Projectile(cB, target, 1823, 44, 3, 43, 43, 0).sendProjectile();
			TaskManager.submit(new Task(1, target, false) {
				@Override
				public void execute() {
					int skill = Misc.getRandom(4);
					Skill skillT = Skill.forId(skill);
					Player player = (Player) target;
					int lvl = player.getSkillManager().getCurrentLevel(skillT);
					lvl -= 1 + Misc.getRandom(4);
					player.getSkillManager().setCurrentLevel(skillT, player.getSkillManager().getCurrentLevel(skillT) - lvl <= 0 ?  1 : lvl);
					target.getPacketSender().sendMessage("Your " + skillT.getFormatName() +" has been slighly drained!");
					stop();
				}
			});
		}
		if(attackStyle == 4) {
			cB.performAnimation(attack_anim2);
			for (Player t : Misc.getCombinedPlayerList(target)) {
				if(t == null || t.getLocation() != Location.CORPOREAL_BEAST)
					continue;
				new Projectile(cB, target, 1824, 44, 3, 43, 43, 0).sendProjectile();
			}
			TaskManager.submit(new Task(1, target, false) {
				@Override
				public void execute() {
					for (Player t : Misc.getCombinedPlayerList(target)) {
						if(t == null || t.getLocation() != Location.CORPOREAL_BEAST)
							continue;
						cB.getCombatBuilder().setVictim(t);
						new CombatHit(cB.getCombatBuilder(), new CombatContainer(cB, t, 1, CombatType.RANGED, true)).handleAttack();
					}
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
		return 8;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}
