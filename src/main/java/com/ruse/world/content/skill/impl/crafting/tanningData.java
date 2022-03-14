package com.ruse.world.content.skill.impl.crafting;

public enum tanningData {
	
	SOFT_LEATHER(new int[][] {{14817, 1}, {14809, 5}, {14801, 29}, {14793, 28}}, 1739, 1741, 1, new int[] {14777, 14785, 14769}, "Soft leather"),
	HARD_LEATHER(new int[][] {{14818, 1}, {14810, 5}, {14802, 29}, {14794, 28}}, 1739, 1743, 3, new int[] {14778, 14786, 14770}, "Hard leather"),
	SNAKESKIN(new int[][] {{14819, 1}, {14811, 5}, {14803, 29}, {14795, 28}}, 6287, 6289, 15, new int[] {14779, 14787, 14771}, "Snakeskin"),
	SNAKESKIN2(new int[][] {{14820, 1}, {14812, 5}, {14804, 29}, {14796, 28}}, 6287, 6289, 20, new int[] {14780, 14788, 14772}, "Snakeskin"),
	GREEN_DRAGON_LEATHER(new int[][] {{14821, 1}, {14813, 5}, {14805, 29}, {14797, 28}}, 1753, 1745, 20, new int[] {14781, 14789, 14773}, "Green d'hide"),
	BLUE_DRAGON_LEATHER(new int[][] {{14822, 1}, {14814, 5}, {14806, 29}, {14798, 28}}, 1751, 2505, 20, new int[] {14782, 14790, 14774}, "Blue d'hide"),
	RED_DRAGON_LEATHER(new int[][] {{14823, 1}, {14815, 5}, {14807, 29}, {14799, 28}}, 1749, 2507, 20, new int[] {14783, 14791, 14775}, "Red d'hide"),
	BLACK_DRAGON_LEATHER(new int[][] {{14824, 1}, {14816, 5}, {14808, 29}, {14800, 28}}, 1747, 2509, 20, new int[] {14784, 14792, 14776}, "Black d'hide");
	
	private int[][] buttonId;
	private int hideId, leatherId, price;
	private int[] frameId;
	private String name;
	
	private tanningData(final int[][] buttonId, final int hideId, final int leatherId, final int price, final int[] frameId, final String name) {
		this.buttonId = buttonId;
		this.hideId = hideId;
		this.leatherId = leatherId;
		this.price = price;
		this.frameId = frameId;
		this.name = name;
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
	
	public int getHideId() {
		return hideId;
	}
	
	public int getLeatherId() {
		return leatherId;
	}
	
	public int getPrice() {
		return price;
	}
	
	public int getNameFrame() {
		return frameId[0];
	}
	
	public int getCostFrame() {
		return frameId[1];
	}
	
	public int getItemFrame() {
		return frameId[2];
	}
	
	public String getName() {
		return name;
	}
}