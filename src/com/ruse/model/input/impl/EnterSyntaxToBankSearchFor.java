package com.ruse.model.input.impl;

import com.ruse.model.container.impl.Bank.BankSearchAttributes;
import com.ruse.model.input.Input;
import com.ruse.world.entity.impl.player.Player;

public class EnterSyntaxToBankSearchFor extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		boolean searchingBank = player.isBanking() && player.getBankSearchingAttribtues().isSearchingBank();
		if(searchingBank)
			BankSearchAttributes.beginSearch(player, syntax);
	}
}
