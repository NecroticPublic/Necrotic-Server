package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.HitQueue.CombatHit;
import com.ruse.world.content.combat.magic.CombatSpells;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;

public class Scorpia implements CombatStrategy {

	private static int babiesKilled = 2;

	public static boolean attackable() {
		return babiesKilled == 2;
	}

	public static void killedBaby() {
		babiesKilled++;
	}

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return true;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		NPC npc = (NPC)entity;
		npc.performAnimation(new Animation(npc.getDefinition().getAttackAnimation()));

		if(npc.getConstitution() <= 500 && !npc.hasHealed()) {
			NPC[] babies = new NPC[]{new NPC(109, new Position(2854,9642)), new NPC(109, new Position(2854,9631))};
			for(NPC n : babies) {
				World.register(n);
				n.getCombatBuilder().attack(victim);
				npc.heal(990);
			}
			babiesKilled = 0;
			npc.setHealed(true);
		} else if(npc.hasHealed() && babiesKilled > 0) {
			if(Misc.getRandom(3) == 1) {
				npc.forceChat("You will regret hurting them..");
			}
			TaskManager.submit(new Task(1, npc, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick == 0) {
						npc.prepareSpell(CombatSpells.BABY_SCORPION.getSpell(), victim);
					} else if(tick == 3) {
						new CombatHit(npc.getCombatBuilder(), new CombatContainer(npc, victim, 1, CombatType.MAGIC, true)).handleAttack();
						stop();
					}
					tick++;
				}
			});
		}

		return new CombatContainer(npc, victim, 1, 1, CombatType.MELEE, true);
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		return false;
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
