package com.ruse.world.content.skill.impl.crafting;

public enum leatherDialogueData {
	
	GREEN_LEATHER(1745, 1065, 1099, 1135),
	BLUE_LEATHER(2505, 2487, 2493, 2499),
	RED_LEATHER(2507, 2489, 2495, 2501),
	BLACK_LEATHER(2509, 2491, 2497, 2503);
	
	private int leather, vambraces, chaps, body;
	
	private leatherDialogueData(final int leather, final int vambraces, final int chaps, final int body) {
		this.leather = leather;
		this.vambraces = vambraces;
		this.chaps = chaps;
		this.body = body;
	}
	
	public int getLeather() {
		return leather;
	}
	
	public int getVamb() {
		return vambraces;
	}
	
	public int getChaps() {
		return chaps;
	}
	
	public int getBody() {
		return body;
	}
}
