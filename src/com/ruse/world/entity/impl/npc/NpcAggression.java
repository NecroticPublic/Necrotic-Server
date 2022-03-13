package com.ruse.world.entity.impl.npc;

import com.ruse.model.Locations;
import com.ruse.model.Locations.Location;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.strategy.impl.Nex;
import com.ruse.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.ruse.world.entity.impl.player.Player;

/**
 * Handles the behavior of aggressive {@link Npc}s around players within the
 * <code>NPC_TARGET_DISTANCE</code> radius.
 * 
 * @author lare96
 */
public final class NpcAggression {

	/**
	 * Time that has to be spent in a region before npcs stop acting aggressive
	 * toward a specific player.
	 */
	public static final int NPC_TOLERANCE_SECONDS = 300; //5 mins

	public static void target(Player player) {

		if(player.isPlayerLocked())
			return;
		
		final boolean dung = Dungeoneering.doingDungeoneering(player);

		// Loop through all of the aggressive npcs.
		for (NPC npc : player.getLocalNpcs()) {

			if(npc == null || npc.getConstitution() <= 0)
				continue;
			
			NPCFacing.updateFacing(player, npc);
			
			if(!(dung && npc.getId() != 11226) && !npc.getDefinition().isAggressive()) {
				continue;
			}
			
			if(npc.getSpawnedFor() != null && npc.getSpawnedFor() != player)
				continue;
			
			if(!npc.findNewTarget()) {
				if(npc.getCombatBuilder().isAttacking() || npc.getCombatBuilder().isBeingAttacked()) {
					continue;
				}
			}

			/** GWD **/
			boolean gwdMob = Nex.nexMob(npc.getId()) || npc.getId() == 6260 || npc.getId() == 6261 || npc.getId() == 6263 || npc.getId() == 6265 || npc.getId() == 6222 || npc.getId() == 6223 || npc.getId() == 6225 || npc.getId() == 6227 || npc.getId() == 6203 || npc.getId() == 6208 || npc.getId() == 6204 || npc.getId() == 6206 || npc.getId() == 6247 || npc.getId() == 6248 || npc.getId() == 6250 || npc.getId() == 6252;
			if(gwdMob) {
				if(!player.getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()) {
					continue;
				}
			}

			// Check if the entity is within distance.
			if (Locations.goodDistance(npc.getPosition(), player.getPosition(), npc.getAggressionDistance()) || gwdMob) {
		
				if (player.getTolerance().elapsed() > (NPC_TOLERANCE_SECONDS * 1000) && player.getLocation() != Location.GODWARS_DUNGEON && player.getLocation() != Location.DAGANNOTH_DUNGEON && !dung) {
					break;
				}

				boolean multi = Location.inMulti(player);

				if(player.isTargeted()) {
					if(!player.getCombatBuilder().isBeingAttacked()) {
						player.setTargeted(false);
					} else if(!multi) {
						break;
					}
				}


				if (player.getSkillManager().getCombatLevel() > (npc.getDefinition().getCombatLevel() * 2) && player.getLocation() != Location.WILDERNESS && !dung) {
					continue;
				}

				if(Location.ignoreFollowDistance(npc) || gwdMob || npc.getDefaultPosition().getDistance(player.getPosition()) < 7 + npc.getMovementCoordinator().getCoordinator().getRadius() || dung) {
					if(CombatFactory.checkHook(npc, player)) {
						player.setTargeted(true);
						npc.getCombatBuilder().attack(player);
						npc.setFindNewTarget(false);
						break;
					}
				}
			}
		}
	}

}
