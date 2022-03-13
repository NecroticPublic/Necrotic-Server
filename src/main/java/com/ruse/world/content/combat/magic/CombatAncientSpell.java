package com.ruse.world.content.combat.magic;

import java.util.Iterator;
import java.util.Optional;

import com.ruse.model.CombatIcon;
import com.ruse.model.Hit;
import com.ruse.model.Hitmask;
import com.ruse.model.Item;
import com.ruse.model.Locations;
import com.ruse.model.Locations.Location;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * A {@link CombatSpell} implementation that is primarily used for spells that
 * are a part of the ancients spellbook.
 * 
 * @author lare96
 */
public abstract class CombatAncientSpell extends CombatSpell {

    @Override
    public void finishCast(Character cast, Character castOn, boolean accurate,
        int damage) {

        // The spell wasn't accurate, so do nothing.
        if (!accurate || damage <= 0) {
            return;
        }

        // Do the spell effect here.
        spellEffect(cast, castOn, damage);

        // The spell doesn't support multiple targets or we aren't in a
        // multicombat zone, so do nothing.
        if (spellRadius() == 0 || !Locations.Location.inMulti(castOn)) {
            return;
        }

        // We passed the checks, so now we do multiple target stuff.
        Iterator<? extends Character> it = null;
        if (cast.isPlayer() && castOn.isPlayer()) {
            it = ((Player) cast).getLocalPlayers().iterator();
        } else if (cast.isPlayer() && castOn.isNpc()) {
            it = ((Player) cast).getLocalNpcs().iterator();
        } else if (cast.isNpc() && castOn.isNpc()) {
            it = World.getNpcs().iterator();
        } else if (cast.isNpc() && castOn.isPlayer()) {
            it = World.getPlayers().iterator();
        }

        for (Iterator<? extends Character> $it = it; $it.hasNext();) {
            Character next = $it.next();

            if (next == null) {
                continue;
            }
            
            if(next.isNpc()) {
            	NPC n = (NPC)next;
            	if(!n.getDefinition().isAttackable() || n.isSummoningNpc()) {
            		continue;
            	}
            } else {
            	Player p = (Player)next;
            	if(p.getLocation() != Location.WILDERNESS || !Location.inMulti(p)) {
            		continue;
            	}
            }
            

            if (next.getPosition().isWithinDistance(castOn.getPosition(),
                spellRadius()) && !next.equals(cast) && !next.equals(castOn) && next.getConstitution() > 0 && next.getConstitution() > 0) {
                cast.getCurrentlyCasting().endGraphic().ifPresent(next::performGraphic);
                int calc = Misc.inclusiveRandom(0, maximumHit());
                next.dealDamage(new Hit(calc, Hitmask.RED, CombatIcon.MAGIC));
                next.getCombatBuilder().addDamage(cast, calc);
                spellEffect(cast, next, calc);
            }
        }
    }

    @Override
    public Optional<Item[]> equipmentRequired(Player player) {

        // Ancient spells never require any equipment, although the method can
        // still be overridden if by some chance a spell does.
        return Optional.empty();
    }

    /**
     * The effect this spell has on the target.
     * 
     * @param cast
     *            the entity casting this spell.
     * @param castOn
     *            the person being hit by this spell.
     * @param damage
     *            the damage inflicted.
     */
    public abstract void spellEffect(Character cast, Character castOn, int damage);

    /**
     * The radius of this spell, only comes in effect when the victim is hit in
     * a multicombat area.
     * 
     * @return how far from the target this spell can hit when targeting
     *         multiple entities.
     */
    public abstract int spellRadius();
}
