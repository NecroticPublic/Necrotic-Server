package com.ruse.world.content.skill.impl.construction;

import java.util.ArrayList;
import java.util.Iterator;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.RegionInstance.RegionInstanceType;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.skill.impl.construction.ConstructionData.Butlers;
import com.ruse.world.content.skill.impl.construction.ConstructionData.Furniture;
import com.ruse.world.content.skill.impl.construction.ConstructionData.HotSpots;
import com.ruse.world.content.skill.impl.construction.ConstructionData.Portals;
import com.ruse.world.content.skill.impl.construction.ConstructionData.RoomData;
import com.ruse.world.content.skill.impl.construction.Palette.PaletteTile;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.npc.NPCMovementCoordinator.Coordinator;
import com.ruse.world.entity.impl.player.Player;

public class Construction {

	public static void buyHouse(Player p) {
		p.getPacketSender().sendInterfaceRemoval();
		if(p.getHouseRooms()[0][0][0] != null) {
			p.getPacketSender().sendMessage("You already own a house. Use the portal to enter it.");
			return;
		}
		boolean usePouch = p.getMoneyInPouchAsInt() >= 5000000;
		if(!usePouch && p.getInventory().getAmount(995) < 5000000) {
			p.getPacketSender().sendMessage("You do not have 5,000,000 coins.");
			return;
		}
		if(usePouch) {
			p.setMoneyInPouch(p.getMoneyInPouch() - 5000000);
			p.getPacketSender().sendString(8135, ""+p.getMoneyInPouch());
		} else {
			p.getInventory().delete(995, 5000000).refreshItems();
		}
		for (int x = 0; x < 13; x++)
			for (int y = 0; y < 13; y++)
				p.getHouseRooms()[0][x][y] = new Room(0, ConstructionData.EMPTY, 0);
		p.getHouseRooms()[0][7][7] = new Room(0, ConstructionData.GARDEN, 0);
		HouseFurniture pf = new HouseFurniture(7, 7, 0, HotSpots.CENTREPIECE.getHotSpotId(), Furniture.EXIT_PORTAL.getFurnitureId(), HotSpots.CENTREPIECE.getXOffset(), HotSpots.CENTREPIECE.getYOffset());
		p.getHouseFurniture().add(pf);
		p.getPacketSender().sendMessage("You've purchased a house. Use the portal to enter it.");
	}
	
	public static void enterHouse(Player p, boolean buildingMode) {
		p.getPacketSender().sendInterfaceRemoval();
		if(p.getHouseRooms()[0][0][0] == null) {
			p.getPacketSender().sendMessage("You don't own a house. Talk to the Estate agent to buy one.");
			return;
		}
		enterHouse(p, p, buildingMode);
	}
	
	public static void enterFriendsHouse(Player p, String p2) {
		Player friend = World.getPlayerByName(p2);
		if(friend == null) {
			p.getPacketSender().sendMessage("That player is currently offline.");
			return;
		}
		if(friend.getHouseRooms()[0][0][0] == null) {
			p.getPacketSender().sendMessage("That player does not own a house.");
			return;
		}
		if(friend.getRegionInstance() == null || !(friend.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE) && !(friend.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_DUNGEON)) {
			p.getPacketSender().sendMessage("That player is not in their house right now.");
			return;
		}
		if(friend.isBuildingMode()) {
			p.getPacketSender().sendMessage("That player is currently in building mode.");
			return;
		}
		enterHouse(p, friend, false);
	}
	
	public static void enterHouse(final Player me, final Player houseOwner, final boolean buildingMode) {
		if(me.getRegionInstance() == null || !(me.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE) && !(me.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_DUNGEON)) {
			createPalette(me);
			return;
		}
		//me.getMovementQueue().reset();
		me.setIsBuildingMode(buildingMode);
		me.setPlayerLocked(true);
		me.getPacketSender().sendInterfaceRemoval();
		me.getPacketSender().sendMapState(2);
		me.moveTo(new Position(ConstructionData.MIDDLE_X, ConstructionData.MIDDLE_Y, 0));
		me.getPacketSender().sendInterface(28640);
		TaskManager.submit(new Task(1, me, true) {
			int ticks = 0, x = -1, y = -1;
			@Override
			public void execute() {
				ticks++;
				switch(ticks) {
				case 1:
					me.getPacketSender().constructMapRegion(((House)houseOwner.getRegionInstance()).getPalette());
					break;
				case 2:
					placeAllFurniture(me, 0);
					placeAllFurniture(me, 1);
					if (me.inConstructionDungeon()) {
						placeAllFurniture(me, 4);
					} else {
						placeAllFurniture(me, 0);
						placeAllFurniture(me, 1);
					}
					if (me.getConstructionCoords() != null) {
						me.moveTo(new Position(me.getConstructionCoords()[0],
								me.getConstructionCoords()[1], 0));
						me.setConstructionCoords(null);
					} else {
						HouseFurniture portal = findNearestPortal(me);
						x = ConstructionData.BASE_X+((portal.getRoomX()+1)*8);
						y = ConstructionData.BASE_Y+((portal.getRoomY()+1)*8);
					}
					break;
				case 5:
					me.getPacketSender().sendInterfaceRemoval();
					me.getPacketSender().sendMapState(0);
					((House)me.getRegionInstance()).greet(me);
					me.setPlayerLocked(false);
					//me.getPacketSender().constructMapRegion(null);
					if(x != -1 && y != -1) {
						me.moveTo(new Position(x + 2, y + 3));
					}
					this.stop();
					break;
				}
			}
		});
	}

	public static HouseFurniture findNearestPortal(Player p)
	{
		Player owner = p.getRegionInstance().getOwner();
		for(HouseFurniture pf : owner.getHouseFurniture())
		{
			if(pf.getFurnitureId() != 13405)
				continue;
			if(pf.getRoomZ() != 0)
				continue;
			return pf;
		}
		return null;
	}

	public static void createPalette(Player p) {
		Palette palette = new Palette();
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					if (p.getHouseRooms()[z][x][y] == null)
						continue;
					if (p.getHouseRooms()[z][x][y].getX() == 0)
						continue;
					PaletteTile tile = new PaletteTile(p.getHouseRooms()[z][x][y].getX(), p.getHouseRooms()[z][x][y].getY(), p.getHouseRooms()[z][x][y].getZ(), p.getHouseRooms()[z][x][y].getRotation());
					palette.setTile(x, y, z, tile);
				}
			}
		}
		House mapInstance = new House(p, true);
		mapInstance.add(p);
		mapInstance.setOwner(p);
		mapInstance.setPalette(palette);
		createDungeonPalette(p);
		enterHouse(p, p, p.isBuildingMode());
		placeNPCs(p);
	}

	public static void createDungeonPalette(Player p) {
		Palette palette = new Palette();
		for (int x = 0; x < 13; x++) {
			for (int y = 0; y < 13; y++) {
				PaletteTile tile = null;
				if (p.getHouseRooms()[4][x][y] == null) {
					tile = new PaletteTile(ConstructionData.RoomData.DUNGEON_EMPTY.getX(), ConstructionData.RoomData.DUNGEON_EMPTY.getY(), 0, 0);
				} else {
					tile = new PaletteTile(p.getHouseRooms()[4][x][y].getX(), p.getHouseRooms()[4][x][y].getY(), p.getHouseRooms()[4][x][y].getZ(), p.getHouseRooms()[4][x][y].getRotation());
				}
				palette.setTile(x, y, 0, tile);
			}
		}
		((House)p.getRegionInstance()).setSecondaryPalette(palette);
	}

	public static void updatePalette(Player p) {
		Palette palette = ((House)p.getRegionInstance()).getPalette();
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					if (p.getHouseRooms()[z][x][y] == null)
						continue;
					if (p.getHouseRooms()[z][x][y].getX() == 0)
						continue;
					PaletteTile tile = new PaletteTile(
							p.getHouseRooms()[z][x][y].getX(),
							p.getHouseRooms()[z][x][y].getY(),
							p.getHouseRooms()[z][x][y].getZ(),
							p.getHouseRooms()[z][x][y].getRotation());
					palette.setTile(x, y, z, tile);
				}
			}
		}
	}

	public static void placeNPCs(Player p)
	{

		if(p.getHouseServant() > 0) {
			HouseFurniture portal = findNearestPortal(p);
			int toX = ConstructionData.BASE_X+((portal.getRoomX()+1)*8);
			int toY = ConstructionData.BASE_Y+((portal.getRoomY()+1)*8);
			Servant npc = (Servant) new NPC(p.getHouseServant(), new Position(toX+3, toY+1, p.getPosition().getZ()));
			((House)p.getRegionInstance()).add(npc);
			World.register(npc);
		}
		if(p.isBuildingMode())
		{
			return;
		}
		for(HouseFurniture pf : p.getHouseFurniture())
		{
			Furniture f = Furniture.forFurnitureId(pf.getFurnitureId());
			int npcId = ConstructionData.getGuardId(f.getFurnitureId());
			if(pf.getRoomZ() != 4)
				continue;
			if(npcId == -1)
				continue;
			Room room = p.getHouseRooms()[pf.getRoomZ()][pf.getRoomX()][pf.getRoomY()];
			HotSpots hs = HotSpots.forHotSpotIdAndCoords(pf.getHotSpotId(),
					pf.getStandardXOff(), pf.getStandardYOff(), room);
			int actualX = ConstructionData.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionData.getXOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			int actualY = ConstructionData.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionData.getYOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			Servant npc = (Servant) new NPC(npcId, new Position(actualX, actualY));
			((House)p.getRegionInstance()).getDungeon().add(npc);
			World.register(npc);
		}
	} 	

	public static void placeAllFurniture(Player p, int x, int y, int z) {
		Player owner = p.getRegionInstance().getOwner();
		for (HouseFurniture pf : owner.getHouseFurniture()) {
			if (pf.getRoomZ() != z)
				continue;
			if(pf.getRoomX() != x || pf.getRoomY() != y)
				continue;
			Room room = owner.getHouseRooms()[pf.getRoomZ()][pf.getRoomX()][pf.getRoomY()];
			HotSpots hs = HotSpots.forHotSpotIdAndCoords(pf.getHotSpotId(),
					pf.getStandardXOff(), pf.getStandardYOff(), room);
			if (hs == null)
				return;
			int actualX = ConstructionData.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionData.getXOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			int actualY = ConstructionData.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionData.getYOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			Furniture f = Furniture.forFurnitureId(pf.getFurnitureId());
			ArrayList<HotSpots> hsses = HotSpots
					.forObjectId_3(f.getHotSpotId());
			doFurniturePlace(hs, f, hsses, getMyChunkFor(actualX, actualY),
					actualX, actualY, room.getRotation(), p, false, z);
		}
	}

	public static void placeAllFurniture(Player p, int heightLevel) {
		Player owner = p.getRegionInstance().getOwner();
		for (HouseFurniture pf : owner.getHouseFurniture()) {
			if (pf.getRoomZ() != heightLevel)
				continue;
			Room room = owner.getHouseRooms()[pf.getRoomZ()][pf.getRoomX()][pf.getRoomY()];
			if (room == null)
				return;
			HotSpots hs = HotSpots.forHotSpotIdAndCoords(pf.getHotSpotId(),
					pf.getStandardXOff(), pf.getStandardYOff(), room);
			if (hs == null)
				return;
			// int rotation = hs.getRotation(room.getRotation());

			int actualX = ConstructionData.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionData.getXOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			int actualY = ConstructionData.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionData.getYOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			Furniture f = Furniture.forFurnitureId(pf.getFurnitureId());
			ArrayList<HotSpots> hsses = HotSpots
					.forObjectId_3(f.getHotSpotId());
			doFurniturePlace(hs, f, hsses, getMyChunkFor(actualX, actualY),
					actualX, actualY, room.getRotation(), p, false, heightLevel);
		}
	}

	public static void doFurniturePlace(HotSpots s, Furniture f,
			ArrayList<HotSpots> hsses, int[] myTiles, int actualX, int actualY,
			int roomRot, Player p, boolean placeBack, int height) {
		int portalId = -1;
		if(s.getHotSpotId() == 72)
		{
			if(s.getXOffset() == 0)
			{
				for(Portal portal : p.getRegionInstance().getOwner().getHousePortals())
				{
					if(portal.getRoomX() == myTiles[0]-1 &&
							portal.getRoomY() == myTiles[1]-1 &&
							portal.getRoomZ() == height && portal.getId() == 0)
					{
						if(Portals.forType(portal.getType()).getObjects() != null)
							portalId = Portals.forType(portal.getType()).getObjects()[f.getFurnitureId()-13636];

					}
				}
			}
			if(s.getXOffset() == 3)
			{
				for(Portal portal : p.getRegionInstance().getOwner().getHousePortals())
				{
					if(portal.getRoomX() == myTiles[0]-1 &&
							portal.getRoomY() == myTiles[1]-1 &&
							portal.getRoomZ() == height && portal.getId() == 1)
					{
						if(Portals.forType(portal.getType()).getObjects() != null)
							portalId = Portals.forType(portal.getType()).getObjects()[f.getFurnitureId()-13636];

					}
				}

			}
			if(s.getXOffset() == 7)
			{
				for(Portal portal : p.getRegionInstance().getOwner().getHousePortals())
				{
					if(portal.getRoomX() == myTiles[0]-1 &&
							portal.getRoomY() == myTiles[1]-1 &&
							portal.getRoomZ() == height && portal.getId() == 2)
					{
						if(Portals.forType(portal.getType()).getObjects() != null)
							portalId = Portals.forType(portal.getType()).getObjects()[f.getFurnitureId()-13636];

					}
				}
			}
		}
		if (height == 4)
			height = 0;

		if (s.getHotSpotId() == 92) {
			int offsetX = ConstructionData.BASE_X + (myTiles[0] * 8);
			int offsetY = ConstructionData.BASE_Y + (myTiles[1] * 8);
			if (s.getObjectId() == 15329 || s.getObjectId() == 15328) {
				p.getPacketSender().sendObject_cons(
						actualX,
						actualY,
						s.getObjectId() == 15328 ? (placeBack ? 15328 : f
								.getFurnitureId()) : (placeBack ? 15329 : f
										.getFurnitureId() + 1), s.getRotation(roomRot),
										0, height);
				offsetX += ConstructionData.getXOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15329 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				offsetY += ConstructionData.getYOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15329 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				p.getPacketSender().sendObject_cons(
						offsetX,
						offsetY,
						s.getObjectId() == 15329 ? (placeBack ? 15328 : f
								.getFurnitureId()) : (placeBack ? 15329 : f
										.getFurnitureId() + 1), s.getRotation(roomRot),
										0, height);

			}
			if (s.getObjectId() == 15326 || s.getObjectId() == 15327) {
				p.getPacketSender().sendObject_cons(
						actualX,
						actualY,
						s.getObjectId() == 15327 ? (placeBack ? 15327 : f
								.getFurnitureId() + 1) : (placeBack ? 15326 : f
										.getFurnitureId()), s.getRotation(roomRot), 0,
										height);
				offsetX += ConstructionData.getXOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15326 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				offsetY += ConstructionData.getYOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15326 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				p.getPacketSender().sendObject_cons(
						offsetX,
						offsetY,
						s.getObjectId() == 15326 ? (placeBack ? 15327 : f
								.getFurnitureId() + 1) : (placeBack ? 15326 : f
										.getFurnitureId()), s.getRotation(roomRot), 0,
										height);

			}
		} else if (s.getHotSpotId() == 85) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8) + 2;
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8) + 2;
			int type = 22, leftObject = 0, rightObject = 0, upperObject = 0, downObject = 0, middleObject = 0, veryMiddleObject = 0, cornerObject = 0;
			if (f.getFurnitureId() == 13331) {
				leftObject = rightObject = upperObject = downObject = 13332;
				middleObject = 13331;
				cornerObject = 13333;
			}
			if (f.getFurnitureId() == 13334) {
				leftObject = rightObject = upperObject = downObject = 13335;
				middleObject = 13334;
				cornerObject = 13336;
			}
			if (f.getFurnitureId() == 13337) {
				leftObject = rightObject = upperObject = downObject = middleObject = cornerObject = 13337;
				type = 10;
			}
			if (f.getFurnitureId() == 13373) {
				veryMiddleObject = 13373;
				leftObject = rightObject = upperObject = downObject = middleObject = 6951;
			}
			if (placeBack || f.getFurnitureId() == 13337) {
				for (int x = 0; x < 4; x++) {
					for (int y = 0; y < 4; y++) {
						p.getPacketSender().sendObject_cons(actualX + x, actualY + y,
								6951, 0, 10, height);
						p.getPacketSender().sendObject_cons(actualX + x, actualY + y,
								6951, 0, 22, height);
					}
				}

			}
			p.getPacketSender().sendObject_cons(actualX, actualY,
					placeBack ? 15348 : cornerObject, 1, type, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15348 : leftObject, 1, type, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 2,
					placeBack ? 15348 : leftObject, 1, type, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 3,
					placeBack ? 15348 : cornerObject, 2, type, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 3,
					placeBack ? 15348 : upperObject, 2, type, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 3,
					placeBack ? 15348 : upperObject, 2, type, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 3,
					placeBack ? 15348 : cornerObject, 3, type, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 2,
					placeBack ? 15348 : rightObject, 3, type, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 1,
					placeBack ? 15348 : rightObject, 3, type, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY,
					placeBack ? 15348 : cornerObject, 0, type, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY,
					placeBack ? 15348 : downObject, 0, type, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY,
					placeBack ? 15348 : downObject, 0, type, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 1,
					placeBack ? 15348 : middleObject, 0, type, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 1,
					placeBack ? 15348 : middleObject, 0, type, height);
			if (veryMiddleObject != 0)
				p.getPacketSender().sendObject_cons(actualX + 1, actualY + 2,
						veryMiddleObject, 0, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 2,
					placeBack ? 15348 : middleObject, 0, type, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 2,
					placeBack ? 15348 : middleObject, 0, type, height);

		} else if (s.getHotSpotId() == 86) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8) + 2;
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8) + 2;

			p.getPacketSender().sendObject_cons(actualX + 1, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 2, 2, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 1,
					placeBack ? 15352 : f.getFurnitureId(), 2, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 2,
					placeBack ? 15352 : f.getFurnitureId() + 1, 2, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 1, 2, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 1, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 1, 0, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 0, 2, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 2,
					placeBack ? 15352 : f.getFurnitureId(), 0, 0, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15352 : f.getFurnitureId(), 0, 0, height);
			p.getPacketSender().sendObject_cons(actualX, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 3, 2, height);

		} else if (s.getHotSpotId() == 78) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
			// south walls
			p.getPacketSender().sendObject_cons(actualX, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 2, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 6, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 2, 2, height);
			// north walls
			p.getPacketSender().sendObject_cons(actualX, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 0, 2, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 6, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 2, height);
			// left walls
			p.getPacketSender().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 2,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 5,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 6,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			// right walls
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 1,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 2,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 5,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 6,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
		} else if (s.getHotSpotId() == 77) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
			// left down corner
			p.getPacketSender().sendObject_cons(actualX, actualY,
					placeBack ? 15372 : f.getFurnitureId() + 1, 3, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY,
					placeBack ? 15370 : f.getFurnitureId(), 0, 10, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15371 : f.getFurnitureId() + 2, 1, 10, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 2,
					placeBack ? 15370 : f.getFurnitureId(), 3, 10, height);
			// right down corner
			p.getPacketSender().sendObject_cons(actualX + 7, actualY,
					placeBack ? 15372 : f.getFurnitureId() + 1, 2, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 6, actualY,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY,
					placeBack ? 15370 : f.getFurnitureId(), 2, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 1,
					placeBack ? 15371 : f.getFurnitureId() + 2, 3, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 2,
					placeBack ? 15370 : f.getFurnitureId(), 3, 10, height);
			// upper left corner
			p.getPacketSender().sendObject_cons(actualX, actualY + 7,
					placeBack ? 15372 : f.getFurnitureId() + 1, 0, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 7,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 7,
					placeBack ? 15370 : f.getFurnitureId(), 0, 10, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 6,
					placeBack ? 15371 : f.getFurnitureId() + 2, 1, 10, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 5,
					placeBack ? 15370 : f.getFurnitureId(), 1, 10, height);
			// upper right corner
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 7,
					placeBack ? 15372 : f.getFurnitureId() + 1, 1, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 6, actualY + 7,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 7,
					placeBack ? 15370 : f.getFurnitureId(), 2, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 6,
					placeBack ? 15371 : f.getFurnitureId() + 2, 3, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 5,
					placeBack ? 15370 : f.getFurnitureId(), 1, 10, height);
		} else if (s.getHotSpotId() == 44) {
			int combatringStrings = 6951;
			int combatringFloorsCorner = 6951;
			int combatringFloorsOuter = 6951;
			int combatringFloorsInner = 6951;
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8) + 1;
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8) + 1;
			if (!placeBack) {
				if (f.getFurnitureId() == 13126) {
					combatringStrings = 13132;
					combatringFloorsCorner = 13126;
					combatringFloorsOuter = 13128;
					combatringFloorsInner = 13127;
				}
				if (f.getFurnitureId() == 13133) {
					combatringStrings = 13133;
					combatringFloorsCorner = 13135;
					combatringFloorsOuter = 13134;
					combatringFloorsInner = 13136;
				}
				if (f.getFurnitureId() == 13137) {
					combatringStrings = 13137;
					combatringFloorsCorner = 13138;
					combatringFloorsOuter = 13139;
					combatringFloorsInner = 13140;
				}
			}

			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 3,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 3,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 2,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 2,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 1,
					placeBack ? 15291 : combatringFloorsOuter, 3, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 1,
					placeBack ? 15291 : combatringFloorsOuter, 3, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 4,
					placeBack ? 15291 : combatringFloorsOuter, 1, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 4,
					placeBack ? 15291 : combatringFloorsOuter, 1, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY + 3,
					placeBack ? 15291 : combatringFloorsOuter, 2, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY + 2,
					placeBack ? 15291 : combatringFloorsOuter, 2, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 3,
					placeBack ? 15291 : combatringFloorsOuter, 0, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 2,
					placeBack ? 15291 : combatringFloorsOuter, 0, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY + 1,
					placeBack ? 15289 : combatringFloorsCorner, 3, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY + 4,
					placeBack ? 15289 : combatringFloorsCorner, 2, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 4,
					placeBack ? 15289 : combatringFloorsCorner, 1, 22, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 1,
					placeBack ? 15289 : combatringFloorsCorner, 0, 22, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 4,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 4,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 1,
					placeBack ? 15277 : combatringStrings, 0, 3, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY,
					placeBack ? 15277 : combatringStrings, 0, 3, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 3, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 5,
					placeBack ? 15277 : combatringStrings, 2, 3, height);
			p.getPacketSender().sendObject_cons(actualX, actualY,
					placeBack ? 15277 : combatringStrings, 1, 3, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 4,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 3,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 2,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 4,
					placeBack ? 15277 : combatringStrings, 0, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 3,
					placeBack ? 15277 : combatringStrings, 0, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 2,
					placeBack ? 15277 : combatringStrings, 0, 0, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 1,
					placeBack ? 15277 : combatringStrings, 0, 0, height);

			if (f.getFurnitureId() == 13145) {
				p.getPacketSender().sendObject_cons(actualX + 1, actualY + 1,
						placeBack ? 6951 : 13145, 0, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 2, actualY + 1,
						placeBack ? 6951 : 13145, 0, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 1, actualY,
						placeBack ? 6951 : 13145, 1, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 1, actualY + 2,
						placeBack ? 6951 : 13145, 3, 0, height);
				if (!placeBack)
					p.getPacketSender().sendObject_cons(actualX + 1, actualY + 1, 13147,
							0, 22, height);

				p.getPacketSender().sendObject_cons(actualX + 3, actualY + 3,
						placeBack ? 6951 : 13145, 0, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 4, actualY + 3,
						placeBack ? 6951 : 13145, 0, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 3, actualY + 2,
						placeBack ? 6951 : 13145, 1, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 3, actualY + 4,
						placeBack ? 6951 : 13145, 3, 0, height);
				if (!placeBack)
					p.getPacketSender().sendObject_cons(actualX + 3, actualY + 3, 13147,
							0, 22, height);
			}
			if (f.getFurnitureId() == 13142 && !placeBack) {
				p.getPacketSender().sendObject_cons(actualX + 2, actualY + 2, 13142, 0,
						22, height);
				p.getPacketSender().sendObject_cons(actualX + 2, actualY + 1, 13143, 0,
						22, height);
				p.getPacketSender().sendObject_cons(actualX + 2, actualY + 3, 13144, 1,
						22, height);

			}
		} else if (s.getCarpetDim() != null) {
			for (int x = 0; x < s.getCarpetDim().getWidth() + 1; x++) {
				for (int y = 0; y < s.getCarpetDim().getHeight() + 1; y++) {
					boolean isEdge = (x == 0 && y == 0 || x == 0
							&& y == s.getCarpetDim().getHeight() || y == 0
							&& x == s.getCarpetDim().getWidth() || x == s
							.getCarpetDim().getWidth()
							&& y == s.getCarpetDim().getHeight());
					boolean isWall = ((x == 0 || x == s.getCarpetDim()
							.getWidth())
							&& (y != 0 && y != s.getCarpetDim().getHeight()) || (y == 0 || y == s
							.getCarpetDim().getHeight())
							&& (x != 0 && x != s.getCarpetDim().getWidth()));
					int rot = 0;
					if (x == 0 && y == s.getCarpetDim().getHeight() && isEdge)
						rot = 0;
					if (x == s.getCarpetDim().getWidth()
							&& y == s.getCarpetDim().getHeight() && isEdge)
						rot = 1;
					if (x == s.getCarpetDim().getWidth() && y == 0 && isEdge)
						rot = 2;
					if (x == 0 && y == 0 && isEdge)
						rot = 3;
					if (y == 0 && isWall)
						rot = 2;
					if (y == s.getCarpetDim().getHeight() && isWall)
						rot = 0;
					if (x == 0 && isWall)
						rot = 3;
					if (x == s.getCarpetDim().getWidth() && isWall)
						rot = 1;
					int offsetX = ConstructionData.BASE_X + (myTiles[0] * 8);
					int offsetY = ConstructionData.BASE_Y + (myTiles[1] * 8);
					offsetX += ConstructionData.getXOffsetForObjectId(
							f.getFurnitureId(), s.getXOffset() + x - 1,
							s.getYOffset() + y - 1, roomRot,
							s.getRotation(roomRot));
					offsetY += ConstructionData.getYOffsetForObjectId(
							f.getFurnitureId(), s.getXOffset() + x - 1,
							s.getYOffset() + y - 1, roomRot,
							s.getRotation(roomRot));
					if (isEdge)
						p.getPacketSender().sendObject_cons(
								offsetX,
								offsetY,
								placeBack ? s.getObjectId() + 2 : f
										.getFurnitureId(),
										HotSpots.getRotation_2(rot, roomRot), 22,
										height);
					else if (isWall)
						p.getPacketSender().sendObject_cons(
								offsetX,
								offsetY,
								placeBack ? s.getObjectId() + 1 : f
										.getFurnitureId() + 1,
										HotSpots.getRotation_2(rot, roomRot),
										s.getObjectType(), height);
					else
						p.getPacketSender().sendObject_cons(
								offsetX,
								offsetY,
								placeBack ? s.getObjectId() : f
										.getFurnitureId() + 2,
										HotSpots.getRotation_2(rot, roomRot),
										s.getObjectType(), height);
				}
			}
		} else if (s.isMutiple()) {

			Room room = p.getRegionInstance().getOwner().getHouseRooms()[p.inConstructionDungeon() ? 4 : p.getPosition().getZ()][myTiles[0] - 1][myTiles[1] - 1];
			for (HotSpots find : hsses) {
				if (find.getObjectId() != s.getObjectId())
					continue;
				if (room != null)
					if (room.getType() != find.getRoomType())
						continue;
				int actualX1 = ConstructionData.BASE_X + (myTiles[0] * 8);
				actualX1 += ConstructionData.getXOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				int actualY1 = ConstructionData.BASE_Y + (myTiles[1] * 8);
				actualY1 += ConstructionData.getYOffsetForObjectId(
						find.getObjectId(), find, roomRot);

				p.getPacketSender()
				.sendObject_cons(
						actualX1,
						actualY1,
						placeBack ? s.getObjectId() : f
								.getFurnitureId(),
								find.getRotation(roomRot),
								find.getObjectType(), height);
			}
		} else {
			p.getPacketSender().sendObject_cons(actualX, actualY,
					(portalId != -1 ? portalId : placeBack ? s.getObjectId() : f.getFurnitureId()),
					s.getRotation(roomRot), s.getObjectType(), height);
		}
	}

	public static void handleFifthObjectClick(int obX, int obY, int objectId,
			Player p) {
		switch (objectId) {
		case 13497:
		case 13499:
		case 13501:
		case 13503:
		case 13505:
			int myTiles[] = getMyChunk(p);
			Room room = p.getRegionInstance().getOwner().getHouseRooms()[1][myTiles[0] - 1][myTiles[1] - 1];
			if (room == null) {
				p.getPacketSender().sendMessage("These stairs lead nowhere.");
			} else {
				p.getRegionInstance().getOwner().getHouseRooms()[1][myTiles[0] - 1][myTiles[1] - 1] = null;
				updatePalette(p);
				p.getPacketSender().constructMapRegion(((House)p.getRegionInstance()).getPalette());
			}
			break;
		default:
			handleFourthObjectClick(obX, obY, objectId, p);
			break;
		}
	}

	public static void handleFourthObjectClick(int obX, int obY, int objectId,
			Player p) {

		if(p.getRegionInstance() == null)
			return;
		if (p.getRegionInstance().getOwner() != p)
			return;
		if (!p.isBuildingMode())
			return;
		if (handleSpaceClick(obX, obY, objectId, p))
			return;
		if (handleRemoveClick(obX, obY, objectId, p))
			return;
		for (int i : ConstructionData.DOORSPACEIDS) {
			if (objectId == i) {
				if (!roomExists(p)) {
					p.getPacketSender().sendInterface(28643);
					return;
				} else {
					DialogueManager.start(p, ConstructionDialogues.rotateObjectDialogue(p));
				}
			}
		}
	}

	public static String hasReqs(Player p, Furniture f, HotSpots hs)
	{

		if(p.getRights() == PlayerRights.DEVELOPER)
			return null;
		if(p.getSkillManager().getCurrentLevel(Skill.CONSTRUCTION) < f.getLevel())
		{
			return "You need a Construction level of at least "+f.getLevel()+" to build this.";
		}
		for (int i1 = 0; i1 < f.getRequiredItems().length; i1++) {
			if (p.getInventory().getAmount(f.getRequiredItems()[i1][0]) < f.getRequiredItems()[i1][1]) {
				String s = ItemDefinition.forId(f.getRequiredItems()[i1][0]).getName();
				if(!s.endsWith("s") && f.getRequiredItems()[i1][1] > 1)
					s = s + "s";
				return "You need "+f.getRequiredItems()[i1][1]+"x "+s+" to build this.";
			}
		}
		if (f.getAdditionalSkillRequirements() != null) {
			for (int ii = 0; ii < f.getAdditionalSkillRequirements().length; ii++) {
				if (p.getSkillManager().getCurrentLevel(Skill.forId(f.getAdditionalSkillRequirements()[ii][0])) < f
						.getAdditionalSkillRequirements()[ii][1]) {
					return "You need a "+Skill.forId(f.getAdditionalSkillRequirements()[ii][0]).getFormatName()+" of at least "+f.getAdditionalSkillRequirements()[ii][1]+""
							+ " to build this.";
				}
			}
		}
		if (f.getFurnitureRequired() != -1) {
			Furniture fur = Furniture.forFurnitureId(f.getFurnitureRequired());
			int[] myTiles = getMyChunk(p);
			for (HouseFurniture pf : p.getHouseFurniture()) {
				if (pf.getRoomX() == myTiles[0] - 1 && pf.getRoomY() == myTiles[1] - 1) {
					if (pf.getHotSpot(p.getHouseRooms()[p.inConstructionDungeon() ? 4 : p.getPosition().getZ()][myTiles[0] - 1][myTiles[1] - 1]
							.getRotation()) == hs) {
						if (pf.getFurnitureId() != fur.getFurnitureId()) {
							return "This is an upgradeable piece of furniture. (build the furniture before this first)";
						}
					}
				}
			}
		}

		return null;
	}

	public static boolean buildActions(Player p, Furniture f, HotSpots hs)
	{
		String s = hasReqs(p, f, hs);
		if(s != null) {
			p.getPacketSender().sendMessage(s);
			return false;
		}
		for (int i = 0; i < f.getRequiredItems().length; i++) {
			ItemDefinition item = ItemDefinition.forId(f.getRequiredItems()[i][0]);
			if(item.isStackable())
				p.getInventory().delete(f.getRequiredItems()[i][0], f.getRequiredItems()[i][1]);
			else {
				for(int a = 0; a < f.getRequiredItems()[i][1]; a++)
				{
					p.getInventory().delete(f.getRequiredItems()[i][0], 1);
				}
			}
		}
		p.getSkillManager().addExperience(Skill.CONSTRUCTION, f.getXP());
		return true;
	}

	public static boolean handleButtonClick(int buttonId, Player p) {
		switch (buttonId) {
		case -27213:
		case 28645:
			p.getPacketSender().sendInterfaceRemoval();
			return true;
		case 2471:
			if (p.getDialogueActionId() == 423) {
				p.getInventory().add(7688, 1);
			}
			if (p.getDialogueActionId() == 428) {
				p.getInventory().add(1923, 1);
				return true;
			}
			if(p.getDialogueActionId() == 442)
			{

				p.getPacketSender().sendInterfaceRemoval();
				if(p.getHouseRooms()[0][0][0] == null)
				{
					p.getPacketSender().sendMessage("You don't own a house. Talk to the House Agent to buy one.");
					return true;
				}
				p.setIsBuildingMode(false);
				Construction.createPalette(p);
				return true;
			}
			if(p.getDialogueActionId() == 642)
			{
				/**
				 * Counter room clockwise
				 */
				rotateRoom(0, p);
				return true;
			}
			if(p.getDialogueActionId() == 645)
			{
				p.setPortalSelected(0);
				////DialogueManager.sendDialogues(p, 646, 0);
				return true;
			}
			break;
		case 2472:
			if (p.getDialogueActionId() == 423) {
				p.getInventory().add(7702, 1);
				return true;
			}
			if (p.getDialogueActionId() == 428) {
				p.getInventory().add(1887, 1);
				return true;
			}
			if(p.getDialogueActionId() == 442)
			{
				p.getPacketSender().sendInterfaceRemoval();
				if(p.getHouseRooms()[0][0][0] == null)
				{
					p.getPacketSender().sendMessage("You don't own a house. Talk to the House Agent to buy one.");
					return true;
				}
				p.setIsBuildingMode(true);
				Construction.createPalette(p);
				return true;
			}
			if(p.getDialogueActionId() == 642)
			{
				/**
				 * Counter room clockwise
				 */
				rotateRoom(1, p);
				return true;
			}
			if(p.getDialogueActionId() == 645)
			{
				p.setPortalSelected(1);
				////DialogueManager.sendDialogues(p, 646, 0);
				return true;
			}
			break;
		case 2473:
			if (p.getDialogueActionId() == 423) {
				p.getInventory().add(7728, 1);
				return true;
			}
			if (p.getDialogueActionId() == 428) {
				DialogueManager.start(p, ConstructionDialogues.withdrawSuppliesDialogue(p, 427));
				return true;
			}
			if(p.getDialogueActionId() == 442)
			{
				p.getPacketSender().commandFrame(2);
				return true;
			}
			if(p.getDialogueActionId() == 642)
			{
				/**
				 * Remove room
				 */
				if (p.getPosition().getZ() == 0 && !p.inConstructionDungeon())
					deleteRoom(p, 0);
				if (p.inConstructionDungeon())
					deleteRoom(p, 4);
				if (p.getPosition().getZ() == 1) {
					deleteRoom(p, 1);
				}
				return true;
			}
			if(p.getDialogueActionId() == 645)
			{
				p.setPortalSelected(2);
				//DialogueManager.sendDialogues(p, 646, 0);
				return true;
			}
			break;
		case 2482:
			if (p.getDialogueActionId() == 424) {
				p.getInventory().add(7688, 1);
				return true;
			}
			if (p.getDialogueActionId() == 430) {
				p.getInventory().add(1923, 1);
				return true;
			}
			if (p.getDialogueActionId() == 434) {
				p.getInventory().add(7738, 1);
				return true;
			}
			if(p.getDialogueActionId() == 440)
			{
				p.getInventory().add(7671, 1);
				return true;
			}
			if(p.getDialogueActionId() == 644)
			{
				p.getRegionInstance().remove(p);
				p.moveTo(new Position(ConstructionConstants.EDGEVILLE_X, ConstructionConstants.EDGEVILLE_Y, 0));
				return true;
			}
			break;
		case 2483:
			if (p.getDialogueActionId() == 424) {
				p.getInventory().add(7702, 1);
				return true;
			}
			if (p.getDialogueActionId() == 430) {
				p.getInventory().add(2313, 1);
				return true;
			}
			if (p.getDialogueActionId() == 434) {
				p.getInventory().add(1927, 1);
				return true;
			}
			if(p.getDialogueActionId() == 440)
			{
				p.getInventory().add(7673, 1);
				return true;
			}
			if(p.getDialogueActionId() == 644)
			{
				p.getRegionInstance().remove(p);
				p.moveTo(new Position(ConstructionConstants.KARAMJA_X, ConstructionConstants.KARAMJA_Y, 0));
				return true;
			}
			break;
		case 2484:
			if (p.getDialogueActionId() == 424) {
				p.getInventory().add(7728, 1);
				return true;
			}
			if (p.getDialogueActionId() == 430) {
				p.getInventory().add(1931, 1);
				return true;
			}
			if (p.getDialogueActionId() == 434) {
				p.getInventory().add(1944, 1);
				return true;
			}
			if(p.getDialogueActionId() == 440)
			{
				p.getInventory().add(7675, 1);
				return true;
			}
			if(p.getDialogueActionId() == 644)
			{
				p.getRegionInstance().remove(p);
				p.moveTo(new Position(ConstructionConstants.DRAYNOR.getX(), ConstructionConstants.DRAYNOR.getY(), 0));
				return true;
			}
			break;
		case 2485:
			if (p.getDialogueActionId() == 424) {
				p.getInventory().add(1919, 1);
				return true;
			}
			if (p.getDialogueActionId() == 430) {
				DialogueManager.start(p, ConstructionDialogues.withdrawSuppliesDialogue(p, 429));
				return true;
			}
			if (p.getDialogueActionId() == 434) {
				p.getInventory().add(1933, 1);
				return true;
			}
			if(p.getDialogueActionId() == 440)
			{
				p.getInventory().add(7676, 1);
				return true;
			}
			if(p.getDialogueActionId() == 644)
			{
				p.getRegionInstance().remove(p);
				p.moveTo(new Position(ConstructionConstants.AL_KHARID_X, ConstructionConstants.AL_KHARID_Y, 0));
				return true;
			}
			break;
		case 2494:
			if(p.getDialogueActionId() == 646)
			{
				String s = Portals.VARROCK.canBuild(p);
				if(s != null)
				{
					p.getPacketSender().sendInterfaceRemoval();
					DialogueManager.start(p, ConstructionDialogues.sendConstructionStatement(s));
				}
				return true;
			}
			if(p.getDialogueActionId() == 647)
			{
				String s = Portals.ARDOUGNE.canBuild(p);
				if(s != null)
				{
					p.getPacketSender().sendInterfaceRemoval();
					DialogueManager.start(p, ConstructionDialogues.sendConstructionStatement(s));
				}
				return true;
			}
			if(p.getDialogueActionId() == 453)
			{
				p.setServantItemFetch(ConstructionData.PLANK);
				p.setConstructionInterface(28643);
				//p.getOutStream().createFrame(27);
				return true;
			}
			if(p.getDialogueActionId() == 454)
			{
				p.setServantItemFetch(ConstructionData.SOFT_CLAY);
				p.setConstructionInterface(28643);
				//p.getOutStream().createFrame(27);
				return true;
			}
			if(p.getDialogueActionId() == 455)
			{
				p.setServantItemFetch(ConstructionData.CLOTH);
				p.setConstructionInterface(28643);
				//p.getOutStream().createFrame(27);
				return true;
			}
			if(p.getDialogueActionId() == 452)
			{
				p.getPacketSender().sendInterfaceRemoval();
				return true;
			}
			if(p.getDialogueActionId() == 441)
			{
				p.getInventory().add(7671, 1);
				return true;
			}
			if (p.getDialogueActionId() == 425 || p.getDialogueActionId() == 426
					|| p.getDialogueActionId() == 427 || p.getDialogueActionId() == 429
					|| p.getDialogueActionId() == 431) {
				p.getInventory().add(7688, 1);
				return true;
			}
			if (p.getDialogueActionId() == 432) {
				p.getInventory().add(1923, 1);
				return true;
			}
			if (p.getDialogueActionId() == 435) {
				p.getInventory().add(7738, 1);
				return true;
			}
			if (p.getDialogueActionId() == 436) {
				p.getInventory().add(1942, 1);
				return true;
			}
			break;
		case 2495:

			if(p.getDialogueActionId() == 646)
			{
				String s = Portals.LUMBRIDGE.canBuild(p);
				if(s != null)
				{
					p.getPacketSender().sendInterfaceRemoval();
					DialogueManager.start(p, ConstructionDialogues.sendConstructionStatement(s));
				}
				return true;
			}
			if(p.getDialogueActionId() == 647)
			{
				String s = Portals.YANILLE.canBuild(p);
				if(s != null)
				{
					p.getPacketSender().sendInterfaceRemoval();
					DialogueManager.start(p, ConstructionDialogues.sendConstructionStatement(s));
				}
				return true;
			}
			if(p.getDialogueActionId() == 453)
			{
				p.setServantItemFetch(ConstructionData.OAK_PLANK);
				p.setConstructionInterface(28643);
				//p.getOutStream().createFrame(27);
				return true;
			}
			if(p.getDialogueActionId() == 454)
			{
				p.setServantItemFetch(ConstructionData.LIMESTONE_BRICK);
				p.setConstructionInterface(28643);
				//p.getOutStream().createFrame(27);
				return true;
			}
			if(p.getDialogueActionId() == 455)
			{
				p.setServantItemFetch(ConstructionData.GOLD_LEAF);
				p.setConstructionInterface(28643);
				//p.getOutStream().createFrame(27);
				return true;
			}
			if(p.getDialogueActionId() == 452)
			{
				if(p.getRegionInstance().getOwner() == p)
				{
					DialogueManager.start(p, ConstructionDialogues.servantOptions(p, 453));
				} else
					DialogueManager.start(p, ConstructionDialogues.notPlayersButler(((NPC)p.getInteractingEntity()).getId()));
				return true;
			}
			if(p.getDialogueActionId() == 441)
			{
				p.getInventory().add(7673, 1);
				return true;
			}
			if (p.getDialogueActionId() == 425) {
				p.getInventory().add(7702, 1);
				return true;
			}
			if (p.getDialogueActionId() == 426 || p.getDialogueActionId() == 427) {
				p.getInventory().add(7714, 1);
				return true;
			}
			if (p.getDialogueActionId() == 429 || p.getDialogueActionId() == 431
					|| p.getDialogueActionId() == 429) {
				p.getInventory().add(7726, 1);
				return true;
			}
			if (p.getDialogueActionId() == 432) {
				p.getInventory().add(2313, 1);
				return true;
			}
			if (p.getDialogueActionId() == 435) {
				p.getInventory().add(1927, 1);
				return true;
			}
			if (p.getDialogueActionId() == 436) {
				p.getInventory().add(1550, 1);
				return true;
			}
			break;
		case 2496:
			if(p.getDialogueActionId() == 646)
			{
				String s = Portals.FALADOR.canBuild(p);
				if(s != null)
				{
					p.getPacketSender().sendInterfaceRemoval();
					DialogueManager.start(p, ConstructionDialogues.sendConstructionStatement(s));
				}
				return true;
			}
			if(p.getDialogueActionId() == 647)
			{
				String s = Portals.KHARYLL.canBuild(p);
				if(s != null)
				{
					p.getPacketSender().sendInterfaceRemoval();
					DialogueManager.start(p, ConstructionDialogues.sendConstructionStatement(s));
				}
				return true;
			}
			if(p.getDialogueActionId() == 453)
			{
				p.setServantItemFetch(ConstructionData.TEAK_PLANK);
				p.setConstructionInterface(28643);
				//p.getOutStream().createFrame(27);
				return true;
			}
			if(p.getDialogueActionId() == 454)
			{
				p.setServantItemFetch(ConstructionData.STEEL_BAR);
				p.setConstructionInterface(28643);
				//p.getOutStream().createFrame(27);
				return true;
			}
			if(p.getDialogueActionId() == 455)
			{
				p.setServantItemFetch(ConstructionData.MARBLE_BLOCK);
				p.setConstructionInterface(28643);
				//p.getOutStream().createFrame(27);
				return true;
			}
			if(p.getDialogueActionId() == 452)
			{
				if(p.getRegionInstance().getOwner() == p) {
					House house = p.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE ? (House) p.getRegionInstance() : ((HouseDungeon)p.getRegionInstance()).getHouse();
					Servant butler = house.getButler();
					butler.getMovementCoordinator().setCoordinator(new Coordinator(true, 5));
					butler.setSpawnedFor(null);
					butler.setGreetVisitors(true);
					p.getPacketSender().sendInterfaceRemoval();
				} else {
					DialogueManager.start(p, ConstructionDialogues.notPlayersButler(((NPC)p.getInteractingEntity()).getId()));
				}
				return true;

			}
			if(p.getDialogueActionId() == 441)
			{
				p.getInventory().add(7675, 1);
				return true;
			}
			if (p.getDialogueActionId() == 425 || p.getDialogueActionId() == 426
					|| p.getDialogueActionId() == 427 || p.getDialogueActionId() == 429
					|| p.getDialogueActionId() == 431) {
				p.getInventory().add(7732, 1);
				return true;
			}
			if (p.getDialogueActionId() == 432) {
				p.getInventory().add(1931, 1);
				return true;
			}
			if (p.getDialogueActionId() == 435) {
				p.getInventory().add(1944, 1);
				return true;
			}
			if (p.getDialogueActionId() == 436) {
				p.getInventory().add(1957, 1);
				return true;
			}
			break;
		case 2497:
			if(p.getDialogueActionId() == 646)
			{
				String s = Portals.CAMELOT.canBuild(p);
				if(s != null)
				{
					p.getPacketSender().sendInterfaceRemoval();
					DialogueManager.start(p, ConstructionDialogues.sendConstructionStatement(s));
				}
				return true;
			}
			if(p.getDialogueActionId() == 647)
			{
				String s = Portals.EMPTY.canBuild(p);
				if(s != null)
				{
					p.getPacketSender().sendInterfaceRemoval();
					DialogueManager.start(p, ConstructionDialogues.sendConstructionStatement(s));
				}
				return true;
			}
			if(p.getDialogueActionId() == 453)
			{
				p.setServantItemFetch(ConstructionData.MAHOGANY_PLANK);
				p.setConstructionInterface(28643);
				//p.getOutStream().createFrame(27);
				return true;
			}
			if(p.getDialogueActionId() == 454)
			{
				DialogueManager.start(p, ConstructionDialogues.servantOptions(p, 453));
				return true;
			}
			if(p.getDialogueActionId() == 455)
			{
				p.setServantItemFetch(ConstructionData.MAGIC_STONE);
				p.setConstructionInterface(28643);
				//p.getOutStream().createFrame(27);
				return true;
			}
			if(p.getDialogueActionId() == 452)
			{
				House house = p.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE ? (House) p.getRegionInstance() : ((HouseDungeon)p.getRegionInstance()).getHouse();
				NPC butler = house.getButler();
				if(butler.getSpawnedFor() != p)
					butler.setSpawnedFor(p);
				else
					butler.setSpawnedFor(null);
				p.getPacketSender().sendInterfaceRemoval();
				return true;
			}
			if(p.getDialogueActionId() == 441)
			{
				p.getInventory().add(7676, 1);
				return true;
			}
			if (p.getDialogueActionId() == 425 || p.getDialogueActionId() == 426
					|| p.getDialogueActionId() == 427 || p.getDialogueActionId() == 429
					|| p.getDialogueActionId() == 431) {
				p.getInventory().add(1919, 1);
				return true;
			}
			if (p.getDialogueActionId() == 432) {
				p.getInventory().add(1949, 1);
				return true;
			}
			if (p.getDialogueActionId() == 435) {
				p.getInventory().add(1933, 1);
				return true;
			}
			if (p.getDialogueActionId() == 436) {
				p.getInventory().add(1985, 1);
				return true;
			}
			break;
		case 2498:
			if(p.getDialogueActionId() == 453)
			{
				DialogueManager.start(p, ConstructionDialogues.servantOptions(p, 454));
				return true;
			} else
				if(p.getDialogueActionId() == 454)
				{
					DialogueManager.start(p, ConstructionDialogues.servantOptions(p, 455));
					return true;
				} else
					if(p.getDialogueActionId() == 455)
					{
						DialogueManager.start(p, ConstructionDialogues.servantOptions(p, 454));
						return true;
					}
			if(p.getDialogueActionId() == 452)
			{
				House house = p.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE ? (House) p.getRegionInstance() : ((HouseDungeon)p.getRegionInstance()).getHouse();
				if(house.getOwner() == p)
				{	
					NPC butler = house.getButler();
					World.deregister(butler);
					p.setHouseServant(0);
					p.getPacketSender().sendInterfaceRemoval();
				} else {
					p.getPacketSender().sendMessage("You can't fire someone else's servant.");
					p.getPacketSender().sendInterfaceRemoval();
				}
				return true;

			}
			if(p.getDialogueActionId() == 441)
			{
				p.getInventory().add(7679, 1);
				return true;
			}
			if (p.getDialogueActionId() == 425) {
				p.getInventory().add(1887, 1);
				return true;
			}
			if (p.getDialogueActionId() == 426) {
				p.getInventory().add(1923, 1);
				return true;
			}
			if (p.getDialogueActionId() == 427) {
				DialogueManager.start(p, ConstructionDialogues.withdrawSuppliesDialogue(p, 428));
				return true;
			}
			if (p.getDialogueActionId() == 429) {
				DialogueManager.start(p, ConstructionDialogues.withdrawSuppliesDialogue(p, 430));
				return true;
			}
			if (p.getDialogueActionId() == 431) {
				DialogueManager.start(p, ConstructionDialogues.withdrawSuppliesDialogue(p, 432));
				return true;
			} else if (p.getDialogueActionId() == 432) {
				DialogueManager.start(p, ConstructionDialogues.withdrawSuppliesDialogue(p, 431));
				return true;
			}
			if (p.getDialogueActionId() == 435) {
				DialogueManager.start(p, ConstructionDialogues.withdrawSuppliesDialogue(p, 436));
				return true;
			} else if (p.getDialogueActionId() == 436) {
				DialogueManager.start(p, ConstructionDialogues.withdrawSuppliesDialogue(p, 435));
				return true;
			}
			break;
		case 2461:
			if(p.getDialogueActionId() == 654)
			{
				if(p.getHouseRooms()[0][0][0] != null)
					return true;
				for (int x = 0; x < 13; x++)
					for (int y = 0; y < 13; y++)
						p.getHouseRooms()[0][x][y] = new Room(0,
								ConstructionData.EMPTY, 0);
				p.getHouseRooms()[0][7][7] = new Room(0, ConstructionData.GARDEN, 0);
				HouseFurniture pf = new HouseFurniture(7, 7, 0, HotSpots.CENTREPIECE.getHotSpotId(), 
						Furniture.EXIT_PORTAL.getFurnitureId(), HotSpots.CENTREPIECE.getXOffset(), 
						HotSpots.CENTREPIECE.getYOffset());
				p.getHouseFurniture().add(pf);
				DialogueManager.start(p, 354);
				return true;
			}
			if(p.getDialogueActionId() == 457)
			{
				Butlers b = Butlers.forId(p.getHouseServant());
				if(p.getInventory().getAmount(995) >= b.getLoanCost()) {
					p.setHouseServantCharges(8);
					p.getInventory().delete(995, b.getLoanCost());
				} else {
					p.getPacketSender().sendMessage("You need "+b.getLoanCost()+" coins to do that.");
				}
				p.getPacketSender().sendInterfaceRemoval();
				return true;

			}
			if(p.getDialogueActionId() == 444)
			{
				Butlers b = Butlers.forId(((NPC)p.getInteractingEntity()).getId());
				if(p.getSkillManager().getCurrentLevel(Skill.CONSTRUCTION) < b.getConsLevel())
				{
					DialogueManager.start(p, ConstructionDialogues.hireServantDeclineDialogue(p, b.getNpcId(), "lvlreq"));
					return true;
				}
				int roomCount = 0;
				for(int z = 0; z < p.getHouseRooms().length; z++)
				{
					for(int x = 0; x < p.getHouseRooms()[z].length; x++)
					{
						for(int y = 0; y < p.getHouseRooms()[z][x].length; y++)
						{
							if(p.getHouseRooms()[z][x][y] == null)
							{
								continue;
							}
							if(p.getHouseRooms()[z][x][y].getType() == ConstructionData.BEDROOM)
								roomCount++;
							if(roomCount > 1)
								break;
						}
					}
				}
				if(roomCount < 2)
				{
					DialogueManager.start(p, ConstructionDialogues.hireServantDeclineDialogue(p, b.getNpcId(), "room"));
					return true;
				}
				DialogueManager.start(p, ConstructionDialogues.hireServantMakeDealDialogue(p, b.getNpcId()));
				return true;
			}
			if(p.getDialogueActionId() == 448)
			{
				p.setServantItemFetch(((NPC)p.getInteractingEntity()).getId());
				DialogueManager.start(p, ConstructionDialogues.finalServantDealDialogue(true));
				return true;
			}
			if(p.getDialogueActionId() == 439)
			{
				p.getInventory().add(7671, 1);
				return true;
			}
			if (p.getDialogueActionId() == 433) {
				p.getInventory().add(7738, 1);
				return true;
			}
			if (p.getDialogueActionId() == 422) {
				if (p.getPosition().getZ() == 0 && !p.inConstructionDungeon())
					deleteRoom(p, 0);
				if (p.inConstructionDungeon())
					deleteRoom(p, 4);
				if (p.getPosition().getZ() == 1) {
					deleteRoom(p, 1);
				}
				return true;
			}
			if (p.getDialogueActionId() == 419) {

				int myTiles[] = getMyChunk(p);
				Room room = p.getRegionInstance().getOwner().getHouseRooms()[4][myTiles[0] - 1][myTiles[1] - 1];
				if (room != null) {
					p.getPacketSender().sendMessage("Error handling room.");
					p.getPacketSender().sendInterfaceRemoval();
				} else {
					createRoom(ConstructionData.DUNGEON_STAIR_ROOM, p, 102);
					p.getPacketSender().sendInterfaceRemoval();
				}
				return true;
			}
			if (p.getDialogueActionId() == 438) {

				int myTiles[] = getMyChunk(p);
				Room room = p.getRegionInstance().getOwner().getHouseRooms()[4][myTiles[0] - 1][myTiles[1] - 1];
				if (room != null) {
					p.getPacketSender().sendMessage("You did something retarded and now there is a under you for some reason.");
					p.getPacketSender().sendInterfaceRemoval();
				} else {
					createRoom(ConstructionData.OUBLIETTE, p, 103);
					p.getPacketSender().sendInterfaceRemoval();
				}
				return true;
			}
			if (p.getDialogueActionId() == 416) {

				int myTiles[] = getMyChunk(p);
				Room room = p.getRegionInstance().getOwner().getHouseRooms()[0][myTiles[0] - 1][myTiles[1] - 1];
				Room room_1 = p.getRegionInstance().getOwner().getHouseRooms()[1][myTiles[0] - 1][myTiles[1] - 1];
				if (room.getType() != ConstructionData.EMPTY) {
					p.getPacketSender().sendMessage("You did something retarded and now there is a under above you for some reason.");
					p.getPacketSender().sendInterfaceRemoval();
				} else {
					createRoom(
							room_1.getType() == ConstructionData.SKILL_HALL_DOWN ? ConstructionData.SKILL_ROOM
									: ConstructionData.QUEST_ROOM, p, 101);
					p.getPacketSender().sendInterfaceRemoval();
				}
				return true;
			}

			if (p.getDialogueActionId() == 414) {

				int myTiles[] = getMyChunk(p);
				Room room = p.getRegionInstance().getOwner().getHouseRooms()[1][myTiles[0] - 1][myTiles[1] - 1];
				Room room_1 = p.getRegionInstance().getOwner().getHouseRooms()[0][myTiles[0] - 1][myTiles[1] - 1];
				if (room != null) {
					p.getPacketSender().sendMessage("You did something retarded and now there is a room above you for some reason.");
					p.getPacketSender().sendInterfaceRemoval();
				} else {
					createRoom(
							room_1.getType() == ConstructionData.SKILL_ROOM ? ConstructionData.SKILL_HALL_DOWN
									: ConstructionData.QUEST_HALL_DOWN, p, 100);
					p.getPacketSender().sendInterfaceRemoval();
				}
				return true;
			}
			break;

		case 2462:// no option
			if(p.getDialogueActionId() == 448 || p.getDialogueActionId() == 444)
			{
				DialogueManager.start(p, ConstructionDialogues.finalServantDealDialogue(false));
				return true;
			}
			if(p.getDialogueActionId() == 439)
			{
				p.getInventory().add(7673, 1);
				return true;
			}
			if (p.getDialogueActionId() == 433) {
				p.getInventory().add(1927, 1);
				return true;
			}
			if (p.getDialogueActionId() == 414 || p.getDialogueActionId() == 416
					|| p.getDialogueActionId() == 419 || p.getDialogueActionId() == 457) {
				p.getPacketSender().sendInterfaceRemoval();
				return true;
			}
			break;

		case 28647:
			createRoom(ConstructionData.PARLOUR, p, p.getPosition().getZ());
			return true;
		case 28651:
			createRoom(ConstructionData.GARDEN, p, p.getPosition().getZ());
			return true;
		case 28655:
			createRoom(ConstructionData.KITCHEN, p, p.getPosition().getZ());
			return true;
		case 28659:
			createRoom(ConstructionData.DINING_ROOM, p, p.getPosition().getZ());
			return true;
		case 28663:
			createRoom(ConstructionData.WORKSHOP, p, p.getPosition().getZ());
			return true;
		case 28667:
			createRoom(ConstructionData.BEDROOM, p, p.getPosition().getZ());
			return true;
		case 28671:
			createRoom(ConstructionData.SKILL_ROOM, p, p.getPosition().getZ());
			return true;
		case 28675:
			createRoom(ConstructionData.GAMES_ROOM, p, p.getPosition().getZ());
			return true;
		case 28679:
			createRoom(ConstructionData.COMBAT_ROOM, p, p.getPosition().getZ());
			return true;
		case 28683:
			createRoom(ConstructionData.QUEST_ROOM, p, p.getPosition().getZ());
			return true;
		case 28687:
			createRoom(ConstructionData.MENAGERY, p, p.getPosition().getZ());
			return true;
		case 28691:
			createRoom(ConstructionData.STUDY, p, p.getPosition().getZ());
			return true;
		case 28695:
			createRoom(ConstructionData.COSTUME_ROOM, p, p.getPosition().getZ());
			return true;
		case 28699:
			createRoom(ConstructionData.CHAPEL, p, p.getPosition().getZ());
			return true;
		case 28703:
			createRoom(ConstructionData.PORTAL_ROOM, p, p.getPosition().getZ());
			return true;
		case 28707:
			createRoom(ConstructionData.FORMAL_GARDEN, p, p.getPosition().getZ());
			return true;
		case 28711:
			createRoom(ConstructionData.THRONE_ROOM, p, p.getPosition().getZ());
			return true;
		case 28715:
			createRoom(ConstructionData.OUBLIETTE, p, p.getPosition().getZ());
			return true;
		case 28719:
			createRoom(ConstructionData.CORRIDOR, p, p.getPosition().getZ());
			return true;
		case 28723:
			createRoom(ConstructionData.JUNCTION, p, p.getPosition().getZ());
			return true;
		case 28727:
			createRoom(ConstructionData.DUNGEON_STAIR_ROOM, p, p.getPosition().getZ());
			return true;
		case 28731:
			createRoom(ConstructionData.PIT, p, p.getPosition().getZ());
			return true;
		case 28735:
			createRoom(ConstructionData.TREASURE_ROOM, p, p.getPosition().getZ());
			return true;
		}
		return false;
	}

	public static boolean roomExists(Player p) {
		int[] myTiles = getMyChunk(p);
		int xOnTile = getXTilesOnTile(myTiles, p);
		int yOnTile = getYTilesOnTile(myTiles, p);
		int direction = 0;
		final int LEFT = 0, DOWN = 1, RIGHT = 2, UP = 3;
		if (xOnTile == 0)
			direction = LEFT;
		if (yOnTile == 0)
			direction = DOWN;
		if (xOnTile == 7)
			direction = RIGHT;
		if (yOnTile == 7)
			direction = UP;
		int xOff = 0, yOff = 0;
		if (direction == LEFT) {
			xOff = -1;
		}
		if (direction == DOWN) {
			yOff = -1;
		}
		if (direction == RIGHT) {
			xOff = 1;
		}
		if (direction == UP) {
			yOff = 1;
		}
		Room room = p.getRegionInstance().getOwner().getHouseRooms()[p.inConstructionDungeon() ? 4 : p.getPosition().getZ()][myTiles[0]
				- 1 + xOff][myTiles[1] - 1 + yOff];
		if (room == null)
			return false;
		if (room.getType() == ConstructionData.BUILDABLE
				|| room.getType() == ConstructionData.EMPTY
				|| room.getType() == ConstructionData.DUNGEON_EMPTY)
			return false;
		return true;
	}

	public static boolean handleRemoveClick(int obX, int obY, int objectId,
			Player p) {
		if (objectId == 13126 || objectId == 13127 || objectId == 13128
				|| objectId == 13132)
			objectId = 13126;
		if (objectId == 13133 || objectId == 13134 || objectId == 13135
				|| objectId == 13136)
			objectId = 13133;
		if (objectId == 13137 || objectId == 13138 || objectId == 13139
				|| objectId == 13140)
			objectId = 13137;
		if (objectId == 13145 || objectId == 13147)
			objectId = 13145;
		if (objectId == 13142 || objectId == 13143 || objectId == 13144)
			objectId = 13142;
		if (objectId == 13588 || objectId == 13589 || objectId == 13590)
			objectId = 13588;
		if (objectId == 13591 || objectId == 13592 || objectId == 13593)
			objectId = 13591;
		if (objectId == 13594 || objectId == 13595 || objectId == 13596)
			objectId = 13594;
		if (objectId > 13456 && objectId <= 13476)
			objectId = 13456;
		if (objectId > 13449 && objectId <= 13455)
			objectId = 13449;
		if (objectId > 13331 && objectId <= 13337 || objectId == 13373)
			objectId = 13331;
		if (objectId > 13313 && objectId <= 13327)
			objectId = 13313;

		Furniture f = Furniture.forFurnitureId(objectId);
		if (f == null)
			return false;
		if (f == Furniture.EXIT_PORTAL || f == Furniture.EXIT_PORTAL_) {
			int portalAmt = 0;
			for (HouseFurniture pf : p.getHouseFurniture()) {
				Furniture ff = Furniture.forFurnitureId(pf.getFurnitureId());
				if (ff == Furniture.EXIT_PORTAL || ff == Furniture.EXIT_PORTAL_)
					portalAmt++;
			}
			if (portalAmt < 2) {
				p.getPacketSender().sendMessage("You need atleast 1 exit portal in your house");
				return true;
			}
		}
		int[] myTiles = getMyChunk(p);
		int roomRot = p.getRegionInstance().getOwner().getHouseRooms()[p.inConstructionDungeon() ? 4 : p.getPosition().getZ()][myTiles[0] - 1][myTiles[1] - 1]
				.getRotation();
		Room room = p.getRegionInstance().getOwner().getHouseRooms()[p.inConstructionDungeon() ? 4 : p.getPosition().getZ()][myTiles[0] - 1][myTiles[1] - 1];
		ArrayList<HotSpots> hsses = ConstructionData.HotSpots.forObjectId_3(f
				.getHotSpotId());
		if (hsses.isEmpty())
			return false;

		HotSpots hs = null;
		if (hsses.size() == 1)
			hs = hsses.get(0);
		else {
			for (HotSpots find : hsses) {
				int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
				actualX += ConstructionData.getXOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
				actualY += ConstructionData.getYOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				if (obX == actualX && obY == actualY) {
					hs = find;
					break;
				}
			}
		}
		if (objectId == 13331) {
			hs = HotSpots.OUBLIETTE_FLOOR_1;
		}
		if (objectId == 13313) {
			hs = HotSpots.OUBLIETTE_CAGE_1;
		}
		if (objectId == 13126 || objectId == 13127 || objectId == 13128
				|| objectId == 13132 || objectId == 13133 || objectId == 13134
				|| objectId == 13135 || objectId == 13136 || objectId == 13137
				|| objectId == 13138 || objectId == 13139 || objectId == 13140
				|| objectId == 13145 || objectId == 13147 || objectId == 13142
				|| objectId == 13143 || objectId == 13144) {
			hs = HotSpots.COMBAT_RING_1;
		}
		if (objectId == 13456)
			if (room.getType() == ConstructionData.FORMAL_GARDEN)
				hs = HotSpots.FORMAL_HEDGE_1;
		if (objectId == 13449)
			if (room.getType() == ConstructionData.FORMAL_GARDEN)
				hs = HotSpots.FORMAL_FENCE;
		if (objectId == 15270 || objectId == 15273 || objectId == 15274
				|| objectId >= 13588 && objectId <= 13597) {
			if (room.getType() == ConstructionData.CHAPEL)
				hs = HotSpots.CHAPEL_RUG_1;
			if (room.getType() == ConstructionData.PARLOUR)
				hs = HotSpots.PARLOUR_RUG_3;
			if (room.getType() == ConstructionData.SKILL_ROOM
					|| room.getType() == ConstructionData.SKILL_HALL_DOWN
					|| room.getType() == ConstructionData.QUEST_ROOM
					|| room.getType() == ConstructionData.QUEST_HALL_DOWN
					|| room.getType() == ConstructionData.DUNGEON_STAIR_ROOM
					|| room.getType() == ConstructionData.SKILL_HALL_DOWN)
				hs = HotSpots.SKILL_HALL_RUG_3;
			if (room.getType() == ConstructionData.BEDROOM)
				hs = HotSpots.BEDROOM_RUG_3;
		}
		doFurniturePlace(hs, f, hsses, myTiles, obX, obY, roomRot, p, true,
				p.getPosition().getZ());
		p.performAnimation(new Animation(3685));
		Iterator<HouseFurniture> iterator = p.getHouseFurniture().iterator();
		while (iterator.hasNext()) {
			HouseFurniture pf = iterator.next();
			if (pf.getRoomX() != myTiles[0] - 1
					|| pf.getRoomY() != myTiles[1] - 1
					|| pf.getRoomZ() != (p.inConstructionDungeon() ? 4 : p.getPosition().getZ()))
				continue;
			if (pf.getStandardXOff() == hs.getXOffset()
					&& pf.getStandardYOff() == hs.getYOffset())
				iterator.remove();
		}
		return true;
	}

	public static boolean handleSpaceClick(int obX, int obY, int objectId,
			Player p) {

		int[] myTiles = getMyChunk(p);
		int roomRot = p.getRegionInstance().getOwner().getHouseRooms()[p.inConstructionDungeon() ? 4 : p.getPosition().getZ()][myTiles[0] - 1][myTiles[1] - 1]
				.getRotation();

		ArrayList<HotSpots> hsses = ConstructionData.HotSpots
				.forObjectId_2(objectId);
		if (hsses.isEmpty())
			return false;

		p.setBuildFurnitureX(obX);
		p.setBuildFurnitureY(obY);
		p.setBuildFuritureId(objectId);
		HotSpots hs = null;
		int myRoom = p.getHouseRooms()[p.inConstructionDungeon() ? 4 : p.getPosition().getZ()][myTiles[0]-1][myTiles[1]-1].getType();
		if (hsses.size() == 1)
			hs = hsses.get(0);
		else {
			for (HotSpots find : hsses) {
				int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
				actualX += ConstructionData.getXOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
				actualY += ConstructionData.getYOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				if (p.getBuildFurnitureX() == actualX
						&& p.getBuildFurnitureY() == actualY
						&& myRoom == find.getRoomType()
						|| find.getCarpetDim() != null && myRoom == find.getRoomType()) {
					hs = find;
					break;
				}
			}
		}
		if (hs == null)
			return true;
		ArrayList<Furniture> f = ConstructionData.Furniture.getForHotSpotId(hs
				.getHotSpotId());
		if (f == null)
			return false;
		handleInterfaceItems(f, p);
		handleInterfaceCrosses(f, p, hs);
		p.getPacketSender().sendInterface(38272);
		return true;
	}

	public static void createRoom(int roomType, Player p, int toHeight) {
		RoomData rd = ConstructionData.RoomData.forID(roomType);
		if (rd == null)
			return;
		if (p.getInventory().getAmount(995) < rd.getCost()) {
			p.getPacketSender().sendMessage("You need " + rd.getCost() + " coins to build this");
			return;
		}
		boolean isDungeonRoom = ConstructionData.isDungeonRoom(roomType);
		if (!p.inConstructionDungeon()) {
			if (isDungeonRoom && toHeight != 102 && toHeight != 103) {
				p.getPacketSender().sendMessage("You can only build this room in your dungeon.");
				return;
			}
		} else {
			if (!isDungeonRoom) {
				p.getPacketSender().sendMessage("You can only build this room on the surface");
				return;
			}
		}
		int[] myTiles = getMyChunk(p);
		if(myTiles == null)
			return;
		int xOnTile = getXTilesOnTile(myTiles, p);
		int yOnTile = getYTilesOnTile(myTiles, p);
		int direction = 0;
		final int LEFT = 0, DOWN = 1, RIGHT = 2, UP = 3, SAME = 4;
		if (xOnTile == 0)
			direction = LEFT;
		if (yOnTile == 0)
			direction = DOWN;
		if (xOnTile == 7)
			direction = RIGHT;
		if (yOnTile == 7)
			direction = UP;
		int rotation = ConstructionData.RoomData.getFirstElegibleRotation(rd,
				direction);
		if (toHeight == 100) {
			/** Create room from stair **/
			direction = SAME;
			toHeight = 1;
			rotation = p.getRegionInstance().getOwner().getHouseRooms()[0][myTiles[0] - 1][myTiles[1] - 1].getRotation();
			int stairId = 0;
			for (HouseFurniture furn : p.getHouseFurniture()) {
				if (furn.getRoomX() == myTiles[0] - 1
						&& furn.getRoomY() == myTiles[1] - 1
						&& furn.getRoomZ() == 0) {
					if (furn.getStandardXOff() == 3
							&& furn.getStandardYOff() == 3) {
						stairId = furn.getFurnitureId() + 1;
					}
				}
			}
			doFurniturePlace(HotSpots.SKILL_HALL_STAIRS_1,
					Furniture.forFurnitureId(stairId), null, myTiles,
					ConstructionData.BASE_X + (myTiles[0] * 8) + 3,
					ConstructionData.BASE_Y + (myTiles[1] * 8) + 3, rotation,
					p, false, 1);
			HouseFurniture pf = new HouseFurniture(myTiles[0] - 1,
					myTiles[1] - 1, 1, 37, stairId, 3, 3);
			p.getHouseFurniture().add(pf);
		}
		if (toHeight == 101) {
			direction = SAME;
			toHeight = 0;
			rotation = p.getRegionInstance().getOwner().getHouseRooms()[1][myTiles[0] - 1][myTiles[1] - 1].getRotation();
			int stairId = 0;
			for (HouseFurniture furn : p.getHouseFurniture()) {
				if (furn.getRoomX() == myTiles[0] - 1
						&& furn.getRoomY() == myTiles[1] - 1
						&& furn.getRoomZ() == 1) {
					if (furn.getStandardXOff() == 3
							&& furn.getStandardYOff() == 3) {
						stairId = furn.getFurnitureId() + 1;
					}
				}
			}
			doFurniturePlace(HotSpots.SKILL_HALL_STAIRS,
					Furniture.forFurnitureId(stairId), null, myTiles,
					ConstructionData.BASE_X + (myTiles[0] * 8) + 3,
					ConstructionData.BASE_Y + (myTiles[1] * 8) + 3, rotation,
					p, false, 1);
			HouseFurniture pf = new HouseFurniture(myTiles[0] - 1,
					myTiles[1] - 1, 0, 36, stairId, 3, 3);
			p.getHouseFurniture().add(pf);
		}

		/**
		 * Create dungeon room from entrance
		 */
		if (toHeight == 102 || toHeight == 103) {
			direction = SAME;
			toHeight = 4;
			rotation = p.getRegionInstance().getOwner().getHouseRooms()[0][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();
			HouseFurniture pf = null;
			if (toHeight == 102) {
				int stairId = 13497;
				pf = new HouseFurniture(myTiles[0] - 1, myTiles[1] - 1, 4, 36,
						stairId, 3, 3);
			} else {
				pf = new HouseFurniture(myTiles[0] - 1, myTiles[1] - 1, 4, 88,
						13328, 1, 6);
			}
			p.getHouseFurniture().add(pf);
		}

		Room room = new Room(rotation, roomType, 0);
		PaletteTile tile = new PaletteTile(room.getX(), room.getY(),
				room.getZ(), room.getRotation());

		int xOff = 0, yOff = 0;
		if (direction == LEFT) {
			xOff = -1;
		}
		if (direction == DOWN) {
			yOff = -1;
		}
		if (direction == RIGHT) {
			xOff = 1;
		}
		if (direction == UP) {
			yOff = 1;
		}

		if (toHeight == 1) {
			Room r = p.getRegionInstance().getOwner().getHouseRooms()[0][(myTiles[0] - 1) + xOff][(myTiles[1] - 1)
			                                                                                      + yOff];
			if (r.getType() == ConstructionData.EMPTY
					|| r.getType() == ConstructionData.BUILDABLE
					|| r.getType() == ConstructionData.GARDEN
					|| r.getType() == ConstructionData.FORMAL_GARDEN) {
				p.getPacketSender().sendMessage("You need a foundation to build there");
				return;
			}
		}
		House house = p.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE ? (House) p.getRegionInstance() : ((HouseDungeon)p.getRegionInstance()).getHouse();
		if (toHeight == 4 || p.inConstructionDungeon()) {
			house.getSecondaryPalette().setTile(
					(myTiles[0] - 1) + xOff, (myTiles[1] - 1) + yOff, 0, tile);
		} else {
			house.getPalette().setTile((myTiles[0] - 1) + xOff,
					(myTiles[1] - 1) + yOff, toHeight, tile);
		}
		house.getOwner().getHouseRooms()[p.inConstructionDungeon() ? 4 : toHeight][(myTiles[0] - 1) + xOff][(myTiles[1] - 1)
		                                                                                                    + yOff] = new Room(rotation, roomType, 0);
		p.setConstructionCoords(new int[] {p.getPosition().getX(), p.getPosition().getY()});
		p.getRegionInstance().destruct();
		createPalette(p);
		/*if (p.getFields().inDungeon()) {
			p.getPacketSender().constructMapRegionForConstruction(
					house.getSecondaryPalette());
		} else {
			p.getPacketSender().constructMapRegionForConstruction(house.getPalette());
		}
		p.getPacketSender().sendInterfaceRemoval();*/

	}

	public static void rotateRoom(int wise, Player p)
	{
		if(p.getRegionInstance() == null || p.getRegionInstance().getOwner() != p)
			return;
		int[] myTiles = getMyChunk(p);
		int xOnTile = getXTilesOnTile(myTiles, p);
		int yOnTile = getYTilesOnTile(myTiles, p);
		int direction = 0;
		final int LEFT = 0, DOWN = 1, RIGHT = 2, UP = 3;
		if (xOnTile == 0)
			direction = LEFT;
		if (yOnTile == 0)
			direction = DOWN;
		if (xOnTile == 7)
			direction = RIGHT;
		if (yOnTile == 7)
			direction = UP;
		int xOff = 0, yOff = 0;
		if (direction == LEFT) {
			xOff = -1;
		}
		if (direction == DOWN) {
			yOff = -1;
		}
		if (direction == RIGHT) {
			xOff = 1;
		}
		if (direction == UP) {
			yOff = 1;
		}
		int chunkX = (myTiles[0] - 1) + xOff;
		int chunkY = (myTiles[1] - 1) + yOff;
		Room r = p.getRegionInstance().getOwner().getHouseRooms()[p.inConstructionDungeon() ? 4 : p.getPosition().getZ()][chunkX][chunkY];
		RoomData rd = RoomData.forID(r.getType());
		int toRot = (wise == 0 ? RoomData.getNextEligibleRotationClockWise(rd, direction, r.getRotation()) :
			RoomData.getNextEligibleRotationCounterClockWise(rd, direction, r.getRotation()));
		PaletteTile tile = new PaletteTile(rd.getX(), rd.getY(), 0, toRot);
		p.getPacketSender().sendObjectsRemoval(chunkX, chunkY, p.getPosition().getZ());
		House house = p.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE ? (House) p.getRegionInstance() : ((HouseDungeon)p.getRegionInstance()).getHouse();
		if (p.inConstructionDungeon()) {
			house.getSecondaryPalette().setTile(chunkX, chunkY, 0, tile);
		} else {
			house.getPalette().setTile(chunkX, chunkY, p.getPosition().getZ(), tile);
		}
		p.getRegionInstance().getOwner().getHouseRooms()[p.inConstructionDungeon() ? 4 : p.getPosition().getZ()][chunkX][chunkY].setRotation(toRot);
		p.setConstructionCoords(new int[] {p.getPosition().getX(), p.getPosition().getY()});
		p.getRegionInstance().destruct();
		createPalette(p);
		/*if (p.getFields().inDungeon()) {
			p.getPacketSender().constructMapRegionForConstruction(
					house.getSecondaryPalette());
		} else {
			p.getPacketSender().constructMapRegionForConstruction(house.getPalette());
		}
		placeAllFurniture(p, chunkX, chunkY, p.getFields().inDungeon() ? 4 : p.getPosition().getZ());
		p.getPacketSender().sendInterfaceRemoval();*/

	}
	public static boolean handleFirstObjectClick(int obX, int obY, int objectId, Player p) {

		/*	if (ConstructionActions.handleFirstObjectClick(p, objectId))
			return true;
		 */
		// Stairs
		if (objectId == ConstructionData.Furniture.OAK_STAIRCASE.getFurnitureId() || objectId == ConstructionData.Furniture.OAK_STAIRCASE_1.getFurnitureId()
				|| objectId == ConstructionData.Furniture.TEAK_STAIRCASE.getFurnitureId() || objectId == ConstructionData.Furniture.TEAK_STAIRCASE_1.getFurnitureId()
				|| objectId == ConstructionData.Furniture.MARBLE_STAIRCASE.getFurnitureId() || objectId == ConstructionData.Furniture.MARBLE_STAIRCASE_1.getFurnitureId()) {
			int myTiles[] = getMyChunk(p);
			Room room = p.getRegionInstance().getOwner().getHouseRooms()[1][myTiles[0] - 1][myTiles[1] - 1];
			if (room == null) {
				//DialogueManager.start(p, ConstructionDialogues.buildUpstairs(p, 414));
				return true;
			} else {
				p.setConstructionCoords(new int[] { p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ() + 1 });
				p.moveTo(new Position(ConstructionData.MIDDLE_X, ConstructionData.MIDDLE_Y, p.getPosition().getZ() + 1));
				if (p.inConstructionDungeon()) {
					p.getPacketSender().constructMapRegion(((House) p.getRegionInstance()).getSecondaryPalette());
					placeAllFurniture(p, 4);
					p.getPacketSender().constructMapRegion(((House) p.getRegionInstance()).getSecondaryPalette());
				} else {
					p.getPacketSender().constructMapRegion(((House) p.getRegionInstance()).getPalette());
					placeAllFurniture(p, 0);
					placeAllFurniture(p, 1);
					p.getPacketSender().constructMapRegion(((House) p.getRegionInstance()).getPalette());
				}
				p.moveTo(new Position(p.getConstructionCoords()[0], p.getConstructionCoords()[1], p.getConstructionCoords()[2]));
				p.setConstructionCoords(null);
				p.getPacketSender().sendInterfaceRemoval();
				return true;
			}
		}
		if (objectId == ConstructionData.Furniture.DUNGEON_ENTRANCE.getFurnitureId()) {
			p.getPacketSender().sendMessage("This has been disabled for now.");
			return true;
			/*int myTiles[] = getMyChunk(p);
			Room room = p.getRegionInstance().getOwner().getHouseRooms()[4][myTiles[0] - 1][myTiles[1] - 1];
			if (room == null) {
				DialogueManager.start(p, ConstructionDialogues.buildUpstairs(p, 419));
				return true;
			} else {
				p.setInConstructionDungeon(true);
				p.setConstructionCoords(new int[] { p.getPosition().getX(), p.getPosition().getY(), 4 });
				p.moveTo(new Position(ConstructionData.MIDDLE_X, ConstructionData.MIDDLE_Y, 4));
				if (p.inConstructionDungeon()) {
					p.getPacketSender().constructMapRegion(((House) p.getRegionInstance()).getSecondaryPalette());
					placeAllFurniture(p, 4);
					p.getPacketSender().constructMapRegion(((House) p.getRegionInstance()).getSecondaryPalette());
				} else {
					p.getPacketSender().constructMapRegion(((House) p.getRegionInstance()).getPalette());
					placeAllFurniture(p, 0);
					placeAllFurniture(p, 1);
					p.getPacketSender().constructMapRegion(((House) p.getRegionInstance()).getPalette());
				}
				p.moveTo(new Position(p.getConstructionCoords()[0], p.getConstructionCoords()[1], p.getConstructionCoords()[2]));
				p.setConstructionCoords(null);
				return true;
			}*/
		}
		return false;
	}

	public static void deleteRoom(Player p, int toHeight) {

		int[] myTiles = getMyChunk(p);
		int xOnTile = getXTilesOnTile(myTiles, p);
		int yOnTile = getYTilesOnTile(myTiles, p);
		int direction = 0;
		final int LEFT = 0, DOWN = 1, RIGHT = 2, UP = 3;
		if (xOnTile == 0)
			direction = LEFT;
		if (yOnTile == 0)
			direction = DOWN;
		if (xOnTile == 7)
			direction = RIGHT;
		if (yOnTile == 7)
			direction = UP;

		int roomType = p.inConstructionDungeon() ? ConstructionData.DUNGEON_EMPTY
				: ConstructionData.EMPTY;
		Room room = new Room(0, roomType, 0);
		PaletteTile tile = new PaletteTile(room.getX(), room.getY(),
				room.getZ(), room.getRotation());

		int xOff = 0, yOff = 0;
		if (direction == LEFT) {
			xOff = -1;
		}
		if (direction == DOWN) {
			yOff = -1;
		}
		if (direction == RIGHT) {
			xOff = 1;
		}
		if (direction == UP) {
			yOff = 1;
		}
		int chunkX = (myTiles[0] - 1) + xOff;
		int chunkY = (myTiles[1] - 1) + yOff;
		Room r = p.getRegionInstance().getOwner().getHouseRooms()[p.inConstructionDungeon() ? 4 : toHeight][chunkX][chunkY];
		if (r.getType() == ConstructionData.GARDEN
				|| r.getType() == ConstructionData.FORMAL_GARDEN) {
			int gardenAmt = 0;
			for (int z = 0; z < p.getRegionInstance().getOwner().getHouseRooms().length; z++) {
				for (int x = 0; x < p.getRegionInstance().getOwner().getHouseRooms()[z].length; x++) {
					for (int y = 0; y < p.getRegionInstance().getOwner().getHouseRooms()[z][x].length; y++) {
						Room r1 = p.getRegionInstance().getOwner().getHouseRooms()[z][x][y];
						if (r1 == null)
							continue;
						if (r1.getType() == ConstructionData.GARDEN
								|| r1.getType() == ConstructionData.FORMAL_GARDEN) {
							gardenAmt++;
						}
					}
				}
			}
			if (gardenAmt < 2) {
				p.getPacketSender().sendMessage("You need atleast 1 garden or formal garden");
				p.getPacketSender().sendInterfaceRemoval();
				return;
			}
		}
		p.getPacketSender().sendObjectsRemoval(chunkX, chunkY, p.getPosition().getZ());
		House house = p.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE ? (House) p.getRegionInstance() : ((HouseDungeon)p.getRegionInstance()).getHouse();
		if (p.getPosition().getZ() == 0) {
			if (p.inConstructionDungeon()) {
				house.getSecondaryPalette().setTile(chunkX, chunkY, 0,
						tile);
			} else {
				house.getPalette().setTile(chunkX, chunkY, toHeight,
						tile);
			}
			p.getRegionInstance().getOwner().getHouseRooms()[p.inConstructionDungeon() ? 4 : toHeight][chunkX][chunkY] = new Room(
					0, roomType, 0);
		} else {
			if (p.inConstructionDungeon()) {
				house.getSecondaryPalette().setTile(chunkX, chunkY, 0,
						null);
			} else {
				house.getPalette().setTile(chunkX, chunkY, toHeight,
						null);
			}
			p.getRegionInstance().getOwner().getHouseRooms()[p.inConstructionDungeon() ? 4 : toHeight][chunkX][chunkY] = null;
		}
		p.setConstructionCoords(new int[] {p.getPosition().getX(), p.getPosition().getY()});
		p.getRegionInstance().destruct();
		createPalette(p);
		/*	if (p.getFields().inDungeon()) {
			p.getPacketSender().constructMapRegionForConstruction(
					house.getSecondaryPalette());
		} else {
			p.getPacketSender().constructMapRegionForConstruction(house.getPalette());
		}
		p.getPacketSender().sendInterfaceRemoval();
		 */
		Iterator<HouseFurniture> iterator = p.getHouseFurniture().iterator();
		while (iterator.hasNext()) {
			HouseFurniture pf = iterator.next();
			if (pf.getRoomX() == chunkX
					&& pf.getRoomY() == chunkY
					&& pf.getRoomZ() == toHeight)
				iterator.remove();
		}
		Iterator<Portal> portals = p.getHousePortals().iterator();
		while(portals.hasNext())
		{
			Portal port = portals.next();
			if (port.getRoomX() == chunkX
					&& port.getRoomY() == chunkY
					&& port.getRoomZ() == toHeight)
				iterator.remove();
		}
	}


	public static void handleInterfaceItems(ArrayList<Furniture> items, Player c) {
		c.getPacketSender().sendConstructionInterfaceItems(items);
	}

	public static void handleInterfaceCrosses(ArrayList<Furniture> items,
			Player c, HotSpots hs) {
		int i = 1000;

		for (Furniture f : items) {
			c.getPacketSender().sendString(
					38275 + (i - 1000) * 6,
					Misc.formatPlayerName(f.toString().toLowerCase().replaceAll("_", " ")));
			c.getPacketSender().sendString(38280 + (i - 1000) * 6,
					"Level: " + f.getLevel());
			int i2 = 0;
			boolean canMake = true;
			for (int i1 = 0; i1 < f.getRequiredItems().length; i1++) {
				c.getPacketSender().sendString(
						(38276 + i2) + (i - 1000) * 6,
						f.getRequiredItems()[i1][1]
								+ " x " + ItemDefinition.forId(f.getRequiredItems()[i1][0]).getName());
				if (c.getInventory().getAmount(f.getRequiredItems()[i1][0]) < f.getRequiredItems()[i1][1]) {
					i2++;
					canMake = false;
					continue;
				}
				i2++;
			}
			if (f.getAdditionalSkillRequirements() != null) {
				for (int ii = 0; ii < f.getAdditionalSkillRequirements().length; ii++) {
					c.getPacketSender()
					.sendString((38276 + (i2++)) + (i - 1000) * 6,
							Skill.forId(f.getAdditionalSkillRequirements()[ii][0]).getFormatName()
							+ " "
							+ f.getAdditionalSkillRequirements()[ii][1]);
					if (c.getSkillManager().getCurrentLevel(Skill.forId(f.getAdditionalSkillRequirements()[ii][0])) < f
							.getAdditionalSkillRequirements()[ii][1]) {
						canMake = false;
					}
				}
			}
			if (f.getFurnitureRequired() != -1) {
				Furniture fur = Furniture.forFurnitureId(f
						.getFurnitureRequired());
				c.getPacketSender().sendString(
						(38276 + (i2++)) + (i - 1000) * 6,
						Misc.formatPlayerName(fur.toString().toLowerCase().replaceAll("_", " ")));
				if (canMake) {
					canMake = false;
					int[] myTiles = getMyChunk(c);
					for (HouseFurniture pf : c.getHouseFurniture()) {
						if (pf.getRoomX() == myTiles[0] - 1
								&& pf.getRoomY() == myTiles[1] - 1) {
							if (pf.getHotSpot(c.getHouseRooms()[c.inConstructionDungeon() ? 4
									: c.getPosition().getZ()][myTiles[0] - 1][myTiles[1] - 1]
											.getRotation()) == hs) {
								if (pf.getFurnitureId() != fur.getFurnitureId()) {
									canMake = false;
								} else {
									canMake = true;
								}
							}
						}
					}
				}
			}
			if (canMake)
				c.getPacketSender().sendConfig(i, 0);
			else
				c.getPacketSender().sendConfig(i, 1);
			for (i2 = i2; i2 < 4; i2++) {
				c.getPacketSender().sendString((38276 + i2) + (i - 1000) * 6, "");

			}
			i++;
		}
		for (i = i; i < 1008; i++) {
			c.getPacketSender().sendString(38275 + (i - 1000) * 6, "");
			c.getPacketSender().sendString(38276 + (i - 1000) * 6, "");
			c.getPacketSender().sendString(38277 + (i - 1000) * 6, "");
			c.getPacketSender().sendString(38278 + (i - 1000) * 6, "");
			c.getPacketSender().sendString(38279 + (i - 1000) * 6, "");
			c.getPacketSender().sendString(38280 + (i - 1000) * 6, "");
			c.getPacketSender().sendConfig(i, 0);
		}
	}

	public static void handleItemClick(int itemID, Player p) {
		if (p.getRegionInstance() == null || p.getRegionInstance().getOwner() != p)
			return;
		if (!p.isBuildingMode())
			return;
		Furniture f = ConstructionData.Furniture.forItemId(itemID);
		if (f == null)
			return;

		ArrayList<HotSpots> hsses = ConstructionData.HotSpots
				.forObjectId_2(p.getBuildFurnitureId());
		if (hsses.isEmpty())
			return;
		int[] myTiles = getMyChunk(p);
		int toHeight = (p.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_DUNGEON ? 4 : p.getPosition().getZ());
		int roomRot = p.getRegionInstance().getOwner().getHouseRooms()[toHeight][myTiles[0] - 1][myTiles[1] - 1].getRotation();
		int myRoomType = p.getRegionInstance().getOwner().getHouseRooms()[toHeight][myTiles[0] - 1][myTiles[1] - 1].getType();
		HotSpots s = null;
		if (hsses.size() == 1) {
			s = hsses.get(0);
		} else {
			for (HotSpots find : hsses) {
				int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
				actualX += ConstructionData.getXOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
				actualY += ConstructionData.getYOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				if (p.getBuildFurnitureX() == actualX
						&& p.getBuildFurnitureY() == actualY
						&& myRoomType == find.getRoomType()
						|| find.getCarpetDim() != null && myRoomType == find.getRoomType()) {
					s = find;
					break;
				}
			}
		}
		if (s == null)
			return;

		if(!buildActions(p, f, s))
			return;
		int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
		actualX += ConstructionData.getXOffsetForObjectId(f.getFurnitureId(),
				s, p.getRegionInstance().getOwner().getHouseRooms()[toHeight][myTiles[0] - 1][myTiles[1] - 1]
						.getRotation());
		int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
		actualY += ConstructionData.getYOffsetForObjectId(f.getFurnitureId(),
				s, roomRot);
		if(s.getRoomType() != myRoomType && s.getCarpetDim() == null)
		{
			p.getPacketSender().sendMessage("You can't build this furniture in this room.");
			return;
		}
		doFurniturePlace(s, f, hsses, myTiles, actualX, actualY, roomRot, p,
				false, p.getPosition().getZ());
		HouseFurniture pf = new HouseFurniture(myTiles[0] - 1,
				myTiles[1] - 1, toHeight, s.getHotSpotId(), f.getFurnitureId(),
				s.getXOffset(), s.getYOffset());
		p.getHouseFurniture().add(pf);
		p.getPacketSender().sendInterfaceRemoval();
		p.performAnimation(new Animation(3684));
	}

	public static int[] getConvertedCoords(int tileX, int tileY, int[] myTiles,
			Room room) {
		int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
		actualX += ConstructionData.getXOffsetForObjectId(1, tileX, tileY,
				room.getRotation(), 0);
		int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
		actualY += ConstructionData.getYOffsetForObjectId(1, tileX, tileY,
				room.getRotation(), 0);
		return new int[] { actualX, actualY };
	}

	public static int[] getMyChunk(Player p) {
		for (int x = 0; x < 13; x++) {
			for (int y = 0; y < 13; y++) {
				int minX = ((ConstructionData.BASE_X) + (x * 8));
				int maxX = ((ConstructionData.BASE_X + 7) + (x * 8));
				int minY = ((ConstructionData.BASE_Y) + (y * 8));
				int maxY = ((ConstructionData.BASE_Y + 7) + (y * 8));
				if (p.getPosition().getX() >= minX && p.getPosition().getX() <= maxX && p.getPosition().getY() >= minY
						&& p.getPosition().getY() <= maxY) {
					return new int[] { x, y };
				}
			}
		}
		return null;
	}

	public static int[] getMyChunkFor(int xx, int yy) {
		for (int x = 0; x < 13; x++) {
			for (int y = 0; y < 13; y++) {
				int minX = ((ConstructionData.BASE_X) + (x * 8));
				int maxX = ((ConstructionData.BASE_X + 7) + (x * 8));
				int minY = ((ConstructionData.BASE_Y) + (y * 8));
				int maxY = ((ConstructionData.BASE_Y + 7) + (y * 8));
				if (xx >= minX && xx <= maxX && yy >= minY && yy <= maxY) {
					return new int[] { x, y };
				}
			}
		}
		return null;
	}

	public static int getXTilesOnTile(int[] tile, Player p) {
		int baseX = ConstructionData.BASE_X + (tile[0] * 8);
		return p.getPosition().getX() - baseX;
	}

	public static int getYTilesOnTile(int[] tile, Player p) {
		int baseY = ConstructionData.BASE_Y + (tile[1] * 8);
		return p.getPosition().getY() - baseY;
	}

	public static int getXTilesOnTile(int[] tile, int myX) {
		int baseX = ConstructionData.BASE_X + (tile[0] * 8);
		return myX - baseX;
	}

	public static int getYTilesOnTile(int[] tile, int myY) {
		int baseY = ConstructionData.BASE_Y + (tile[1] * 8);
		return myY - baseY;
	}

	public static boolean buildingHouse(Player player) {
		return player.getRegionInstance() != null && player.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE && player.isBuildingMode();
	}
}
