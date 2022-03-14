package com.ruse.world.content.skill.impl.slayer;

import com.ruse.model.Position;
import com.ruse.util.Misc;

/**
 * @author Gabriel Hannason 
 */

public enum SlayerTasks {

	NO_TASK(null, -1, null, -1, null),

	/**
	 * Easy tasks
	 																			SlayerMaster taskMaster, int npcId, String npcLocation, int XP, Position taskPosition
	 */
	GOBLIN(SlayerMaster.VANNAKA, 4275, "Goblins are found in the Training Teleports.", 19, new Position(3246, 3245, 0)),
	ROCK_CRAB(SlayerMaster.VANNAKA, 1265, "Rock Crabs can be found in the Training Teleport.", 21, new Position(2709, 3715, 0)),
	EXPERIMENT(SlayerMaster.VANNAKA, 1677, "Experiments can be found in the Training Teleport.", 22, new Position(3564, 9954, 0)),
	GIANT_BAT(SlayerMaster.VANNAKA, 78, "Giant Bats can be found in Taverly Dungeon.", 20, new Position(2907, 9833, 0)),
	CHAOS_DRUID(SlayerMaster.VANNAKA, 181, "Chaos Druids can be found in Edgeville Dungeon.", 22, new Position(3109, 9931, 0)),
	ZOMBIE(SlayerMaster.VANNAKA, 76, "Zombies can be found in Edgeville Dungeon.", 20, new Position(3144, 9903, 0)),
	HOBGOBLIN(SlayerMaster.VANNAKA, 2686, "Hobgoblins can be found in Edgeville Dungeon.", 45, new Position(3123, 9876, 0)),
	HILL_GIANT(SlayerMaster.VANNAKA, 117, "Hill Giants can be found in Edgeville Dungeon.", 47, new Position(3120, 9844, 0)),
	DEADLY_RED_SPIDER(SlayerMaster.VANNAKA, 63, "Deadly Red Spiders can be found in Edgeville Dungeon.", 45, new Position(3083, 9940, 0)),
	BABY_BLUE_DRAGON(SlayerMaster.VANNAKA, 52, "Baby Blue Dragons can be found in Taverly Dungeon.", 50, new Position(2891, 9772, 0)),
	SKELETON(SlayerMaster.VANNAKA, 90, "Skeletons can be found in Edgeville Dungeon.", 22, new Position(3094, 9896, 0)),
	EARTH_WARRIOR(SlayerMaster.VANNAKA, 124, "Earth Warriors can be found in Edgeville Dungeon.", 54, new Position(3124, 9986, 0)),
	YAK(SlayerMaster.VANNAKA, 5529, "Yaks can be found in the Training Teleport.", 25, new Position(3203, 3267, 0)),
	GHOUL(SlayerMaster.VANNAKA, 1218, "Ghouls can be found in the Training Teleport.", 48, new Position(3418, 3508, 0)),
	MONK_OF_ZAMORAK(SlayerMaster.VANNAKA, 190, "Monks of Zamorak are North-West in the Chaos Tunnels.", 45, new Position(3151, 5489, 0)),
	BANSHEE(SlayerMaster.VANNAKA, 1612, "Banshee can be found on the first floor of the Slayer Tower.", 45, new Position(3441, 3545, 0)),
	CRAWLING_HAND(SlayerMaster.VANNAKA, 1652, "Crawling hands are found at the Entrance of Slayer Tower.", 45, new Position(3418, 3544, 0)),
	/**
	 * Medium tasks
	 */
	BANDIT(SlayerMaster.DURADEL, 1880, "Bandits can be found in the Training teleport.", 65, new Position(3172, 2976, 0)),
	WILD_DOG(SlayerMaster.DURADEL, 1593, "Wild Dogs can be found in Brimhaven Dungeon.", 67, new Position(2680, 9557, 0)),
	MOSS_GIANT(SlayerMaster.DURADEL, 112, "Moss Giants can be found in Brimhaven Dungeon.", 66, new Position(2676, 9549, 0)),
	FIRE_GIANT(SlayerMaster.DURADEL, 110, "Fire Giants can be found in Brimhaven Dungeon.", 69, new Position(2664, 9480, 0)),
	GREEN_DRAGON(SlayerMaster.DURADEL, 941, "Green Dragons can be found in western Wilderness.", 75, new Position(2977, 3615, 0)),
	BLUE_DRAGON(SlayerMaster.DURADEL, 55, "Blue Dragons can be found in Taverly Dungeon.", 80, new Position(2892, 9799, 0)),
	HELLHOUND(SlayerMaster.DURADEL, 49, "Hellhounds can be found in Taverly Dungeon.", 80, new Position(2870, 9848, 0)),
	BLACK_DEMON(SlayerMaster.DURADEL, 84, "Black Demons can be found in Edgeville Dungeon.", 83, new Position(3089, 9967, 0)),
	BLOODVELD(SlayerMaster.DURADEL, 1618, "Bloodvelds can be found in Slayer Tower.", 72, new Position(3418, 3570, 1)),
	INFERNAL_MAGE(SlayerMaster.DURADEL, 1643, "Infernal Mages can be found in Slayer Tower.", 70, new Position(3445, 3579, 1)),
	ABERRANT_SPECTRE(SlayerMaster.DURADEL, 1604, "Aberrant Spectres can be found in Slayer Tower.", 73, new Position(3432, 3553, 1)),
	NECHRYAEL(SlayerMaster.DURADEL, 1613, "Nechryaels can be found in Slayer Tower.", 78, new Position(3448, 3564, 2)),
	GARGOYLE(SlayerMaster.DURADEL, 1610, "Gargoyles can be found in Slayer Tower.", 81, new Position(3438, 3534, 2)),
	TZHAAR_XIL(SlayerMaster.DURADEL, 2605, "TzHaar-Xils can be found in Tzhaar City.", 90, new Position(2445, 5147, 0)),
	TZHAAR_HUR(SlayerMaster.DURADEL, 2600, "TzHaar-Hurs can be found in Tzhaar City.", 79, new Position(2456, 5135, 0)),
	ORK(SlayerMaster.DURADEL, 6273, "Orks can be found in the Godwars Dungeon.", 76, new Position(2867, 5322, 2)),
	ARMOURED_ZOMBIE(SlayerMaster.DURADEL, 8162, "Armoured Zombies can be found in the Training Teleport.",80, new Position(3085, 9672, 0)),
	DUST_DEVIL(SlayerMaster.DURADEL, 1624, "Dust Devils can be found in the Training Teleport.",95, new Position(3279, 2964, 0)),
	JUNGLE_STRYKEWYRM(SlayerMaster.DURADEL, 9467, "Strykewyrms can be found in the Strykewyrm Cavern.",109, new Position(2731, 5095, 0)),
	DESERT_STRYKEWYRM(SlayerMaster.DURADEL, 9465, "Strykewyrms can be found in the Strykewyrm Cavern.", 112, new Position(2731, 5095, 0)),
	
	/**
	 * Hard tasks
	 */
	DARK_BEAST(SlayerMaster.KURADEL, 2783, "Dark Beasts can be found in the Chaos Tunnels, via Dungeon teleport.", 160, new Position(3168, 5463, 0)),
	MONKEY_GUARD(SlayerMaster.KURADEL, 1459, "Monkey Guards can be found in the Training Teleport.",140, new Position(2795, 2775, 0)),
	WATERFIEND(SlayerMaster.KURADEL, 5361, "Waterfiends can be found in the Ancient Cavern.", 134, new Position(1737, 5353, 0)),
	ICE_STRYKEWYRM(SlayerMaster.KURADEL, 9463, "Strykewyrms can be found in the Strykewyrm Cavern.", 138, new Position(2731, 5095, 0)),
	STEEL_DRAGON(SlayerMaster.KURADEL, 1592, "Steel dragons can be found in Brimhaven Dungeon.", 156, new Position(2710, 9441, 0)),
	MITHRIL_DRAGON(SlayerMaster.KURADEL, 5363, "Mithril Dragons can be found in the Ancient Cavern.", 160, new Position(1761, 5329, 1)),
	GREEN_BRUTAL_DRAGON(SlayerMaster.KURADEL, 5362, "Green Brutal Dragons can be found in the Ancient Cavern.", 155, new Position(1767, 5340, 0)),
	SKELETON_WARLORD(SlayerMaster.KURADEL, 6105, "Skeleton Warlords can be found in the Ancient Cavern.", 144, new Position(1763, 5358, 0)),
	SKELETON_BRUTE(SlayerMaster.KURADEL, 6104, "Skeleton Brutes can be found in the Ancient Cavern.", 144, new Position(1788, 5335, 0)),
	AVIANSIE(SlayerMaster.KURADEL, 6246, "Aviansies can be found in the Godwars Dungeon.", 146, new Position(2868, 5268, 2)),
	FROST_DRAGON(SlayerMaster.KURADEL, 51, "Frost Dragons can be found in the deepest of Wilderness.", 225, new Position(2968, 3902, 0)),
	ANGRY_BARBARIAN_SPIRIT(SlayerMaster.KURADEL, 749, "Angry Barbarian Spirits are found in the Ancient Cavern.", 155, new Position(1749, 5337, 0)),
	BRUTAL_GREEN_DRAGON(SlayerMaster.KURADEL, 5362, "Brutal Green Drags are found in the Ancient Cavern.", 210, new Position(1762, 5323, 0)),
	LOST_BARBARIAN(SlayerMaster.KURADEL, 6102, "Angry Barbarians are found in the Ancient Cavern.", 137, new Position(1765, 5344, 0)),
	ABYSSAL_DEMON(SlayerMaster.KURADEL, 1615, "Abyssal Demons are on the top floor of the Slayer Tower.", 137, new Position(3420, 3567, 2)),
	

	/**
	 * Elite
	 */
	NEX(SlayerMaster.SUMONA, 13447, "Nex can be found in the Godwars Dungeon.", 1000, new Position(2903, 5203)),
	GENERAL_GRAARDOR(SlayerMaster.SUMONA, 6260, "General Graardor can be found in the Godwars Dungeon.", 680, new Position(2863, 5354, 2)),
	TORMENTED_DEMON(SlayerMaster.SUMONA, 8349, "Tormented Demons can be found using the Boss teleport.", 400, new Position(2717, 9805, 0)),
	KING_BLACK_DRAGON(SlayerMaster.SUMONA, 50, "The King Black Dragon can be found using the Boss teleport.", 260, new Position(2273, 4681, 0)),
	DAGANNOTH_SUPREME(SlayerMaster.SUMONA, 2881, "The Dagannoth Kings can be found using the Boss teleport.", 260, new Position(1908, 4367, 0)),
	DAGANNOTH_REX(SlayerMaster.SUMONA, 2883, "The Dagannoth Kings can be found using the Boss teleport.", 260, new Position(1908, 4367, 0)),
	DAGANNOTH_PRIME(SlayerMaster.SUMONA, 2882, "The Dagannoth Kings can be found using the Boss teleport.", 260, new Position(1908, 4367, 0)),
	CHAOS_ELEMENTAL(SlayerMaster.SUMONA, 3200, "The Chaos Elemental can be found using the Boss teleport.", 580, new Position(3285, 3921, 0)),
	SLASH_BASH(SlayerMaster.SUMONA, 2060, "Slash Bash can be found using the Boss teleport.", 280, new Position(2547, 9448, 0)),
	KALPHITE_QUEEN(SlayerMaster.SUMONA, 1160, "The Kalphite Queen can be found using the Boss teleport.", 310, new Position(3476, 9502, 0)),
	PHOENIX(SlayerMaster.SUMONA, 8549, "The Phoenix can be found using the Boss teleport.", 210, new Position(2839, 9557, 0)),
	CORPOREAL_BEAST(SlayerMaster.SUMONA, 8133, "The Corporeal Beast can be found using the Boss teleport.", 800, new Position(2885, 4375, 0)),
	//BANDOS_AVATAR(SlayerMaster.SUMONA, 4540, "The Bandos Avatar can be found using the Boss teleport.", 34000, new Position(2891, 4767)),
	CALLISTO(SlayerMaster.SUMONA, 2009, "Callisto can be found using the Boss teleport.", 575, new Position(3163, 3796, 0)),
	VETION(SlayerMaster.SUMONA, 2006, "Vet'ion can be found using the Boss teleport.", 615, new Position(3009, 3767, 0)),
	VENENATIS(SlayerMaster.SUMONA, 2000, "Venenatis can be found using the Boss teleport.", 592, new Position(3005, 3732, 0)),
	SCORPIA(SlayerMaster.SUMONA, 2001, "Scorpia can be found using the Boss teleport.", 605, new Position(2849, 9640, 0)),
	ZULRAH(SlayerMaster.SUMONA, 2042, "Zulrah can be found in Oldschool Boss teleports.", 605, new Position(3406, 2794, 0)) //hax for 2042, 2044, 2043 || 
	;

	;
	/*
	 * @param taskMaster
	 * @param npcId
	 * @param npcLocation
	 * @param XP
	 * @param taskPosition
	 */

	private SlayerTasks(SlayerMaster taskMaster, int npcId, String npcLocation, int XP, Position taskPosition) {
		this.taskMaster = taskMaster;
		this.npcId = npcId;
		this.npcLocation = npcLocation;
		this.XP = XP;
		this.taskPosition = taskPosition;
	}

	private SlayerMaster taskMaster;
	private int npcId;
	private String npcLocation;
	private int XP;
	private Position taskPosition;

	public SlayerMaster getTaskMaster() {
		return this.taskMaster;
	}

	public int getNpcId() {
		return this.npcId;
	}

	public String getNpcLocation() {
		return this.npcLocation;
	}

	public int getXP() {
		return this.XP;
	}

	public Position getTaskPosition() {
		return this.taskPosition;
	}

	public static SlayerTasks forId(int id) {
		for (SlayerTasks tasks : SlayerTasks.values()) {
			if (tasks.ordinal() == id) {
				return tasks;
			}
		}
		return null;
	}

	public static int[] getNewTaskData(SlayerMaster master) {
		int slayerTaskId = 1, slayerTaskAmount = 20;
		int easyTasks = 0, mediumTasks = 0, hardTasks = 0, eliteTasks = 0;

		/*
		 * Calculating amount of tasks
		 */
		for(SlayerTasks task: SlayerTasks.values()) {
			if(task.getTaskMaster() == SlayerMaster.VANNAKA)
				easyTasks++;
			else if(task.getTaskMaster() == SlayerMaster.DURADEL) 
				mediumTasks++;
			else if(task.getTaskMaster() == SlayerMaster.KURADEL) 
				hardTasks++;
			else if(task.getTaskMaster() == SlayerMaster.SUMONA)
				eliteTasks++;
		}

		/*
		 * Getting a task
		 */
		if(master == SlayerMaster.VANNAKA) {
			slayerTaskId = 1 + Misc.getRandom(easyTasks);
			if(slayerTaskId > easyTasks)
				slayerTaskId = easyTasks;
			slayerTaskAmount = 15 + Misc.getRandom(15);
		} else if(master == SlayerMaster.DURADEL) {
			slayerTaskId = easyTasks - 1 + Misc.getRandom(mediumTasks);
			slayerTaskAmount = 12 + Misc.getRandom(13);
		} else if(master == SlayerMaster.KURADEL) {
			slayerTaskId = 1 + easyTasks + mediumTasks + Misc.getRandom(hardTasks - 1);
			slayerTaskAmount = 10 + Misc.getRandom(15);
		} else if(master == SlayerMaster.SUMONA) {
			slayerTaskId = 1 + easyTasks + mediumTasks + hardTasks + Misc.getRandom(eliteTasks - 1);
			slayerTaskAmount = 2 + Misc.getRandom(7);
		}
		return new int[] {slayerTaskId, slayerTaskAmount};
	}
}
