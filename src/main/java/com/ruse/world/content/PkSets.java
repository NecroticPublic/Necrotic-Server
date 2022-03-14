package com.ruse.world.content;

import com.ruse.model.GameMode;
import com.ruse.model.Item;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

public enum PkSets {

	PURE_SET(1100000, new Item[] {new Item(662), new Item(6107), new Item(6108), new Item(3105), new Item(4587), new Item(1215), new Item(1191), new Item(11118), new Item(2550), new Item(1704), new Item(386, 300), new Item(2441, 15), new Item(2437, 15), new Item(3025, 15)}),
	
	HYBRIDING_MAIN_SET(2300000, new Item[] {new Item(10828), new Item(1704), new Item(4091), new Item(4093), new Item(2503), new Item(2497), new Item(3105), new Item(4675), new Item(3842), new Item(1725), new Item(1127), new Item(1163), new Item(4587), new Item(1215), new Item(1201), new Item(6568), new Item(555, 600), new Item(560, 400), new Item(565, 200), new Item(386, 100), new Item(6686, 15), new Item(3025, 25), new Item(3041, 15), new Item(2441, 15), new Item(2437, 15), new Item(2443, 15)}),
	
	RANGE_MAIN_SET(1500000, new Item[]{new Item(10828), new Item(1704), new Item(10499), new Item(4131), new Item(1079), new Item(2503), new Item(9185), new Item(2491), new Item(9185), new Item(9244, 50), new Item(861), new Item(892, 100), new Item(3842), new Item(560, 100), new Item(9075, 200), new Item(557, 500), new Item(386, 100), new Item(2445, 15), new Item(3025, 15), new Item(2443, 15)}),
	
	MELEE_MAIN_SET(1300000, new Item[]{new Item(10828), new Item(1725), new Item(2550), new Item(1127), new Item(1079), new Item(4131), new Item(4587), new Item(1215), new Item(1201), new Item(6568), new Item(560, 100), new Item(9075, 200), new Item(557, 500), new Item(386, 100), new Item(2441, 15), new Item(2437, 15), new Item(2443, 15), new Item(3025, 25)}),
	
	MAGIC_MAIN_SET(1600000, new Item[]{new Item(3755), new Item(1704), new Item(4091), new Item(4093), new Item(4097), new Item(4675), new Item(2550), new Item(3842), new Item(555, 600), new Item(560, 400), new Item(565, 200), new Item(386, 100), new Item(3041, 15), new Item(3025, 25)});
	
	PkSets(int cost, Item[] items) {
		this.cost = cost;
		this.items = items;
	}
	
	private int cost;
	private Item[] items;
	
	public static void buySet(Player player, PkSets set) {
		player.getPacketSender().sendInterfaceRemoval();
		if(player.getGameMode().equals(GameMode.IRONMAN) || player.getGameMode().equals(GameMode.ULTIMATE_IRONMAN)) {
			player.getPacketSender().sendMessage("You cannot use this feature as an Ironman or Ultimate Ironman.");
			return;
		}
		if(player.getInventory().getFreeSlots() < set.items.length) {
			player.getPacketSender().sendMessage("You need at least "+set.items.length+" free inventory slots to buy this set.");
			return;
		}
		int cost = set.cost;
		boolean usePouch = player.getMoneyInPouch() >= cost;
		int plrMoney = (int) (usePouch ? player.getMoneyInPouchAsInt() : player.getInventory().getAmount(995));
		if(plrMoney < cost) {
			player.getPacketSender().sendMessage("You do not have enough money to buy this set. It costs "+Misc.insertCommasToNumber(""+set.cost)+" coins.");
			return;
		}
		if(usePouch) {
			player.setMoneyInPouch(player.getMoneyInPouch() - cost);
			player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch());
		} else {
			player.getInventory().delete(995, cost);
		}
		player.getInventory().addItems(set.items, true);
		player.getClickDelay().reset();
		player.getPacketSender().sendMessage("You've bought a pvp set.");
	}
}
