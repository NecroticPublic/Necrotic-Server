package com.ruse.world.content.skill.impl.herblore;


import com.ruse.model.Item;
import com.ruse.world.content.skill.impl.herblore.PotionCombinating.CombiningDoses;
import com.ruse.world.entity.impl.player.Player;

public class Decanting {
	
	public static void notedDecanting(Player player) {
		/*
		 * This could be written better with an enum, but I was too lazy when writing.
		 * Behold, the culmination of all if statements, Decanting.java
		 */
		for (CombiningDoses p : CombiningDoses.values()) {
			int fullU = p.getFullId();
			int fullN = Item.getNoted(fullU);
			int halfU = p.getHalfId();
			int halfN = Item.getNoted(halfU);
			int quarterU = p.getQuarterId();
			int quarterN = Item.getNoted(quarterU);
			int threeQuartersU = p.getThreeQuartersId();
			int threeQuartersN = Item.getNoted(threeQuartersU);
			int totalDoses = 0;
			int remainder = 0;
			int totalEmptyPots = 0;
			if (player.getInventory().contains(fullU)) {
				totalDoses += (4 * player.getInventory().getAmount(fullU));
				totalEmptyPots += player.getInventory().getAmount(fullU);
				player.getInventory().delete(fullU, player.getInventory().getAmount(fullU));
			}
			if (player.getInventory().contains(threeQuartersN)) {
				totalDoses += (3 * player.getInventory().getAmount(threeQuartersN));
				totalEmptyPots += player.getInventory().getAmount(threeQuartersN);
				player.getInventory().delete(threeQuartersN, player.getInventory().getAmount(threeQuartersN));
			}
			if (player.getInventory().contains(threeQuartersU)) {
				totalDoses += (3 * player.getInventory().getAmount(threeQuartersU));
				totalEmptyPots += player.getInventory().getAmount(threeQuartersU);
				player.getInventory().delete(threeQuartersU, player.getInventory().getAmount(threeQuartersU));
			}
			if (player.getInventory().contains(halfN)) {
				totalDoses += (2 * player.getInventory().getAmount(halfN));
				totalEmptyPots += player.getInventory().getAmount(halfN);
				player.getInventory().delete(halfN, player.getInventory().getAmount(halfN));
			}
			if (player.getInventory().contains(halfU)) {
				totalDoses += (2 * player.getInventory().getAmount(halfU));
				totalEmptyPots += player.getInventory().getAmount(halfU);
				player.getInventory().delete(halfU, player.getInventory().getAmount(halfU));
			}
			if (player.getInventory().contains(quarterN)) {
				totalDoses += (1 * player.getInventory().getAmount(quarterN));
				totalEmptyPots += player.getInventory().getAmount(quarterN);
				player.getInventory().delete(quarterN, player.getInventory().getAmount(quarterN));
			}
			if (player.getInventory().contains(quarterU)) {
				totalDoses += (1 * player.getInventory().getAmount(quarterU));
				totalEmptyPots += player.getInventory().getAmount(quarterU);
				player.getInventory().delete(quarterU, player.getInventory().getAmount(quarterU));
			}
			if (totalDoses > 0) {
				if (totalDoses >= 4) {
					player.getInventory().add(fullN, (totalDoses/4));
					if ((totalDoses % 4) != 0) {
						totalEmptyPots -= 1;
						remainder = totalDoses % 4;
					}
				} else if (totalDoses == 3) {
						player.getInventory().add(threeQuartersN, 1);
				} else if (totalDoses == 2) {
						player.getInventory().add(halfN, 1);
				} else if (totalDoses == 1) {
						player.getInventory().add(quarterN, 1);
				}
				if (remainder == 3) {
						player.getInventory().add(threeQuartersN, 1);
				} else if (remainder == 2) {
						player.getInventory().add(halfN, 1);
				} else if (remainder == 1) {
						player.getInventory().add(quarterN, 1);
				}
				totalEmptyPots -= (totalDoses/4);
				player.getInventory().add(Item.getNoted(PotionCombinating.EMPTY_VIAL), totalEmptyPots);
			}
			
		}
		player.getPacketSender().sendMessage("All applicable potions have been decanted!");
	}
	
	public static void startDecanting(Player player) {
		/**
		 * @depreciated use {@link #notedDecanting()} instead.
		 */
		for (CombiningDoses p : CombiningDoses.values()) {
			int full = p.getFullId();
			int half = p.getHalfId();
			int quarter = p.getQuarterId();
			int threeQuarters = p.getThreeQuartersId();
			int totalDoses = 0;
			int remainder = 0;
			int totalEmptyPots = 0;
			if (player.getInventory().contains(threeQuarters)) {
				totalDoses += (3 * player.getInventory().getAmount(threeQuarters));
				totalEmptyPots += player.getInventory().getAmount(threeQuarters);
				player.getInventory().delete(threeQuarters, player.getInventory().getAmount(threeQuarters));
			}
			if (player.getInventory().contains(half)) {
				totalDoses += (2 * player.getInventory().getAmount(half));
				totalEmptyPots += player.getInventory().getAmount(half);
				player.getInventory().delete(half, player.getInventory().getAmount(half));
			}
			if (player.getInventory().contains(quarter)) {
				totalDoses += (1 * player.getInventory().getAmount(quarter));
				totalEmptyPots += player.getInventory().getAmount(quarter);
				player.getInventory().delete(quarter, player.getInventory().getAmount(quarter));
			}
			if (totalDoses > 0) {
				if (totalDoses >= 4)
					player.getInventory().add(full, totalDoses/4);
				else if (totalDoses == 3)
					player.getInventory().add(threeQuarters, 1);
				else if (totalDoses == 2)
					player.getInventory().add(half, 1);
				else if (totalDoses == 1)
					player.getInventory().add(quarter, 1);
				if ((totalDoses % 4) != 0) {
					totalEmptyPots -= 1;
					remainder = totalDoses % 4;
					if (remainder == 3)
						player.getInventory().add(threeQuarters, 1);
					else if (remainder == 2)
						player.getInventory().add(half, 1);
					else if (remainder == 1)
						player.getInventory().add(quarter, 1);
				}
				totalEmptyPots -= (totalDoses/4);
				player.getInventory().add(PotionCombinating.EMPTY_VIAL, totalEmptyPots);
			}
		}
	}

}
