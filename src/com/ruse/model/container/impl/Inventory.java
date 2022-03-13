package com.ruse.model.container.impl;

import java.util.Optional;

import com.ruse.model.Item;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import com.ruse.model.container.impl.Bank.BankSearchAttributes;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

/**
 * Represents a player's inventory item container.
 * 
 * @author relex lawl
 */

public class Inventory extends ItemContainer {

	/**
	 * The Inventory constructor.
	 * @param player	The player who's inventory is being represented.
	 */
	public Inventory(Player player) {
		super(player);
	}

	@Override
	public Inventory switchItem(ItemContainer to, Item item, int slot, boolean sort, boolean refresh) {
		if (getItems()[slot].getId() != item.getId())
			return this;
		if (to.getFreeSlots() <= 0 && !(to.contains(item.getId()) && item.getDefinition().isStackable())) {
			to.full();
			return this;
		}
		if(to instanceof BeastOfBurden || to instanceof PriceChecker) {
			if(to instanceof PriceChecker && !item.sellable()) {
				getPlayer().getPacketSender().sendMessage("You cannot do that with this item because it cannot be sold.");
				return this;
			}
			if(item.getAmount() > to.getFreeSlots()) {
				if(!item.getDefinition().isStackable()) {
					item.setAmount(to.getFreeSlots());
				}
			}
		}
		if(to instanceof Bank) {
			int checkId = ItemDefinition.forId(item.getId()).isNoted() ? item.getId() - 1 : item.getId();
			if(to.getAmount(checkId) + item.getAmount() >= Integer.MAX_VALUE || to.getAmount(checkId) + item.getAmount() <= 0) {
				int canBank = (Integer.MAX_VALUE - to.getAmount(checkId)) ;
				if(canBank == 0) {
					getPlayer().getPacketSender().sendMessage("You cannot deposit more of that item into your bank.");
					return this;
				}
				item.setAmount(canBank);
			}
		}
		delete(item, slot, refresh, to);
		if (to instanceof Bank && ItemDefinition.forId(item.getId()).isNoted() && !ItemDefinition.forId(item.getId() - 1).isNoted())
			item.setId(item.getId() - 1);
		to.add(item);
		if (sort && getAmount(item.getId()) <= 0)
			sortItems();
		if (refresh) {
			refreshItems();
			to.refreshItems();
		}
		if(to instanceof Bank && getPlayer().getBankSearchingAttribtues().isSearchingBank() && getPlayer().getBankSearchingAttribtues().getSearchedBank() != null) {
			BankSearchAttributes.addItemToBankSearch(getPlayer(), item);
		}
		return this;
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
	public Inventory refreshItems() {
		getPlayer().getPacketSender().sendItemContainer(this, INTERFACE_ID);
		return this;
	}

	@Override
	public Inventory full() {
		getPlayer().getPacketSender().sendMessage("Not enough space in your inventory.");
		return this;
	}

	/**
	 * Adds a set of items into the inventory.
	 *
	 * @param item
	 * the set of items to add.
	 */
	public void addItemSet(Item[] item) {
		for (Item addItem : item) {
			if (addItem == null) {
				continue;
			}
			add(addItem);
		}
	}

	/**
	 * Deletes a set of items from the inventory.
	 *
	 * @param optional
	 * the set of items to delete.
	 */
	public void deleteItemSet(Optional<Item[]> optional) {
		if(optional.isPresent()) {
			for (Item deleteItem : optional.get()) {
				if (deleteItem == null) {
					continue;
				}

				delete(deleteItem);
			}
		}
	}

	public static final int INTERFACE_ID = 3214;
}
