package com.ruse.model.input.impl;

import org.mindrot.jbcrypt.BCrypt;

import com.ruse.GameSettings;
import com.ruse.model.input.Input;
import com.ruse.util.NameUtils;
import com.ruse.world.entity.impl.player.Player;

public class ChangePassword extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		player.getPacketSender().sendInterfaceRemoval();

		if(syntax == null || syntax.length() <= 2 || syntax.length() > 15 || !NameUtils.isValidName(syntax)) {
			player.getPacketSender().sendMessage("That password is invalid. Please try another password.");
			return;
		}
		if(syntax.contains("_")) {
			player.getPacketSender().sendMessage("Your password can not contain underscores.");
			return;
		}
		if(player.getBankPinAttributes().hasBankPin() && !player.getBankPinAttributes().hasEnteredBankPin()) {
			player.getPacketSender().sendMessage("Please visit the nearest bank and enter your pin before doing this.");
			return;
		}
		
		if (true) {
			player.setPassword(syntax);
			if (GameSettings.BCRYPT_HASH_PASSWORDS) {
				player.setSalt(BCrypt.gensalt(GameSettings.BCRYPT_ROUNDS)); //discussion: https://crypto.stackexchange.com/questions/18963/should-you-change-salt-when-changing-password
			}
			player.getPacketSender().sendMessage("Your password has been updated.");
		}
		
	}
	
}
