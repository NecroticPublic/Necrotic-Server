package com.ruse.model.definitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruse.GameSettings;
import com.ruse.model.Graphic;
import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.Equipment;
import com.ruse.util.JsonLoader;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.Achievements;
import com.ruse.world.content.Achievements.AchievementData;
import com.ruse.world.content.DropLog;
import com.ruse.world.content.DropLog.DropLogEntry;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.cluescrolls.CLUESCROLL;
import com.ruse.world.content.minigames.impl.WarriorsGuild;
import com.ruse.world.content.skill.impl.prayer.BonesData;
import com.ruse.world.content.skill.impl.summoning.CharmingImp;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * Controls the npc drops
 * 
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>, Gabbe &
 *         Samy
 * 
 */
public class NPCDrops {

	/**
	 * The map containing all the npc drops.
	 */
	private static Map<Integer, NPCDrops> dropControllers = new HashMap<Integer, NPCDrops>();

	public static JsonLoader parseDrops() {

		ItemDropAnnouncer.init();

		return new JsonLoader() {

			@Override
			public void load(JsonObject reader, Gson builder) {
				int[] npcIds = builder.fromJson(reader.get("npcIds"),
						int[].class);
				NpcDropItem[] drops = builder.fromJson(reader.get("drops"),
						NpcDropItem[].class);

				NPCDrops d = new NPCDrops();
				d.npcIds = npcIds;
				d.drops = drops;
				for (int id : npcIds) {
					dropControllers.put(id, d);
				}
			}

			@Override
			public String filePath() {
				return "./data/def/json/drops.json";
			}
		};
	}

	/**
	 * The id's of the NPC's that "owns" this class.
	 */
	private int[] npcIds;

	/**
	 * All the drops that belongs to this class.
	 */
	private NpcDropItem[] drops;

	/**
	 * Gets the NPC drop controller by an id.
	 * 
	 * @return The NPC drops associated with this id.
	 */
	public static NPCDrops forId(int id) {
		return dropControllers.get(id);
	}

	public static Map<Integer, NPCDrops> getDrops() {
		return dropControllers;
	}

	/**
	 * Gets the drop list
	 * 
	 * @return the list
	 */
	public NpcDropItem[] getDropList() {
		return drops;
	}

	/**
	 * Gets the npcIds
	 * 
	 * @return the npcIds
	 */
	public int[] getNpcIds() {
		return npcIds;
	}

	/**
	 * Represents a npc drop item
	 */
	public static class NpcDropItem {

		/**
		 * The id.
		 */
		private final int id;

		/**
		 * Array holding all the amounts of this item.
		 */
		private final int[] count;

		/**
		 * The chance of getting this item.
		 */
		private final int chance;

		/**
		 * New npc drop item
		 * 
		 * @param id
		 *            the item
		 * @param count
		 *            the count
		 * @param chance
		 *            the chance
		 */
		public NpcDropItem(int id, int[] count, int chance) {
			this.id = id;
			this.count = count;
			this.chance = chance;
		}

		/**
		 * Gets the item id.
		 * 
		 * @return The item id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the chance.
		 * 
		 * @return The chance.
		 */
		public int[] getCount() {
			return count;
		}

		/**
		 * Gets the chance.
		 * 
		 * @return The chance.
		 */
		public DropChance getChance() {
			switch (chance) {
			case 1:
				return DropChance.ALMOST_ALWAYS; // 1/2
			case 2:
				return DropChance.VERY_COMMON; // 1/5
			case 3:
				return DropChance.COMMON; // 1/15
			case 4:
				return DropChance.UNCOMMON; // 1/40
			case 5:
				return DropChance.RARE; // 1/100
			case 6:
				return DropChance.LEGENDARY; // 1/320
			case 7:
				return DropChance.LEGENDARY_2; // 1/410
			case 8:
				return DropChance.LEGENDARY_3; // 1/485
			case 9:
				return DropChance.LEGENDARY_4; // 1/680
			case 10:
				return DropChance.LEGENDARY_5; // 1/810
			case 11:
				return DropChance.LEGENDARY_6; // 1/950
			case 12:
				return DropChance.LEGENDARY_7; // 1/1100
			default:
				return DropChance.ALWAYS; // 1/1
			}
		}

		/**
		 * Gets the item
		 * 
		 * @return the item
		 */
		public Item getItem() {
			int amount = 0;
			for (int i = 0; i < count.length; i++)
				amount += count[i];
			if (amount > count[0])
				amount = count[0] + Misc.getRandom(count[1]);
			return new Item(id, amount);
		}
	}

	public enum DropChance {
		ALWAYS(0), ALMOST_ALWAYS(2), VERY_COMMON(5), COMMON(15), UNCOMMON(40), NOTTHATRARE(
				100), RARE(155), LEGENDARY(320), LEGENDARY_2(410), LEGENDARY_3(485), LEGENDARY_4(680), LEGENDARY_5(810), LEGENDARY_6(950), LEGENDARY_7(1100);
		
		
		DropChance(int randomModifier) {
			this.random = randomModifier;
		}

		private int random;

		public int getRandom() {
			return this.random;
		}
	}
	
	private static void resetInterface(Player player) {
		for(int i = 8145; i < 8196; i++)
			player.getPacketSender().sendString(i, "");
		for(int i = 12174; i < 12224; i++)
			player.getPacketSender().sendString(i, "");
		player.getPacketSender().sendString(8136, "Close window");
	}
	
	public static void sendDropTableInterface(Player player, int npcId) {
		try {
			
			NPCDrops drops = NPCDrops.forId(npcId);
			
			if (drops == null || drops.getDropList() == null) {
				player.getPacketSender().sendMessage("That NPC has no drop table. Error 951, "+npcId+".");
				return;
			}
			
			if (NpcDefinition.forId(npcId) == null || NpcDefinition.forId(npcId).getName() == null) {
				player.getPacketSender().sendMessage("Error 952, "+npcId+".");
				return;
			}
			
			resetInterface(player);
			
			player.getPacketSender().sendString(8144, NpcDefinition.forId(npcId).getName()+" drop table").sendInterface(8134);
			
			int index = 0, start = 8147, cap = 8196, secondstart = 12174, secondcap = 12224, index2 = 0;
			boolean newline = false;
			
			if (NpcDefinition.forId(npcId).getCombatLevel() >= 70) {
				player.getPacketSender().sendString(8147, "1x @blu@Clue scroll@bla@.");
				index++;
			}
			
			for (int i = 0; i < drops.getDropList().length; i++) {
				final DropChance dropChance = drops.getDropList()[i].getChance();
				
				if (drops.getDropList()[i].getItem().getId() <= 0
						|| drops.getDropList()[i].getItem().getId() > ItemDefinition.getMaxAmountOfItems()
						|| drops.getDropList()[i].getItem().getAmount() <= 0) {
					continue;
				}
				
				if (ItemDefinition.forId(drops.getDropList()[i].getItem().getId()) == null || ItemDefinition.forId(drops.getDropList()[i].getItem().getId()).getName() == null) {
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
					player.getPacketSender().sendMessage("<shad=ffffff>"+drops.getDropList()[i].getItem().getAmount() + "x <shad=0>"
							+ Misc.getColorBasedOnValue(drops.getDropList()[i].getItem().getDefinition().getValue()*drops.getDropList()[i].getItem().getAmount())
							+ drops.getDropList()[i].getItem().getDefinition().getName() + "<shad=-1>@bla@" + " at a drop rate of 1/"+(dropChance.getRandom() == DropChance.ALWAYS.getRandom() ? "1" : dropChance.getRandom())
							+ "<shad=ffffff>.");
					continue;
				}
				
					player.getPacketSender().sendString(toSend, drops.getDropList()[i].getItem().getAmount() + "x "
							+ Misc.getColorBasedOnValue(drops.getDropList()[i].getItem().getDefinition().getValue()*drops.getDropList()[i].getItem().getAmount())
							+ drops.getDropList()[i].getItem().getDefinition().getName() + "@bla@" 
							+ " at a drop rate of 1/"
							+(dropChance.getRandom() == DropChance.ALWAYS.getRandom() ? "1" : dropChance.getRandom())
							+ ".");
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
	
	public static void sendOldDropTableInterface(Player p, int npcId) {
		try {
			NPCDrops drops = NPCDrops.forId(npcId);
			if (drops == null || drops.getDropList() == null) {
				p.getPacketSender().sendMessage("That NPC has no drop table. Error 951, "+npcId+".");
				return;
			}
			if (NpcDefinition.forId(npcId) == null || NpcDefinition.forId(npcId).getName() == null) {
				p.getPacketSender().sendMessage("Error 952, "+npcId+".");
				return;
			}
			resetInterface(p);
			p.getPacketSender().sendString(8144, NpcDefinition.forId(npcId).getName()+" drop table").sendInterface(8134);
			p.getPacketSender().sendString(8147, "Drop table of @yel@" + NpcDefinition.forId(npcId).getName());
			p.getPacketSender().sendString(8148, "@str@                                                             ");
			int index = 2;
			/*if (drops == null || drops.getDropList() == null) {
				p.getPacketSender().sendString(8147, "No drop table for "+NpcDefinition.forId(npcId).getName()+".");
				p.getPacketSender().sendString(8148, "Error 951, "+npcId+".");
				return;
			}*/
			for (int i = 0; i < drops.getDropList().length; i++) {
				final DropChance dropChance = drops.getDropList()[i].getChance();
				if (drops.getDropList()[i].getItem().getId() <= 0
						|| drops.getDropList()[i].getItem().getId() > ItemDefinition.getMaxAmountOfItems()
						|| drops.getDropList()[i].getItem().getAmount() <= 0) {
					continue;
				}
				if (ItemDefinition.forId(drops.getDropList()[i].getItem().getId()) == null || ItemDefinition.forId(drops.getDropList()[i].getItem().getId()).getName() == null) {
					continue;
				}
				int toSend = 8147 + index > 8196 ? 12174 + index : 8147 + index;
				if (p.getRights().OwnerDeveloperOnly()) {
					p.getPacketSender().sendString(toSend, drops.getDropList()[i].getItem().getAmount() + "x "
							+ Misc.getColorBasedOnValue(drops.getDropList()[i].getItem().getDefinition().getValue())
							+ drops.getDropList()[i].getItem().getDefinition().getName() + "@bla@"
							+ " at a chance of 1/"
							+ (dropChance.getRandom() == DropChance.ALWAYS.getRandom() ? "1" : dropChance.getRandom())
							+ ".");
				} else {
				p	.getPacketSender().sendString(toSend, drops.getDropList()[i].getItem().getAmount() + "x "
						+ Misc.getColorBasedOnValue(drops.getDropList()[i].getItem().getDefinition().getValue())
						+ drops.getDropList()[i].getItem().getDefinition().getName() + "@bla@"
						+ ".");
				}
				index++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getDropTable(Player p, int npcId) {
		NPCDrops drops = NPCDrops.forId(npcId);
		if (drops == null) {
			p.getPacketSender().sendMessage("No drops were found. [Error 194510]");
			return;
		}

		if (NpcDefinition.forId(npcId).getCombatLevel() >= 70) {
			//CLUESCROLL
		}
		if (GameSettings.Halloween) {
			//Scythe, Pumpkin
		}
		if (GameSettings.Christmas2016) {
			//sled
		}
		
		//
		for (int i = 0; i < drops.getDropList().length; i++) {
			if (drops.getDropList()[i].getItem().getId() <= 0 || drops.getDropList()[i].getItem().getId() > ItemDefinition.getMaxAmountOfItems() || drops.getDropList()[i].getItem().getAmount() <= 0) {
				continue;
			}

			final DropChance dropChance = drops.getDropList()[i].getChance();
			p.getPacketSender().sendMessage(drops.getDropList()[i].getItem().getAmount()+"x "
			+ItemDefinition.forId(drops.getDropList()[i].getItem().getId()).getName()+" at a chance of 1/" + (dropChance.getRandom() == DropChance.ALWAYS.getRandom() ? "1" : dropChance.getRandom())+".");//+drops.getDropList()[i].getChance());
		}
		//
		
	}




	/**
	 * Drops items for a player after killing an npc. A player can max receive
	 * one item per drop chance.
	 * 
	 * @param p
	 *            Player to receive drop.
	 * @param npc
	 *            NPC to receive drop FROM.
	 */
	public static void dropItems(Player p, NPC npc) {
		if (npc.getLocation() == Location.WARRIORS_GUILD)
			WarriorsGuild.handleDrop(p, npc);
		NPCDrops drops = NPCDrops.forId(npc.getId());
		final Position npcPos = npc.getPosition().copy();
		if (drops == null) { 
			/* If an NPC's drops don't exist still give charms cuz lol */
			if (Misc.inclusiveRandom(1, 3) == 1) {
				drop(p, new Item(CharmingImp.GOLD_CHARM, 1), npc, npcPos, false);
			}
			if (Misc.inclusiveRandom(1, 4) == 1) {
				drop(p, new Item(CharmingImp.GREEN_CHARM, 1), npc, npcPos, false);
			}
			if (Misc.inclusiveRandom(1, 5) == 1) {
				drop(p, new Item(CharmingImp.CRIM_CHARM, 1), npc, npcPos, false);
			}
			if (Misc.inclusiveRandom(1, 6) == 1) {
				drop(p, new Item(CharmingImp.BLUE_CHARM, 1), npc, npcPos, false);
			}
			return;
		}
		final boolean goGlobal = p.getPosition().getZ() >= 0 && p.getPosition().getZ() < 4;
		final boolean ringOfWealth = p.getEquipment().get(Equipment.RING_SLOT).getId() == 2572;
		boolean[] dropsReceived = new boolean[14];
		boolean goldCharms = true;
		boolean crimsonCharms = true;
		boolean greenCharms = true;
		boolean blueCharms = true;

		/*if (drops.getDropList().length > 0 && p.getPosition().getZ() >= 0 && p.getPosition().getZ() < 4) {
			casketDrop(p, npc.getDefinition().getCombatLevel(), npcPos);
			goldCasketDrop(p, npcPos);
		}*/
		
		//p.getPacketSender().sendMessage("That mob's combat level was: "+npc.getDefinition().getCombatLevel());
		
		if (p.getLocation() == Location.GODWARS_DUNGEON) { //ecumenical key
			int count = enumenicalCount(p);

			if (count < 3) {

				int chance = count * 10 + 60;

				if (Misc.getRandom(chance) == 1) {
					// System.out.println("lol we dropped an ecu key");
					GroundItemManager.spawnGroundItem(p, new GroundItem(new Item(22053, 1), npcPos, p.getUsername(), false, 150, true, 200));
					p.getPacketSender().sendMessage("@cya@<shad=0><img=10> An ecumenical key has been dropped.");
				} /*
					 * else { System.out.println("Not spawned Ecumenical key");
					 * } System.out.println("Chance is 1/"+chance);
					 */
			}
		}
		
		if (npc.getDefinition().getCombatLevel() >= 70 && p.getLocation() != Location.GRAVEYARD) {
			dropClue(p, npcPos, npc);
		}
		
		if (p.getLocation() != Location.GRAVEYARD && GameSettings.Halloween) {
			dropScythe(p, npcPos, npc.getDefinition().getCombatLevel(), npc.getDefinition().getName());
			dropPumpkin(p, npcPos);
		}
		
		if (p.getLocation() != Location.GRAVEYARD && GameSettings.Christmas2016) {
			dropSled(p, npcPos, npc.getDefinition().getCombatLevel(), npc.getDefinition().getName());
		}

		for (int i = 0; i < drops.getDropList().length; i++) {
			if (drops.getDropList()[i].getItem().getId() <= 0 || drops.getDropList()[i].getItem().getId() > ItemDefinition.getMaxAmountOfItems() || drops.getDropList()[i].getItem().getAmount() <= 0) {
				continue;
			}
			
			if (drops.getDropList()[i].getItem().getId() == CharmingImp.GOLD_CHARM) {
				goldCharms = false;
			}
			if (drops.getDropList()[i].getItem().getId() == CharmingImp.GREEN_CHARM) {
				greenCharms = false;
			}
			if (drops.getDropList()[i].getItem().getId() == CharmingImp.CRIM_CHARM) {
				crimsonCharms = false;
			}
			if (drops.getDropList()[i].getItem().getId() == CharmingImp.BLUE_CHARM) {
				blueCharms = false;
			}

			final DropChance dropChance = drops.getDropList()[i].getChance();

			if (dropChance == DropChance.ALWAYS) {
				drop(p, drops.getDropList()[i].getItem(), npc, npcPos, goGlobal);
			} else {
				if(shouldDrop(dropsReceived, dropChance, ringOfWealth)) {
					drop(p, drops.getDropList()[i].getItem(), npc, npcPos, goGlobal);
					dropsReceived[dropChance.ordinal()] = true;
				}
			}
		}
		
		if (goldCharms && Misc.inclusiveRandom(1, 3) == 1) {
			//System.out.println("GONNA DROP A GO CHARM");
			drop(p, new Item(CharmingImp.GOLD_CHARM, 1), npc, npcPos, false);
		}
		if (greenCharms && Misc.inclusiveRandom(1, 4) == 1) {
			//System.out.println("GONNA DROP A GR CHARM");
			drop(p, new Item(CharmingImp.GREEN_CHARM, 1), npc, npcPos, false);
		}
		if (crimsonCharms && Misc.inclusiveRandom(1, 5) == 1) {
			//System.out.println("GONNA DROP A CR CHARM");
			drop(p, new Item(CharmingImp.CRIM_CHARM, 1), npc, npcPos, false);
		}
		if (blueCharms && Misc.inclusiveRandom(1, 6) == 1) {
			//System.out.println("GONNA DROP A BL CHARM");
			drop(p, new Item(CharmingImp.BLUE_CHARM,1 ), npc, npcPos, false);
		}
	}
	
	public static int enumenicalCount(Player p) {
		int count = 0;
		for (int i = 0; i < p.getBanks().length; i++) {
			if (p.getBanks()[i].contains(22053)) {
				count = count + p.getBanks()[i].getAmount(22053);
			}
		}
		if (p.getInventory().contains(22053)) {
			count = count + p.getInventory().getAmount(22053);
		}
		return count;
	}

	public static boolean shouldDrop(boolean[] b, DropChance chance, boolean ringOfWealth) {
		int random = chance.getRandom(); //pull the chance from the table
		if (ringOfWealth && random >= 60) { //if the chance from the table is greater or equal to 60, and player is wearing ring of wealth
			random -= (random / 10); //the chance from the table is lowered by 10% of the table's value
		}
		return !b[chance.ordinal()] && Misc.getRandom(random) == 1; //return true if random between 0 & table value is 1.
	}

	public static void drop(Player player, Item item, NPC npc, Position pos, boolean goGlobal) {
		if(npc.getId() == 2007 || npc.getId() == 2042 || npc.getId() == 2043 || npc.getId() == 2044) {
			pos = player.getPosition().copy();
		}
		if ((player.getInventory().contains(18337) || (player.getSkillManager().skillCape(Skill.PRAYER) && player.getBonecrushEffect())) && BonesData.forId(item.getId()) != null) {
			player.getPacketSender().sendGlobalGraphic(new Graphic(777), pos);
			if(BonesData.forId(item.getId()) == BonesData.FROSTDRAGON_BONES) {
				Achievements.doProgress(player, AchievementData.BURY_25_FROST_DRAGON_BONES);
				Achievements.doProgress(player, AchievementData.BURY_500_FROST_DRAGON_BONES);
			}
			if (player.getRights().isMember()) {
				player.getSkillManager().addExperience(Skill.PRAYER, BonesData.forId(item.getId()).getBuryingXP() * 2);
				return;
			} else {
				player.getSkillManager().addExperience(Skill.PRAYER, BonesData.forId(item.getId()).getBuryingXP());
				return;
			}
		}
		int itemId = item.getId();
		int amount = item.getAmount();

		if (itemId == 995 && player.getEquipment().get(Equipment.RING_SLOT).getId() == 22045) {
			if (!player.getInventory().contains(itemId) && player.getInventory().getFreeSlots() == 0) {
				player.getPacketSender().sendMessage("Your inventory is full, your Dragonstone ring (e) is unable to pick up coins!");
			} else {
				player.getInventory().add(itemId, amount);
				return;
			}
		}
		
		if (itemId == CharmingImp.GOLD_CHARM
				|| itemId == CharmingImp.GREEN_CHARM
				|| itemId == CharmingImp.CRIM_CHARM
				|| itemId == CharmingImp.BLUE_CHARM) {
			if ((player.getInventory().contains(6500) || player.getSkillManager().skillCape(Skill.DUNGEONEERING)) && CharmingImp.handleCharmDrop(player, itemId, amount)) {
				return;
			}
		}

		Player toGive = player;

		boolean ccAnnounce = false;
		if(Location.inMulti(player)) {
			if(player.getCurrentClanChat() != null && player.getCurrentClanChat().getLootShare()) {
				CopyOnWriteArrayList<Player> playerList = new CopyOnWriteArrayList<Player>();
				for(Player member : player.getCurrentClanChat().getMembers()) {
					if(member != null) {
						if(member.getPosition().isWithinDistance(player.getPosition())) {
							playerList.add(member);
						}
					}
				}
				if(playerList.size() > 0) {
					toGive = playerList.get(Misc.getRandom(playerList.size() - 1));
					if(toGive == null || toGive.getCurrentClanChat() == null || toGive.getCurrentClanChat() != player.getCurrentClanChat()) {
						toGive = player;
					}
					ccAnnounce = true;
				}
			}
		}
		
		if(itemId == 18778) { //Effigy, don't drop one if player already has one
			if(toGive.getInventory().contains(18778) || toGive.getInventory().contains(18779) || toGive.getInventory().contains(18780) || toGive.getInventory().contains(18781)) {
				return;
			} 
			for(Bank bank : toGive.getBanks()) {
				if(bank == null) {
					continue;
				}
				if(bank.contains(18778) || bank.contains(18779) || bank.contains(18780) || bank.contains(18781)) {
					return;
				}
			}
		}

		if (ItemDropAnnouncer.announce(itemId)) {
			String itemName = item.getDefinition().getName();
			String itemMessage = Misc.anOrA(itemName) + " " + itemName;
			String npcName = Misc.formatText(npc.getDefinition().getName());
			switch (itemId) {
			case 14484:
				itemMessage = "a pair of Dragon Claws";
				break;
			case 20000:
			case 20001:
			case 20002:
				itemMessage = itemName;
				break;
			}
			switch (npc.getId()) {
			case 50:
			case 3200:
			case 8133:
			case 4540:
			case 1160:
			case 8549:
				npcName = "The " + npcName + "";
				break;
			case 51:
			case 54:
			case 5363:
			case 8349:
			case 1592:
			case 1591:
			case 1590:
			case 1615:
			case 9463:
			case 9465:
			case 9467:
			case 1382:
			case 13659:
			case 11235:
				npcName = "" + Misc.anOrA(npcName) + " " + npcName + "";
				break;
			}
			String message = "<img=10><col=009966><shad=0> " + toGive.getUsername()
					+ " has just received " + itemMessage + " from " + npcName
					+ "!";
			World.sendMessage(message);

			if(ccAnnounce) {
				ClanChatManager.sendMessage(player.getCurrentClanChat(), "<col=16777215>[<col=255>Lootshare<col=16777215>]<col=3300CC> "+toGive.getUsername()+" received " + itemMessage + " from "+npcName+"!");
			}

			PlayerLogs.log(toGive.getUsername(), "" + toGive.getUsername() + " received " + itemMessage + " from " + npcName + "!");
		}
		
		GroundItemManager.spawnGroundItem(toGive, new GroundItem(item, pos,
				toGive.getUsername(), false, 150, goGlobal, 200));
		DropLog.submit(toGive, new DropLogEntry(itemId, item.getAmount()));
	}

	public static void simulateDrops(Player player, int npcid) {//todo: make this consider rarity, cba atm. Works for what i want.
		NPCDrops drop = NPCDrops.forId(npcid);
		for (int i = 0; i < drop.getDropList().length; i++) {
			if (drop.getDropList()[i].getItem().getId() <= 0 || drop.getDropList()[i].getItem().getId() > ItemDefinition.getMaxAmountOfItems() || drop.getDropList()[i].getItem().getAmount() <= 0) {
				continue;
			}
			player.getBank(1).add(drop.getDropList()[i].getItem());
		}
	}



	public static void casketDrop(Player player, int combat, Position pos) {
		int chance = (int) (1 + combat);
		if (Misc.getRandom(combat <= 50 ? 1300 : 1000) < chance) {
			GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(7956), pos, player.getUsername(), false, 150, true, 200));
		}
	}
	
	public static void dropScythe(Player player, Position pos, int combat, String npcName) {
		int chance = (500-combat);
		if (chance < 24) {
			chance = 24;
		}
		if (Misc.getRandom(chance) == 1 || (player.getUsername().equalsIgnoreCase("Debug") &&  player.getRights().isDeveloperOnly())) {
			GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(1419), pos, player.getUsername(), false, 150, true, 200));
			//player.getPacketSender().sendMessage("@or1@<shad=0>A Scythe falls to the ground nearby.");
			World.sendMessage("<img=10>@or1@<shad=0> " + player.getUsername()
			+ " has just received a Scythe from " + Misc.anOrA(npcName) + " " + npcName
			+ "! Happy Halloween!");
		}
		//player.getPacketSender().sendMessage("Chance = "+chance);
	}
	
	public static void dropSled(Player player, Position pos, int combat, String npcName) {
		int chance = (500-combat);
		if (chance < 24) {
			chance = 24;
		}
		if (Misc.getRandom(chance) == 1 || (player.getUsername().equalsIgnoreCase("Debug") &&  player.getRights().isDeveloperOnly())) {
			GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(4084), pos, player.getUsername(), false, 150, true, 200));
			//player.getPacketSender().sendMessage("@or1@<shad=0>A Scythe falls to the ground nearby.");
			World.sendMessage("<img=10>@red@<shad=0> " + player.getUsername()
			+ " has just received a Sled from " + Misc.anOrA(npcName) + " " + npcName
			+ "! @gre@Merry Christmas!");
		}
		//player.getPacketSender().sendMessage("Chance = "+chance);
	}
	
	public static void dropPumpkin(Player player, Position pos) {
		//player.getPacketSender().sendMessage("dropPumpkin");
		if (Misc.getRandom(10) == 1){
			if (player.doneHween2016() && (!player.getInventory().isFull() || player.getInventory().contains(1960))) {
				player.getInventory().add(new Item(1960));
			} else {
				GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(1960), pos, player.getUsername(), false, 150, true, 200));
			}
		}
	}
	
	public static void dropClue(Player player, Position pos, NPC npc) {
		boolean cont = true;
		for (int q = 0; q < player.getBanks().length; q++) {
			if (player.getBank(q).containsAny(CLUESCROLL.hardClueId)) {
				cont = false;
			}
		}
		if (player.getInventory().containsAny(CLUESCROLL.hardClueId)) {
			cont = false;
		}
		if (cont) {
			if(npc.getId() == 2007 || npc.getId() == 2042 || npc.getId() == 2043 || npc.getId() == 2044) {
				pos = player.getPosition().copy();
			}
			int rand = Misc.getRandom(100);
			if (rand == 1 || (rand == 2 && player.checkItem(Equipment.RING_SLOT, 2572))) {
				if (rand == 2) {
					player.getPacketSender().sendMessage("@mag@<shad=0><img=11> Your Ring of Wealth activates, spawning a clue!");
				} else {
					player.getPacketSender().sendMessage("@red@<shad=0><img=10> A clue scroll has appeared!");
				}
				GroundItemManager.spawnGroundItem(player, new GroundItem(new Item (CLUESCROLL.values()[Misc.getRandom((int) (CLUESCROLL.values().length-1))].getClueId()), pos, player.getUsername(), false, 150, true, 200));
			}
		}
	}
	
	public static void goldCasketDrop(Player player, Position pos) {
		if (Misc.getRandom(10) <= 1 ) {
			GroundItemManager.spawnGroundItem(player, new GroundItem(new Item (2714), pos, player.getUsername(), false, 150, true, 200));
		}
	}

	public static class ItemDropAnnouncer {

		private static List<Integer> ITEM_LIST;

		private static final int[] TO_ANNOUNCE = new int[] { 14484, 4224,
			11702, 11704, 11706, 11708, 11704, 11724, 11726, 11728, 11718,
			11720, 11722, 11730, 11716, 14876, 11286, 13427, 6731, 6737,
			6735, 4151, 2513, 15259, 13902, 13890, 13884, 13861, 13858,
			13864, 13905, 13887, 13893, 13899, 13873, 13879, 13876, 13870,
			6571, 14008, 14009, 14010, 14011, 14012, 14013, 14014, 14015,
			14016, 13750, 13748, 13746, 13752, 11335, 15486, 13870, 13873,
			13876, 13884, 13890, 13896, 13902, 13858, 13861, 13864, 13867,
			11995, 11996, 11997, 11978, 12001, 12002, 12003, 12004, 12005,
			12006, 11990, 11991, 11992, 11993, 11994, 11989, 11988, 11987,
			11986, 11985, 11984, 11983, 11982, 11981, 11979, 13659, 11235,
			20000, 20001, 20002, 15103, 6585, 12926, 12929, 15486, 16753, 17235, 16863, 22007, 13996, 12931,//drag boots
			15104, 15105, 15106, 12603, 12601, 12605, 19908, 22012,
			22012, 18786, 19780, 11335, 14479,
			18719,
			22034, //armadyl c'bow
			15109, //jar of dirt
			22033, 22049, 22050, //ZULRAH PETS
			22055, //Wildywyrm pet
			13999};

		private static void init() {
			ITEM_LIST = new ArrayList<Integer>();
			for (int i : TO_ANNOUNCE) {
				ITEM_LIST.add(i);
			}
		}

		public static boolean announce(int item) {
			return ITEM_LIST.contains(item);
		}
	}
}