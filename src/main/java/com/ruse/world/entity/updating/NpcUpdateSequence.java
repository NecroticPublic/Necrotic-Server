package com.ruse.world.entity.updating;

import com.ruse.world.World;
import com.ruse.world.entity.impl.npc.NPC;

/**
 * A {@link WorldUpdateSequence} implementation for {@link Npc}s that provides
 * code for each of the updating stages. The actual updating stage is not
 * supported by this implementation because npc's are updated for players.
 *
 * @author lare96
 */
public class NpcUpdateSequence implements UpdateSequence<NPC> {
	
	@Override
	public void executePreUpdate(NPC t) {
		try {
			t.getMovementCoordinator().sequence();
			t.getMovementQueue().sequence();
			t.sequence();
		} catch (Exception e) {
			e.printStackTrace();
			World.deregister(t);
		}
	}
	@Override
	public void executeUpdate(NPC t) {
		throw new UnsupportedOperationException(
				"NPCs cannot be updated for NPCs!");
	}
	
	@Override
	public void executePostUpdate(NPC t) {
		try {
			NPCUpdating.resetFlags(t);
		} catch (Exception e) {
			e.printStackTrace();
			World.deregister(t);
		}
	}
}