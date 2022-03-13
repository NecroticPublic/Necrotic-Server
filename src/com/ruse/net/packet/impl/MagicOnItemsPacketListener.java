package com.ruse.net.packet.impl;

import com.ruse.GameSettings;
import com.ruse.ReducedSellPrice;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.GraphicHeight;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.Skill;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.world.content.combat.magic.MagicSpells;
import com.ruse.world.content.combat.magic.Spell;
import com.ruse.world.content.skill.impl.dungeoneering.UltimateIronmanHandler;
import com.ruse.world.content.skill.impl.magic.Magic;
import com.ruse.world.content.skill.impl.smithing.Smelting;
import com.ruse.world.entity.impl.player.Player;

/**
 * Handles magic on items. 
 * @author Gabriel Hannason
 */
public class MagicOnItemsPacketListener implements PacketListener {

	@SuppressWarnings("unused")
	@Override
	public void handleMessage(Player player, Packet packet) {
		if(packet.getOpcode() == MAGIC_ON_GROUNDITEMS) {
			final int itemY = packet.readLEShort();
			final int itemId = packet.readShort();
			final int itemX = packet.readLEShort();
			final int spellId = packet.readUnsignedShortA();
			final MagicSpells spell = MagicSpells.forSpellId(spellId);
			if(spell == null)
				return;
			if (player != null && player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("Used spell id: "+spellId+" on grounditem: "+itemId+" on XY: "+itemX+", "+itemY);
			}
			if (UltimateIronmanHandler.hasItemsStored(player) && player.getLocation() != Location.DUNGEONEERING) {
				player.getPacketSender().sendMessage("<shad=0>@red@You cannot use this spell until you claim your stored Dungeoneering items.");
				return;
			}
			player.getMovementQueue().reset();
			//switch(spell) {}
		} else if(packet.getOpcode() == MAGIC_ON_ITEMS) {
			int slot = packet.readShort();
			int itemId = packet.readShortA();
			int childId = packet.readShort();
			int spellId = packet.readShortA();
			
			boolean lowAlch = false;
			
			if(!player.getClickDelay().elapsed(1300))
				return;
			if(slot < 0 || slot > player.getInventory().capacity())
				return;
			if(player.getInventory().getItems()[slot].getId() != itemId)
				return;
			Item item = new Item(itemId);
			MagicSpells magicSpell = MagicSpells.forSpellId(spellId);
			if(magicSpell == null)
				return;
			Spell spell = magicSpell.getSpell();
			if (player != null && player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("Used spell id: "+spellId+" on item: "+itemId);
			}
			if (UltimateIronmanHandler.hasItemsStored(player) && player.getLocation() != Location.DUNGEONEERING) {
				player.getPacketSender().sendMessage("<shad=0>@red@You cannot use this spell until you claim your stored Dungeoneering items.");
				return;
			}
			switch(magicSpell) {
			case ENCHANT_SAPPHIRE:
			case ENCHANT_EMERALD:
			case ENCHANT_RUBY:
			case ENCHANT_DIAMOND:
			case ENCHANT_DRAGONSTONE:
			case ENCHANT_ONYX:
				Magic.enchantItem(player, itemId, spellId);
				break;
			case LOW_ALCHEMY:
				lowAlch = true;
			case HIGH_ALCHEMY:
				if(item.getId() != 22053 && (!item.tradeable() || !item.sellable() || item.getId() == 995)) {
					player.getPacketSender().sendMessage("This spell can not be cast on this item.");
					return;
				}
				if(spell == null || !spell.canCast(player, true))
					return;
				player.getInventory().delete(itemId, 1);
				int value = 0;
				if (item.reducedPrice()) {
					value = (int)(ReducedSellPrice.forId(item.getId()).getSellValue() * (lowAlch ? 0.6 : 0.75));
				} else {
					value = (int)(item.getDefinition().getValue() * (lowAlch ? 0.6 : 0.75));
				}
				player.getInventory().add(995, value);
				player.performAnimation(new Animation(712));
				player.performGraphic(new Graphic(magicSpell == MagicSpells.HIGH_ALCHEMY ? 113 : 112, GraphicHeight.LOW));
				player.getSkillManager().addExperience(Skill.MAGIC, spell.baseExperience());
				player.getPacketSender().sendTab(GameSettings.MAGIC_TAB);
				//player.getPacketSender().sendMessage("Alching is currently disabled.");
				break;
			case SUPERHEAT_ITEM:
				for(int i = 0; i < ORE_DATA.length; i++) {
					if(item.getId() == ORE_DATA[i][0]) {
						if(player.getInventory().getAmount(ORE_DATA[i][2]) < ORE_DATA[i][3]) {
							player.getPacketSender().sendMessage("You do not have enough "+new Item(ORE_DATA[i][2]).getDefinition().getName()+"s for this spell.");
							return;
						}
						if(spell == null || !spell.canCast(player, true))
							return;
						player.getInventory().delete(item.getId(), 1);
						for(int k = 0; k < ORE_DATA[i][3]; k++)
							player.getInventory().delete(ORE_DATA[i][2], 1);
						player.performAnimation(new Animation(725));
						player.performGraphic(new Graphic(148, GraphicHeight.HIGH));
						player.getInventory().add(ORE_DATA[i][4], 1);
						player.getPacketSender().sendTab(GameSettings.MAGIC_TAB);
						player.getSkillManager().addExperience(Skill.MAGIC, spell.baseExperience());
						player.getSkillManager().addExperience(Skill.SMITHING, Smelting.getExperience(ORE_DATA[i][4]));
						return;
					}		
				}
				player.getPacketSender().sendMessage("This spell can only be cast on Mining ores.");
				break;
			case BAKE_PIE:
				if (itemId == 2317 || itemId == 2319 || itemId == 2321) {
					player.getSkillManager().addExperience(Skill.MAGIC, spell.baseExperience());
					player.performAnimation(new Animation(4413));
					player.performGraphic(new Graphic(746, GraphicHeight.HIGH));
					player.getInventory().delete(item.getId(), 1);
					player.getPacketSender().sendMessage("You bake the pie");
					player.getInventory().add(itemId == 2317 ? 2323 : itemId == 2319 ? 2327 : itemId == 2321 ? 2325 : -1, 1);
				} else
					player.getPacketSender().sendMessage("This spell can only be cast on an uncooked pie.");
				break;
			default:
				break;
			}
			player.getClickDelay().reset();
			player.getInventory().refreshItems();
		}
	}

	final static int[][] ORE_DATA = {
		{436, 1, 438, 1, 2349, 53}, // TIN
		{438, 1, 436, 1, 2349, 53}, // COPPER
		{440, 1, -1, -1, 2351, 53}, // IRON ORE
		{442, 1, -1, -1, 2355, 53}, // SILVER ORE
		{444, 1, -1, -1, 2357, 23}, // GOLD BAR
		{447, 1, 453, 4, 2359, 30}, // MITHRIL ORE
		{449, 1, 453, 6, 2361, 38}, // ADDY ORE
		{451, 1, 453, 8, 2363, 50}, // RUNE ORE
	};

	public static final int MAGIC_ON_GROUNDITEMS = 181;
	public static final int MAGIC_ON_ITEMS = 237;
}
