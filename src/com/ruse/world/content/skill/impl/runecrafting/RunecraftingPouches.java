package com.ruse.world.content.skill.impl.runecrafting;

import com.ruse.world.entity.impl.player.Player;

public class RunecraftingPouches {

	private static final int RUNE_ESS = 1436, PURE_ESS = 7936;

	public enum RunecraftingPouch {
		SMALL(5509, 3, 3),
		MEDIUM_POUCH(5510, 9, 9),
		LARGE_POUCH(5512, 18, 18),
		GIANT_POUCH( 5514, 30, 30);

		RunecraftingPouch(int id, int maxRuneEss, int maxPureEss) {
			this.id = id;
			this.maxRuneEss = maxRuneEss;
			this.maxPureEss = maxPureEss;
		}

		private int id;
		private int maxRuneEss, maxPureEss;
		
		public static RunecraftingPouch forId(int id) {
			for(RunecraftingPouch pouch : RunecraftingPouch.values()) {
				if(pouch.id == id)
					return pouch;
			}
			return null;
		}
	}

	public static void fill(Player p, RunecraftingPouch pouch) {
		if(p.getInterfaceId() > 0) {
			p.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			return;
		}
		int rEss = p.getInventory().getAmount(RUNE_ESS);
		int pEss = p.getInventory().getAmount(PURE_ESS);
		if(rEss == 0 && pEss == 0) {
			p.getPacketSender().sendMessage("You do not have any essence in your inventory.");
			return;
		}
		rEss = rEss > pouch.maxRuneEss ? pouch.maxRuneEss : rEss;
		pEss = pEss > pouch.maxPureEss ? pouch.maxPureEss : pEss;
		int stored = 0;
		if(p.getStoredRuneEssence() >= pouch.maxRuneEss)
			p.getPacketSender().sendMessage("Your pouch can not hold any more Rune essence.");
		if(p.getStoredPureEssence() >= pouch.maxPureEss)
			p.getPacketSender().sendMessage("Your pouch can not hold any more Pure essence.");
		while(rEss > 0 && p.getStoredRuneEssence() < pouch.maxRuneEss && p.getInventory().contains(RUNE_ESS)) {
			p.getInventory().delete(RUNE_ESS, 1);
			p.setStoredRuneEssence(p.getStoredRuneEssence()+1);
			stored++;
		}
		while(pEss > 0 && p.getStoredPureEssence() < pouch.maxPureEss && p.getInventory().contains(PURE_ESS)) {
			p.getInventory().delete(PURE_ESS, 1);
			p.setStoredPureEssence(p.getStoredPureEssence()+1);
			stored++;
		}
		if(stored > 0)
			p.getPacketSender().sendMessage("You fill your pouch with "+stored+" essence..");
	}
	
	public static void empty(Player p, RunecraftingPouch pouch) {
		if(p.getInterfaceId() > 0) {
			p.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			return;
		}
		while(p.getStoredRuneEssence() > 0 && p.getInventory().getFreeSlots() > 0) {
			p.getInventory().add(RUNE_ESS, 1);
			p.setStoredRuneEssence(p.getStoredRuneEssence()-1);
		}
		while(p.getStoredPureEssence() > 0 && p.getInventory().getFreeSlots() > 0) {
			p.getInventory().add(PURE_ESS, 1);
			p.setStoredPureEssence(p.getStoredPureEssence()-1);
		}
	}
	
	public static void check(Player p, RunecraftingPouch pouch) {
		p.getPacketSender().sendMessage("Your pouch currently contains "+p.getStoredPureEssence()+"/"+pouch.maxPureEss+" Pure essence, and "+p.getStoredRuneEssence()+"/"+pouch.maxRuneEss+" Rune essence.");
	}
}
