package com.ruse.model.input.impl;

import com.ruse.model.input.Input;
import com.ruse.world.content.DropsInterface;
import com.ruse.world.entity.impl.player.Player;

public class EnterSyntaxToSearchDropsFor extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		//player.getPacketSender().sendInterfaceRemoval();

		/*if (player.getInterfaceId() != DropsInterface.INTERFACE_ID) {
			player.getPacketSender().sendMessage("Your drop interface = "+player.getInterfaceId());
			player.getPacketSender().sendMessage("You can only search with the drop interface open.");
			return;
		}*/
		if(syntax == null || syntax.length() < 1 || syntax.length() > 12) {//|| !NameUtils.isValidName(syntax)) {
			player.getPacketSender().sendMessage("Unable to search for \""+syntax+"\". Please try again.");
			return;
		}

		if (true) {
			DropsInterface.handleSearchInput(player, syntax);
		}
	}
}

