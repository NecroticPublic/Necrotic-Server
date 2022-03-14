package com.ruse;

import com.ruse.model.Item;

public enum ReducedSellPrice {
	
	Cannonball(2, 1000),
	UnicornHornDust(235, 550),
	GroundMudRune(9594, 300),
	DragonstoneBoltTips(9193, 675)
	;
	
	private ReducedSellPrice(int unNotedId, int sellValue) {
		this.unNotedId = unNotedId;
		this.sellValue = sellValue;
	}

	private final int unNotedId;
	private final int sellValue;

	public static ReducedSellPrice forId(int id) {
		for (ReducedSellPrice rsp : ReducedSellPrice.values()) {
			if (rsp.getUnNotedId() == id) {
				return rsp;
			}
			if (rsp.getNotedId() == id) {
				return rsp;
			}
		}
		return null;
	}
	
	public int getUnNotedId() {
		return unNotedId;
	}
	
	public int getNotedId() {
		return Item.getNoted(unNotedId);
	}
	
	public int getSellValue() {
		return sellValue;
	}

}
