package com.ruse.world.content.skill.impl.crafting;

public enum leatherData {
	
	LEATHER_GLOVES(new int[][] {{8638, 1}, {8637, 5}, {8636, 10}}, 1741, 1059, 1, 13.8, 1),
	LEATHER_BOOTS(new int[][] {{8641, 1}, {8640, 5}, {8639, 10}}, 1741, 1061, 7, 16.25, 1),
	LEATHER_COWL(new int[][] {{8653, 1}, {8652, 5}, {8651, 10}}, 1741, 1167, 9, 18.5, 1),
	LEATHER_VAMBRACES(new int[][] {{8644, 1}, {8643, 5}, {8642, 10}}, 1741, 1063, 11, 22, 1),
	LEATHER_BODY(new int[][] {{8635, 1}, {8634, 5}, {8633, 10}}, 1741, 1129, 14, 25, 1),
	LEATHER_CHAPS(new int[][] {{8647, 1}, {8646, 5}, {8645, 10}}, 1741, 1095, 18, 27, 1),
	HARD_LEATHER_BODY(new int[][] {{2799, 1}, {2798, 5}, {1747, 28}}, 1743, 1131, 28, 35, 1),
	COIF(new int[][] {{8650, 1}, {8649, 5}, {8648, 10}}, 1741, 1169, 38, 37, 1),
	SNAKESKIN_BOOTS(new int[][] {{8961, 1}, {8960, 5}, {8959, 10}}, 6289, 6328, 45, 30, 6),
	SNAKESKIN_VAMBRACES(new int[][] {{8965, 1}, {8964, 5}, {8963, 10}}, 6289, 6330, 47, 35, 8),
	SNAKESKIN_BANDANA(new int[][] {{8957, 1}, {8956, 5}, {8955, 10}}, 6289, 6326, 48, 45, 5),
	SNAKESKIN_CHAPS(new int[][] {{8953, 1}, {8952, 5}, {8951, 10}}, 6289, 6324, 51, 50, 12),
	SNAKESKIN_BODY(new int[][] {{8949, 1}, {8948, 5}, {8947, 10}}, 6289, 6322, 53, 55, 15),
	GREEN_DHIDE_VAMBRACES(new int[][] {{8889, 1}, {8888, 5}, {8887, 10}}, 1745, 1065, 57, 62, 1),
	GREEN_DHIDE_CHAPS(new int[][] {{8893, 1}, {8892, 5}, {8891, 10}}, 1745, 1099, 60, 124, 2),
	GREEN_DHIDE_BODY(new int[][] {{8897, 1}, {8896, 5}, {8895, 10}}, 1745, 1135, 63, 186, 3),
	BLUE_DHIDE_VAMBRACES(new int[][] {{8889, 1}, {8888, 5}, {8887, 10}}, 2505, 2487, 66, 70, 1),
	BLUE_DHIDE_CHAPS(new int[][] {{8893, 1}, {8892, 5}, {8891, 10}}, 2505, 2493, 68, 140, 2),
	BLUE_DHIDE_BODY(new int[][] {{8897, 1}, {8896, 5}, {8895, 10}}, 2505, 2499, 71, 210, 3),
	RED_DHIDE_VAMBRACES(new int[][] {{8889, 1}, {8888, 5}, {8887, 10}}, 2507, 2489, 73, 78, 1),
	RED_DHIDE_CHAPS(new int[][] {{8893, 1}, {8892, 5}, {8891, 10}}, 2507, 2495, 75, 156, 2),
	RED_DHIDE_BODY(new int[][] {{8897, 1}, {8896, 5}, {8895, 10}}, 2507, 2501, 77, 258, 3),
	BLACK_DHIDE_VAMBRACES(new int[][] {{8889, 1}, {8888, 5}, {8887, 10}}, 2509, 2491, 79, 86, 1),
	BLACK_DHIDE_CHAPS(new int[][] {{8893, 1}, {8892, 5}, {8891, 10}}, 2509, 2497, 82, 172, 2),
	BLACK_DHIDE_BODY(new int[][] {{8897, 1}, {8896, 5}, {8895, 10}}, 2509, 2503, 84, 258, 3);
	private int[][] buttonId;
	private int leather, product, level, amount;
	private double xp;
	
	private leatherData(final int[][] buttonId, final int leather, final int product, final int level, final double xp, final int amount) {
		this.buttonId = buttonId;
		this.leather = leather;
		this.product = product;
		this.level = level;
		this.xp = xp;
		this.amount = amount;
	}	
	
	public int getButtonId(final int button) {
		for (int i = 0; i < buttonId.length; i++) {
			if (button == buttonId[i][0]) {
				return buttonId[i][0];
			}
		}
		return -1;
	}
	
	public int getAmount(final int button) {
		for (int i = 0; i < buttonId.length; i++) {
			if (button == buttonId[i][0]) {
				return buttonId[i][1];
			}
		}
		return -1;
	}
	
	public int getLeather() {
		return leather;
	}
	
	public int getProduct() {
		return product;
	}
	
	public int getLevel() {
		return level;
	}
	
	public double getXP() {
		return xp;
	}
	
	public int getHideAmount() {
		return amount;
	}
}