package com.ruse.world.content.skill.impl.summoning;

import com.ruse.world.content.skill.impl.summoning.BossPets.BossPet;
import com.ruse.world.entity.impl.player.Player;

public class SummoningData {

	/** 
	 * Array details:
	 * 0: BoB slot
	 * 1: frameId for sending the item
	 */
	public static int frames[][] = {
		{0, 2702}, {1, 2705},{2, 2706}, {3, 2707}, {4, 2708}, {5, 2709},
		{6, 2710}, {7, 2711}, {8, 2712}, {9, 2713}, {10, 2714}, {11, 2715},
		{12, 2716}, {13, 2717}, {14, 2718}, {15, 2719}, {16, 2720}, {17, 2721},
		{18, 2722}, {19, 2723}, {20, 2724}, {21, 2725}, {22, 2726}, {23, 2727}, 
		{24, 2728}, {25, 2729}, {26, 2730}, {27, 2731}, {28, 2732}, {29, 2733}
	};

	/**
	 *@param bob slot
	 *@return frame id which is used to send an item on the interface
	 */
	public static int getFrameID(int slot) {
		int k = -1;
		for(int i = 0; i < frames.length; i++) {
			if(frames[i][0] == slot) {
				k = frames[i][1];
			}
		}
		return k;
	}

	/**
	 * 
	 * @param itemId
	 * @return amount of item in the array
	 */
	/*public static int getItemAmount(Player c, int itemId) {
		int k = 0;
		Item itemToCheck = new Item(itemId);
		if(!itemToCheck.getDefinition().isStackable()) {
			for(int i= 0; i < c.getAdvancedSkills().getSummoning().storedItems.size(); i++) {
				if(c.getAdvancedSkills().getSummoning().storedItems.get(i).getId() == itemId)
					k++;
			}
		}
		return k;
	}

	 */

	public static int calculateScrolls(Player player) {
		if(player.getSummoning().getFamiliar() != null) {
			int scrollId = FamiliarData.forNPCId(player.getSummoning().getFamiliar().getSummonNpc().getId()).scroll;
			return player.getInventory().getAmount(scrollId);
		}
		return 0;
	}

	public static boolean isPouch(Player player, int item, int action) {
		if(action == 2) {
			FamiliarData familiar = FamiliarData.forId(item);
			if (familiar != null) {
				if(player.getSummoning().getFamiliar() != null && player.getSummoning().getFamiliar().isPet()) {
					player.getPacketSender().sendMessage("You already have a familiar.");
					return true;
				}
				boolean renew = player.getSummoning().getFamiliar() != null && FamiliarData.forNPCId(player.getSummoning().getFamiliar().getSummonNpc().getId()).getPouchId() == item;
				player.getSummoning().summon(familiar, renew, false);
				return true;
			}
		} else if(action == 3) {
			BossPet pet = BossPet.forId(item);
			if(pet != null) {
				player.getSummoning().summonPet(pet, false);
				return true;
			}
		}
		return false;
	}

	public static boolean beastOfBurden(int npcId) {
		switch(npcId) {
		case 6806: // thorny snail
		case 6807:
		case 6994: // spirit kalphite
		case 6995:
		case 6867: // bull ant
		case 6868:
		case 6794: // spirit terrorbird
		case 6795:
		case 6815: // war tortoise
		case 6816:
		case 6874:// pack yak
		case 6873: // pack yak
		case 3594: // yak
		case 3590: // war tortoise
		case 3596: // terrorbird
			return true;
		}
		return false;
	}
	
	public static boolean regenerationFamililar(int npcId) {
		switch (npcId) {
		case 6813: //bunyip
		case 6822: //unicorn
			return true;
		}
		return false;
	}


	public static int getStoreAmount(int npcId) {
		switch(npcId) {
		case 6806: // thorny snail
		case 6807:
			return 3;
		case 6867:
		case 6868: // bull ant
			return 6;
		case 6794: // terrorbird
			return 12;
		case 6815:
			return 18; // war tortoisee
		case 3590: // war tortoise
			return 18;
		case 3594: // yak
		case 6873: // pack yak
			return 30;
		}
		return 0;
	}


	public static int getFollowerTimer(int npc) { // seconds
		switch(npc) {
		case 7343:
			return 3840;
		case 6830:
			return 360;
		case 6825:
			return 240;
		case 6840:
			return 900;
		case 6806:
			return 960;
		case 6796:
			return 1080;
		case 7331:
			return 720;
		case 6831:
			return 1140;
		case 6837:
			return 1020;
		case 7361:
			return 1080;
		case 6847:
			return 1320;
		case 6994:
			return 1320;
		case 6872:
			return 1440;
		case 7353:
			return 1860;
		case 6835:
			return 1980;
		case 6845:
			return 1500;
		case 6805:
			return 1620;
		case 7370:
			return 1620;
		case 7333:
			return 1620;
		case 7351:
			return 5640;
		case 7367:
			return 5640;
		case 6853:
			return 1800;
		case 6855:
			return 2220;
		case 6857:
			return 2760;
		case 6859:
			return 3300;
		case 6861:
			return 3960;
		case 6863:
			return 9060;
		case 6867:
			return 1800;
		case 6851:
			return 1860;
		case 6833:
			return 1800;
		case 6875:
			return 2160;
		case 6877:
			return 2160;
		case 6879:
			return 2160;
		case 6881:
			return 2160;
		case 6883:
			return 2160;
		case 6885:
			return 2160;
		case 6887:
			return 2160;
		case 7377:
			return 1920;
		case 6823:
			return 2040;
		case 6843:
			return 2040;
		case 6794:
			return 2160;
		case 6818:
			return 1800;
		case 6992:
			return 2580;
		case 6991:
			return 2280;
		case 7365:
			return 2940;
		case 7337:
			return 2940;
		case 7363:
			return 2940;
		case 6809:
			return 2640;
		case 6865:
			return 2880;
		case 6820:
			return 2460;
		case 6802:
			return 3360;
		case 6827:
			return 2940;
		case 6889:
			return 480;
		case 6815:
			return 2580;
		case 6813:
			return 2640;
		case 6817:
			return 2700;
		case 7375:
			return 3600;
		case 7349:
			return 1920;
		case 7339:
			return 4140;
		case 7329:
			return 3360;
		case 7341:
			return 3660;
		case 7359:
			return 3840;
		case 7357:
			return 3480;
		case 7355:
			return 3720;
		case 6873:
			return 3480;
		case 6822:
			return 3240;
		case 6869:
			return 3720;
		case 6804:
			return 3420;
		case 9488:
			return 2940;
		case 6800:
			return 2940;
		case 7347:
			return 2940;
		case 7335:
			return 2700;
		case 6798:
			return 4140;
		case 6849:
			return 2820;
		case 7345:
			return 3300;
		case 8575:
			return 1800;
		case 6839:
			return 1680;
		case 7372:
			return 1440;
		}
		return 60;
	}
	
	/*

	public static enum FamiliarCombatData {
		SPIRIT_WOLF(6830, AttackType.MELEE, 1, new Animation(8292), new Animation(8293), -1, 5, 40),
		DREADFOWL(6825,AttackType.MELEE, 1, new Animation(5387), new Animation(5388), -1, 5, 40),
		SPIRIT_SPIDER(6841, AttackType.MELEE, 1, new Animation(5327), new Animation(5329), -1, 5, 30),
		THORNY_SNAIL(6806, AttackType.RANGED, 5, new Animation(8148), new Animation(8147), 328, 5, 40),
		GRANITE_CRAB(6796, AttackType.MELEE, 1, new Animation(8104), new Animation(8147), -1, 5, 40),
		SPIRIT_MOSQUITO(7331, AttackType.MELEE, 1, new Animation(8032), new Animation(8033), -1, 5, 40),
		DESERT_WYRM(6831, AttackType.MELEE, 1, new Animation(7795), new Animation(7797), -1, 5, 40),
		SPIRIT_SCORPION(6837, AttackType.MELEE, 1, new Animation(6254), new Animation(6256), -1, 5, 60),
		SPIRIT_TZKIH(7361, AttackType.MELEE, 1, new Animation(8257), new Animation(8258), -1, 5, 50),
		ALBINO_RAT(6847, AttackType.MELEE, 1, new Animation(4933), new Animation(4935), -1, 5, 50),
		SPIRIT_KALPHITE(6994, AttackType.MAGIC, 5, new Animation(8519), new Animation(8517), 2224, 5, 50),
		COMPOST_MOUND(6872, AttackType.MELEE, 1, new Animation(7769), new Animation(7770), -1, 5, 50),
		GIANT_CHINCOMPA(7353, AttackType.RANGED, 5, new Animation(7755), new Animation(7756), -1, 5, 50);

		private FamiliarCombatData(int npcId, AttackType attkType, int attkDistanceRequired, Animation attackAnimation, Animation deathAnimation, int projectTile, int attackSpeed, int maxhit) {
			this.npcId = npcId;
			this.attackType = attkType;
			this.distanceRequired = attkDistanceRequired;
			this.attackAnimation = attackAnimation;
			this.deathAnimation = deathAnimation;
			this.projectileId = projectTile;
			this.attackSpeed = attackSpeed;
			this.maxhit = maxhit;
		}

		public int npcId;
		public AttackType attackType;
		public int distanceRequired;
		public Animation attackAnimation;
		public Animation deathAnimation;
		public int projectileId;
		public int attackSpeed;
		public int maxhit;

		public static FamiliarCombatData forId(int id) {
			for (FamiliarCombatData familiar : FamiliarCombatData.values()) {
				if (familiar.npcId == id) {
					return familiar;
				}
			}
			return null;
		}
	}*/
}
