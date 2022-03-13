package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Skill;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.HitQueue.CombatHit;
import com.ruse.world.content.combat.magic.CombatSpells;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class Venenatis implements CombatStrategy {

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
		NPC venenatis = (NPC)entity;
		if(venenatis.isChargingAttack() || victim.getConstitution() <= 0) {
			return true;
		}
		venenatis.setChargingAttack(true);
		venenatis.performAnimation(new Animation(venenatis.getDefinition().getAttackAnimation()));
		venenatis.getCombatBuilder().setContainer(new CombatContainer(venenatis, victim, 1, 1, CombatType.MELEE, true));
		TaskManager.submit(new Task(1, venenatis, false) {
			int tick = 0;
			@Override
			public void execute() {
				if(tick == 0) {
					final int random = Misc.getRandom(15);
					if(random <= 12) {
						venenatis.prepareSpell(CombatSpells.EARTH_WAVE.getSpell(), victim);
					} else if(random == 13) {
						venenatis.prepareSpell(CombatSpells.ENFEEBLE.getSpell(), victim);
					} else if(random == 14) {
						venenatis.prepareSpell(CombatSpells.CONFUSE.getSpell(), victim);
					} else if(random == 15) {
						venenatis.prepareSpell(CombatSpells.STUN.getSpell(), victim);
					}
				} else if(tick == 3) {
					new CombatHit(venenatis.getCombatBuilder(), new CombatContainer(venenatis, victim, 1, CombatType.MAGIC, true)).handleAttack();
					if(Misc.getRandom(10) <= 2) {
						Player p = (Player)victim;
						int lvl = p.getSkillManager().getCurrentLevel(Skill.PRAYER);
						lvl *= 0.9;
						p.getSkillManager().setCurrentLevel(Skill.PRAYER, p.getSkillManager().getCurrentLevel(Skill.PRAYER) - lvl <= 0 ?  1 : lvl);
						p.getPacketSender().sendMessage("Venenatis has reduced your Prayer level.");
					}
					venenatis.setChargingAttack(false);
					stop();
				}
				tick++;
			}
		});
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 3;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}
