package com.ruse.world.content.skill.impl.herblore;

public enum UnfinishedPotions {
	
	GUAM_POTION(91, 249, 1),
	MARRENTILL_POTION(93, 251, 5),
	TARROMIN_POTION(95, 253, 12),
	HARRALANDER_POTION(97, 255, 22),
	RANARR_POTION(99, 257, 30),
	TOADFLAX_POTION(3002, 2998, 34),
	SPIRIT_WEED_POTION(12181, 12172, 40),
	IRIT_POTION(101, 259, 45),
	WERGALI_POTION(14856, 14854, 1),
	AVANTOE_POTION(103, 261, 50),
	KWUARM_POTION(105, 263, 55),
	SNAPDRAGON_POTION(3004, 3000, 63),
	CADANTINE_POTION(107, 265, 66),
	LANTADYME(2483, 2481, 69),
	DWARF_WEED_POTION(109, 267, 72),
	TORSTOL_POTION(111, 269, 78);
	
	
	private int unfinishedPotion, herbNeeded, levelReq;
	
	private UnfinishedPotions(int unfinishedPotion, int herbNeeded, int levelReq) {
		this.unfinishedPotion = unfinishedPotion;
		this.herbNeeded = herbNeeded;
		this.levelReq = levelReq;
	}
	
	public int getUnfPotion() {
		return unfinishedPotion;
	}
	
	public int getHerbNeeded() {
		return herbNeeded;
	}
	
	public int getLevelReq() {
		return levelReq;
	}
	
	public static UnfinishedPotions forId(int herbId) {
		for(UnfinishedPotions unf : UnfinishedPotions.values()) {
			if (unf.getHerbNeeded() == herbId) {
				return unf;
			}
		}
		return null;
	}
	
	public static UnfinishedPotions forUnfPot(int unfId) {
		for(UnfinishedPotions unf : UnfinishedPotions.values()) {
			if (unf.getUnfPotion() == unfId) {
				return unf;
			}
		}
		return null;
	}
	
}