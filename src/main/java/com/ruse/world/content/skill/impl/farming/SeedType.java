package com.ruse.world.content.skill.impl.farming;

public enum SeedType {
	HERB, ALLOTMENT, FLOWER;

	public static SeedType forId(int id) {
		for(SeedType type : SeedType.values()) {
			if(type != null && type.ordinal() == id)
				return type;
		}
		return HERB;
	}
}
