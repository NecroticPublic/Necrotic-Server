package com.ruse.model;

/**
 * Represents a graphic an entity might perform.
 * 
 * @author relex lawl
 */

public class Graphic {
	
	/**
	 * The graphic constructor for a character to perform.
	 * @param id	The graphic's id.
	 */
	public Graphic(int id) {
		this.id = id;
		this.delay = 0;
		this.height = GraphicHeight.LOW;
	}
	
	/**
	 * The graphic constructor for a character to perform.
	 * @param id		The graphic's id.
	 * @param delay		The delay to wait until performing the graphic.
	 */
	public Graphic(int id, int delay) {
		this.id = id;
		this.delay = delay;
		this.height = GraphicHeight.LOW;
	}
	
	/**
	 * The graphic constructor for a character to perform.
	 * @param id	The graphic's id.
	 */
	public Graphic(int id, GraphicHeight height) {
		this.id = id;
		this.delay = 0;
		this.height = height;
	}
	
	/**
	 * The graphic constructor for a character to perform.
	 * @param id		The graphic's id.
	 * @param delay		The delay to wait until performing the graphic.
	 */
	public Graphic(int id, int delay, GraphicHeight height) {
		this.id = id;
		this.delay = delay;
		this.height = height;
	}
	
	/**
	 * The graphic's id.
	 */
	private final int id;
	
	/**
	 * The delay which the graphic must wait before being performed.
	 */
	private final int delay;
	
	/**
	 * The graphic's height level to display in.
	 */
	private final GraphicHeight height;
	
	/**
	 * Gets the graphic's id.
	 * @return	id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the graphic's wait delay.
	 * @return	delay.
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Gets the graphic's height level to be displayed in.
	 * @return	The height level.
	 */
	public GraphicHeight getHeight() {
		return height;
	}
}
