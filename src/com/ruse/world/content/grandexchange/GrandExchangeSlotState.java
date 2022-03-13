package com.ruse.world.content.grandexchange;

import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

/**
 * A Grand Exchange slot's state
 * @author Gabriel Hannason
 */
public enum GrandExchangeSlotState {

	EMPTY {
		@Override
		public void update(Player p, int slot, int geData, int percent, int item) {
			p.getPacketSender().sendGrandExchangeUpdate("resetslot <"+slot+">");
		}
	},
	PENDING_PURCHASE {
		@Override
		public void update(Player p, int slot, int geData, int percent, int item) {
			if(item > 0 && ItemDefinition.forId(item).isNoted()) {
				item = Item.getUnNoted(item);
			}
			p.getPacketSender().sendGrandExchangeUpdate("slotselected item #"+item+"# slotbuy <"+slot+"> ["+geData+"] slotpercent {"+percent+"}");
		}
	},
	FINISHED_PURCHASE {
		@Override
		public void update(Player p, int slot, int geData, int percent, int item) {
			if(item > 0 && ItemDefinition.forId(item).isNoted()) {
				item = Item.getUnNoted(item);
			}
			p.getPacketSender().sendGrandExchangeUpdate("slotselected item #"+item+"# slotbuy <"+slot+"> ["+geData+"] slotpercent {"+percent+"}");
		}
	},
	PENDING_SALE {
		@Override
		public void update(Player p, int slot, int geData, int percent, int item) {
			if(item > 0 && ItemDefinition.forId(item).isNoted()) {
				item = Item.getUnNoted(item);
			}
			p.getPacketSender().sendGrandExchangeUpdate("slotselected item #"+item+"# slotsell <"+slot+"> ["+geData+"] slotpercent {"+percent+"}");
		}
	},
	FINISHED_SALE {
		@Override
		public void update(Player p, int slot, int geData, int percent, int item) {
			if(item > 0 && ItemDefinition.forId(item).isNoted()) {
				item = Item.getUnNoted(item);
			}
			p.getPacketSender().sendGrandExchangeUpdate("slotselected item #"+item+"# slotsell <"+slot+"> ["+geData+"] slotpercent {"+percent+"}");
		}
	},
	ABORTED {
		@Override
		public void update(Player p, int slot, int geData, int percent, int item) {
			p.getPacketSender().sendGrandExchangeUpdate("<"+slot+"> slotaborted");
		}
	};
	
	public abstract void update(Player p, int slot, int geData, int percent, int item);

	public static GrandExchangeSlotState forId(int updateStateOrdinal) {
		for(GrandExchangeSlotState state : GrandExchangeSlotState.values()) {
			if(state.ordinal() == updateStateOrdinal) {
				return state;
			}
		}
		return null;
	}
	
}
