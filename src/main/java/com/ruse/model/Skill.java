package com.ruse.model;

import com.ruse.util.Misc;


/**
 * This enum contains data used as constants for skill configurations
 * such as experience rates, string id's for interface updating.
 * @author Gabriel Hannason
 */
public enum Skill {

															//fun easy, med, hard, extreme -- modern
    ATTACK(6247, 1, 0, 9747, 9748, -1, 10000, 188, 150, 113, 75, 113), //done
    DEFENCE(6253, 1, 6, 9753, 9754, -1, 10000, 188, 150, 113, 75, 113), //done 
    STRENGTH(6206, 1, 3, 9750, 9751, -1, 10000, 188, 150, 113, 75, 113), //done
    CONSTITUTION(6216, 3, 1, 9768, 9769, -1, 10000, 188, 150, 113, 75, 113), //done 
    RANGED(4443, 2, 9, 9756, 9757, -1, 10000, 188, 150, 113, 75, 113), //done 
    PRAYER(6242, 6, 12, 9759, 9760, 22014, 10000, 188, 150, 113, 75, 113), //done
    MAGIC(6211, 2, 15, 9762, 9763, -1, 10000, 188, 150, 113, 75, 113), //done
    COOKING(6226, 1, 11, 9801, 9802, 22028, 10000, 188, 150, 113, 75, 113), //done
    WOODCUTTING(4272, 1, 17, 9807, 9808, 22030, 10000, 94, 75, 56, 38, 56), //done
    FLETCHING(6231, 2, 16, 9783, 9784, 22021, 10000, 156, 125, 94, 62, 94), //done
    FISHING(6258, 1, 8, 9798, 9799, 22027, 10000, 81, 65, 49, 32, 85), //done
    FIREMAKING(4282, 2, 14, 9804, 9805, 22029, 10000, 93, 75, 56, 37, 56), //done
    CRAFTING(6263, 4, 13, 9780, 9781, 22020, 10000, 118, 95, 71, 47, 71), //done
    SMITHING(6221, 7, 5, 9795, 9796, 22026, 10000, 125, 100, 75, 50, 85), //done
    MINING(4416, 3, 2, 9792, 9793, 22025, 10000, 125, 100, 75, 50, 85), //done
    HERBLORE(6237, 4, 7, 9774, 9775, 22018, 10000, 131, 105, 79, 53, 79), //done
    AGILITY(4277, 4, 4, 9771, 9772, 22017, 10000, 94, 75, 56, 38, 75), //done
    THIEVING(4261, 1, 10, 9777, 9778, 22019, 10000, 170, 136, 102, 68, 102), //done
    SLAYER(12122, 6, 19, 9786, 9787, 22022, 10000, 188, 150, 113, 75, 5), //done 
    FARMING(5267, 4, 20, 9810, 9811, 22031, 10000, 89, 71, 53, 35, 53), //done
    RUNECRAFTING(4267, 3, 18, 9765, 9766, 22015, 10000, 130, 104, 78, 52, 78), //done
	CONSTRUCTION(7267, 3, 21, 9789, 9790, -1, 1, 1, 1, 1, 1, 1), //done
    HUNTER(8267, 3, 22, 9948, 9949, 22024, 10000, 131, 105, 79, 53, 79), //done
    SUMMONING(9267, 5, 23, 12169, 12170, 22032, 10000, 134, 107, 80, 54, 80), //done
    DUNGEONEERING(10267, 5, 24, 18508, 18509, -1, 10000, 5, 4, 3, 2, 2); 
	
	/*
	 * 	public static final int ATTACK_MODIFIER = 230;
		public static final int DEFENCE_MODIFIER = 230;
		public static final int STRENGTH_MODIFIER = 230;
		public static final int CONSTITUTION_MODIFIER = 190;
		public static final int RANGED_MODIFIER = 260;
		public static final int PRAYER_MODIFIER = 50;
		public static final int MAGIC_MODIFIER = 290;
		public static final int HERBLORE_MODIFIER = 5;
		public static final int CRAFTING_MODIFIER = 3;
		public static final int RUNECRAFTING_MODIFIER = 3;
	 */

	private Skill(int chatboxInterface, int prestigePoints, int prestigeId, int skillCapeId, int skillCapeTrimmedId, int petid, int funExperienceModifier, int easyExperienceModifier, int regularExperienceModifier, int hardExperienceModifier, int extremeExperienceModifier, int modernExperienceModifier) {
		this.chatboxInterface = chatboxInterface;
		this.prestigePoints = prestigePoints;
		this.prestigeId = prestigeId;
		this.skillcapeid = skillCapeId;
		this.skillcapetrimmedid = skillCapeTrimmedId;
		this.petid = petid;
		this.funExperienceModifier = funExperienceModifier;
		this.easyExperienceModifier = easyExperienceModifier;
		this.regularExperienceModifier = regularExperienceModifier;
		this.hardExperienceModifier = hardExperienceModifier;
		this.extremeExperienceModifier = extremeExperienceModifier;
		this.modernExperienceModifier = modernExperienceModifier;
	}
	
	/**
	 * The skill's chatbox interface
	 * The interface which will be sent
	 * on levelup.
	 */
	private int chatboxInterface;

	/**
	 * The amount of points
	 * the player will receive 
	 * for prestiging the skill.
	 */
	private int prestigePoints;
	
	/**
	 * The button id for prestiging
	 * this skill.
	 */
	private int prestigeId;
	
	private int skillcapeid;
	
	private int skillcapetrimmedid;
	
	private int petid;
	
	private int funExperienceModifier;
	private int easyExperienceModifier;
	private int regularExperienceModifier;
	private int hardExperienceModifier;
	private int extremeExperienceModifier;
	private int modernExperienceModifier;
	
	public int getFunExperienceModifier() {
		return funExperienceModifier;
	}
	public int getEasyExperienceModifier() {
		return easyExperienceModifier;
	}
	public int getRegularExperienceModifier() {
		return regularExperienceModifier;
	}
	public int getHardExperienceModifier() {
		return hardExperienceModifier;
	}
	public int getExtremeExperienceModifier() {
		return extremeExperienceModifier;
	}
	public int getModernExperienceModifier() {
		return modernExperienceModifier;
	}
	
	/**
	 * Gets the Skill's chatbox interface.
	 * @return The interface which will be sent on levelup.
	 */
	public int getChatboxInterface() {
		return chatboxInterface;
	}
	
	/**
	 * Get's the amount of points the player
	 * will receive for prestiging the skill.
	 * @return The prestige points reward.
	 */
	public int getPrestigePoints() {
		return prestigePoints;
	}
	
	public int getSkillCapeId() {
		return skillcapeid;
	}
	
	public int getSkillCapeTrimmedId() {
		return skillcapetrimmedid;
	}
	
	public int getPetId() {
		return petid;
	}
	
	
	/**
	 * Gets the Skill's name.
	 * @return	The skill's name in a lower case format.
	 */
	public String getName() {
		return toString().toLowerCase();
	}

	/**
	 * Gets the Skill's name.
	 * @return	The skill's name in a formatted way.
	 */
	public String getFormatName() {
		return Misc.formatText(getName());
	}

	/**
	 * Gets the Skill value which ordinal() matches {@code id}.
	 * @param id	The index of the skill to fetch Skill instance for.
	 * @return		The Skill instance.
	 */
	public static Skill forId(int id) {
		for (Skill skill : Skill.values()) {
			if (skill.ordinal() == id) {
				return skill;
			}
		}
		return null;
	}
	
	/**
	 * Gets the Skill value which prestigeId matches {@code id}.
	 * @param id	The skill with matching prestigeId to fetch.
	 * @return		The Skill instance.
	 */
	public static Skill forPrestigeId(int id) {
		for (Skill skill : Skill.values()) {
			if (skill.prestigeId == id) {
				return skill;
			}
		}
		return null;
	}
	
	public static boolean skillcapeEffect(int id) {
		for (Skill skill : Skill.values()) {
			if (skill.skillcapeid == id || skill.skillcapetrimmedid == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the Skill value which name matches {@code name}.
	 * @param string	The name of the skill to fetch Skill instance for.
	 * @return		The Skill instance.
	 */
	public static Skill forName(String name) {
		for (Skill skill : Skill.values()) {
			if (skill.toString().equalsIgnoreCase(name)) {
				return skill;
			}
		}
		return null;
	}

	/**
	 * Custom skill multipliers
	 * @return multiplier.
	 */

}