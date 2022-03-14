package com.ruse.model.input.impl;

import com.ruse.model.Item;
import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.impl.smithing.EquipmentMaking;
import com.ruse.world.content.skill.impl.smithing.SmithingData;
import com.ruse.world.entity.impl.player.Player;

public class EnterAmountToMakeSmithing extends EnterAmount {

	public EnterAmountToMakeSmithing(int item, int slot) {
		super(item, slot);
	}

	@Override
	public void handleAmount(Player player, int amount) {
		Item item111 = new Item(getItem());
		int barsRequired = SmithingData.getBarAmount(item111);
		Item bar = new Item(player.getSelectedSkillingItem(), barsRequired);
		if(amount > (player.getInventory().getAmount(bar.getId()) / barsRequired))
			amount = (player.getInventory().getAmount(bar.getId()) / barsRequired);
		//int x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
		EquipmentMaking.smithItem(player, new Item(player.getSelectedSkillingItem(), barsRequired), new Item(item111.getId(), SmithingData.getItemAmount(item111)), amount);
	}
}
