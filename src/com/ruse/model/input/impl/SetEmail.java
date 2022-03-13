package com.ruse.model.input.impl;

import com.ruse.model.input.Input;
import com.ruse.world.entity.impl.player.Player;

public class SetEmail extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		player.getPacketSender().sendInterfaceRemoval();
		if(true) {
			player.getPacketSender().sendMessage("This service is currently unavailable.");
			return;
		}
	}
}
