package com.ruse.world.entity.impl.npc;

import com.ruse.model.Position;
import com.ruse.model.Locations.Location;
import com.ruse.model.movement.PathFinder;
import com.ruse.util.Misc;
import com.ruse.world.clip.region.RegionClipping;

/**
 * Will make all {@link NPC}s set to coordinate, pseudo-randomly move within a
 * specified radius of their original position.
 * 
 * @author lare96
 */
public class NPCMovementCoordinator {

	/** The npc we are coordinating movement for. */
	private NPC npc;

	/** The coordinate state this npc is in. */
	private CoordinateState coordinateState;

	/** The coordinator for coordinating movement. */
	private Coordinator coordinator;

	public enum CoordinateState {
		HOME,
		AWAY
	}

	public NPCMovementCoordinator(NPC npc) {
		this.npc = npc;
		this.coordinator = new Coordinator();
		this.coordinateState = CoordinateState.HOME;
	}

	public void sequence() {
		if (coordinateState == CoordinateState.HOME && !coordinator.isCoordinate()) {
			return;
		}
		updateCoordinator();
		switch (coordinateState) {
		case HOME:
			
			if (npc.getCombatBuilder().isBeingAttacked() || npc.getCombatBuilder().isAttacking())
				return;
			
			if (npc.getMovementQueue().isMovementDone()) {
				if (Misc.getRandom(10) <= 1) {
					Position pos = generateLocalPosition();
					if(pos != null) {
						npc.getMovementQueue().walkStep(pos.getX(), pos.getY());
					}
				}
			}
			
			break;
		case AWAY:
			npc.getCombatBuilder().reset(true);
			PathFinder.findPath(npc, npc.getDefaultPosition().getX(), npc.getDefaultPosition().getY(), true, 1, 1);
			break;
		}
	}

	public void updateCoordinator() {
		int deltaX = npc.getPosition().getX() - npc.getDefaultPosition().getX();
		int deltaY = npc.getPosition().getY() - npc.getDefaultPosition().getY();
		
		if((deltaX > coordinator.getRadius()) || (deltaY > coordinator.getRadius())) {
			if(Location.ignoreFollowDistance(npc) || npc.getMovementQueue().getFollowCharacter() != null || npc.getCombatBuilder().isAttacking() || npc.getCombatBuilder().isBeingAttacked()) {
				return;
			}
			coordinateState = CoordinateState.AWAY;
		} else {
			coordinateState = CoordinateState.HOME;
		}
	}

	private Position generateLocalPosition() {
		int dir = -1;
		int x = 0, y = 0;
		if (!RegionClipping.blockedNorth(npc.getPosition()))
		{
			dir = 0;
		}
		else if (!RegionClipping.blockedEast(npc.getPosition()))
		{
			dir = 4;
		}
		else if (!RegionClipping.blockedSouth(npc.getPosition()))
		{
			dir = 8;
		}
		else if (!RegionClipping.blockedWest(npc.getPosition()))
		{
			dir = 12;
		}
		int random = Misc.getRandom(3);

		boolean found = false;

		if (random == 0)
		{
			if (!RegionClipping.blockedNorth(npc.getPosition()))
			{
				y = 1;
				found = true;
			}
		}
		else if (random == 1)
		{
			if (!RegionClipping.blockedEast(npc.getPosition()))
			{
				x = 1;
				found = true;
			}
		}
		else if (random == 2)
		{
			if (!RegionClipping.blockedSouth(npc.getPosition()))
			{
				y = -1;
				found = true;
			}
		}
		else if (random == 3)
		{
			if (!RegionClipping.blockedWest(npc.getPosition()))
			{
				x = -1;
				found = true;
			}
		}
		if (!found)
		{
			if (dir == 0)
			{
				y = 1;
			}
			else if (dir == 4)
			{
				x = 1;
			}
			else if (dir == 8)
			{
				y = -1;
			}
			else if (dir == 12)
			{
				x = -1;
			}
		}
		if(x == 0 && y == 0)
			return null;
		int spawnX = npc.getDefaultPosition().getX();
		int spawnY = npc.getDefaultPosition().getY();
		if (x == 1) {
			if (npc.getPosition().getX() + x > spawnX + 1)
				return null;
		}
		if (x == -1) {
			if (npc.getPosition().getX() - x < spawnX - 1)
				return null;
		}
		if (y == 1) {
			if (npc.getPosition().getY() + y > spawnY + 1)
				return null;
		}
		if (y == -1) {
			if (npc.getPosition().getY() - y < spawnY - 1)
				return null;
		}
		return new Position(x, y);
	}

	public Coordinator getCoordinator() {
		return this.coordinator;
	}

	public void setCoordinator(Coordinator coordinator) {
		this.coordinator = coordinator;
	}

	public void setCoordinateState(CoordinateState coordinateState) {
		this.coordinateState = coordinateState;
	}

	public CoordinateState getCoordinateState() {
		return coordinateState;
	}

	public static class Coordinator {

		public Coordinator(boolean coordinate, int radius) {
			this.coordinate = coordinate;
			this.radius = radius;
		}
		
		public Coordinator() {
			
		}
		
		private boolean coordinate;
		private int radius;

		public boolean isCoordinate() {
			return coordinate;
		}

		public Coordinator setCoordinate(boolean coordinate) {
			this.coordinate = coordinate;
			return this;
		}

		public int getRadius() {
			return radius;
		}

		public Coordinator setRadius(int radius) {
			this.radius = radius;
			return this;
		}
	}
}