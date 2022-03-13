package com.ruse.model.container.impl;

import com.ruse.model.Item;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

public class PriceChecker extends ItemContainer {

	private boolean open;
	public PriceChecker(Player player) {
		super(player);
	}

	public PriceChecker open() {
		getPlayer().getMovementQueue().reset();
		getPlayer().getPacketSender().sendMessage("").sendMessage("Note: When selling an item to a store, it loses 15% of its original value!").sendMessage("Prices shown in the price-checker are the original values!");
		refreshItems();
		open = true;
		return this;
	}

	@Override
	public int capacity() {
		return 20;
	}

	@Override
	public StackType stackType() {
		return StackType.DEFAULT;
	}

	@Override
	public PriceChecker refreshItems() {
		boolean itemFound = false;
		for(int i = 0; i < frames.length; i++) {
			boolean slotOccupied = i < capacity() && getItems()[i].getId() > 0;
			if(slotOccupied) {
				itemFound = true;
				Item it = getItems()[i];
				if(it.getDefinition().isStackable()) {
					int itemAmount = it.getAmount();
					int totalPrice = it.getDefinition().getValue() * itemAmount;
					int frame = getFrame(i, 1);
					getPlayer().getPacketSender().sendString(frame, ""+it.getDefinition().getValue()+" x"+itemAmount);
					getPlayer().getPacketSender().sendString(frame+1, "= "+totalPrice);
					getPlayer().getPacketSender().sendItemOnInterface(getFrame(i, 2), it.getId(), itemAmount);
				} else {
					getPlayer().getPacketSender().sendString(getFrame(i, 1), ""+Misc.insertCommasToNumber(Integer.toString(it.getDefinition().getValue()))+"");
					getPlayer().getPacketSender().sendItemOnInterface(getFrame(i, 2), it.getId(), 1);
				}
			} else {
				getPlayer().getPacketSender().sendString(getFrame(i, 1), "");
				getPlayer().getPacketSender().sendString(getFrame(i, 1)+1, "");
				getPlayer().getPacketSender().sendItemOnInterface(getFrame(i, 2), -1, 1);
			}
			getPlayer().getPacketSender().sendString(18351, ""+Misc.insertCommasToNumber(Integer.toString(calculateTotalWealth())));
		}
		if(itemFound) {
			getPlayer().getPacketSender().sendString(18413, "");
		} else {
			getPlayer().getPacketSender().sendString(18413, "Click an item in your inventory to view it's wealth.");
		}
		getPlayer().getPacketSender().sendInterfaceSet(INTERFACE_ID, 3321);
		getPlayer().getPacketSender().sendItemContainer(getPlayer().getInventory(), 3322);
		return this;
	}

	@Override
	public PriceChecker switchItem(ItemContainer to, Item item, int slot, boolean sort, boolean refresh) {
		if (getItems()[slot].getId() != item.getId())
			return this;
		if (to.getFreeSlots() <= 0) {
			to.full();
			return this;
		}
		if(to instanceof BeastOfBurden || to instanceof PriceChecker) {
			if(item.getAmount() > to.getFreeSlots() && !(item.getDefinition().isStackable() && to.contains(item.getId()))) {
				item.setAmount(to.getFreeSlots());
			}
		}
		if(item.getAmount() <= 0)
			return this;
		delete(item, slot, refresh, to);
		to.add(item);
		if (sort && getAmount(item.getId()) <= 0)
			sortItems();
		if (refresh) {
			refreshItems();
			to.refreshItems();
		}
		return this;
	}

	@Override
	public PriceChecker full() {
		getPlayer().getPacketSender().sendMessage("The pricechecker cannot hold any more items.");
		return this;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void exit() {
		moveItems(getPlayer().getInventory(), false, true);
		open = false;
		getPlayer().getPacketSender().sendInterfaceRemoval();
	}
	
	
	public int calculateTotalWealth() {
		int k = 0;
		for(Item item: getValidItems())
			k += item.getDefinition().getValue() * item.getAmount();
		if(k >= Integer.MAX_VALUE) {
			getPlayer().getPacketSender().sendString(18351, "Too High!");
			return 0;
		}
		return k;
	}
	
	public static int priceCheckerSlot(int interfaceId) {
		if(interfaceId == 18246)
			return 0;
		else if (interfaceId == 18500)
			return 1;
		else {
			int index = interfaceId - 18499;
			return index >= 1 && index <= 20 ? index : -1;
		}
	}

	public static final int INTERFACE_ID = 42000, INTERFACE_PC_ID = 2100;
	
	private static final int frames[][] = {
		{0, 18353, 18246}, {1, 18356, 18500},{2, 18359, 18501}, {3, 18362, 18502 }, {4, 18365, 18503}, {5, 18368, 18504},
		{6, 18371, 18505}, {7, 18374, 18506}, {8, 18377, 18507}, {9, 18380, 18508}, {10, 18383, 18509}, {11, 18386, 18510},
		{12, 18389, 18511 }, {13, 18392, 18512}, {14, 18395, 18513}, {15, 18398, 18514}, {16, 18401, 18515}, {17, 18404, 18516},
		{18, 18407, 18517}, {19, 18410, 18518}
	};

	public static int getFrame(int slot, int index) {
		int k = -1;
		for(int i = 0; i < frames.length; i++) {
			if(frames[i][0] == slot) {
				k = frames[i][index];
			}
		}
		return k;
	}
}
