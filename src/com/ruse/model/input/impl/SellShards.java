package com.ruse.model.input.impl;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.input.EnterAmount;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

public class SellShards extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		if(amount > 85880000)
			amount = 85880000;
		player.getPacketSender().sendInterfaceRemoval();
		
		int shards = player.getInventory().getAmount(18016);
		if(amount > shards)
			amount = shards;
		if(amount == 0) {
			return;
		} else {
			int rew = ItemDefinition.forId(18016).getValue() * amount;
			player.getInventory().delete(18016, (int) amount);
			player.getInventory().add(995, rew);
			player.getPacketSender().sendMessage("You've sold "+amount+" Spirit Shards for "+Misc.insertCommasToNumber(""+(int)rew)+" coins.");
		}
	}

}
