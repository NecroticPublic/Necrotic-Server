package com.ruse.world.content.skill.impl.smithing;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Item;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.content.skill.impl.smithing.BarData.Bars;
import com.ruse.world.entity.impl.player.Player;

public class EquipmentMaking {
	
	public static int[] possibleItems = {1205, 1351, 1103, 1139, 819, 1277, 1422, 1075, 1155, 39, 1321, 1337, 1087, 1173, 864, 1291, 1375, 1117, 1189, 1307, 3095, 4819, 1203, 1349, 1101, 1137, 9140, 1279, 1420, 1067, 1153, 40, 1323, 1335, 1081, 1175, 863, 1293, 1363, 1115, 1191, 1309, 3096, 4540, 4820, 1207, 1353, 1105, 1141, 9141, 1281, 1424, 1069, 1157, 41, 1325, 1339, 1083, 1177, 865, 1295, 1365, 1119, 1193, 2, 1311, 3097, 1539, 2370, 1209, 1355, 1109, 1143, 9142, 1285, 1428, 1071, 1159, 42, 1329, 1343, 1085, 1181, 866, 1299, 1369, 1121, 1197, 1315, 3099, 4822, 1211, 1357, 1111, 1145, 9143, 1287, 1430, 1073, 1161, 43, 1331, 1345, 1091, 1183, 867, 1301, 1371, 1123, 1199, 1317, 3100, 4823, 1213, 1359, 1113, 1147, 9144, 1289, 1432, 1079, 1163, 44, 1333, 1347, 1093, 1185, 868, 1303, 1373, 1127, 1201, 1319, 3101, 4824,
			9174, 9177, 9179, 9181, 9183, 9185, //CROSSBOWS
			1265, 1267, 1269, 1273, 1271, 1275 //PICKAXES
	};
	
	public static void handleAnvil(Player player) {
		String bar = searchForBars(player);
		if(bar == null) {
			player.getPacketSender().sendMessage("You do not have any bars in your inventory to smith.");
			return;
		} else {
			switch(bar.toLowerCase()) {
			case "rune bar":
				player.setSelectedSkillingItem(2363);
				SmithingData.makeRuneInterface(player);
				break;
			case "adamant bar":
				player.setSelectedSkillingItem(2361);
				SmithingData.makeAddyInterface(player);
				break;
			case "mithril bar":
				player.setSelectedSkillingItem(2359);
				SmithingData.makeMithInterface(player);
				break;
			case "steel bar":
				player.setSelectedSkillingItem(2353);
				SmithingData.makeSteelInterface(player);
				break;
			case "iron bar":
				player.setSelectedSkillingItem(2351);
				SmithingData.makeIronInterface(player);
				break;
			case "bronze bar":
				player.setSelectedSkillingItem(2349);
				SmithingData.showBronzeInterface(player);
				break;
			case "gold bar":
			//case "silver bar":
				player.getPacketSender().sendMessage("Gold bars should be crafted at a furnace.");
				break;
			case "silver bar":
				player.getPacketSender().sendMessage("Hmm... Silver isn't the best choice for the anvil.");
				break;
			}
		}
	}
	
	public static String searchForBars(Player player) {
		for(int bar : SmithingData.BARS_SMITH_ORDER) {
			if(player.getInventory().contains(bar)) {
				return ItemDefinition.forId(bar).getName();
			}
		}
		return null;
	}
	
	public static void smithItem(final Player player, final Item bar, final Item itemToSmith, final int x) {
		boolean canMakeItem = false;
		
		if(bar.getId() < 0)
			return;
		if(!player.getClickDelay().elapsed(1100)) {
			return;
		}
		player.getSkillManager().stopSkilling();
		if(!player.getInventory().contains(2347)) {
			player.getPacketSender().sendMessage("You need a Hammer to smith items.");
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(player.getInventory().getAmount(bar.getId()) < bar.getAmount() || x <= 0) {
			player.getPacketSender().sendMessage("You do not have enough bars to smith this item.");
			return;
		}
		if(SmithingData.getData(itemToSmith, "reqLvl") > player.getSkillManager().getCurrentLevel(Skill.SMITHING)) {
			player.getPacketSender().sendMessage("You need a Smithing level of at least "+SmithingData.getData(itemToSmith, "reqLvl")+" to make this item.");
			return;
		}		
		
		int currentItemId = itemToSmith.getId();
		
		for (int i = 0; i < possibleItems.length; i++) {
			if (possibleItems[i] == currentItemId) {
				canMakeItem = true;
				break;
			}
		}
		
		boolean good2go = false;
		
		for (int i = 0; i < Bars.values().length; i++) {
			/*if (bar.getId() == Bars.values()[i].getItemId()) {
				System.out.println("correct barid");
			}
			if (itemToSmith.getDefinition().getName().startsWith(ItemDefinition.forId(Bars.values()[i].getItemId()).getName().substring(0, 3))) {
				System.out.println("bar 0,3 matches");
			}
			System.out.println(ItemDefinition.forId(Bars.values()[i].getItemId()).getName().substring(0, 3)+" ||| "+itemToSmith.getDefinition().getName());
			if (itemToSmith.getDefinition().getName().startsWith("Cannon")) {
				System.out.println("cannon matches");
			}*/
			if (bar.getId() == Bars.values()[i].getItemId() 
				&& (itemToSmith.getDefinition().getName().startsWith(ItemDefinition.forId(Bars.values()[i].getItemId()).getName().substring(0, 3))
				|| itemToSmith.getDefinition().getName().equalsIgnoreCase("cannonball") || itemToSmith.getDefinition().getName().equalsIgnoreCase("Oil lantern frame"))) {
					good2go = true;
				break;
			} 
		}
		
		if (!good2go || !canMakeItem) {
			PlayerLogs.log("1 - smithing abuse", player.getUsername()+" just tried to smith item: "+currentItemId+" ("+ItemDefinition.forId(currentItemId).getName()+"), IP: "+player.getHostAddress() + " using bar "+bar.getDefinition().getName());
			World.sendStaffMessage("<col=b40404>[BUG ABUSE] "+player.getUsername()+" just tried to smith: "+ItemDefinition.forId(currentItemId).getName() + " using bar "+bar.getDefinition().getName());
			//player.getPacketSender().sendMessage("How am I going to smith "+Misc.anOrA(ItemDefinition.forId(currentItemId).getName())+" "+ItemDefinition.forId(currentItemId).getName()+"?");
			player.getSkillManager().stopSkilling();
			return;
		}
		
		player.getClickDelay().reset();
		player.getPacketSender().sendInterfaceRemoval();
		player.setCurrentTask(new Task(3, player, true) {
			int amountMade = 0;
			@Override
			public void execute() {
				if(player.getInventory().getAmount(bar.getId()) < bar.getAmount() || !player.getInventory().contains(2347) || amountMade >= x) {
					this.stop();
					return;
				}
				if(player.getInteractingObject() != null)
					player.getInteractingObject().performGraphic(new Graphic(2123));
				player.performAnimation(new Animation(898));
				amountMade++;
				Sounds.sendSound(player, Sound.SMITH_ITEM);
				player.getInventory().delete(bar);
				player.getInventory().add(itemToSmith);
				player.getInventory().refreshItems();
				if (ItemDefinition.forId(itemToSmith.getId()).getName().contains("Bronze")) {
					player.getSkillManager().addExperience(Skill.SMITHING, (int) (Bars.Bronze.getExp() * bar.getAmount()));
					//player.getPacketSender().sendMessage("Using: "+bar.getAmount()+" Bronze bars to make: "+ItemDefinition.forId(itemToSmith.getId()).getName());
				} else if (ItemDefinition.forId(itemToSmith.getId()).getName().contains("Iron")) {
					player.getSkillManager().addExperience(Skill.SMITHING, (int) (Bars.Iron.getExp() * bar.getAmount()));
					//player.getPacketSender().sendMessage("Using: "+bar.getAmount()+" Iron bars to make: "+ItemDefinition.forId(itemToSmith.getId()).getName());
				} 	else if (ItemDefinition.forId(itemToSmith.getId()).getName().contains("Steel") || ItemDefinition.forId(itemToSmith.getId()).getName().equalsIgnoreCase("Cannonball")) {
					player.getSkillManager().addExperience(Skill.SMITHING, (int) (Bars.Steel.getExp() * bar.getAmount()));
					//player.getPacketSender().sendMessage("Using: "+bar.getAmount()+" Steel bars to make: "+ItemDefinition.forId(itemToSmith.getId()).getName());
				}   else if (ItemDefinition.forId(itemToSmith.getId()).getName().contains("Mith")) {
					player.getSkillManager().addExperience(Skill.SMITHING, (int) (Bars.Mithril.getExp() * bar.getAmount()));
					//player.getPacketSender().sendMessage("Using: "+bar.getAmount()+" Mith bars to make: "+ItemDefinition.forId(itemToSmith.getId()).getName());
				}	else if (ItemDefinition.forId(itemToSmith.getId()).getName().contains("Adamant")) {
					player.getSkillManager().addExperience(Skill.SMITHING, (int) (Bars.Adamant.getExp() * bar.getAmount()));
					//player.getPacketSender().sendMessage("Using: "+bar.getAmount()+" Adamant bars to make: "+ItemDefinition.forId(itemToSmith.getId()).getName());
				}	else if (ItemDefinition.forId(itemToSmith.getId()).getName().contains("Rune") || ItemDefinition.forId(itemToSmith.getId()).getName().contains("Runite")) {
					player.getSkillManager().addExperience(Skill.SMITHING, (int) (Bars.Rune.getExp() * bar.getAmount()));
					//player.getPacketSender().sendMessage("Using: "+bar.getAmount()+" Rune bars to make: "+ItemDefinition.forId(itemToSmith.getId()).getName());
				}	else {
					player.getPacketSender().sendMessage("ERROR 95152, no experience added. Please report this to staff!");
				}
				//player.getSkillManager().addExperience(Skill.SMITHING, (int) (Bars.Bronze.getExp() * bar.getAmount()));
				//player.getSkillManager().addExperience(Skill.SMITHING, (int) (SmithingData.getData(itemToSmith, "xp")));
			}
		});
		TaskManager.submit(player.getCurrentTask());
	}
}
