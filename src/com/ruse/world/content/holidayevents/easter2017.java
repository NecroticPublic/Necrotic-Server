package com.ruse.world.content.holidayevents;

import com.ruse.world.entity.impl.player.Player;

public class easter2017 {
	
	public static void resetInterface(Player player) {
		for(int i = 8145; i < 8196; i++)
			player.getPacketSender().sendString(i, "");
		for(int i = 12174; i < 12224; i++)
			player.getPacketSender().sendString(i, "");
		player.getPacketSender().sendString(8136, "Close window");
	}
	
	public static void openInterface(Player player) {
		resetInterface(player);
		player.getPacketSender().sendString(8144, "@bla@- @mag@Easter Event Clues @bla@-").sendInterface(8134);
		int index = 1;
		player.getPacketSender().sendString(8147, "@gre@I should use the World Map to teleport around the world.");
		index++;
		for (int i = 0; i < easter2017data.values().length; i++) {
			
			if (player.getEaster2017() > i+1) {
				player.getPacketSender().sendString(8147+index, "@str@@blu@"+easter2017data.values()[i].getHint());
				index++;
				player.getPacketSender().sendString(8147+index, "...Was in "+easter2017data.values()[i].getLocation()+"!");
			} else if (player.getEaster2017() == easter2017data.values()[i].getRequiredProgress()) {
				player.getPacketSender().sendString(8147+index, "@blu@"+easter2017data.values()[i].getHint());
			}
		
		index++;
		}
		
		if (player.getEaster2017() >= 7) {
			index++;
			player.getPacketSender().sendString(8147+index, "@cya@I should return to the easter bunny!");
			index++;
		}
		if (player.getEaster2017() >= 8) {
			player.getPacketSender().sendString(8147+index, "@red@Event COMPLETE!");
			index++;
			player.getPacketSender().sendString(8147+index, "@gre@I earned bunny ears and a basket of eggs!");
		}
	}

}
