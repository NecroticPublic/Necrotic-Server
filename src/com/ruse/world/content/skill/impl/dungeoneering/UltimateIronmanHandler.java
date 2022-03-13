package com.ruse.world.content.skill.impl.dungeoneering;

import com.ruse.model.Flag;
import com.ruse.model.Item;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.WeaponInterfaces;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.entity.impl.player.Player;

public class UltimateIronmanHandler {
	
	public static boolean hasItemsStored(Player player) {
		if (player.getDungeoneeringIronEquipment().getValidItems().size() + player.getDungeoneeringIronInventory().getValidItems().size() == 0) {
			return false;
		}
		return true;
	}
	
	public static void handleQuickStore(Player player) {
		if (player.busy()) {
			player.getPacketSender().sendMessage("You are far too busy to do that.");
			return;
		}
		if (!player.getClickDelay().elapsed(100)) {
			return;
		}
		if (player.getDungeoneeringIronEquipment().getValidItems().size() > 0 || player.getDungeoneeringIronInventory().getValidItems().size() > 0) {
			player.getPacketSender().sendMessage("You must claim your gear first.");
			return;
		}
		/*if (player.getEquipment().getValidItems().size() > 0 || player.getInventory().getValidItems().size() > 0) {
			player.getPacketSender().sendMessage("You cannot be wearing any gear, or have anything in your inventory.");
		}*/ 
		
		/**
		 * @TODO put this on other check, but check against valid items already stored.
		 */
		
		int items = player.getInventory().getValidItems().size();
		if (player.getInventory().contains(15707)) {
			items -= 1;
		}
		int equip = player.getEquipment().getValidItems().size();
		
		if (items+equip <= 0) {
			player.getPacketSender().sendMessage("<shad=0>@gre@Found no valid items for storage.");
			return;
		}
		
		for (Item i : player.getInventory().getValidItems()) {
			if (i.getId() == 15707) {
				continue;
			}
			player.getDungeoneeringIronInventory().add(i);
			PlayerLogs.log(player.getUsername(), "Stored "+i.getAmount()+"x "+i.getDefinition().getName()+" in dung HCIM inv");
			player.getInventory().delete(i);
			//System.out.println("Added "+i.getAmount()+"x "+i.getDefinition().getName() +" to inventory list.");
		}
		
		for (Item i : player.getEquipment().getValidItems()) {
			player.getDungeoneeringIronEquipment().add(i);
			//System.out.println("Added "+i.getAmount()+"x "+i.getDefinition().getName() +" to equipment list.");
			PlayerLogs.log(player.getUsername(), "Stored "+i.getAmount()+"x "+i.getDefinition().getName()+" in dung HCIM equip");
			player.getEquipment().delete(i);
		}
		
		player.getPacketSender().sendMessage("<shad=0>@gre@Successfully stored "+equip+" equipment, and "+items+ (items > 1 ? " items" : " item")+". Total: "+(equip+items)+"/39.");
		
		Item weapon = player.getEquipment().get(Equipment.WEAPON_SLOT);
		WeaponInterfaces.assign(player, weapon);
		
		player.getInventory().refreshItems();
		player.getEquipment().refreshItems();
		player.getUpdateFlag().flag(Flag.APPEARANCE);
		
		player.save();
		
		
		
		/*System.out.println("--------------------------------------");
		
		for (Item i : player.getDungeoneeringIronInventory().getValidItems()) {
			System.out.println("Found "+i.getAmount()+"x "+i.getDefinition().getName() +" in IronInventory");
		}
		
		for (Item i : player.getDungeoneeringIronEquipment().getValidItems()) {
			System.out.println("Found "+i.getAmount()+"x "+i.getDefinition().getName() +" in IronEquipment");
		}*/
		
	}
	
	public static void handleQuickRetrieve(Player player) {
		if (player.busy()) {
			player.getPacketSender().sendMessage("You are far too busy to do that.");
			return;
		}
		if (!player.getClickDelay().elapsed(100)) {
			player.getPacketSender().sendMessage("You're interacting too fast.");
			return;
		}
		if (player.getEquipment().getValidItems().size() > 0 && player.getDungeoneeringIronEquipment().getValidItems().size() > 0) {
			player.getPacketSender().sendMessage("You must not be wearing anything to claim your equipment.");
			return;
		}
		if (player.getInventory().getFreeSlots() < player.getDungeoneeringIronInventory().getValidItems().size()) {
			player.getPacketSender().sendMessage("You must have at least "+player.getDungeoneeringIronInventory().getValidItems().size()+" free inventory slots first.");
			return;
		}
		
		int total = player.getDungeoneeringIronEquipment().getValidItems().size() + player.getDungeoneeringIronInventory().getValidItems().size();
		if (total <= player.getInventory().getFreeSlots() && total > 0) {
			int equip = player.getDungeoneeringIronEquipment().getValidItems().size();
			int leftover = player.getDungeoneeringIronInventory().getValidItems().size();
			for (Item i : player.getDungeoneeringIronEquipment().getValidItems()) {
				player.getInventory().add(i);
				PlayerLogs.log(player.getUsername(), "Retrieved "+i.getAmount()+"x "+i.getDefinition().getName()+" in dung HCIM equip");
				player.getDungeoneeringIronEquipment().delete(i);
			}
			for (Item i : player.getDungeoneeringIronInventory().getValidItems()) {
				player.getInventory().add(i);
				PlayerLogs.log(player.getUsername(), "Retrieved "+i.getAmount()+"x "+i.getDefinition().getName()+" in dung HCIM inv");
				player.getDungeoneeringIronInventory().delete(i);
			}
			player.getPacketSender().sendMessage("<shad=0>@gre@Your final "+ (equip+leftover > 1 ? equip+leftover+" items are " : "item is ") + "returned to you.");
			
		} else if (player.getDungeoneeringIronEquipment().getValidItems().size() > 0) {
			int equip = player.getDungeoneeringIronEquipment().getValidItems().size();
			int leftover = player.getDungeoneeringIronInventory().getValidItems().size();
			for (Item i : player.getDungeoneeringIronEquipment().getValidItems()) {
				player.getInventory().add(i);
				PlayerLogs.log(player.getUsername(), "Retrieved "+i.getAmount()+"x "+i.getDefinition().getName()+" in dung HCIM equip");
				player.getDungeoneeringIronEquipment().delete(i);
			}
			if (player.getDungeoneeringIronInventory().getValidItems().size() > 0) {
				player.getPacketSender().sendMessage("<shad=0>@gre@You retrieve "+equip+" equipment, and have "+ (leftover > 1 ? leftover+" items" : "item")+" remaining.");
			} else {
				player.getPacketSender().sendMessage("<shad=0>@gre@Your worn equipment is returned to you.");
			}
		} else if (player.getDungeoneeringIronInventory().getValidItems().size() > 0){

			int inv = player.getDungeoneeringIronInventory().getValidItems().size();
			for (Item i : player.getDungeoneeringIronInventory().getValidItems()) {
				player.getInventory().add(i);
				PlayerLogs.log(player.getUsername(), "Retrieved "+i.getAmount()+"x "+i.getDefinition().getName()+" in dung HCIM inv");
				player.getDungeoneeringIronInventory().delete(i);
			}
			player.getPacketSender().sendMessage("<shad=0>@gre@Your final "+ (inv > 1 ? inv+" items are " : "item is ") + "returned to you.");
		
		} else {
			player.getPacketSender().sendMessage("<shad=0>@gre@You don't have any other items stored.");
		}
		
		Item weapon = player.getEquipment().get(Equipment.WEAPON_SLOT);
		WeaponInterfaces.assign(player, weapon);
		
		player.getInventory().refreshItems();
		player.getEquipment().refreshItems();
		player.getUpdateFlag().flag(Flag.APPEARANCE);
		
		player.save();
	}

}
