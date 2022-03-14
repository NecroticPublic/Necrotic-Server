package com.ruse.world.content.holidayevents;

public enum easter2017data {

	CHEST(11339, "The first egg is hidden in treasure.", "Chest west of home", "Amongst the golden coin, is a pretty egg.", 1),
	CRYSTAL_BALL(589, "A strange old woman's crystal ball.", "Varrock's gypsy", "You find a magnet, and easter egg under the long cloth.", 2),
	BARBER_POLE(11678, "Looks like candy, but spins.", "Falador barbershop's sign", "You search the pole and find a pretty egg behind it!", 3),
	TOY_STALL(5595, "Filled with toys for children.", "Draynor toy stall", "In between the toys, you find a delicious treat!", 4),
	FIREPLACE(2725, "In the hottest part of the party.", "Party room fireplace", "On the mantle of the fireplace you find a slightly gooey egg.", 5),
	BED(423, "A straw bed in a hot area.", "Al-kharid bed", "Hidden in the straw bed is a colorful egg.", 6)
	;
	
	int objectId; String hint; String location; String searchmessage; int requiredprogress;
	
	easter2017data(int objectId, String hint, String location, String searchmessage, int requiredprogress) {
		this.objectId = objectId;
		this.hint = hint;
		this.location = location;
		this.searchmessage = searchmessage;
		this.requiredprogress = requiredprogress;
	}
	
	public int getObjectId() {
		return objectId;
	}
	
	public String getHint() {
		return hint;
	}
	
	public String getLocation() {
		return location;
	}

	public int getRequiredProgress() {
		return requiredprogress;
	}
	
	public String getSearchMessage() {
		return "<img=10> "+searchmessage;
	}
	

	public static easter2017data forObjectId(int objectId) {
		for(easter2017data data: easter2017data.values()) {
			if(data.getObjectId() == objectId) {
				return data;
			}
		}
		return null;
	}
	
}
