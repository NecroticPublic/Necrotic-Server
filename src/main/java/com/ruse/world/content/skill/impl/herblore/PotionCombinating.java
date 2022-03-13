package com.ruse.world.content.skill.impl.herblore;

import com.ruse.world.entity.impl.player.Player;

/**
 * Combinates potions into doses
 * @author Gabriel Hannason
 */
public class PotionCombinating {
	
	public static void combinePotion(Player p, int firstPotID, int secondPotID) {
		CombiningDoses potion = CombiningDoses.getPotionByID(firstPotID);
		if (potion == null || !p.getInventory().contains(firstPotID) || !p.getInventory().contains(secondPotID))
			return;
		if (potion.getDoseForID(secondPotID) > 0) {
			int firstPotAmount = potion.getDoseForID(firstPotID);
			int secondPotAmount = potion.getDoseForID(secondPotID);
			if (firstPotAmount + secondPotAmount <= 4) {
				p.getInventory().delete(firstPotID, 1);
				p.getInventory().delete(secondPotID, 1);
				p.getInventory().add(potion.getIDForDose(firstPotAmount + secondPotAmount), 1);
				p.getInventory().add(EMPTY_VIAL, 1);
			} else {
				int overflow = (firstPotAmount + secondPotAmount) - 4;
				p.getInventory().delete(firstPotID, 1);
				p.getInventory().delete(secondPotID, 1);
				p.getInventory().add(potion.getIDForDose(4), 1);
				p.getInventory().add(potion.getIDForDose(overflow), 1);
			}
		}
	}
	
	public static final int VIAL = 227;
	public static final int EMPTY_VIAL = 229;

	public enum CombiningDoses {

		STRENGTH(119, 117, 115, 113, VIAL, "Strength"), 
		SUPER_STRENGTH(161,159, 157, 2440, VIAL, "Super strength"), 
		ATTACK(125, 123, 121, 2428, VIAL, "Attack"), 
		SUPER_ATTACK(149, 147, 145, 2436, VIAL,"Super attack"), 
		DEFENCE(137, 135, 133, 2432, VIAL, "Defence"), 
		SUPER_DEFENCE(167, 165, 163, 2442, VIAL, "Super defence"), 
		RANGING_POTION(173, 171, 169, 2444, VIAL, "Ranging"),
		FISHING(155, 153, 151, 2438, VIAL, "Fishing"), 
		PRAYER(143, 141, 139, 2434, VIAL, "Prayer"), 
		ANTIFIRE(2458, 2456, 2454, 2452, VIAL, "Antifire"), 
		ZAMORAK_BREW(193, 191, 189, 2450, VIAL, "Zamorakian brew"), 
		ANTIPOISON(179, 177, 175, 2446, VIAL, "Antipoison"), 
		RESTORE(131, 129, 127, 2430, VIAL, "Restoration"), 
		MAGIC_POTION(3046, 3044, 3042, 3040, VIAL, "Magic"), 
		SUPER_RESTORE(3030, 3028, 3026, 3024, VIAL, "Super Restoration"), 
		ENERGY(3014, 3012, 3010, 3008, VIAL, "Energy"), 
		SUPER_ENERGY(3022, 3020, 3018, 3016, VIAL, "Super Energy"), 
		AGILITY(3038, 3036, 3034, 3032, VIAL, "Agility"), 
		SARADOMIN_BREW(6691, 6689, 6687, 6685, VIAL, "Saradomin brew"), 
		ANTIPOISON1(5949, 5947, 5945, 5943, VIAL, "Antipoison(+)"), 
		ANTIPOISON2(5958, 5956, 5954, 5952, VIAL, "Antipoison(++)"), 
		SUPER_ANTIPOISON(185, 183, 181, 2448, VIAL, "Super Antipoison"), 
		RELICYMS_BALM(4848, 4846, 4844, 4842, VIAL, "Relicym's balm"), 
		SERUM_207(3414, 3412, 3410, 3408, VIAL, "Serum 207"), 
		COMBAT(9745, 9743, 9741, 9739, VIAL, "Combat"), 
		EXTR_RANGE(15327, 15326, 15325, 15324, VIAL, "Extreme ranging"),
		EXTR_STR(15315, 15314, 15313, 15312, VIAL, "Extreme stength"), 
		EXTR_MAGE(15323, 15322, 15321, 15320, VIAL, "Extreme magic"), 
		EXTR_ATK(15311, 15310, 15309, 15308, VIAL, "Extreme attack"), 
		EXTR_DEF(15319, 15318, 15317, 15316, VIAL, "Extreme defence"), 
		SUPER_PRAYER(15331, 15330, 15329, 15328, VIAL, "Super prayer"), 
		OVERLOAD(15335, 15334, 15333, 15332, VIAL, "Overload"), 
		SUPER_FIRE(15307, 15306, 15305, 15304, VIAL, "Super antifire"), 	
		REC_SPEC(15303, 15302, 15301, 15300, VIAL, "Recover special");

		/*
		 * This is what the data in the above enumeration is, in order. EX:
		 * COMBAT(oneDosePotionID, twoDosePotionID, threeDosePotionID,
		 * fourDosePotionID, vial, "potionName");
		 */

		int oneDosePotionID, twoDosePotionID, threeDosePotionID,
				fourDosePotionID, vial;
		String potionName;

		/**
		 * @param oneDosePotionID
		 *            - This is the ID for the potion when it contains one dose.
		 * 
		 * @param twoDosePotionID
		 *            - This is the ID for the potion when it contains two
		 *            doses.
		 * 
		 * @param threeDosePotionID
		 *            - This is the ID for the potion when it contains three
		 *            doses.
		 * 
		 * @param fourDosePotionID
		 *            - This is the ID for the (full) potion when it contains
		 *            four doses.
		 * 
		 * @param vial
		 *            - This is referenced from: private static final int VIAL =
		 *            227; It's a constant and its value never changes.
		 * 
		 * @param potionName
		 *            - This is a string which is used to set a name for the
		 *            potion. Within an enumeration you can use the name().
		 *            method to take the name in-front of the stored data. This
		 *            however could not be done because of some potion names.
		 */

		private CombiningDoses(int oneDosePotionID, int twoDosePotionID,
				int threeDosePotionID, int fourDosePotionID, int vial,
				String potionName) {
			this.oneDosePotionID = oneDosePotionID;
			this.twoDosePotionID = twoDosePotionID;
			this.threeDosePotionID = threeDosePotionID;
			this.fourDosePotionID = fourDosePotionID;
			this.vial = vial;
			this.potionName = potionName;
		}

		/*
		 * These are code getters to use the data stored in the above
		 * enumeration.
		 */

		public int getQuarterId() {
			return oneDosePotionID;
		}

		public int getHalfId() {
			return twoDosePotionID;
		}

		public int getThreeQuartersId() {
			return threeDosePotionID;
		}

		public int getFullId() {
			return fourDosePotionID;
		}

		public int getVial() {
			return vial;
		}

		public String getPotionName() {
			return potionName;
		}

		/**
		 * 
		 * @param id
		 * @return The dose that this id represents for this potion, or -1 if it
		 *         doesn't belong to this potion.
		 * @date 2/28/12
		 * @author 0021sordna
		 */

		public int getDoseForID(int id) {
			if (id == this.oneDosePotionID) {
				return 1;
			}
			if (id == this.twoDosePotionID) {
				return 2;
			}
			if (id == this.threeDosePotionID) {
				return 3;
			}
			if (id == this.fourDosePotionID) {
				return 4;
			}
			return -1;
		}

		/**
		 * 
		 * @param dose
		 * @return The ID for this dose of the potion, or -1 if this dose
		 *         doesn't exist.
		 * @date 2/28/12
		 * @author 0021sordna
		 */

		public int getIDForDose(int dose) {
			if (dose == 1) {
				return this.oneDosePotionID;
			}
			if (dose == 2) {
				return this.twoDosePotionID;
			}
			if (dose == 3) {
				return this.threeDosePotionID;
			}
			if (dose == 4) {
				return this.fourDosePotionID;
			}
			if (dose == 0) {
				return EMPTY_VIAL;
			}
			return -1;
		}

		/**
		 * 
		 * @param ID
		 * @return The potion that matches the ID. ID can be any dose of the
		 *         potion.
		 * @date 2/28/12
		 * @author 0021sordna
		 */

		public static CombiningDoses getPotionByID(int id) {
			for (CombiningDoses potion : CombiningDoses.values()) {
				if (id == potion.oneDosePotionID) {
					return potion;
				}
				if (id == potion.twoDosePotionID) {
					return potion;
				}
				if (id == potion.threeDosePotionID) {
					return potion;
				}
				if (id == potion.fourDosePotionID) {
					return potion;
				}
			}
			return null;
		}
	}	
}
