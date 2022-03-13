package com.ruse.world.content.skill.impl.construction;

import com.ruse.world.content.skill.impl.construction.ConstructionData.RoomData;

/**
 * 
 * @author Owner Blade
 *
 */
public class Room {

	private int rotation, type, theme;
	private int x, y;
	private boolean[] doors;
	public Room(int rotation, int type, int theme)
	{
		this.rotation = rotation;
		this.type = type;
		this.theme = theme;
		getVarData();
	}
	private void getVarData()
	{
		RoomData rd = ConstructionData.RoomData.forID(type);
		x = rd.getX();
		y = rd.getY();
		doors = rd.getRotatedDoors(rotation);
	}
	public boolean[] getDoors()
	{
		return doors;
	}
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	public int getZ()
	{
		return theme;
	}
	public int getType()
	{
		return type;
	}
	public int getRotation()
	{
		return rotation;
	}
	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}
}