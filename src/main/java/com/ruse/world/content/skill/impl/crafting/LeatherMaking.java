package com.ruse.world.content.skill.impl.crafting;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.input.impl.EnterAmountOfLeatherToCraft;
import com.ruse.util.Misc;
import com.ruse.world.content.Achievements;
import com.ruse.world.content.Achievements.AchievementData;
import com.ruse.world.entity.impl.player.Player;

public class LeatherMaking {

	public static void craftLeatherDialogue(final Player player, final int itemUsed, final int usedWith) {
		player.getSkillManager().stopSkilling();
		for (final leatherData l : leatherData.values()) {
			final int leather = (itemUsed == 1733 ? usedWith : itemUsed);
			if (leather == l.getLeather()) {
				if (l.getLeather() == 1741) {
					player.getPacketSender().sendInterfaceModel(8654, 1, 150);
					player.getPacketSender().sendInterface(2311);
					player.setInputHandling(new EnterAmountOfLeatherToCraft());
					player.setSelectedSkillingItem(leather);
					break;
				} else if(l.getLeather() == 1743) {
					player.getPacketSender().sendString(2799, ItemDefinition.forId(1131).getName()).sendInterfaceModel(1746, 1131, 150).sendChatboxInterface(4429);
					player.getPacketSender().sendString(2800, "How many would you like to make?");
					player.setInputHandling(new EnterAmountOfLeatherToCraft());
					player.setSelectedSkillingItem(leather);
					break;
				}
				String[] name = {
						"Body", "Chaps", "Bandana", "Boots", "Vamb",
				};
				if (l.getLeather() == 6289) {
					player.getPacketSender().sendChatboxInterface(8938);
					player.getPacketSender().sendInterfaceModel(8941, 6322, 180);
					player.getPacketSender().sendInterfaceModel(8942, 6324, 180);
					player.getPacketSender().sendInterfaceModel(8943, 6326, 180);
					player.getPacketSender().sendInterfaceModel(8944, 6328, 180);
					player.getPacketSender().sendInterfaceModel(8945, 6330, 180);
					for (int i = 0; i < name.length; i++) {
						player.getPacketSender().sendString(8949 + (i * 4), name[i]);
					}
					player.setInputHandling(new EnterAmountOfLeatherToCraft());
					player.setSelectedSkillingItem(leather);
					return;
				}
			}
		}
		for (final leatherDialogueData d : leatherDialogueData.values()) {
			final int leather = (itemUsed == 1733 ? usedWith : itemUsed);
			String[] name = {
					"Vamb", "Chaps", "Body",
			};
			if (leather == d.getLeather()) {
				player.getPacketSender().sendChatboxInterface(8880);
				player.getPacketSender().sendInterfaceModel(8883, d.getVamb(), 180);
				player.getPacketSender().sendInterfaceModel(8884, d.getChaps(), 180);
				player.getPacketSender().sendInterfaceModel(8885, d.getBody(), 180);
				for (int i = 0; i < name.length; i++) {
					player.getPacketSender().sendString(8889 + (i * 4), name[i]);
				}
				player.setInputHandling(new EnterAmountOfLeatherToCraft());
				player.setSelectedSkillingItem(leather);
				return;
			}
		}
	}

	public static boolean handleButton(Player player, int button) {
		if(player.getSelectedSkillingItem() < 0)
			return false;
		for (final leatherData l : leatherData.values()) {
			if (button == l.getButtonId(button) && player.getSelectedSkillingItem() == l.getLeather()) {
				craftLeather(player, l, l.getAmount(button));
				return true;
			}
		}
		return false;
	}

	public static void craftLeather(final Player player, final leatherData l, final int amount) {
		player.getPacketSender().sendInterfaceRemoval();
		if (l.getLeather() == player.getSelectedSkillingItem()) {
			if (player.getSkillManager().getCurrentLevel(Skill.CRAFTING) < l.getLevel()) {
				player.getPacketSender().sendMessage("You need a Crafting level of at least "+ l.getLevel() +" to make this.");
				return;
			}
			if (!player.getInventory().contains(1734)) {
				player.getPacketSender().sendMessage("You need some thread to make this.");
				player.getPacketSender().sendInterfaceRemoval();
				return;
			}
			if (player.getInventory().getAmount(l.getLeather()) < l.getHideAmount()) {
				player.getPacketSender().sendMessage("You need some "+ ItemDefinition.forId(l.getLeather()).getName().toLowerCase() +" to make this item.");
				player.getPacketSender().sendInterfaceRemoval();
				return;
			}
			player.setCurrentTask(new Task(2, player, true) {
				int toMake = amount;
				@Override
				public void execute() {
					if (!player.getInventory().contains(1734) || !player.getInventory().contains(1733) || player.getInventory().getAmount(l.getLeather()) < l.getHideAmount()) {
						player.getPacketSender().sendMessage("You have run out of materials.");
						stop();
						return;
					}
					if(Misc.getRandom(5) <= 3)
						player.getInventory().delete(1734, 1);
					player.getInventory().delete(l.getLeather(), l.getHideAmount()).add(l.getProduct(), 1);
					player.getSkillManager().addExperience(Skill.CRAFTING, (int) l.getXP());
					if(l == leatherData.LEATHER_BOOTS) {
						Achievements.finishAchievement(player, AchievementData.CRAFT_A_PAIR_OF_LEATHER_BOOTS);
					} else if(l == leatherData.BLACK_DHIDE_BODY) {
						Achievements.doProgress(player, AchievementData.CRAFT_20_BLACK_DHIDE_BODIES);
					}
					player.performAnimation(new Animation(1249));
					toMake--;
					if (toMake <= 0) {
						stop();
						return;
					}
				}
			});
			TaskManager.submit(player.getCurrentTask());
		}
	}
}
