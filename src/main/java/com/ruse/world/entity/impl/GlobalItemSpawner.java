package com.ruse.world.entity.impl;

import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

public class GlobalItemSpawner {
	
	public static Position ROCKCAKE_POSITION = new Position(3667, 2994, 0);
	private static long timer = System.currentTimeMillis();
	
	public static void startup() {
		if (System.currentTimeMillis() - timer > 1000*60) { //every minute run spawnBarrowSpade for each online player
			World.sendGlobalGroundItems(); //This loops through all online players who aren't null, then sends them back to spawnBarrowSpade after filtration
		}
	}
	
	public static void spawnGlobalGroundItems(Player player) {
		nullCheckAndSpawn(player, new Item(952, 1), new Position(3571, 3312, 0));
		nullCheckAndSpawn(player, new Item(1351, 1), new Position(2693, 9560, 0));
		nullCheckAndSpawn(player, new Item(1949, 1), new Position(3142, 3453, 0));
		nullCheckAndSpawn(player, new Item(1005, 1), new Position(3143, 3453, 0));
		nullCheckAndSpawn(player, new Item(946, 1), new Position(3205, 3212, 0));
		nullCheckAndSpawn(player, new Item(1923, 1), new Position(3208, 3214, 0));
		nullCheckAndSpawn(player, new Item(1931, 1), new Position(3209, 3214, 0));
		nullCheckAndSpawn(player, new Item(1935, 1), new Position(3211, 3212, 0));
		nullCheckAndSpawn(player, new Item(558, 1), new Position(3206, 3208, 0));
		nullCheckAndSpawn(player, new Item (7509, 1), ROCKCAKE_POSITION);
		timer = System.currentTimeMillis();
	}
	
	private static void nullCheckAndSpawn(Player player, Item item, Position pos) {
		if(GroundItemManager.getGroundItem(player, item, pos) == null) {
			GroundItemManager.spawnGroundItem(player, new GroundItem(item, pos, player.getUsername(), false, 60*60, false, 0)); //each player will have an instance of the shovel, will last 60*60 seconds (1 hr)
		}
	}

}
