package com.ruse.world.content.skill.impl.construction;

import com.ruse.model.Position;
import com.ruse.model.container.impl.Bank;
import com.ruse.world.World;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;
/**
 * 
 * @author Owner Blade
 *
 */
public class Servant extends NPC {

	private boolean fetching, greetVisitors;
	private int[] inventory;
	
	public Servant(int npcId, Position position) {
		super(npcId, position);
	}
	
	public Servant(int npcId, Position position, int inventorySize)
	{
		super(npcId, position);
		inventory = new int[inventorySize];
	}
	
	public boolean addInventoryItem(int itemId)
	{
		for(int i = 0; i < inventory.length; i++)
		{
			if(inventory[i] == 0)
			{
				inventory[i] = itemId;
				return true;
			}
		}
		return false;
	}
	
	public int freeSlots()
	{
		int value = 0;
		for(int i = 0; i < inventory.length; i++)
		{
			if(inventory[i] == 0)
			{
				value++;
			}
		}
		return value;
	}
	
	@Override
	public void appendDeath()
	{
		World.deregister(this);
		getRegionInstance().remove(this);
		putBackInBank(getRegionInstance().getOwner());
	}
	
	public void putBackInBank(Player p)
	{
		p.setBanking(true);
		for(int i = 0; i < inventory.length; i++)
		{
			if(i <= 0)
				continue;
			int tab = Bank.getTabForItem(p, inventory[i]);
			p.getBank(tab).add(inventory[i], 1);
		}
		p.setBanking(false);
	}
	
	public void takeItemsFromBank(Player p, int itemId, int amount)
	{
		for(int i = 0; i < amount; i++)
		{
			if(freeSlots() == 0)
				return;
			int tab = Bank.getTabForItem(p, inventory[i]);
			if(!p.getBank(tab).contains(itemId))
				return;
			if(addInventoryItem(itemId))
				p.getBank(tab).delete(itemId+1, 1);
		}
	}
	
	public void giveItems(Player p)
	{
		for(int i = 0; i < inventory.length; i++)
		{
			if(p.getInventory().getFreeSlots() == 0)
				break;
			p.getInventory().add(inventory[i], 1);
			inventory[i] = 0;
		}
	}
	
	public int[] getInventory()
	{
		return inventory;
	}
	
	public boolean isFetching() {
		return fetching;
	}
	
	public void setFetching(boolean fetching) {
		this.fetching = fetching;
	}
	
	public boolean isGreetVisitors() {
		return greetVisitors;
	}
	
	public void setGreetVisitors(boolean greetVisitors) {
		this.greetVisitors = greetVisitors;
	}

	
}
