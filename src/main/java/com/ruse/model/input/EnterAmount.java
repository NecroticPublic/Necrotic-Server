package com.ruse.model.input;


/**
 * Handles entering amounts
 * @author Gabriel Hannason
 */
public abstract class EnterAmount extends Input {
	
	private int item, slot;
	
	public int getItem() {
		return item;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public EnterAmount() {}
	
	public EnterAmount(int item) {
		this.item = item;
	}
	
	public EnterAmount(int item, int slot) {
		this.item = item;
		this.slot = slot;
	}
	
}
