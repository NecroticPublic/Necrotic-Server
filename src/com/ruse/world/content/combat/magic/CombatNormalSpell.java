package com.ruse.world.content.combat.magic;

import com.ruse.world.entity.impl.Character;

/**
 * A {@link Spell} implementation primarily used for spells that have no effects
 * at all when they hit the player.
 * 
 * @author lare96
 */
public abstract class CombatNormalSpell extends CombatSpell {

    @Override
    public void finishCast(Character cast, Character castOn, boolean accurate,
        int damage) {}
}