package com.ruse.net.packet.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.WalkToTask;
import com.ruse.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.ruse.model.Animation;
import com.ruse.model.GameMode;
import com.ruse.model.GameObject;
import com.ruse.model.Graphic;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.definitions.GameObjectDefinition;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.clip.region.RegionClipping;
import com.ruse.world.content.CrystalChest;
import com.ruse.world.content.ItemForging;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.cluescrolls.CLUESCROLL;
import com.ruse.world.content.combat.range.ToxicBlowpipe;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.impl.DungPartyInvitation;
import com.ruse.world.content.holidayevents.christmas2016;
import com.ruse.world.content.minigames.impl.WarriorsGuild;
import com.ruse.world.content.skill.impl.cooking.Cooking;
import com.ruse.world.content.skill.impl.cooking.CookingData;
import com.ruse.world.content.skill.impl.crafting.Flax;
import com.ruse.world.content.skill.impl.crafting.Gems;
import com.ruse.world.content.skill.impl.crafting.Jewelry;
import com.ruse.world.content.skill.impl.crafting.LeatherMaking;
import com.ruse.world.content.skill.impl.firemaking.Firelighter;
import com.ruse.world.content.skill.impl.firemaking.Firemaking;
import com.ruse.world.content.skill.impl.firemaking.Logdata.logData;
import com.ruse.world.content.skill.impl.fletching.BoltData;
import com.ruse.world.content.skill.impl.fletching.Fletching;
import com.ruse.world.content.skill.impl.herblore.Crushing;
import com.ruse.world.content.skill.impl.herblore.Herblore;
import com.ruse.world.content.skill.impl.herblore.PotionCombinating;
import com.ruse.world.content.skill.impl.herblore.WeaponPoison;
import com.ruse.world.content.skill.impl.prayer.BonesOnAltar;
import com.ruse.world.content.skill.impl.prayer.Prayer;
import com.ruse.world.content.skill.impl.slayer.SlayerDialogues;
import com.ruse.world.content.skill.impl.slayer.SlayerTasks;
import com.ruse.world.content.skill.impl.smithing.EquipmentMaking;
import com.ruse.world.content.skill.impl.smithing.Smelting;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * This packet listener is called when a player 'uses' an item on another
 * entity.
 * 
 * @author relex lawl
 */

public class UseItemPacketListener implements PacketListener {

	/**
	 * The PacketListener logger to debug information and print out errors.
	 */
	// private final static Logger logger =
	// Logger.getLogger(UseItemPacketListener.class);

	private static void useItem(Player player, Packet packet) {
		if (player.isTeleporting() || player.getConstitution() <= 0)
			return;
		int interfaceId = packet.readLEShortA();
		int slot = packet.readShortA();
		int id = packet.readLEShort();
	}

	private static void itemOnItem(Player player, Packet packet) {
		int usedWithSlot = packet.readUnsignedShort();
		int itemUsedSlot = packet.readUnsignedShortA();
		if (usedWithSlot < 0 || itemUsedSlot < 0
				|| itemUsedSlot > player.getInventory().capacity()
				|| usedWithSlot > player.getInventory().capacity())
			return;
		Item usedWith = player.getInventory().getItems()[usedWithSlot];
		Item itemUsedWith = player.getInventory().getItems()[itemUsedSlot];
		if (player.getRights() == PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage("ItemOnItem - <shad=000000><col=ffffff>[<col=ff774a>"+ItemDefinition.forId(itemUsedWith.getId()).getName()+":"+itemUsedWith.getId()+":"+itemUsedWith.getAmount()+" <col=ffffff>was used on <col=4AD2FF>"+ItemDefinition.forId(usedWith.getId()).getName()+":"+usedWith.getId()+":"+usedWith.getAmount()+"<col=ffffff>]");
		}
		if (usedWith.getId() == 9003 && itemUsedWith.getId() == 989) {
			CrystalChest.sendRewardInterface(player);
			return;
		}
		
		/* Clue Handler */
		if (itemUsedWith.getId() == 9003 && (usedWith.getId() == 2724 || ItemDefinition.forId(usedWith.getId()).getName().contains("Clue"))) {
			CLUESCROLL.sendDropTableInterface(player);
		}
		
		for (int i = 0; i < Firelighter.values().length; i++) {
			if (usedWith.getId() == Firelighter.values()[i].getLighterId() || itemUsedWith.getId() == Firelighter.values()[i].getLighterId()) {
				Firelighter.handleFirelighter(player, i);
				break;
			}
		}

		for (int i = 0; i < Crushing.values().length; i++) {
			if (usedWith.getId() == Crushing.values()[i].getInput() || itemUsedWith.getId() == Crushing.values()[i].getInput()) {
				Crushing.handleCrushing(player, i);
				break;
			}
		}

		for (int i = 0; i < BoltData.values().length; i++) {
			if (usedWith.getId() == BoltData.values()[i].getTip() || itemUsedWith.getId() == BoltData.values()[i].getTip()) {
				Fletching.tipBolt(player, BoltData.values()[i].getTip());
				break;
			}
		}
		WeaponPoison.execute(player, itemUsedWith.getId(), usedWith.getId());
		if (itemUsedWith.getId() == 590 || usedWith.getId() == 590)
			Firemaking.lightFire(player, itemUsedWith.getId() == 590 ? usedWith.getId() : itemUsedWith.getId(), false, 1);
		if (itemUsedWith.getDefinition().getName().contains("(") && usedWith.getDefinition().getName().contains("("))
			PotionCombinating.combinePotion(player, usedWith.getId(), itemUsedWith.getId());
		if (usedWith.getId() == Herblore.VIAL || itemUsedWith.getId() == Herblore.VIAL){
			if (Herblore.makeUnfinishedPotion(player, usedWith.getId()) || Herblore.makeUnfinishedPotion(player, itemUsedWith.getId()))
				return;
		}
		if (Herblore.finishPotion(player, usedWith.getId(), itemUsedWith.getId()) || Herblore.finishPotion(player, itemUsedWith.getId(), usedWith.getId()))
			return;
		if (usedWith.getId() == 12934 || itemUsedWith.getId() == 12926 || usedWith.getId() == 12926 || itemUsedWith.getId() == 12934) {
			ToxicBlowpipe.loadPipe(player);
		}
		if (usedWith.getId() == 946 || itemUsedWith.getId() == 946)
			Fletching.openSelection(player, usedWith.getId() == 946 ? itemUsedWith.getId() : usedWith.getId());
		if (usedWith.getId() == 1777 || itemUsedWith.getId() == 1777)
			Fletching.openBowStringSelection(player, usedWith.getId() == 1777 ? itemUsedWith.getId() : usedWith.getId());
		if (usedWith.getId() == 53 || itemUsedWith.getId() == 53 || usedWith.getId() == 52 || itemUsedWith.getId() == 52)
			Fletching .makeArrows(player, usedWith.getId(), itemUsedWith.getId());
		if (itemUsedWith.getId() == 1755 || usedWith.getId() == 1755)
			Gems.selectionInterface(player, usedWith.getId() == 1755 ? itemUsedWith.getId() : usedWith.getId());
		if (itemUsedWith.getId() == 1755 || usedWith.getId() == 1755)
			Fletching.openGemCrushingInterface(player, usedWith.getId() == 1755 ? itemUsedWith.getId() : usedWith.getId());
		if (usedWith.getId() == 1733 || itemUsedWith.getId() == 1733)
			LeatherMaking.craftLeatherDialogue(player, usedWith.getId(), itemUsedWith.getId());
		Herblore.handleSpecialPotion(player, itemUsedWith.getId(), usedWith.getId());
		if (itemUsedWith.getId() == 1759 || usedWith.getId() == 1759) {
			Jewelry.stringAmulet(player, itemUsedWith.getId(), usedWith.getId());
		}
		ItemForging.forgeItem(player, itemUsedWith.getId(), usedWith.getId());
	}

	@SuppressWarnings("unused")
	private static void itemOnObject(Player player, Packet packet) {
		@SuppressWarnings("unused")
		int interfaceType = packet.readShort();
		final int objectId = packet.readShort();
		final int objectY = packet.readLEShortA();
		final int itemSlot = packet.readLEShort();
		final int objectX = packet.readLEShortA();
		final int itemId = packet.readShort();
		
		if (itemSlot < 0 || itemSlot > player.getInventory().capacity())
			return;
		final Item item = player.getInventory().getItems()[itemSlot];
		if (item == null)
			return;
		final GameObject gameObject = new GameObject(objectId, new Position(
				objectX, objectY, player.getPosition().getZ()));
		if(objectId > 0 && objectId != 6 && !RegionClipping.objectExists(gameObject)) {
			//	player.getPacketSender().sendMessage("An error occured. Error code: "+id).sendMessage("Please report the error to a staff member.");
			return;
		}
		player.setInteractingObject(gameObject);
		if (player.getRights() == PlayerRights.DEVELOPER) {
			if (GameObjectDefinition.forId(gameObject.getId()) != null && GameObjectDefinition.forId(gameObject.getId()).getName() != null) {
				player.getPacketSender().sendMessage("ItemOnObject - <shad=000000><col=ffffff>[<col=ff774a>"+ItemDefinition.forId(itemId).getName()+":"+itemId+" <col=ffffff>was used on <col=4AD2FF>"+GameObjectDefinition.forId(gameObject.getId()).getName()+":"+gameObject.getId()+"<col=ffffff>]");
			} else {
				player.getPacketSender().sendMessage("ItemOnObject - <shad=000000><col=ffffff>[<col=ff774a>"+ItemDefinition.forId(itemId).getName()+":"+itemId+" <col=ffffff>was used on <col=4AD2FF>"+gameObject.getId()+"<col=ffffff>] @red@(null obj. def)");
			}
		}
		player.setWalkToTask(new WalkToTask(player, gameObject.getPosition().copy(),
				gameObject.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				if (CookingData.forFish(item.getId()) != null && CookingData.isRange(objectId)) {
					player.setPositionToFace(gameObject.getPosition());
					Cooking.selectionInterface(player, CookingData.forFish(item.getId()));
					return;
				}
				if (Prayer.isBone(itemId) && objectId == 409) {
					BonesOnAltar.openInterface(
							player, itemId);
					return;
				}
				if (player.getFarming().plant(itemId, objectId,
						objectX, objectY))
					return;
				if (player.getFarming().useItemOnPlant(itemId,
						objectX, objectY))
					return;
				if (objectId == 15621) { // Warriors guild
					// animator
					if (!WarriorsGuild.itemOnAnimator(player,
							item, gameObject))
						player.getPacketSender()
						.sendMessage(
								"Nothing interesting happens..");
					return;
				}
				if (GameObjectDefinition.forId(objectId) != null && GameObjectDefinition.forId(objectId).getName() != null && GameObjectDefinition.forId(objectId).getName().equalsIgnoreCase("furnace") && ItemDefinition.forId(itemId) != null && ItemDefinition.forId(itemId).getName() != null && ItemDefinition.forId(itemId).getName().contains("ore")) {
					Smelting.openInterface(player);
					return;
				}
				if (GameObjectDefinition.forId(objectId) != null && GameObjectDefinition.forId(objectId).getName() != null && GameObjectDefinition.forId(objectId).getName().equalsIgnoreCase("furnace") && itemId == 2357) {
					Jewelry.jewelryInterface(player);
				}
				if(player.getGameMode() == GameMode.ULTIMATE_IRONMAN) { //UIM can use any noted item on a bank booth to instantly unnote it to fill all your free inventory spaces
					if(GameObjectDefinition.forId(objectId) != null) {
						GameObjectDefinition def = GameObjectDefinition.forId(objectId);
						if(def.name != null && def.name.toLowerCase().contains("bank") && def.actions != null && def.actions[0] != null && def.actions[0].toLowerCase().contains("use")) {
							ItemDefinition def1 = ItemDefinition.forId(itemId);
							ItemDefinition def2;
							int newId = def1.isNoted() ? itemId-1 : itemId+1;
							def2 = ItemDefinition.forId(newId);
							if(def2 != null && def1.getName().equals(def2.getName())) {
								int amt = player.getInventory().getAmount(itemId);
								if(!def2.isNoted()) {
									if(amt > player.getInventory().getFreeSlots())
										amt = player.getInventory().getFreeSlots();
								}
								if(amt == 0) {
									player.getPacketSender().sendMessage("You do not have enough space in your inventory to do that.");
									return;
								}
								player.getInventory().delete(itemId, amt).add(newId, amt);
								
							} else {
								player.getPacketSender().sendMessage("You cannot do this with that item.");
							}
							return;
						}
					}
				}
				switch(objectId) {
				case 172:
				case 173:
					if (itemId == 9003) {
						CrystalChest.sendRewardInterface(player);
					}
					break;
				case -23813:
				case 41723:
					if (!christmas2016.isChristmas() || player.getChristmas2016() < 2) {
						return;
					} else {
						christmas2016.handleItemOnReindeer(player, itemId);
					}
				break;
				case 2644:
					if(itemId == 1779) {
						Flax.showSpinInterface(player);
					}
					break;
				case 6189:
				case 11666:
					Jewelry.jewelryInterface(player);
					break;
				case 7836:
				case 7808:
					if(itemId == 6055) {
						int amt = player.getInventory().getAmount(6055);
						if(amt > 0) {
							player.getInventory().delete(6055, amt);
							player.getPacketSender().sendMessage("You put the weed in the compost bin.");
							player.getSkillManager().addExperience(Skill.FARMING, 1*amt);
						}
					}
					break;
				case 4306:
					EquipmentMaking.handleAnvil(player);
					break;
				}
			}
		}));
	}

	private static void itemOnNpc(final Player player, Packet packet) {
		final int id = packet.readShortA();
		final int index = packet.readShortA();
		final int slot = packet.readLEShort();
		if(index < 0 || index > World.getNpcs().capacity()) {
			return;
		}
		if(slot < 0 || slot > player.getInventory().getItems().length) {
			return;
		}
		NPC npc = World.getNpcs().get(index);
		if(npc == null) {
			return;
		}
		if(player.getInventory().getItems()[slot].getId() != id) {
			return;
		}
		if (player.getRights() == PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage("ItemOnNPC - <shad=000000><col=ffffff>[<col=ff774a>"+ItemDefinition.forId(id).getName()+":"+id+" <col=ffffff>was used on <col=4AD2FF>"+npc.getDefinition().getName()+":"+npc.getId()+"<col=ffffff>]");
		}
		if(player.getGameMode() == GameMode.ULTIMATE_IRONMAN) { //UIM can use any noted item on a bank booth to instantly unnote it to fill all your free inventory spaces
			if(npc.getDefinition() != null) {
				NpcDefinition def = npc.getDefinition();
				if(def.getName() != null && def.getName().toLowerCase().contains("banker")) {
					ItemDefinition def1 = ItemDefinition.forId(id);
					ItemDefinition def2;
					int newId = def1.isNoted() ? id-1 : id+1;
					def2 = ItemDefinition.forId(newId);
					if(def2 != null && def1.getName().equals(def2.getName())) {
						int amt = player.getInventory().getAmount(id);
						if(!def2.isNoted()) {
							if(amt > player.getInventory().getFreeSlots())
								amt = player.getInventory().getFreeSlots();
						}
						if(amt == 0) {
							player.getPacketSender().sendMessage("You do not have enough space in your inventory to do that.");
							return;
						}
						player.getInventory().delete(id, amt).add(newId, amt);
						
					} else {
						player.getPacketSender().sendMessage("You cannot do this with that item.");
					}
					return;
				}
			}
		}
		
		switch(id) {
		case 9003:
			if (player.getLastTomed() != npc.getId()) {
				player.getPacketSender().sendMessage(NpcDefinition.forId(npc.getId()).getName()+" has been scanned to your tome.");
				if (npc.getId() == 1158) {
					player.setLastTomed(1160);
					break;
				}
			}
			player.setLastTomed(npc.getId());
			break;
		case 1907:
			if (npc.getId() == 1552 && christmas2016.isChristmas()) {
				if (player.getChristmas2016() < 5) {
					player.getPacketSender().sendMessage("I should do this after giving the Reindeer their runes.");
					return;
				} else if (player.getInventory().getAmount(1907) >= 1) {
					player.setPositionToFace(npc.getPosition());
					player.performAnimation(new Animation(4540));
					player.getInventory().delete(1907, 100);
					player.setchristmas2016(6);
					player.getPacketSender().sendMessage("You give Santa the "+ItemDefinition.forId(1907).getName()+". I should speak with him.");
				}
			
			}
			break;
		case 3550://clue
			boolean clue = CLUESCROLL.handleNpcUse(player, npc.getId());
			if (!clue) {
				player.getPacketSender().sendMessage("Nothing interesting happens.");
			} else {
				player.getPacketSender().sendMessage("You manage to continue your clue..");
			}
			break;
		case 4837:
			if (NpcDefinition.forId(npc.getId()).getName().contains("ark wizar")) {
				TaskManager.submit(new Task(1, player, true) {
					int tick = 0;
					@Override
					public void execute() {
						if (tick >= 8) {
							stop();
						}
						switch(tick) {
						case 0:
							player.getInventory().delete(new Item(4837, 1));
							break;
						case 1:
							player.forceChat("Can I have your autograph?");
							break;
						case 3:
							npc.forceChat("Yea noob lol here u go");
							break;
						case 4:
							npc.performAnimation(new Animation(1249));
							break;
						case 7:
							player.getInventory().add(new Item(22040, 1));
							player.getPacketSender().sendMessage("The Dark Wizard signs your Necromancy Book, transforming it.");
							break;
						}
						tick++;
					}
				});
			}
			break;
		}
		for (int i = 0; i < logData.values().length; i++) {
			if (logData.values()[i].getLogId() == id) {
				if (npc.getId() == 7377) {
					if (player.getSummoning().getFamiliar() == null) {
						player.getPacketSender().sendMessage("That isn't your familiar!");
						return;
					}
					if (player.getSummoning().getFamiliar().getSummonNpc().getId() != 7377) {
						player.getPacketSender().sendMessage("You must have your own Pyrefiend to use that effect.");
						return;
					}
					Firemaking.lightFire(player, id, false, 1);
				}
			break;
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static void itemOnGroundItem(Player player) {
		player.getPacketSender().sendMessage("Nothing interesting happens.");
		//System.out.println("itemongrounditem");
	}

	@SuppressWarnings("unused")
	private static void itemOnPlayer(Player player, Packet packet) {
		int interfaceId = packet.readUnsignedShortA();
		int targetIndex = packet.readUnsignedShort();
		int itemId = packet.readUnsignedShort();
		int slot = packet.readLEShort();
		if (slot < 0 || slot > player.getInventory().capacity() || targetIndex > World.getPlayers().capacity())
			return;
		Player target = World.getPlayers().get(targetIndex);
		if(target == null)
			return;
		switch (itemId) {
		case 962:
			if(!player.getInventory().contains(962) || player.getRights() == PlayerRights.ADMINISTRATOR)
				return;
			player.setPositionToFace(target.getPosition());
			player.performGraphic(new Graphic(1006));
			player.performAnimation(new Animation(451));
			player.getPacketSender().sendMessage("You pull the Christmas cracker...");
			target.getPacketSender().sendMessage(""+player.getUsername()+" pulls a Christmas cracker on you..");
			player.getInventory().delete(962, 1);
			player.getPacketSender().sendMessage("The cracker explodes and you receive a Party hat!");
			int phat = 1038 + Misc.getRandom(10);
			player.getInventory().add(phat, 1);
			target.getPacketSender().sendMessage(""+player.getUsername()+" has received a Party hat!");
			PlayerLogs.log(player.getUsername(), "Opened a cracker containing a "+ItemDefinition.forId(phat).getName()+" on "+target.getUsername());
			/*	if(Misc.getRandom(1) == 1) {
				target.getPacketSender().sendMessage("The cracker explodes and you receive a Party hat!");
				target.getInventory().add((1038 + Misc.getRandom(5)*2), 1);
				player.getPacketSender().sendMessage(""+target.getUsername()+" has received a Party hat!");
			} else {
				player.getPacketSender().sendMessage("The cracker explodes and you receive a Party hat!");
				player.getInventory().add((1038 + Misc.getRandom(5)*2), 1);			
				target.getPacketSender().sendMessage(""+player.getUsername()+" has received a Party hat!");
			}*/
			break;
		/*	case 15707:
				DungeoneeringParty.invite(player);
				break; */

			case 15707:
				Player partyDung = World.getPlayers().get(targetIndex);
				if(player.getLocation() != Location.DUNGEONEERING || player.isTeleporting()) {
					player.getPacketSender().sendMessage("You're not in Daemonheim");
					return;
				}
				if(partyDung.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
					player.getPacketSender().sendMessage("That player is already in a party.");
					return;
				}
				if(player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) {
					player.getPacketSender().sendMessage("You're not in a party!");
					return;
				}
				if(player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner() != player) {
					player.getPacketSender().sendMessage("Only the party leader can invite other players.");
					return;
				}
				if(player.busy()) {
					player.getPacketSender().sendMessage("You're busy and can't invite anyone.");
					return;
				}
				if(partyDung.busy()) {
					player.getPacketSender().sendMessage(partyDung.getUsername()+" is too busy to get your invite.");
					return;
				}
				DialogueManager.start(partyDung, new DungPartyInvitation(player, partyDung));
				player.getPacketSender().sendMessage("An invitation has been sent to " + partyDung.getUsername() );
				break;
		case 4566:
			player.performAnimation(new Animation(451));
				break;
		case 4155:
			if (player.getSlayer().getDuoPartner() != null) {
				player.getPacketSender().sendMessage(
						"You already have a duo partner.");
				return;
			}
			if (player.getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
				player.getPacketSender().sendMessage(
						"You already have a Slayer task. You must reset it first.");
				return;
			}
			Player duoPartner = World.getPlayers().get(targetIndex);
			if (duoPartner != null) {
				if (duoPartner.getSlayer().getDuoPartner() != null) {
					player.getPacketSender().sendMessage(
							"This player already has a duo partner.");
					return;
				}
				if(duoPartner.getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
					player.getPacketSender().sendMessage("This player already has a Slayer task.");
					return;
				}
				if(duoPartner.getSlayer().getSlayerMaster() != player.getSlayer().getSlayerMaster()) {
					player.getPacketSender().sendMessage("You do not have the same Slayer master as that player.");
					return;
				}
				if (duoPartner.busy() || duoPartner.getLocation() == Location.WILDERNESS) {
					player.getPacketSender().sendMessage(
							"This player is currently busy.");
					return;
				}
				DialogueManager.start(duoPartner, SlayerDialogues.inviteDuo(duoPartner, player));
				player.getPacketSender().sendMessage(
						"You have invited " + duoPartner.getUsername()
						+ " to join your Slayer duo team.");
			}
			break;
		}
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		switch (packet.getOpcode()) {
		case ITEM_ON_ITEM:
			itemOnItem(player, packet);
			break;
		case USE_ITEM:
			useItem(player, packet);
			break;
		case ITEM_ON_OBJECT:
			itemOnObject(player, packet);
			break;
		case ITEM_ON_GROUND_ITEM:
			itemOnGroundItem(player);
			break;
		case ITEM_ON_NPC:
			itemOnNpc(player, packet);
			break;
		case ITEM_ON_PLAYER:
			itemOnPlayer(player, packet);
			break;
		}
	}

	public final static int USE_ITEM = 122;

	public final static int ITEM_ON_NPC = 57;

	public final static int ITEM_ON_ITEM = 53;

	public final static int ITEM_ON_OBJECT = 192;

	public final static int ITEM_ON_GROUND_ITEM = 25;

	public static final int ITEM_ON_PLAYER = 14;
}
