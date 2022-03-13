package com.ruse.world.content.skill.impl.thieving;

import com.ruse.model.Item;
import com.ruse.util.Misc;

public enum PickpocketData {
	MAN(new int[]{1,2,3,4,5,6}, 1, 8, new int[]{10, 10}, new String[]{"Keep your hands to yourself!", "What are you doing?!", "Hey! Cut it out.", "Stop that!"}, new Item[] {new Item(995, 3)}),
	FARMER(new int[]{7}, 10, 15, new int[]{10, 20}, new String[]{"Oi!", "What're you doing?", "Stop it.", "You're mad.", "Quit it."}, new Item[] {new Item(5318), new Item(995, 9)}),
	FEMALE_HAM(new int[]{1715}, 15, 19, new int[]{10, 30}, new String[]{"Thief!", "We do not take kindly to thieves!", "Get away from me!", "What's your problem?"}, new Item[] {new Item(4298), new Item(4300), new Item(4302), new Item(4304), new Item(4306), new Item(4308), new Item(4310), new Item(1351), new Item(1205), new Item(1265), new Item(1739), new Item(1349), new Item(1267), new Item(1129), new Item(1131), new Item(1167), new Item(1095), new Item(1063), new Item(1511), new Item(1734), new Item(321), new Item(2138), new Item(440), new Item(453), new Item(886), new Item(1269), new Item(1353), new Item(995, 20), new Item(199), new Item(203), new Item(211), new Item(205), new Item(207), new Item(209), new Item(946), new Item(1733), new Item(1207), new Item(590), new Item(10496), new Item(1627), new Item(1625), new Item(995, 25)}),
	MALE_HAM(new int[]{1714}, 20, 23, new int[]{16, 36}, new String[]{"Pickpocket!", "Stay out of my pockets!", "Caught you!", "What is this?"}, new Item[] {new Item(4298), new Item(4300), new Item(4302), new Item(4304), new Item(4306), new Item(4308), new Item(4310), new Item(1351), new Item(1205), new Item(1265), new Item(1739), new Item(1349), new Item(1267), new Item(1129), new Item(1131), new Item(1167), new Item(1095), new Item(1063), new Item(1511), new Item(1734), new Item(321), new Item(2138), new Item(440), new Item(453), new Item(886), new Item(1269), new Item(1353), new Item(995, 20), new Item(199), new Item(203), new Item(211), new Item(205), new Item(207), new Item(209), new Item(946), new Item(1733), new Item(1207), new Item(590), new Item(10496), new Item(1627), new Item(1625), new Item(995, 45)}),
	WARRIOR_WOMAN_ALKHARID(new int[]{18, 15}, 25, 26, new int[]{18, 38}, new String[]{"You there! Away from my pockets!", "Inconsiderate rat!", "Oi!", "Halt."}, new Item[] {new Item(995, 18)}),
	ROGUE(new int[]{187}, 32, 36, new int[]{20, 40}, new String[]{"Thief, stay away.", "I am not afraid of conflict.", "You best not try that again.", "Is this how you want to die?"}, new Item[]{new Item(995, 40), new Item(995, 25), new Item(1523), new Item(1219), new Item(1993), new Item(556, 8)}),
	CAVE_GOBLIN(new int[]{5752, 5753, 5755, 5756, 5757, 5758, 5759}, 36, 40, new int[]{22,42}, new String[]{"Human?", "Surface-dweller! Why?!", "Stay away human!", "No touch!"}, new Item[]{new Item(995, 50)}),
	MASTER_FARMER(new int[]{3299, 2234, 2235}, 38, 43, new int[]{30, 30}, new String[]{"Let it go, mate.", "Cor blimey!", "Do you mind?", "Oi!", "Got'cha!"}, new Item[] {new Item(5291), new Item(5292), new Item(5293), new Item(5294), new Item(5291), new Item(5292), new Item(5293), new Item(5294), new Item(5295), new Item(5296), new Item(5297), new Item(5298), new Item(5299), new Item(5300), new Item(5301), new Item(5302), new Item(5303), new Item(5304)}),
	GUARD(new int[]{9}, 40, 47, new int[]{25, 45}, new String[]{"Guard might get nervous...", "I used to be an adventurer like you.", "Disrespect the law, and you disrespect me.", "No lollygaggin'.", "Everything all right?", "Only burglars and vampires creep around after dark. So which are you?"}, new Item[]{new Item(995, 30)}),
	//FREMENNIK
	DESERT_BANDIT(new int[]{1880, 1881}, 53, 80, new int[]{50, 50}, new String[]{"Are you sure about that?","We don't take kindly to strangers." ,"You looking for a beating?", "You mix potions, right? Could I get a brew?"}, new Item[]{new Item(995, 50)}),
	KNIGHT(new int[]{23, 26}, 53, 80, new int[]{50, 50}, new String[]{"Stay out of trouble.", "What is it?", "I don't have time for this.", "Hey! Hands off."}, new Item[]{new Item(995, 50)}),
	YANILLE_WATCHMAN(new int[]{34}, 65, 138, new int[]{50, 60}, new String[]{"Not on my watch.", "Guard might get nervous...", "I used to be an adventurer like you.", "Disrespect the law, and you disrespect me.", "No lollygaggin'.", "Everything all right?", "Only burglars and vampires creep around after dark. So which are you?"}, new Item[]{new Item(995, 60), new Item(2309)}),
	PALADIN(new int[]{20, 2256}, 70, 152, new int[]{60, 60}, new String[]{"You dare dishonor me?", "My blade is ready - try that again.", "I would leave now, thief.", "Not the brightest thief."}, new Item[]{new Item(995, 80), new Item(562, 2)}),
	GNOME(new int[]{66, 67, 68, 159, 160, 161, 168, 169}, 75, 199, new int[]{50, 100}, new String[]{"Stop!", "You're too brash, human.", "Keep your paws off me.", "Quit it."}, new Item[]{new Item(995, 300), new Item(577), new Item(444), new Item(569), new Item(2150), new Item(2162)}),
	HERO(new int[]{21}, 80, 275, new int[]{100, 150}, new String[]{"Keep your distance!", "Don't you know who I am?", "Insignificant fool!", "I will be your death.", "Is this worth dying over?"}, new Item[]{new Item(995, 200), new Item(995, 300), new Item(565), new Item(1601), new Item(1993), new Item(560, 2), new Item(569), new Item(444)}),
	ELF(new int[]{2363, 2364, 2365, 2366}, 85, 353, new int[]{100, 200}, new String[]{"Mortality is cruel.", "You might lose that finger!", "Keep a distance from me.", "Hahaha."}, new Item[]{new Item(995, 280), new Item(995, 350), new Item(561, 3), new Item(1601), new Item(1993), new Item(560, 2), new Item(569), new Item(444)}),
	//tzhaar-hur
	;

	private final int[] npcId;
	private final int levelRequirement;
	private final int experience;
	private final int[] damage;
	private final String[] failMessage;
	private final Item[] loot;

	private PickpocketData(final int[] npcId, final int level, final int experience, final int[] damage, final String[] failMessage, final Item[] loot) {
		this.npcId = npcId;
		this.levelRequirement = level;
		this.experience = experience;
		this.damage = damage;
		this.failMessage = failMessage;
		this.loot = loot;
	}
	
	public String getFailMessage() {
		return Misc.randomElement(failMessage);
	}
	
	public int getRequirement() {
		return levelRequirement;
	}

	public int getExperience() {
		return experience;
	}

	public Item[] getLoot() {
		return loot;
	}
	
	public Item getReward() {
		return Misc.randomElement(loot);
	}
	
	public int[] getNpcs() {
		return npcId;
	}
	
	public int getDamage() {
		if (damage[0] == damage[1]) {
			return damage[0];
		}
		return Misc.inclusiveRandom(damage[0], damage[1]);
	}
	
	public static PickpocketData forNpc(int npcId) {
		for (PickpocketData p : PickpocketData.values()) {
			for (int i : p.getNpcs()) {
				if (i == npcId) {
					return p;
				}
			}
		}
		return null;
	}
}