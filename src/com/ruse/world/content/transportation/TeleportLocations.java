package com.ruse.world.content.transportation;

import com.ruse.GameSettings;
import com.ruse.model.Position;

public enum TeleportLocations {
	
	/**
	 * @Author Crimson
	 * Central location to be used as main repository for in-game cordinates. There's no real reason to have them scattered across a ton of files.
	 */

	//CITIES
	AL_KHARID(new Position(3292, 3176, 0), "Welcome to the sandy Al Kharid."),
	ARDOUGNE(new Position(2662, 3307, 0), "East Ardougne - the city that never sleeps."),
	CAMELOT(new Position(2757, 3477, 0), "Greetings adventurer, welcome to Camelot."),
	CANIFIS(new Position(3496, 3486, 0), "Canifis, an oldschool, spooky town."),
	DRAYNOR(new Position(3105, 3251, 0), "A run-down village, it's seen better days."),
	EDGEVILLE(new Position(3094, 3503, 0), "Welcome to the Edge of the world."),
	FALADOR(new Position(2965, 3379, 0), "Welcome to the White Knight city, traveler."),
	HOME(GameSettings.HOME_CORDS, "Teleporting you home."),
	VARROCK(new Position(3213, 3424, 0), "Greetings, from the kingdom of Varrock."),
	KARAMJA(new Position(2918, 3176, 0), "Enjoy the tropics of Karamja."),
	LUMBRIDGE(new Position(3224, 3219, 0), "Enjoy your visit to Lumbridge, adventurer!"),
	YANILLE(new Position(2607, 3093, 0), "Ah, Yanille - a fortress in a class of it's own."),
	
	//MONSTERS
    ROCK_CRABS(new Position(2679, 3717, 0), ""),
    EXPERIMENTS(new Position(3558, 9948, 0), ""),
    YAKS(new Position(3204, 3264, 0), ""),
    BANDITS(new Position(3171, 2981, 0), ""),
    GHOULS(new Position(3420, 3510, 0), ""),
    CHAOS_DRUIDS(new Position(2931, 9846, 0), ""),
    GOBLINS(new Position(3259, 3228, 0), ""),
    DUST_DEVILS(new Position(3279, 2964, 0), ""),
    CHICKENS(new Position(3235, 3295, 0), ""),
    MONKEY_SKELETONS(new Position(2802, 9148, 0), ""),
    MONKEY_GUARDS(new Position(2793, 2773, 0), ""),
    ARMOURED_ZOMBIES(new Position(3085, 9672, 0), ""),
    
    //DUNGEONS
    EDGE_DUNGEON(new Position(3097, 9870, 0), ""),
    SLAYER_TOWER(new Position(3429, 3538, 0), ""),
    BRIMHAVEN_DUNGEON(new Position(2713, 9564, 0), ""),
    TAVERLY_DUNGEON(new Position(2884, 9797, 0), ""),
    GODWARS_DUNGEON(new Position(2871, 5318, 2), ""),
    STRYKEWYRM_CAVERN(new Position(2731, 5095, 0), ""),
	ANCIENT_CAVERN(new Position(1746, 5325, 0), ""),
    CHAOS_TUNNELS(new Position(3184, 5471, 0), ""),
  	
	//MODERN BOSSES
	GWD(new Position(2871, 5318, 2), "Welcome - to the God Wars."),
	DAGKINGS(new Position(1908, 4367, 0), ""),
	FROSTDRAGONSWILDY(new Position(2961, 3882, 0), ""),
	TORMENTEDDEMONS(new Position(2717, 9805, 0), ""),
	KBD(new Position(2273, 4681, 0), ""),
	CHAOSELE(new Position(3281, 3914, 0), ""),
	SLASHBASH(new Position(2547, 9448, 0), ""),
	KQ(new Position(3476, 9502, 0), ""),
	PHOENIX(new Position(2839, 9557, 0), ""),
	BANDOSAVATAR(new Position(2368, 4949, 0), ""),
	GLACORS(new Position(3050, 9573, 0), ""),
	CORPBEAST(new Position(2900, 4384, 0), ""),
	NEX(new Position(2903, 5204, 0), ""),
    //OLDSCHOOL BOSSES
  	CALLISTO(new Position(3163, 3796, 0), ""),
  	VETION(new Position(3009, 3767, 0), ""),
  	VENENATIS(new Position(3005, 3732, 0), ""),
  	ZULRAH(new Position(3406, 2794, 0), ""),
  	KRAKEN(new Position(3683, 9888, 0), ""),
  	SCORPIA(new Position(2849, 9640, 0), ""),
	
	//MINIGAME TELEPORTS
	WARRIORSGUILD(new Position(2855, 3543, 0), ""),
	PESTCONTROL(new Position(2663, 2656, 0), ""),
	DUELARENA(new Position(3372, 3269, 0), ""),
	BARROWS(new Position(3565, 3313, 0), ""),
	FIGHTCAVE(new Position(2441, 5173, 0), ""),
	FIGHTPIT(new Position(2399, 5177, 0), ""),
	GRAVEYARD(new Position(3503, 3562, 0), ""),
	
	//WILDERNESS TELEPORTS
	EDGEVILLEDITCH(new Position(3087, 3517, 0), ""),
	MAGEBANK_WILDY(new Position(3090, 3956, 0), ""),
	MAGEBANK_SAFE(new Position(2539, 4712, 0), ""),
	EDGEWESTDRAGONS(new Position(2981, 3597, 0), ""),
	CHAOSALTAR(new Position(3241, 3620, 0), ""),
	EDGEEASTDRAGONS(new Position(3331, 3661, 0), ""),
	GHOSTTOWN(new Position(3651, 3486, 0), ""),
	DEMONIC_RUINS(new Position(3287, 3888, 0), ""),
	
	//COMMANDS
	DZONE(new Position(2851, 3348, 0), ""),
	SHOPS(new Position(3690, 2977, 0), ""),
	TRADE(new Position(3164, 3485, 0), ""),
	CHILL(new Position(2856, 3812, 1), "Oooh, it's quite cold up here."),




	;
	
	private Position pos;
	private String hint;
	
	private TeleportLocations(Position pos, String hint) {
		this.pos = pos;
		this.hint = hint;
	}
	
	public Position getPos() {
		return this.pos;
	}
	
	public String getHint() {
		return this.hint;
	}

}
