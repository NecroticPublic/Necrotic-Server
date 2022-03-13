package com.ruse.world.content.combat.range;

import com.ruse.util.Misc;
import com.ruse.world.content.ItemDegrading;
import com.ruse.world.entity.impl.player.Player;



public class ToxicBlowpipe {


	//item used      // item used
	public static boolean loadPipe(Player player) {
		int max = ItemDegrading.maxZulrahCharges,
		zscale = 12934,
		blowpipe = 12926,
		invcount = player.getInventory().getAmount(zscale),
		storecount = player.getBlowpipeCharges();
		
		if(!player.getInventory().contains(blowpipe) || !player.getInventory().contains(zscale)) {
			return false;
		}

		if(storecount >= max) {
			player.getPacketSender().sendMessage("Your item can not hold any more Zulrah scales.");
			return false;
		}
		
		
		if ((invcount + storecount) > max) {
			int toremove = max - storecount;
			player.getInventory().delete(zscale, toremove);
			player.setBlowpipeCharges(max);
			player.getPacketSender().sendMessage("You use "+Misc.format(toremove)+" scales to fill your blowpipe.");
			return true;
		} 
		
		if ((invcount + storecount) < max) {
			player.getInventory().delete(zscale, invcount);
			player.setBlowpipeCharges(invcount+storecount);
			player.getPacketSender().sendMessage("You add "+Misc.format(invcount)+" and now have "+Misc.format(player.getBlowpipeCharges())+" total scale charges.");
			return true;	
		}

		return true;
	}
}


//if we ever wanna do adding darts to blowpipe
/*
	public enum dartTypes {
		BRONZE_DART(806),
		IRON_DART(807),
		STEEL_DART(808),
		MITHRIL_DART(809),
		ADAMANT_DART(810),
		RUNE_DART(811),
		DRAGON_DART(11230);


		private dartTypes(int dartId) {
			this.dartId = dartId;
		}

		public int dartId;



		public int getDartId() { return dartId; }

		public static dartTypes forId(int dartId2) {
			for(dartTypes dt : dartTypes.values()) {
				if(dt.getDartId() == dartId2) {
					return dt;
				}
			}
			return null;
		}

	}

*/


