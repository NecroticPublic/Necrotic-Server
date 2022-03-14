package com.ruse.world.content.skill.impl.construction;

import java.util.ArrayList;

import com.ruse.model.RegionInstance;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * 
 * @author Owner Blade
 *
 */
public class House extends RegionInstance {

	private HouseDungeon dungeon;
	private ArrayList<HouseFurniture> litBurners;
	private ArrayList<HouseFurniture> furnitureActivated;
	private boolean locked = false;
	private Player player;
	private Palette palette, secondaryPalette;
	
	public House(Player player) {
		super(player, RegionInstanceType.CONSTRUCTION_HOUSE);
		this.player = player;
		setLitBurners(new ArrayList<HouseFurniture>());
	}
	
	public House(Player player, boolean b)
	{
		super(player, RegionInstanceType.CONSTRUCTION_HOUSE);
		this.player = player;
		setLitBurners(new ArrayList<HouseFurniture>());
		setFurnitureActivated(new ArrayList<HouseFurniture>());
		dungeon = new HouseDungeon(player);
		dungeon.setHouse(this);
	}
	
	public Servant getButler()
	{
		for(NPC npc : getNpcsList())
		{
			if(npc == null)
				continue;
			if(npc.getId() == getOwner().getHouseServant())
				return ((Servant)npc);
		}
		return null;
	}
	
	public void process() {
		House house = player.getRegionInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE ? (House) player.getRegionInstance() : ((HouseDungeon)player.getRegionInstance()).getHouse();
		int[] myTiles = Construction.getMyChunk(player);
		if(myTiles == null)
			return;
		if(myTiles[0] == -1 || myTiles[1] == -1)
			return;
		Room r = house.getOwner().getHouseRooms()[player.inConstructionDungeon() ? 4 : player.getPosition().getZ()][myTiles[0]-1][myTiles[1]-1];
		if(r == null)
			return;
		if(r.getType() == ConstructionData.OUBLIETTE)
		{
			int xOnTile = Construction.getXTilesOnTile(myTiles, player.getPosition().getX());
			int yOnTile = Construction.getYTilesOnTile(myTiles, player.getPosition().getY());
			if(xOnTile >=  2 && xOnTile <= 5
					&& yOnTile >= 2 && yOnTile <= 5)
			{
				HouseFurniture pf = null;
				for(HouseFurniture pf_ : house.getOwner().getHouseFurniture())
				{
					if(pf_.getRoomX() == myTiles[0]-1 && pf_.getRoomY() == myTiles[1]-1
							&& pf_.getHotSpotId() == 85)
					{
						pf = pf_;
						break;
					}
				}
				if(pf != null) {
					if(pf.getFurnitureId() == 13334
							|| pf.getFurnitureId() == 13337)
					{
						if(player.getConstitution() > 0) {
							//p.getCombat().appendHit(p, 20, 0, 2, false);
						//	p.setDamage(new Damage(new Hit(20, CombatIcon.NONE, Hitmask.NONE)));
						}
					}
				}
			}
		}
		
		if(r.getType() == ConstructionData.CORRIDOR)
		{
			int[] converted = Construction.getConvertedCoords(3, 2, myTiles, r);
			int[] converted_1 = Construction.getConvertedCoords(4, 2, myTiles, r);
			int[] converted_2 = Construction.getConvertedCoords(3, 5, myTiles, r);
			int[] converted_3 = Construction.getConvertedCoords(4, 5, myTiles, r);
			if(player.getPosition().getX() == converted[0] && player.getPosition().getY() == converted[1]
					|| player.getPosition().getX() == converted_1[0] && player.getPosition().getY() == converted_1[1]
					|| player.getPosition().getX() == converted_2[0] && player.getPosition().getY() == converted_2[1]
					|| player.getPosition().getX() == converted_3[0] && player.getPosition().getY() == converted_3[1])
			{
				HouseFurniture pf = null;
				for(HouseFurniture pf_ : house.getOwner().getHouseFurniture())
				{
					if(pf_.getRoomX() == myTiles[0] && pf_.getRoomY() == myTiles[1]
							&& pf_.getHotSpotId() == 91)
					{
						int[] coords = Construction.getConvertedCoords(pf_.getStandardXOff(), pf_.getStandardYOff(), myTiles, r);
						if(coords[0] == myTiles[0] && coords[1] == myTiles[1]) {
							pf = pf_;
							break;
						}
					}
				}
				if(pf != null) {
					if(pf.getFurnitureId() >= 13356
							|| pf.getFurnitureId() <= 13360)
					{
						if(player.getConstitution() > 0) {
							//p.getCombat().appendHit(p, 20, 0, 2, false);
							//p.setDamage(new Damage(new Hit(20, CombatIcon.NONE, Hitmask.NONE)));
						}
					}
				}
			}
		}
	}
	
	/*public void playerKilled(Player p)
	{
		int[] myTiles = Construction.getMyChunk(p);
		if(p.combatRingType > 0)
		{
			p.getPacketSender().sendInteractionOption("null", 3, true);
			p.combatRingType = 0;
			p.moveTo(new Position(ConstructionData.BASE_X+(myTiles[0]*8)+1, ConstructionData.BASE_Y+(myTiles[1]*8)+1, p.getPosition().getZ()));
		} else {
			PlayerFurniture portal = Construction.findNearestPortal(p);
			int toX = ConstructionData.BASE_X+((portal.getRoomX()+1)*8);
			int toY = ConstructionData.BASE_Y+((portal.getRoomY()+1)*8);
			p.moveTo(new Position(toX+2, toY+2, 0));
		}
	}*/
	public ArrayList<HouseFurniture> getLitBurners() {
		return litBurners;
	}
	public void setLitBurners(ArrayList<HouseFurniture> litBurners) {
		this.litBurners = litBurners;
	}

	public int getBurnersLit(int roomX, int roomY, int roomZ)
	{
		int i = 0;
		for(HouseFurniture pf : litBurners)
		{
			if(roomX == pf.getRoomX() && roomY == pf.getRoomY() && roomZ == pf.getRoomZ())
				i++;
				
		}
		return i;
	}
	public ArrayList<HouseFurniture> getFurnitureActivated() {
		return furnitureActivated;
	}
	public void setFurnitureActivated(ArrayList<HouseFurniture> furnitureActivated) {
		this.furnitureActivated = furnitureActivated;
	}
	public ArrayList<HouseFurniture> getActivatedObject(int roomX, int roomY, int roomZ)
	{
		ArrayList<HouseFurniture> pfs = new ArrayList<HouseFurniture>();
		for(HouseFurniture pf : furnitureActivated)
		{
			if(roomX == pf.getRoomX() && roomY == pf.getRoomY() && roomZ == pf.getRoomZ())
				pfs.add(pf);
				
		}
		return pfs;
	}
	
	public void greet(Player p)
	{
		Servant s = getButler();
		if(s == null)
			return;
		if(s.isGreetVisitors())
		{
			s.forceChat("Welcome "+p.getUsername()+"!");
		}
	}
	
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public HouseDungeon getDungeon() {
		return dungeon;
	}
	public void setDungeon(HouseDungeon dungeon) {
		this.dungeon = dungeon;
	}

	public void setPalette(Palette palette) {
		this.palette = palette;
	}
	
	public Palette getPalette() {
		return this.palette;
	}
	
	public void setSecondaryPalette(Palette secondaryPalette) {
		this.secondaryPalette = secondaryPalette;
	}
	
	public Palette getSecondaryPalette() {
		return this.secondaryPalette;
	}
	
	@Override
	public void remove(Character character)
	{
		if(dungeon != null)
			dungeon.remove(character);
		super.remove(character);
	}
	
	@Override
	public void destruct()
	{
		dungeon.destruct();
		super.destruct();
	}
	
	@Override
	public void setOwner(Player p)
	{
		super.setOwner(p);
		dungeon.setOwner(p);
	}
	
}
