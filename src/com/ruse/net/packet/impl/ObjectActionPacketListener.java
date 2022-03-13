package com.ruse.net.packet.impl;

import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.WalkToTask;
import com.ruse.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.ruse.model.Animation;
import com.ruse.model.Direction;
import com.ruse.model.DwarfCannon;
import com.ruse.model.Flag;
import com.ruse.model.GameObject;
import com.ruse.model.Graphic;
import com.ruse.model.GraphicHeight;
import com.ruse.model.Locations.Location;
import com.ruse.model.MagicSpellbook;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Prayerbook;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.GameObjectDefinition;
import com.ruse.model.input.impl.DonateToWell;
import com.ruse.model.input.impl.EnterAmountOfLogsToAdd;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.clip.region.RegionClipping;
import com.ruse.world.content.CrystalChest;
import com.ruse.world.content.CustomObjects;
import com.ruse.world.content.WildernessObelisks;
import com.ruse.world.content.combat.magic.Autocasting;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.range.DwarfMultiCannon;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.grandexchange.GrandExchange;
import com.ruse.world.content.holidayevents.christmas2016;
import com.ruse.world.content.holidayevents.easter2017data;
import com.ruse.world.content.minigames.impl.Barrows;
import com.ruse.world.content.minigames.impl.Dueling;
import com.ruse.world.content.minigames.impl.Dueling.DuelRule;
import com.ruse.world.content.minigames.impl.FightCave;
import com.ruse.world.content.minigames.impl.FightPit;
import com.ruse.world.content.minigames.impl.Nomad;
import com.ruse.world.content.minigames.impl.PestControl;
import com.ruse.world.content.minigames.impl.RecipeForDisaster;
import com.ruse.world.content.minigames.impl.WarriorsGuild;
import com.ruse.world.content.portal.portal;
import com.ruse.world.content.randomevents.EvilTree.EvilTreeDef;
import com.ruse.world.content.skill.impl.agility.Agility;
import com.ruse.world.content.skill.impl.construction.Construction;
import com.ruse.world.content.skill.impl.construction.ConstructionActions;
import com.ruse.world.content.skill.impl.crafting.Flax;
import com.ruse.world.content.skill.impl.crafting.Jewelry;
import com.ruse.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.ruse.world.content.skill.impl.fishing.Fishing;
import com.ruse.world.content.skill.impl.fishing.Fishing.Spot;
import com.ruse.world.content.skill.impl.hunter.Hunter;
import com.ruse.world.content.skill.impl.hunter.PuroPuro;
import com.ruse.world.content.skill.impl.mining.Mining;
import com.ruse.world.content.skill.impl.mining.MiningData;
import com.ruse.world.content.skill.impl.mining.Prospecting;
import com.ruse.world.content.skill.impl.runecrafting.Runecrafting;
import com.ruse.world.content.skill.impl.runecrafting.RunecraftingData;
import com.ruse.world.content.skill.impl.smithing.EquipmentMaking;
import com.ruse.world.content.skill.impl.smithing.Smelting;
import com.ruse.world.content.skill.impl.thieving.Stalls;
import com.ruse.world.content.skill.impl.woodcutting.Woodcutting;
import com.ruse.world.content.skill.impl.woodcutting.WoodcuttingData;
import com.ruse.world.content.skill.impl.woodcutting.WoodcuttingData.Hatchet;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportLocations;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.impl.player.Player;

/**
 * This packet listener is called when a player clicked
 * on a game object.
 * 
 * @author relex lawl
 */

public class ObjectActionPacketListener implements PacketListener {

	/**
	 * The PacketListener logger to debug information and print out errors.
	 */
	//private final static Logger logger = Logger.getLogger(ObjectActionPacketListener.class);

	private static void firstClick(final Player player, Packet packet) {
		final int x = packet.readLEShortA();
		final int id = packet.readUnsignedShort();
		final int y = packet.readUnsignedShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final GameObject gameObject = new GameObject(id, position);
		if(id > 0 && id != 6 && !RegionClipping.objectExists(gameObject)) {
			if (player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("A interaction error occured. Error code: "+id);
			} else {
				player.getPacketSender().sendMessage("Nothing interesting happens.");
			}
			return;
		}
		int distanceX = (player.getPosition().getX() - position.getX());
		int distanceY = (player.getPosition().getY() - position.getY());
		if (distanceX < 0)
			distanceX = -(distanceX);
		if (distanceY < 0)
			distanceY = -(distanceY);
		int size = distanceX > distanceY ? GameObjectDefinition.forId(id).getSizeX() : GameObjectDefinition.forId(id).getSizeY();
		if (size <= 0)
			size = 1;
		gameObject.setSize(size);
		if(player.getMovementQueue().isLockMovement())
			return;
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("First click object id; [id, position] : [" + id + ", " + position.toString() + "]");
		player.setInteractingObject(gameObject).setWalkToTask(new WalkToTask(player, position, gameObject.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				player.setPositionToFace(gameObject.getPosition());
				if(WoodcuttingData.Trees.forId(id) != null) {
					Woodcutting.cutWood(player, gameObject, false);
					return;
				}
				if(EvilTreeDef.forId(id) != null){
					Woodcutting.cutWood(player, gameObject, false);
					return;
				}
				if(MiningData.forRock(gameObject.getId()) != null) {
					Mining.startMining(player, gameObject);
					return;
				}
				if (player.getFarming().click(player, x, y, 1))
					return;
				if(Runecrafting.runecraftingAltar(player, gameObject.getId())) {
					RunecraftingData.RuneData rune = RunecraftingData.RuneData.forId(gameObject.getId());
					if(rune == null)
						return;
					Runecrafting.craftRunes(player, rune);
					return;
				}
				if(Agility.handleObject(player, gameObject)) {
					return;
				}
				if(Barrows.handleObject(player, gameObject)) {
					return;
				}
				if(player.getLocation() != null && player.getLocation() == Location.WILDERNESS && WildernessObelisks.handleObelisk(gameObject.getId())) {
					return;
				}
				if(ConstructionActions.handleFirstObjectClick(player, gameObject)) {
					return;
				}
				if (gameObject.getDefinition() != null && gameObject.getDefinition().getName() != null && gameObject.getDefinition().name.equalsIgnoreCase("door") && player.getRights().OwnerDeveloperOnly()) {
					player.getPacketSender().sendMessage("You just clicked a door. ID: "+id);
					//CustomObjects.deleteGlobalObject(gameObject);
					
					/*Door door = Door.create(gameObject.getId(), gameObject.getPosition().getX(), gameObject.getPosition().getY());
					//GameObject obj = Region.loadRegion(gameObject.getPosition().getX(), gameObject.getPosition().getY()).getObject(gameObject.getId(), gameObject.getPosition().getX(), gameObject.getPosition().getY());
					//player.createObject(gameObject.getPosition().getX(), gameObject.getPosition().getY(), gameObject.getId(), door.isOpen() ? obj.getFace() : obj.getFace() + 1, 0);
					player.getPacketSender().sendObject(new GameObject(gameObject.getId(), new Position(gameObject.getPosition().getX(), gameObject.getPosition().getY()), 10, (door.isOpen() ? gameObject.getFace() : gameObject.getFace() + 1)));
					door.setOpen(!door.isOpen());*/
				}
				switch(id) {
				case 2305:
					if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
						player.moveTo(new Position(3003, 10354, player.getPosition().getZ()));
						player.getPacketSender().sendMessage("You escape from the spikes.");
					}
					break;
				case 589: //varrock ball
					if (Misc.easter(2017)) {
						if (player.getInventory().isFull()) {
							player.getPacketSender().sendMessage("My inventory is too full, I should make room first.");
							return;
						}
						if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
							player.getPacketSender().sendMessage(easter2017data.forObjectId(id).getSearchMessage());
							player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress()+1);
							player.getInventory().add(1961, 1);
						}
					} else {
						player.getPacketSender().sendMessage("Just a wise old woman's ball.");
					}
					break;
				case 11678:
					if (Misc.easter(2017)) {
						if (player.getInventory().isFull()) {
							player.getPacketSender().sendMessage("My inventory is too full, I should make room first.");
							return;
						}
						if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
							player.getPacketSender().sendMessage(easter2017data.forObjectId(id).getSearchMessage());
							player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress()+1);
							player.getInventory().add(1961, 1);
						}
					} else {
						player.getPacketSender().sendMessage("Nope. Nothing special to it.");
					}
					break;
				case 5595:
					if (Misc.easter(2017)) {
						if (player.getInventory().isFull()) {
							player.getPacketSender().sendMessage("My inventory is too full, I should make room first.");
							return;
						}
						if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
							player.getPacketSender().sendMessage(easter2017data.forObjectId(id).getSearchMessage());
							player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress()+1);
							player.getInventory().add(1961, 1);
						}
					} else {
						player.getPacketSender().sendMessage("Just some toys.");
					}
					break;
				case 2725:
					if (Misc.easter(2017)) {
						if (player.getInventory().isFull()) {
							player.getPacketSender().sendMessage("My inventory is too full, I should make room first.");
							return;
						}
						if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
							player.getPacketSender().sendMessage(easter2017data.forObjectId(id).getSearchMessage());
							player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress()+1);
							player.getInventory().add(1961, 1);
						}
					} else {
						player.getPacketSender().sendMessage("Just regular fireplace things.");
					}
					break;
				case 423:
					if (Misc.easter(2017)) {
						if (player.getInventory().isFull()) {
							player.getPacketSender().sendMessage("My inventory is too full, I should make room first.");
							return;
						}
						if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
							player.getPacketSender().sendMessage(easter2017data.forObjectId(id).getSearchMessage());
							player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress()+1);
							player.getInventory().add(1961, 1);
						}
					} else {
						player.getPacketSender().sendMessage("I don't want to mess around with someone's bed.");
					}
					break;
				case 11339:
					if (Misc.easter(2017)) {
						if (player.getInventory().isFull()) {
							player.getPacketSender().sendMessage("My inventory is too full, I should make room first.");
							return;
						}
						if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
							player.getPacketSender().sendMessage(easter2017data.forObjectId(id).getSearchMessage());
							player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress()+1);
							player.getInventory().add(1961, 1);
						}
					} else {
						player.getPacketSender().sendMessage("Just some gold, I can get enough on my own.");
					}
					break;
				case 17953:
					if (player.getLocation() == Location.ZULRAH_WAITING) {
						player.getPacketSender().sendMessage("You push the boat into the swamp...");
						//player.setPositionToFace(gameObject.getPosition());
						player.performAnimation(new Animation(923));
						TaskManager.submit(new Task(1, player, true) {
							int tick = 0;
							@Override
							public void execute() {
								if (tick >= 2) {
									//player.moveTo(new Position(player.getPosition().getX()-1, player.getPosition().getY()));
									player.moveTo(new Position(3420, 2777, (player.getIndex()+1)*4));
									player.getPacketSender().sendMessage("...And arrive in Zulrah's territory.");
									stop();
								}
								tick++;
							}
						});
					} else if (player.getLocation() == Location.ZULRAH) {
						if (!player.getRights().isMember() && player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 85) {
							player.getPacketSender().sendMessage("You need 85 Agility to navigate the boat back to camp!");
							return;
						}
						if (player.getRights().isMember() && player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 85) {
							player.getPacketSender().sendMessage("As a member you can navigate the swamp without 85 Agility.");
						}
						player.getPacketSender().sendMessage("You push the boat into the swamp...");
						//player.setPositionToFace(gameObject.getPosition());
						player.performAnimation(new Animation(923));
						TaskManager.submit(new Task(1, player, true) {
							int tick = 0;
							@Override
							public void execute() {
								if (tick >= 2) {
									//player.moveTo(new Position(player.getPosition().getX()-1, player.getPosition().getY()));
									player.moveTo(new Position(3406, 2794, 0));
									player.getPacketSender().sendMessage("...And return to the pillar santuary.");
									stop();
								}
								tick++;
							}
						});
					}
					//TeleportHandler.teleportPlayer(player, new Position(3420, 2777, (player.getIndex()+1)*4), player.getSpellbook().getTeleportType()); //zulrah instance
					break;
				case 28295:
					if (christmas2016.isChristmas()) {
						player.getPacketSender().sendMessage("Welcome to the Christmas 2016 event!");
						player.moveTo(christmas2016.eventStart);
					}
					break;
				case 28296:
					if (!player.getClickDelay().elapsed(1250)) {
						//player.getPacketSender().sendMessage("Your hands are getting cold, slow down!");
						return;
					}
					player.getClickDelay().reset();
					if (!player.getInventory().isFull() || (player.getInventory().getFreeSlots() == 0 && player.getInventory().contains(10501))) {
						player.performAnimation(new Animation(827));
						player.getInventory().add(10501, Misc.getRandom(20));
						player.getPacketSender().sendMessage("You pack some of the snow together...");
					} else {
						player.getPacketSender().sendMessage("You'll need some inventory space first!");
					}
					break;
				case 134:
				case 135:
					if (player.getPosition().getY() < 3354 && GameSettings.Halloween) {
						TeleportHandler.teleportPlayer(player, new Position(3109, 3354, 404),TeleportType.NORMAL);
					} 
					boolean move = player.getPosition().getY() < 3354;
					if (!move) {
							player.getPacketSender().sendMessage("Nope, it's not going to move.");
					}
						
						/*
					TaskManager.submit(new Task(0, player, true) {
						int tick = 0;
						@Override
						public void execute() {
							tick++;
							if (player.getPosition().getX() > 3106 && player.getPosition().getX() < 3111 && player.getPosition().getY() == 3353) { //player.getPosition().getY() > 3226 && player.getPosition().getY() < 3229) {
								player.getMovementQueue().walkStep(0, 1);
								player.getPacketSender().sendMessage("The heavy doors shut as you enter.");
							} else if (player.getPosition().getY() > 3353) {
								player.getPacketSender().sendMessage("That has no intention of moving...");
							} else {
								player.getPacketSender().sendMessage("You're too far away!");
							}
							if(tick == 1)
								stop();
						}
						@Override
						public void stop() {
							setEventRunning(false);
							//player.setCrossingObstacle(false);
						}
					});
					}*/
					break;
				case 2112:
					if (!player.getRights().isMember()) {
						player.getPacketSender().sendMessage("You must be a member to access this area.");
						return;
					}
					TaskManager.submit(new Task(0, player, true) {
						int tick = 0;
						@Override
						public void execute() {
							tick++;
							if (player.getPosition().getX() == 3046 && player.getPosition().getY() == 9757) {
								player.getMovementQueue().walkStep(0, -1);
								player.getPacketSender().sendMessage("As a member, you can pass through the door.");
							} else if (player.getPosition().getX() == 3046 && player.getPosition().getY() == 9756) {
								player.getMovementQueue().walkStep(0, 1);
								player.getPacketSender().sendMessage("As a member, you can pass through the door.");
							} else {
								player.getPacketSender().sendMessage("You must be in front of the door first.");
							}
							if(tick == 1)
								stop();
						}
						@Override
						public void stop() {
							setEventRunning(false);
							//player.setCrossingObstacle(false);
						}
					});
					break;
				case 2882:
				case 2883:
					TaskManager.submit(new Task(0, player, true) {
						int tick = 0;
						@Override
						public void execute() {
							tick++;
							if (player.getPosition().getX() == 3268 && player.getPosition().getY() > 3226 && player.getPosition().getY() < 3229) {
								player.getMovementQueue().walkStep(-1, 0);
								player.getPacketSender().sendMessage("You pass through the gate.");
							} else if (player.getPosition().getX() == 3267 && player.getPosition().getY() > 3226 && player.getPosition().getY() < 3229) {
								player.getMovementQueue().walkStep(1, 0);
								player.getPacketSender().sendMessage("You pass through the gate.");
							} else {
								player.getPacketSender().sendMessage("You must be in front of the gate first.");
							}
							if(tick == 1)
								stop();
						}
						@Override
						public void stop() {
							setEventRunning(false);
							//player.setCrossingObstacle(false);
						}
					});
					break;
				case 5262:
					if(player.getLocation() == Location.KRAKEN) {
						player.getPacketSender().sendMessage("You leave the cave and end up at home.");
						player.moveTo(GameSettings.DEFAULT_POSITION.copy());
					}
					break;
				case 2273:
					player.moveTo(new Position(3563, 3313, 0));
					Location.THE_SIX.leave(player);
					break;
				case 5259:
					if(player.getPosition().getX() >= 3653) { // :)
						if(player.getPosition().getY() != 3485 && player.getPosition().getY() != 3486) {
							player.getPacketSender().sendMessage("You need to stand infront of the barrier to pass through.");
							return;
						}
						player.moveTo(new Position(3651, player.getPosition().getY()));
					} else {
						player.setDialogueActionId(73);
						DialogueManager.start(player, 115);
					}
					break;
				case 10805:
				case 10806:
					GrandExchange.open(player);
					break;
				case 38700:
							if (gameObject.getPosition().getX() == 3668 && gameObject.getPosition().getY() == 2976) {
								player.getPacketSender().sendMessage("<img=10> @blu@Welcome to the free-for-all arena! You will not lose any items on death here.");
								player.moveTo(new Position(2815, 5511));
							} else if(player.getLocation() == Location.FREE_FOR_ALL_WAIT) {
								player.moveTo(GameSettings.DEFAULT_POSITION.copy());
							} else if(gameObject.getPosition().getX() == 2849 && gameObject.getPosition().getY() == 3353) {
								player.getPacketSender().sendMessage("<img=10> @blu@Welcome to the free-for-all arena! You will not lose any items on death here.");
								player.moveTo(new Position(2815, 5511));
							}
					break;
				case 2465:
					if(player.getLocation() == Location.EDGEVILLE) {
						player.getPacketSender().sendMessage("<img=10> @blu@Welcome to the free-for-all arena! You will not lose any items on death here.");
						player.moveTo(new Position(2815, 5511));
					} else {
						player.getPacketSender().sendMessage("The portal does not seem to be functioning properly.");
					}
					break;
				case 45803:
				case 1767:
					DialogueManager.start(player, 114);
					player.setDialogueActionId(72);
					break;
				case 7352:
					if(Dungeoneering.doingDungeoneering(player) && player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getGatestonePosition() != null) {
						player.moveTo(player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getGatestonePosition());
						player.setEntityInteraction(null);
						player.getPacketSender().sendMessage("You are teleported to your party's gatestone.");
						player.performGraphic(new Graphic(1310));
					} else
						player.getPacketSender().sendMessage("Your party must drop a Gatestone somewhere in the dungeon to use this portal.");
					break;
				case 7353:
					player.moveTo(new Position(2439, 4956, player.getPosition().getZ()));
					break;
				case 7321:
					player.moveTo(new Position(2452, 4944, player.getPosition().getZ()));
					break;
				case 7322:
					player.moveTo(new Position(2455, 4964, player.getPosition().getZ()));
					break;
				case 7315:
					player.moveTo(new Position(2447, 4956, player.getPosition().getZ()));
					break;
				case 7316:
					player.moveTo(new Position(2471, 4956, player.getPosition().getZ()));
					break;
				case 7318:
					player.moveTo(new Position(2464, 4963, player.getPosition().getZ()));
					break;
					//case 7319:
					//player.moveTo(new Position(2467, 4940, player.getPosition().getZ()));
					//break;
				case 7324:
					player.moveTo(new Position(2481, 4956, player.getPosition().getZ()));
					break;

				case 7319:
					if (gameObject.getPosition().getX() == 2481 && gameObject.getPosition().getY() == 4956)
						player.moveTo(new Position(2467, 4940, player.getPosition().getZ()));
					break;

                    case 4388:
                    	break;

				case 11356:
					player.moveTo(new Position(2860, 9741));
					player.getPacketSender().sendMessage("You step through the portal..");
					break;
				case 47180:
					if(!player.getRights().isMember()) {
						player.getPacketSender().sendMessage("You must be a Member to use this.");
						return;
					}
					player.getPacketSender().sendMessage("You activate the device..");
					player.moveTo(new Position(2586, 3912));
					break;
				case 10091:
				case 8702:
					if(gameObject.getId() == 8702) {
						if(!player.getRights().isMember()) {
							player.getPacketSender().sendMessage("You must be a Member to use this.");
							return;
						}
					}
					Fishing.setupFishing(player, Spot.ROCKTAIL);
					break;
				case 9319:
					if(player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 61){
						player.getPacketSender().sendMessage("You need an Agility level of at least 61 or higher to climb this");
						return;
					}
					if(player.getPosition().getZ() == 0)
						player.moveTo(new Position(3422, 3549, 1));
					else if(player.getPosition().getZ() == 1) {
						if(gameObject.getPosition().getX() == 3447) 
							player.moveTo(new Position(3447, 3575, 2));
						else
							player.moveTo(new Position(3447, 3575, 0));
					}
					break;

				case 9320:
					if(player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 61) {
						player.getPacketSender().sendMessage("You need an Agility level of at least 61 or higher to climb this");
						return;
					}
					if(player.getPosition().getZ() == 1)
						player.moveTo(new Position(3422, 3549, 0));
					else if(player.getPosition().getZ() == 0)
						player.moveTo(new Position(3447, 3575, 1));
					else if(player.getPosition().getZ() == 2)
						player.moveTo(new Position(3447, 3575, 1));
					player.performAnimation(new Animation(828));
					break;
				case 2470:
					if (player.getTeleblockTimer() > 0) {
						player.getPacketSender().sendMessage(
								"You are teleblocked, don't die, noob.");
						return;
					}
					if(gameObject.getPosition().getX() == 2464 && gameObject.getPosition().getY() == 4782) {
						player.moveTo(GameSettings.DEFAULT_POSITION.copy());
						player.getPacketSender().sendMessage("The portal teleports you home.");
						return;
					}
					if(gameObject.getPosition().getX() == 3674 && gameObject.getPosition().getY() == 2981 && GameSettings.FridayThe13th) {
						player.moveTo(new Position(2463, 4782));
						player.getPacketSender().sendMessage("Enjoy the Friday the 13th mini-event.");
					}
					if(gameObject.getPosition().getX() == 3674 && gameObject.getPosition().getY() == 2981 && GameSettings.Halloween) {
						player.moveTo(new Position(3108, 3352, 4));
							player.getPacketSender().sendMessage("<img=10> You teleport to the event!");
							return;
					}
					break;
				case 2274:
					if (player.getTeleblockTimer() > 0) {
						player.getPacketSender().sendMessage(
								"You are teleblocked, don't die, noob.");
						return;
					}
					if(gameObject.getPosition().getX() == 2912 && gameObject.getPosition().getY() == 5300) {
						player.moveTo(new Position(2914, 5300, 1));
					} else if(gameObject.getPosition().getX() == 2914 && gameObject.getPosition().getY() == 5300) {
						player.moveTo(new Position(2912, 5300, 2));
					} else if(gameObject.getPosition().getX() == 3553 && gameObject.getPosition().getY() == 9695) {
						player.moveTo(new Position(3565, 3313, 0));
					} else if(gameObject.getPosition().getX() == 2919 && gameObject.getPosition().getY() == 5276) {
						player.moveTo(new Position(2918, 5274));
					} else if(gameObject.getPosition().getX() == 2918 && gameObject.getPosition().getY() == 5274) {
						player.moveTo(new Position(2919, 5276, 1));
					} else if(gameObject.getPosition().getX() == 3001 && gameObject.getPosition().getY() == 3931 || gameObject.getPosition().getX() == 3652 && gameObject.getPosition().getY() == 3488) {
						player.moveTo(GameSettings.DEFAULT_POSITION.copy());
						player.getPacketSender().sendMessage("The portal teleports you home.");
					//} else if(gameObject.getPosition().getX() == 2914 && gameObject.getPosition().getY() == 5300 && (player.getAmountDonated() >= 5 || player.getSkillManager().getCurrentLevel(Skill.AGILITY) == 99)) {
					//	player.getPacketSender().sendMessage("You would have access to the shortcut.");
					}
					break;
				 case 28779:
					if (player.getTeleblockTimer() > 0) {
						player.getPacketSender().sendMessage(
								"You are teleblocked, and cannot navigate the chaos tunnels.");
						return;
					}
					Position des = new Position(-1, -1);
					for (int i = 0; i < portal.values().length; i++) {
						if (portal.values()[i].getLocation().getX() == gameObject.getPosition().getX() && portal.values()[i].getLocation().getY() == gameObject.getPosition().getY()) {
							des = new Position(portal.values()[i].getDestination().getX(), portal.values()[i].getDestination().getY(), player.getPosition().getZ());
							//System.out.println("Matched on portal index "+i);
							break;
						}
					}
					if (des.getX() != -1 && des.getY() != -1) {
						player.moveTo(des);
					} else {
						player.getPacketSender().sendMessage("ERROR 13754, no internals. Report on forums!");
					}
					/*if(gameObject.getPosition().getX() == 3186 && gameObject.getPosition().getY() == 5472) {
						player.moveTo(new Position(3192, 5471, 0));
					} else if(gameObject.getPosition().getX() == 3192 && gameObject.getPosition().getY() == 5472) {
						player.moveTo(new Position(3185, 5472, 0));
					} else if(gameObject.getPosition().getX() == 3197 && gameObject.getPosition().getY() == 5448) {
						player.moveTo(new Position(3205, 5445, 0));
					} else if(gameObject.getPosition().getX() == 3204 && gameObject.getPosition().getY() == 5445) {
						player.moveTo(new Position(3196, 5448, 0));
					} else if(gameObject.getPosition().getX() == 3189 && gameObject.getPosition().getY() == 5444) {
						player.moveTo(new Position(3187, 5459, 0));
					} else if(gameObject.getPosition().getX() == 3187 && gameObject.getPosition().getY() == 5460) {
						player.moveTo(new Position(3190, 5444, 0));
					} else if(gameObject.getPosition().getX() == 3178 && gameObject.getPosition().getY() == 5460) {
						player.moveTo(new Position(3168, 5457, 0));
					} else if(gameObject.getPosition().getX() == 3168 && gameObject.getPosition().getY() == 5456) {
						player.moveTo(new Position(3178, 5459, 0));
					} else if(gameObject.getPosition().getX() == 3167 && gameObject.getPosition().getY() == 5471) {
						player.moveTo(new Position(3172, 5473, 0));
					} else if(gameObject.getPosition().getX() == 3171 && gameObject.getPosition().getY() == 5473) {
						player.moveTo(new Position(3167, 5470, 0));
					} else if(gameObject.getPosition().getX() == 3171 && gameObject.getPosition().getY() == 5478) {
						player.moveTo(new Position(3166, 5478, 0));
					} else if(gameObject.getPosition().getX() == 3167 && gameObject.getPosition().getY() == 5478) {
						player.moveTo(new Position(3172, 5478, 0));
						//
					}*/
					break; 
				case 7836:
				case 7808:
					int amt = player.getInventory().getAmount(6055);
					if(amt > 0) {
						player.getInventory().delete(6055, amt);
						player.getPacketSender().sendMessage("You put the weed in the compost bin.");
						player.getSkillManager().addExperience(Skill.FARMING, 1*amt);
					} else {
						player.getPacketSender().sendMessage("You do not have any weeds in your inventory.");
					}
					break;
				case 5960: //Levers
				case 5959:
					if (player.getLocation() == Location.MAGEBANK_SAFE) {
						TeleportHandler.teleportPlayer(player, TeleportLocations.MAGEBANK_WILDY.getPos(), TeleportType.LEVER);
					} else if (player.getWildernessLevel() >= 53 && player.getLocation() == Location.WILDERNESS) {
						TeleportHandler.teleportPlayer(player, TeleportLocations.MAGEBANK_SAFE.getPos(), TeleportType.LEVER);
					} else {
						player.getPacketSender().sendMessage("ERROR: 00512, P: ["+player.getPosition().getX()+","+player.getPosition().getY()+","+player.getPosition().getZ()+"] - please report this bug!");
					}
					break;
					//player.setDirection(Direction.WEST);
					//TeleportHandler.teleportPlayer(player, new Position(3090, 3475), TeleportType.LEVER);
					//break;
				case 5096:
					if (gameObject.getPosition().getX() == 2644 && gameObject.getPosition().getY() == 9593)
						player.moveTo(new Position(2649, 9591));
					break;

				case 5094:
					if (gameObject.getPosition().getX() == 2648 && gameObject.getPosition().getY() == 9592)
						player.moveTo(new Position(2643, 9594, 2));
					break;

				case 5098:
					if (gameObject.getPosition().getX() == 2635 && gameObject.getPosition().getY() == 9511)
						player.moveTo(new Position(2637, 9517));
					break;

				case 5097:
					if (gameObject.getPosition().getX() == 2635 && gameObject.getPosition().getY() == 9514)
						player.moveTo(new Position(2636, 9510, 2));
					break;
				case 26428:
				case 26426:
				case 26425:
				case 26427:
					String bossRoom = "Armadyl";
					boolean leaveRoom = player.getPosition().getY() > 5295;
					int index = 0;
					Position movePos = new Position(2839, !leaveRoom ? 5296 : 5295, 2);
					if(id == 26425) {
						bossRoom = "Bandos";
						leaveRoom = player.getPosition().getX() > 2863;
						index = 1;
						movePos = new Position(!leaveRoom ? 2864 : 2863, 5354, 2);
					} else if(id == 26427) {
						bossRoom = "Saradomin";
						leaveRoom = player.getPosition().getX() < 2908;
						index = 2;
						movePos = new Position(leaveRoom ? 2908 : 2907, 5265);
					} else if(id == 26428) {
						bossRoom = "Zamorak";
						leaveRoom = player.getPosition().getY() <= 5331;
						index = 3;
						movePos = new Position(2925, leaveRoom ? 5332 : 5331, 2);
					}
					if(!leaveRoom && (!player.getRights().isMember() && player.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[index] < 20)) {
						if (player.getInventory().contains(22053)) {
							player.getInventory().delete(22053, 1);
							player.getPacketSender().sendMessage("Your ecumenical key is consumed, and you pass through the door.");
						} else {
							player.getPacketSender().sendMessage("You need "+Misc.anOrA(bossRoom)+" "+bossRoom+" killcount of at least 20 to enter this room.");
							return;
						}
					}
					if(player.getRights().isMember()) {
						player.getPacketSender().sendMessage("@red@As a member, you don't need to worry about kill count.");
						player.performGraphic(new Graphic(6, GraphicHeight.LOW));
					}
					player.moveTo(movePos);
					player.getMinigameAttributes().getGodwarsDungeonAttributes().setHasEnteredRoom(leaveRoom ? false : true);
					player.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[index] = 0;
					player.getPacketSender().sendString(16216+index, "0");
					break;
				case 26289:
				case 26286:
				case 26288:
				case 26287:
					if(System.currentTimeMillis() - player.getMinigameAttributes().getGodwarsDungeonAttributes().getAltarDelay() < 600000) {
						player.getPacketSender().sendMessage("");
						player.getPacketSender().sendMessage("You can only pray at a God's altar once every 10 minutes.");
						player.getPacketSender().sendMessage("You must wait another "+(int)((600 - (System.currentTimeMillis() - player.getMinigameAttributes().getGodwarsDungeonAttributes().getAltarDelay()) * 0.001))+" seconds before being able to do this again.");
						return;
					}
					int itemCount = id == 26289 ? Equipment.getItemCount(player, "Bandos", false) : id == 26286 ? Equipment.getItemCount(player, "Zamorak", false) : id == 26288 ? Equipment.getItemCount(player, "Armadyl", false) : id == 26287 ? Equipment.getItemCount(player, "Saradomin", false) : 0;
					int toRestore = player.getSkillManager().getMaxLevel(Skill.PRAYER) + (itemCount * 10);
					if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) >= toRestore) {
						player.getPacketSender().sendMessage("You do not need to recharge your Prayer points at the moment.");
						return;
					}
					player.performAnimation(new Animation(645));
					player.getSkillManager().setCurrentLevel(Skill.PRAYER, toRestore);
					player.getMinigameAttributes().getGodwarsDungeonAttributes().setAltarDelay(System.currentTimeMillis());
					break;
				case 23093:
					if(player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 70) {
						player.getPacketSender().sendMessage("You need an Agility level of at least 70 to go through this portal.");
						return;
					}
					if(!player.getClickDelay().elapsed(2000)) 
						return;
					int plrHeight = player.getPosition().getZ();
					if(plrHeight == 2)
						player.moveTo(new Position(2914, 5300, 1));
					else if(plrHeight == 1) {
						int x = gameObject.getPosition().getX();
						int y = gameObject.getPosition().getY();
						if(x == 2914 && y == 5300)
							player.moveTo(new Position(2912, 5299, 2));
						else if(x == 2920 && y == 5276)
							player.moveTo(new Position(2920, 5274, 0));
					} else if(plrHeight == 0)
						player.moveTo(new Position(2920, 5276, 1));
					player.getClickDelay().reset();
					break;
				case 26439:
					if(player.getSkillManager().getMaxLevel(Skill.CONSTITUTION) <= 700 && !(player.getRights().isMember())) {
						player.getPacketSender().sendMessage("You need a Constitution level of at least 70 to swim across, or be a member.");
						return;
					}
					if(player.getSkillManager().getMaxLevel(Skill.CONSTITUTION) <= 700) {
						player.performGraphic(new Graphic(6, GraphicHeight.LOW));
						player.getPacketSender().sendMessage("@red@You don't have 70 Constitution, but as a member you can cross anyway.");
					}
					if(!player.getClickDelay().elapsed(1000))
						return;
					if(player.isCrossingObstacle())
						return;
					final String startMessage = "You jump into the icy cold water..";
					final String endMessage = "You climb out of the water safely.";
					final int jumpGFX = 68;
					final int jumpAnimation = 772;
					player.setSkillAnimation(773);
					player.setCrossingObstacle(true);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					player.performAnimation(new Animation(3067));
					final boolean goBack2 = player.getPosition().getY() >= 5344;
					player.getPacketSender().sendMessage(startMessage);  
					player.moveTo(new Position(2885, !goBack2 ? 5335 : 5342, 2));
					player.setDirection(goBack2 ? Direction.SOUTH : Direction.NORTH);
					player.performGraphic(new Graphic (jumpGFX));
					player.performAnimation(new Animation(jumpAnimation));
					TaskManager.submit(new Task(1, player, false) {
						int ticks = 0;
						@Override
						public void execute() {
							ticks++;
							player.getMovementQueue().walkStep(0, goBack2 ? -1 : 1);
							if(ticks >= 10)
								stop();
						}
						@Override
						public void stop() {
							player.setSkillAnimation(-1);
							player.setCrossingObstacle(false);
							player.getUpdateFlag().flag(Flag.APPEARANCE);
							player.getPacketSender().sendMessage(endMessage);
							player.moveTo(new Position(2885, player.getPosition().getY() < 5340 ? 5333 : 5345, 2));
							setEventRunning(false);
						}
					});
					player.getClickDelay().reset((System.currentTimeMillis() + 9000));
					break;
				case 26384:
					if(player.isCrossingObstacle())
						return;
					if(!player.getInventory().contains(2347) && !(player.getRights().isMember())) {
						player.getPacketSender().sendMessage("You need to have a hammer to bang on the door with.");
						return;
					}
					if(!player.getInventory().contains(2347) && (player.getRights().isMember())) {
						player.getPacketSender().sendMessage("@red@You don't have a hammer, but as a member you can enter anyway.");
						player.performGraphic(new Graphic(6, GraphicHeight.LOW));
					}
					if (player.getRights().isMember())
					player.setCrossingObstacle(true);
					final boolean goBack = player.getPosition().getX() <= 2850;
					player.performAnimation(new Animation(377));
					TaskManager.submit(new Task(2, player, false) {
						@Override
						public void execute() {
							player.moveTo(new Position(goBack ? 2851 : 2850, 5333, 2));
							player.setCrossingObstacle(false);
							stop();
						}
					});
					break;
				case 57211:
					player.getPacketSender().sendMessage("@red@Nobody is home. Please use the teleport under Modern Bosses to get to Nex.");
					break;
				case 26303:
					if(!player.getClickDelay().elapsed(1200))
						return;
					if (player.getSkillManager().getCurrentLevel(Skill.RANGED) < 70 && !(player.getRights().isMember()))
						player.getPacketSender().sendMessage("You need a Ranged level of at least 70 to swing across here.").sendMessage("Or, you can get membership for $10 and pass without the requirement.");
					else if (!player.getInventory().contains(9418) && !(player.getRights().isMember())) {
						player.getPacketSender().sendMessage("You need a Mithril grapple to swing across here. Explorer Jack might have one.").sendMessage("Or, you can get membership for $10 and pass without the requirement.");
						return;
					} else {
						if (player.getSkillManager().getCurrentLevel(Skill.RANGED) < 70) {
							player.getPacketSender().sendMessage("@red@You don't have 70 Ranged, but as a member you can enter anyway.");
							player.performGraphic(new Graphic(6, GraphicHeight.LOW));
						}
						if (!(player.getInventory().contains(9418))) {
							player.performGraphic(new Graphic(6, GraphicHeight.LOW));
							player.getPacketSender().sendMessage("@red@You don't have a Mith grapple, but as a member you can enter anyway.");
						}
						player.performAnimation(new Animation(789));
						TaskManager.submit(new Task(2, player, false) {
							@Override
							public void execute() {
								player.getPacketSender().sendMessage("You throw your Mithril grapple over the pillar and move across.");
								player.moveTo(new Position(2871, player.getPosition().getY() <= 5270 ? 5279 : 5269, 2));
								stop();
							}
						});
						player.getClickDelay().reset();
					}
					break;
				case 4493:
					if(player.getPosition().getX() >= 3432) {
						player.moveTo(new Position(3433, 3538, 1));
					}
					break;
				case 4494:
					player.moveTo(new Position(3438, 3538, 0));
					break;
				case 4495:
					player.moveTo(new Position(3417, 3541, 2));
					break;
				case 4496:
					player.moveTo(new Position(3412, 3541, 1));
					break;
				case 2491:
					player.setDialogueActionId(48);
					DialogueManager.start(player, 87);
					break;
				case 25339:
				case 25340:
					player.moveTo(new Position(1778, 5346, player.getPosition().getZ() == 0 ? 1 : 0));
					break;
				case 10229:
				case 10230:
					boolean up = id == 10229;
					player.performAnimation(new Animation(up ? 828 : 827));
					player.getPacketSender().sendMessage("You climb "+(up ? "up" : "down")+" the ladder..");
					TaskManager.submit(new Task(1, player, false) {
						@Override
						protected void execute() {
							player.moveTo(up ? new Position(1912, 4367) : new Position(2900, 4449));
							stop();
						}
					});
					break;
				case 1568:
					player.moveTo(new Position(3097, 9868));
					break;
				case 5103: //Brimhaven vines
				case 5104:
				case 5105:
				case 5106:
				case 5107:
					if(!player.getClickDelay().elapsed(4000))
						return;
					if(player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) < 30) {
						player.getPacketSender().sendMessage("You need a Woodcutting level of at least 30 to do this.");
						return;
					}
					if(WoodcuttingData.getHatchet(player) < 0) {
						player.getPacketSender().sendMessage("You do not have a hatchet which you have the required Woodcutting level to use.");
						return;
					}
					final Hatchet axe = Hatchet.forId(WoodcuttingData.getHatchet(player));
					player.performAnimation(new Animation(axe.getAnim()));
					gameObject.setFace(-1);
					TaskManager.submit(new Task(3 + Misc.getRandom(4), player, false) {
						@Override
						protected void execute() {
							if(player.getMovementQueue().isMoving()) {
								stop();
								return;
							}
							int x = 0;
							int y = 0;
							if(player.getPosition().getX() == 2689 && player.getPosition().getY() == 9564) {
								x = 2;
								y = 0;
							} else if(player.getPosition().getX() == 2691 && player.getPosition().getY() == 9564) {
								x = -2;
								y = 0;
							} else if(player.getPosition().getX() == 2683 && player.getPosition().getY() == 9568) {
								x = 0;
								y = 2;
							} else if(player.getPosition().getX() == 2683 && player.getPosition().getY() == 9570) {
								x = 0;
								y = -2;
							} else if(player.getPosition().getX() == 2674 && player.getPosition().getY() == 9479) {
								x = 2;
								y = 0;
							} else if(player.getPosition().getX() == 2676 && player.getPosition().getY() == 9479) {
								x = -2;
								y = 0;
							} else if(player.getPosition().getX() == 2693 && player.getPosition().getY() == 9482) {
								x = 2;
								y = 0;
							} else if(player.getPosition().getX() == 2672 && player.getPosition().getY() == 9499) {
								x = 2;
								y = 0;
							} else if(player.getPosition().getX() == 2674 && player.getPosition().getY() == 9499) {
								x = -2;
								y = 0;
							}
							CustomObjects.objectRespawnTask(player, new GameObject(-1, gameObject.getPosition().copy()), gameObject, 10);
							player.getPacketSender().sendMessage("You chop down the vines..");
							player.getSkillManager().addExperience(Skill.WOODCUTTING, 45);
							player.performAnimation(new Animation(65535));
							player.getMovementQueue().walkStep(x, y);
							stop();
						}
					});
					player.getClickDelay().reset();
					break;
				case 29942:
					if(player.getSkillManager().getCurrentLevel(Skill.SUMMONING) == player.getSkillManager().getMaxLevel(Skill.SUMMONING)) {
						player.getPacketSender().sendMessage("You do not need to recharge your Summoning points right now.");
						return;
					}
					player.performGraphic(new Graphic(1517));
					player.getSkillManager().setCurrentLevel(Skill.SUMMONING, player.getSkillManager().getMaxLevel(Skill.SUMMONING), true);
					player.getPacketSender().sendString(18045, " "+player.getSkillManager().getCurrentLevel(Skill.SUMMONING)+"/"+player.getSkillManager().getMaxLevel(Skill.SUMMONING));
					player.getPacketSender().sendMessage("You recharge your Summoning points.");
					break;
				case 57225:
					if(!player.getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()) {
						player.setDialogueActionId(44);
						DialogueManager.start(player, 79);
					} else {
						player.moveTo(new Position(2906, 5204));
						player.getMinigameAttributes().getGodwarsDungeonAttributes().setHasEnteredRoom(false);
					}
					break;
				case 26945:
					player.setDialogueActionId(41);
					DialogueManager.start(player, 75);
					break;
				case 9294:
					if(player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 80) {
						player.getPacketSender().sendMessage("You need an Agility level of at least 80 to use this shortcut.");
						return;
					}
					player.performAnimation(new Animation(769));
					TaskManager.submit(new Task(1, player, false) {
						@Override
						protected void execute() {
							player.moveTo(new Position(player.getPosition().getX() >= 2880 ? 2878 : 2880, 9813));	
							stop();
						}
					});
					break;
				case 9293:
					if (!player.getRights().isMember() && player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 70) {
						player.getPacketSender().sendMessage("You must have at least 70 Agility to use this shortcut.");
						return;
					}
					if (player.getRights().isMember() && player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 70) {
						player.getPacketSender().sendMessage("You do not have 70 Agility, but as a member you can pass anyway.");
					}
					boolean back = player.getPosition().getX() > 2888;
					player.moveTo(back ? new Position(2886, 9799) : new Position(2891, 9799));
					break;
				case 2320:
					back = player.getPosition().getY() == 9969 || player.getPosition().getY() == 9970;
					player.moveTo(back ? new Position(3120, 9963) : new Position(3120, 9969));
					break;
				case 1755:
					player.performAnimation(new Animation(828));
					player.getPacketSender().sendMessage("You climb the ladder..");
					TaskManager.submit(new Task(1, player, false) {
						@Override
						protected void execute() {
							if(gameObject.getPosition().getX() == 2547 && gameObject.getPosition().getY() == 9951) {
								player.moveTo(new Position(2548, 3551));
							} else if(gameObject.getPosition().getX() == 3005 && gameObject.getPosition().getY() == 10363) { 
								player.moveTo(new Position(3005, 3962));
							} else if(gameObject.getPosition().getX() == 3084 && gameObject.getPosition().getY() == 9672) {
								player.moveTo(new Position(3117, 3244));
							} else if(gameObject.getPosition().getX() == 3097 && gameObject.getPosition().getY() == 9867) {
								player.moveTo(new Position(3096, 3468));
							} else if(gameObject.getPosition().getX() == 3209 && gameObject.getPosition().getY() == 9616) {
								player.moveTo(new Position(3210, 3216));
							}
							stop();
						}
					});
					break;
				case 28742:
					player.performAnimation(new Animation(827));
					player.getPacketSender().sendMessage("You enter the trapdoor..");
					TaskManager.submit(new Task(1, player, false) {
						@Override
						protected void execute() {
							player.moveTo(new Position(3209, 9617));
							stop();
						}
					});
					break;
				case 5110:
					player.moveTo(new Position(2647, 9557));
					player.getPacketSender().sendMessage("You pass the stones..");
					break;
				case 5111:
					player.moveTo(new Position(2649, 9562));
					player.getPacketSender().sendMessage("You pass the stones..");
					break;
				case 6434:
					player.performAnimation(new Animation(827));
					player.getPacketSender().sendMessage("You enter the trapdoor..");
					TaskManager.submit(new Task(1, player, false) {
						@Override
						protected void execute() {
							player.moveTo(new Position(3085, 9672));
							stop();
						}
					});
					break;
				case 19187:
				case 19175:
					Hunter.dismantle(player, gameObject);
					break;
				case 25029:
					PuroPuro.goThroughWheat(player, gameObject);
					break;
				case 47976:
					Nomad.endFight(player, false);
					break;
				case 2182:
					if(!player.getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0)) {
						player.getPacketSender().sendMessage("You have no business with this chest. Talk to the Gypsy first!");
						return;
					}
					RecipeForDisaster.openRFDShop(player);
					break;
				case 12356:
					if(!player.getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0)) {
						player.getPacketSender().sendMessage("You have no business with this portal. Talk to the Gypsy first!");
						return;
					}
					if(player.getPosition().getZ() > 0) {
						RecipeForDisaster.leave(player);
					} else {
						player.getMinigameAttributes().getRecipeForDisasterAttributes().setPartFinished(1, true);
						RecipeForDisaster.enter(player);
					}
					break;
				case 9369:
					if (player.getPosition().getY() > 5175) {
						FightPit.addPlayer(player);
					} else {
						FightPit.removePlayer(player, "leave room");
					}
					break;
				case 9368:
					if (player.getPosition().getY() < 5169) {
						FightPit.removePlayer(player, "leave game");
					}
					break;
				case 9357:
					FightCave.leaveCave(player, false);
					break;
				case 9356:
					FightCave.enterCave(player);
					break;
				case 6704:
					player.moveTo(new Position(3577, 3282, 0));
					break;
				case 6706:
					player.moveTo(new Position(3554, 3283, 0));
					break;
				case 6705:
					player.moveTo(new Position(3566, 3275, 0));
					break;
				case 6702:
					player.moveTo(new Position(3564, 3289, 0));
					break;
				case 6703:
					player.moveTo(new Position(3574, 3298, 0));
					break;
				case 6707:
					player.moveTo(new Position(3556, 3298, 0));
					break;
				case 3203:
					if(player.getLocation() == Location.DUEL_ARENA && player.getDueling().duelingStatus == 5) {
						if(Dueling.checkRule(player, DuelRule.NO_FORFEIT)) {
							player.getPacketSender().sendMessage("Forfeiting has been disabled in this duel.");			
							return;
						}
						player.getCombatBuilder().reset(true);
						if(player.getDueling().duelingWith > -1) {
							Player duelEnemy = World.getPlayers().get(player.getDueling().duelingWith);
							if(duelEnemy == null)
								return;
							duelEnemy.getCombatBuilder().reset(true);
							duelEnemy.getMovementQueue().reset();
							duelEnemy.getDueling().duelVictory();
						}
						player.moveTo(new Position(3368 + Misc.getRandom(5), 3267+ Misc.getRandom(3), 0));
						player.getDueling().reset();
						player.getCombatBuilder().reset(true);
						player.restart();
					}
					break;
				case 14315:
					PestControl.boardBoat(player);
					break;
				case 14314:
					if(player.getLocation() == Location.PEST_CONTROL_BOAT) {
						player.getLocation().leave(player);
					}
					break;
				case 2145:
					player.getPacketSender().sendMessage("There's no good reason to disturb that.");
					break;
				case 1738:
					if(gameObject.getPosition().getX() == 3204 && gameObject.getPosition().getY() == 3207 && player.getPosition().getZ() == 0) {
						player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 1));
					} else if (player.getLocation() == Location.WARRIORS_GUILD){
						player.moveTo(new Position(2840, 3539, 2));
					}
					break;
				case 1739:
					if(player.getLocation() == Location.LUMBRIDGE) {
						//player.moveTo(teleportTarget)
						//player.setDialogueActionId(154);
						//DialogueManager.start(player, 154);
						player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 2));
					}
				case 15638:
					if (player.getLocation() == Location.WARRIORS_GUILD) {
						player.moveTo(new Position(2840, 3539, 0));
					}
					break;
				case 1740:
					if(player.getLocation() == Location.LUMBRIDGE) {
						player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 1));
					}
					break;
				case 15644:
				case 15641:
					switch(player.getPosition().getZ()) {
					case 0:
						player.moveTo(new Position(2855, player.getPosition().getY() >= 3546 ? 3545 : 3546));
						break;
					case 2:
						if(player.getPosition().getX() == 2846) {
							if(player.getInventory().getAmount(8851) < 70) {
								player.getPacketSender().sendMessage("You need at least 70 tokens to enter this area.");
								return;
							}
							DialogueManager.start(player, WarriorsGuild.warriorsGuildDialogue(player));
							player.moveTo(new Position(2847, player.getPosition().getY(), 2));
							WarriorsGuild.handleTokenRemoval(player);
						} else if(player.getPosition().getX() == 2847) {
							WarriorsGuild.resetCyclopsCombat(player);
							player.moveTo(new Position(2846, player.getPosition().getY(), 2));
							player.getMinigameAttributes().getWarriorsGuildAttributes().setEnteredTokenRoom(false);
						}
						break;
					}
					break;
				case 28714:
					player.performAnimation(new Animation(828));
					player.delayedMoveTo(new Position(3674, 2980), 2);
					break;
				case 26933:
					player.performAnimation(new Animation(827));
					player.delayedMoveTo(new Position(3096, 9867), 2);
					break;
				case 1746:
					player.performAnimation(new Animation(827));
					player.delayedMoveTo(new Position(2209, 5348), 2);
					break;
				case 19191:
				case 19189:
				case 19180:
				case 19184:
				case 19182:
				case 19178:
					Hunter.lootTrap(player, gameObject);
					break;
				case 13493:
					if(!player.getRights().isMember()) {
						player.getPacketSender().sendMessage("You must be a Member to use this.");
						return;
					}
					double c = Math.random()*100;
					int reward = c >= 70 ? 13003 : c >= 45 ? 4131 : c >= 35 ? 1113 : c >= 25 ? 1147 : c >= 18 ? 1163 : c >= 12 ? 1079 : c >= 5 ? 1201 : 1127;
					Stalls.stealFromStall(player, 95, 121, reward, "You stole some rune equipment.");
					break;
				case 30205:
					player.setDialogueActionId(11);
					DialogueManager.start(player, 20);
					break;
				case 28716:
					if(!player.busy()) {
						player.getSkillManager().updateSkill(Skill.SUMMONING);
						player.getPacketSender().sendInterface(63471);
					} else
						player.getPacketSender().sendMessage("Please finish what you're doing before opening this.");
					break;
				case 6:
					DwarfCannon cannon = player.getCannon();
					if (cannon == null || cannon.getOwnerIndex() != player.getIndex()) {
						player.getPacketSender().sendMessage("This is not your cannon!");
					} else {
						DwarfMultiCannon.startFiringCannon(player, cannon);
					}
					break;
				case 2:
					player.moveTo(new Position(player.getPosition().getX() > 2690 ? 2687 : 2694, 3714));
					player.getPacketSender().sendMessage("You walk through the entrance..");
					break;
				case 2026:
				case 2028:
				case 2029:
				case 2030:
				case 2031:
					player.setEntityInteraction(gameObject);
					Fishing.setupFishing(player, Fishing.forSpot(gameObject.getId(), false));
					return;
				case 12692:
				case 2783:
				case 4306:
					player.setInteractingObject(gameObject);
					EquipmentMaking.handleAnvil(player);
					break;
				case 2732:
				case 11404:
		        case 11406:
		        case 11405:
		        case 20000:
		        case 20001:
					EnterAmountOfLogsToAdd.openInterface(player);
					break;
				case 409:
				case 27661:
				case 2640:
				case 36972:
					player.performAnimation(new Animation(645));
					if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager().getMaxLevel(Skill.PRAYER)) {
						player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
						player.getPacketSender().sendMessage("You recharge your Prayer points.");
					}
					break;
				case 8749:
					boolean restore = player.getSpecialPercentage() < 100;
					if(restore) {
						player.setSpecialPercentage(100);
						CombatSpecial.updateBar(player);
						player.getPacketSender().sendMessage("Your special attack energy has been restored.");
					}
					for(Skill skill : Skill.values()) {
						int increase = skill != Skill.PRAYER && skill != Skill.CONSTITUTION && skill != Skill.SUMMONING ? 19 : 0;
						if(player.getSkillManager().getCurrentLevel(skill) < (player.getSkillManager().getMaxLevel(skill) + increase))
							player.getSkillManager().setCurrentLevel(skill, (player.getSkillManager().getMaxLevel(skill) + increase));
					}
					player.performGraphic(new Graphic(1302));
					player.getPacketSender().sendMessage("Your stats have received a major buff.");
					break;
				case 4859:
					player.performAnimation(new Animation(645));
					if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager().getMaxLevel(Skill.PRAYER)) {
						player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
						player.getPacketSender().sendMessage("You recharge your Prayer points.");
					}
					break;
				case 411:
					if(player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 30) {
						player.getPacketSender().sendMessage("You need a Defence level of at least 30 to use this altar.");
						return;
					}
					player.performAnimation(new Animation(645));
					if(player.getPrayerbook() == Prayerbook.NORMAL) {
						player.getPacketSender().sendMessage("You sense a surge of power flow through your body!");
						player.setPrayerbook(Prayerbook.CURSES);
					} else {
						player.getPacketSender().sendMessage("You sense a surge of purity flow through your body!");
						player.setPrayerbook(Prayerbook.NORMAL);
					}
					player.getPacketSender().sendTabInterface(GameSettings.PRAYER_TAB, player.getPrayerbook().getInterfaceId());
					PrayerHandler.deactivateAll(player);
					CurseHandler.deactivateAll(player);
					break;
				case 6552:
					player.performAnimation(new Animation(645));
					player.setSpellbook(player.getSpellbook() == MagicSpellbook.ANCIENT ? MagicSpellbook.NORMAL : MagicSpellbook.ANCIENT);
					player.getPacketSender().sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId()).sendMessage("Your magic spellbook is changed..");
					Autocasting.resetAutocast(player, true);
					break;
				case 410:
					if(player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 40) {
						player.getPacketSender().sendMessage("You need a Defence level of at least 40 to use this altar.");
						return;
					}
					player.performAnimation(new Animation(645));
					player.setSpellbook(player.getSpellbook() == MagicSpellbook.LUNAR ? MagicSpellbook.NORMAL : MagicSpellbook.LUNAR);
					player.getPacketSender().sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId()).sendMessage("Your magic spellbook is changed..");;
					Autocasting.resetAutocast(player, true);
					break;
				case 452:
					player.getPacketSender().sendMessage("There's no ore in that rock.");
					break;
				case 172:
					CrystalChest.handleChest(player, gameObject, false);
					break;
				case 6910:
				case 4483:
				case 3193:
				case 2213:
				case 11758:
				case 14367:
				case 42192:
				case 75:
				case 26972:
				case 11338:
				case 19230:
					player.getBank(player.getCurrentBankTab()).open();
					break;
				case 11666:
					Smelting.openInterface(player);
					break;
				}
			}
		}));
	}

	private static void secondClick(final Player player, Packet packet) {
		final int id = packet.readLEShortA();
		final int y = packet.readLEShort();
		final int x = packet.readUnsignedShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final GameObject gameObject = new GameObject(id, position);
		if(id > 0 && id != 6 && !RegionClipping.objectExists(gameObject)) {
			//player.getPacketSender().sendMessage("An error occured. Error code: "+id).sendMessage("Please report the error to a staff member.");
			return;
		}
		player.setPositionToFace(gameObject.getPosition());
		int distanceX = (player.getPosition().getX() - position.getX());
		int distanceY = (player.getPosition().getY() - position.getY());
		if (distanceX < 0)
			distanceX = -(distanceX);
		if (distanceY < 0)
			distanceY = -(distanceY);
		int size = distanceX > distanceY ? distanceX : distanceY;
		gameObject.setSize(size);
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Second click object id; [id, position] : [" + id + ", " + position.toString() + "]");
		player.setInteractingObject(gameObject).setWalkToTask(new WalkToTask(player, position, gameObject.getSize(), new FinalizedMovementTask() {
			public void execute() {
				if(MiningData.forRock(gameObject.getId()) != null) {
					Prospecting.prospectOre(player, id);
					return;
				}
				if (player.getFarming().click(player, x, y, 1))
					return;
				switch(gameObject.getId()) {
				case 2145:
					player.getPacketSender().sendMessage("Eww. That's a terrible idea!");
					break;
				case 1739:
					if(player.getLocation() == Location.LUMBRIDGE) {
						player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 0));
					}
					break;
				case 6910:
				case 4483:
				case 3193:
				case 2213:
				case 11758:
				case 14367:
				case 42192:
				case 75:
				case 26972:
				case 11338:
				case 19230:
					player.getBank(player.getCurrentBankTab()).open();
					break;
				case 26945:
					player.setDialogueActionId(41);
					player.setInputHandling(new DonateToWell());
					player.getPacketSender().sendInterfaceRemoval().sendEnterAmountPrompt("How much money would you like to contribute with?");
					break;
				case 2646:
				case 312:
					if(!player.getClickDelay().elapsed(1200))
						return;
					if(player.getInventory().isFull()) {
						player.getPacketSender().sendMessage("You don't have enough free inventory space.");
						return;
					}
					String type = gameObject.getId() == 312 ? "Potato" : "Flax";
					player.performAnimation(new Animation(827));
					player.getInventory().add(gameObject.getId() == 312 ? 1942 : 1779, 1);
					player.getPacketSender().sendMessage("You pick some "+type+"..");
					gameObject.setPickAmount(gameObject.getPickAmount() + 1);
					if(Misc.getRandom(3) == 1 && gameObject.getPickAmount() >= 1 || gameObject.getPickAmount() >= 6) {
						player.getPacketSender().sendClientRightClickRemoval();
						gameObject.setPickAmount(0);
						CustomObjects.globalObjectRespawnTask(new GameObject(-1, gameObject.getPosition()), gameObject, 10);
					}
					player.getClickDelay().reset();
					break;
				case 2644:
					Flax.showSpinInterface(player);
					break;
				case 6:
					DwarfCannon cannon = player.getCannon();
					if (cannon == null || cannon.getOwnerIndex() != player.getIndex()) {
						player.getPacketSender().sendMessage("This is not your cannon!");
					} else {
						DwarfMultiCannon.pickupCannon(player, cannon, false);
					}
					break;
				case 5917: //friday the 13th event
					Stalls.stealFromStall(player, 1, 0, 13150, "You search the Plasma Vent... and find a Spooky Box!");
					break;
				case 4875:
					Stalls.stealFromStall(player, 1, 13, 18199, "You steal a banana.");
					break;
				case 4874:
					Stalls.stealFromStall(player, 30, 34, 15009, "You steal a golden ring.");
					break;
				case 4876:
					Stalls.stealFromStall(player, 60, 57, 17401, "You steal a damaged hammer.");
					break;
				case 4877:
					Stalls.stealFromStall(player, 65, 80, 1389, "You steal a staff.");
					break;
				case 4878:
					Stalls.stealFromStall(player, 80, 101, 11998, "You steal a scimitar.");
					break;
				case 3044:
				case 6189:
				case 26814:
				case 11666:
					Jewelry.jewelryInterface(player);
					break;
				case 2152:
					player.performAnimation(new Animation(8502));
					player.performGraphic(new Graphic(1308));
					player.getSkillManager().setCurrentLevel(Skill.SUMMONING, player.getSkillManager().getMaxLevel(Skill.SUMMONING));
					player.getPacketSender().sendMessage("You renew your Summoning points.");
					break;
				}
			}
		}));
	}

	private static void thirdClick(Player player, Packet packet) {
		
	}

	private static void fourthClick(Player player, Packet packet) {
		
	}

	private static void fifthClick(final Player player, Packet packet) {
		final int id = packet.readUnsignedShortA();
		final int y = packet.readUnsignedShortA();
		final int x = packet.readShort();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final GameObject gameObject = new GameObject(id, position);
		if(!Construction.buildingHouse(player)) {
			if(id > 0 && !RegionClipping.objectExists(gameObject)) {
				//player.getPacketSender().sendMessage("An error occured. Error code: "+id).sendMessage("Please report the error to a staff member.");
				return;
			}
		}
		player.setPositionToFace(gameObject.getPosition());
		int distanceX = (player.getPosition().getX() - position.getX());
		int distanceY = (player.getPosition().getY() - position.getY());
		if (distanceX < 0)
			distanceX = -(distanceX);
		if (distanceY < 0)
			distanceY = -(distanceY);
		int size = distanceX > distanceY ? distanceX : distanceY;
		gameObject.setSize(size);
		if(player.getRights() == PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage("Third click object id; [id, position] : [" + id + ", " + position.toString() + "]");
		}
		player.setInteractingObject(gameObject);
		player.setWalkToTask(new WalkToTask(player, position, gameObject.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				switch(id) {
				}
				Construction.handleFifthObjectClick(x, y, id, player);
			}
		}));
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if(player.isTeleporting() || player.isPlayerLocked() || player.getMovementQueue().isLockMovement())
			return;
		switch (packet.getOpcode()) {
		case FIRST_CLICK:
			firstClick(player, packet);
			if (player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("1st click obj");
			}
			break;
		case SECOND_CLICK:
			secondClick(player, packet);
			if (player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("2nd click obj");
			}
			break;
		case THIRD_CLICK:
			if (player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("3rd click obj. no handler.");
			}
			//thirdClick(player, packet);
			break;
		case FOURTH_CLICK:
			if (player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("4th click obj. no handler");
			}
			//fourthClick(player, packet);
			break;
		case FIFTH_CLICK:
			fifthClick(player, packet);
			if (player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("5th click obj");
			}
			break;
		}
	}

	public static final int FIRST_CLICK = 132, SECOND_CLICK = 252, THIRD_CLICK = 70, FOURTH_CLICK = 234, FIFTH_CLICK = 228;
}
