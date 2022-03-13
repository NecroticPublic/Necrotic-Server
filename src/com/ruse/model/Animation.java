package com.ruse.model;

/**
 * This file manages an entity's animation which should be performed.
 * 
 * @author relex lawl
 * 
 */

public class Animation {

	/**
	 * Animation constructor for entity to perform.
	 * @param id		The id of the animation entity should perform.
	 * @param delay		The delay which to wait before entity performs animation.
	 */
	public Animation(int id, int delay) {
		this.id = id;
		this.delay = delay;
	}
	
	/**
	 * Animation constructor for entity to perform.
	 * @param id	The id of the animation entity should perform.
	 */
	public Animation(int id) {
		this.id = id;
		this.delay = 0;
	}
	
	/**
	 * The animation's id.
	 */
	private int id;
	
	/**
	 * Gets the animation's id.
	 * @return	id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the animation's id.
	 * @param id	Id to set animation's id to.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * The delay in which to perform the animation.
	 */
	private int delay;
	
	/**
	 * Gets the animation's performance delay.
	 * @return	delay.
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * Sets the animation's performance delay.
	 * @param delay		Value to set delay to.
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}
}
