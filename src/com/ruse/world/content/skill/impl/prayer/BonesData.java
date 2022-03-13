package com.ruse.world.content.skill.impl.prayer;

public enum BonesData {
	 BONES(526, 5),
	 BAT_BONES(530, 6),
	 WOLF_BONES(2859, 1),
	 BIG_BONES(532, 15),
	 FEMUR_BONES(15182, 200),
	 BABYDRAGON_BONES(534, 30),
	 JOGRE_BONE(3125, 15),
	 ZOGRE_BONES(4812, 23),
	 LONG_BONES(10976, 200),
	 CURVED_BONE(10977, 200),
	 SHAIKAHAN_BONES(3123, 25),
	 DRAGON_BONES(536, 73),
	 FAYRG_BONES(4830, 84),
	 RAURG_BONES(4832, 96),
	 DAGANNOTH_BONES(6729, 125),
	 OURG_BONES(14793, 140),
	 FROSTDRAGON_BONES(18830, 180),
	 GUABT_SNAKE_SPINE(22047, 250);
	
	BonesData(int boneId, int buryXP) {
		this.boneId = boneId;
		this.buryXP = buryXP;
	}

	private int boneId;
	private int buryXP;
	
	public int getBoneID() {
		return this.boneId;
	}
	
	public int getBuryingXP() {
		return this.buryXP;
	}
	
	public static BonesData forId(int bone) {
		for(BonesData prayerData : BonesData.values()) {
			if(prayerData.getBoneID() == bone) {
				return prayerData;
			}
		}
		return null;
	}
	
}
