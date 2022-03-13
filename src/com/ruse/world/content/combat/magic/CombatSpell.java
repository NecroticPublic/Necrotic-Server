package com.ruse.world.content.combat.magic;

import java.util.Optional;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Projectile;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;

/**
 * A {@link Spell} implementation used for combat related spells.
 * 
 * @author lare96
 */
public abstract class CombatSpell extends Spell {

	@Override
	public void startCast(Character cast, Character castOn) {

		int castAnimation = -1;

		NPC npc = cast.isNpc() ? ((NPC)cast) : null;
		if(npc != null) {
			if(npc.getId() == 3496 || npc.getId() == 6278 || npc.getId() == 2000 || npc.getId() == 109 || npc.getId() == 3580 || npc.getId() == 2007) {
				castAnimation = npc.getDefinition().getAttackAnimation();
			}
		}

		if(castAnimation().isPresent() && castAnimation == -1) {
			castAnimation().ifPresent(cast::performAnimation);
		} else {
			cast.performAnimation(new Animation(castAnimation));
		}

		// Then send the starting graphic.
		if(npc != null) {
			if(npc.getId() != 2000 && npc.getId() != 109 && npc.getId() != 3580 && npc.getId() != 2007) {
				startGraphic().ifPresent(cast::performGraphic);
			}
		} else {
			startGraphic().ifPresent(cast::performGraphic);
		}

		// Finally send the projectile after two ticks.
		castProjectile(cast, castOn).ifPresent(g -> {
			TaskManager.submit(new Task(2, cast.getCombatBuilder(), false) {
				@Override
				public void execute() {
					g.sendProjectile();
					this.stop();
				}
			});
		});
	}

	/**
	 * The fixed ID of the spell implementation as recognized by the protocol.
	 * 
	 * @return the ID of the spell, or <tt>-1</tt> if there is no ID for this
	 *         spell.
	 */
	public abstract int spellId();

	/**
	 * The maximum hit an {@link Character} can deal with this spell.
	 * 
	 * @return the maximum hit able to be dealt with this spell implementation.
	 */
	public abstract int maximumHit();

	/**
	 * The animation played when the spell is cast.
	 * 
	 * @return the animation played when the spell is cast.
	 */
	public abstract Optional<Animation> castAnimation();

	/**
	 * The starting graphic played when the spell is cast.
	 * 
	 * @return the starting graphic played when the spell is cast.
	 */
	public abstract Optional<Graphic> startGraphic();

	/**
	 * The projectile played when this spell is cast.
	 * 
	 * @param cast
	 *            the entity casting the spell.
	 * @param castOn
	 *            the entity targeted by the spell.
	 * 
	 * @return the projectile played when this spell is cast.
	 */
	public abstract Optional<Projectile> castProjectile(Character cast,
			Character castOn);

	/**
	 * The ending graphic played when the spell hits the victim.
	 * 
	 * @return the ending graphic played when the spell hits the victim.
	 */
	public abstract Optional<Graphic> endGraphic();

	/**
	 * Fired when the spell hits the victim.
	 * 
	 * @param cast
	 *            the entity casting the spell.
	 * @param castOn
	 *            the entity targeted by the spell.
	 * @param accurate
	 *            if the spell was accurate.
	 * @param damage
	 *            the amount of damage inflicted by this spell.
	 */
	public abstract void finishCast(Character cast, Character castOn,
			boolean accurate, int damage);
}