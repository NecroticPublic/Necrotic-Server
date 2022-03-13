package com.ruse.world.content.skill.impl.hunter;

import com.ruse.model.GameObject;
import com.ruse.world.entity.impl.player.Player;

/**
 * 
 * @author Rene
 */
public class Trap {

	/**
	 * The possible states a trap can be in
	 */
	public static enum TrapState {

		SET, CAUGHT;
	}

	/**
	 * The WorldObject linked to this HunterObject
	 */
	private GameObject gameObject;

	/**
	 * The amount of ticks this object should stay for
	 */
	private int ticks;
	/**
	 * This trap's state
	 */
	private TrapState trapState;

	/**
	 * Reconstructs a new Trap
	 * 
	 * @param object
	 * @param state
	 */
	public Trap(GameObject object, TrapState state, int ticks, Player owner) {
		gameObject = object;
		trapState = state;
		this.ticks = ticks;
		this.player = owner;
	}

	/**
	 * Gets the GameObject
	 */
	public GameObject getGameObject() {
		return gameObject;
	}

	/**
	 * @return the ticks
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * Gets a trap's state
	 */
	public TrapState getTrapState() {
		return trapState;
	}

	/**
	 * Sets the GameObject
	 * 
	 * @param gameObject
	 */
	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}

	/**
	 * @param ticks
	 *            the ticks to set
	 */
	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	/**
	 * Sets a trap's state
	 * 
	 * @param state
	 */
	public void setTrapState(TrapState state) {
		trapState = state;
	}
	
	private Player player;
	
	public Player getOwner() {
		return player;
	}
	
	public void setOwner(Player player) {
		this.player = player;
	}
}
