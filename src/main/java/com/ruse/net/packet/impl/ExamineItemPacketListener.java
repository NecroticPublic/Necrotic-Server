package com.ruse.net.packet.impl;

import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.world.content.skill.impl.herblore.FinishedPotions;
import com.ruse.world.entity.impl.player.Player;

public class ExamineItemPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int item = packet.readShort();
		if(item == 995 || item == 18201) {
			player.getPacketSender().sendMessage("Mhmm... Shining coins...");
			return;
		}
		
		if (ItemDefinition.forId(item) != null && ItemDefinition.forId(item).getName() != null && ItemDefinition.forId(item).getName().toLowerCase().contains("(unf)")) {
			for (int i = 0; i < FinishedPotions.values().length; i++) {
				if (item == FinishedPotions.values()[i].getUnfinishedPotion()) {
					player.getPacketSender().sendMessage("Finish this potion with a "+ItemDefinition.forId(FinishedPotions.values()[i].getItemNeeded()).getName()+".");
					return;
				}
			}
		}
		if (item == 12926 || item == 12934) {
			ItemDefinition itemDef = ItemDefinition.forId(item);
			if(itemDef != null) {
				player.getPacketSender().sendMessage("@gre@<shad=0>You currently have "+Misc.format(player.getBlowpipeCharges())+" Zulrah scales stored.");
			}
		}
		ItemDefinition itemDef = ItemDefinition.forId(item);
		if(itemDef != null) {
			player.getPacketSender().sendMessage(itemDef.getDescription());
			for (Skill skill : Skill.values()) {
				if (itemDef.getRequirement()[skill.ordinal()] > player.getSkillManager().getMaxLevel(skill)) {
					player.getPacketSender().sendMessage("@red@Attention: You need " + new StringBuilder().append(skill.getName().startsWith("a") || skill.getName().startsWith("e") || skill.getName().startsWith("i") || skill.getName().startsWith("o") || skill.getName().startsWith("u") ? "an " : "a ").toString() + Misc.formatText(skill.getName()) + " level of at least " + itemDef.getRequirement()[skill.ordinal()] + " to wear this.");
				}
			}
		}
	}

}
