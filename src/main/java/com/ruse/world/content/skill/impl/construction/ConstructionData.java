package com.ruse.world.content.skill.impl.construction;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.definitions.GameObjectDefinition;
import com.ruse.world.entity.impl.player.Player;
/**
 * 
 * @author Owner Blade
 * Edited by Gabbe, fixed all wrong requirements etc
 */
public class ConstructionData {
	

	
	public static final int EMPTY = 0, BUILDABLE = 1, GARDEN = 2, PARLOUR = 3,
			KITCHEN = 4, DINING_ROOM = 5, WORKSHOP = 6, BEDROOM = 7,
			SKILL_ROOM = 8, QUEST_HALL_DOWN = 9, GAMES_ROOM = 10,
			COMBAT_ROOM = 11, QUEST_ROOM = 12, MENAGERY = 13, STUDY = 14,
			COSTUME_ROOM = 15, CHAPEL = 16, PORTAL_ROOM = 17,
			FORMAL_GARDEN = 18, THRONE_ROOM = 19, OUBLIETTE = 20, PIT = 21,
			DUNGEON_STAIR_ROOM = 22, TREASURE_ROOM = 23, CORRIDOR = 24,
			JUNCTION = 25, SKILL_HALL_DOWN = 26, ROOF = 27, DUNGEON_EMPTY = 28,
			
			BASE_X = 1856, BASE_Y = 5056, MIDDLE_X = 1912, MIDDLE_Y = 5112;
	
	public static final int[] DOORSPACEIDS = new int[] {15314, 15313, 15305, 15306,15317};
	public static enum Portals
	{
		VARROCK(1, new Position(ConstructionConstants.VARROCK_X, ConstructionConstants.VARROCK_Y), 25,
				new int[][] {{563, 100}, {554, 100/*fire*/}, {556, 300 /*Air*/}}, new int[] {13615, 13622, 13629}),
		LUMBRIDGE(2, new Position(ConstructionConstants.LUMBY_X, ConstructionConstants.LUMBY_Y), 31,
				new int[][] {{563, 100}, {557, 100/*EARTH*/}, {556, 300}}, new int[] {13616, 13623, 13630}),
		FALADOR(3, new Position(ConstructionConstants.FALADOR_X, ConstructionConstants.FALADOR_Y), 37,
				new int[][] {{563, 100}, {555, 100/*water*/}, {556, 300}}, new int[] {13617, 13624, 13631}),
		CAMELOT(4, new Position(ConstructionConstants.CAMELOT_X, ConstructionConstants.CAMELOT_Y), 45,
				new int[][] {{563, 200}, {556, 500}}, new int[] {13618, 13625, 13632}),
		ARDOUGNE(5, new Position(ConstructionConstants.ARDOUGNE_X, ConstructionConstants.ARDOUGNE_Y), 51,
				new int[][] {{563, 200}, {555, 200}}, new int[] {13619, 13626, 13633}),
		YANILLE(6, new Position(ConstructionConstants.YANILLE.getX(), ConstructionConstants.YANILLE.getY()), 58,
				new int[][] {{563, 200}, {557, 200}}, new int[] {13620, 13627, 13634}),
		KHARYLL(7, new Position(ConstructionConstants.KHARYRLL_X, ConstructionConstants.KHARYRLL_Y), 66,
				new int[][] {{563, 200}, {565, 100}}, new int[] {13621, 13628, 13635}),
		EMPTY(-1, null, -1, null, null),
				;
		private Position destination;
		private int[][] requiredItems;
		private int[] objects;
		private int magicLevel, type;
		private Portals(int id, Position destination, int magicLevel, int[][] requiredItems, int[] objects)
		{
			this.type = id;
			this.destination = destination;
			this.requiredItems = requiredItems;
			this.objects = objects;
			this.magicLevel = magicLevel;
		}
		public Position getDestination()
		{
			return destination;
		}
		public static Portals forType(int type)
		{
			for(Portals p : values())
				if(p.type == type)
					return p;
			return null;
		}
		public static Portals forObjectId(int objectId)
		{
			for(Portals p : values())
			{
				for(int i : p.objects)
					if(i == objectId)
						return p;
			}
			return null;
		}
		public int[] getObjects()
		{
			return objects;
		}
		public String canBuild(Player p)
		{
			if(requiredItems == null)
			{
				boolean found = false;
				int[] myTiles = Construction.getMyChunk(p);
				Iterator<Portal> it = p.getHousePortals().iterator();
				while(it.hasNext())
				{
					Portal portal = it.next();
					if(portal.getRoomX() == myTiles[0]-1
							&& portal.getRoomY() == myTiles[1]-1
							&& portal.getRoomZ() == (p.inConstructionDungeon() ? 4 : p.getPosition().getZ())
							&& portal.getId() == p.getPortalSelected())
					{
						it.remove();
						found = true;
						break;
					}
				}
				if(!found)
				{
					p.getPacketSender().sendInterfaceRemoval();
					return "Can't remove that, doesn't exist."; 
				} else {
					//p.getPacketSender().sendObjectsRemoval(myTiles[0]-1, myTiles[1]-1, p.inConstructionDungeon() ? 4 : p.getPosition().getZ());
					//Construction.placeAllFurniture(p, myTiles[0]-1, myTiles[1]-1, p.inConstructionDungeon() ? 4 : p.getPosition().getZ());
					p.getPacketSender().sendInterfaceRemoval();
					return null;
				}
			}
			for(int i = 0; i < requiredItems.length; i++)
			{
				if(!p.getInventory().contains(requiredItems[i][0]))
					return "You don't have the required items to build this.";
				else if(!p.getInventory().contains(requiredItems[i][1]))
					return "You don't have the required items to build this.";
			}
			if(p.getSkillManager().getCurrentLevel(Skill.MAGIC) < magicLevel)
				return "You need a magic level of "+magicLevel+" to build this";
			build(p);
			return null;
		}
		public void build(Player p)
		{
			for(int i = 0; i < requiredItems.length; i++)
			{
				p.getInventory().delete(requiredItems[i][0], requiredItems[i][1]);
			}
			int[] myTiles = Construction.getMyChunk(p);
			boolean found = false;
			for(Portal portal : p.getHousePortals())
			{
				if(portal.getRoomX() == myTiles[0]-1
						&& portal.getRoomY() == myTiles[1]-1
						&& portal.getRoomZ() == (p.inConstructionDungeon() ? 4 : p.getPosition().getZ())
						&& portal.getId() == p.getPortalSelected())
				{
					portal.setType(type);
					found = true;
				}
			}
			if(!found)
			{
				Portal portal = new Portal();
				portal.setId(p.getPortalSelected());
				portal.setRoomX(myTiles[0] - 1);
				portal.setRoomY(myTiles[1] - 1);
				portal.setRoomZ(p.inConstructionDungeon() ? 4 : p.getPosition().getZ());
				portal.setType(type);
				p.getHousePortals().add(portal);
			}
			//p.getPacketSender().sendObjectsRemoval(myTiles[0]-1, myTiles[1]-1, p.inConstructionDungeon() ? 4 : p.getPosition().getZ());
			//Construction.placeAllFurniture(p, myTiles[0]-1, myTiles[1]-1, p.inConstructionDungeon() ? 4 : p.getPosition().getZ());
			p.getPacketSender().sendInterfaceRemoval();
		}
	}
	public static enum Butlers
	{
		JACK(4235, 20, 500, 6, 60),
		MAID(4237, 25, 1000, 10, 30),
		COOK(4239, 30, 3000, 16, 17),
		BUTLER(4241, 40, 5000, 20, 12),
		DEMON_BUTLER(4243, 50, 10000, 26, 7),
		;
		private int npcId, consLevel, loanCost,inventory;
		private double tripSeconds;
		private Butlers(int npcId, int consLevel, int loanCost, int inventory, double tripSeconds)
		{
			this.setNpcId(npcId);
			this.setConsLevel(consLevel);
			this.setLoanCost(loanCost);
			this.setInventory(inventory);
			this.setTripSeconds(tripSeconds * 1.4D);
		}
		public static Butlers forId(int npcId)
		{
			for(Butlers b : values())
				if(b.getNpcId() == npcId)
					return b;
			return null;
		}
		public double getTripSeconds() {
			return tripSeconds;
		}
		public void setTripSeconds(double tripSeconds) {
			this.tripSeconds = tripSeconds;
		}
		public int getConsLevel() {
			return consLevel;
		}
		public void setConsLevel(int consLevel) {
			this.consLevel = consLevel;
		}
		public int getLoanCost() {
			return loanCost;
		}
		public void setLoanCost(int loanCost) {
			this.loanCost = loanCost;
		}
		public int getInventory() {
			return inventory;
		}
		public void setInventory(int inventory) {
			this.inventory = inventory;
		}
		public int getNpcId() {
			return npcId;
		}
		public void setNpcId(int npcId) {
			this.npcId = npcId;
		}
	}
	public static final String[] HANGMANWORDS = new String[] 
	{
		"DENNISISNOOB",
		"ABYSSAL","ADAMANTITE","ALKHARID","ARDOUGNE","ASGARNIA","AVANTOE","BASILISK","BANSHEE","BARROWS","BLOODVELD","BOBTHECAT",
		"BRIMHAVEN","BURTHORPE","CADANTINE","CAMELOT","CANIFIS","CATHERBY","CHAOSDRUID","CHAOSDWARF","CHOMPYBIRD","COCKATRICE",
		"CRANDOR","CROMADIURE","DAGANNOTH","DORGESHUUN","DRAGON","DRAYNOR","DUSTDEVIL","DWARFWEED","EDGEVILLE","ENTRANA",
		"FALADOR","FELDIP","FIREGIANT","FREMENNIK","GARGOYLE","GOBLIN","GRANDTREE","GUAMLEAF","GUTANOTH","GUTHIX","HILLGIANT",
		"HELLHOUND","HIGHWAYMAN","HOBGOBLIN","ICEGIANT","ICEQUEEN","ICEWARRIOR","ICEWOLF","ICETROLL","IRITLEAF","ISAFDAR",
		"JOGRE","KALPHITE","KANDARIN","KARAMJA","KELDAGRIM","KHAZARD","KWUARM","LANTADYME","LLETYA","LUMBRIDGE","NECHRYAEL",
		"MARRENTILL","MENAPHOS","MISTHALIN","MITHRIL","MOGRE","MORTTON","MORYTANIA","MOSSGIANT","NIGHTSHADE","PALADIN",
		"PHASMATYS","PORTSARIM","PRIFDDINAS","PYREFIEND","RANARRWEED","RELLEKKA","RIMMINGTON","RUNESCAPE","RUNITE",
		"SARADOMIN","SKELETON","SNAPDRAGON","SNAPEGRASS","SOPHANEM","SOULLESS","SPIRITTREE","TARROMIN","TAVERLEY",
		"TERRORBIRD","TIRANNWN","TOADFLAX","TORSTOL","UGTHANKI","UNICORN","VARROCK","WHIP","YANILLE","ZAMORAK"};
	public enum RoomData
	{
		EMPTY(1864, 5056, ConstructionData.EMPTY, 1, 0, new boolean[] {true, true, true, true}),
		BUILDABLE(1864, 5056, ConstructionData.BUILDABLE, 1, 0, new boolean[] {true, true, true, true}),
		GARDEN(1856, 5064, ConstructionData.GARDEN, 1, 1000, new boolean[] {true, true, true, true}),
		PARLOUR(1920, 5112, ConstructionData.PARLOUR, 1, 1000, new boolean[] {true, true, true, false}),
		KITCHEN(1936, 5112, ConstructionData.KITCHEN, 5, 5000, new boolean[] {true, true, false, false}),
		DINING_ROOM(1952, 5112,ConstructionData.DINING_ROOM, 10, 5000, new boolean[] {true, true, true, false}),
		WORKSHOP(1920, 5096, ConstructionData.WORKSHOP, 15, 10000, new boolean[] {false, true, false, true}),
		BEDROOM(1968, 5112, ConstructionData.BEDROOM, 20, 10000, new boolean[] {true, true, false, false}),
		SKILL_ROOM(1928, 5104, ConstructionData.SKILL_ROOM, 25, 15000, new boolean[] {true, true, true, true}),
		QUEST_HALL_DOWN(1976, 5104, ConstructionData.QUEST_HALL_DOWN, 35, 0, new boolean[] {true, true, true, true}),
		SKILL_HALL_DOWN(1944, 5104, ConstructionData.SKILL_HALL_DOWN, 25, 0, new boolean[] {true, true, true, true}),
		GAMES_ROOM(1960, 5088, ConstructionData.GAMES_ROOM, 30, 25000, new boolean[] {true, true, true, false}),
		COMBAT_ROOM(1944, 5088, ConstructionData.COMBAT_ROOM, 32, 25000, new boolean[] {true, true, true, false}),
		QUEST_ROOM(1960, 5104, ConstructionData.QUEST_ROOM, 35, 25000, new boolean[] {true, true, true, true}),
		MENAGERY(1912, 5072, ConstructionData.MENAGERY, 37, 30000, new boolean[] {true, true, true, true}),
		STUDY(1952, 5096, ConstructionData.STUDY, 40, 50000, new boolean[] {true, true, true, false}),
		CUSTOME_ROOM(1968, 5064, ConstructionData.COSTUME_ROOM, 42, 50000, new boolean[] {false, true, false, false}),
		CHAPEL(1936, 5096, ConstructionData.CHAPEL, 45, 50000, new boolean[] {true, true, false, false}),
		PORTAL_ROOM(1928, 5088, ConstructionData.PORTAL_ROOM, 50, 100000, new boolean[] {false, true, false, false}),
		FORMAL_GARDEN(1872, 5064, ConstructionData.FORMAL_GARDEN, 55, 75000, new boolean[] {true, true, true, true}),
		THRONE_ROOM(1968, 5096, ConstructionData.THRONE_ROOM, 60, 150000, new boolean[] {false, true, false, false}),
		OUBLIETTE(1904, 5080, ConstructionData.OUBLIETTE, 65, 150000, new boolean[] {true, true, true, true}),
		PIT(1896, 5072, ConstructionData.PIT, 70, 10000, new boolean[] {true, true, true, true}),
		DUNGEON_STAIR_ROOM(1872, 5080, ConstructionData.DUNGEON_STAIR_ROOM, 70, 7500, new boolean[] {true, true, true, true}),
		TREASURE_ROOM(1912, 5088, ConstructionData.TREASURE_ROOM, 75, 250000, new boolean[] {false, true, false, false}),
		CORRIDOR(1888, 5080, ConstructionData.CORRIDOR, 70, 7500, new boolean[] {false, true, false, true}),
		JUNCTION(1856, 5080, ConstructionData.JUNCTION, 70, 7500, new boolean[] {true, true, true, true}),
		ROOF(1888, 5064, ConstructionData.ROOF, 0, 0, new boolean[] {true, true, true, true}),
		DUNGEON_EMPTY(1880, 5056, ConstructionData.DUNGEON_EMPTY, 0, 0, new boolean[] {true, true, true, true}),
		;
		
		public static RoomData forID(int id) {
			for (RoomData rd : values()) {
				if (rd.id == id)
					return rd;
			}
			return null;
		}

		private int x, y, cost, levelToBuild, id;
		private boolean[] doors;

		private RoomData(int x, int y, int id, int levelToBuild, int cost,
				boolean[] doors) {
			this.x = x;
			this.y = y;
			this.id = id;
			this.levelToBuild = levelToBuild;
			this.cost = cost;
			this.doors = doors;
		}

		public boolean[] getDoors() {
			return doors;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getCost() {
			return cost;
		}

		public int getLevelToBuild() {
			return levelToBuild;
		}

		public int getId() {
			return id;
		}

		public static int getFirstElegibleRotation(RoomData rd, int from) {
			for (int rot = 0; rot < 4; rot++) {
				boolean[] door = rd.getRotatedDoors(rot);
				if (from == 0 && door[2])
					return rot;
				if (from == 1 && door[3])
					return rot;
				if (from == 2 && door[0])
					return rot;
				if (from == 3 && door[1])
					return rot;
			}
			return -1;
		}
		public static int getNextEligibleRotationClockWise(RoomData rd, int from, int currentRot) {
			for (int rot = currentRot+1; rot < currentRot+4; rot++) {
				int rawt = (rot > 3 ? (rot - 4) : rot);
				boolean[] door = rd.getRotatedDoors(rawt);
				if (from == 0 && door[2])
					return rawt;
				if (from == 1 && door[3])
					return rawt;
				if (from == 2 && door[0])
					return rawt;
				if (from == 3 && door[1])
					return rawt;
			}
			return currentRot;
		}
		public static int getNextEligibleRotationCounterClockWise(RoomData rd, int from, int currentRot) {
			for (int rot = currentRot-1; rot > currentRot-4; rot--) {
				int rawt = (rot < 0 ? (rot + 4) : rot);
				boolean[] door = rd.getRotatedDoors(rawt);
				if (from == 0 && door[2])
					return rawt;
				if (from == 1 && door[3])
					return rawt;
				if (from == 2 && door[0])
					return rawt;
				if (from == 3 && door[1])
					return rawt;
			}
			return -1;
		}

		public boolean[] getRotatedDoors(int rotation) {
			if (rotation == 0)
				return doors;
			if (rotation == 1) {
				boolean[] newDoors = new boolean[4];
				if (doors[0])
					newDoors[3] = true;
				if (doors[1])
					newDoors[0] = true;
				if (doors[2])
					newDoors[1] = true;
				if (doors[3])
					newDoors[2] = true;
				return newDoors;
			}
			if (rotation == 2) {
				boolean[] newDoors = new boolean[4];
				if (doors[0])
					newDoors[2] = true;
				if (doors[1])
					newDoors[3] = true;
				if (doors[2])
					newDoors[0] = true;
				if (doors[3])
					newDoors[1] = true;
				return newDoors;
			}
			if (rotation == 3) {
				boolean[] newDoors = new boolean[4];
				if (doors[0])
					newDoors[1] = true;
				if (doors[1])
					newDoors[2] = true;
				if (doors[2])
					newDoors[3] = true;
				if (doors[3])
					newDoors[0] = true;
				return newDoors;
			}
			return null;
		}
	}
	public static boolean isDungeonRoom(int roomType)
	{
		return roomType == DUNGEON_STAIR_ROOM
				|| roomType == CORRIDOR
				|| roomType == JUNCTION
				|| roomType == OUBLIETTE
				|| roomType == PIT
				|| roomType == TREASURE_ROOM;
	}
	public static boolean isGardenRoom(int roomType)
	{
		return roomType == GARDEN
				|| roomType == FORMAL_GARDEN;
	}
	public enum HotSpots
	{
		/**
		 * Garden
		 */
		CENTREPIECE(0, 15361, 3, 3, 0, ConstructionData.GARDEN),
		TREE_1(1, 15362, 1, 5, 0, ConstructionData.GARDEN),
		TREE_2(1, 15363, 6, 6, 0, ConstructionData.GARDEN),
		SMALL_PLANT_1(2, 15366, 3, 1, 0, ConstructionData.GARDEN),
		SMALL_PLANT_2(3, 15367, 4, 5, 0, ConstructionData.GARDEN),
		BIG_PLANT_1(4, 15364, 6, 0, 0, ConstructionData.GARDEN),
		BIG_PLANT_2(5, 15365, 0, 0 ,0, ConstructionData.GARDEN),
		/**
		 * Parlour
		 */
		PARLOUR_CHAIR_1(6, 15410, 2, 4, 2, ConstructionData.PARLOUR, 11),
		PARLOUR_CHAIR_2(6, 15412, 4, 3, 2, ConstructionData.PARLOUR),
		PARLOUR_CHAIR_3(6, 15411, 5, 4, 1, ConstructionData.PARLOUR, 11),
		PARLOUR_RUG_1(7, 15414, 2, 2, 0, ConstructionData.PARLOUR, 22, new Dimension(5, 5)),
		PARLOUR_RUG_2(7, 15415, 2, 2, 0, ConstructionData.PARLOUR, 22, new Dimension(5, 5)),
		PARLOUR_RUG_3(7, 15413, 2, 2, 0, ConstructionData.PARLOUR, 22, new Dimension(5, 5)),
		PARLOUR_BOOKCASE_1(8, 15416, 0, 1, 0, ConstructionData.PARLOUR),
		PARLOUR_BOOKCASE_2(8, 15416, 7, 1, 2, ConstructionData.PARLOUR),
		PARLOUR_FIREPLACE(9, 15418, 3, 7, 1, ConstructionData.PARLOUR),
		PARLOUR_CURTAIN_1(10, 15419, 0, 2, 0, ConstructionData.PARLOUR, 5, true),
		PARLOUR_CURTAIN_2(10, 15419, 0, 5, 0, ConstructionData.PARLOUR, 5, true),
		PARLOUR_CURTAIN_3(10, 15419, 2, 7, 1, ConstructionData.PARLOUR, 5, true),
		PARLOUR_CURTAIN_4(10, 15419, 5, 7, 1, ConstructionData.PARLOUR, 5, true),
		PARLOUR_CURTAIN_5(10, 15419, 7, 5, 2, ConstructionData.PARLOUR, 5, true),
		PARLOUR_CURTAIN_6(10, 15419, 7, 2, 2, ConstructionData.PARLOUR, 5, true),
		PARLOUR_CURTAIN_7(10, 15419, 5, 0, 3, ConstructionData.PARLOUR, 5, true),
		PARLOUR_CURTAIN_8(10, 15419, 2, 0, 3, ConstructionData.PARLOUR, 5, true),
		
		/**
		 * Kitchen
		 */
		KITCHEN_CAT_BASKET(11, 15402, 0, 0, 0, ConstructionData.KITCHEN, 22),
		KITCHEN_TABLE(12, 15405, 3, 3, 0, ConstructionData.KITCHEN),
		KITCHEN_BARREL(13, 15401, 0, 6, 3, ConstructionData.KITCHEN),
		KITCHEN_STOVE(14, 15398, 3, 7, 1, ConstructionData.KITCHEN),
		KITCHEN_SINK(15, 15404, 7, 3, 0, ConstructionData.KITCHEN),
		KITCHEN_LARDER(16, 15403, 6, 0, 3, ConstructionData.KITCHEN),
		KITCHEN_SHELF_1(17, 15400, 1, 7, 1, ConstructionData.KITCHEN, 5),
		KITCHEN_SHELF_2(17, 15400, 6, 7, 1, ConstructionData.KITCHEN, 5),
		KITCHEN_SHELF_3(17, 15399, 7, 6, 2, ConstructionData.KITCHEN, 5),
		
		/**
		 * Dining room
		 */
		DINING_CURTAIN_1(10, 15302, 0, 2, 0, ConstructionData.DINING_ROOM, 5, true),
		DINING_CURTAIN_2(10, 15302, 0, 5, 0, ConstructionData.DINING_ROOM, 5, true),
		DINING_CURTAIN_3(10, 15302, 7, 5, 2, ConstructionData.DINING_ROOM, 5, true),
		DINING_CURTAIN_4(10, 15302, 7, 2, 2, ConstructionData.DINING_ROOM, 5, true),
		DINING_CURTAIN_5(10, 15302, 5, 0, 3, ConstructionData.DINING_ROOM, 5, true),
		DINING_CURTAIN_6(10, 15302, 2, 0, 3, ConstructionData.DINING_ROOM, 5, true),
		DINING_BELL_PULL(18, 15304, 0, 0, 2, ConstructionData.DINING_ROOM),
		DINING_WALL_DEC_1(19, 15303, 2, 7, 1, ConstructionData.DINING_ROOM, 5),
		DINING_WALL_DEC_2(19, 15303, 5, 7, 1, ConstructionData.DINING_ROOM, 5),
		DINING_FIREPLACE(9, 15301, 3, 7, 1, ConstructionData.DINING_ROOM),
		DINING_SEATING_1(20, 15300, 2, 2, 2, ConstructionData.DINING_ROOM, true),
		DINING_SEATING_2(20, 15300, 3, 2, 2, ConstructionData.DINING_ROOM, true),
		DINING_SEATING_3(20, 15300, 4, 2, 2, ConstructionData.DINING_ROOM, true),
		DINING_SEATING_4(20, 15300, 5, 2, 2, ConstructionData.DINING_ROOM, true),
		DINING_SEATING_5(20, 15299, 2, 5, 0, ConstructionData.DINING_ROOM, true),
		DINING_SEATING_6(20, 15299, 3, 5, 0, ConstructionData.DINING_ROOM, true),
		DINING_SEATING_7(20, 15299, 4, 5, 0, ConstructionData.DINING_ROOM, true),
		DINING_SEATING_8(20, 15299, 5, 5, 0, ConstructionData.DINING_ROOM, true),
		DINING_TABLE(21, 15298, 2, 3, 0, ConstructionData.DINING_ROOM),
		
		/**
		 * Workshop
		 */
		WORKSHOP_TOOL_1(22, 15443, 1, 0, 3, ConstructionData.WORKSHOP, 5),
		WORKSHOP_TOOL_2(22, 15445, 0, 1, 0, ConstructionData.WORKSHOP, 5),
		WORKSHOP_TOOL_3(22, 15447, 0, 6, 0, ConstructionData.WORKSHOP, 5),
		WORKSHOP_TOOL_4(22, 15446, 7, 1, 2, ConstructionData.WORKSHOP, 5),
		WORKSHOP_TOOL_5(22, 15444, 6, 0, 3, ConstructionData.WORKSHOP, 5),
		WORKSHOP_CLOCKMAKING(23, 15441, 0, 3, 3, ConstructionData.WORKSHOP),
		WORKSHOP_WORKBENCH(24, 15439, 3, 4, 0, ConstructionData.WORKSHOP),
		WORKSHOP_REPAIR_STANCE(25, 15448, 7, 3, 1, ConstructionData.WORKSHOP),
		WORKSHOP_HERALDY(26, 15450, 7, 6, 1, ConstructionData.WORKSHOP),
		
		/**
		 * Bedroom
		 */
		BEDROOM_RUG_1(7, 15266, 2, 2, 0, ConstructionData.BEDROOM, 22, new Dimension(5, 3)),
		BEDROOM_RUG_2(7, 15265, 2, 2, 0, ConstructionData.BEDROOM, 22, new Dimension(5, 3)),
		BEDROOM_RUG_3(7, 15264, 2, 2, 0, ConstructionData.BEDROOM, 22, new Dimension(5, 3)),

		BEDROOM_CURTAIN_1(10, 15263, 0, 2, 0, ConstructionData.BEDROOM, 5, true),
		BEDROOM_CURTAIN_2(10, 15263, 0, 5, 0, ConstructionData.BEDROOM, 5, true),
		BEDROOM_CURTAIN_3(10, 15263, 2, 7, 1, ConstructionData.BEDROOM, 5, true),
		BEDROOM_CURTAIN_4(10, 15263, 5, 7, 1, ConstructionData.BEDROOM, 5, true),
		BEDROOM_CURTAIN_5(10, 15263, 7, 5, 2, ConstructionData.BEDROOM, 5, true),
		BEDROOM_CURTAIN_6(10, 15263, 7, 2, 2, ConstructionData.BEDROOM, 5, true),
		BEDROOM_CURTAIN_7(10, 15263, 5, 0, 3, ConstructionData.BEDROOM, 5, true),
		BEDROOM_CURTAIN_8(10, 15263, 2, 0, 3, ConstructionData.BEDROOM, 5, true),
		BEDROOM_FIREPLACE(9, 15267, 7, 3, 2, ConstructionData.BEDROOM),
		BEDROOM_DRESSER(27, 15262, 0, 7, 0, ConstructionData.BEDROOM),
		BEDROOM_BED(28, 15260, 3, 6, 0, ConstructionData.BEDROOM),
		BEDROOM_WARDROBE(29, 15261, 6, 7, 0, ConstructionData.BEDROOM),
		BEDROOM_CLOCK(30, 15268, 7, 0, 1, ConstructionData.BEDROOM, 11),
		
		/**
		 * Skill hall
		 */
		SKILL_HALL_RUG_1(7, 15379, 2, 2, 0, ConstructionData.SKILL_ROOM, 22, new Dimension(5, 5)),
		SKILL_HALL_RUG_2(7, 15378, 2, 2, 0, ConstructionData.SKILL_ROOM, 22, new Dimension(5, 5)),
		SKILL_HALL_RUG_3(7, 15377, 2, 2, 0, ConstructionData.SKILL_ROOM, 22, new Dimension(5, 5)),
		SKILL_HALL_RUNE_CASE(31, 15386, 0, 6, 1, ConstructionData.SKILL_ROOM),
		SKILL_HALL_FISHING_TOPHY(32, 15383, 1, 7, 0, ConstructionData.SKILL_ROOM),
		SKILL_HALL_HEAD_TOPHY(33, 15382, 6, 7, 0, ConstructionData.SKILL_ROOM),
		SKILL_HALL_ARMOUR_1(34, 15384, 5, 3, 0, ConstructionData.SKILL_ROOM),
		SKILL_HALL_ARMOUR_2(35, 34255, 2, 3, 0, ConstructionData.SKILL_ROOM),
		SKILL_HALL_STAIRS(36, 15380, 3, 3, 0, ConstructionData.SKILL_ROOM),
		SKILL_HALL_STAIRS_1(37, 15381, 3, 3, 0, ConstructionData.SKILL_ROOM),
		
		SKILL_HALL_RUG_1_DOWN(7, 15379, 2, 2, 0, ConstructionData.SKILL_HALL_DOWN, 22, new Dimension(5, 5)),
		SKILL_HALL_RUG_2_DOWN(7, 15378, 2, 2, 0, ConstructionData.SKILL_HALL_DOWN, 22, new Dimension(5, 5)),
		SKILL_HALL_RUG_3_DOWN(7, 15377, 2, 2, 0, ConstructionData.SKILL_HALL_DOWN, 22, new Dimension(5, 5)),
		SKILL_HALL_RUNE_CASE_DOWN(31, 15386, 0, 6, 1, ConstructionData.SKILL_HALL_DOWN),
		SKILL_HALL_FISHING_TOPHY_DOWN(32, 15383, 1, 7, 0, ConstructionData.SKILL_HALL_DOWN),
		SKILL_HALL_HEAD_TOPHY_DOWN(33, 15382, 6, 7, 0, ConstructionData.SKILL_HALL_DOWN),
		SKILL_HALL_ARMOUR_1_DOWN(34, 15384, 5, 3, 0, ConstructionData.SKILL_HALL_DOWN),
		SKILL_HALL_ARMOUR_2_DOWN(35, 34255, 2, 3, 0, ConstructionData.SKILL_HALL_DOWN),
		SKILL_HALL_STAIRS_DOWN(36, 15380, 3, 3, 0, ConstructionData.SKILL_HALL_DOWN),
		SKILL_HALL_STAIRS_1_DOWN(37, 15381, 3, 3, 0, ConstructionData.SKILL_HALL_DOWN),
		
		/**
		 * Games room
		 */
		RANGING_GAME(38, 15346, 1, 0, 2, ConstructionData.GAMES_ROOM),
		STONE_SPACE(39, 15344, 2, 4, 0, ConstructionData.GAMES_ROOM),
		ELEMENTAL_BALANCE(40, 15345, 5, 4, 0, ConstructionData.GAMES_ROOM),
		PRIZE_CHEST(41, 15343, 3, 7, 0, ConstructionData.GAMES_ROOM),
		GAME_SPACE(42, 15342, 6, 0, 1, ConstructionData.GAMES_ROOM),
				
		/**
		 * Combat room
		 */
		STORAGE_RACK(43, 15296, 3, 7, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_WALL_DEC_1(19, 15297, 1, 7, 1, ConstructionData.COMBAT_ROOM, 5),
		COMBAT_WALL_DEC_2(19, 15297, 6, 7, 1, ConstructionData.COMBAT_ROOM, 5),

		COMBAT_RING_1(44, 15294, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_3(44, 15293, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_4(44, 15292, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_5(44, 15291, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_6(44, 15290, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_7(44, 15289, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_8(44, 15288, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_9(44, 15287, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_10(44, 15286, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_11(44, 15282, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_12(44, 15281, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_13(44, 15280, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_14(44, 15279, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_15(44, 15278, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		COMBAT_RING_16(44, 15277, 1, 1, 0, ConstructionData.COMBAT_ROOM),
		/**
		 * Quest hall
		 */
		QUEST_HALL_BOOKCASE(8, 15397, 0, 1, 0, ConstructionData.QUEST_ROOM),
		QUEST_HALL_MAP(45, 15396, 7, 1, 2, ConstructionData.QUEST_ROOM, 5),
		QUEST_HALL_SWORD(46, 15395, 7, 6, 2, ConstructionData.QUEST_ROOM, 5),
		QUEST_HALL_LANDSCAPE(47, 15393, 6, 7, 1, ConstructionData.QUEST_ROOM, 5),
		QUEST_HALL_PORTRAIT(48, 15392, 1, 7, 1, ConstructionData.QUEST_ROOM, 5),
		QUEST_HALL_GUILD_TROPHY(49, 15394, 0, 6, 0, ConstructionData.QUEST_ROOM, 5),
		QUEST_HALL_STAIRS(36, 15390, 3, 3, 0, ConstructionData.QUEST_ROOM),
		QUEST_HALL_RUG_1(7, 15389, 2, 2, 0, ConstructionData.QUEST_ROOM, 22, new Dimension(5, 5)),
		QUEST_HALL_RUG_2(7, 15388, 2, 2, 0, ConstructionData.QUEST_ROOM, 22, new Dimension(5, 5)),
		QUEST_HALL_RUG_3(7, 15387, 2, 2, 0, ConstructionData.QUEST_ROOM, 22, new Dimension(5, 5)),
		
		QUEST_HALL_BOOKCASE_DOWN(8, 15397, 0, 1, 0, ConstructionData.QUEST_HALL_DOWN),
		QUEST_HALL_MAP_DOWN(45, 15396, 7, 1, 2, ConstructionData.QUEST_HALL_DOWN, 5),
		QUEST_HALL_SWORD_DOWN(46, 15395, 7, 6, 2, ConstructionData.QUEST_HALL_DOWN, 5),
		QUEST_HALL_LANDSCAPE_DOWN(47, 15393, 6, 7, 1, ConstructionData.QUEST_HALL_DOWN, 5),
		QUEST_HALL_PORTRAIT_DOWN(48, 15392, 1, 7, 1, ConstructionData.QUEST_HALL_DOWN, 5),
		QUEST_HALL_GUILD_TROPHY_DOWN(49, 15394, 0, 6, 0, ConstructionData.QUEST_HALL_DOWN, 5),
		QUEST_HALL_STAIRS_DOWN(36, 15390, 3, 3, 0, ConstructionData.QUEST_HALL_DOWN),
		QUEST_HALL_RUG_1_DOWN(7, 15389, 2, 2, 0, ConstructionData.QUEST_HALL_DOWN, 22, new Dimension(5, 5)),
		QUEST_HALL_RUG_2_DOWN(7, 15388, 2, 2, 0, ConstructionData.QUEST_HALL_DOWN, 22, new Dimension(5, 5)),
		QUEST_HALL_RUG_3_DOWN(7, 15387, 2, 2, 0, ConstructionData.QUEST_HALL_DOWN, 22, new Dimension(5, 5)),
		
		/**
		 * Menagerie
		 */
		MENAGERIE_PET_HOUSE(50, 44909, 1, 1, 2, ConstructionData.MENAGERY),
		MENAGERIE_PET_FEEDER(51, 44910, 5, 1, 3, ConstructionData.MENAGERY),
		MENAGERIE_OBELISK(52, 44911, 5, 5, 3, ConstructionData.MENAGERY),
		
		HABITAT_SPACE(53, 44908, 0, 0, 0, ConstructionData.MENAGERY),
		
		/**
		 * Study
		 */
		STUDY_WALL_CHART_1(54, 15423, 0, 1, 0, ConstructionData.STUDY, 5, true),
		STUDY_WALL_CHART_2(54, 15423, 1, 7, 1, ConstructionData.STUDY, 5, true),
		STUDY_WALL_CHART_3(54, 15423, 6, 7, 1, ConstructionData.STUDY, 5, true),
		STUDY_WALL_CHART_4(54, 15423, 7, 1, 2, ConstructionData.STUDY, 5, true),
		STUDY_LECTERN(55, 15420, 2, 2, 2, ConstructionData.STUDY),
		STUDY_STATUE(56, 48662, 6, 1, 2, ConstructionData.STUDY),
		STUDY_CRYSTAL_BALL(57, 15422, 5, 4, 2, ConstructionData.STUDY),
		STUDY_GLOBE(58, 15421, 1, 4, 2, ConstructionData.STUDY),
		STUDY_BOOKCASE_1(8, 15425, 3, 7, 1, ConstructionData.STUDY),
		STUDY_BOOKCASE_2(8, 15425, 4, 7, 1, ConstructionData.STUDY),
		STUDY_TELESCOPE(59, 15424, 5, 7, 2, ConstructionData.STUDY),
		
		/**
		 * Costume room
		 */
		COSTUME_TREASURE_CHEST(60, 18813, 0, 3, 3, ConstructionData.COSTUME_ROOM),
		COSTUME_FANCY(61, 18814, 3, 3, 0, ConstructionData.COSTUME_ROOM),
		TOY_BOX(62, 18812, 7, 3, 1, ConstructionData.COSTUME_ROOM),
		ARMOUR_CASE(63, 18815, 2, 7, 1, ConstructionData.COSTUME_ROOM),
		MAGIC_WARDROBE(64, 18811, 3, 7, 1, ConstructionData.COSTUME_ROOM),
		CAPE_RACK(65, 18810, 6, 6, 1, ConstructionData.COSTUME_ROOM),
		/**
		 * Chapel
		 */
		CHAPEL_STATUE_1(66, 15275, 0, 0, 2, ConstructionData.CHAPEL, 11, true),
		CHAPEL_STATUE_2(66, 15275, 7, 0, 1, ConstructionData.CHAPEL, 11, true),
		CHAPEL_LAMP_1(67, 15271, 1, 5, 2, ConstructionData.CHAPEL, true),
		CHAPEL_LAMP_2(67, 15271, 6, 5, 2, ConstructionData.CHAPEL, true),
		CHAPEL_MUSICAL(68, 15276, 7, 3, 1, ConstructionData.CHAPEL),
		CHAPEL_ALTAR(69, 15270, 3, 5, 0, ConstructionData.CHAPEL),
		CHAPEL_ICON(70, 15269, 3, 7, 0, ConstructionData.CHAPEL),
		CHAPEL_WINDOW_0(71, 13733, 0, 2, 0, ConstructionData.CHAPEL, 0, true),
		CHAPEL_WINDOW_1(71, 13733, 0, 5, 0, ConstructionData.CHAPEL, 0, true),
		CHAPEL_WINDOW_2(71, 13733, 2, 7, 1, ConstructionData.CHAPEL, 0, true),
		CHAPEL_WINDOW_3(71, 13733, 5, 7, 1, ConstructionData.CHAPEL, 0, true),
		CHAPEL_WINDOW_4(71, 13733, 7, 5, 2, ConstructionData.CHAPEL, 0, true),
		CHAPEL_WINDOW_5(71, 13733, 7, 2, 2, ConstructionData.CHAPEL, 0, true),

		CHAPEL_RUG_1(7, 15270, 4, 1, 0, ConstructionData.CHAPEL, 22, new Dimension(1, 4)),
		CHAPEL_RUG_2(7, 15274, 4, 1, 0, ConstructionData.CHAPEL, 22, new Dimension(1, 4)),
		CHAPEL_RUG_3(7, 15273, 4, 1, 0, ConstructionData.CHAPEL, 22, new Dimension(1, 4)),
		
		/**
		 * Portal room
		 */

		PORTAL_1(72, 15406, 0, 3, 1, ConstructionData.PORTAL_ROOM),
		PORTAL_2(72, 15407, 3, 7, 2, ConstructionData.PORTAL_ROOM),
		PORTAL_3(72, 15408, 7, 3, 3, ConstructionData.PORTAL_ROOM),
		PORTAL_CENTREPIECE(73, 15409, 3, 3, 0, ConstructionData.PORTAL_ROOM),
		/**
		 * Formal garden
		 */
		FORMAL_CENTREPIECE(74, 15368, 3, 3, 0, ConstructionData.FORMAL_GARDEN),
		FORMAL_SMALL_PLANT_1_0(75, 15375, 5, 1, 2, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_SMALL_PLANT_1_1(75, 15375, 6, 2, 2, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_SMALL_PLANT_1_2(75, 15375, 1, 5, 0, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_SMALL_PLANT_1_3(75, 15375, 2, 6, 0, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_SMALL_PLANT_2_0(76, 15376, 1, 2, 2, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_SMALL_PLANT_2_1(76, 15376, 2, 1, 2, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_SMALL_PLANT_2_2(76, 15376, 5, 6, 0, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_SMALL_PLANT_2_3(76, 15376, 6, 5, 0, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_BIG_PLANT_1_0(75, 15373, 6, 1, 2, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_BIG_PLANT_1_1(75, 15373, 1, 6, 0, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_BIG_PLANT_2_0(76, 15374, 1, 1, 2, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_BIG_PLANT_2_1(76, 15374, 6, 6, 0, ConstructionData.FORMAL_GARDEN, true),
		FORMAL_HEDGE_1(77, 15370, 0, 0, 0, ConstructionData.FORMAL_GARDEN),
		FORMAL_HEDGE_2(77, 15371, 0, 0, 0, ConstructionData.FORMAL_GARDEN),
		FORMAL_HEDGE_3(77, 15372, 0, 0, 0, ConstructionData.FORMAL_GARDEN),
		FORMAL_FENCE(78, 15369, 0, 0, 0, ConstructionData.FORMAL_GARDEN),
		/**
		 * Throne room
		 */
		THRONE_ROOM_THRONE_1(79, 15426, 3, 6, 0, ConstructionData.THRONE_ROOM, true),
		THRONE_ROOM_THRONE_2(79, 15426, 4, 6, 0, ConstructionData.THRONE_ROOM, true),
		THRONE_ROOM_LEVER(80, 15435, 6, 6, 0, ConstructionData.THRONE_ROOM),
		THRONE_ROOM_TRAPDOOR(81, 15438, 1, 6, 0, ConstructionData.THRONE_ROOM, 22),
		THRONE_DECORATION_1(83, 15434, 3, 7, 1, ConstructionData.THRONE_ROOM, 4, true),
		THRONE_DECORATION_2(83, 15434, 4, 7, 1, ConstructionData.THRONE_ROOM, 4, true),
		THRONE_TRAP_1(84, 15431, 3, 3, 0, ConstructionData.THRONE_ROOM, 22, true),
		THRONE_TRAP_2(84, 15431, 4, 3, 0, ConstructionData.THRONE_ROOM, 22, true),
		THRONE_TRAP_3(84, 15431, 3, 4, 0, ConstructionData.THRONE_ROOM, 22, true),
		THRONE_TRAP_4(84, 15431, 4, 4, 0, ConstructionData.THRONE_ROOM, 22, true),
		THRONE_BENCH_1(20, 15436, 0, 0, 3, ConstructionData.THRONE_ROOM, true),
		THRONE_BENCH_2(20, 15436, 0, 1, 3, ConstructionData.THRONE_ROOM, true),
		THRONE_BENCH_3(20, 15436, 0, 2, 3, ConstructionData.THRONE_ROOM, true),
		THRONE_BENCH_4(20, 15436, 0, 3, 3, ConstructionData.THRONE_ROOM, true),
		THRONE_BENCH_5(20, 15436, 0, 4, 3, ConstructionData.THRONE_ROOM, true),
		THRONE_BENCH_6(20, 15436, 0, 5, 3, ConstructionData.THRONE_ROOM, true),
		THRONE_BENCH_7(20, 15437, 7, 0, 1, ConstructionData.THRONE_ROOM, true),
		THRONE_BENCH_8(20, 15437, 7, 1, 1, ConstructionData.THRONE_ROOM, true),
		THRONE_BENCH_9(20, 15437, 7, 2, 1, ConstructionData.THRONE_ROOM, true),
		THRONE_BENCH_10(20, 15437, 7, 3, 1, ConstructionData.THRONE_ROOM, true),
		THRONE_BENCH_11(20, 15437, 7, 4, 1, ConstructionData.THRONE_ROOM, true),
		THRONE_BENCH_12(20, 15437, 7, 5, 1, ConstructionData.THRONE_ROOM, true),
		/**
		 * Oubliette
		 */
		OUBLIETTE_FLOOR_1(85, 15350, 2, 2, 22, ConstructionData.OUBLIETTE),
		OUBLIETTE_FLOOR_2(85, 15348, 2, 2, 22, ConstructionData.OUBLIETTE),
		OUBLIETTE_FLOOR_3(85, 15347, 2, 2, 22, ConstructionData.OUBLIETTE),
		OUBLIETTE_FLOOR_4(85, 15351, 2, 2, 22, ConstructionData.OUBLIETTE),
		OUBLIETTE_FLOOR_5(85, 15349, 2, 2, 22, ConstructionData.OUBLIETTE),
		OUBLIETTE_CAGE_1(86, 15353, 2, 2, 0, ConstructionData.OUBLIETTE, 0),
		OUBLIETTE_CAGE_2(86, 15352, 2, 2, 0, ConstructionData.OUBLIETTE, 0),
		OUBLIETTE_GUARD(87, 15354, 0, 0, 3, ConstructionData.OUBLIETTE),
		OUBLIETTE_LADDER(88, 15356, 1, 6, 0, ConstructionData.OUBLIETTE),
		OUBLIETTE_DECORATION_1(89, 15331, 0, 2, 0, ConstructionData.OUBLIETTE, 4, true),
		OUBLIETTE_DECORATION_2(89, 15331, 2, 7, 1, ConstructionData.OUBLIETTE, 4, true),
		OUBLIETTE_DECORATION_3(89, 15331, 7, 5, 2, ConstructionData.OUBLIETTE, 4, true),
		OUBLIETTE_DECORATION_4(89, 15331, 5, 0, 3, ConstructionData.OUBLIETTE, 4, true),
		OUBLIETTE_LIGHT_1(90, 15355, 2, 0, 3, ConstructionData.OUBLIETTE, 4, true),
		OUBLIETTE_LIGHT_2(90, 15355, 0, 5, 0, ConstructionData.OUBLIETTE, 4, true),
		OUBLIETTE_LIGHT_3(90, 15355, 5, 7, 1, ConstructionData.OUBLIETTE, 4, true),
		OUBLIETTE_LIGHT_4(90, 15355, 7, 2, 2, ConstructionData.OUBLIETTE, 4, true),
		/**
		 * Corridor
		 */
		CORRIDOR_LIGHT_1(90, 15330, 3, 1, 0, ConstructionData.CORRIDOR, 4, true),
		CORRIDOR_LIGHT_2(90, 15330, 3, 6, 0, ConstructionData.CORRIDOR, 4, true),
		CORRIDOR_LIGHT_3(90, 15330, 4, 1, 2, ConstructionData.CORRIDOR, 4, true),
		CORRIDOR_LIGHT_4(90, 15330, 4, 6, 2, ConstructionData.CORRIDOR, 4, true),
		CORRIDOR_DECORATION_1(89, 15331, 3, 4, 0, ConstructionData.CORRIDOR, 4, true),
		CORRIDOR_DECORATION_2(89, 15331, 4, 3, 2, ConstructionData.CORRIDOR, 4, true),
		CORRIDOR_GUARD(87, 15323, 3, 3, 2, ConstructionData.CORRIDOR),
		CORRIDOR_TRAP_1(91, 15325, 3, 2, 0, ConstructionData.CORRIDOR, 22, true),
		CORRIDOR_TRAP_2(91, 15325, 4, 2, 0, ConstructionData.CORRIDOR, 22, true),
		CORRIDOR_TRAP_3(91, 15324, 3, 5, 0, ConstructionData.CORRIDOR, 22, true),
		CORRIDOR_TRAP_4(91, 15324, 4, 5, 0, ConstructionData.CORRIDOR, 22, true),
		CORRIDOR_DOOR_1(92, 15329, 3, 1, 3, ConstructionData.CORRIDOR, 0),
		CORRIDOR_DOOR_2(92, 15328, 4, 1, 3, ConstructionData.CORRIDOR, 0),
		CORRIDOR_DOOR_3(92, 15326, 3, 6, 1, ConstructionData.CORRIDOR, 0),
		CORRIDOR_DOOR_4(92, 15327, 4, 6, 1, ConstructionData.CORRIDOR, 0),
		
		/**
		 * junction
		 */
		JUNCTION_TRAP_1(91, 15325, 3, 2, 0, ConstructionData.JUNCTION, 22, true),
		JUNCTION_TRAP_2(91, 15325, 4, 2, 0, ConstructionData.JUNCTION, 22, true),
		JUNCTION_TRAP_3(91, 15324, 3, 5, 0, ConstructionData.JUNCTION, 22, true),
		JUNCTION_TRAP_4(91, 15324, 4, 5, 0, ConstructionData.JUNCTION, 22, true),
		JUNCTION_LIGHT_1(90, 15330, 3, 1, 0, ConstructionData.JUNCTION, 4, true),
		JUNCTION_LIGHT_2(90, 15330, 1, 4, 1, ConstructionData.JUNCTION, 4, true),
		JUNCTION_LIGHT_3(90, 15330, 4, 6, 2, ConstructionData.JUNCTION, 4, true),
		JUNCTION_LIGHT_4(90, 15330, 6, 3, 3, ConstructionData.JUNCTION, 4, true),
		JUNCTION_DECORATION_1(89, 15331, 1, 3, 3, ConstructionData.JUNCTION, 4, true),
		JUNCTION_DECORATION_2(89, 15331, 6, 4, 1, ConstructionData.JUNCTION, 4, true),
		JUNCTION_GUARD(87, 15323, 3, 3, 2, ConstructionData.JUNCTION),
		/**
		 * Dungeon stair room
		 */
		DUNG_STAIR_LIGHT_1(90, 34138, 2, 1, 3, ConstructionData.DUNGEON_STAIR_ROOM, 4, true),
		DUNG_STAIR_LIGHT_2(90, 15330, 1, 2, 0, ConstructionData.DUNGEON_STAIR_ROOM, 4, true),
		DUNG_STAIR_LIGHT_3(90, 15330, 2, 6, 1, ConstructionData.DUNGEON_STAIR_ROOM, 4, true),
		DUNG_STAIR_LIGHT_4(90, 34138, 1, 5, 0, ConstructionData.DUNGEON_STAIR_ROOM, 4, true),
		DUNG_STAIR_LIGHT_5(90, 34138, 5, 6, 1, ConstructionData.DUNGEON_STAIR_ROOM, 4, true),
		DUNG_STAIR_LIGHT_6(90, 15330, 6, 5, 2, ConstructionData.DUNGEON_STAIR_ROOM, 4, true),
		DUNG_STAIR_LIGHT_7(90, 34138, 6, 2, 2, ConstructionData.DUNGEON_STAIR_ROOM, 4, true),
		DUNG_STAIR_LIGHT_8(90, 15330, 5, 1, 3, ConstructionData.DUNGEON_STAIR_ROOM, 4, true),
		DUNG_STAIR_DECORATION_1(89, 15331, 1, 6, 1, ConstructionData.DUNGEON_STAIR_ROOM, 4, true),
		DUNG_STAIR_DECORATION_2(89, 15331, 6, 1, 3, ConstructionData.DUNGEON_STAIR_ROOM, 4, true),
		DUNG_STAIR_GUARD_1(87, 15337, 1, 1, 2, ConstructionData.DUNGEON_STAIR_ROOM),
		DUNG_STAIR_GUARD_2(87, 15336, 5, 5, 0, ConstructionData.DUNGEON_STAIR_ROOM),
		DUNG_STAIRS(36, 15380, 3, 3, 0, ConstructionData.DUNGEON_STAIR_ROOM),
		/**
		 * Pit
		 */
		PIT_TRAP_1(93, 39230, 3, 1, 0, ConstructionData.PIT),
		PIT_TRAP_2(93, 39231, 5, 3, 3, ConstructionData.PIT),
		PIT_TRAP_3(93, 36692, 3, 5, 2, ConstructionData.PIT),
		PIT_TRAP_4(93, 39229, 1, 3, 1, ConstructionData.PIT),
		PIT_GUARDIAN(94, 36676, 3, 3, 0, ConstructionData.PIT),
		PIT_LIGHT_1(90, 34138, 2, 1, 3, ConstructionData.PIT, 4, true),
		PIT_LIGHT_2(90, 15330, 1, 2, 0, ConstructionData.PIT, 4, true),
		PIT_LIGHT_3(90, 15330, 2, 6, 1, ConstructionData.PIT, 4, true),
		PIT_LIGHT_4(90, 34138, 1, 5, 0, ConstructionData.PIT, 4, true),
		PIT_LIGHT_5(90, 34138, 5, 6, 1, ConstructionData.PIT, 4, true),
		PIT_LIGHT_6(90, 15330, 6, 5, 2, ConstructionData.PIT, 4, true),
		PIT_LIGHT_7(90, 34138, 6, 2, 2, ConstructionData.PIT, 4, true),
		PIT_LIGHT_8(90, 15330, 5, 1, 3, ConstructionData.PIT, 4, true),
		PIT_DECORATION_1(89, 15331, 1, 6, 1, ConstructionData.PIT, 4, true),
		PIT_DECORATION_2(89, 15331, 6, 1, 3, ConstructionData.PIT, 4, true),
		
		PIT_DOOR_1(92, 36675, 3, 1, 3, ConstructionData.PIT, 0),
		PIT_DOOR_2(92, 36672, 4, 1, 3, ConstructionData.PIT, 0),
		PIT_DOOR_3(92, 36672, 3, 6, 1, ConstructionData.PIT, 0),
		PIT_DOOR_4(92, 36675, 4, 6, 1, ConstructionData.PIT, 0),
		PIT_DOOR_5(92, 36672, 1, 3, 0, ConstructionData.PIT, 0),
		PIT_DOOR_6(92, 36675, 1, 4, 0, ConstructionData.PIT, 0),
		PIT_DOOR_7(92, 36675, 6, 3, 0, ConstructionData.PIT, 2),
		PIT_DOOR_8(92, 36672, 6, 4, 0, ConstructionData.PIT, 2),
		/**
		 * Treasure room
		 */
		TREASURE_ROOM_DECORATION_1(89, 15331, 1, 2, 0, ConstructionData.TREASURE_ROOM, 4, true),
		TREASURE_ROOM_DECORATION_2(89, 15331, 6, 2, 2, ConstructionData.TREASURE_ROOM, 4, true),
		TREASURE_ROOM_LIGHT_1(90, 15330, 1, 5, 0, ConstructionData.TREASURE_ROOM, 4, true),
		TREASURE_ROOM_LIGHT_2(90, 15330, 6, 5, 2, ConstructionData.TREASURE_ROOM, 4, true),
		TREASURE_MONSTER(95, 15257, 3, 3, 0, ConstructionData.TREASURE_ROOM),
		TREASURE_CHEST(96, 15256, 2, 6, 0, ConstructionData.TREASURE_ROOM),
		TREASURE_DECORATION_1(83, 15259, 3, 6, 1, ConstructionData.TREASURE_ROOM, 4, true),
		TREASURE_DECORATION_2(83, 15259, 4, 6, 1, ConstructionData.TREASURE_ROOM, 4, true),
		TREASURE_ROOM_DOOR_1(92, 15327, 3, 1, 3, ConstructionData.TREASURE_ROOM, 0),
		TREASURE_ROOM_DOOR_2(92, 15326, 4, 1, 3, ConstructionData.TREASURE_ROOM, 0),
		;
		private int hotSpotId, objectId, xOffset, yOffset, standardRotation,
				objectType, roomType;
		private boolean mutiple;
		private Dimension carpetDim;
		
		private HotSpots(int hotSpotId, int objectId, int xOffset, int yOffset,
				int standardRotation, int roomType) {
			this.hotSpotId = hotSpotId;
			this.objectId = objectId;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.standardRotation = standardRotation;
			setCarpetDim(null);
			setObjectType(10);
			setMutiple(false);
			setRoomType(roomType);
			
		}

		private HotSpots(int hotSpotId, int objectId, int xOffset, int yOffset,
				int standardRotation, int roomType, int objectType) {
			this(hotSpotId, objectId, xOffset, yOffset, standardRotation, roomType);
			this.setObjectType(objectType);
		}
		private HotSpots(int hotSpotId, int objectId, int xOffset, int yOffset,
				int standardRotation, int roomType, int objectType, boolean mutiple) {
			this(hotSpotId, objectId, xOffset, yOffset, standardRotation, roomType, objectType);
			this.setMutiple(mutiple);
		}
		private HotSpots(int hotSpotId, int objectId, int xOffset, int yOffset,
				int standardRotation,int roomType, boolean mutiple) {
			this(hotSpotId, objectId, xOffset, yOffset, standardRotation, roomType);
			this.setMutiple(mutiple);
		}

		private HotSpots(int hotSpotId, int objectId, int xOffset, int yOffset,
				int standardRotation, int roomType, int objectType, Dimension carpetDim) {
			this(hotSpotId, objectId, xOffset, yOffset, standardRotation, roomType,
					objectType);
			this.setCarpetDim(carpetDim);
		}

		public static HotSpots forHotSpotId(int hotSpotId) {
			for (HotSpots hs : values()) {
				if (hs.hotSpotId == hotSpotId)
					return hs;
			}
			return null;
		}
		
		public static HotSpots forHotSpotIdAndCoords(int hotSpotId, int xOffset, int yOffset, Room room) {
			for (HotSpots hs : values()) {
				if (hs.hotSpotId == hotSpotId && hs.xOffset == xOffset && hs.yOffset == yOffset
						&& (hs.isMutiple() ? hs.getRoomType() == room.getType() : xOffset == xOffset))
					return hs;
			}
			return null;
		}
		public static HotSpots forObjectId(int objectId) {
			for (HotSpots hs : values()) {
				if (hs.objectId == objectId)
					return hs;
			}
			return null;
		}

		public static ArrayList<HotSpots> forObjectId_2(int objectId) {
			ArrayList<HotSpots> hs_1 = new ArrayList<HotSpots>();
			for (HotSpots hs : values()) {
				if (hs.objectId == objectId)
					hs_1.add(hs);
			}
			return hs_1;
		}
		public static ArrayList<HotSpots> forObjectId_3(int hotSpotId) {
			ArrayList<HotSpots> hs_1 = new ArrayList<HotSpots>();
			for (HotSpots hs : values()) {
				if (hs.hotSpotId == hotSpotId)
					hs_1.add(hs);
			}
			return hs_1;
		}

		public static HotSpots forObjectIdAndCoords(int objectId, int x, int y) {
			for (HotSpots hs : values()) {
				if (hs.objectId == objectId && hs.xOffset == x
						&& hs.yOffset == y) {
					return hs;
				}
			}
			return null;
		}

		public int getRotation(int roomRot) {
			if (roomRot == 0)
				return standardRotation;
			else if (roomRot == 1)
				return (standardRotation == 3 ? 0 : standardRotation + 1);
			else if (roomRot == 2)
				return (standardRotation == 2 ? 0 : standardRotation + 2);
			else if (roomRot == 3)
				return (standardRotation == 1 ? 0 : standardRotation + 3);
			return standardRotation;
		}

		public static int getRotation_2(int rot, int roomRot) {
			if (roomRot == 0)
				return rot;
			else if (roomRot == 1)
				return (rot == 3 ? 0 : rot + 1);
			else if (roomRot == 2)
				return (rot == 2 ? 0 : rot + 2);
			else if (roomRot == 3)
				return (rot == 1 ? 0 : rot + 3);
			return rot;
		}

		public int getHotSpotId() {
			return hotSpotId;
		}

		public int getObjectId() {
			return objectId;
		}

		public int getXOffset() {
			return xOffset;
		}

		public int getYOffset() {
			return yOffset;
		}

		public int getObjectType() {
			return objectType;
		}

		public void setObjectType(int objectType) {
			this.objectType = objectType;
		}

		public Dimension getCarpetDim() {
			return carpetDim;
		}

		public void setCarpetDim(Dimension carpetDim) {
			this.carpetDim = carpetDim;
		}

		public boolean isMutiple() {
			return mutiple;
		}

		public void setMutiple(boolean mutiple) {
			this.mutiple = mutiple;
		}
		public int getRoomType() {
			return roomType;
		}

		public void setRoomType(int roomType) {
			this.roomType = roomType;
		}
	}

	public static int getXOffsetForObjectId(int objectId, int hsId, int rotation) {
		HotSpots hs = HotSpots.forObjectId(hsId);
		if (hs == null)
			return 0;
		GameObjectDefinition objectDef = GameObjectDefinition.forId(objectId);
		int finalXTile = 0 + getRotatedLandscapeChunkX(rotation,
				objectDef.getSizeY(), hs.xOffset, hs.yOffset, objectDef.getSizeX(), hs.getRotation(0));
		return finalXTile;
	}

	public static int getYOffsetForObjectId(int objectId, int hsId, int rotation) {
		HotSpots hs = HotSpots.forObjectId(hsId);
		if (hs == null)
			return 0;
		GameObjectDefinition objectDef = GameObjectDefinition.forId(objectId);
		int finalYTile = 0 + getRotatedLandscapeChunkY(hs.yOffset,
				objectDef.getSizeY(), rotation, objectDef.getSizeX(), hs.xOffset, hs.getRotation(0));
		return finalYTile;
	}
	public static int getYOffsetForObjectId(int objectId, int offsetX, int offsetY, int rotation, int objectRot) {
		GameObjectDefinition objectDef = GameObjectDefinition.forId(objectId);
		int finalYTile = 0 + getRotatedLandscapeChunkY(offsetY,
				objectDef.getSizeY(), rotation, objectDef.getSizeX(), offsetX, objectRot);
		return finalYTile;
	}
	public static int getXOffsetForObjectId(int objectId, int offsetX, int offsetY,
			int rotation, int objectRot) {
		GameObjectDefinition objectDef = GameObjectDefinition.forId(objectId);
		int finalXTile = 0 + getRotatedLandscapeChunkX(rotation,
				objectDef.getSizeY(), offsetX, offsetY,
				objectDef.getSizeX(), objectRot);
		return finalXTile;
	}
	
	public static int getXOffsetForObjectId(int objectId, HotSpots hs,
			int rotation) {
		GameObjectDefinition objectDef = GameObjectDefinition.forId(objectId);
		int finalXTile = 0 + getRotatedLandscapeChunkX(rotation,
				objectDef.getSizeY(), hs.getXOffset(), hs.getYOffset(),
				objectDef.getSizeX(), hs.getRotation(0));
		return finalXTile;
	}

	public static int getYOffsetForObjectId(int objectId, HotSpots hs,
			int rotation) {
		GameObjectDefinition objectDef = GameObjectDefinition.forId(objectId);
		int finalYTile = 0 + getRotatedLandscapeChunkY(hs.getYOffset(),
				objectDef.getSizeY(), rotation, objectDef.getSizeX(), hs.getXOffset(), hs.getRotation(0));
		return finalYTile;
	}
	public enum Furniture
	{
		EMPTY(-1, 6951, 0, 0, 0, null),
		EXIT_PORTAL(0, 13405, 100, 1, 8168,new int[][] {{IRON_BAR, 10}}),
		DECORATIVE_ROCK(0, 13406, 100, 5, 8169,new int[][] {{LIMESTONE_BRICK, 5}}),
		POND(0, 13407, 100, 10, 8170,new int[][] {{SOFT_CLAY, 10}}),
		IMP_STATUE(0, 13408, 100, 15, 8171, new int[][] {{LIMESTONE_BRICK, 5}, {SOFT_CLAY, 5}}),
		DUNGEON_ENTRANCE(0, 13409, 500, 70, 8172, new int[][] {{MARBLE_BLOCK, 1}}),
		DEAD_TREE(1, 13411, 31, 5, 8173,new int[][] {{8417, 1}}),
		NICE_TREE(1, 13412, 44, 10, 8174,new int[][] {{8419, 1}}),
		OAK_TREE(1, 13413, 70, 15, 8175,new int[][] {{8421, 1}}),
		WILLOW_TREE(1, 13414, 100, 30, 8176,new int[][] {{8423, 1}}),
		MARPLE_TREE(1, 13415, 122, 45, 8177,new int[][] {{8425, 1}}),
		YEW_TREE(1, 13416, 141, 60, 8178,new int[][] {{8427, 1}}),
		MAGIC_TREE(1, 13417, 223, 75, 8179,new int[][] {{8429, 1}}),
		PLANT(2, 13431, 31, 1, 8180, new int[][] {{8431, 1}}),
		SMALL_FERN(2, 13432, 70, 6, 8181, new int[][] {{8433, 1}}),
		FERN(2, 13433, 100, 12, 8182, new int[][] {{8435, 1}}),
		DOCK_LEAF(3, 13434, 31, 1, 8183,new int[][] {{8431, 1}}),
		THISTLE(3, 13435, 70, 6, 8184,new int[][] {{8433, 1}}),
		REEDS(3, 13436, 100, 12, 8185,new int[][] {{8435, 1}}),
		BIG_FERN(4, 13425, 31, 1, 8186,new int[][] {{8431, 1}}),
		BUSH(4, 13426, 70, 6, 8187, new int[][] {{8433, 1}}),
		TALL_PLANT(4, 13427, 100, 12, 8188, new int[][] {{8435, 1}}),
		SHORT_PLANT(5, 13428, 31, 1, 8189,new int[][] {{8431, 1}}),
		LARGE_LEAF_BUSH(5, 13429, 70, 6, 8190,new int[][] {{8433, 1}}),
		HUGE_PLANT(5, 13430, 100, 12, 8191,new int[][] {{8435, 1}}),
		/**
		 * Parlour
		 */
		CRUDE_WOODEN_CHAIR(6, 13581, 66, 1, 8309,new int[][] {{PLANK, 2}, {NAILS, 2}}),
		WOODEN_CHAIR(6, 13582, 96, 8, 8310,new int[][] {{PLANK, 3}, {NAILS, 3}}),
		ROCKING_CHAIR(6, 13583, 96, 14, 8311,new int[][] {{PLANK, 3}, {NAILS, 3}}),
		OAK_CHAIR(6, 13584, 120, 19, 8312,new int[][] {{OAK_PLANK, 2}}),
		OAK_ARMCHAIR(6, 13585, 180, 26, 8313,new int[][] {{OAK_PLANK, 3}}),
		TEAK_ARMCHAIR(6, 13586, 180, 35, 8314,new int[][] {{TEAK_PLANK, 3}}),
		MAHOGANY_ARMCHAIR(6, 13587, 20, 50, 8315,new int[][] {{MAHOGANY_PLANK, 3}}),
		BROWN_RUG(7, 13588, 30, 2, 8316,new int[][] {{CLOTH, 2}}),
		RUG(7, 13591, 60, 13, 8317, new int[][] {{CLOTH, 4}}),
		OPULENT_RUG(7, 13594, 360, 65, 8318, new int[][] {{CLOTH, 4}, {GOLD_LEAF, 1}}),
		WOODEN_BOOKCASE(8, 13597, 115, 4, 8319,new int[][] {{PLANK, 4}, {NAILS, 4}}),
		OAK_BOOKCASE(8, 13598, 180, 29, 8320,new int[][] {{OAK_PLANK, 3}}),
		MAHOGANY_BOOKCASE(8, 13599, 420, 40, 8321,new int[][] {{MAHOGANY_PLANK, 3}}),
		CLAY_FIREPLACE(9, 13609, 30, 3, 8325, new int[][] {{SOFT_CLAY, 3}}),
		STONE_FIREPLACE(9, 13611, 40, 33, 8326, new int[][] {{LIMESTONE_BRICK, 2}}),
		MARBLE_FIREPLACE(9, 13613, 500, 63, 8327, new int[][] {{MARBLE_BLOCK, 1}}),
		TORN_CURTAINS(10, 13603, 132, 2, 8322, new int[][] {{PLANK, 3}, {CLOTH, 3}, {NAILS, 3}}),
		CURTAINS(10, 13604, 225, 18, 8323, new int[][] {{OAK_PLANK, 3}, {CLOTH, 3}}),
		OPULENT_CURTAINS(10, 13605, 315, 40, 8324, new int[][] {{TEAK_PLANK, 3}, {CLOTH, 3}}),
		/**
		 * Kitchen
		 */
		CAT_BLANKET(11, 13574, 15, 5, 8236, new int[][] {{CLOTH, 1}}),
		CAT_BASKET(11, 13575, 58, 19, 8237, new int[][] {{PLANK, 2}, {NAILS, 2}}),
		CUSHIONED_CAT_BASKET(11, 13576, 58, 33, 8238, new int[][] {{PLANK, 2}, {NAILS, 2}, {1737, 2}}),
		WOOD_KITCHEN_TABLE(12, 13577, 87, 12, 8246, new int[][] {{PLANK, 3}, {NAILS, 3}}),
		OAK_KITCHEN_TABLE(12, 13578, 180, 32, 8247, new int[][] {{OAK_PLANK, 3}}),
		TEAK_KITCHEN_TABLE(12, 13579, 270, 52, 8248, new int[][] {{TEAK_PLANK, 3}}),
		BEER_BARREL(13, 13568, 87, 7, 8239, new int[][] {{PLANK, 3}, {NAILS, 3}}),
		CIDER_BARREL(13, 13569, 91, 12, 8240, new int[][] {{PLANK, 3}, {NAILS, 3}, {5763, 8}}, new int[][] {{7, 14}}),
		ASGARNIAN_ALE_BARREL(13, 13570, 184, 14, 8241, new int[][] {{OAK_PLANK, 3}, {1905, 8}}, new int[][] {{7, 24}}),
		GREENMANS_ALE_BARREL(13, 13571, 184, 26, 8242, new int[][] {{OAK_PLANK, 3}, {1909, 8}}, new int[][] {{7, 29}}),
		DRAGON_BITTER_BARREL(13, 13572, 224, 36, 8243, new int[][] {{OAK_PLANK, 3}, {STEEL_BAR, 2}, {1911, 8}}, new int[][] {{7, 39}}),
		CHEFS_DELIGHT_BARREL(13, 13573, 224, 48, 8244, new int[][] {{OAK_PLANK, 3}, {STEEL_BAR, 2}, {5755, 8}}, new int[][] {{7, 54}}),
		FIRE_PIT(14, 13528, 250, 40, 8216, new int[][] {{SOFT_CLAY, 2}, {STEEL_BAR, 1}}),
		FIRE_PIT_HOOKS(14, 13529, 60, 11, 8217, new int[][] {{SOFT_CLAY,2}, {STEEL_BAR, 2}}),
		FIRE_PIT_POT(14, 13531, 80, 17, 8218, new int[][] {{SOFT_CLAY,2}, {STEEL_BAR, 3}}),
		SMALL_OVEN(14, 13533, 80, 24, 8219, new int[][] {{STEEL_BAR, 4}}),
		LARGE_OVEN(14, 13536, 100, 29, 8220, new int[][] {{STEEL_BAR, 5}}),
		STEEL_RANGE(14, 13539, 120, 34, 8221, new int[][] {{STEEL_BAR, 6}}),
		FANCY_RANGE(14, 13542, 140, 42, 8222, new int[][] {{STEEL_BAR, 8}}),
		WOODEN_LARDER(16, 13565, 228, 9, 8233, new int[][] {{PLANK,8}, {NAILS, 8}}),
		OAK_LARDER(16, 13566, 480, 33, 8234, new int[][] {{OAK_PLANK,8}}),
		TEAK_LARDER(16, 13567, 780, 43, 8235, new int[][] {{TEAK_PLANK,8}, {CLOTH, 2}}),
		PUMP_AND_DRAIN(15, 13559, 100, 7, 8230, new int[][] {{STEEL_BAR, 5}}),
		PUMP_AND_TUB(15, 13561, 200, 27, 8231, new int[][] {{STEEL_BAR, 10}}),
		SINK(15, 13563, 300, 47, 8232, new int[][] {{STEEL_BAR, 15}}),
		WOODEN_SHELVES_1(17, 13545, 87, 6, 8223, new int[][] {{PLANK, 3}, {NAILS, 3}}),
		WOODEN_SHELVES_2(17, 13546, 147, 12, 8224, new int[][] {{PLANK, 3}, {NAILS, 3}, {SOFT_CLAY, 6}}),
		WOODEN_SHELVES_3(17, 13547, 147, 23, 8225, new int[][] {{PLANK, 3}, {NAILS, 3}, {SOFT_CLAY, 6}}),
		OAK_SHELVES_1(17, 13548, 240, 34, 8226, new int[][] {{OAK_PLANK, 3}, {SOFT_CLAY, 6}}),
		OAK_SHELVES_2(17, 13549, 240, 45, 8227, new int[][] {{OAK_PLANK, 3}, {SOFT_CLAY, 6}}),
		TEAK_SHELVES_1(17, 13550, 330, 56, 8228, new int[][] {{TEAK_PLANK, 3}, {SOFT_CLAY, 6}}),
		TEAK_SHELVES_2(17, 13551, 930, 67, 8229, new int[][] {{TEAK_PLANK, 3}, {SOFT_CLAY, 6}}),
		/**
		 * bell pull
		 */
		ROPE_BELL_PULL(18, 13307, 64, 26, 8099, new int[][] {{OAK_PLANK, 1}, {ROPE, 1}}),
		BELL_PULL(18, 13308, 120, 37, 8100, new int[][] {{TEAK_PLANK, 1}, {CLOTH, 2}}),
		POSH_BELL_PULL(18, 13309, 420, 60, 8101, new int[][] {{TEAK_PLANK, 1}, {CLOTH, 2}, {GOLD_LEAF, 1}}),
		/**
		 * Wall decoration (dining room)
		 */
		OAK_DECORATION(19, 13798, 120, 16, 8102, new int[][] {{OAK_PLANK, 2}}),
		TEAK_DECORATION(19, 13814, 180, 36, 8103, new int[][] {{TEAK_PLANK, 2}}),
		GILDED_DECORATION(19, 13782, 1020, 56, 8104, new int[][] {{MAHOGANY_PLANK, 3}, {GOLD_LEAF, 2}}),
		/**
		 * Dining room seating
		 */
		WOODEN_BENCH(20, 13300, 115, 10, 8108, new int[][] {{PLANK, 4}, {NAILS, 4}}),
		OAK_BENCH(20, 13301, 240, 22, 8109, new int[][] {{OAK_PLANK, 4}}),
		CARVED_OAK_BENCH(20, 13302, 240, 31, 8110, new int[][] {{OAK_PLANK, 4}}),
		TEAK_DINING_BENCH(20, 13303, 360, 38, 8111, new int[][] {{TEAK_PLANK, 4}}),
		CARVED_TEAK_DINING_BENCH(20, 13304, 360, 44, 8112, new int[][] {{TEAK_PLANK, 4}}),
		MAHOGANY_BENCH(20, 13305, 560, 52, 8113, new int[][] {{MAHOGANY_PLANK, 4}}),
		GILDED_BENCH(20, 13306, 1760, 61, 8114, new int[][] {{MAHOGANY_PLANK, 4}, {GOLD_LEAF, 4}}),
		/**
		 * Dining room table
		 */
		WOOD_DINING_TABLE(21, 13293, 115, 10, 8115, new int[][] {{PLANK, 4}, {NAILS, 4}}),
		OAK_TABLE(21, 13294, 240, 22, 8116, new int[][] {{OAK_PLANK, 4}}),
		CARVED_OAK_TABLE(21, 13295, 360, 31, 8117, new int[][] {{OAK_PLANK, 5}}),
		TEAK_DINING_TABLE(21, 13296, 360, 38, 8118, new int[][] {{TEAK_PLANK, 4}}),
		CARVED_TEAK_TABLE(21, 13297, 600, 45, 8119, new int[][] {{TEAK_PLANK, 6}, {CLOTH, 4}}),
		MAHOGANY_TABLE(21, 13298, 840, 52, 8120, new int[][] {{MAHOGANY_PLANK, 6}}),
		OPULENT_TABLE(21, 13299, 3100, 72, 8121, new int[][] {{MAHOGANY_PLANK, 6}, {CLOTH, 4}, {GOLD_LEAF, 4}, {MARBLE_BLOCK, 2}}),
		/**
		 * Workshop 
		 */
		TOOL_STORE_1(22, 13699, 120, 15, 8384, new int[][] {{OAK_PLANK, 2}}),
		TOOL_STORE_2(22, 13700, 120, 25, 8385, new int[][] {{OAK_PLANK, 2}}, 13699),
		TOOL_STORE_3(22, 13701, 120, 35, 8386, new int[][] {{OAK_PLANK, 2}}, 13700),
		TOOL_STORE_4(22, 13702, 120, 44, 8387, new int[][] {{OAK_PLANK, 2}}, 13701),
		TOOL_STORE_5(22, 13703, 121, 55, 8388, new int[][] {{OAK_PLANK, 2}}, 13702),
		
		CRAFTING_TABLE_1(23, 13709, 50, 16, 8380, new int[][] {{OAK_PLANK, 4}}),
		CRAFTING_TABLE_2(23, 13710, 100, 25, 8381, new int[][] {{MOLTEN_GLASS, 1}}, 13709),
		CRAFTING_TABLE_3(23, 13711, 175, 34, 8382, new int[][] {{MOLTEN_GLASS, 2}}, 13710),
		CRAFTING_TABLE_4(23, 13712, 240, 42, 8383, new int[][] {{OAK_PLANK, 2}}, 13711),
		
		WOODEN_WORKBENCH(24, 13704, 145, 17, 8375, new int[][] {{PLANK, 5}, {NAILS, 5}}),
		OAK_WORKBENCH(24, 13705, 300, 32, 8376, new int[][] {{OAK_PLANK, 5}}),
		STEEL_FRAMED_BENCH(24, 13706, 145, 46, 8377, new int[][] {{OAK_PLANK, 6}, {STEEL_BAR, 4}}),
		BENCH_WITH_VICE(24, 13707, 750, 62, 8378, new int[][] {{OAK_PLANK, 2}, {STEEL_BAR, 1}}, 13706),
		BENCH_WITH_A_LATHE(24, 13708, 1000, 77, 8379, new int[][] {{OAK_PLANK, 2}, {STEEL_BAR, 1}}, 13707),
		
		REAPIR_BENCH(25, 13713, 240, 15, 8389, new int[][] {{OAK_PLANK, 2}}),
		WHETSTONE(25, 13714, 260, 35, 8390, new int[][] {{OAK_PLANK, 4},{LIMESTONE_BRICK, 1}}),
		ARMOUR_STAND(25, 13715, 500, 55, 8391, new int[][] {{OAK_PLANK, 8}, {LIMESTONE_BRICK, 1}}),
		
		PLUMING_STAND(26, 13716, 120, 16, 8392, new int[][] {{OAK_PLANK, 2}}),
		SHIELD_EASEL(26, 13717, 240, 41, 8393, new int[][] {{OAK_PLANK, 4}}),
		BANNER_EASEL(26, 13718, 510, 66, 8394, new int[][] {{OAK_PLANK, 8}, {CLOTH, 2}}),
		
		SHAVING_STAND(27, 13162, 30, 21, 8045, new int[][] {{PLANK, 1}, {NAILS, 1}, {MOLTEN_GLASS, 1}}),
		OAK_SHAVING_STAND(27, 13163, 61, 29, 8046, new int[][] {{OAK_PLANK, 1}, {MOLTEN_GLASS, 1}}),
		OAK_DRESSER(27, 13164, 121, 37, 8047, new int[][] {{OAK_PLANK, 2}, {MOLTEN_GLASS, 1}}),
		TEAK_DRESSER(27, 13165, 181, 46, 8048, new int[][] {{TEAK_PLANK, 2}, {MOLTEN_GLASS, 1}}),
		FANCY_TEAK_DRESSER(27, 13166, 182, 56, 8049, new int[][] {{TEAK_PLANK, 2}, {MOLTEN_GLASS, 2}}),
		MAHOGANY_DRESSER(27, 13167, 281, 64, 8050, new int[][] {{MAHOGANY_PLANK, 2}, {MOLTEN_GLASS, 1}}),
		GILDED_DRESSER(27, 13168, 582, 74, 8051, new int[][] {{MAHOGANY_PLANK, 2}, {MOLTEN_GLASS, 2}, {GOLD_LEAF, 1}}),
		
		WOODEN_BED(28, 13148, 117, 20, 8031, new int[][] {{PLANK, 3}, {NAILS, 3}, {CLOTH, 2}}),
		OAK_BED(28, 13149, 210, 30, 8032, new int[][] {{OAK_PLANK, 3}, {CLOTH, 2}}),
		LARGE_OAK_BED(28, 13150, 330, 34, 8033, new int[][] {{OAK_PLANK, 5}, {CLOTH, 2}}),
		TEAK_BED(28, 13151, 300, 40, 8034, new int[][] {{TEAK_PLANK, 3}, {CLOTH, 2}}),
		LARGE_TEAK_BED(28, 13152, 480, 45, 8035, new int[][] {{TEAK_PLANK, 5}, {CLOTH, 2}}),
		FOUR_POSTER(28, 13153, 450, 53, 8036, new int[][] {{MAHOGANY_PLANK, 3}, {CLOTH, 2}}),
		GILDED_FOUR_POSTER(28, 13154, 1330, 60, 8037, new int[][] {{MAHOGANY_PLANK, 5}, {CLOTH, 2}, {GOLD_LEAF, 2}}),

		SHOE_BOX(29, 13155, 58, 20, 8038, new int[][] {{PLANK, 2}, {NAILS, 2}}),
		OAK_DRAWERS(29, 13156, 120, 27, 8039, new int[][] {{OAK_PLANK, 2}}),
		OAK_WARDROBE(29, 13157, 180, 39, 8040, new int[][] {{OAK_PLANK, 3}}),
		TEAK_DRAWERS(29, 13158, 180, 51, 8041, new int[][] {{TEAK_PLANK, 2}}),
		TEAK_WARDROBE(29, 13159, 270, 63, 8042, new int[][] {{TEAK_PLANK, 3}}),
		MAHOGANY_WARDROBE(29, 13160, 420, 75, 8043, new int[][] {{MAHOGANY_PLANK, 3}}),
		GILDED_WARDROBE(29, 13161, 720, 87, 8044, new int[][] {{MAHOGANY_PLANK, 3}, {GOLD_LEAF, 1}}),

		OAK_CLOCK(30, 13169, 142, 25, 8052, new int[][] {{OAK_PLANK, 2}, {CLOCKWORK, 1}}),
		TEAK_CLOCK(30, 13170, 142, 55, 8053, new int[][] {{TEAK_PLANK, 2}, {CLOCKWORK, 1}}),
		GILDED_CLOCK(30, 13171, 602, 85, 8054, new int[][] {{MAHOGANY_PLANK, 2}, {CLOCKWORK, 1}, {GOLD_LEAF, 1}}),
		

		RUNE_CASE_1(31, 13507, 190, 41, 8276, new int[][] {{TEAK_PLANK, 2}, {MOLTEN_GLASS, 2}}, new int[][]{{20, 14}}),
		RUNE_CASE_2(31, 13508, 212, 41, 8277, new int[][] {{TEAK_PLANK, 2}, {MOLTEN_GLASS, 2}}, new int[][]{{20, 44}}),
		MOUNTED_BASS(32, 13488, 151, 36, 8267, new int[][] {{OAK_PLANK, 2}, {7990, 1}}),
		MOUNTED_SWORDFISH(32, 13489, 230, 56, 8268, new int[][] {{TEAK_PLANK, 2}, {7992, 1}}),
		MOUNTED_SHARK(32, 13490, 350, 76, 8269, new int[][] {{MAHOGANY_PLANK, 2}, {7994, 1}}),
		
		CRAWLING_HAND(33, 13481, 211, 38, 8260, new int[][] {{TEAK_PLANK, 2}, {7982, 1}}),
		COCKTRICE_HEAD(33, 13482, 224, 38, 8261, new int[][] {{TEAK_PLANK, 2}, {7983, 1}}),
		BASILISK_HEAD(33, 13483, 243, 38, 8262, new int[][] {{TEAK_PLANK, 2}, {7984, 1}}),
		KURASK_HEAD(33, 13484, 357, 58, 8263, new int[][] {{MAHOGANY_PLANK, 2}, {7985, 1}}),
		ABYSSAL_DEMON_HEAD(33, 13485, 389, 58, 8264, new int[][] {{MAHOGANY_PLANK, 2}, {7986, 1}}),
		KING_BLACK_DRAGON_HEAD(33, 13486, 1103, 78, 8265, new int[][] {{MAHOGANY_PLANK, 2}, {7987, 1}, {GOLD_LEAF, 2}}),
		KALPHITE_QUEEN_HEAD(33, 13487, 1103, 78, 8266, new int[][] {{MAHOGANY_PLANK, 2}, {7988, 1}, {GOLD_LEAF, 2}}),

		MITHRIL_ARMOUR(34, 13491, 135, 28, 8270, new int[][] {{1159, 1}, {1121, 1}, {1085, 1}}, new int[][] {{13, 68}}),
		ADAMANT_ARMOUR(34, 13492, 150, 28, 8271, new int[][] {{1161, 1}, {1123, 1}, {1087, 1}}, new int[][] {{13, 88}}),
		RUNE_ARMOUR(34, 13493, 165, 28, 8272, new int[][] {{1163, 1}, {1125, 1}, {1089, 1}}, new int[][] {{13, 99}}),
		
		BASIC_DECORATIVE_ARMOUR_STAND(35, 13495, 135, 28, 8273, new int[][] {{4071, 1}, {4072, 1}, {4069, 1}}),
		DETAILED_DECORATIVE_ARMOUR_STAND(35, 13496, 150, 28, 8274, new int[][] {{4506, 1}, {4507, 1}, {4504, 1}}),
		INTRICATE_DECORATIVE_ARMOUR_STAND(35, 13497, 165, 28, 8275, new int[][] {{4511, 1}, {4512, 1}, {4509, 1}}),
		PROFOUND_DECORATIVE_ARMOUR_STAND(35, 34281, 180, 28, 18755, new int[][] {{18708, 1}, {18709, 1}, {18706, 1}}),

		OAK_STAIRCASE(36, 13497, 680, 27, 8249, new int[][] {{OAK_PLANK, 10}, {STEEL_BAR, 4}}),
		TEAK_STAIRCASE(36, 13499, 980, 48, 8252, new int[][] {{TEAK_PLANK, 10}, {STEEL_BAR, 4}}),
		SPIRAL_STAIRCASE(36, 13503, 1040, 67, 8258, new int[][] {{TEAK_PLANK, 10}, {LIMESTONE_BRICK, 7}}),
		MARBLE_STAIRCASE(36, 13501, 3200, 82, 8255, new int[][] {{MAHOGANY_PLANK, 5}, {MARBLE_BLOCK, 5}}),
		MARBLE_SPIRAL(36, 13505, 4400, 97, 8259, new int[][] {{TEAK_PLANK, 10}, {MARBLE_BLOCK, 10}}),

		OAK_STAIRCASE_1(37, 13498, 680, 27, 8249, new int[][] {{OAK_PLANK, 10}, {STEEL_BAR, 4}}),
		TEAK_STAIRCASE_1(37, 13500, 980, 48, 8252, new int[][] {{TEAK_PLANK, 10}, {STEEL_BAR, 4}}),
		SPIRAL_STAIRCASE_1(37, 13504, 1040, 67, 8258, new int[][] {{TEAK_PLANK, 10}, {LIMESTONE_BRICK, 7}}),
		MARBLE_STAIRCASE_1(37, 13502, 3200, 82, 8255, new int[][] {{MAHOGANY_PLANK, 5}, {MARBLE_BLOCK, 5}}),
		MARBLE_SPIRAL_1(37, 13506, 4400, 97, 8259, new int[][] {{TEAK_PLANK, 10}, {MARBLE_BLOCK, 10}}),
		/*
		 * get me hotspot ids
		 */
		CLAY_ATTACK_STONE(39, 13392, 100, 39, 8153, new int[][] {{SOFT_CLAY, 10}}),
		ATTACK_STONE(39, 13393, 200, 59, 8154, new int[][] {{LIMESTONE_BRICK, 10}}),
		MARBLE_ATTACK_STONE(39, 13394, 2000, 79, 8155, new int[][] {{MARBLE_BLOCK, 10}}),
		
		ELEMENTAL_BALANCE_1(40, 13395, 179, 37, 8156, new int[][] {{AIR_RUNE, 500},{EARTH_RUNE, 500},{WATER_RUNE, 500},{FIRE_RUNE,500}}),
		ELEMENTAL_BALANCE_2(40, 13396, 252, 57, 8157, new int[][] {{AIR_RUNE, 1000},{EARTH_RUNE, 1000},{WATER_RUNE, 1000},{FIRE_RUNE,1000}}),
		ELEMENTAL_BALANCE_3(40, 13397, 356, 77, 8158, new int[][] {{AIR_RUNE, 2000},{EARTH_RUNE, 2000},{WATER_RUNE, 2000},{FIRE_RUNE,2000}}),
		
		JESTER(42, 13390, 360, 39, 8159, new int[][] {{TEAK_PLANK, 4}}),
		TREASURE_HUNT(42, 13379, 800, 49, 8160, new int[][] {{TEAK_PLANK, 8}, {STEEL_BAR, 4}}),
		HANGMAN(42, 13404, 1200, 59, 8161, new int[][] {{TEAK_PLANK, 12},{STEEL_BAR,6}}),
		
		OAK_PRIZED_CHEST(41, 13384, 240, 34, 8165, new int[][] {{OAK_PLANK, 4}}),
		TEAK_PRIZED_CHEST(41, 13386, 660, 34, 8166, new int[][] {{TEAK_PLANK, 4},{GOLD_LEAF,4}}),
		MAHOGANY_PRIZED_CHEST(41, 13388, 860, 54, 8167, new int[][] {{MAHOGANY_PLANK, 4},{GOLD_LEAF,4}}),
		
		HOOP_AND_STICK(38, 13399, 120, 30, 8162, new int[][] {{OAK_PLANK, 4}}),
		DARTBOARD(38, 13400, 290, 54, 8163, new int[][] {{TEAK_PLANK, 3},{STEEL_BAR,1}}),
		ARCHERY_TARGET(38, 13402, 600, 81, 8164, new int[][] {{TEAK_PLANK, 6},{STEEL_BAR,3}}),
		

		GLOVE_RACK(43, 13381, 120, 34, 8028, new int[][] {{OAK_PLANK, 2}}),
		WEAPONS_RACK(43, 13382, 180, 44, 8029, new int[][] {{TEAK_PLANK, 2}}),
		EXTRA_WEAPONS_RACK(43, 13383, 440, 54, 8030, new int[][] {{TEAK_PLANK, 4}, {STEEL_BAR, 4}}),

		BOXING_RING(44, 13126, 570, 32, 8023, new int[][] {{OAK_PLANK, 6}, {CLOTH,4}}),
		FENCING_RING(44, 13133, 570, 41, 8024, new int[][] {{OAK_PLANK, 8}, {CLOTH,6}}),
		COMBAT_RING(44, 13137, 630, 51, 8025, new int[][] {{TEAK_PLANK, 6}, {CLOTH,6}}),
		RANGING_PEDESTALS(44, 13145, 620, 71, 8026, new int[][] {{TEAK_PLANK, 8}}),// wall type (0)
		BALANCE_BEAM(44, 13142, 1000, 81, 8027, new int[][] {{TEAK_PLANK, 10}, {STEEL_BAR,5}}),//ground dec type (22)
		

		SMALL_MAP(45, 13525, 211, 38, 8294, new int[][] {{TEAK_PLANK, 2}, {8004,1}}),
		MEDIUM_MAP(45, 13526, 451, 58, 8295, new int[][] {{MAHOGANY_PLANK, 3}, {8005,1}}),
		LARGE_MAP(45, 13527, 591, 78, 8296, new int[][] {{MAHOGANY_PLANK, 4}, {8006,1}}),
		
		SILVERLIGHT(46, 13519, 187, 42, 8279, new int[][] {{TEAK_PLANK, 2}, {2402,1}}),
		EXCALIBUR(46, 13520, 194, 42, 8280, new int[][] {{TEAK_PLANK, 2}, {35,1}}),
		DARKLIGHT(46, 13521, 202, 42, 8281, new int[][] {{TEAK_PLANK, 2}, {6746,1}}),

		LUMBRIDGE(47, 13517, 314, 44, 8289, new int[][] {{TEAK_PLANK, 3}, {8002,1}}),
		THE_DESERT(47, 13514, 314, 44, 8290, new int[][] {{TEAK_PLANK, 3}, {8003,1}}),
		MORYTANIA(47, 13518, 314, 44, 8291, new int[][] {{TEAK_PLANK, 3}, {8004,1}}),
		KARAMJA(47, 13516, 464, 65, 8292, new int[][] {{MAHOGANY_PLANK, 3}, {8005,1}}),
		ISAFDAR(47, 13515, 464, 65, 8293, new int[][] {{MAHOGANY_PLANK, 3}, {8006,1}}),

		KING_ARTHUR(48, 13510, 211, 35, 8285, new int[][] {{TEAK_PLANK, 2}, {7995,1}}),
		ELENA(48, 13511, 211, 35, 8286, new int[][] {{TEAK_PLANK, 2}, {7996,1}}),
		GIANT_DWARF(48, 13512, 211, 35, 8287, new int[][] {{TEAK_PLANK, 2}, {7997,1}}),
		MISCELLANIANS(48, 13513, 311, 55, 8288, new int[][] {{MAHOGANY_PLANK, 2}, {7998,1}}),

		ANTI_DRAGON_SHIELD(49, 13522, 280, 47, 8282, new int[][] {{TEAK_PLANK, 3}, {1540,1}}),
		AMULET_OF_GLORY(49, 13523, 290, 47, 8283, new int[][] {{TEAK_PLANK, 3}, {1704,1}}),
		CAPE_OF_LEGENDS(49, 13524, 300, 47, 8284, new int[][] {{TEAK_PLANK, 3}, {1052,1}}),

		OAK_PET_HOUSE(50, 44828, 240, 37, 15227, new int[][] {{OAK_PLANK, 4}}),
		TEAK_PET_HOUSE(50, 44829, 380, 52, 15228, new int[][] {{TEAK_PLANK, 4}}),
		MAHOGANY_PET_HOUSE(50, 44830, 580, 67, 15229, new int[][] {{MAHOGANY_PLANK, 4}}),
		CONSECRATED_PET_HOUSE(50, 44831, 1580, 92, 15230, new int[][] {{MAHOGANY_PLANK, 4}, {MAGIC_STONE, 1}}),
		DESECRATED_PET_HOUSE(50, 44832, 1580, 92, 15231, new int[][] {{MAHOGANY_PLANK, 4}, {MAGIC_STONE, 1}}),
		NATURAL_PET_HOUSE(50, 44833, 1580, 92, 15232, new int[][] {{MAHOGANY_PLANK, 4}, {MAGIC_STONE, 1}}),

		OAK_PET_FEEDER(51, 44834, 240, 37, 15233, new int[][] {{OAK_PLANK, 4}}),
		TEAK_PET_FEEDER(51, 44835, 380, 52, 15234, new int[][] {{TEAK_PLANK, 4}}),
		MAHOGANY_PET_FEEDER(51, 44836, 880, 67, 15235, new int[][] {{MAHOGANY_PLANK, 4}, {GOLD_LEAF, 1}}),
		
		MINI_OBELISK(52, 44837, 676, 41, 15236, new int[][] {{MARBLE_BLOCK, 1}, {12183, 1000}}),

		GARDEN_HABITAT(53, 0, 201, 37, 15222, new int[][] {{8431, 1}, {8433, 1}, {8435, 1}}),
		JUNGLEN_HABITAT(53, 1, 287, 47, 15223, new int[][] {{1929, 5}, {8435, 3}, {8423, 1}}),
		DESERT_HABITAT(53, 2, 238, 57, 15224, new int[][] {{1783, 10}, {LIMESTONE_BRICK, 5}, {15237, 1}}),
		POLAR_HABITAT(53, 3, 373, 67, 15225, new int[][] {{556, 1000}, {555, 1000}, {15239, 1}}),
		VOLCANIC_HABITAT(53, 4, 77, 77, 15226, new int[][] {{4699, 1000}, {13245, 5}, {8417, 1}}),
		

		ALCHEMICAL_CHART(54, 13662, 30, 43, 8354, new int[][] {{CLOTH, 2}}),
		ASTRONOMICAL_CHART(54, 13663, 30, 63, 8355, new int[][] {{CLOTH, 3}}),
		INFERNO_CHART(54, 13664, 30, 83, 8356, new int[][] {{CLOTH, 4}}),

		OAK_LECTERN(55, 13642, 60, 40, 8334, new int[][] {{OAK_PLANK, 1}}),
		EAGLE_LECTERN(55, 13643, 120, 47, 8335, new int[][] {{OAK_PLANK, 2}}),
		DEMON_LECTERN(55, 13644, 120, 47, 8336, new int[][] {{OAK_PLANK, 2}}),
		TEAK_EAGLE_LECTERN(55, 13645, 180, 57, 8337, new int[][] {{TEAK_PLANK, 2}}),
		TEAK_DEMON_LECTERN(55, 13646, 180, 57, 8338, new int[][] {{TEAK_PLANK, 2}}),
		MAHOGANY_EAGLE_LECTERN(55, 13647, 570, 67, 8339, new int[][] {{MAHOGANY_PLANK, 2}, {GOLD_LEAF, 1}}),
		MAHOGANY_DEMON_LECTERN(55, 13648, 570, 67, 8340, new int[][] {{MAHOGANY_PLANK, 2}, {GOLD_LEAF, 1}}),
		
		STATUE(56, 48644, 2, 1, 15521, new int[][] {{15521, 1}}),

		CRYSTAL_BALL(57, 13659, 280, 42, 8351, new int[][] {{TEAK_PLANK, 3}, {567, 1}}),
		ELEMENTAL_SPHERE(57, 13660, 580, 54, 8352, new int[][] {{TEAK_PLANK, 3}, {567, 1}, {GOLD_LEAF, 1}}),
		CRYSTAL_OF_POWER(57, 13661, 890, 66, 8353, new int[][] {{MAHOGANY_PLANK, 2}, {567, 1}, {GOLD_LEAF,1 }}),

		GLOBE(58, 13649, 180, 41, 8341, new int[][] {{OAK_PLANK, 3}}),
		ORNAMENTAL_GLOBE(58, 13650, 270, 50, 8342, new int[][] {{TEAK_PLANK, 3}}),
		LUNAR_GLOBE(58, 13651, 570, 59, 8343, new int[][] {{TEAK_PLANK, 3}, {GOLD_LEAF, 1}}),
		CELESTIAL_GLOBE(58, 13652, 570, 68, 8344, new int[][] {{TEAK_PLANK, 3}, {GOLD_LEAF, 1}}),
		ARMILLARY_GLOBE(58, 13653, 960, 77, 8345, new int[][] {{MAHOGANY_PLANK, 2} , {GOLD_LEAF, 2}, {STEEL_BAR, 4}}),
		SMALL_ORRERY(58, 13654, 1320, 86, 8346, new int[][] {{MAHOGANY_PLANK, 3}, {GOLD_LEAF, 3}}),
		LARGE_ORRERY(58, 13655, 1420, 95, 8347, new int[][] {{MAHOGANY_PLANK, 3}, {GOLD_LEAF, 5}}),

		WOODEN_TELESCOPE(59, 13656, 121, 44, 8348, new int[][] {{OAK_PLANK, 2}, {MOLTEN_GLASS, 1}}),
		TEAK_TELESCOPE(59, 13657, 181, 64, 8349, new int[][] {{TEAK_PLANK, 2}, {MOLTEN_GLASS, 1}}),
		MAHOGANY_TELESCOPE(59, 13658, 580, 84, 8350, new int[][] {{MAHOGANY_PLANK, 2}, {MOLTEN_GLASS, 1}}),
		
		/*
		 * Costume room
		 */
		OAK_TREASURE_CHEST(60, 18804, 450, 48, 9839, new int[][] {{OAK_PLANK, 2}}),
		TEAK_TREASURE_CHEST(60, 18806, 1300, 66, 9840, new int[][] {{TEAK_PLANK, 2}}),
		MOHOGANY_TREASURE_CHEST(60, 18808, 2300, 84, 9841, new int[][] {{MAHOGANY_PLANK, 2}}),
		
		OAK_ARMOR_CASE(63, 18778, 650, 46, 9826, new int[][] {{OAK_PLANK, 3}}),
		TEAK_ARMOR_CASE(63, 18780, 950, 64, 9827, new int[][] {{TEAK_PLANK, 3}}),
		MOHOGANY_ARMOR_CASE(63, 18782, 3500, 82, 9828, new int[][] {{MAHOGANY_PLANK, 3}}),
		
		OAK_MAGIC_WARDROBE(64, 18784, 1350, 42, 9829, new int[][] {{OAK_PLANK, 4}}),
		CARVED_OAK_MAGIC_WARDROBE(64, 18786, 1500, 51, 9830, new int[][] {{OAK_PLANK, 6}}),
		TEAK_MAGIC_WARDROBE(64, 18788, 1750, 60, 9831, new int[][] {{TEAK_PLANK, 4}}),
		CARVED_TEAK_MAGIC_WARDROBE(64, 18790, 2150, 69, 9832, new int[][] {{TEAK_PLANK, 6}}),
		MAHOGANY_MAGIC_WARDROBE(64, 18792, 3100, 78, 9833, new int[][] {{MAHOGANY_PLANK, 4}}),
		GILDED_MAGIC_WARDROBE(64, 18794, 3500, 87, 9834, new int[][] {{MAHOGANY_PLANK, 4},{GOLD_LEAF, 1}}),
		MARBLE_MAGIC_WARDROBE(64, 18796, 4150, 96, 9835, new int[][] {{MARBLE_BLOCK, 4}}),
		
		OAK_CAPE_RACK(65, 18766, 2600, 54, 9817, new int[][] {{OAK_PLANK, 4}}),
		TEAK_CAPE_RACK(65, 18767, 2850, 63, 9818, new int[][] {{TEAK_PLANK, 4}}),
		MAHOGANY_CAPE_RACK(65, 18768, 3120, 72, 9819, new int[][] {{MAHOGANY_PLANK, 4}}),
		GILDED_CAPE_RACK(65, 18769, 3640, 81, 9820, new int[][] {{MAHOGANY_PLANK, 4},{GOLD_LEAF,1}}),
		MARBLE_CAPE_RACK(65, 18770, 3850, 90, 9821, new int[][] {{MARBLE_BLOCK, 1}}),
		MAGIC_CAPE_RACK(65, 18770, 4250, 99, 9822, new int[][] {{MAGIC_STONE, 1}}),
		
		OAK_TOY_BOX(62, 18798, 1650, 50, 9836, new int[][] {{OAK_PLANK, 2}}),
		TEAK_TOY_BOX(62, 18800, 1890, 68, 9837, new int[][] {{TEAK_PLANK, 2}}),
		MAHOGANY_TOY_BOX(62, 18802, 2480, 86, 9838, new int[][] {{MAHOGANY_PLANK, 2}}),
		
		OAK_FANCY_DRESS_BOX(61, 18772, 850, 44, 9823, new int[][] {{OAK_PLANK, 2}}),
		TEAK_FANCY_DRESS_BOX(61, 18774, 1300, 62, 9824, new int[][] {{TEAK_PLANK, 2}}),
		MAHOGANY_FANCY_DRESS_BOX(61, 18776, 1890, 80, 9825, new int[][] {{MAHOGANY_PLANK, 2}}),
		

		SMALL_STATUES(66, 13271, 40, 49, 8082, new int[][] {{LIMESTONE_BRICK, 2}}),
		MEDIUM_STATUES(66, 13272, 500, 69, 8083, new int[][] {{MARBLE_BLOCK, 1}}),
		LARGE_STATUES(66, 13273, 1500, 89, 8084, new int[][] {{MARBLE_BLOCK, 3}}),

		STEEL_TORCHES(67, 13200, 80, 45, 8070, new int[][] {{STEEL_BAR, 2}}),
		WOODEN_TORCHES(67, 13202, 58, 49, 8069, new int[][] {{PLANK, 2}, {NAILS, 2}}),
		STEEL_CANDLESTICKS(67, 13204, 124, 53, 8071, new int[][] {{STEEL_BAR, 6} , {36, 6}}),
		GOLD_CANDLESTICKS(67, 13206, 46, 57, 8072, new int[][] {{2357, 6}, {36, 6}}),
		INCENSE_BURNERS(67, 13208, 280, 61, 8073, new int[][] {{OAK_PLANK, 4}, {STEEL_BAR, 2}}),
		MAHOGANY_BURNERS(67, 13210, 600, 65, 8074, new int[][] {{MAHOGANY_PLANK, 4}, {STEEL_BAR, 2}}),
		MARBLE_BURNERS(67, 13212, 1600, 69, 8075, new int[][] {{MARBLE_BLOCK, 2}, {STEEL_BAR, 2}}),

		WINDCHIMES(68, 13214, 323, 49, 8079, new int[][] {{OAK_PLANK, 4}, {NAILS,4}, {STEEL_BAR, 4}}),
		BELLS(68, 13215, 480, 58, 8080, new int[][] {{TEAK_PLANK, 4}, {STEEL_BAR, 6}}),
		ORGAN(68, 13216, 680, 69, 8081, new int[][] {{MAHOGANY_PLANK, 4}, {STEEL_BAR, 6}}),

		OAK_ALTAR(69, 13179, 240, 45, 8062, new int[][] {{OAK_PLANK, 4}}),
		TEAK_ALTAR(69, 13182, 360, 50, 8063, new int[][] {{TEAK_PLANK, 4}}),
		CLOTH_ALTAR(69, 13185, 390, 56, 8064, new int[][] {{TEAK_PLANK, 4}, {CLOTH, 2}}),
		MAHOGANY_ALTAR(69, 13188, 590, 60, 8065, new int[][] {{MAHOGANY_PLANK, 4}, {CLOTH, 2}}),
		LIMESTONE_ALTAR(69, 13191, 910, 64, 8066, new int[][] {{MAHOGANY_PLANK, 4}, {CLOTH, 2}, {LIMESTONE_BRICK, 2}}),
		MARBLE_ALTAR(69, 13194, 1030, 70, 8067, new int[][] {{MARBLE_BLOCK, 2}, {CLOTH, 2}}),
		GILDED_ALTAR(69, 13197, 2230, 75, 8068, new int[][] {{MARBLE_BLOCK, 2}, {CLOTH, 2}, {GOLD_LEAF, 4}}),

		GUTHIX_SYMBOL(70, 13174, 120, 48, 8057, new int[][] {{OAK_PLANK, 2}}),
		SARADOMIN_SYMBOL(70, 13172, 120, 48, 8055, new int[][] {{OAK_PLANK, 2}}),
		ZAMORAK_SYMBOL(70, 13173, 120, 48, 8056, new int[][] {{OAK_PLANK, 2}}),
		GUTHIX_ICON(70, 13177, 960, 59, 8060, new int[][] {{TEAK_PLANK, 4}, {GOLD_LEAF, 2}}),
		SARADOMIN_ICON(70, 13175, 960, 59, 8058, new int[][] {{TEAK_PLANK, 4}, {GOLD_LEAF, 2}}),
		ZAMORAK_ICON(70, 13176, 960, 59, 8059, new int[][] {{TEAK_PLANK, 4}, {GOLD_LEAF, 2}}),
		ICON_OF_BOB(70, 13178, 1160, 71, 8061, new int[][] {{MAHOGANY_PLANK, 4}, {GOLD_LEAF, 2}}),

		SHUTTERED_WINDOW(71, 13217, 228, 49, 8076, new int[][] {{PLANK, 8}, {NAILS, 8}}),
		DECORATIVE_WINDOW(71, 13220, 400, 69, 8077, new int[][] {{MOLTEN_GLASS, 8}}),
		STAINED_GLASS(71, 13221, 400, 79, 8078, new int[][] {{MOLTEN_GLASS, 16}}),
		
		TEAK_PORTAL(72, 13636, 270, 50, 8328, new int[][] {{TEAK_PLANK, 3}}),
		MAHOGANY_PORTAL(72, 13637, 500, 65, 8329, new int[][] {{MAHOGANY_PLANK, 3}}),
		MARBLE_PORTAL(72, 13638, 2000, 80, 8330, new int[][] {{MARBLE_BLOCK, 3}}),

		TELEPORT_FOCUS(73, 13640, 40, 50, 8331, new int[][] {{LIMESTONE_BRICK, 2}}),
		GREATER_FOCUS(73, 13641, 500, 65, 8332, new int[][] {{MARBLE_BLOCK, 1}}),
		SCYING_POOL(73, 13639, 2000, 80, 8333, new int[][] {{MARBLE_BLOCK, 4}}),
		
		/*
		 * Formal Garden
		 */
		EXIT_PORTAL_(74, 13405, 100, 1, 8168, new int[][] {{IRON_BAR, 10}}),
		GAZEBO(74, 13477, 1200, 65, 8192, new int[][] {{MAHOGANY_PLANK, 4},{STEEL_BAR, 4}}),
		DUNGEON_ENTERANCE(74, 13409, 500, 70, 8172, new int[][] {{MARBLE_BLOCK, 1}}),
		SMALL_FOUNTAIN(74, 13478, 500, 71, 8193, new int[][] {{MARBLE_BLOCK, 1}}),
		LARGE_FOUNTAIN(74, 13479, 1000, 75, 8194, new int[][] {{MARBLE_BLOCK, 2}}),
		POSH_FOUNTAIN(74, 13480, 1500, 81, 8195, new int[][] {{MARBLE_BLOCK, 3}}),
		
		SUN_FLOWER(75, 13443, 70, 66, 8213, new int[][] {{BSF, 1},{WATER_CAN, 1}}),
		MARIGOLDS(75, 13444, 100, 71, 8214, new int[][] {{BM, 1},{WATER_CAN, 1}}),
		ROSES(75, 13448, 100, 76, 8215, new int[][] {{BR, 1},{WATER_CAN, 1}}),
		
		ROSEMARY(76, 13437, 70, 66, 8210, new int[][] {{BRM, 1},{WATER_CAN, 1}}),
		DAFFODILS(76, 13441, 100, 71, 8211, new int[][] {{BD, 1},{WATER_CAN, 1}}),
		BLUEBELLS(76, 13439, 122, 76, 8212, new int[][] {{BBB, 1},{WATER_CAN, 1}}),
		
		THORNY_HEDGE(77, 13456, 70, 56, 8203, new int[][] {{BTH, 1},{WATER_CAN, 1}}),
		NICE_HEDGE(77, 13459, 100, 60, 8204, new int[][] {{BNH, 1},{WATER_CAN, 1}}),
		SMALL_BOX_HEDGE(77, 13462, 122, 64, 8205, new int[][] {{SBH, 1},{WATER_CAN, 1}}),
		TOPIARY_HEDGE(77, 13465, 141, 68, 8206, new int[][] {{TH, 1},{WATER_CAN, 1}}),
		FANCY_HEDGE(77, 13468, 158, 72, 8207, new int[][] {{FH, 1},{WATER_CAN, 1}}),
		TALL_FANCY_HEDGE(77, 13471, 223, 76, 8208, new int[][] {{TFH, 1},{WATER_CAN, 1}}),
		TALL_BOX_HEDGE(77, 13474, 316, 80, 8209, new int[][] {{TFH, 1},{WATER_CAN, 1}}),
		
		BOUNDARY_STONES(78, 13449, 100, 55, 8196, new int[][] {{SOFT_CLAY, 10}}),
		WOODEN_FENCE(78, 13450, 280, 59, 8197, new int[][] {{PLANK, 10}}),
		STONE_WALL(78, 13451, 200, 63, 8198, new int[][] {{LIMESTONE_BRICK, 10}}),
		IRON_RAILINGS(78, 13452, 220, 67, 8199, new int[][] {{IRON_BAR, 10},{LIMESTONE_BRICK, 6}}),
		PICKET_FENCE(78, 13453, 640, 71, 8200, new int[][] {{OAK_PLANK, 10},{STEEL_BAR, 2}}),
		GARDEN_FENCE(78, 13454, 940, 75, 8201, new int[][] {{TEAK_PLANK, 10},{STEEL_BAR, 2}}),
		MARBLE_WALL(78, 13455, 4000, 79, 8202, new int[][] {{MARBLE_BLOCK, 8}}),
		
		/*
		 * Throne room
		 */
		OAK_THRONE(79, 13665, 800, 60, 8357, new int[][] {{OAK_PLANK, 5},{MARBLE_BLOCK, 2}}),
		TEAK_THRONE(79, 13666, 1450, 67, 8358, new int[][] {{TEAK_PLANK, 5},{MARBLE_BLOCK, 2}}),
		MAHOGANY_THRONE(79, 13667, 2200, 74, 8359, new int[][] {{MAHOGANY_PLANK, 10},{MARBLE_BLOCK, 3}}),
		GILDED_THRONE(79, 13668, 1700, 81, 8360, new int[][] {{MAHOGANY_PLANK, 10},{MARBLE_BLOCK, 3},{GOLD_LEAF, 3}}),
		SKELETON_THRONE(79, 13669, 7003, 88, 8361, new int[][] {{OAK_PLANK, 10},{MARBLE_BLOCK, 2},{BONES, 5},{SKULLS, 2}}),
		CRYSTAL_THRONE(79, 13670, 15000, 95, 8362, new int[][] {{MAGIC_STONE, 15}}),
		DEMONIC_THRONE(79, 13671, 25000, 99, 8363, new int[][] {{MAGIC_STONE, 25}}),
		
		OAK_LEVER(80, 13672, 300, 68, 8364, new int[][] {{OAK_PLANK, 5}}),
		TEAK_LEVER(80, 13673, 450, 78, 8365, new int[][] {{TEAK_PLANK, 5}}),
		MAHOGANY_LEVER(80, 13674, 700, 88, 8366, new int[][] {{MAHOGANY_PLANK, 5}}),
		
		OAK_TRAPDOOR(81, 13675, 300, 68, 8367, new int[][] {{OAK_PLANK, 5}}),
		TEAK_TRAPDOOR(81, 13676, 450, 78, 8368, new int[][] {{TEAK_PLANK, 5}}),
		MAHOGANY_TRAPDOOR(81, 13677, 700, 88, 8369, new int[][] {{MAHOGANY_PLANK, 5}}),
		
		OAK_DECORATION_(83, 13798, 120, 16, 8102, new int[][] {{OAK_PLANK, 2}}),
		TEAK_DECORATION_(83, 13814, 180, 36, 8103, new int[][] {{TEAK_PLANK, 2}}),
		GILDED_DECORATION_(83, 13782, 1020, 56, 8104, new int[][] {{MAHOGANY_PLANK, 3}, {GOLD_LEAF, 2}}),
		ROUND_SHIELD(83, 13734, 120, 66, 8105, new int[][] {{OAK_PLANK, 2}}),
		SQUARE_SHEILD(83, 13766, 330, 76, 8106, new int[][] {{TEAK_PLANK, 4}}),
		KITE_DECORATION(83, 13750, 420, 86, 8107, new int[][] {{MAHOGANY_PLANK, 3}}),
		
		FLOOR_DECORATION(84, 13684, 700, 61, 8370, new int[][] {{MAHOGANY_PLANK, 5}}),
		STEEL_CAGE(84, 13685, 1100, 68, 68, new int[][] {{MAHOGANY_PLANK, 5},{STEEL_BAR, 20}}),//13681
		TRAPDOOR(84, 13686, 770, 74, 8372, new int[][] {{MAHOGANY_PLANK, 5},{STEEL_BAR, 20}}),//13680
		LESSER_MAGIC_CIRCLE(84, 13687, 2700, 82, 8373, new int[][] {{MAHOGANY_PLANK, 2},{MAGIC_STONE, 2}}),//13682
		GREATER_MAGIC_CIRCLE(84, 13688, 4700, 89, 8374, new int[][] {{MAHOGANY_PLANK, 5},{MAGIC_STONE, 4}}),//13683
		
		/*
		 * Oubliette room
		 */
		TENTACLE_POOL(85, 13331, 326, 71, 8303, new int[][] {{BOW, 20},{COIN, 100000}}),
		SPIKES(85, 13334, 623, 65, 8302, new int[][] {{STEEL_BAR, 20},{COIN, 50000}}),
		FLAME_PIT(85, 13337, 357, 77, 8304, new int[][] {{STEEL_BAR, 20},{COIN, 50000}}),
		ROCNAR(85, 13373, 387, 83, 8305, new int[][] {{COIN, 150000}}),
		
		//+2 more each type pls (cages only)
		OAK_CAGE(86, 13313, 640, 65, 8297, new int[][] {{OAK_PLANK, 10},{STEEL_BAR, 2}}),
		OAK_AND_STEEL_CAGE(86, 13316, 800, 70, 8298, new int[][] {{OAK_PLANK, 10},{STEEL_BAR, 10}}),
		STEEL_CAGE_(86, 13319, 400, 75, 8299, new int[][] {{STEEL_BAR, 2}}),
		SPIKED_CAGE(86, 13322, 500, 80, 8300, new int[][] {{STEEL_BAR, 2}}),
		BONE_CAGE(86, 13325, 603, 85, 8301, new int[][] {{STEEL_BAR, 2}}),
		
		SKELETON_GUARD(87, 13366, 223, 70, 8131, new int[][] {{COIN, 50000}}),
		GUARD_DOG(87, 13367, 273, 74, 8132, new int[][] {{COIN, 75000}}),
		HOBGOBLIN(87, 13368, 316, 78, 8133, new int[][] {{COIN, 100000}}),
		BABY_RED_DRAGON(87, 13372, 387, 82, 8134, new int[][] {{COIN, 150000}}),
		HUGE_SPIDER(87, 13370, 447, 86, 8135, new int[][] {{COIN, 200000}}),
		TROLL(87, 13369, 1000, 90, 8136, new int[][] {{COIN, 1000000}}),
		HELLHOUND(87, 2715, 2236, 94, 8137, new int[][] {{COIN, 5000000}}),
		
		OAK_LADDER(88, 13328, 300, 68, 8306, new int[][] {{OAK_PLANK, 5}}),
		TEAK_LADDER(88, 13329, 450, 78, 8307, new int[][] {{TEAK_PLANK, 5}}),
		MAHOGANY_LADDER(88, 13330, 700, 88, 8308, new int[][] {{MAHOGANY_PLANK, 5}}),
		
		DECORATIVE_BLOOD(89, 13312, 4, 72, 8125, new int[][] {{RD, 4}}),
		DECORATIVE_PIPE(89, 13311, 120, 83, 8126, new int[][] {{STEEL_BAR, 6}}),
		HANGING_SKELETON(89, 13310, 3, 94, 8127, new int[][] {{SKULLS, 2},{BONES, 6}}),
		
		CANDLE(90, 13342, 243, 83, 8128, new int[][] {{OAK_PLANK, 4},{LIT_CANDLE, 4}}),
		TORCH(90, 13343, 244, 84, 8129, new int[][] {{OAK_PLANK, 4},{LIT_CANDLE, 4}}),
		SKULL_TORCH(90, 13343, 244, 84, 8130, new int[][] {{OAK_PLANK, 4},{LIT_CANDLE, 4},{SKULLS, 4}}),

		SPIKE_TRAP(91, 13356, 223, 72, 8143, new int[][] {{COIN, 50000}}),
		MAN_TRAP(91, 13357, 273, 76, 8144, new int[][] {{COIN, 75000}}),
		TANGLE_TRAP(91, 13358, 323, 80, 8145, new int[][] {{COIN, 100000}}),
		MARBLE_TRAP(91, 13359, 387, 84, 8146, new int[][] {{COIN, 150000}}),
		TELEPORT_TRAP(91, 13360, 470, 88, 8147, new int[][] {{COIN, 200000}}),
		
		OAK_DOOR(92, 13344, 600, 74, 8122, new int[][] {{OAK_PLANK, 10}}),
		STEEL_PLATED_DOOR(92, 13346, 800, 84, 8123, new int[][] {{OAK_PLANK, 10}, {STEEL_BAR, 10}}),
		MARBLE_DOOR(92, 13348, 2000, 94, 8124, new int[][] {{MARBLE_BLOCK, 4}}),
		/**
		 * Pit
		 */
		MINOR_PIT_TRAP(93, 39266, 304, 71, 18797, new int[][] {{COIN, 45000}, {554, 500}}),
		MAJOR_PIT_TRAP(93, 39268, 1000, 83, 18798, new int[][] {{COIN, 125000}, {554, 2500}}),
		SUPERIOR_PIT_TRAP(93, 39270, 1100, 96, 18799, new int[][] {{COIN, 850000}, {554, 4500}}),

		PIT_DOG(94, 39260, 200, 70, 18791, new int[][] {{COIN, 40000}}),
		PIT_OGRE(94, 39261, 234, 73, 18792, new int[][] {{COIN, 55000}}),
		PIT_PROTECTOR(94, 39262, 300, 79, 18793, new int[][] {{COIN, 90000}}),
		PIT_SCARABITE(94, 39263, 387, 84, 18794, new int[][] {{COIN, 150000}}),
		PIT_BLACK_DEMON(94, 39264, 547, 89, 18795, new int[][] {{COIN, 300000}}),
		PIT_IRON_DRAGON(94, 39265, 2738, 97, 18796, new int[][] {{COIN, 7500000}}),

		/**
		 * Treasure room
		 */
		DEMON(95, 13378, 707, 75, 8138, new int[][] {{COIN, 500000}}),
		KALPHITE_SOLDIER(95, 13374, 866, 80, 8139, new int[][] {{COIN, 750000}}),
		TOK_XIL(95, 13377, 2236, 85, 8140, new int[][] {{COIN, 5000000}}),
		DAGANNOTH(95, 13376, 2738, 90, 8141, new int[][] {{COIN, 7500000}}),
		STEEL_DRAGON(95, 13375, 3162, 95, 8142, new int[][] {{COIN, 10000000}}),

		WOODEN_CRATE(96, 13283, 143, 75, 8148, new int[][] {{PLANK, 5}, {NAILS, 5}}),
		OAK_CRATE(96, 13285, 340, 79, 8149, new int[][] {{OAK_PLANK, 5}, {STEEL_BAR, 2}}),
		TEAK_CRATE(96, 13287, 530, 83, 8150, new int[][] {{TEAK_PLANK, 5}, {STEEL_BAR, 4}}),
		MAHOGANY_CRATE(96, 13289, 1000, 87, 8151, new int[][] {{MAHOGANY_PLANK, 5}, {GOLD_LEAF, 1}}),
		MAGIC_CRATE(96, 13291, 1000, 91, 8152, new int[][] {{MAGIC_STONE, 1}}),
		
		
		;
		private int hotspotId, furnitureId, xp, level, itemId;
		private int[][] requiredItems, additionalSkillRequirements;
		private int furnitureRequired;

		private Furniture(int hotspotId, int furnitureId, int xp, int level,
				int itemId, int[][] requiredItems) {
			this.hotspotId = hotspotId;
			this.furnitureId = furnitureId;
			this.xp = xp;
			this.level = level;
			this.requiredItems = requiredItems;
			this.itemId = itemId;
			furnitureRequired = -1;
		}
		private Furniture(int hotspotId, int furnitureId, int xp, int level,
				int itemId, int[][] requiredItems, int[][] additionalSkillRequirements) {
			this(hotspotId, furnitureId, xp, level, itemId, requiredItems);
			this.setAdditionalSkillRequirements(additionalSkillRequirements);
		}
		private Furniture(int hotspotId, int furnitureId, int xp, int level,
				int itemId, int[][] requiredItems, int furnitureRequired) {
			this(hotspotId, furnitureId, xp, level, itemId, requiredItems);
			this.setFurnitureRequired(furnitureRequired);
		}
		public static ArrayList<Furniture> getForHotSpotId(int hotspotId) {
			ArrayList<Furniture> toReturn = new ArrayList<Furniture>();
			for (Furniture f : values()) {
				if (f.hotspotId == hotspotId) {
					toReturn.add(f);
				}
			}
			if (toReturn.isEmpty())
				return null;
			return toReturn;
		}

		public static Furniture forFurnitureId(int furnitureId) {
			for (Furniture f : values()) {
				if (f.getFurnitureId() == furnitureId) {
					return f;
				}
			}
			return null;
		}
		public static Furniture forItemId(int itemId) {
			for (Furniture f : values()) {
				if (f.getItemId() == itemId) {
					return f;
				}
			}
			return null;
		}

		public int getItemId() {
			return itemId;
		}

		public int[][] getRequiredItems() {
			return requiredItems;
		}

		public int getLevel() {
			return level;
		}

		public int getXP() {
			return xp;
		}

		public int getHotSpotId() {
			return hotspotId;
		}

		public int getFurnitureId() {
			return furnitureId;
		}
		public int getFurnitureRequired() {
			return furnitureRequired;
		}
		public void setFurnitureRequired(int furnitureRequired) {
			this.furnitureRequired = furnitureRequired;
		}
		public int[][] getAdditionalSkillRequirements() {
			return additionalSkillRequirements;
		}
		public void setAdditionalSkillRequirements(
				int[][] additionalSkillRequirements) {
			this.additionalSkillRequirements = additionalSkillRequirements;
		}
	}

	public static final int LIT_CANDLE = 34, PLANK = 960, OAK_PLANK = 8778, TEAK_PLANK = 8780,
			MAHOGANY_PLANK = 8782, GOLD_LEAF = 8784, MARBLE_BLOCK = 8786,
			MAGIC_STONE = 8788, CLOTH = 8790, CLOCKWORK = 8792, SAW = 8794,
			NAILS = 1539, MOLTEN_GLASS = 1775, SOFT_CLAY = 1761,
			LIMESTONE_BRICK = 3420, STEEL_BAR = 2353, IRON_BAR = 2351,
			ROPE = 954, AIR_RUNE = 556, WATER_RUNE = 555, EARTH_RUNE = 557,
			FIRE_RUNE = 554,BSF = 8457, WATER_CAN = 5331, BM = 8459, BR = 8461,
			BRM = 8451, BD = 8453, BBB = 8455, BTH = 8437, BNH = 8439, SBH = 8441,
			TH = 8443, FH = 8445, TFH = 8447, TBH = 8449, BONES = 526 ,SKULLS = 964,
			COIN = 995, BOW = 1929, RD = 1763;

	/*
	 * Bagged sunflower = bsf
	 * Bagged marrigolds = BM
	 * Bagged roses = BR
	 * Bagged rosemary = BRM
	 * Bagged daffodiles = BD
	 * Bagged bluebells = BBB
	 * Bagged thorny hedge = BTH
	 * Bagged nice hedge = BNH
	 * Bagged small boxed hedge = SBH
	 * Bagged toparry hedge = TH
	 * Bagged Fancy hedge = FH
	 * Bagged Tall fancy Hedge = TFH
	 * Bagged Tall Box hedge = TBH <- win? xD
	 * Bucket of water = BOW
	 */
	public static int getRotatedLandscapeChunkX(int rotation, int objectSizeY,
			int x, int y, int objectSizeX, int objectRot) {
		rotation &= 3;
		int tmp1 = objectSizeX;
		int tmp2 = objectSizeY;
		if(!(objectRot == 0 || objectRot == 2)) {
			objectSizeX = tmp2;
			objectSizeY = tmp1;
		}
		if (rotation == 0)
			return x;
		if (rotation == 1)
			return y;
		if (rotation == 2) {
			int ret = 7 - x - (objectSizeX - 1);
			if(ret < 0)
				ret = 0;
			return ret;
		} else {
			int ret = 7 - y - (objectSizeY - 1);
			if(ret < 0)
				ret = 0;
			return ret;
		}
	}

	public static int getRotatedLandscapeChunkY(int y, int objectSizeY,
			int rotation, int objectSizeX, int x, int objectRot) {
		rotation &= 3;
		int tmp1 = objectSizeX;
		int tmp2 = objectSizeY;
		if(!(objectRot == 0 || objectRot == 2)) {
			objectSizeX = tmp2;
			objectSizeY = tmp1;
		}
		if (rotation == 0)
			return y;
		if (rotation == 1) {
			int ret = 7 - x - (objectSizeX - 1);
			if(ret < 0)
				ret = 0;
			return ret;
		}
		if (rotation == 2) {
			int ret = 7 - y - (objectSizeY- 1);
			if(ret < 0)
				ret = 0;
			return ret;
		} else
			return x;
	}
	public static int getGuardId(int objectId)
	{
		switch(objectId)
		{
			case 13331:
				return 3580;
			case 13373:
				return 3594;
			
			case 13366:
				return 3581;
			case 13367:
				return 3582;
			case 13368:
				return 3583;
			case 13372:
				return 3588;
			case 13370:
				return 3585;
			case 13369:
				return 3584;
			case 2715:
				return 3586;

			case 39260:
				return 11562;
			case 39261:
				return 11563;
			case 39262:
				return 11564;
			case 39263:
				return 11565;
			case 39264:
				return 11566;
			case 39265:
				return 11567;

			case 13378:
				return 3593;
			case 13374:
				return 3589;
			case 13377:
				return 3592;
			case 13376:
				return 3591;
			case 13375:
				return 3590;
		}
		return -1;
	}
}