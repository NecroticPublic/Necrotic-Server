package com.ruse.model;

/**
 * Enumeration holding all possible notification types
 * @author Stan
 *
 */
public enum MessageType {

	NPC_ALERT("<col=008FB2>->", "@red@"),
	SERVER_ALERT("<img=10>", "<col=008FB2>"),
	PLAYER_ALERT("<img=10>", "<col=008FB2>"),
	LOOT_ALERT("@gre@[LOOT]", "<col=008FB2>");

	private String prefix;
	private String color;
	
	MessageType(String prefix, String color){
		this.setPrefix(prefix);
		this.setColor(color);
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
