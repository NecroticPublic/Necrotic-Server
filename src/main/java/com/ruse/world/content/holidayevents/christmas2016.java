package com.ruse.world.content.holidayevents;

import com.ruse.GameSettings;
import com.ruse.model.Animation;
import com.ruse.model.Position;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

public class christmas2016 {
	
	public static Position eventStart = new Position(2789, 3859, 400);
	
	public static boolean isChristmas() {
		return GameSettings.Christmas2016;
	}
	
	public static void handleItemOnReindeer(Player player, int itemId) {
		if (itemId == 1907) { //mind bomb
			player.getPacketSender().sendMessage("That'd be a waste. Jack wanted me to give this to Santa.");
		} else if (itemId == 563) {
			if (player.getChristmas2016() >= 3) {
				player.getPacketSender().sendMessage("You've already given the Reindeer their "+ItemDefinition.forId(itemId).getName()+".");
				return;
			} else if (player.getInventory().getAmount(itemId) >= 100) {
				player.setPositionToFace(new Position(2792, 3861));
				player.performAnimation(new Animation(4540));
				player.getInventory().delete(itemId, 100);
				player.setchristmas2016(3);
				player.getPacketSender().sendMessage("You put the "+ItemDefinition.forId(itemId).getName()+" under the Reins.");
			} else {
				player.getPacketSender().sendMessage("You need at least 100 "+ItemDefinition.forId(itemId).getName()+" for the Reindeer.");
			}
		
		} else if (itemId == 564) {
			if (player.getChristmas2016() == 2) {
				player.getPacketSender().sendMessage("You should use the Law runes first.");
				return;
			}
			if (player.getChristmas2016() >= 4) {
				player.getPacketSender().sendMessage("You've already given the Reindeer their "+ItemDefinition.forId(itemId).getName()+".");
				return;
			} else if (player.getInventory().getAmount(itemId) >= 100) {
				player.setPositionToFace(new Position(2792, 3861));
				player.performAnimation(new Animation(4540));
				player.getInventory().delete(itemId, 100);
				player.setchristmas2016(4);
				player.getPacketSender().sendMessage("You put the "+ItemDefinition.forId(itemId).getName()+" under the Reins.");
			} else {
				player.getPacketSender().sendMessage("You need at least 100 "+ItemDefinition.forId(itemId).getName()+" for the Reindeer.");
			}
		
		} else if (itemId == 561) {
			if (player.getChristmas2016() == 2) {
				player.getPacketSender().sendMessage("You should use the Law runes first.");
				return;
			}
			if (player.getChristmas2016() == 3) {
				player.getPacketSender().sendMessage("You should use the Cosmic runes first.");
				return;
			}
			if (player.getChristmas2016() >= 5) {
				player.getPacketSender().sendMessage("You've already given the Reindeer their "+ItemDefinition.forId(itemId).getName()+".");
				return;
			} else if (player.getInventory().getAmount(itemId) >= 100) {
				player.setPositionToFace(new Position(2792, 3861));
				player.performAnimation(new Animation(4540));
				player.getInventory().delete(itemId, 100);
				player.setchristmas2016(5);
				player.getPacketSender().sendMessage("You put the "+ItemDefinition.forId(itemId).getName()+" under the Reins.");
			} else {
				player.getPacketSender().sendMessage("You need at least 100 "+ItemDefinition.forId(itemId).getName()+" for the Reindeer.");
			}
		
		}
		
		
	}
	
	/*public static void announceChristmas() {
		final NPC npc = new NPC(1552, new Position(3674, 2981, 0));
		if (npc == null || ) {
			//npc.setPosition(new Position(2674, 2981, 0));
			npc.setPositionToFace(new Position(3675, 2981, 0));
			World.register(npc);
		}
		if (npc.getPosition().getX() != 3674 || npc.getPosition().getY() != 2981) {
			return;
		} else {
			System.out.println("handle santa msg");
		}
		
	}*/
	
	

}
