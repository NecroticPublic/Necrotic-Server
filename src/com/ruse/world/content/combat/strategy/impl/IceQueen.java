package com.ruse.world.content.combat.strategy.impl;

import com.ruse.model.definitions.NpcDefinition;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;

public class IceQueen implements CombatStrategy {
	
	public static NpcDefinition npcdef = NpcDefinition.forId(795);

	@Override
	public boolean canAttack(Character entity, Character victim) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int attackDelay(Character entity) {
		return npcdef.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return npcdef.getSize();
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}

}
