package com.ruse.model.input.impl;

import com.ruse.model.Item;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.input.EnterAmount;
import com.ruse.world.entity.impl.player.Player;

public class EnterAmountToRemoveFromBank extends EnterAmount {


	public EnterAmountToRemoveFromBank(int item, int slot) {
		super(item, slot);
	}

	@Override
	public void handleAmount(Player player, int amount) {
		if(!player.isBanking())
			return;
		int tab = Bank.getTabForItem(player, getItem());
		int item = player.getBankSearchingAttribtues().isSearchingBank() && player.getBankSearchingAttribtues().getSearchedBank() != null ? player.getBankSearchingAttribtues().getSearchedBank().getItems()[getSlot()].getId() : player.getBank(tab).getItems()[getSlot()].getId();
		if(item != getItem())
			return;
		if(!player.getBank(tab).contains(item))
			return;
		int invAmount = player.getBank(tab).getAmount(item);
		if(amount > invAmount) 
			amount = invAmount;
		if(amount <= 0)
			return;
		player.getBank(tab).setPlayer(player).switchItem(player.getInventory(), new Item(item, amount), player.getBank(tab).getSlot(item), false, true);
	}
}
