package com.ruse.model.container.impl;

import com.ruse.model.Item;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import com.ruse.world.entity.impl.player.Player;

/**
 * Represents a player's DungeoneeringIronInventory item container.
 * 
 * @author relex lawl
 */

public class DungeoneeringIronInventory extends ItemContainer {

	/**
	 * The DungeoneeringIronInventory constructor.
	 * @param player	The player who's DungeoneeringIronInventory is being represented.
	 */
	public DungeoneeringIronInventory(Player player) {
		super(player);
	}

	@Override
	public int capacity() {
		return 28;
	}

	@Override
	public StackType stackType() {
		return StackType.DEFAULT;
	}

	@Override
	public ItemContainer refreshItems() {
		return this;
	}

	@Override
	public DungeoneeringIronInventory full() {
		return this;
	}
	
	public DungeoneeringIronInventory open() {
		getPlayer().getMovementQueue().reset();
		//getPlayer().getPacketSender().sendMessage("").sendMessage("Note: When selling an item to a store, it loses 15% of its original value!").sendMessage("Prices shown in the price-checker are the original values!");
		refreshItems();
		//open = true;
		return this;
	}


	/**
	 * The helmet slot.
	 */
	public static final int HEAD_SLOT = 0;

	/**
	 * The cape slot.
	 */
	public static final int CAPE_SLOT = 1;

	/**
	 * The amulet slot.
	 */
	public static final int AMULET_SLOT = 2;

	/**
	 * The weapon slot.
	 */
	public static final int WEAPON_SLOT = 3;

	/**
	 * The chest slot.
	 */
	public static final int BODY_SLOT = 4;

	/**
	 * The shield slot.
	 */
	public static final int SHIELD_SLOT = 5;

	/**
	 * The bottoms slot.
	 */
	public static final int LEG_SLOT = 7;

	/**
	 * The gloves slot.
	 */
	public static final int HANDS_SLOT = 9;

	/**
	 * The boots slot.
	 */
	public static final int FEET_SLOT = 10;

	/**
	 * The rings slot.
	 */
	public static final int RING_SLOT = 12;

	/**
	 * The arrows slot.
	 */
	public static final int AMMUNITION_SLOT = 13;


	/**
	 * Gets the amount of item of a type a player has, for example, gets how many Zamorak items a player is wearing for GWD
	 * @param p		The player
	 * @param s		The item type to search for
	 * @return		The amount of item with the type that was found
	 */
	public static int getItemCount(Player p, String s, boolean inventory) {
		int count = 0;
		for(Item t : p.getDungeoneeringIronInventory().getItems()) {
			if(t == null || t.getId() < 1 || t.getAmount() < 1)
				continue;
			if(t.getDefinition().getName().toLowerCase().contains(s.toLowerCase()))
				count++;
		}
		if(inventory)
			for(Item t : p.getInventory().getItems()) {
				if(t == null || t.getId() < 1 || t.getAmount() < 1)
					continue;
				if(t.getDefinition().getName().toLowerCase().contains(s.toLowerCase()))
					count++;
			}
		return count;
	}
	
	public void exit() {
		for (Item i : this.getValidItems()) {
			System.out.println(i.getDefinition().getName());
		}
		//moveItems(getPlayer().getInventory(), false, true);
		getPlayer().getPacketSender().sendInterfaceRemoval();
	}
	
}
