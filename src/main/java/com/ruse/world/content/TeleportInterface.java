package com.ruse.world.content;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportLocations;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.impl.player.Player;

public class TeleportInterface {

	public static String[] CATEGORIES = {"Cities", "Monsters", "Dungeons", "Bosses", "Minigames", "Wilderness"};
	public static int[] PAGEOPTIONS = {44202, 44402, 44602, 44802, 45002, 45202};
	public static int[] PAGEBUTTONS = {-21434, -21234, -21034, -20834, -20634, -20434};
	
	public static int INTERFACE_ID = 44000, 
			DESCRIPTION_TITLE = 44086,
			TITLE = 44005,
			CATEGORY_ID = 44072,
			NEW_CATEGORY_ID = 44048,
			TELEPORTBUTTON = 44096,
			PREV_BUTTON = 44089;
			
	public static int DESCRIPTION[] = { 44087, 44088 };
	public static int PREVIOUS[] = { 44090, 44091, 44092, 44098 };
	
	public static boolean handleButton(int id) {
		if (id >= -21534 && id <= -20428) {
			return true;
		}
		return false;
	}

	public static void open(Player player) {
		resetInterface(player);
		player.getPacketSender().sendInterface(INTERFACE_ID);
	}
	public static void resetInterface(Player player) {
		if (player.getTeleportInterfaceData() != null) {
			player.setTeleportInterfaceData(null);
		}
		player.getPacketSender().sendString(TITLE, "Necrotic Teleports");
		player.getPacketSender().sendString(TELEPORTBUTTON, "Teleport");
		for (int i = 0; i < CATEGORIES.length; i++) {
			player.getPacketSender().sendString(NEW_CATEGORY_ID+i, CATEGORIES[i]);
		}
		player.getPacketSender().sendString(DESCRIPTION_TITLE, "Select a teleport!");
		player.getPacketSender().sendString(DESCRIPTION[0], "Choose a category from the left.");
		player.getPacketSender().sendString(DESCRIPTION[1], "Then an option from the right.");
		player.getPacketSender().sendString(PREV_BUTTON, "History");
		populatePrevious(player);
		
		for (int i = 0; i < TeleportInterfaceData.values().length; i++) {
			player.getPacketSender().sendString(TeleportInterfaceData.values()[i].getStringId(), TeleportInterfaceData.values()[i].getDescriptionTitle());
		}
	}
	
	public static void populatePrevious(Player player) {
		lengthFix(player);
		for (int i = 0; i < player.getPreviousTeleports().length; i++) {
			if (player.getPreviousTeleportsIndex(i) < 0 && TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(i)) != null) {
				player.getPacketSender().sendString(PREVIOUS[i], TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(i)).getDescriptionTitle());
			} else {
				player.getPacketSender().sendString(PREVIOUS[i], "");
			}
		}
	}

	
	public static void lengthFix(Player player) {
		int[] fix = { 0, 0, 0, 0};
		if (player.getPreviousTeleports().length < fix.length) { //this fixes players who had less then 4 from the previous bug
			player.setPreviousTeleports(fix);
			player.getPacketSender().sendMessage("<img=10> A error has been detected in your teleport history.");
			player.getPacketSender().sendMessage("<img=10> To avoid damage, your previous teleports have been reset.");
		}
	}
	
	public static void updatePrevious(Player player, int newest) {		
		boolean duplicate = false;
		int[] orig = player.getPreviousTeleports();
		
		for (int i = 0; i < orig.length; i++) {
			if (orig[i] == newest) {
				duplicate = true;
				break;
			}
		}
		
		if (duplicate) {
			
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(newest);
			list.add(player.getPreviousTeleportsIndex(0));
			list.add(player.getPreviousTeleportsIndex(1));
			list.add(player.getPreviousTeleportsIndex(2));
			list.add(player.getPreviousTeleportsIndex(3));
			Set<Integer> hs = new LinkedHashSet<>();
			hs.addAll(list);
			list.clear();
			list.addAll(hs);
			int[] arr = list.stream().filter(i -> i != null).mapToInt(i -> i).toArray();
			player.setPreviousTeleports(arr);
			
		} else {
			
			int[] newstuff = { newest, player.getPreviousTeleportsIndex(0), player.getPreviousTeleportsIndex(1),
					player.getPreviousTeleportsIndex(2) };
			player.setPreviousTeleports(newstuff);

		}
		
		populatePrevious(player);
	}
	
	public static void updateDescription(Player player, TeleportInterfaceData data) {
		player.getPacketSender().sendString(DESCRIPTION_TITLE, data.getDescriptionTitle());
		player.getPacketSender().sendString(DESCRIPTION[0], data.getDescriptionText1());
		player.getPacketSender().sendString(DESCRIPTION[1], data.getDescriptionText2());
	}
	
	public static void handleButtonClick(Player player, int button) {
		switch (button) {
		case -21534: //exit button
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case -21443: //teleport button
			if (player.getTeleportInterfaceData() == null) {
				player.getPacketSender().sendMessage("Please select a teleport destination first.");
				return;
			} else {
				TeleportHandler.teleportPlayer(player, player.getTeleportInterfaceData().getDestination().getPos(), TeleportType.NORMAL);
				if (player.getTeleportInterfaceData().getDestination().getHint() != null) {
					String txt = player.getTeleportInterfaceData().getDestination().getHint();
					if (!txt.equalsIgnoreCase("") && !txt.equalsIgnoreCase(" ") && txt.length() > 0) {
						player.getPacketSender().sendMessage(txt);
					}
				}
				updatePrevious(player, player.getTeleportInterfaceData().getButtonId());
			}
			break;
		case -21446: //previous #1
			if (player.getPreviousTeleportsIndex(0) < 0) {
				player.setTeleportInterfaceData(TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(0)));
				updateDescription(player, TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(0)));
			}
			break;
		case -21445: // previous #2
			if (player.getPreviousTeleportsIndex(1) < 0) {
				player.setTeleportInterfaceData(TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(1)));
				updateDescription(player, TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(1)));
			}
			break;
		case -21444: // previous #3
			if (player.getPreviousTeleportsIndex(2) < 0) {
				player.setTeleportInterfaceData(TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(2)));
				updateDescription(player, TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(2)));
			}
			break;
		case -21438: // previous #4
			if (player.getPreviousTeleportsIndex(3) < 0) {
				player.setTeleportInterfaceData(TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(3)));
				updateDescription(player, TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(3)));
			}
			break;
		}
		
		//player.getPacketSender().sendInterfaceSpriteChange(44052, 1201, 1202);
		
		for (int i = 0; i < TeleportInterfaceData.values().length; i++) {
			
			if (TeleportInterfaceData.values()[i].getButtonId() == button) {
				updateDescription(player, TeleportInterfaceData.forButtonId(button));
				player.setTeleportInterfaceData(TeleportInterfaceData.values()[i]);
				break;
			} 
			
		}
	}
	
	public static enum TeleportInterfaceData {
		ZERO(0, "Select a teleport!", "Choose a category from the left.", "Then an option from the right.", TeleportLocations.HOME, 0),
		/* Cities */
		AL_KHARID(PAGEOPTIONS[0], "Al Kharid", "A northern city of the Kharidian Desert.", "Ruled by the Emir.", TeleportLocations.AL_KHARID, PAGEBUTTONS[0]),
		ARDOUGNE(PAGEOPTIONS[0]+1, "Ardougne", "The capital of the Kingdom of Kandarin.", "The West is infected with a plague.", TeleportLocations.ARDOUGNE, PAGEBUTTONS[0]+1),
		CAMELOT(PAGEOPTIONS[0]+2, "Camelot", "A giant castle, and home of King Arthur.", "Just a stone's throw east of Seers' Village.", TeleportLocations.CAMELOT, PAGEBUTTONS[0]+2),
		CANIFIS(PAGEOPTIONS[0]+3, "Canifis", "A small town in Morytania, east of Varrock.", "The town is inhabited by werewolves!", TeleportLocations.CANIFIS, PAGEBUTTONS[0]+3),
		DRAYNOR(PAGEOPTIONS[0]+4, "Draynor", "A village between Falador and Lumbridge.", "Part of the Kingdom of Misthalin.", TeleportLocations.DRAYNOR, PAGEBUTTONS[0]+4),
		EDGEVILLE(PAGEOPTIONS[0]+5, "Edgeville", "A small town at the edge of the Wilderness.", "A popular spot of PKers to gather.", TeleportLocations.EDGEVILLE, PAGEBUTTONS[0]+5),
		FALADOR(PAGEOPTIONS[0]+6, "Falador", "The capital city of Asgarnia.", "Home of the white knights.", TeleportLocations.FALADOR, PAGEBUTTONS[0]+6),
		HOME(PAGEOPTIONS[0]+7, "Home", "Located on the island of Mos Le'Harmless.", "Everything you need in a central spot.", TeleportLocations.HOME, PAGEBUTTONS[0]+7),
		VARROCK(PAGEOPTIONS[0]+8, "Varrock", "The large capital city of Misthalin.", "Home of many heros.", TeleportLocations.VARROCK, PAGEBUTTONS[0]+8),
		KARAMJA(PAGEOPTIONS[0]+9, "Karamja", "The largest island in Gielinor.", "Populated with all sorts of creatures.", TeleportLocations.KARAMJA, PAGEBUTTONS[0]+9),
		LUMBRIDGE(PAGEOPTIONS[0]+10, "Lumbridge", "Known for a bridge crossing the River Lum.", "Popular among low levels.", TeleportLocations.LUMBRIDGE, PAGEBUTTONS[0]+10),
		YANILLE(PAGEOPTIONS[0]+11, "Yanille", "Lies to the south of Ardougne.", "Heavily defended by guards.", TeleportLocations.YANILLE, PAGEBUTTONS[0]+11),
		GRAND_EXCHANGE(PAGEOPTIONS[0]+12, "Grand Exchange", "A popular trade spot in Varrock.", "The central bank for player items.", TeleportLocations.TRADE, PAGEBUTTONS[0]+12),
		CHILL(PAGEOPTIONS[0]+13, "Chill", "An icy mountain away from most folk.", "A hearty place to relax.", TeleportLocations.CHILL, PAGEBUTTONS[0]+13),
		/* Monsters */
		ROCK_CRABS(PAGEOPTIONS[1], "Rock Crabs", "Level: 13", "@gre@Non-wilderness.", TeleportLocations.ROCK_CRABS, PAGEBUTTONS[1]),
		EXPERIMENTS(PAGEOPTIONS[1]+1, "Experiments", "Level: 25", "@gre@Non-wilderness.", TeleportLocations.EXPERIMENTS, PAGEBUTTONS[1]+1),
		YAKS(PAGEOPTIONS[1]+2, "Yaks", "Level: 22", "@gre@Non-wilderness.", TeleportLocations.YAKS, PAGEBUTTONS[1]+2),
		BANDITS(PAGEOPTIONS[1]+3, "Bandits", "Level: 56", "@gre@Non-wilderness.", TeleportLocations.BANDITS, PAGEBUTTONS[1]+3),
		GHOULS(PAGEOPTIONS[1]+4, "Ghouls", "Level: 42", "@gre@Non-wilderness.", TeleportLocations.GHOULS, PAGEBUTTONS[1]+4),
		CHAOS_DRUIDS(PAGEOPTIONS[1]+5, "Chaos Druids", "Level: 13", "@gre@Non-wilderness.", TeleportLocations.CHAOS_DRUIDS, PAGEBUTTONS[1]+5),
		GOBLINS(PAGEOPTIONS[1]+6, "Goblins", "Level: 2", "@gre@Non-wilderness.", TeleportLocations.GOBLINS, PAGEBUTTONS[1]+6),
		DUST_DEVILS(PAGEOPTIONS[1]+7, "Dust Devils", "Level: 93", "@gre@Non-wilderness.", TeleportLocations.DUST_DEVILS, PAGEBUTTONS[1]+7),
		CHICKENS(PAGEOPTIONS[1]+8, "Chickens", "Level: 1", "@gre@Non-wilderness.", TeleportLocations.CHICKENS, PAGEBUTTONS[1]+8),
		MONKEY_SKELETONS(PAGEOPTIONS[1]+9, "Monkey Skeletons", "Level: 142", "@gre@Non-wilderness.", TeleportLocations.MONKEY_SKELETONS, PAGEBUTTONS[1]+9),
		MONKEY_GUARDS(PAGEOPTIONS[1]+10, "Monkey Guards", "Level: 167", "@gre@Non-wilderness.", TeleportLocations.MONKEY_GUARDS, PAGEBUTTONS[1]+10),
		ARMOURED_ZOMBIES(PAGEOPTIONS[1]+11, "Armoured Zombies", "Level: 85", "@gre@Non-wilderness.", TeleportLocations.ARMOURED_ZOMBIES, PAGEBUTTONS[1]+11),
		/* Dungeons */
		EDGE_DUNG(PAGEOPTIONS[2], "Edgeville Dungeon", "A low level dungeon.", "Holds a few slayer assignments.", TeleportLocations.EDGE_DUNGEON, PAGEBUTTONS[2]),
		SLAYER_TOWER(PAGEOPTIONS[2]+1, "Slayer Tower", "A slayer specific area.", "Filled with low to high level monsters.", TeleportLocations.SLAYER_TOWER, PAGEBUTTONS[2]+1),
		BRIMHAVEN_DUNG(PAGEOPTIONS[2]+2, "Brimhaven Dungeon", "A medium to high level dungeon.", "Has a lot of monsters and tasks.", TeleportLocations.BRIMHAVEN_DUNGEON, PAGEBUTTONS[2]+2),
		TAVERLY_DUNG(PAGEOPTIONS[2]+3, "Taverly Dungeon", "A low to medium level dungeon.", "Has a few slayer tasks.", TeleportLocations.TAVERLY_DUNGEON, PAGEBUTTONS[2]+3),
		GODWARS_DUNG(PAGEOPTIONS[2]+4, "God wars Dungeon", "A high level dungeon.", "Full of godly incarnations.", TeleportLocations.GODWARS_DUNGEON, PAGEBUTTONS[2]+4),
		STRYKEWYRM_CAVERN(PAGEOPTIONS[2]+5, "Strykewyrm Cavern", "A high level dungeon.", "Only contains strykewyrms.", TeleportLocations.STRYKEWYRM_CAVERN, PAGEBUTTONS[2]+5),
		ANCIENT_CAVERN(PAGEOPTIONS[2]+6, "Ancient Cavern", "A high level dungeon.", "Holds ancient barbarian tasks.", TeleportLocations.ANCIENT_CAVERN, PAGEBUTTONS[2]+6),
		CHAOS_TUNNELS(PAGEOPTIONS[2]+7, "Chaos Tunnels", "A low to high level dungeon.", "(Pending completion) Has a few tasks.", TeleportLocations.CHAOS_TUNNELS, PAGEBUTTONS[2]+7),
		/* Bosses */
		GODWARS(PAGEOPTIONS[3], "God wars", "Home to each god's champion.", "Lots of unique drops available.", TeleportLocations.GWD, PAGEBUTTONS[3]),
		DAGANNOTH_KINGS(PAGEOPTIONS[3]+1, "Dagannoth Kings", "The Dagannoth lair, containing the kings.", "Medium to high level content.", TeleportLocations.DAGKINGS, PAGEBUTTONS[3]+1),
		FROST_DRAGONS(PAGEOPTIONS[3]+2, "Frost Dragons", "Popular for their frostdragon bones.", "@red@Warning: Level 46 Wilderness!", TeleportLocations.FROSTDRAGONSWILDY, PAGEBUTTONS[3]+2),
		TORMENTED_DEMONS(PAGEOPTIONS[3]+3, "Tormented Demons", "Have a chance to drop Dragon claws.", "Also have a chance to kill you.", TeleportLocations.TORMENTEDDEMONS, PAGEBUTTONS[3]+3),
		KING_BLACK_DRAGON(PAGEOPTIONS[3]+4, "King Black Drag", "The King of black dragons.", "@gre@Non-wilderness.", TeleportLocations.KBD, PAGEBUTTONS[3]+4),
		CHAOS_ELEMENTAL(PAGEOPTIONS[3]+5, "Chaos Elemental", "An unnerving entity.", "@red@Warning: Level 50 Wilderness!", TeleportLocations.CHAOSELE, PAGEBUTTONS[3]+5),
		SLASHBASH(PAGEOPTIONS[3]+6, "Slash Bash", "An angry ogre in his cave.", "His flesh is rotting off!", TeleportLocations.SLASHBASH, PAGEBUTTONS[3]+6),
		KALPHITE_QUEEN(PAGEOPTIONS[3]+7, "Kalphite Queen", "Queen of the Kalphites.", "This boss has multiple forms!", TeleportLocations.KQ, PAGEBUTTONS[3]+7),
		PHOENIX(PAGEOPTIONS[3]+8, "Phoenix", "The flying infero.", "Beatiful and deadly.", TeleportLocations.PHOENIX, PAGEBUTTONS[3]+8),
		BANDOS_AVATAR(PAGEOPTIONS[3]+9, "Bandos Avatar", "A avatar created by the power of Bandos.", "Incredibly lethal.", TeleportLocations.BANDOSAVATAR, PAGEBUTTONS[3]+9),
		GLACORS(PAGEOPTIONS[3]+10, "Glacors", "Frozen shells of their former selves.", "Coveted for their boot drops.", TeleportLocations.GLACORS, PAGEBUTTONS[3]+10),
		CORPERAL_BEAST(PAGEOPTIONS[3]+11, "Corporeal Beast", "Guardian of the spirit shields.", "Very strong, be careful!", TeleportLocations.CORPBEAST, PAGEBUTTONS[3]+11),
		NEX(PAGEOPTIONS[3]+12, "Nex", "An ancient creature and Zarosian.", "Drops ancient armor.", TeleportLocations.NEX, PAGEBUTTONS[3]+12),
		CALLISTO(PAGEOPTIONS[3]+13, "Callisto", "A chaotic bear.", "@red@Warning: Level 35 Wilderness.", TeleportLocations.CALLISTO, PAGEBUTTONS[3]+13),
		VET_ION(PAGEOPTIONS[3]+14, "Vet'ion", "An old, demented soul.", "@red@Warning: Level 31 Wilderness.", TeleportLocations.VETION, PAGEBUTTONS[3]+14),
		VENENATIS(PAGEOPTIONS[3]+15, "Venenatis", "A terrible spider.", "Warning: Level 27 Wilderness.", TeleportLocations.VENENATIS, PAGEBUTTONS[3]+15),
		ZULRAH(PAGEOPTIONS[3]+16, "Zulrah", "Our custom take on Zulrah. Be careful!", "Keep items on death.", TeleportLocations.ZULRAH, PAGEBUTTONS[3]+16),
		KRAKEN(PAGEOPTIONS[3]+17, "Kraken", "A monster of the sea.", "@gre@Non-wilderness.", TeleportLocations.KRAKEN, PAGEBUTTONS[3]+17),
		SCORPIA(PAGEOPTIONS[3]+18, "Scorpia", "Queen of the scorpions.", "@gre@Non-wilderness.", TeleportLocations.SCORPIA, PAGEBUTTONS[3]+18),
		/* Minigames */
		DUEL_ARENA(PAGEOPTIONS[4], "Duel Arena", "One will leave with supreme wealth.", "The other will leave with nothing.", TeleportLocations.DUELARENA, PAGEBUTTONS[4]),
		BARROWS(PAGEOPTIONS[4]+1, "Barrows", "Home of the ancient barrows brothers.", "@red@Warning: RoT6 is LOSE ITEMS ON DEATH.", TeleportLocations.BARROWS, PAGEBUTTONS[4]+1),
		PEST_CONTROL(PAGEOPTIONS[4]+2, "Pest Control", "A defense outpost of the Void knights.", "Rewards powerful gear.", TeleportLocations.PESTCONTROL, PAGEBUTTONS[4]+2),
		WARRIOR_GUILD(PAGEOPTIONS[4]+3, "Warrior's Guild", "Home of great warriors.", "A place to gather defenders.", TeleportLocations.WARRIORSGUILD, PAGEBUTTONS[4]+3),
		FIGHT_CAVE(PAGEOPTIONS[4]+4, "Fight Caves", "Prove yourself against Jad.", "Only one (Jad) wave.", TeleportLocations.FIGHTCAVE, PAGEBUTTONS[4]+4),
		FIGHT_PIT(PAGEOPTIONS[4]+5, "Fight Pits", "PvP against others, free-style.", "No rewards.", TeleportLocations.FIGHTPIT, PAGEBUTTONS[4]+5),
		GRAVEYARD(PAGEOPTIONS[4]+6, "Graveyard", "Take on the zombie masses.", "Mid level rewards.", TeleportLocations.GRAVEYARD, PAGEBUTTONS[4]+6),
		/* Wilderness */
		EDGE_DITCH(PAGEOPTIONS[5], "Edgeville Ditch", "To the edge of the wilderness.", "@gre@Safe teleport.", TeleportLocations.EDGEVILLEDITCH, PAGEBUTTONS[5]),
		MAGE_BANK(PAGEOPTIONS[5]+1, "Mage Bank", "Home to some of the most chaotic mages.", "@gre@Safe teleport.", TeleportLocations.MAGEBANK_SAFE, PAGEBUTTONS[5]+1),
		WEST_DRAGONS(PAGEOPTIONS[5]+2, "West Dragons", "Popular for PvM and PvPers.", "@red@Warning: Level 10 Wilderness!", TeleportLocations.EDGEWESTDRAGONS, PAGEBUTTONS[5]+2),
		EAST_DRAGONS(PAGEOPTIONS[5]+3, "East Dragons", "Popular for PvM and PvPers.", "@red@Warning: Level 23 Wilderness!", TeleportLocations.EDGEEASTDRAGONS, PAGEBUTTONS[5]+3),
		CHAOS_ALTAR(PAGEOPTIONS[5]+4, "Chaos Altar", "A chaotic altar surrounded by lava.", "@red@Warning: Level 13 Wilderness!", TeleportLocations.CHAOSALTAR, PAGEBUTTONS[5]+4),
		DEMONIC_RUINS(PAGEOPTIONS[5]+5, "Demonic Ruins", "Once a temple, mangled by demons.", "@red@Warning: Level 46 Wilderness!", TeleportLocations.DEMONIC_RUINS, PAGEBUTTONS[5]+5),
		GHOST_TOWN(PAGEOPTIONS[5]+6, "Ghost Town", "A town of revenants in deep wilderness.", "@gre@Safe teleport.", TeleportLocations.GHOSTTOWN, PAGEBUTTONS[5]+6)
		;
		
		int stringId; String descriptionTitle; String descriptionText1; String descriptionText2; TeleportLocations destination; int buttonId;
		
		TeleportInterfaceData(int stringId, String descriptionTitle, String descriptionText1, String descriptionText2, TeleportLocations destination, int buttonId) {
			this.stringId = stringId;
			this.descriptionTitle = descriptionTitle;
			this.descriptionText1 = descriptionText1;
			this.descriptionText2 = descriptionText2;
			this.destination = destination;
			this.buttonId = buttonId;
		}
		public int getStringId() {
			return this.stringId;
		}
		public String getDescriptionTitle() {
			return this.descriptionTitle;
		}
		public String getDescriptionText1() {
			return this.descriptionText1;
		}
		public String getDescriptionText2() {
			return this.descriptionText2;
		}
		public TeleportLocations getDestination() {
			return this.destination;
		}
		public int getButtonId() {
			return this.buttonId;
		}
		
		public static TeleportInterfaceData forTitle(String title) {
			for(TeleportInterfaceData data: TeleportInterfaceData.values()) {
				if(data.getDescriptionTitle().equalsIgnoreCase(title)) {
					return data;
				}
			}
			return null;
		}
		
		public static TeleportInterfaceData forButtonId(int button) {
			for(TeleportInterfaceData data: TeleportInterfaceData.values()) {
				if(data.getButtonId() == button) {
					return data;
				}
			}
			return null;
		}
		
		
	}

}
