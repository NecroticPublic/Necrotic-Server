package com.ruse.world.content.npcinteraction;

public enum PotionDecanting {
    
    STRENGTH(113, 115, 117, 119),
    ATTACK(2428, 121, 123, 125),
    RESTORE(2430, 127, 129, 131),
    DEFENCE(2432, 133, 135, 137),
    PRAYER(2434, 139, 141, 143),
    FISHING(2438, 151, 153, 155),
    RANGING(2444, 169, 171, 173),
    ANTIFIRE(2452, 2454, 2456, 2458),
    ENERGY(3008, 3010, 3012, 3014),
    AGILITY(3032, 3034, 3036, 3038),
    MAGIC(3040, 3042, 3044, 3046),
    COMBAT(9739, 9741, 9743, 9745),
    SUMMONING(12140, 12142, 12144, 12146),
    SUPER_ATTACK(2436, 145, 147, 149),
    SUPER_STRENGTH(2440, 157, 159, 161),
    SUPER_DEFENCE(2442, 163, 165, 167),
    SUPER_ENERGY(3016, 3018, 3020, 3022),
    SUPER_RESTORE(3024, 3026, 3028, 3030);
	PotionDecanting(int fullId, int threeQuartersId, int halfId, int quarterId) {
        this.quarterId = quarterId;
        this.halfId = halfId;
        this.threeQuartersId = threeQuartersId;
        this.fullId = fullId;
    }
    
    private int quarterId, halfId, threeQuartersId, fullId;
    
    public int getQuarterId() {
        return this.quarterId;
    }
    
    public int getHalfId() {
        return this.halfId;
    }
    
    public int getThreeQuartersId() {
        return this.threeQuartersId;
    }
    
    public int getFullId() {
        return this.fullId;
    }


}