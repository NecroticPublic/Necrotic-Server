package com.ruse.world.content.skill.impl.hunter;

/**
 * 
 * @author Faris
 */
public enum Bait {
	/**
	 * Standard bait type for box traps, will increase the catch rate of
	 * Chinchompa hunter NPCs
	 */
	SPICY_MINCED_MEAT(9996),

	/**
	 * Standard bait type for snare traps, will increase the catch rate of bird
	 * type hunter NPCs
	 */
	TOMATO(1982);

	private int baitId;

	Bait(int baitId) {
		this.baitId = baitId;
	}

	public int getBaitId() {
		return baitId;
	}

}
