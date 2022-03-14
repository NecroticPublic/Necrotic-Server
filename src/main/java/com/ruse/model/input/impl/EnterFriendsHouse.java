package com.ruse.model.input.impl;

import com.ruse.model.input.Input;
import com.ruse.world.content.skill.impl.construction.Construction;
import com.ruse.world.entity.impl.player.Player;

public class EnterFriendsHouse extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		player.getPacketSender().sendInterfaceRemoval();
		if(syntax.length() < 3 || player.getUsername().equalsIgnoreCase(syntax))
			return;
		Construction.enterFriendsHouse(player, syntax);
	}
}
