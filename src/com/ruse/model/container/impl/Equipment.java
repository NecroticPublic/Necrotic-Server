package com.ruse.model.container.impl;

import com.ruse.model.Item;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

/**
 * Represents a player's equipment item container.
 * 
 * @author relex lawl
 */

public class Equipment extends ItemContainer {

	/**
	 * The Equipment constructor.
	 * @param player	The player who's equipment is being represented.
	 */
	public Equipment(Player player) {
		super(player);
	}

	@Override
	public int capacity() {
		return 14;
	}

	@Override
	public StackType stackType() {
		return StackType.DEFAULT;
	}

	@Override
	public ItemContainer refreshItems() {
		getPlayer().getPacketSender().sendItemContainer(this, INVENTORY_INTERFACE_ID);
		return this;
	}

	@Override
	public Equipment full() {
		return this;
	}

	/**
	 * The equipment inventory interface id.
	 */
	public static final int INVENTORY_INTERFACE_ID = 1688;

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

	public boolean wearingNexAmours() {
		int head = getPlayer().getEquipment().getItems()[HEAD_SLOT].getId();
		int body = getPlayer().getEquipment().getItems()[BODY_SLOT].getId();
		int legs = getPlayer().getEquipment().getItems()[LEG_SLOT].getId();
		boolean torva = head == 14008 && body == 14009 && legs == 14010;
		boolean pernix = head == 14011 && body == 14012 && legs == 14013;
		boolean virtus = head == 14014 && body == 14015 && legs == 14016;
		return torva || pernix || virtus;
	}

	public boolean wearingHalberd() {
		ItemDefinition def = ItemDefinition.forId(getPlayer().getEquipment().getItems()[Equipment.WEAPON_SLOT].getId());
		return def != null && def.getName().toLowerCase().endsWith("halberd");
	}

	public boolean properEquipmentForWilderness() {
		int count = 0;
		for(Item item : getValidItems()) {
			if(item != null && item.tradeable())
				count++;
		}
		return count >= 3;
	}

	/**
	 * Gets the amount of item of a type a player has, for example, gets how many Zamorak items a player is wearing for GWD
	 * @param p		The player
	 * @param s		The item type to search for
	 * @return		The amount of item with the type that was found
	 */
	public static int getItemCount(Player p, String s, boolean inventory) {
		int count = 0;
		for(Item t : p.getEquipment().getItems()) {
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
}
