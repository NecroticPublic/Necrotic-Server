package com.ruse.net.packet.impl;

import com.ruse.GameSettings;
import com.ruse.engine.task.impl.WalkToTask;
import com.ruse.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.ruse.model.GameMode;
import com.ruse.model.Graphic;
import com.ruse.model.Locations.Location;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Shop.ShopManager;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.EnergyHandler;
import com.ruse.world.content.LoyaltyProgramme;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.magic.CombatSpell;
import com.ruse.world.content.combat.magic.CombatSpells;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.grandexchange.GrandExchange;
import com.ruse.world.content.holidayevents.christmas2016;
import com.ruse.world.content.minigames.impl.WarriorsGuild;
import com.ruse.world.content.minigames.impl.trioMinigame;
import com.ruse.world.content.skill.impl.construction.ConstructionActions;
import com.ruse.world.content.skill.impl.crafting.Tanning;
import com.ruse.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.ruse.world.content.skill.impl.dungeoneering.UltimateIronmanHandler;
import com.ruse.world.content.skill.impl.fishing.Fishing;
import com.ruse.world.content.skill.impl.herblore.Decanting;
import com.ruse.world.content.skill.impl.hunter.PuroPuro;
import com.ruse.world.content.skill.impl.runecrafting.DesoSpan;
import com.ruse.world.content.skill.impl.slayer.SlayerDialogues;
import com.ruse.world.content.skill.impl.slayer.SlayerMaster;
import com.ruse.world.content.skill.impl.slayer.SlayerTasks;
import com.ruse.world.content.skill.impl.summoning.BossPets;
import com.ruse.world.content.skill.impl.summoning.Summoning;
import com.ruse.world.content.skill.impl.summoning.SummoningData;
import com.ruse.world.content.skill.impl.thieving.Pickpocket;
import com.ruse.world.content.skill.impl.thieving.PickpocketData;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class NPCOptionPacketListener implements PacketListener {


	private static void firstClick(Player player, Packet packet) {
		int index = packet.readLEShort();
		if(index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		player.setEntityInteraction(npc);
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("First click npc id: "+npc.getId());
		if(BossPets.pickup(player, npc)) {
			player.getMovementQueue().reset();
			return;
		}
		player.setWalkToTask(new WalkToTask(player, npc.getPosition(), npc.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				if(SummoningData.beastOfBurden(npc.getId())) {
					Summoning summoning = player.getSummoning();
					if(summoning.getBeastOfBurden() != null && summoning.getFamiliar() != null && summoning.getFamiliar().getSummonNpc() != null && summoning.getFamiliar().getSummonNpc().getIndex() == npc.getIndex()) {
						summoning.store();
						player.getMovementQueue().reset();
					} else {
						player.getPacketSender().sendMessage("That familiar is not yours!");
					}
					return;
				}
				if(ConstructionActions.handleFirstClickNpc(player, npc)) {
					return;
				}
				switch(npc.getId()) {
				case 5382:
					if (player.getGameMode().equals(GameMode.ULTIMATE_IRONMAN)) {
						DialogueManager.start(player, 192);
					} else {
						DialogueManager.start(player, 195);
					}
					break;
				case 659:
					if (GameSettings.newYear2017) {
						if (player.getNewYear2017() == 0) {
							DialogueManager.start(player, 189);
							player.setDialogueActionId(189);
						} else {
							DialogueManager.start(player, 190);
						}
					} else {
						npc.forceChat("I love a good party!");
					}
					break;
					case 1835:
						if (Misc.easter(2017)) {
							if (player.getInventory().getFreeSlots() < 2) {
								player.getPacketSender().sendMessage("He might give me something, I should have at least 2 free inventory spaces.");
								return;
							}
							if (player.getEaster2017() >= 1 && player.getEaster2017() < 7 ) {
								player.getPacketSender().sendMessage("The Bunny gives another letter to you.");
								player.getInventory().add(22051, 1);
							} else if(player.getEaster2017() == 8) {
								player.getPacketSender().sendMessage("He's recovering from the easter event!");
							} else if(player.getEaster2017() == 7) {
								DialogueManager.start(player, 208);
								player.setDialogueActionId(211);
							} else {
								DialogueManager.start(player, 203);
								player.setDialogueActionId(210);
							}
						} else {
							player.getPacketSender().sendMessage("He's recovering from the easter event!");
						}
						break;
				case 4653:
					DialogueManager.start(player, 178);
					player.setDialogueActionId(178);
					break;
				case 1872:
					if (player.getLocation() == Location.ZULRAH_WAITING) {
						DialogueManager.start(player, 200);
					}
					break;
				case 1552:
					if (christmas2016.isChristmas()) {
						if (player.getChristmas2016() == 0) {
							DialogueManager.start(player, 169);
							player.setDialogueActionId(171);
						} else if (player.getChristmas2016() == 1) {
							player.getPacketSender().sendMessage("Santa wants me to talk to Explorer Jack at home.");
						} else if (player.getChristmas2016() == 2) {
							DialogueManager.start(player, 181);
						} else if (player.getChristmas2016() > 2 && player.getChristmas2016() < 5) {
							DialogueManager.start(player, 182);
							player.getPacketSender().sendMessage("The Reindeer need Law, Cosmic, and Nature runes.");
						} else if (player.getChristmas2016() == 5) {
							DialogueManager.start(player, 183);
							player.getPacketSender().sendMessage("I should \"use\" the Mind Bomb on Santa.");
						} else if (player.getChristmas2016() == 6) {
							DialogueManager.start(player, 184);
							player.setDialogueActionId(187);
						} else if (player.getChristmas2016() == 7) {
							DialogueManager.start(player, 188);
						} else { 
							npc.forceChat("Ho ho ho!");
						}
					} else {
						npc.forceChat("Ho ho ho!");
					}
					break;
				case 736:
					player.forceChat("Ban emily!");
					npc.forceChat("Mods! Help! They're harassing me again!");
					break;
				case 3777:
					DialogueManager.start(player, 141);
					player.setDialogueActionId(88);
					break;
				case 5:
				case 4:
					npc.setPositionToFace(player.getPosition());
					DialogueManager.start(player, 167);
					break;
				case 1:
				case 2:
				case 3:
					npc.setPositionToFace(player.getPosition());
					DialogueManager.start(player, 165);
					break;
				case 2238:
					npc.setPositionToFace(player.getPosition());
					DialogueManager.start(player, 155);
					break;
				case 1152:
					DialogueManager.start(player, 127);
					player.setDialogueActionId(79);
					break;
				case 1837:
					DialogueManager.start(player, 144);
					player.setDialogueActionId(99);
					break;
				case 457:
					DialogueManager.start(player, 117);
					player.setDialogueActionId(74);
					break;
				case 8710:
				case 8707:
				case 8706:
				case 8705:
					EnergyHandler.rest(player);
					break;
				case 534:
					ShopManager.getShops().get(78).open(player);
					break;
				case 947:
					if(player.getPosition().getX() >= 3092) {
						player.getMovementQueue().reset();
						GrandExchange.open(player);
					}
					break;
				case 11226:
					if(Dungeoneering.doingDungeoneering(player)) {
						ShopManager.getShops().get(45).open(player);
					}
					break;
				case 9713:
					DialogueManager.start(player, 107);
					player.setDialogueActionId(69);
					break;
				case 2622:
					ShopManager.getShops().get(43).open(player);
					break;
				case 3101:
					DialogueManager.start(player, 90);
					player.setDialogueActionId(57);
					break;
				case 7969:
					//player.getPacketSender().sendMessage("yayayaya i am lord");
					if (christmas2016.isChristmas() == false || player.getChristmas2016() == 0) {
						ShopManager.getShops().get(28).open(player);
						return;
					} else if (player.getChristmas2016() == 1) {
						//player.getPacketSender().sendMessage("dialogue 173");
						DialogueManager.start(player, 173);
						player.setDialogueActionId(173);
					} else if (player.getChristmas2016() == 2) {
						DialogueManager.start(player, 173);
						player.setDialogueActionId(505050);
					}
					//DialogueManager.start(player, ExplorerJack.getDialogue(player));
					break;
				case 1597:
					SlayerMaster.changeSlayerMaster(player, SlayerMaster.VANNAKA);
					DialogueManager.start(player, SlayerDialogues.dialogue(player));
					break;
				case 8275:
					SlayerMaster.changeSlayerMaster(player, SlayerMaster.DURADEL);
					DialogueManager.start(player, SlayerDialogues.dialogue(player));
					break;
				case 9085:
					SlayerMaster.changeSlayerMaster(player, SlayerMaster.KURADEL);
					DialogueManager.start(player, SlayerDialogues.dialogue(player));
					break;
				case 7780:
					SlayerMaster.changeSlayerMaster(player, SlayerMaster.SUMONA);
					/*if(npc.getId() != player.getSlayer().getSlayerMaster().getNpcId()) {
						player.getPacketSender().sendMessage(player.getSlayer().getSlayerMaster().toString() + " is your current slayer master. Change it at the Slayer Instructor.");
						player.getSlayer().setSlayerMaster(SlayerMaster.SUMONA);
						player.getPacketSender().sendMessage("Master should be Vannaka");
						//return;
					}*/
					DialogueManager.start(player, SlayerDialogues.dialogue(player));
					break;
				case 437:
					DialogueManager.start(player, 99);
					player.setDialogueActionId(58);
					break;
				case 5112:
					ShopManager.getShops().get(38).open(player);
					break;
				case 8591:
					//player.nomadQuest[0] = player.nomadQuest[1] = player.nomadQuest[2] = false;
					if(!player.getMinigameAttributes().getNomadAttributes().hasFinishedPart(0)) {
						DialogueManager.start(player, 48);
						player.setDialogueActionId(23);
					} else if(player.getMinigameAttributes().getNomadAttributes().hasFinishedPart(0) && !player.getMinigameAttributes().getNomadAttributes().hasFinishedPart(1)) {
						DialogueManager.start(player, 50);
						player.setDialogueActionId(24);
					} else if(player.getMinigameAttributes().getNomadAttributes().hasFinishedPart(1))
						DialogueManager.start(player, 53);
					break;
				case 273:
					DialogueManager.start(player, 61);
					player.setDialogueActionId(28);
					break;
				case 3385:
					if(player.getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0) && player.getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() < 6) {
						DialogueManager.start(player, 39);
						return;
					}
					if(player.getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() == 6) {
						DialogueManager.start(player, 46);
						return;
					}
					DialogueManager.start(player, 38);
					player.setDialogueActionId(20);
					break;
				case 6139:
					DialogueManager.start(player, 29);
					player.setDialogueActionId(17);
					break;
				case 3789:
					player.getPacketSender().sendInterface(18730);
					player.getPacketSender().sendString(18729, "Commendations: "+Integer.toString(player.getPointsHandler().getCommendations()));
					break;
				case 2948:
					DialogueManager.start(player, WarriorsGuild.warriorsGuildDialogue(player));
					break;
				case 650:
					ShopManager.getShops().get(35).open(player);
					break;
				case 6055:
				case 6056:
				case 6057:
				case 6058:
				case 6059:
				case 6060:
				case 6061:
				case 6062:
				case 6063:
				case 6064:
				case 7903:
					if(npc.getId() == 7903 && player.getLocation() == Location.MEMBER_ZONE) {
						if(!player.getRights().isMember()) {
							player.getPacketSender().sendMessage("You must be a Member to use this.");
							return;
						}
					}
					PuroPuro.catchImpling(player, npc);
					break;
				case 8022:
				case 8028:
					DesoSpan.siphon(player, npc);
					break;
				case 2579:
					player.setDialogueActionId(13);
					DialogueManager.start(player, 24);
					break;
				case 6537:
					player.setDialogueActionId(10);
					DialogueManager.start(player, 19);
					break;
				case 4249:
					player.setDialogueActionId(9);
					DialogueManager.start(player, 64);
					break;
				case 6807:
				case 6994:
				case 6995:
				case 6867:
				case 6868:
				case 6794:
				case 6795:
				case 6815:
				case 6816:
				case 6874:
				case 6873:
				case 3594:
				case 3590:
				case 3596:
					if(player.getSummoning().getFamiliar() == null || player.getSummoning().getFamiliar().getSummonNpc() == null || player.getSummoning().getFamiliar().getSummonNpc().getIndex() != npc.getIndex()) {
						player.getPacketSender().sendMessage("That is not your familiar.");
						return;
					}
					player.getSummoning().store();
					break;
				case 605:
					player.setDialogueActionId(8);
					DialogueManager.start(player, 13);
					break;
				case 6970:
					player.setDialogueActionId(3);
					DialogueManager.start(player, 3);
					break;
				case 318:
				case 316:
				case 313:
				case 312:
					player.setEntityInteraction(npc);
					Fishing.setupFishing(player, Fishing.forSpot(npc.getId(), false));
					break;
				case 805:
					if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
						ShopManager.getShops().get(97).open(player);
						return;
					}
					ShopManager.getShops().get(34).open(player);
					break;
				case 462:
					ShopManager.getShops().get(33).open(player);
					break;
				case 461:
					ShopManager.getShops().get(32).open(player);
					break;
				case 8444:
					if(!player.getRights().isMember()) {
						player.getPacketSender().sendMessage("You must be a Member to use this.");
						return;
					}
					ShopManager.getShops().get(31).open(player);
					break;
				case 8459:
					if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
						ShopManager.getShops().get(94).open(player);
						return;
					}
					ShopManager.getShops().get(30).open(player);
					break;
				case 3299:
					ShopManager.getShops().get(21).open(player);
					break;
				case 548:
					ShopManager.getShops().get(20).open(player);
					break;
				case 1685:
					ShopManager.getShops().get(19).open(player);
					break;
				case 308:
					if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
						ShopManager.getShops().get(93).open(player);
						return;
					}
					ShopManager.getShops().get(18).open(player);
					break;
				case 802:
					ShopManager.getShops().get(17).open(player);
					break;
				case 970:
					ShopManager.getShops().get(81).open(player);
					break;
				case 278:
					ShopManager.getShops().get(16).open(player);
					break;
				case 4946:
					if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
						ShopManager.getShops().get(98).open(player);
						return;
					}
					ShopManager.getShops().get(15).open(player);
					break;
				case 948:
					if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
						ShopManager.getShops().get(95).open(player);
						return;
					}
					ShopManager.getShops().get(13).open(player);
					break;
				case 4906:
					if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
						ShopManager.getShops().get(96).open(player);
						return;
					}
					ShopManager.getShops().get(14).open(player);
					break;
				case 520:
				case 521:
					ShopManager.getShops().get(12).open(player);
					break;
				case 2292:
					ShopManager.getShops().get(11).open(player);
					break;
				case 28:
					ShopManager.getShops().get(83).open(player);
					break;
				case 2676:
					player.getPacketSender().sendInterface(3559);
					player.getAppearance().setCanChangeAppearance(true);
					break;
				case 519:
					ShopManager.getShops().get(84).open(player);
					break;
				case 494:
				case 1360:
					player.getBank(player.getCurrentBankTab()).open();
					break;
				}
				if(!(npc.getId() >= 8705 && npc.getId() <= 8710)) {
					npc.setPositionToFace(player.getPosition());
				}
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	private static void attackNPC(Player player, Packet packet) {
		int index = packet.readShortA();
		if(index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC interact = World.getNpcs().get(index);
		if (interact == null)
			return;

		if (!NpcDefinition.getDefinitions()[interact.getId()].isAttackable()) {
			return;
		}

		if(interact.getConstitution() <= 0) {
			player.getMovementQueue().reset();
			return;
		}

		if(player.getCombatBuilder().getStrategy() == null) {
			player.getCombatBuilder().determineStrategy();
		}
		if (CombatFactory.checkAttackDistance(player, interact)) {
			player.getMovementQueue().reset();
		}
		if (UltimateIronmanHandler.hasItemsStored(player) && player.getLocation() != Location.DUNGEONEERING) {
			player.getPacketSender().sendMessage("You must claim your stored items at Dungeoneering first.");
			player.getMovementQueue().reset();
			return;
		}

		player.getCombatBuilder().attack(interact);
	}

	public void handleSecondClick(Player player, Packet packet) {
		int index = packet.readLEShortA();
		if(index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if(npc == null)
			return;
		player.setEntityInteraction(npc);
		final int npcId = npc.getId();
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Second click npc id: "+npcId);
		player.setWalkToTask(new WalkToTask(player, npc.getPosition(), npc.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				if (PickpocketData.forNpc(npc.getId()) != null) {
					Pickpocket.handleNpc(player, npc);
					return;
				}
				//if ()
				switch(npc.getId()) {
				case 8459:
					Decanting.notedDecanting(player);
					break;
				case 5382:
					if (player.getGameMode().equals(GameMode.ULTIMATE_IRONMAN)) {
						UltimateIronmanHandler.handleQuickStore(player);
					} else {
						DialogueManager.start(player, 195);
					}
					
					player.getClickDelay().reset();
					break;
				case 4653:
					player.getPacketSender().sendInterfaceRemoval();
					ShopManager.getShops().get(85).open(player);
				break;
				case 736:
					npc.forceChat("Thanx for the follow :)");
					break;
				case 1837:
					player.getPacketSender().sendInterfaceRemoval();
					if(player.getInventory().getAmount(11180) < 1) {
						player.getPacketSender().sendMessage("You do not have enough tokens.");
						return;
					} else
						player.getInventory().delete(11180, 1);
						// So  we grab the players pID too determine what Z they will be getting. Not sure how kraken handles it, but this is how we'll handle it.
					    player.moveTo(new Position(3025, 5231));
						//player.getPacketSender().sendMessage("Index: " + player.getIndex());
						//player.getPacketSender().sendMessage("Z: " + player.getIndex() * 4);
						player.getPacketSender().sendMessage("Teleporting to Trio...");
						player.getPacketSender().sendMessage("@red@Warning:@bla@ you @red@will@bla@ lose your items on death here!");
						//Will sumbit a task to handle token remove, once they leave the minigame the task will be removed.
					//	trioMinigame.failsafe(player);
					//	trioMinigame.handleNPCSpawning(player);
						trioMinigame.handleTokenRemoval(player);

					break;
				case 3777:
					ShopManager.getShops().get(24).open(player);
					break;
				case 550:
					if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
						ShopManager.getShops().get(92).open(player);
						return;
					}
					ShopManager.getShops().get(2).open(player);
					break;
				case 2579:
					ShopManager.getShops().get(46).open(player);
					player.getPacketSender().sendMessage("<col=255>You currently have "+player.getPointsHandler().getPrestigePoints()+" Prestige points!");
					break;
				case 457:
					player.getPacketSender().sendMessage("The ghost teleports you away.");
					player.getPacketSender().sendInterfaceRemoval();
					player.moveTo(new Position(3651, 3486));
					break;
				case 2622:
					ShopManager.getShops().get(43).open(player);
					break;
				case 462:
					npc.performAnimation(CombatSpells.CONFUSE.getSpell().castAnimation().get());
					npc.forceChat("Off you go!");
					TeleportHandler.teleportPlayer(player,new Position(2911, 4832), player.getSpellbook().getTeleportType());
					break;
				case 3101:
					DialogueManager.start(player, 95);
					player.setDialogueActionId(57);
					break;
				case 7969:
					ShopManager.getShops().get(28).open(player);
					break;
				case 605:
					player.getPacketSender().sendMessage("").sendMessage("You currently have "+player.getPointsHandler().getVotingPoints()+" Voting points.").sendMessage("You can earn points and coins by voting. To do so, simply use the ::vote command.");;
					ShopManager.getShops().get(90).open(player);
					break;
				case 1597:
					SlayerMaster.changeSlayerMaster(player, SlayerMaster.VANNAKA);
					if(player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK)
						player.getSlayer().assignTask();
					else
						DialogueManager.start(player, SlayerDialogues.findAssignment(player));
					break;
				case 8275:
					SlayerMaster.changeSlayerMaster(player, SlayerMaster.DURADEL);
					if(player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK)
						player.getSlayer().assignTask();
					else
						DialogueManager.start(player, SlayerDialogues.findAssignment(player));
					break;
				case 9085:
					SlayerMaster.changeSlayerMaster(player, SlayerMaster.KURADEL);
					if(player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK)
						player.getSlayer().assignTask();
					else
						DialogueManager.start(player, SlayerDialogues.findAssignment(player));
					break;
				case 7780:
					SlayerMaster.changeSlayerMaster(player, SlayerMaster.SUMONA);
					if(player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK)
						player.getSlayer().assignTask();
					else
						DialogueManager.start(player, SlayerDialogues.findAssignment(player));
					break;
				case 8591:
					if(!player.getMinigameAttributes().getNomadAttributes().hasFinishedPart(1)) {
						player.getPacketSender().sendMessage("You must complete Nomad's quest before being able to use this shop.");
						return;
					}
					ShopManager.getShops().get(37).open(player);
					break;
				case 805:
					Tanning.selectionInterface(player);
					break;
				case 318:
				case 316:
				case 313:
				case 312:
					player.setEntityInteraction(npc);
					Fishing.setupFishing(player, Fishing.forSpot(npc.getId(), true));
					break;
				case 4946:
					ShopManager.getShops().get(15).open(player);
					break;
				case 946:
					ShopManager.getShops().get(1).open(player);
					break;
				case 961:
					ShopManager.getShops().get(6).open(player);
					break;
				case 1861:
						if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
							ShopManager.getShops().get(92).open(player);
							return;
						}
						ShopManager.getShops().get(3).open(player);
						break;
				case 705:
					ShopManager.getShops().get(4).open(player);
					break;
				case 2253:
					ShopManager.getShops().get(9).open(player);
					break;
				case 6970:
					player.setDialogueActionId(35);
					DialogueManager.start(player, 63);
					break;

				//begin ironman second click handles

				}
				npc.setPositionToFace(player.getPosition());
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	public void handleThirdClick(Player player, Packet packet) {
		int index = packet.readShort();
		if(index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		player.setEntityInteraction(npc).setPositionToFace(npc.getPosition().copy());
		npc.setPositionToFace(player.getPosition());
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Third click npc id: "+npc.getId());
		player.setWalkToTask(new WalkToTask(player, npc.getPosition(), npc.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				switch(npc.getId()) {
				case 5382:
					if (player.getGameMode().equals(GameMode.ULTIMATE_IRONMAN)) {
						UltimateIronmanHandler.handleQuickRetrieve(player);
					} else {
						DialogueManager.start(player, 195);
					}
					
					player.getClickDelay().reset();
					break;
				case 4653:
					player.getPacketSender().sendMessage("Unfortunately, ship charters are still being established. Check back soon.");
					break;
				case 736:
					player.forceChat("Nah. I don't want to feed the cancer.");
					break;
				case 3777:
					ShopManager.getShops().get(25).open(player);
					break;
				case 3101:
					ShopManager.getShops().get(42).open(player);
					break;
				case 1597:
				case 8275:
				case 9085:
				case 7780:
					ShopManager.getShops().get(40).open(player);
					break;
				case 946:
					if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
						ShopManager.getShops().get(91).open(player);
						return;
					}
					ShopManager.getShops().get(0).open(player);
					break;
				case 1861:
					if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
						ShopManager.getShops().get(92).open(player);
						return;
					}
					ShopManager.getShops().get(2).open(player);
					break;
				//case 597:
					//ShopManager.getShops().get(54).open(player);
					//break;
				case 961:
					if(player.getRights() == PlayerRights.PLAYER) {
						player.getPacketSender().sendMessage("This feature is currently only available for members.");
						return;
					}
					boolean restore = player.getSpecialPercentage() < 100;
					if(restore) {
						player.setSpecialPercentage(100);
						CombatSpecial.updateBar(player);
						player.getPacketSender().sendMessage("Your special attack energy has been restored.");
					}
					for(Skill skill : Skill.values()) {
						if(player.getSkillManager().getCurrentLevel(skill) < player.getSkillManager().getMaxLevel(skill)) {
							player.getSkillManager().setCurrentLevel(skill, player.getSkillManager().getMaxLevel(skill));
							restore = true;
						}
					}
					if(restore) {
						player.performGraphic(new Graphic(1302));
						player.getPacketSender().sendMessage("Your stats have been restored.");
					} else
						player.getPacketSender().sendMessage("Your stats do not need to be restored at the moment.");
					break;
				case 705:
					ShopManager.getShops().get(5).open(player);
					break;
					case 605:
						player.getPacketSender().sendMessage("Coming soon!");
					//	player.getPacketSender().sendMessage("").sendMessage("You currently have "+player.getPointsHandler().getVotingPoints()+" Voting points.").sendMessage("You can earn points and coins by voting. To do so, simply use the ::vote command.");;
						//ShopManager.getShops().get(90).open(player);
						break;
				case 2253:
					ShopManager.getShops().get(10).open(player);
					break;
				case 5913:
					if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
						ShopManager.getShops().get(91).open(player);
						return;
					}
						ShopManager.getShops().get(0).open(player);
					break;
				}
				npc.setPositionToFace(player.getPosition());
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	public void handleFourthClick(Player player, Packet packet) {
		int index = packet.readLEShort();
		if(index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		player.setEntityInteraction(npc);
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Fourth click npc id: "+npc.getId());
		player.setWalkToTask(new WalkToTask(player, npc.getPosition(), npc.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				switch(npc.getId()) {
				case 946:
					ShopManager.getShops().get(82).open(player);
					break;
				case 3777:
					ShopManager.getShops().get(80).open(player); //DONATOR SHOP 3 HERE
					break;
				case 705:
					ShopManager.getShops().get(7).open(player);
					break;
				case 2253:
					ShopManager.getShops().get(8).open(player);
					break;
				case 605:
					LoyaltyProgramme.open(player);
					break;
				case 1597:
				case 9085:
				case 8275:
				case 7780:
					player.getPacketSender().sendString(36030, "Current Points:   "+player.getPointsHandler().getSlayerPoints());
					player.getPacketSender().sendInterface(36000);
					break;
				}
				npc.setPositionToFace(player.getPosition());
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if(player.isTeleporting() || player.isPlayerLocked() || player.getMovementQueue().isLockMovement())
			return;
		switch (packet.getOpcode()) {
		case ATTACK_NPC:
			attackNPC(player, packet); 
			break;
		case FIRST_CLICK_OPCODE:
			firstClick(player, packet);
			break;
		case SECOND_CLICK_OPCODE:
			handleSecondClick(player, packet);
			break;
		case THIRD_CLICK_OPCODE:
			handleThirdClick(player, packet);
			break;
		case FOURTH_CLICK_OPCODE:
			handleFourthClick(player, packet);
			break;
		case MAGE_NPC:
			int npcIndex = packet.readLEShortA();
			int spellId = packet.readShortA();

			if (npcIndex < 0 || spellId < 0 || npcIndex > World.getNpcs().capacity()) {
				return;
			}

			NPC n = World.getNpcs().get(npcIndex);
			player.setEntityInteraction(n);
			
			if (player != null && n != null&& player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("Used spell id: "+spellId+" on npc: "+n.getId());
			}

			CombatSpell spell = CombatSpells.getSpell(spellId);

			if (n == null || spell == null) {
				player.getMovementQueue().reset();
				return;
			}

			if (!NpcDefinition.getDefinitions()[n.getId()].isAttackable()) {
				player.getMovementQueue().reset();
				return;
			}

			if(n.getConstitution() <= 0) {
				player.getMovementQueue().reset();
				return;
			}

			player.setPositionToFace(n.getPosition());
			player.setCastSpell(spell);
			if(player.getCombatBuilder().getStrategy() == null) {
				player.getCombatBuilder().determineStrategy();
			}
			if (CombatFactory.checkAttackDistance(player, n)) {
				player.getMovementQueue().reset();
			}
			player.getCombatBuilder().resetCooldown();
			player.getCombatBuilder().attack(n);
			break;
		}
	}

	public static final int ATTACK_NPC = 72, FIRST_CLICK_OPCODE = 155, MAGE_NPC = 131, SECOND_CLICK_OPCODE = 17, THIRD_CLICK_OPCODE = 21, FOURTH_CLICK_OPCODE = 18;
}
