package com.ruse.world.content.cluescrolls;

import java.util.ArrayList;
import java.util.List;

import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.entity.impl.player.Player;

public enum CLUESCROLL {

	HOME_TREASURE(2722, new Position(3658, 2993), -1, "Dig near a Treasure Chest at home."),
	CHILL_GATES(2723, new Position(2855, 3810), -1, "Dig infront of the Frozen gates."),
	TAVERLY_DUNG_PIPE(2725, new Position(2886, 9799), -1, "Dig next to a pipe in the Taverly Dungeon."),
	DAEMONHEIM_SKILLCAPE_STAND(2727, new Position(3446, 3717), -1, "Dig infront of this Skillcape stand."),
	YAK_PEN_CORNER(2729, new Position(3193, 3276), -1, "Dig in a corner of the Yak's pen."),
	DUEL_ARENA_BED(2731, new Position(3365, 3274), -1, "Dig in front of a bed in the Duel Arena Infirmary."),
	CHAOS_ALTAR(2733, new Position(3239, 3606), -1, "Dig behind the Chaos Altar. Watch out for PKers!"),
	NEX_LANDSLIDE(2735, new Position(2907, 5204), -1, "Dig at the top of a landslide, watch out for Zarosians."),
	BARROWS_FOREST(2737, new Position(3577, 3321), -1, "Dig around the trees beside the shed at Barrows."),
	BEHIND_EDGEVILLE_BANK(2739, new Position(3099, 3492), -1, "Dig behind the Edgeville bank - near the banker's window."),
	CHICKENS_SACK(2741, new Position(3235, 3287), -1, "Dig beside a grain sack near the chickens!"),
	LAVA_FORGE(2743, new Position(2447, 5151), -1, "Dig near the Lava Forge!"),
	LAVA_BANK(2745, new Position(2445, 5176), -1, "Dig near the hottest banker."),
	GNOME_ROPE(2747, new Position(2477, 3420), -1, "Dig at the start of this Gnome Agility obstacle."),
	TAVERLY_FOUNTAIN(2773, new Position(2892, 3440), -1, "Dig infront of the fountain in Taverly."),
	LUMBRIDGE_GRAVEYARD(2774, new Position(3248, 3202), -1, "Dig near a grave in Lumbridge."),
	FALADOR_STATUE(2776, new Position(2965, 3380), -1, "Dig infront of the statue of Saradomin."),
	FALADOR_FLOWERBED(2778, new Position(3012, 3373), -1, "Dig up a flowerbed in Falador."),
	DRAYNOR_BANK(2780, new Position(3088, 3247), -1, "Dig infront of the damaged bank wall."),
	EDGEVILLE_MONASTERY(2782, new Position(3049, 3506), -1, "Dig between the red and pink rose patches."),
	PORT_SARIM_RUNES(2783, new Position(3014, 3259), -1, "Dig in Betty's Runes shop."),
	EMILY_HOME(2785, new Position(3666, 2991), -1, "Try not to get banned digging in front of this streamer."),
	YELLOW_ENERGY(2786, new Position(2134, 5534, 3), -1, "Dig infront of this Runecrafting Wizard."),
	CRYSTAL_CHEST(2788, new Position(3671, 2977), -1, "Dig in front of the Crystal Chest."),
	HOME_MERCHANT(2790, new Position(3678, 2973), -1, "Dig in front of the boxes beside the flea Merchant at home."),
	FLAX_FIELD(2792, new Position(2739, 3440), -1, "Dig under this piece of Flax!"),
	SEERS_STATUE(2793, new Position(2740, 3493), -1, "Dig behind this Mysterious statue beside Arthur's castle."),
	PORO_HUNTING(2794, new Position(2560, 4288), -1, "Dig in one of the outer corners of Puro-Puro."),
	GOBLIN_VILLAGE_LUMB(2796, new Position(3243, 3244), -1, "Dig in the Goblin's house."),
	KARAMJA_PLANTATION(2797, new Position(2940, 3155), -1, "Dig beside these boxes in the plantation."),
	HAM_CAMP(2799, new Position(3166, 3252), -1, "Dig on top of this trapdoor around Lumbridge."),
	LUMBRIDGE_GATE(3520, new Position(3267, 3228), -1, "Dig in front of this path to the desert."),
	ALKARID_SCIM_SHOP(3522, new Position(3287, 3187), -1, "Dig in this scimitar shop."),
	FALADOR_BARBERSHOP(3524, new Position(2944, 3383), -1, "Dig in the barber shop."),
	CAMELOT_COAL_TRUCKS(3525, new Position(2694, 3505), -1, "Dig in the room for storing Coal trucks."),
	ARDY_SPICE_STALL(3526, new Position(2659, 3299), -1, "Dig in front of this spicy stall."),
	WATCHTOWER_F(3528, new Position(2543, 3115), -1, "Dig in the bottom floor of this tower."),
	VET_TION_GRAVES(3530, new Position(2980, 3763), -1, "Dig between these Coffins near Vet'tion."),
	PEST_CONTROL(3532, new Position(2658, 2677), -1, "Dig on the other dock at Pest Control."),
	DUEL_ARENA_TOMATO(3534, new Position(3382, 3267), -1, "Dig beside these Rotten Tomatoes."),
	WARRIOR_GUILD(3536, new Position(2866, 3546), -1, "Dig in the entrance room of the Warrior's Guild."),
	GRAVEYARD_GUARDIAN(3538, new Position(3503, 3565), -1, "Dig in front of this Guardian."),
	BRIMHAVEN_CORPSE(3540, new Position(2700, 9561), -1, "Dig beside this corpse on the dungeon stairs."),
	DARK_BEAST_CHAOS_TUNNELS(3542, new Position(3164, 5463), -1, "Dig beside these Mushrooms near the dark beasts."),
	AGED_LOG(3544, new Position(1741, 5352), -1, "Dig beside the Aged log in the dungeon."),
	BEEHIVE(3546, new Position(2758, 3443), -1, "Dig beside this Beehive."),
	SHOP_BANKER(3548, new Position(3690, 2975), -1, "Dig beside a banker at home."),
	USE_ON_EMILY(3550, null, 736, "Use this Cluescroll on our streamer.")
	;
	
	private int clueId;
	private Position digTile;
	private int npcId;
	private String hint;
	
	public static int[] hardClueId = { 2722, 2723, 2725, 2727, 2729, 2731, 2733, 2735, 2737, 2739, 2741, 2743, 2745, 2747, 2773, 2774, 2776, 2778, 2780, 2782, 2783, 2785, 2786, 2788, 2790, 2792, 2793, 2794 , 2796, 2797, 2799, 
			3520, 3522, 3524, 3525, 3526, 3528, 3530, 3532, 3534, 3536, 3538, 3540, 3542, 3544, 3546, 3548, 3550 };
	
	public static int hardCasket = 2724;
	
	public static Item[] consumableRewards = { new Item(556, 250), new Item(558, 250), new Item(555, 250), new Item(554, 250),
			new Item(557, 250), new Item(559, 250), new Item(564, 250), new Item(562, 250), new Item(566, 250), 
			new Item(9075, 250), new Item(563, 250), new Item(561, 250), new Item(560, 250), new Item(4698, 250), new Item(565, 250), //runes
			
			new Item(882, 350), new Item(884, 300), new Item(886, 250), new Item(888, 200), new Item(890, 175), new Item(892, 165), new Item(11212, 160), 
			new Item(810, 300), new Item(811, 250), new Item(11230, 100), new Item(877, 100), new Item(9140, 100), new Item(9141, 100), new Item(9142, 100), 
			new Item(9143, 100), new Item(9144, 100), new Item(9240, 100), new Item(9241, 100), new Item(9242, 100), new Item(9243, 100), new Item(9341, 100), new
			Item(9244, 75), new Item(9245, 75), new Item(864, 100), new Item(863, 100), new Item(865, 100), new Item(866, 100), new Item(867, 100), new Item(868, 100), 
			new Item(2, 250), //Ammunition
			
			new Item(326, 500), new Item(340, 500), new Item(330, 500), new Item(362, 400), new Item(380, 190), new Item(374, 120), new Item(7947, 75), 
			new Item(386, 40), new Item(392, 20), new Item(15273, 20), //Food
			new Item(2443, 50), new Item(2437, 55), new Item(2441, 50), new Item(2445, 45), new Item(3041, 30), new Item(6686, 20), 
			new Item(2453, 50), new Item(3025, 15), new Item(2435, 25), new Item(2447, 50), //potions
			new Item(1734, 1000) //thread
	};
	
	public static Item[] ultraRareRewards = {
			new Item(22041, 1), //Black h'ween
			new Item(14484, 1), //dragon claws
			new Item(19780, 1), //korsai
			new Item(1055, 1), new Item(1053, 1), new Item(1057, 1), //h'ween masks
			new Item(1048, 1), new Item(1042, 1), new Item(1046, 1), new Item(1044, 1), new Item(1040, 1), new Item(1038, 1), //phats
			new Item(22012, 1), //Crimson's Katana
			new Item(14008, 1), new Item(14009, 1), new Item(14010, 1), //Torva
			new Item(14011, 1), new Item(14012, 1), new Item(14013, 1), //Pernix
			new Item(14014, 1), new Item(14015, 1), new Item(14016, 1), //Virtus
			new Item(13746, 1), new Item(13748, 1), new Item(13750, 1), new Item(13752, 1) //spirit shield sigils
	};
	
	public static Item[] equipmentRewards = {
			new Item(10362, 1), //Glory (T)
			new Item(7414, 1),
			new Item(4151, 1), new Item(15441, 1), new Item(15442, 1), new Item(15443, 1), new Item(15444, 1), new Item(22007), //whips
			new Item(15018, 1), new Item(15019, 1), new Item(15020, 1), new Item(15220, 1), new Item(12601), //rings
			new Item(19293, 1), new Item(19333, 1), //frost dragon mask, fury (or) kit
			new Item(18744, 1), new Item(18745, 1), new Item(18746, 1), //halos
			new Item(15486, 1), new Item(14004, 1), new Item(14005, 1), new Item(14006, 1), new Item(14007, 1), //staff of light
			new Item(2581, 1), new Item(14000, 1), new Item(14001, 1), new Item(14002, 1), new Item(14003, 1), new Item(2577, 1), //robin hoods
			new Item(19336, 1), new Item(19337, 1), new Item(19338, 1), new Item(19339, 1), new Item(19340, 1), new Item(13262, 1), new Item(20084, 1), //dragon items, golden maul
			new Item(4708, 1), new Item(4712, 1), new Item(4714, 1), new Item(4710), //ahrims
			new Item(4716, 1), new Item(4720, 1), new Item(4722, 1), new Item(4718, 1), //dharoks
			new Item(4724, 1), new Item(4728, 1), new Item(4730, 1), new Item(4726, 1), //guthans
			new Item(4745, 1), new Item(4749, 1), new Item(4751, 1), new Item(4747, 1), //torags
			new Item(4732, 1), new Item(4734, 1), new Item(4736, 1), new Item(4738, 1), new Item(4740, 1000), //karil's
			new Item(4753, 1), new Item(4757, 1), new Item(4759, 1), new Item(4755, 1), //verac's
			new Item(2595, 1), new Item(2591, 1), new Item(2593, 1), new Item(2597, 1), new Item(3473, 1), //black (g)
			new Item(3488, 1), new Item(3486, 1), new Item(3481, 1), new Item(3483, 1), new Item(3485, 1), //gilded
			new Item(2605, 1), new Item(2599, 1), new Item(2601, 1), new Item(2603, 1), new Item (3474, 1), //adamant (t)
			new Item(2611, 1), new Item(3475, 1), new Item(2613, 1), new Item(2607, 1), new Item(2609, 1), //adamant (g)
			new Item(2627, 1), new Item(2623, 1), new Item(2625, 1), new Item(3477, 1), new Item(2629, 1), //rune (t)
			new Item(2621, 1), new Item(3476, 1), new Item(2619, 1), new Item(2615, 1), new Item(2617, 1), //rune (g)
			new Item(7380, 1), new Item(7372, 1), new Item(7378, 1), new Item(7370, 1), new Item(7368, 1), new Item(7364, 1), new Item(7366, 1), new Item(7362, 1), //range (g) and (t)
			new Item(10374, 1), new Item(10370, 1), new Item(10372, 1), new Item(10368, 1), //zammy dragonhide set
			new Item(11730, 1), new Item (11690, 1), //sara sword, gs blade
			new Item(15126, 1) //Ranging amulet
	};
	
	public static Item[] fillerRewards = { new Item(5574, 1), new Item(5575, 1), new Item(5576, 1), new Item(10828, 1),
			new Item(1540, 1), new Item(6528, 1), new Item(4587, 1), new Item(1215, 1), new Item(6568, 1),
			new Item(9672, 1), new Item(9674, 1), new Item(9676, 1), new Item(11118, 1), new Item(4675, 1),
			new Item(861, 1), new Item(8007, 100), new Item(8008, 100), new Item(8009, 100), new Item(8010, 100),
			new Item(8011, 100), new Item(8012, 100), new Item(8013, 100), new Item(13599, 100), new Item(13600, 100),
			new Item(13601, 100), new Item(13602, 100), new Item(13603, 100), new Item(13604, 100),
			new Item(13605, 100), new Item(13606, 100), new Item(13607, 100), new Item(13608, 100),
			new Item(13609, 100), new Item(13610, 100), new Item(13611, 100) };
	
	public static Item[] thirdAgeRewards = {
			new Item(10350, 1), new Item(10348, 1), new Item(10346, 1), new Item(10352, 1), //3rd age melee
			new Item(10334, 1), new Item(10330, 1), new Item(10332, 1), new Item(10336, 1), //3rd age range
			new Item(10342, 1), new Item(10338, 1), new Item(10340, 1), new Item(10344, 1), //3rd age mage
			new Item(19308, 1), new Item(19311, 1), new Item(19314, 1), new Item(19317, 1), new Item(19320, 1), //3rd age druid	
	};
	
	//private static int easyCasket = 3511;
	//private static int mediumCasket = 2802;
	//private static int eliteCasket = 19039;

	private CLUESCROLL(int clueId, Position digTile, int npcId, String hint) {
		this.clueId = clueId;
		this.digTile = digTile;
		this.npcId = npcId;
		this.hint = hint;
	}
	
	public int getClueId() {
		return clueId;
	}
	
	public Position getDigTile() {
		return digTile;
	}
	
	public String getHint() {
		return hint;
	}
	
	public int getNpcId() {
		return npcId;
	}
	
	public static void mockCasket(int iterations) {
		/**
		 * depreciated
		 */
		for (int i = 0; i < iterations; i++) {
			Item a = consumableRewards[Misc.getRandom((int) (consumableRewards.length-1))];
			Item b = fillerRewards[Misc.getRandom((int) (fillerRewards.length-1))];
			Item c;
			boolean equip = Misc.getRandom(1) == 0;
			if (equip) {
				c = equipmentRewards[Misc.getRandom((int) (equipmentRewards.length-1))];
			} else {
				c = consumableRewards[Misc.getRandom((int) (consumableRewards.length-1))];
			}
			String log = "["+i+"] "+a.getAmount()+"x "+ItemDefinition.forId(a.getId()).getName()+", "+b.getAmount()+"x "+ItemDefinition.forId(b.getId()).getName()+", "+c.getAmount()+"x "+ItemDefinition.forId(c.getId()).getName()+".";
			PlayerLogs.log("1 - mock", log);
			System.out.println("Completed "+i);
		}
	}
	
	public static void openCasket(Player player) {
		if (!player.getClickDelay().elapsed(3000)) {
			return;
		}
		boolean equip = Misc.getRandom(1) == 0;
		boolean thirdAge = Misc.getRandom(702) == 1;
		boolean ultraRare = Misc.getRandom(3510) == 1;
		int originalCount = player.getInventory().getAmount(hardCasket);
		boolean space = player.getInventory().getFreeSlots() >= 2;
		boolean ttt = originalCount >= 1;
		if (!ttt) {
			player.getPacketSender().sendMessage("Error: 90101");
			return;
		}
		if (!space) {
			player.getPacketSender().sendMessage("You must have at least 2 free inventory spaces.");
			return;
		}
		player.getInventory().delete(hardCasket, 1);
		if (player.getInventory().getAmount(hardCasket) != (int) (originalCount-1)) {
			player.getPacketSender().sendMessage("ERROR 11012");
			if (player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("OC = "+originalCount+", OC-1 = "+(int) (originalCount-1)+", current count = "+player.getInventory().getAmount(hardCasket));
			}
			return;
		}
		Item a;
		if (ultraRare || player.getUsername().equalsIgnoreCase("debug")) {
			a = ultraRareRewards[Misc.getRandom(ultraRareRewards.length-1)];
			World.sendMessage("<img=101><col=e3522c><shad=0> " + player.getUsername()
			+ " has looted a " +ItemDefinition.forId(a.getId()).getName()  + " from a Treasure Trail!");
		} else {
			a = consumableRewards[Misc.getRandom(consumableRewards.length-1)]; //(
		}
		Item b = fillerRewards[Misc.getRandom(fillerRewards.length-1)];
		player.getInventory().add(a).add(b);
		Item c;
		if (thirdAge) {
			c = thirdAgeRewards[Misc.getRandom(thirdAgeRewards.length-1)];
			World.sendMessage("<img=101><col=e3522c><shad=0> " + player.getUsername()
			+ " has looted a " +ItemDefinition.forId(c.getId()).getName()  + " from a Treasure Trail!");
		} else if (equip) {
			c = equipmentRewards[Misc.getRandom(equipmentRewards.length-1)];
		} else {
			c = consumableRewards[Misc.getRandom(consumableRewards.length-1)];
		}
		player.getInventory().add(c);
		String col = "<col=255>";
		player.getPacketSender().sendMessage(col+"<img=10> Your casket contained...");
		player.getPacketSender().sendMessage(col+a.getAmount()+"x "+ItemDefinition.forId(a.getId()).getName()+",").sendMessage(col+b.getAmount()+"x "+ItemDefinition.forId(b.getId()).getName()+",").sendMessage(col+c.getAmount()+"x "+ItemDefinition.forId(c.getId()).getName()+".");
		PlayerLogs.log("1 - hard clue caskets", player.getUsername()+" has looted: "+a.getAmount()+"x "+ItemDefinition.forId(a.getId()).getName()+", "+b.getAmount()+"x "+ItemDefinition.forId(b.getId()).getName()+", "+c.getAmount()+"x "+ItemDefinition.forId(c.getId()).getName()+".");
		player.getClickDelay().reset();
	}
	
	private static void awardCasket(Player player) {
		player.getPointsHandler().setClueSteps(0, false);
		player.getInventory().add(hardCasket, 1);
	}
	
	public static boolean handleClueDig(Player player) {
		for (int i = 0; i < CLUESCROLL.values().length; i++) {
			if (player.getInventory().contains(CLUESCROLL.values()[i].getClueId()) && player.getPosition().getX() == CLUESCROLL.values()[i].getDigTile().getX() && player.getPosition().getY() == CLUESCROLL.values()[i].getDigTile().getY()) {
				if (player.getRights().OwnerDeveloperOnly()) {
					player.getPacketSender().sendMessage("[debug] You are on: "+CLUESCROLL.values()[i].getDigTile().getX()+", "+CLUESCROLL.values()[i].getDigTile().getY()+", index: "+i);
				}
				player.getInventory().delete(CLUESCROLL.values()[i].getClueId(), 1);
				player.getPointsHandler().setClueSteps(1, true);
				int c = Misc.getRandom(1);
				if (player.getRights().OwnerDeveloperOnly()) {
					player.getPacketSender().sendMessage("[debug] You rolled a: "+c+" on Misc.getRandom(1)");
				}
				if ((player.getPointsHandler().getClueSteps() >= 3 && c == 1) || player.getPointsHandler().getClueSteps() >= 10) {
					awardCasket(player);
				} else {
					int newClue = Misc.getRandom(CLUESCROLL.values().length-1);
					player.getInventory().add(CLUESCROLL.values()[newClue].getClueId(), 1);
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleNpcUse(Player player, int npcid) {
		for (int i = 0; i < CLUESCROLL.values().length; i++) {
			if (player.getInventory().contains(CLUESCROLL.values()[i].getClueId()) && npcid == CLUESCROLL.values()[i].getNpcId()) {
				if (player.getRights().OwnerDeveloperOnly()) {
					player.getPacketSender().sendMessage("[debug] Your NPC ID: "+npcid+", CLUE npcId "+CLUESCROLL.values()[i].getNpcId()+", index: "+i);
				}
				player.getInventory().delete(CLUESCROLL.values()[i].getClueId(), 1);
				player.getPointsHandler().setClueSteps(1, true);
				int c = Misc.getRandom(1);
				if (player.getRights().OwnerDeveloperOnly()) {
					player.getPacketSender().sendMessage("[debug] You rolled a: "+c+" on Misc.getRandom(1)");
				}
				if ((player.getPointsHandler().getClueSteps() >= 3 && c == 1) || player.getPointsHandler().getClueSteps() >= 10) {
					awardCasket(player);
				} else {
					int newClue = Misc.getRandom(CLUESCROLL.values().length-1);
					player.getInventory().add(CLUESCROLL.values()[newClue].getClueId(), 1);
				}
				return true;
			}
		}
		return false;
	}
	
	public static void sendDropTableInterface(Player player) {
		try {
			
			List<Item> list = new ArrayList<Item>();
			for (int i = 0; i < ultraRareRewards.length; i++) {
				list.add(ultraRareRewards[i]);
			}
			for (int i = 0; i < thirdAgeRewards.length; i++) {
				list.add(thirdAgeRewards[i]);
			}
			for (int i = 0; i < equipmentRewards.length; i++) {
				list.add(equipmentRewards[i]);
			}
			for (int i = 0; i < consumableRewards.length; i++) {
				list.add(consumableRewards[i]);
			}
			for (int i = 0; i < fillerRewards.length; i++) {
				list.add(fillerRewards[i]);
			}
			
			resetInterface(player);
			
			player.getPacketSender().sendString(8144, "Clue Reward table").sendInterface(8134);
			
			int index = 0, start = 8147, cap = 8196, secondstart = 12174, secondcap = 12224, index2 = 0;
			boolean newline = false;
			
			for (int i = 0; i < list.size(); i++) {
			
			//for (int i = 0; i < drops.getDropList().length; i++) {
				
				
				if (ItemDefinition.forId(list.get(i).getId()) == null || ItemDefinition.forId(list.get(i).getId()).getName() == null) {
					continue;
				}
				
				int toSend = 8147 + index;
				
				if (index + start > cap) {
					newline = true;
				}
				
				if (newline) {
					toSend = secondstart + index2;
				}
				
				if (newline && toSend >= secondcap) {
					player.getPacketSender().sendMessage("<shad=ffffff>"+list.get(i).getAmount()+"x <shad=0>"
							+ Misc.getColorBasedOnValue(ItemDefinition.forId(list.get(i).getId()).getValue()*list.get(i).getAmount())
							+ ItemDefinition.forId(list.get(i).getId()).getName()+".");
					
					/*player.getPacketSender().sendMessage("<shad=ffffff>"+drops.getDropList()[i].getItem().getAmount() + "x <shad=0>"
							+ Misc.getColorBasedOnValue(drops.getDropList()[i].getItem().getDefinition().getValue()*drops.getDropList()[i].getItem().getAmount())
							+ drops.getDropList()[i].getItem().getDefinition().getName() + "<shad=-1>@bla@" + (player.getRights().OwnerDeveloperOnly() ? " at a drop rate of 1/"+(dropChance.getRandom() == DropChance.ALWAYS.getRandom() ? "1" : dropChance.getRandom()) : "")
							+ "<shad=ffffff>."); */
					continue;
				}
				player.getPacketSender().sendString(toSend, list.get(i).getAmount()+"x "
						+ Misc.getColorBasedOnValue(ItemDefinition.forId(list.get(i).getId()).getValue()*list.get(i).getAmount())
						+ ItemDefinition.forId(list.get(i).getId()).getName()+".");
				
				if (newline) {
					index2++;
				} else {
					index++;
				}
				
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void resetInterface(Player player) {
		for(int i = 8145; i < 8196; i++)
			player.getPacketSender().sendString(i, "");
		for(int i = 12174; i < 12224; i++)
			player.getPacketSender().sendString(i, "");
		player.getPacketSender().sendString(8136, "Close window");
	}
	
	
	
	
	
}


//