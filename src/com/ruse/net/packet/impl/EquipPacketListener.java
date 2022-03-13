package com.ruse.net.packet.impl;

import com.ruse.GameSettings;
import com.ruse.model.Flag;
import com.ruse.model.GameMode;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.PlayerRights;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.container.impl.Inventory;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.WeaponAnimations;
import com.ruse.model.definitions.WeaponInterfaces;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.BonusManager;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPunishment.Jail;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.content.combat.magic.Autocasting;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.minigames.impl.Dueling;
import com.ruse.world.content.minigames.impl.Dueling.DuelRule;
import com.ruse.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.ruse.world.entity.impl.player.Player;

/**
 * This packet listener manages the equip action a player
 * executes when wielding or equipping an item.
 * 
 * @author relex lawl
 */

public class EquipPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		int id = packet.readShort();
		int slot = packet.readShortA();
		int interfaceId = packet.readShortA();
		if(player.getInterfaceId() > 0 && player.getInterfaceId() != 21172 /* EQUIP SCREEN */) {
			player.getPacketSender().sendInterfaceRemoval();
			//return;
		}
		switch(id) {
		case 9922:
		case 9921:
		case 22036:
		case 22037:
		case 22038:
		case 22039:
		case 22040:
			int count = 0;
			String itm = "items";
			for (int i =0; i < GameSettings.hweenIds2016.length; i++) {
				if (GameSettings.hweenIds2016[i] == id && !player.getHween2016(i)) {
					player.getPacketSender().sendMessage("You've unlocked @red@"+ItemDefinition.forId(id).getName()+"<col=-1>.");
					player.setHween2016(i, true);
				}
				if (!player.getHween2016(i)) {
					count = count+1;
				}
			}
			if (count == 1) {
				itm = "item";
			}
			if (count > 0) {
				player.getPacketSender().sendMessage("<img=10> You need "+count+" more "+itm+" to finish the event.");
			} else if (!player.doneHween2016()) {
				player.getPacketSender().sendMessage("<img=10> @or1@<shad=0>Congratulations! You've completed the Halloween event!");
				player.setDoneHween2016(true);
			}
			break;
		case 15755: //novite
		case 16207:
		case 16273:
		case 15936:
		case 16262:
		//case 16383 starter:
		case 16024:
		//case 16405: starter
		case 16174:
		case 16647:
		case 16196: 
		case 15925: 
		case 15914:
		case 16713: 
		case 16080: 
		case 16889: 
		case 16127:
		//case 16935: starter
		case 16035: 
		case 17019: 
		case 16116: 
		case 16013: 
		case 17341: 
		case 15808: //novite end
		case 16208: //bathus start
		case 16275: 
		case 15937:
		case 16341:
		case 16263: 
		case 16385:
		case 16025:
		case 16407:
		case 16175:
		case 16649: 
		case 16197:
		case 16671:
		case 15926:
		case 16693:
		case 15915:
		case 16715:
		case 16081:
		case 16891:
		case 16128:
		case 16937:
		case 16036: 
		case 17021: 
		case 16117:
		case 17241:
		case 16014:
		case 17343:
		case 15809: //bathus end
		case 15757: //Marmaros start
		case 16209:
		case 16277:
		case 15938:
		case 16343:
		case 16264:
		case 16387: 
		case 16026: 
		case 16409:
		case 16176:
		case 16651:
		case 16198:
		case 16673:
		case 15927:
		case 16695:
		case 15916:
		case 16717:
		case 16082: 
		case 16893:
		case 16129:
		case 16939:
		case 16037:
		case 17023:
		case 16118:
		case 17243:
		case 16015:
		case 17345:
		case 15810://marmaros end
		case 15759: 
		case 16210: 
		case 16279: 
		case 15939: 
		case 16345: 
		case 16265: 
		case 16389: 
		case 16027: 
		case 16411: 
		case 16177: 
		case 16653: 
		case 16199: 
		case 16675: 
		case 15928: 
		case 16697: 
		case 15917: 
		case 16719: 
		case 16083: 
		case 16895: 
		case 16130: 
		case 16941: 
		case 16038: 
		case 17025: 
		case 16119: 
		case 17245: 
		case 16016: 
		case 17347: 
		case 15811: 
		case 15761: 
		case 16211: 
		case 16281: 
		case 15940: 
		case 16347: 
		case 16266: 
		case 16391: 
		case 16028: 
		case 16413: 
		case 16178: 
		case 16655: 
		case 16200: 
		case 16677: 
		case 15929: 
		case 16699: 
		case 15918: 
		case 16721: 
		case 16084: 
		case 16897: 
		case 16131: 
		case 16943: 
		case 16039: 
		case 17027: 
		case 16120: 
		case 17247: 
		case 16017: 
		case 17349: 
		case 15812: 
		case 15763: 
		case 16212: 
		case 16283: 
		case 15941: 
		case 16349: 
		case 16267: 
		case 16393: 
		case 16029: 
		case 16415: 
		case 16179: 
		case 16657: 
		case 16201: 
		case 16679: 
		case 15930: 
		case 16701: 
		case 15919: 
		case 16723: 
		case 16085: 
		case 16899: 
		case 16132: 
		case 16945: 
		case 16040: 
		case 17029: 
		case 16121: 
		case 17249: 
		case 16018: 
		case 17351: 
		case 15813: 
		case 15765: 
		case 16213: 
		case 16285: 
		case 15942: 
		case 16351: 
		case 16268: 
		case 16395: 
		case 16030: 
		case 16417: 
		case 16180: 
		case 16659: 
		case 16202: 
		case 16681: 
		case 15931: 
		case 16703: 
		case 15920: 
		case 16725: 
		case 16086: 
		case 16901: 
		case 16133: 
		case 16947: 
		case 16041: 
		case 17031: 
		case 16122: 
		case 17251: 
		case 16019: 
		case 17353: 
		case 15814: 
		case 15767: 
		case 16214: 
		case 16287: 
		case 15943: 
		case 16353: 
		case 16269: 
		case 16397: 
		case 16031: 
		case 16419: 
		case 16181: 
		case 16661: 
		case 16203: 
		case 16683: 
		case 15932: 
		case 16705: 
		case 15921: 
		case 16727: 
		case 16087: 
		case 16903: 
		case 16134: 
		case 16949: 
		case 16042: 
		case 17033: 
		case 16123: 
		case 17253: 
		case 16020: 
		case 17355: 
		case 15815: 
		case 15769: 
		case 16215: 
		case 16289: 
		case 15944: 
		case 16355: 
		case 16270: 
		case 16399: 
		case 16032: 
		case 16421: 
		case 16182: 
		case 16663: 
		case 16204: 
		case 16685: 
		case 15933: 
		case 16707: 
		case 15922: 
		case 16729: 
		case 16088: 
		case 16905: 
		case 16135: 
		case 16951: 
		case 16043: 
		case 17035: 
		case 16124: 
		case 17255: 
		case 16021: 
		case 17357: 
		case 15816: 
		case 15771: 
		case 16216: 
		case 16291: 
		case 15945: 
		case 16357: 
		case 16271: 
		case 16401: 
		case 16033: 
		case 16423: 
		case 16183: 
		case 16665: 
		case 16205: 
		case 16687: 
		case 15934: 
		case 16709: 
		case 15923: 
		case 16731: 
		case 16089: 
		case 16907: 
		case 16136: 
		case 16953: 
		case 16044: 
		case 17037: 
		case 16125: 
		case 17257: 
		case 16022: 
		case 17359: 
		case 15817: 
		case 15773: 
		case 16217: 
		case 16293: 
		case 15946: 
		case 16359: 
		case 16272: 
		case 16403: 
		case 16034: 
		case 16425: 
		case 16184: 
		case 16667: 
		case 16206: 
		case 16689: 
		case 15935: 
		case 16711: 
		case 15924: 
		case 16733: 
		case 16090: 
		case 16909: 
		case 16137: 
		case 16955: 
		case 16045: 
		case 17039: 
		case 16126: 
		case 17259: 
		case 16023: 
		case 17361: 
		case 15818:
			if(!(player.getRights().OwnerDeveloperOnly()) && player.getLocation() != Location.DUNGEONEERING && !Dungeoneering.doingDungeoneering(player)){
				player.getInventory().delete(id, 1);
				player.getPacketSender().sendMessage("You aren't supposed to have that.");
				World.sendStaffMessage(player.getUsername()+" has tried to equip a "+ItemDefinition.forId(id).getName()+" outside of dungeoneering."); //TODO FIX THIS
				PlayerLogs.log("1 - Item smuggling", player.getUsername()+" tried to equip a: "+ItemDefinition.forId(id).getName());
				break;
			} else {
				System.out.println(player.getUsername()+" equipped a "+ItemDefinition.forId(id).getName());
			}
			break;
		case 13632:
			DialogueManager.sendStatement(player, "<img=10>The staff has UNLIMITED Ice Burst runes!");
			player.getPacketSender().sendMessage("<img=10>@gre@<shad=0>The staff has UNLIMITED Ice Burst runes!");
			break;
		case 13634:
			DialogueManager.sendStatement(player, "<img=10>The staff has UNLIMITED Blood Burst runes!");
			player.getPacketSender().sendMessage("<img=10>@gre@<shad=0>The staff has UNLIMITED Blood Burst runes!");
			break;
		case 22010:
			if (!player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("lol gtfo");
				player.getInventory().delete(id, 1);
				World.sendStaffMessage(player.getUsername()+" just tried to equip a Ginrei... Jailed, removed. Still has? "+ player.getInventory().contains(22010) + " "+player.getBank(0).contains(22010));
				Jail.jailPlayer(player);
			}
			break;
		case 15835:
		case 17293:
			if(!(player.getRights().OwnerDeveloperOnly()) && player.getLocation() != Location.DUNGEONEERING && !Dungeoneering.doingDungeoneering(player)){
				player.getInventory().delete(id, 1);
				player.getPacketSender().sendMessage("You aren't supposed to have that.");
				World.sendStaffMessage("@red@[BUG] "+player.getUsername()+" just tried to equip a Doomcore Staff out of dung!");
				return;
			} else {
				DialogueManager.sendStatement(player, "<img=10>The Doomcore Staff has UNLIMITED runes!");
				player.getPacketSender().sendMessage("<img=10><shad=0> @gre@The Doomcore Staff @bla@has UNLIMITED runes!");
			}
			break;
		case 773:
			if(!(player.getRights().equals(PlayerRights.OWNER) || player.getRights().equals(PlayerRights.DEVELOPER))) {
				player.getPacketSender().sendMessage("We wants it, we needs it. Must have the precious. They stole it from us.").sendMessage("Sneaky little hobbitses. Wicked, tricksy, false! The ring has vanished again..");
				player.getInventory().delete(id, 1);
			break;
			}
			else {
				player.getPacketSender().sendMessage("Precious, precious, precious! My Precious! O my Precious!");
				World.sendStaffMessage("@red@[BUG] "+player.getUsername()+" just tried to equip a Perfect Ring!");
			}
			break;
		case 20171:
			boolean haveAmmo = player.getEquipment().get(Equipment.AMMUNITION_SLOT).getId() == -1;
			boolean spiritArrow = player.getEquipment().get(Equipment.AMMUNITION_SLOT).getId() == 78;
			if(!haveAmmo) {
				if (spiritArrow) {
					player.getPacketSender().sendMessage("Your bow connects to your Soul arrow.");
				} else {
					player.getPacketSender().sendMessage("Please un-equip your arrows before using the Zaryte bow.");
					//player.getPacketSender().sendMessage("Your ammo is: "+player.getEquipment().get(Equipment.AMMUNITION_SLOT));
				return;
				}
			} else {
				player.getEquipment().set(Equipment.AMMUNITION_SLOT, new Item (78, 1));
				player.getPacketSender().sendMessage("Your Zaryte bow activates it's unique arrow.");
			}
			player.getPacketSender().sendMessage("<img=10> The Zaryte bow WILL NOT work in PvP fights!");
			break;
		case 78:
			boolean zaryte = player.getEquipment().get(Equipment.WEAPON_SLOT).getId() == 20171;
			boolean hasArrow = player.getEquipment().get(Equipment.AMMUNITION_SLOT).getId() == 78;
			boolean noWep = player.getEquipment().get(Equipment.WEAPON_SLOT).getId() == -1;
			if (noWep) {
				player.getPacketSender().sendMessage("Soul arrows are not toys.");
				player.getInventory().delete(78, 1);
				return;
			}
			if (zaryte) {
				if (hasArrow) {
					player.getPacketSender().sendMessage("You already have a link to another Soul arrow.");
					player.getInventory().delete(78, 1);
					return;
				} else {
					player.getPacketSender().sendMessage("You connect the Soul arrow to your Zaryte bow.");
				}
			} else {
				player.getPacketSender().sendMessage("A toy like that could never use a Soul arrow.");
				player.getInventory().delete(78, 1);
				return;
			}
			break;
		case 16691: //iron men items
		case 9704:
		case 17239:
		case 16669:
		case 16339:
		case 6068:
		case 9703:
			if(player.getGameMode() != GameMode.IRONMAN && player.getLocation() != Location.DUNGEONEERING){
				player.getPacketSender().sendMessage("You must be in Iron Man mode to use this.");
				return;
			}
			break;
		}
		switch (interfaceId) {
		case Inventory.INTERFACE_ID:
			/*
			 * Making sure slot is valid.
			 */
			if (slot >= 0 && slot <= 28) {
				Item item = player.getInventory().getItems()[slot].copy();
				if(!player.getInventory().contains(item.getId()))
					return;
				/*
				 * Making sure item exists and that id is consistent.
				 */
				if (item != null && id == item.getId()) {
					for (Skill skill : Skill.values()) {
						if(skill == Skill.CONSTRUCTION)
							continue;
						if (item.getDefinition().getRequirement()[skill.ordinal()] > player.getSkillManager().getMaxLevel(skill)) {
							StringBuilder vowel = new StringBuilder();
							if (skill.getName().startsWith("a") || skill.getName().startsWith("e") || skill.getName().startsWith("i") || skill.getName().startsWith("o") || skill.getName().startsWith("u")) {
								vowel.append("an ");
							} else {
								vowel.append("a ");
							}
							player.getPacketSender().sendMessage("You need " + vowel.toString() + Misc.formatText(skill.getName()) + " level of at least " + item.getDefinition().getRequirement()[skill.ordinal()] + " to wear this.");
							return;
						}
					}
					int equipmentSlot = item.getDefinition().getEquipmentSlot();
					Item equipItem = player.getEquipment().forSlot(equipmentSlot).copy();
					if(player.getLocation() == Location.DUEL_ARENA) {
						for(int i = 10; i < player.getDueling().selectedDuelRules.length; i++) {
							if(player.getDueling().selectedDuelRules[i]) {
								DuelRule duelRule = DuelRule.forId(i);
								if(equipmentSlot == duelRule.getEquipmentSlot() || duelRule == Dueling.DuelRule.NO_SHIELD && item.getDefinition().isTwoHanded()) {
									player.getPacketSender().sendMessage("The rules that were set do not allow this item to be equipped.");
									return;
								}
							}
						}
						if(player.getDueling().selectedDuelRules[DuelRule.LOCK_WEAPON.ordinal()]) {
							if(equipmentSlot == Equipment.WEAPON_SLOT || item.getDefinition().isTwoHanded()) {
								player.getPacketSender().sendMessage("Weapons have been locked during this duel!");
								return;
							}
						}
					}
					if (player.hasStaffOfLightEffect() && equipItem.getDefinition().getName().toLowerCase().contains("staff of light")) {
						player.setStaffOfLightEffect(-1);
						player.getPacketSender().sendMessage("You feel the spirit of the Staff of Light begin to fade away...");
					
					}
					if (equipItem.getDefinition().isStackable() && equipItem.getId() == item.getId()) {
						int amount = equipItem.getAmount() + item.getAmount() <= Integer.MAX_VALUE ? equipItem.getAmount() + item.getAmount() : Integer.MAX_VALUE;
						player.getInventory().delete(item);
						player.getEquipment().getItems()[equipmentSlot].setAmount(amount);
						equipItem.setAmount(amount);
						player.getEquipment().refreshItems();
					} else {
		                if (item.getDefinition().isTwoHanded() && item.getDefinition().getEquipmentSlot() == Equipment.WEAPON_SLOT) {
		                    int slotsRequired = player.getEquipment().isSlotOccupied(Equipment.SHIELD_SLOT) && player.getEquipment().isSlotOccupied(Equipment.WEAPON_SLOT) ? 1 : 0;
		                    if (player.getInventory().getFreeSlots() < slotsRequired) {
		                        player.getInventory().full();
		                        return;
		                    }

		                    Item shield = player.getEquipment().getItems()[Equipment.SHIELD_SLOT];
		                    Item weapon = player.getEquipment().getItems()[Equipment.WEAPON_SLOT];

		                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(-1, 0));
		                    player.getInventory().delete(item);
		                    player.getEquipment().set(equipmentSlot, item);

		                    if (shield.getId() != -1) {
		                        player.getInventory().add(shield);
		                    }

		                    if (weapon.getId() != -1) {
		                        player.getInventory().add(weapon);
		                    }
		                } else if (equipmentSlot == Equipment.SHIELD_SLOT && player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getDefinition().isTwoHanded()) { //
							player.getInventory().setItem(slot, player.getEquipment().getItems()[Equipment.WEAPON_SLOT]);
							player.getEquipment().setItem(Equipment.WEAPON_SLOT, new Item(-1));
							player.getEquipment().setItem(Equipment.SHIELD_SLOT, item);
							resetWeapon(player);
						} else {
							if (item.getDefinition().getEquipmentSlot() == equipItem.getDefinition().getEquipmentSlot() && equipItem.getId() != -1) {
								if(player.getInventory().contains(equipItem.getId())) {
									player.getInventory().delete(item);
									player.getInventory().add(equipItem);
								} else
									player.getInventory().setItem(slot, equipItem);
								player.getEquipment().setItem(equipmentSlot, item);
							} else {
								player.getInventory().setItem(slot, new Item(-1, 0));
								player.getEquipment().setItem(item.getDefinition().getEquipmentSlot(), item);
							}
						}
					}
					if (equipmentSlot == Equipment.WEAPON_SLOT) {
						resetWeapon(player);
					} else if((equipmentSlot == Equipment.RING_SLOT && item.getId() == 2570) || (equipmentSlot == Equipment.CAPE_SLOT && player.getSkillManager().skillCape(Skill.DEFENCE))) {
						player.getPacketSender().sendMessage("<img=10> <col=996633>Warning! The Ring of Life special effect does not work in the Wilderness or").sendMessage("<col=996633> Duel Arena.");
					}

					if(player.getEquipment().get(Equipment.WEAPON_SLOT).getId() != 4153) {
						player.getCombatBuilder().cooldown(false);
					}

					player.setCastSpell(null);
					BonusManager.update(player);
					player.getEquipment().refreshItems();
					player.getInventory().refreshItems();
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					Sounds.sendSound(player, Sound.EQUIP_ITEM);
				}
			}
			break;
		}
	}

	public static void resetWeapon(Player player) {
		Item weapon = player.getEquipment().get(Equipment.WEAPON_SLOT);
		WeaponInterfaces.assign(player, weapon);
		WeaponAnimations.update(player);
		if(player.getAutocastSpell() != null || player.isAutocast()) {
			Autocasting.resetAutocast(player, true);
			player.getPacketSender().sendMessage("Autocast spell cleared.");
		}
		player.setSpecialActivated(false);
		CombatSpecial.updateBar(player);
	}
	

	public static final int OPCODE = 41;
}