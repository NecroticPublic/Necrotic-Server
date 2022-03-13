package com.ruse.net.packet.impl;

import java.text.NumberFormat;
import java.util.Locale;

import com.ruse.model.Animation;
import com.ruse.model.GameMode;
import com.ruse.model.GameObject;
import com.ruse.model.Graphic;
import com.ruse.model.Hit;
import com.ruse.model.Item;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.VoteRewardHandler;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.world.content.Consumables;
import com.ruse.world.content.Digging;
import com.ruse.world.content.Effigies;
import com.ruse.world.content.ExperienceLamps;
import com.ruse.world.content.Gambling;
import com.ruse.world.content.ItemDegrading;
import com.ruse.world.content.MemberScrolls;
import com.ruse.world.content.MoneyPouch;
import com.ruse.world.content.cluescrolls.CLUESCROLL;
import com.ruse.world.content.combat.range.DwarfMultiCannon;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.holidayevents.easter2017;
import com.ruse.world.content.skill.impl.dungeoneering.ItemBinding;
import com.ruse.world.content.skill.impl.herblore.Herblore;
import com.ruse.world.content.skill.impl.herblore.ingredientsBook;
import com.ruse.world.content.skill.impl.hunter.BoxTrap;
import com.ruse.world.content.skill.impl.hunter.Hunter;
import com.ruse.world.content.skill.impl.hunter.JarData;
import com.ruse.world.content.skill.impl.hunter.PuroPuro;
import com.ruse.world.content.skill.impl.hunter.SnareTrap;
import com.ruse.world.content.skill.impl.hunter.Trap.TrapState;
import com.ruse.world.content.skill.impl.prayer.Prayer;
import com.ruse.world.content.skill.impl.runecrafting.Runecrafting;
import com.ruse.world.content.skill.impl.runecrafting.RunecraftingPouches;
import com.ruse.world.content.skill.impl.runecrafting.RunecraftingPouches.RunecraftingPouch;
import com.ruse.world.content.skill.impl.slayer.SlayerDialogues;
import com.ruse.world.content.skill.impl.slayer.SlayerTasks;
import com.ruse.world.content.skill.impl.summoning.BossPets.BossPet;
import com.ruse.world.content.skill.impl.summoning.CharmingImp;
import com.ruse.world.content.skill.impl.summoning.SummoningData;
import com.ruse.world.content.skill.impl.woodcutting.BirdNests;
import com.ruse.world.content.transportation.JewelryTeleporting;
import com.ruse.world.content.transportation.JewelryTeleports;
import com.ruse.world.content.transportation.TeleportTabs;
import com.ruse.world.entity.impl.player.Player;


public class ItemActionPacketListener implements PacketListener {

	public static int count = 0;
	private static String[] ROCK_CAKE = {"Oww!", "Ouch!", "Owwwy!", "I nearly broke a tooth!", "My teeth!", "Who would eat this?", "*grunt*", ":'("};

	private static void firstAction(final Player player, Packet packet) {
		int interfaceId = packet.readUnsignedShort();
		int slot = packet.readShort();
		int itemId = packet.readShort();
		/*if(interfaceId == 38274) {
			Construction.handleItemClick(itemId, player);
			return;
		}*/
		if (Misc.checkForOwner()) {
			System.out.println("Slot: "+slot+", itemId: "+itemId+", interface: "+interfaceId);
		}
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		player.setInteractingItem(player.getInventory().getItems()[slot]);
		if (Prayer.isBone(itemId)) {
			Prayer.buryBone(player, itemId);
			return;
		}
		if (Consumables.isFood(player, itemId, slot))
			return;
		if(Consumables.isPotion(itemId)) {
			Consumables.handlePotion(player, itemId, slot);
			return;
		}

		if(BirdNests.isNest(itemId)) {
			BirdNests.searchNest(player, itemId);
			return;
		}
		if (Herblore.cleanHerb(player, itemId))
			return;
		if(MemberScrolls.handleScroll(player, itemId))
			return;
		if(Effigies.isEffigy(itemId)) {
			Effigies.handleEffigy(player, itemId);
			return;
		}
		if(ExperienceLamps.handleLamp(player, itemId)) {
			return;
		}
		switch(itemId) {
		case 2150:
			player.getInventory().delete(2150, 1);
			player.getInventory().add(2152, 1);
			player.getPacketSender().sendMessage("You remove the Toad's legs.");
			break;
		case 7510:
			int plc = player.getConstitution();
			int plcr = 50;
			
			if (plc <= 50) {
				plcr = plc-1;
			}
			if (plc == 1) {
				plcr = 0;
			}
			
			if (plcr > 0) {
				player.performAnimation(new Animation(829));
				Hit h = new Hit(plcr);
				player.dealDamage(h);
				player.forceChat(Misc.randomElement(ROCK_CAKE));
			} else {
				player.getPacketSender().sendMessage("You'll die if you keep eating this putrid rock!");
			}
			break;
		case 22051:
			if (!player.busy()) {
				easter2017.openInterface(player);
			} else {
				player.getPacketSender().sendMessage("You're too busy to do that!");
			}
			break;
		case 9003:
			player.getPacketSender().sendMessage("<img=10> \"Use\" this Tome on any NPC, then select \"View-drops\" to see it's entire drop table.");
			break;
		case 2946:
			if (!player.getClickDelay().elapsed(1000)) {
				player.getPacketSender().sendMessage("Please wait 1 second before doing that.");
				return;
			} 
			if (player.getInventory().getFreeSlots() < 4) {
				player.getPacketSender().sendMessage("You should have at least 4 inventory spaces free.");
				return;
			}
			if (player.getNewYear2017() < 1) {
				player.getPacketSender().sendMessage("You must have completed the New Year 2017 event.");
				return;
			}
			player.getInventory().delete(2946, 1);
			player.getInventory().add(7329, 1);
			player.getInventory().add(7330, 1);
			player.getInventory().add(7331, 1);
			player.getInventory().add(10326, 1);
			player.getInventory().add(10327, 1);
			player.getPacketSender().sendMessage("<shad=0>@red@Enjoy your firelighters!");
			player.getClickDelay().reset();
			break;
		case 20061:
			if(!player.getClickDelay().elapsed(5000)) {
				player.getPacketSender().sendMessage("Please wait 5 seconds before doing that.");
				return;
				}
			if(player.getInventory().getFreeSlots() < 8) {
				player.getPacketSender().sendMessage("You should have at least 8 inventory spaces free.");
				return;
			}
			if(player.getTotalPlayTime() > 36000000) {
				player.getPacketSender().sendMessage("You need to of played less than 10 hours to open this crate!");
				return;
			}
			player.getInventory().add(10828, 1);
			player.getInventory().add(10551, 1);
			player.getInventory().add(14017, 1);
			player.getInventory().add(13262, 1);
			player.getInventory().add(19670, 15);
			player.getInventory().add(4087, 1);
			player.getInventory().add(556, 1000);
			player.getInventory().add(558, 1000);
			player.getInventory().add(863, 1000);
			player.setRights(PlayerRights.CONTRIBUTOR);
			player.getPacketSender().sendRights();
			player.getClickDelay().reset();
			break;
		case 1561:

			
			//player.getPacketSender().sendMessage("hi");
			if (!player.getClickDelay().elapsed(10000)) {
				player.getPacketSender().sendMessage("Please wait 10 seconds before doing that.");
				return;
			}
			player.getInventory().delete(1561, 1);
			int invSpace = player.getInventory().getFreeSlots();
			int daCount = 0;
			for (int i = 0; i < BossPet.values().length; i++) {
				//System.out.println("daCount < invSpace "+(boolean) (daCount < invSpace));
				//System.out.println("getBossPet("+i+"), "+ItemDefinition.forId(BossPet.values()[i].itemId).getName()+" = "+player.getBossPet(i));
				if (daCount < invSpace && player.getBossPet(i)) {
					player.getInventory().add(BossPet.values()[i].itemId, 1);
					player.getPacketSender().sendMessage("Returned your "+ItemDefinition.forId(BossPet.values()[i].itemId).getName()+" to your inventory.");
					daCount++;
				} else 	if (daCount >= invSpace && player.getBossPet(i) && !player.getBank(0).isFull()) {
					player.getBank(0).add(BossPet.values()[i].itemId, 1);
					player.getPacketSender().sendMessage("Returned your "+ItemDefinition.forId(BossPet.values()[i].itemId).getName()+" to your bank.");
					daCount++;
				}
			}
			player.getClickDelay().reset();
			break;
		case 4837:
			player.getPacketSender().sendMessage("<col=0><shad=ffffff>This tomb has an unmistakable dark energy to it.");
			player.getPacketSender().sendMessage("The Devil's Notebook - by Anton LaVey, Dark Wizard.");
		break;
		case 2424:
			player.getInventory().delete(new Item(2424, 1));
			player.forceChat("It puts the lotion on it's skin...");
			player.performAnimation(new Animation(860));
			player.getInventory().add(new Item(3057, 1));
			break;
		case 8013:
		case 8012:
		case 8011:
		case 8010:
		case 8009:
		case 8008:
		case 8007:
		case 13599:
		case 13600:
		case 13601:
		case 13602:
		case 13603:
		case 13604:
		case 13605:
		case 13606:
		case 13611:
		case 13607:
		case 13608:
		case 13609:
		case 13610:
			TeleportTabs.teleportTabs(player, itemId);
			break;
		case 2724:
			CLUESCROLL.openCasket(player);
			break;
		case 13663:
			if(player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
				return;
			}
			player.setUsableObject(new Object[2]).setUsableObject(0, "reset");
			player.getPacketSender().sendString(38006, "Choose stat to reset!").sendMessage("@red@Please select a skill you wish to reset and then click on the 'Confim' button.").sendString(38090, "Which skill would you like to reset?");
			player.getPacketSender().sendInterface(38000);
			break;
		case 19670:
			if(player.busy()) {
				player.getPacketSender().sendMessage("You can not do this right now.");
				return;
			}
			VoteRewardHandler.voteRewards(player, false);
			break;
		case 9013:
			String[] messages = { 
					"BOO!",
					"SURPRISE!",
					"SPOOOKED YA",
					"Ooooh!",
					"Spoooooooookyy",
					"ooooOOoooOOOooOOOOooo",
					"I'm a ghost!",
					"I ain't afraid of no ghost",
					"If there's something strange in your neighborhood...",
					"Who you gonna call?",
					"GHOST BUSTERS!",//KEEP A COMMA ON THE LAST ONE!
					};
			int max = messages.length;

				//System.out.println("Count: " + count + " | Max: " + max);
			player.forceChat(messages[count]);
			player.performAnimation(new Animation(2836));
			player.performGraphic(new Graphic(293));
			count++;
			
			if (count == max) {
				//System.out.println("Resetting count to 0");
				count = 0;
			}
			break;
		case 20692:
			player.performGraphic(new Graphic (199));
			player.getInventory().delete(20692, 1);
			break;
		case 7956:
			player.getInventory().delete(7956, 1);
			int[] rewards = 		{200, 202, 204, 206, 208, 210, 212, 214, 216, 218, 220, 2486, 3052, 1624, 1622, 1620, 1618, 1632, 1516, 1514, 454, 448, 450, 452, 378, 372, 7945, 384, 390, 15271, 533, 535, 537, 18831, 556, 558, 555, 554, 557, 559, 564, 562, 566, 9075, 563, 561, 560, 565, 888, 890, 892, 11212, 9142, 9143, 9144, 9341, 9244, 866, 867, 868,  2, 10589, 10564, 6809, 4131, 15126, 4153, 1704, 1149};
			int[] rewardsAmount = 	{50, 50, 50, 30, 20,  30,  30,  30,  30,  20,  10,   5,   4,  70,  40,  25,  10,   10,  100,  50,  100,  80, 25, 25,250, 200,  125, 50, 30,    25, 50, 20,  20,     5,500,500,500,500,500,500,500,500, 200,  200, 200, 200, 200, 200,1000,750, 200,   100, 1200, 1200,  120,  50,   20, 1000,500,100,100,     1,     1,    1,    1,     1,    1,    1,    1};
			int rewardPos = Misc.getRandom(rewards.length-1);
			player.getInventory().add(rewards[rewardPos], (int)((rewardsAmount[rewardPos]*0.5) + (Misc.getRandom(rewardsAmount[rewardPos]))));
			break;
		case 15387:
			player.getInventory().delete(15387, 1);
			rewards = new int[] {1377, 1149, 7158, 3000, 219, 5016, 6293, 6889, 2205, 3051, 269, 329, 3779, 6371, 2442, 347, 247};
			player.getInventory().add(rewards[Misc.getRandom(rewards.length-1)], 1);
			break;
		case 407:
			player.getInventory().delete(407, 1);
			if (Misc.getRandom(3) < 3) {
				player.getInventory().add(409, 1);
			} else if(Misc.getRandom(4) < 4) {
				player.getInventory().add(411, 1);
			} else 
				player.getInventory().add(413, 1);
			break;
		case 405:
			player.getInventory().delete(405, 1);
			if (Misc.getRandom(1) < 1) {
				int coins = Misc.getRandom(30000);
				player.getInventory().add(995, coins);
				player.getPacketSender().sendMessage("The casket contained "+NumberFormat.getInstance(Locale.US).format(coins)+" coins!");
			} else
				player.getPacketSender().sendMessage("The casket was empty.");
			break;
		case 2714:
			int amount = Misc.getRandom(100000);
			player.getInventory().delete(2714, 1);
			player.getInventory().add(995, amount);
			player.getPacketSender().sendMessage("Inside the casket you find "+NumberFormat.getInstance(Locale.US).format(amount)+" coins!");
			break;
		case 15084:
			//player.getPacketSender().sendMessage("Dicing has temporarily been disabled. Sorry for the inconvenience.");
			Gambling.rollDice(player);
			break;
		case 299:
			Gambling.plantSeed(player);
			break;
		case 15103:
			player.getPacketSender().sendMessage("This came from a Goblin. Kill Nex, or Zulrah for another.");
			break;
		case 15104:
			player.getPacketSender().sendMessage("This came from Nex or Zulrah. Kill Monkey Skeletons for another.");
			break;
		case 15105:
			player.getPacketSender().sendMessage("This came from a Monkey Skeleton. Kill the KBD for another.");
			break;
		case 15106:
			player.getPacketSender().sendMessage("This came from the KBD, kill Goblins for another.");
			break;
			/*player.getPacketSender().sendMessage("What lies in the <shad=f999f7>Antiqua Carcere<shad=-1>?");
			player.getPacketSender().sendMessage("The <shad=b40404>Nbmfgjdvt<shad=-1> may hold next piece.");
			player.getPacketSender().sendMessage("The...");
			player.getPacketSender().sendMessage("<shad=ffffff>01010101 01101110 01100111");
			player.getPacketSender().sendMessage("<shad=ffffff>01110101 01101001 01110011 ");
			player.getPacketSender().sendMessage("holds the next piece.");
			player.getPacketSender().sendMessage("The...");  
			player.getPacketSender().sendMessage("<shad=000000>53 61 67 69 74 74 61 72 69 69 73");
			player.getPacketSender().sendMessage("holds the next piece.");
			break;
			*/
		case 4155:
			if(player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getPacketSender().sendMessage("You do not have a Slayer task.");
				return;
			}
			DialogueManager.start(player, SlayerDialogues.dialogue(player));
			break;
		case 18719: //potion of flight
			if (player.canFly()) {
				player.getPacketSender().sendMessage("You already know how to fly.");
			} else {
				player.getInventory().delete(18719, 1);
				player.getPacketSender().sendMessage("Your mind is filled with the secrets of flight!").sendMessage("Use ::fly to toggle flight on and off.");	
				player.setCanFly(true);
				player.setFlying(true);
				player.newStance();
			}
			break;
		case 7587:
			if (player.canGhostWalk()) {
				player.getPacketSender().sendMessage("You already know how to ghost walk.");
			} else {
				player.getInventory().delete(7587, 1);
				player.getPacketSender().sendMessage("Your mind is filled with the secrets of death!").sendMessage("Use ::ghostwalk to toggle it on and off.");	
				player.setCanGhostWalk(true);
				player.setGhostWalking(true);
				player.newStance();
			}
			break;
		case 11858:
		case 11860:
		case 11862:
		case 11848:
		case 11856:
		case 11850:
		case 11854:
		case 11852:
		case 11846:
		case 19580:
			if(!player.getClickDelay().elapsed(2000) || !player.getInventory().contains(itemId))
				return;
			if(player.busy()) {
				player.getPacketSender().sendMessage("You cannot open this right now.");
				return;
			}

			int[] items = itemId == 11858 ? new int[] {10350, 10348, 10346, 10352} : 
				itemId == 19580 ? new int[]{19308, 19311, 19314, 19317, 19320} : 
				itemId == 11860 ? new int[]{10334, 10330, 10332, 10336} : 
					itemId == 11862 ? new int[]{10342, 10338, 10340, 10344} : 
						itemId == 11848 ? new int[]{4716, 4720, 4722, 4718} : 
							itemId == 11856 ? new int[]{4753, 4757, 4759, 4755} : 
								itemId == 11850 ? new int[]{4724, 4728, 4730, 4726} : 
									itemId == 11854 ? new int[]{4745, 4749, 4751, 4747} : 
										itemId == 11852 ? new int[]{4732, 4734, 4736, 4738} : 
											itemId == 11846 ? new int[]{4708, 4712, 4714, 4710} :
												new int[]{itemId};

											if(player.getInventory().getFreeSlots() < items.length) {
												player.getPacketSender().sendMessage("You do not have enough space in your inventory.");
												return;
											}
											player.getInventory().delete(itemId, 1);
											for(int i : items) {
												player.getInventory().add(i, 1);
											}
											player.getPacketSender().sendMessage("You open the set and find items inside.");
											player.getClickDelay().reset();
											break;
		case 952:
			Digging.dig(player);
			break;
		case 11261:
		case 1748:
		case 1750:
		case 1752:
		case 1754:
		case 228:
			int uimint[] = {1748, 1750, 1752, 1754, 228};
			for (int i = 0; i < uimint.length; i++) {
				if (uimint[i] == itemId && !player.getGameMode().equals(GameMode.ULTIMATE_IRONMAN)) {
					player.getPacketSender().sendMessage("Only Ultimate Ironman characters can do that.");
					return;
				}
			}
			if (!player.getClickDelay().elapsed(100)) {
				return;
			}
			if (player.busy()) {
				player.getPacketSender().sendMessage("You're too busy to un-note that.");
				break;
			}
			if (player.getInventory().isFull()) {
				player.getPacketSender().sendMessage("You need a free inventory space to un-note that.");
				break;
			}
			String orig = ItemDefinition.forId(itemId).getName(), cur = ItemDefinition.forId(itemId-1).getName();
			if (!orig.equalsIgnoreCase(cur)) {
				player.getPacketSender().sendMessage("Error 21641: a = "+itemId+" &-1");
				break;
			}
			player.getClickDelay().reset();
			int b = player.getInventory().getAmount(itemId);
			player.getInventory().delete(itemId, 1);
			if (player.getInventory().getAmount(itemId) == (b-1)) {
				player.getInventory().add((itemId-1), 1);
			} else {
				player.getPacketSender().sendMessage("Error 41265: b = "+b+", c = "+player.getInventory().getAmount(itemId));
				break;
			}
			break;
			case 10006:
			// Hunter.getInstance().laySnare(client);
			Hunter.layTrap(player, new SnareTrap(new GameObject(19175, new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())), TrapState.SET, 200, player));
			break;
		case 10008:			
			Hunter.layTrap(player, new BoxTrap(new GameObject(19187, new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())), TrapState.SET, 200, player));
			break;
		case 5509:
		case 5510:
		case 5512:
		case 5514:
			RunecraftingPouches.fill(player, RunecraftingPouch.forId(itemId));
			break;
		case 292:
			ingredientsBook.readBook(player, 0, false);
			break;
		case 6199:
			int rewards2[][] = {
					{15501, 15272, 2503, 10499, 6326, 861, 1163, 1201, 6111, 544, 542, 5574, 5575, 5576, 1215, 3105, 13734, 7400, 11118}, //Common, 0
					{15501, 11133, 15126, 10828, 3751, 3753, 10589, 10564, 6809, 4587, 1249, 3204, 1305, 1377, 1434, 6528, 7158, 4153, 6, 8, 10, 12, 4675, 6914, 6889}, //Uncommon, 1
					{6739, 15259, 15332, 2579, 6920, 6922, 13879, 13883, 15241, 15243} //Rare, 2
			};
			double numGen = Math.random();
			/** Chances
			 *  50% chance of Common Items - cheap gear, high-end consumables
			 *  40% chance of Uncommon Items - various high-end coin-bought gear
			 *  10% chance of Rare Items - Highest-end coin-bought gear, some voting-point/pk-point equipment
			 */
			int rewardGrade = numGen >= 0.5 ? 0 : numGen >= 0.20 ? 1 : 2;
			rewardPos = Misc.getRandom(rewards2[rewardGrade].length-1);
			player.getInventory().delete(6199, 1);
			player.getInventory().add(rewards2[rewardGrade][rewardPos], 1).refreshItems();
			break;
		case 15501:
			int superiorRewards[][] = {
					{11133, 15126, 10828, 3751, 3753, 10589, 10564, 6809, 4587, 1249, 3204, 1305, 1377, 1434, 6528, 7158, 4153, 6, 8, 10, 12, 4675, 6914, 6889}, //Uncommon, 0
					{6739, 15259, 15332, 2579, 6920, 6922, 15241, 11882, 11884, 11906, 20084}, //Rare, 1
					{6570, 15018, 15019, 15020, 15220, 11730, 18349, 18353, 13896, 18357, 13899, 10551, 4151, 2577}, //Epic, 2
					{11235, 17273, 14484, 11696, 11698, 11700, 13262, 15486, 19336, 19337, 19338, 19339, 19340, 14009, 14010, 14008, 22034} //Legendary, 3
			};
			double superiorNumGen = Math.random();
			/** Chances
			 *  54% chance of Uncommon Items - various high-end coin-bought gear
			 *  30% chance of Rare Items - Highest-end coin-bought gear, Some poor voting-point/pk-point equipment
			 *  11% chance of Epic Items -Better voting-point/pk-point equipment
			 *  5% chance of Legendary Items - Only top-notch voting-point/pk-point equipment
			 */
			int superiorRewardGrade = superiorNumGen >= 0.46 ? 0 : superiorNumGen >= 0.16 ? 1 : superiorNumGen >= 0.05? 2 : 3;
			int superiorRewardPos = Misc.getRandom(superiorRewards[superiorRewardGrade].length-1);
			player.getInventory().delete(15501, 1);
			player.getInventory().add(superiorRewards[superiorRewardGrade][superiorRewardPos], 1).refreshItems();
			break;
			case 15682:
				if(!player.getInventory().contains(15682)) {
					return;
				}
				if(!player.getClickDelay().elapsed(1000)) {
						return;
				}
				player.getClickDelay().reset();
				player.getInventory().delete(15682, 1);
				player.getInventory().add(19670, 3);
				break;
		case 11884:
			player.getInventory().delete(11884, 1);
			player.getInventory().add(2595, 1).refreshItems();
			player.getInventory().add(2591, 1).refreshItems();
			player.getInventory().add(3473, 1).refreshItems();
			player.getInventory().add(2597, 1).refreshItems();
			break;
		case 11882:
			player.getInventory().delete(11882, 1);
			player.getInventory().add(2595, 1).refreshItems();
			player.getInventory().add(2591, 1).refreshItems();
			player.getInventory().add(2593, 1).refreshItems();
			player.getInventory().add(2597, 1).refreshItems();
			break;
		case 11906:
			player.getInventory().delete(11906, 1);
			player.getInventory().add(7394, 1).refreshItems();
			player.getInventory().add(7390, 1).refreshItems();
			player.getInventory().add(7386, 1).refreshItems();
			break;
		case 15262:
			if(!player.getClickDelay().elapsed(1000))
				return;
			player.getInventory().delete(15262, 1);
			player.getInventory().add(18016, 10000).refreshItems();
			player.getClickDelay().reset();
			break;
		case 6:
			DwarfMultiCannon.setupCannon(player);
			break;
		case 2722:
		case 2723:
		case 2725:
		case 2727:
		case 2729:
		case 2731:
		case 2733:
		case 2735:
		case 2737:
		case 2739:
		case 2741:
		case 2743:
		case 2745:
		case 2747:
		case 2773:
		case 2774:
		case 2776:
		case 2778:
		case 2780:
		case 2782:
		case 2783:
		case 2785:
		case 2786:
		case 2788:
		case 2790:
		case 2792:
		case 2793:
		case 2794:
		case 2796:
		case 2797:
		case 2799:
		case 3520:
		case 3522:
		case 3524:
		case 3525:
		case 3526:
		case 3528:
		case 3530:
		case 3532:
		case 3534:
		case 3536:
		case 3538:
		case 3540:
		case 3542:
		case 3544:
		case 3546:
		case 3548:
		case 3550:
			for (int i = 0; i < CLUESCROLL.values().length; i++) {
				if (CLUESCROLL.values()[i].getClueId() == itemId) {
					int steps = player.getPointsHandler().getClueSteps();
					String s = "steps";
					if (steps == 1) {
						s = "step";
					}
					player.getPacketSender().sendMessage(CLUESCROLL.values()[i].getHint()+" You've done "+player.getPointsHandler().getClueSteps()+" "+s+".");
					break;
				}
			}
		}
	}

	public static void secondAction(Player player, Packet packet) {
		int interfaceId = packet.readLEShortA();
		int slot = packet.readLEShort();
		int itemId = packet.readShortA();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		if (SummoningData.isPouch(player, itemId, 2))
			return;
		switch(itemId) {
		case 6500:
			if(player.getCombatBuilder().isAttacking() || player.getCombatBuilder().isBeingAttacked()) {
				player.getPacketSender().sendMessage("You cannot configure this right now.");
				return;
			}
			player.getPacketSender().sendInterfaceRemoval();
			DialogueManager.start(player, 101);
			player.setDialogueActionId(60);
			break;
		case 4155:
			player.getPacketSender().sendInterfaceRemoval();
			DialogueManager.start(player, 103);
			player.setDialogueActionId(65);
		break;
		case 1712: //glory start
		case 1710:
		case 1708:
		case 1706: //glory end
		case 11118: //cb brace start
		case 11120:
		case 11122:
		case 11124: //cb brace end
		case 2552: //duel start
		case 2554:
		case 2556:
		case 2558:
		case 2560:
		case 2562:
		case 2564:
		case 2566: //duel end
		case 3853: //games start
		case 3855:
		case 3857:
		case 3859:
		case 3861:
		case 3863:
		case 3865:
		case 3867: //games end
		case 11194: //digsite start 
		case 11193:
		case 11192:
		case 11191:
		case 11190: //digsite start
			String jewelName = JewelryTeleports.stripName(itemId);
			JewelryTeleports.handleDialogue(player, itemId, JewelryTeleports.jewelIndex(jewelName));
			break;
		case 10362:
			JewelryTeleporting.rub(player, itemId);
			break;
		case 10732:
		case 4566:
			player.performAnimation(new Animation(1835));
			break;

		case 11113:
			player.getPacketSender().sendMessage("All skill teleports are available in the skills tab.");
			break;
			
		case 1704:
			player.getPacketSender().sendMessage("Your amulet has run out of charges.");
			break;
		case 11126:
			player.getPacketSender().sendMessage("Your bracelet has run out of charges.");
			break;
		case 13281:
		case 13282:
		case 13283:
		case 13284:
		case 13285:
		case 13286:
		case 13287:
		case 13288:
			player.getSlayer().handleSlayerRingTP(itemId);
			break;
		case 5509:
		case 5510:
		case 5512:
		case 5514:
			RunecraftingPouches.check(player, RunecraftingPouch.forId(itemId));
			break;
		case 2550:
			if (!player.getInventory().contains(2550)) {
				player.getPacketSender().sendMessage("You must have a ring of recoil in your inventory to do this.");
				return;
			}
			if (ItemDegrading.maxRecoilCharges - player.getRecoilCharges()  ==  ItemDegrading.maxRecoilCharges) {
				player.getPacketSender().sendMessage("You already have the maximum ring of recoil charges.");
				return;
			}
			player.getInventory().delete(2550, 1);
			player.setRecoilCharges(0);
			player.getPacketSender().sendMessage("Your ring of recoil turns to dust, and your charges are reset.");
			break;
        case 12926:
                int charges = player.getBlowpipeCharges();
                if(!player.getInventory().contains(12926)) {
                    return;
                }
                if(charges <= 0) {
                    player.getPacketSender().sendMessage("You have no charges!");
                    return;
                }
                if (player.getInventory().contains(12934) || !player.getInventory().isFull()) {
                	player.getInventory().add(12934, charges);
                	player.setBlowpipeCharges(0);
                	player.getPacketSender().sendMessage("You uncharge your blowpipe and recieve " + Misc.format(charges) + " Zulrah scales");
                } else {
                	player.getPacketSender().sendMessage("You need an inventory space.");
                }
                break;
		case 995:
			MoneyPouch.depositMoney(player, player.getInventory().getAmount(995));
			break;
		case 1438:
		case 1448:
		case 1440:
		case 1442:
		case 1444:
		case 1446:
		case 1454:
		case 1452:
		case 1462:
		case 1458:
		case 1456:
		case 1450:
			Runecrafting.handleTalisman(player, itemId);
			break;
		}
	}

	public void thirdClickAction(Player player, Packet packet) {
		int itemId = packet.readShortA();
		int slot = packet.readLEShortA();
		int interfaceId = packet.readLEShortA();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		if(JarData.forJar(itemId) != null) {
			PuroPuro.lootJar(player, new Item(itemId, 1), JarData.forJar(itemId));
			return;
		}
		if (SummoningData.isPouch(player, itemId, 3)) {
			return;
		}
		if(ItemBinding.isBindable(itemId)) {
			ItemBinding.bindItem(player, itemId);
			return;
		}
		switch(itemId) {
		case 7510:
			int plc = player.getConstitution();
			int plcr = plc-1;
			
			if (plc == 1) {
				plcr = 0;
			}
			
			if (plcr > 0) {
				player.performAnimation(new Animation(829));
				Hit h = new Hit(plcr);
				player.dealDamage(h);
				player.forceChat(Misc.randomElement(ROCK_CAKE));
			} else {
				player.getPacketSender().sendMessage("You'll die if you keep eating this putrid rock!");
			}
			break;
		case 2550:
			int recoilcharges = (ItemDegrading.maxRecoilCharges - player.getRecoilCharges());
			player.getPacketSender().sendMessage("You have "+recoilcharges+" recoil " + (recoilcharges == 1 ? "charge" : "charges") + " remaining.");
			break;
		case 9003:
			if (!player.getClickDelay().elapsed(100)) {
				return;
			}
			if (player.getLastTomed() == 0) {
				player.getPacketSender().sendMessage("<img=10> No NPC set in the Tome.");
				return;
			}
			if (player.busy() || player.getCombatBuilder().isAttacking() || player.getCombatBuilder().isBeingAttacked()) {
				player.getPacketSender().sendMessage("You're much too busy to do that.");
				return;
			}
			NPCDrops.sendDropTableInterface(player, player.getLastTomed());
			player.getClickDelay().reset();
			break;
		//case 13738:
		case 13740:
		case 13742:
		//case 13744:
			if(player.isSpiritDebug()) {
				player.getPacketSender().sendMessage("You toggle your Spirit Shield to not display specific messages.");
				player.setSpiritDebug(false);
			} else if (player.isSpiritDebug() == false) {
				player.getPacketSender().sendMessage("You toggle your Spirit Shield to display specific messages.");
				player.setSpiritDebug(true);
			}
		break;
		case 19670:
			if(player.busy()) {
				player.getPacketSender().sendMessage("You can not do this right now.");
				return;
			}
			VoteRewardHandler.voteRewards(player, true);
			break;
		case 6500:
			CharmingImp.sendConfig(player);
			break;
		case 4155: //kills-remaining
			player.getPacketSender().sendMessage(player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK ? ("You do not have a Slayer task.") : ("You're assigned to kill "+Misc.formatText(player.getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " "))+"s, only "+player.getSlayer().getAmountToSlay()+" more to go."));
			break;
		case 13281:
		case 13282:
		case 13283:
		case 13284:
		case 13285:
		case 13286:
		case 13287:
		case 13288:
			player.getPacketSender().sendInterfaceRemoval();
			player.getPacketSender().sendMessage(player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK ? ("You do not have a Slayer task.") : ("You're assigned to kill "+Misc.formatText(player.getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " "))+"s, only "+player.getSlayer().getAmountToSlay()+" more to go."));
			break;
		case 6570:
			if(player.getInventory().contains(6570) && player.getInventory().getAmount(6529) >= 50000) {
				player.getInventory().delete(6570, 1).delete(6529, 50000).add(19111, 1);
				player.getPacketSender().sendMessage("You have upgraded your Fire cape into a TokHaar-Kal cape!");
			} else {
				player.getPacketSender().sendMessage("You need at least 50.000 Tokkul to upgrade your Fire Cape into a TokHaar-Kal cape.");
			}
			break;
		case 15262:
			if(!player.getClickDelay().elapsed(1300))
				return;
			int amt = player.getInventory().getAmount(15262);
			if(amt > 0)
				player.getInventory().delete(15262, amt).add(18016, 10000 * amt);
			player.getClickDelay().reset();
			break;
		case 5509:
		case 5510:
		case 5512:
		case 5514:
			RunecraftingPouches.empty(player, RunecraftingPouch.forId(itemId));
			break;
		case 11283: //DFS
			player.getPacketSender().sendMessage("Your Dragonfire shield has "+player.getDfsCharges()+"/20 dragon-fire charges.");
			break;
		}
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		switch (packet.getOpcode()) {
		case SECOND_ITEM_ACTION_OPCODE:
			secondAction(player, packet);
			break;
		case FIRST_ITEM_ACTION_OPCODE:
			firstAction(player, packet);
			break;
		case THIRD_ITEM_ACTION_OPCODE:
			thirdClickAction(player, packet);
			break;
		}
	}

	public static final int SECOND_ITEM_ACTION_OPCODE = 75;

	public static final int FIRST_ITEM_ACTION_OPCODE = 122;

	public static final int THIRD_ITEM_ACTION_OPCODE = 16;

}