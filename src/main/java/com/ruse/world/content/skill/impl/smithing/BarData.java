package com.ruse.world.content.skill.impl.smithing;

public class BarData {
	
	public static enum Bars {

		Bronze(13, 2349),
		Iron(25, 2351),
		Steel(38, 2353),
		Mithril(50, 2359),
		Adamant(63, 2361),
		Rune(75, 2363);

		Bars(int exp, int itemId) {
			this.exp = exp;	
			this.itemId = itemId;
		}
		
		private int exp, itemId;

		public int getExp() {
			return exp;
		}
		
		public int getItemId() {
			return itemId;
		}
	}


}
