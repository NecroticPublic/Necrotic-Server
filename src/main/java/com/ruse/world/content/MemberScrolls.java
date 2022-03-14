package com.ruse.world.content;

import com.ruse.model.PlayerRights;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.entity.impl.player.Player;

public class MemberScrolls {
	
	public static void checkForRankUpdate(Player player) {
		if(player.getRights().isStaff()) {
			return;
		}
		if(player.getRights().isMember()) {
			return;
		}
		PlayerRights rights = null;
		if(player.getAmountDonated() >= 1)
			rights = PlayerRights.CONTRIBUTOR;
		if(rights != null && rights != player.getRights()) {
			player.getPacketSender().sendMessage("You've become a "+Misc.formatText(rights.toString().toLowerCase())+"! Congratulations!");
			player.setRights(rights);
			player.getPacketSender().sendRights();
		}
	}
	
	/* 

	 */

	public static boolean handleScroll(Player player, int item) {
		switch(item) {
		case 6769:
		case 10942:
		case 10934:
		case 10935:
		case 10943:
			PlayerLogs.log(player.getUsername(), "Has just redeemed a "+ItemDefinition.forId(item).getName()+" successfully!");
			int funds = item == 6769 ? 5 : item == 10942 ? 10 : item == 10934 ? 25 : item == 10935 ? 50 : item == 10943 ? 100 : -1;
			player.getInventory().delete(item, 1);
			player.incrementAmountDonated(funds);
			player.getPointsHandler().setMemberPoints(funds, true);
			player.getPacketSender().sendMessage("Your account has gained funds worth $"+funds+". Your total is now at $"+player.getAmountDonated()+".");
			checkForRankUpdate(player);
			PlayerPanel.refreshPanel(player);
			break;
		case 10944:
			if(player.getRights().isStaff()) {
				player.getPacketSender().sendMessage("As a staff member, you already have member rights.");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a "+ItemDefinition.forId(item).getName()+", but was a staff member! (Unsuccessful)");
				break;
			}
			if(player.getRights().isMember()) {
				player.getPacketSender().sendMessage("You are a member, that would be wasteful!");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a "+ItemDefinition.forId(item).getName()+", but was already a member! (Unsuccessful)");
				break;
			} else {
				PlayerLogs.log(player.getUsername(), "Has just redeemed a "+ItemDefinition.forId(item).getName()+" successfully!");
				player.getPacketSender().sendMessage("Sending redemption request...");
				player.getInventory().delete(10944, 1);
				player.incrementAmountDonated(10);
				player.setRights(PlayerRights.MEMBER);
				player.getPacketSender().sendMessage("Congratulations! You've upgraded to a member account!");
				player.getPacketSender().sendRights();
				PlayerPanel.refreshPanel(player);
			}
			break;
		case 15420:
			if (!player.getClickDelay().elapsed(100)) {
				player.getPacketSender().sendMessage("Please wait before doing that.");
				break;
			}
			if (player.getChristmas2016() < 7) {
				player.getPacketSender().sendMessage("You need to finish the Christmas 2016 event first.");
				break;
			}
			if (player.getInventory().getFreeSlots() >= 6) {
				player.getInventory().add(13101, 1);
				player.getInventory().add(14595, 1);
				player.getInventory().add(14603, 1);
				player.getInventory().add(22043, 1);
				player.getInventory().add(14602, 1);
				player.getInventory().add(14605, 1);
				player.getPacketSender().sendMessage("Enjoy your presents!");
				player.getPacketSender().sendMessage("-Santa, 2016");
				player.getInventory().delete(15420, 1);
				break;
			} else {
				player.getPacketSender().sendMessage("You'll need at least 6 free spaces to do that.");
			}
			
			player.getClickDelay().reset();
			break;
		case 7630:
			PlayerLogs.log(player.getUsername(), "Has just redeemed a "+ItemDefinition.forId(item).getName()+" successfully!");
			if(player.getInventory().getFreeSlots() > 0) {
				player.getInventory().delete(7630, 1);
				player.getInventory().add(12934, 1000);
				player.getPacketSender().sendMessage("You unpack the crate, and find 1000 of Zulrah's Scales inside.");
			} else {
				player.getPacketSender().sendMessage("You need at least 1 free inventory space to unpack this.");
			}
			break;
		case 13150:
			if (player.getInventory().getFreeSlots() > 1) {
				int boo = Misc.getRandom(10);
				if (player.getRights().isMember()) {
					boo = boo + 1;
					player.getPacketSender().sendMessage("Your Member status increases the odds of getting loot...");
				}
				
				if (boo >= 10) {
					player.getInventory().add(9013, 1);
					if (!player.didFriday13May2016()) {
						player.setFriday13May2016(true);
						player.getPacketSender().sendMessage("<img=10> @red@Congratulations! You get a Skull sceptre from the box!");
					} else {
						player.getPacketSender().sendMessage("<img=10> @red@You get another Skull sceptre from the box!");
					}
				} else {
					player.getPacketSender().sendMessage("That box was empty! Try again!");
				}
				player.getInventory().delete(13150, 1);
			} else {
				player.getPacketSender().sendMessage("You should have at least 2 open spaces first.");
			}
			break;

		}
		return false;
	}
	
	public static Dialogue getTotalFunds(final Player player) {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}
			
			@Override
			public int npcId() {
				return 4657;
			}

			@Override
			public String[] dialogue() {
				return player.getAmountDonated() > 0 ? new String[]{"Your account has claimed scrolls worth $"+player.getAmountDonated()+" in total.", "Thank you for supporting us!"} : new String[]{"Your account has claimed scrolls worth $"+player.getAmountDonated()+" in total."};
			}
			
			@Override
			public Dialogue nextDialogue() {
				return DialogueManager.getDialogues().get(5);
			}
		};
	}
}
