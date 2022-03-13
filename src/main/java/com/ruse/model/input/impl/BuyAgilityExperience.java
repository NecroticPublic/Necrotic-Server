package com.ruse.model.input.impl;

import com.ruse.model.Skill;
import com.ruse.model.input.EnterAmount;
import com.ruse.world.entity.impl.player.Player;

public class BuyAgilityExperience extends EnterAmount {
	
	public static int experience = 103;

	@Override
	public void handleAmount(Player player, int amount) {
		player.getPacketSender().sendInterfaceRemoval();
		if (amount < 1) {
			player.getPacketSender().sendMessage("You must spend at least 1 ticket.");
			return;
		}
		int ticketAmount = player.getInventory().getAmount(2996);
		if(ticketAmount == 0) {
			player.getPacketSender().sendMessage("You do not have any tickets.");
			return;
		}
		if(ticketAmount > amount) {
			ticketAmount = amount;
		}
		
		if(player.getInventory().getAmount(2996) < ticketAmount) {
			return;
		}
		
		int exp = ticketAmount * experience; //7680;
		player.getInventory().delete(2996, ticketAmount);
		player.getSkillManager().addExperience(Skill.AGILITY, exp);
		player.getPacketSender().sendMessage("You've bought "+exp+" Agility experience for "+ticketAmount+" Agility ticket"+(ticketAmount == 1 ? "" : "s")+".");
	}

}
