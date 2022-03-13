package com.ruse.net.packet.impl;

import com.ruse.model.Item;
import com.ruse.model.container.impl.Bank;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.world.entity.impl.player.Player;

public class BankModifiableX implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int slot = packet.readUnsignedShortA();
		int component = packet.readShort();
		int item = packet.readUnsignedShortA();
		int amount = packet.readInt();
		switch(component) {
			case 5064:
				Item storing = player.getInventory().forSlot(slot).copy().setAmount(amount);
				if (!player.isBanking() || storing.getId() != item || !player.getInventory().contains(storing.getId()) || player.getInterfaceId() != 5292)
					return;
				player.setCurrentBankTab(Bank.getTabForItem(player, storing.getId()));
				player.getInventory().switchItem(player.getBank(player.getCurrentBankTab()), storing, slot, false, true);
				
				break;
			case 5382:
				int tab = Bank.getTabForItem(player, item);
				int outcome = player.getBankSearchingAttribtues().isSearchingBank() && player.getBankSearchingAttribtues().getSearchedBank() != null ? player.getBankSearchingAttribtues().getSearchedBank().getItems()[slot].getId() : player.getBank(tab).getItems()[slot].getId();
	
				if(item != outcome)
					return;
				if(!player.getBank(tab).contains(outcome))
					return;
	
				player.getBank(tab).setPlayer(player).switchItem(player.getInventory(), new Item(outcome, amount), player.getBank(tab).getSlot(outcome), false, true);
				break;
		}
	}

}
