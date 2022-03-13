package com.ruse.world.content.skill.impl.crafting;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

public class Jewelry {
	
	public static void stringAmulet(Player player, final int itemUsed, final int usedWith) {
		final int amuletId = (itemUsed == 1759 ? usedWith : itemUsed);
		if (!player.getInventory().contains(1759)) {
			player.getPacketSender().sendMessage("You need a ball of wool in order to string your "+ItemDefinition.forId(amuletId).getName().toLowerCase()+".");
			return;
		}
		if (!player.getInventory().contains(amuletId)) {
			player.getPacketSender().sendMessage("You need an amulet to utilize your ball of wool.");
			return;
		}
		for (final amuletData a : amuletData.values()) {
			if (amuletId == a.getAmuletId()) {
				player.getInventory().delete(1759, 1);
				player.getInventory().delete(amuletId, 1);
				player.getInventory().add(a.getProduct(), 1);
				player.getSkillManager().addExperience(Skill.CRAFTING, 4);
			}
		}
	}
	
	public static void jewelryMaking(Player player, final String type, final int itemId, final int amount) {
		switch (type) {
		case "RING":
			for (int i = 0; i < jewelryData.RINGS.item.length; i++) {
				if (itemId == jewelryData.RINGS.item[i][1]) {
					mouldJewelry(player, jewelryData.RINGS.item[i][0], itemId, amount, jewelryData.RINGS.item[i][2], jewelryData.RINGS.item[i][3]);
				}
			}
			break;
		case "NECKLACE":
			for (int i = 0; i < jewelryData.NECKLACE.item.length; i++) {
				if (itemId == jewelryData.NECKLACE.item[i][1]) {
					mouldJewelry(player, jewelryData.NECKLACE.item[i][0], itemId, amount, jewelryData.NECKLACE.item[i][2], jewelryData.NECKLACE.item[i][3]);
				}
			}
			break;
		case "AMULET":
			for (int i = 0; i < jewelryData.AMULETS.item.length; i++) {
				if (itemId == jewelryData.AMULETS.item[i][1]) {
					mouldJewelry(player, jewelryData.AMULETS.item[i][0], itemId, amount, jewelryData.AMULETS.item[i][2], jewelryData.AMULETS.item[i][3]);
				}
			}
			break;
		}
	}
	
	private static void mouldJewelry(Player player, final int required, final int itemId, final int amount, final int level, final int xp) {
		if (player.getInterfaceId() != 4161) {
			return;
		}
		player.getPacketSender().sendInterfaceRemoval();

		if (player.getSkillManager().getCurrentLevel(Skill.CRAFTING) < level) {
			player.getPacketSender()
					.sendMessage("You need a Crafting level of at least " +level+ " to mould this.");
			return;
		}
		if (!player.getInventory().contains(2357)) {
			player.getPacketSender().sendMessage("You need a gold bar to mould this item.");
			return;
		}
		if (!player.getInventory().contains(required)) {
			player.getPacketSender().sendMessage("You need "+Misc.anOrA(ItemDefinition.forId(required).getName())
			+" "+ItemDefinition.forId(required).getName().toLowerCase() +" to mould this item.");
			return;
		}
		player.setCurrentTask(new Task(2, player, true) {
			int toMake = amount;

			@Override
			public void execute() {
				if (!player.getInventory().contains(2357) || !player.getInventory().contains(required)) {
					player.getPacketSender().sendMessage("You have run out of materials.");
					stop();
					return;
				}
				if (required != 2357) {
					player.getInventory().delete(2357, 1);
				}
				player.getInventory().delete(required, 1).add(itemId, 1);
				player.getSkillManager().addExperience(Skill.CRAFTING, (int) xp);
				player.setPositionToFace(new Position(3078, 9495, 0));
				player.performAnimation(new Animation(896));
				toMake--;
				if (toMake <= 0) {
					stop();
					return;
				}
			}
		});
		TaskManager.submit(player.getCurrentTask());
	}

	public static enum jewelryData {
		
		RINGS(new int[][] {{2357, 1635, 5, 15}, {1607, 1637, 20, 40}, {1605, 1639, 27, 55}, {1603, 1641, 34, 70}, {1601, 1643, 43, 85}, {1615, 1645, 55, 100}, {6573, 6575, 67, 115}}),
		NECKLACE(new int[][] {{2357, 1654, 6, 20}, {1607, 1656, 22, 55}, {1605, 1658, 29, 60}, {1603, 1660, 40, 75}, {1601, 1662, 56, 90}, {1615, 1664, 72, 105}, {6573, 6577, 82, 120}}),
		AMULETS(new int[][] {{2357, 1673, 8, 30}, {1607, 1675, 24, 65}, {1605, 1677, 31, 70}, {1603, 1679, 50, 85}, {1601, 1681, 70, 100}, {1615, 1683, 80, 150}, {6573, 6579, 90, 165}});
		
		public int[][] item;

		private jewelryData(final int[][] item) {
			this.item = item;
		}
	}

	public static enum amuletData {
		GOLD(1673, 1692),
		SAPPHIRE(1675, 1694),
		EMERALD(1677, 1696),
		RUBY(1679, 1698),
		DIAMOND(1681, 1700),
		DRAGONSTONE(1683, 1702),
		ONYX(6579, 6581);
			
		private int amuletId, product;

		private amuletData(final int amuletId, final int product) {
			this.amuletId = amuletId;
			this.product = product;
		}

		public int getAmuletId() {
			return amuletId;
		}

		public int getProduct() {
			return product;
		}
	}

	private static void loadFreshJewlryInterface(Player player) {
		player.getSkillManager().stopSkilling();
		for (int j = 0; j < 7; j++) { // want it to go from 0 -> 6th iteration
			player.getPacketSender().sendItemOnInterface(4233, -1, j, 1);
			player.getPacketSender().sendItemOnInterface(4239, -1, j, 1);
			player.getPacketSender().sendItemOnInterface(4245, -1, j, 1);
		}

		player.getPacketSender().sendString(4230, "You need a ring mould to craft rings.");
		player.getPacketSender().sendInterfaceModel(4229, 1592, 120);

		player.getPacketSender().sendString(4236, "You need a necklace mould to craft necklaces.");
		player.getPacketSender().sendInterfaceModel(4235, 1597, 120);

		player.getPacketSender().sendString(4242, "You need an amulet mould to craft amulets.");
		player.getPacketSender().sendInterfaceModel(4241, 1595, 120);

		player.getPacketSender().sendInterface(4161);
	}
	
	public static void jewelryInterface(Player player) {
		loadFreshJewlryInterface(player);
		for (final jewelryData i : jewelryData.values()) {
			if (player.getInventory().contains(1592)) {
				boolean changed = false;
				for (int j = 0; j < i.item.length; j++) {
					if (player.getInventory().contains(jewelryData.RINGS.item[j][0])) {
						player.getPacketSender().sendItemOnInterface(4233, jewelryData.RINGS.item[j][1], j, 1);
						changed = true;
					} 

				}
				
				if (!changed) {
					player.getPacketSender().sendString(4230, "You need a gemstone and/or a gold bar first.");
					player.getPacketSender().sendInterfaceModel(4229, 1613, 120);
				} else {
					player.getPacketSender().sendString(4230, "");
					player.getPacketSender().sendInterfaceModel(4229, -1, 0);
				}
			}
			if (player.getInventory().contains(1597)) {
				boolean changed = false;
				for (int j = 0; j < i.item.length; j++) {
					if (player.getInventory().contains(jewelryData.NECKLACE.item[j][0])) {
						player.getPacketSender().sendItemOnInterface(4239, jewelryData.NECKLACE.item[j][1], j, 1);
						changed = true;
					} 
					/*else {
						player.getPacketSender().sendItemOnInterface(4239, jewelryData.NECKLACE.item[j][0], j, 1);
					}*/

				}
				if (!changed) {
					player.getPacketSender().sendString(4236, "You need a gemstone and/or a gold bar first.");
					player.getPacketSender().sendInterfaceModel(4235, 4671, 120);
				} else {
					player.getPacketSender().sendString(4236, "");
					player.getPacketSender().sendInterfaceModel(4235, -1, 0);
				}
			}

			if (player.getInventory().contains(1595)) {
				boolean changed = false;
				for (int j = 0; j < i.item.length; j++) {
					if (player.getInventory().contains(jewelryData.AMULETS.item[j][0])) {
						player.getPacketSender().sendItemOnInterface(4245, jewelryData.AMULETS.item[j][1], j, 1);
						changed = true;
					} 
					/*else {
						player.getPacketSender().sendItemOnInterface(4245, jewelryData.AMULETS.item[j][0], j, 1);
					}*/
						 
				}
				if (!changed) {
					player.getPacketSender().sendString(4242, "You need a gemstone and/or a gold bar first.");
					player.getPacketSender().sendInterfaceModel(4241, 4673, 120);
				} else {
					player.getPacketSender().sendString(4242, "");
					player.getPacketSender().sendInterfaceModel(4241, -1, 0);
				}
			}
		}
	}
}
