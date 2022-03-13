package com.ruse.model;

/**
 * Represents a damage's combat icon.
 * 
 * @author relex lawl
 */

public enum CombatIcon {
	
	/*
	 * No combat icon will be drawn.
	 */
	BLOCK,
	
	/*
	 * A sword icon will be drawn next to the hit.
	 */
	MELEE,
	
	/*
	 * A bow icon will be drawn next to the hit.
	 */
	RANGED,
	
	/*
	 * A magic hat will be drawn next to the hit.
	 */
	MAGIC,
	
	/*
	 * An arrow-like object will be drawn next to the hit.
	 */
	DEFLECT,
	
	/*
	 * A cannon ball will be drawn next to the hit.
	 */
	CANNON,
	
	/*
	 * Blue shield combat icon
	 */
	BLUE_SHIELD,
	
	/*
	 * Small java combat icon
	 */
	JAVA,
	
	/*
	 * Small veteran combat icon
	 */
	VETERAN,
	
	
	/*
	 * No combat icon
	 */
	NONE;

	/**
	 * Gets the id that will be sent to client for said CombatIcon.
	 * @return	The index that will be sent to client.
	 */
	public int getId() {
		return ordinal() - 1;
	}
	
	/**
	 * Gets the CombatIcon object for said id, being compared
	 * to it's ordinal (so ORDER IS CRUCIAL).
	 * @param id	The ordinal index of the combat icon.
	 * @return		The CombatIcon who's ordinal equals id.
	 */
	public static CombatIcon forId(int id) {
		for (CombatIcon icon : CombatIcon.values()) {
			if (icon.getId() == id)
				return icon;
		}
		return CombatIcon.BLOCK;
	}
}