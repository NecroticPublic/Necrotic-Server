package com.ruse.world.content.transportation;

import com.ruse.GameSettings;
import com.ruse.model.Position;

public enum CityTeleports {

	AL_KHARID(new Position(3292, 3176), "Welcome to the sandy Al Kharid."),
	ARDOUGNE(new Position(2662, 3307), "East Ardougne - the city that never sleeps."),
	CAMELOT(new Position(2757, 3477), "Greetings adventurer, welcome to Camelot."),
	CANIFIS(new Position(3496, 3486), "Canifis, an oldschool, spooky town."),
	DRAYNOR(new Position(3105, 3251), "A run-down village, it's seen better days."),
	EDGEVILLE(new Position(3094, 3503), "Welcome to the Edge of the world."),
	FALADOR(new Position(2965, 3379), "Welcome to the White Knight city, traveler."),
	HOME(GameSettings.HOME_CORDS, "Teleporting you home."),
	VARROCK(new Position(3213, 3424), "Greetings, from the kingdom of Varrock."),
	KARAMJA(new Position(2918, 3176), "Enjoy the tropics of Karamja."),
	LUMBRIDGE(new Position(3224, 3219), "Enjoy your visit to Lumbridge, adventurer!"),
	YANILLE(new Position(2607, 3093), "Ah, Yanille - a fortress in a class of it's own.")
	//SWAG(new Position(1, 1), ""),
	;
	
	private Position pos;
	private String hint;
	
	private CityTeleports(Position pos, String hint) {
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