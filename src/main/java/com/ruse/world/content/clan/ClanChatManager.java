package com.ruse.world.content.clan;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruse.model.GameMode;
import com.ruse.model.Item;
import com.ruse.util.Misc;
import com.ruse.util.NameUtils;
import com.ruse.webhooks.discord.DiscordMessager;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPunishment;
import com.ruse.world.content.minigames.impl.TheSix;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * 100% Runescape clanchat system.
 * @author Gabriel Hannason
 */
public class ClanChatManager {

	private static final String FILE_DIRECTORY = "./data/saves/clans/";

	private static ClanChat[] clans = new ClanChat[4000];

	public static ClanChat[] getClans() {
		return clans;
	}

	public static ClanChat getClanChat(int index) {
		return clans[index];
	}

	public static ClanChat getClanChatChannel(Player player) {
		for(ClanChat clan : clans) {
			if(clan == null || clan.getOwnerName() == null)
				continue;
			if(clan.getOwnerName().equals(player.getUsername())) {
				return clan;
			}
		}
		return null;
	}
	

	public static void init() {
		try {
			if (new File(FILE_DIRECTORY).listFiles() == null) {
				System.out.println("Missing Necrotic.json in ClanChat directory. Exiting.");
				System.exit(0);
			}
			for (File file : (new File(FILE_DIRECTORY)).listFiles()) {
				if (!file.exists())
					continue;
				
				if (file.getName().equalsIgnoreCase("Necrotic.json")) {
					file.setReadable(true);
					file.setReadOnly();
					System.out.println("Set \"Necrotic.json\" to Read Only. (Anti-corruption)");
				}

				// Now read the properties from the json parser.
				try (FileReader fileReader = new FileReader(file)) {
					//System.out.println("1");
					JsonParser fileParser = new JsonParser();
					//System.out.println("2");
					JsonObject reader = (JsonObject) fileParser.parse(fileReader);
					//System.out.println("3");
					if (reader.has("name") && reader.has("owner") && reader.has("index")) {
						//System.out.println("checked ifs");
						ClanChat clan = new ClanChat(reader.get("owner").getAsString(), reader.get("name").getAsString(), reader.get("index").getAsInt());
						//System.out.println("pulled owner, name, and index");
						clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_ENTER, ClanChatRank.forId(reader.get("rank-required-to-enter").getAsInt()));
						//System.out.println("pulled rank required to enter");
						clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_KICK, ClanChatRank.forId(reader.get("rank-required-to-kick").getAsInt()));
						//System.out.println("pulled rank required to kick");
						clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_TALK, ClanChatRank.forId(reader.get("rank-required-to-talk").getAsInt()));
						//System.out.println("pulled rank required to talk");
						clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_VISIT_GUILD, ClanChatRank.forId(reader.get("rank-required-to-visit-guild").getAsInt()));
						//System.out.println("pulled rank required to visit guild");
						clan.setGuild(reader.get("owns-guild").getAsBoolean());
						//System.out.println("Loading clan chats....");
						clans[reader.get("index").getAsInt()] = clan;
					} 
				}
				//int totalRanks = input.readShort();
				//for (int i = 0; i < totalRanks; i++) {
				//	clan.getRankedNames().put(input.readUTF(), ClanChatRank.forId(input.read()));
				//}
			//	int totalBans = input.readShort();
			//	for (int i = 0; i < totalBans; i++) {
			//		clan.addBannedName(input.readUTF());
			//	}
				
			}
		} catch (IOException exception) {
			exception.printStackTrace();
			//ystem.out.println("Is this running?");
		}
	}

	public static void writeFile(ClanChat clan) {
		try {
			if (clan.getName().equalsIgnoreCase("Necrotic")) {
				System.out.println("Skipped writing Necrotic.json file because it's read only.");
				return;
			}
			File file = new File(FILE_DIRECTORY + clan.getName() + ".json");
			file.getParentFile().setWritable(true);
			if (!file.getParentFile().exists()) {
				try {
					file.getParentFile().mkdirs();
				} catch (SecurityException e) {
					System.out.println("Unable to create directory for clan data!");
				}
			}
			//System.out.println("starting to write");
			try (FileWriter writer = new FileWriter(file)) {
			//System.out.println("wr1");
				Gson builder = new GsonBuilder().setPrettyPrinting().create();
			//	System.out.println("wr2");
				JsonObject object = new JsonObject();
			//	System.out.println("wr3");
				object.addProperty("name", clan.getName());
			//	System.out.println("wr4");
				object.addProperty("owner", clan.getOwnerName());
			//	System.out.println("wr5");
				object.addProperty("index", clan.getIndex());
			//	System.out.println("wr6");								// if this isn't null											do this															if it is do this.
				object.addProperty("rank-required-to-enter", (clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER] != null ? clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER].ordinal() : -1));
			//	System.out.println(clan.RANK_REQUIRED_TO_ENTER);
			//	System.out.println("wr7");
				object.addProperty("rank-required-to-talk", (clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK] != null ? clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK].ordinal() : -1));
			//	System.out.println(clan.RANK_REQUIRED_TO_TALK);
			//	System.out.println("wr7.1");
				object.addProperty("rank-required-to-kick", (clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK] != null ? clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK].ordinal() : -1));
			//	System.out.println(clan.RANK_REQUIRED_TO_KICK);
			//	System.out.println("wr7.2");
				object.addProperty("rank-required-to-visit-guild",(clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK] != null ? clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK].ordinal() : -1));
			//	System.out.println(clan.RANK_REQUIRED_TO_VISIT_GUILD);
			//	System.out.println("wr7.3");
				object.addProperty("ranked-player-count", clan.getRankedNames().size());
			//	System.out.println("wr7.4");
				for (Entry<String, ClanChatRank> iterator : clan.getRankedNames().entrySet()) {
			//		System.out.println("wr8");
					String name = iterator.getKey();
			//		System.out.println("wr8.1");
					int rank = iterator.getValue().ordinal();
			//		System.out.println("wr8.2");
					object.addProperty(name, rank);
			//		System.out.println("wr8.3");
				}
			//	System.out.println("wr9");
				object.addProperty("owns-guild", clan.getGuild()); //change false later
			//	System.out.println("wr10");
				writer.write(builder.toJson(object));
			//	System.out.println("wr11");
				writer.close();
			} catch (Exception e) {
				System.out.println(e);
			}
			//	System.out.println("wr12");
			} catch (Exception e) {
				System.out.println(e);
			}
	}



	public static void save() {
		for (ClanChat clan : clans) {
			if (clan != null) {
				writeFile(clan);
			}
		}
	}

	public static void createClan(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		if(getClanChatChannel(player) != null) {
			player.getPacketSender().sendMessage("You have already created a clanchat channel.");
			return;
		}
		File file = new File(FILE_DIRECTORY + player.getUsername()+".json");
		if (file.exists())
			file.delete();
		ClanChat createdCc = create(player);
		if(createdCc != null) {
			if(player.getCurrentClanChat() == null) {
				join(player, createdCc);
			}
			player.getPacketSender().sendMessage("You now have a clanchat channel. To enter the chat, simply use your name as keyword.");
		}
	}

	public static void deleteClan(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		if (player.getCurrentClanChat() != null) {
			player.getPacketSender().sendMessage("Please leave the clanchat channel you are currently in before doing this.");
			return;
		}
		if(getClanChatChannel(player) == null) {
			player.getPacketSender().sendMessage("You have not created a clanchat channel yet.");
			return;
		}
		delete(player);
	}

	public static ClanChat create(Player player) {
		File file = new File(FILE_DIRECTORY + player.getUsername()+".json");
		if (file.exists()) {
			player.getPacketSender().sendMessage("Your clan channel is already public!");
			return null;
		}
		int index = getIndex();
		if (index == -1) { // Too many clans
			player.getPacketSender().sendMessage("An error occured! Please contact an administrator and report this.");
			return null;
		}
		clans[index] = new ClanChat(player, player.getUsername(), index);
		clans[index].getRankedNames().put(player.getUsername(), ClanChatRank.OWNER);
		writeFile(clans[index]);
		return clans[index];
	}

	public static void join(Player player, String channel) {
		if(channel == null || channel.equals("null"))
			return;
		if (player.getCurrentClanChat() != null) {
			player.getPacketSender().sendMessage("You are already in a clan channel.");
			return;
		}
		channel = channel.toLowerCase();
		for (ClanChat clan : clans) {
			if (clan != null) {
				if(clan.getName().toLowerCase().equals(channel)) {
					join(player, clan);
					return;
				}
			}
		}
		for (ClanChat clan : clans) {
			if (clan != null) {
				if(clan.getOwnerName().toLowerCase().equals(channel)) {
					join(player, clan);
					return;
				}
			}
		}
		player.getPacketSender().sendMessage("That channel does not exist.");
	}

	public static void updateList(ClanChat clan) {
		Collections.sort(clan.getMembers(), new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				ClanChatRank rank1 = clan.getRank(o1);
				ClanChatRank rank2 = clan.getRank(o2);
				if(rank1 == null && rank2 == null) {
					return 1;
				}
				if(rank1 == null && rank2 != null) {
					return 1;
				} else if(rank1 != null && rank2 == null) {
					return -1;
				}
				if(rank1.ordinal() == rank2.ordinal()) {
					return 1;
				}
				if(rank1 == ClanChatRank.OWNER) {
					return -1;
				} else if(rank2 == ClanChatRank.OWNER) {
					return 1;
				}
				if(rank1.ordinal() > rank2.ordinal()) {
					return -1;
				}
				return 1;
			}
		});
		for (Player member : clan.getMembers()) {
			if (member != null) {
				int childId = 29344;
				for (Player others : clan.getMembers()) {
					if (others != null) {
						ClanChatRank rank = clan.getRank(others);
						int image = -1;
						if(rank != null) {
							image = 841 + rank.ordinal();
						}
						String prefix = image >= 0 ? ("<img=" + (image) +  "> ") : "";
						member.getPacketSender().sendString(childId, prefix + others.getUsername());
						childId++;
					}
				}
				for (int i = childId; i < 29444; i++) {
					member.getPacketSender().sendString(i, "");
				}
				ClanChatRank rank = clan.getRank(member);
				if(rank != null) {
					if(rank == ClanChatRank.OWNER || rank == ClanChatRank.STAFF) {
						member.getPacketSender().sendClanChatListOptionsVisible(2); //Kick/demote/promote options
					} else if(clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK] != null && rank.ordinal() >= clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK].ordinal()) {
						member.getPacketSender().sendClanChatListOptionsVisible(1); //only kick option
					} else {
						member.getPacketSender().sendClanChatListOptionsVisible(0); //no options
					}
				}
			}
		}
	}

	public static void sendMessage(Player player, String message) {
		ClanChat clan = player.getCurrentClanChat();
		if (clan == null) {
			player.getPacketSender().sendMessage("You're not in a clanchat channel.");
			return;
		}
		if (PlayerPunishment.Jail.isJailed(player)) {
			player.getPacketSender().sendMessage("You can talk in the clan as soon as you're out of jail.");
			return;
		}
		ClanChatRank rank = clan.getRank(player);
		if(clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK] != null) {
			if (rank == null || rank.ordinal() < clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK].ordinal()) {
				player.getPacketSender().sendMessage("You do not have the required rank to speak in this channel.");
				return;
			}
		}
		String bracketColor = "<col=16777215>";
		String clanNameColor = "<col=255>";
		String nameColor = "@red@";
		String chatColor = "<col=993D00>";
		for (Player memberPlayer : clan.getMembers()) {
			if (memberPlayer != null) {
				if(memberPlayer.getRelations().getIgnoreList().contains(player.getLongUsername()))
					continue;
				int img = player.getRights().ordinal();
				int ironimg = -1;
				if (player.getGameMode() == GameMode.IRONMAN) {
					ironimg = 840;
				} else if (player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
					ironimg = 839;
				}
				String ironSpace = "";
				if (ironimg > 1) {
					ironSpace = " ";
				} 
				
				String formatted = String.format("%02d", clan.getName().length()+1);
				
				String rankImg = img > 0 ? " <img="+img+">" : "";
				memberPlayer.getPacketSender()
						.sendMessage(":clan:"+formatted + /*bracketColor +*/ "[" + /*clanNameColor +*/ clan.getName() + /*bracketColor +*/ "]"
						+ /*nameColor +*/ "<img=" + ironimg + ">" + rankImg +" "
						+ NameUtils.capitalizeWords(player.getUsername()) + ": " + /*chatColor
						+*/ NameUtils.capitalize(message));
			}
		}
		PlayerLogs.log(player.getUsername(), "(CC) " +(player.getCurrentClanChat() != null && player.getClanChatName() != null ? player.getClanChatName() : "NULL")+ ". Said: "+StringUtils.capitalize(message.toLowerCase()));
		DiscordMessager.sendClanMessage("(CC) " +(player.getCurrentClanChat() != null && player.getClanChatName() != null ? player.getClanChatName() : "NULL")+ ". **"+player.getUsername()+"** said: "+StringUtils.capitalize(message.toLowerCase()));
	
	}

	public static void sendMessage(ClanChat clan, String message) {
		for (Player member : clan.getMembers()) {
			if (member != null) {
				member.getPacketSender().sendMessage(message);
			}
		}
	}

	public static void leave(Player player, boolean kicked) {
		final ClanChat clan = player.getCurrentClanChat();
		if (clan == null) {
			player.getPacketSender().sendMessage("You are not in a clanchat channel.");
			return;
		}
		if(player.doingClanBarrows()) {
			TheSix.leave(player, true);
		}
		player.getPacketSender().sendString(29340, "Talking in: N/A");
		player.getPacketSender().sendString(29450, "Owner: N/A");
		player.getPacketSender().sendString(29454, "Lootshare: N/A");
		player.setCurrentClanChat(null);
		clan.removeMember(player.getUsername());
		for (int i = 29344; i < 29444; i++) {
			player.getPacketSender().sendString(i, "");
		}
		player.getPacketSender().sendClanChatListOptionsVisible(0);
		updateList(clan);
		player.getPacketSender().sendMessage(kicked ? "You have been kicked from the channel." : "You have left the channel.");
	}

	private static void join(Player player, ClanChat clan) {
		if (clan.getOwnerName().equals(player.getUsername())) {
			if (clan.getOwner() == null) {
				clan.setOwner(player);
			}
			clan.giveRank(player, ClanChatRank.OWNER);
		}
		player.getPacketSender().sendMessage("Attempting to join channel...");
		if (clan.getMembers().size() >= 100) {
			player.getPacketSender().sendMessage("This clan channel is currently full.");
			return;
		}
		if(clan.isBanned(player.getUsername())) {
			player.getPacketSender().sendMessage("You're currently banned from using this channel. Bans expire every 20 minutes.");
			return;
		}
		checkFriendsRank(player, clan, false);
		ClanChatRank rank = clan.getRank(player);
		if (player.getRights().isStaff()) {
			if(rank == null || rank != ClanChatRank.OWNER) {
				rank = ClanChatRank.STAFF;
				clan.giveRank(player, ClanChatRank.STAFF);
			}
		} else {
			if(rank != null && rank == ClanChatRank.STAFF) {
				clan.giveRank(player, null);
			}
		}
		if (clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER] != null) {
			if (rank == null || clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER].ordinal() > rank.ordinal()) {
				player.getPacketSender().sendMessage("Your rank is not high enough to enter this channel.");
				return;
			}
		}
		player.setCurrentClanChat(clan);
		player.setClanChatName(clan.getName());
		String clanName = NameUtils.capitalizeWords(clan.getName());
		clan.addMember(player);
		player.getPacketSender().sendString(29340, "Talking in: @whi@" + clanName);
		player.getPacketSender().sendString(29450, "Owner: " + NameUtils.capitalizeWords(clan.getOwnerName()));
		player.getPacketSender().sendString(29454, "Lootshare: "+getLootshareStatus(clan));
		player.getPacketSender().sendMessage("Now talking in "+clan.getOwnerName()+"'s channel.");
		player.getPacketSender().sendMessage("To talk start each line of chat with the / symbol.");
		updateList(clan);
	}

	public static void checkFriendsRank(Player player, ClanChat chat, boolean update) {
		ClanChatRank rank = chat.getRank(player);
		if(rank == null) {
			if(chat.getOwner() != null && chat.getOwner().getRelations().isFriendWith(player.getUsername())) {
				chat.giveRank(player, ClanChatRank.FRIEND);
				if(update) {
					updateList(chat);
				}
			}
		} else {
			if(rank == ClanChatRank.FRIEND && chat.getOwner() != null && !chat.getOwner().getRelations().isFriendWith(player.getUsername())) {
				chat.giveRank(player, null);
				if(update) {
					updateList(chat);
				}
			}
		}
	}

	public static void delete(Player player) {
		try {
			ClanChat clan = getClanChatChannel(player);
			File file = new File(FILE_DIRECTORY + clan.getName().replaceAll(" ", "\\ "));
			for (Player member : clan.getMembers()) {
				if (member != null) {
					leave(member, true);
					member.setClanChatName("");
				}
			}
			if(player.getClanChatName() != null && clan.getName() != null && player.getClanChatName().equalsIgnoreCase(clan.getName())) {
				leave(player, false);
				player.setClanChatName("");
			}
			clans[clan.getIndex()] = null;
			file.delete();
			player.getPacketSender().sendMessage("Your clanchat channel was successfully deleted.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setName(Player player, String newName) {
		final ClanChat clan = getClanChatChannel(player);
		if (clan == null) {
			player.getPacketSender().sendMessage("You need to have a clan channel to do this.");
			return;
		}
		if(newName.length() == 0)
			return;
		if (newName.length() > 12)
			newName = newName.substring(0, 11);
		if(new File(FILE_DIRECTORY + newName).exists()) {
			player.getPacketSender().sendMessage("That clanchat name is already taken.");
			return;
		}
		if(clan.getLastAction().elapsed(5000) || player.getRights().isStaff()) {
			new File(FILE_DIRECTORY + clan.getName()+".json").delete();
			clan.setName(NameUtils.capitalizeWords(newName));
			for(Player member : clan.getMembers()) {
				if(member == null)
					continue;
				member.setClanChatName(clan.getName());
				member.getPacketSender().sendString(29340, "Talking in: @whi@" + clan.getName());
			}
			clanChatSetupInterface(player, false);
			writeFile(clan);
			clan.getLastAction().reset();
		} else {
			player.getPacketSender().sendMessage("You need to wait a few seconds between every clanchat action.");
		}
	}

	public static void kick(Player player, Player target) {
		ClanChat clan = player.getCurrentClanChat();
		if (clan == null) {
			player.getPacketSender().sendMessage("You're not in a clan channel.");
			return;
		}
		final ClanChatRank rank = clan.getRank(player);
		if (rank == null || rank != ClanChatRank.STAFF && clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK] != null && rank.ordinal() < clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK].ordinal()) {
			player.getPacketSender().sendMessage("You do not have the required rank to kick this player.");
			return;
		}
		for (Player member : clan.getMembers()) {
			if (member != null && member.equals(target)) {
				ClanChatRank memberRank = clan.getRank(member);
				if (memberRank != null) {
					if(memberRank == ClanChatRank.STAFF || memberRank == ClanChatRank.OWNER) {
						player.getPacketSender().sendMessage("That player cannot be kicked.");
						break;
					}
					if(rank.ordinal() < memberRank.ordinal()) {
						player.getPacketSender().sendMessage("You cannot kick a player who has a higher rank than you!");
						break;
					}
				}
				clan.addBannedName(member.getUsername());
				leave(member, true);
				sendMessage(player.getCurrentClanChat(), "<col=16777215>[<col=255>"+clan.getName() +"<col=16777215>]<col=3300CC> "+member.getUsername()+" has been kicked from the channel by "+player.getUsername()+".");
				break;
			}
		}
	}

	public static void handleMemberOption(Player player, int index, int menuId) {
		if ((player.getCurrentClanChat() == null || !player.getCurrentClanChat().getOwnerName().equals(player.getUsername())) && menuId != 1) {
			player.getPacketSender().sendMessage("Only the clanchat owner can do that.");
			return;
		}
		Player target = getPlayer(index, player.getCurrentClanChat());
		if(target == null || target.equals(player)) {
			return;
		}
		switch(menuId) {
		case 8:
		case 7:
		case 6:
		case 5:
		case 4:
		case 3:
			ClanChatRank rank = ClanChatRank.forMenuId(menuId);
			ClanChatRank targetRank = player.getCurrentClanChat().getRank(target);
			if(targetRank != null) {
				if(targetRank == rank) {
					player.getPacketSender().sendMessage("That player already has that rank.");
					return;
				}
				if(targetRank == ClanChatRank.STAFF) {
					player.getPacketSender().sendMessage("That player cannot be promoted or demoted.");
					return;
				}
			}
			if(player.getCurrentClanChat().getLastAction().elapsed(5000) || player.getRights().isStaff()) {
				player.getCurrentClanChat().giveRank(target, rank);
				updateList(player.getCurrentClanChat());
				sendMessage(player.getCurrentClanChat(), "<col=16777215>[<col=255>"+player.getCurrentClanChat().getName() +"<col=16777215>]<col=3300CC> "+target.getUsername()+" has been given the rank: "+Misc.formatText(rank.name().toLowerCase())+".");
				player.getCurrentClanChat().getLastAction().reset();
			} else {
				player.getPacketSender().sendMessage("You need to wait a few seconds between every clanchat action.");
			}
			break;
		case 2:
			targetRank = player.getCurrentClanChat().getRank(target);
			if(targetRank == null) {
				player.getPacketSender().sendMessage("That player has no rank.");
				return;
			}
			if(targetRank == ClanChatRank.STAFF) {
				player.getPacketSender().sendMessage("That player cannot be promoted or demoted.");
				return;
			}
			if(player.getCurrentClanChat().getLastAction().elapsed(5000) || player.getRights().isStaff()) {
				player.getCurrentClanChat().getRankedNames().remove(target.getUsername());
				checkFriendsRank(target, player.getCurrentClanChat(), false);
				updateList(player.getCurrentClanChat());
				sendMessage(player.getCurrentClanChat(), "<col=16777215>[<col=255>"+player.getCurrentClanChat().getName() +"<col=16777215>]<col=3300CC> "+target.getUsername()+" has been demoted from his rank.");
				player.getCurrentClanChat().getLastAction().reset();
			} else {
				player.getPacketSender().sendMessage("You need to wait a few seconds between every clanchat action.");
			}
			break;
		case 1:
			kick(player, target);
			break;
		}
	}

	public static boolean dropShareLoot(Player player, NPC npc, Item itemDropped) {
		/*	ClanChat clan = player.getFields().getClanChat();
		if (clan != null) {
			boolean received = false;
			List<Player> players = getPlayersWithinPosition(clan, npc.getPosition());
			String green = "<col=" + ClanChatMessageColor.GREEN.getRGB()[player.getFields().rgbIndex] + ">";
			if (clan.isItemSharing() && itemDropped.getId() != 995) {
				Player rewarded = players.size() > 0 ? players.get(MathUtils.random(players.size() - 1)) : null;
				if (rewarded != null) {
					rewarded.getPacketSender().sendMessage(green + "You have received " + itemDropped.getAmount() + "x " + itemDropped.getDefinition().getName() + ".");
					received = true;
				}
			}
			if (clan.isCoinSharing() && itemDropped.getId() == 995) {
				for (Item drop : npc.getDrops()) {
					if ((drop.getDefinition().getValue() * drop.getAmount()) < 50000) {
						GroundItem groundItem = new GroundItem(drop, npc.getPosition().copy());
						GameServer.getWorld().register(groundItem, player);
						continue;
					}
					int amount = (int) (ItemDefinition.forId(drop.getId()).getValue() / players.size());
					Item split = new Item(995, amount);
					for (Player member : players) {
						GroundItem groundItem = new GroundItem(split.copy(), npc.getPosition().copy());
						GameServer.getWorld().register(groundItem, member);
						member.getPacketSender().sendMessage(green + "You have received " + amount + "x " + split.getDefinition().getName() + " as part of a split drop.");
					}
				}
			} else if(!clan.isItemSharing() && !clan.isCoinSharing() || !received)
				return false;
		} else
			return false;*/
		return false;
	}

	public static void toggleLootShare(Player player) {
		final ClanChat clan = player.getCurrentClanChat();
		if (clan == null) {
			player.getPacketSender().sendMessage("You're not in a clan channel.");
			return;
		}
		if(!player.getRights().isStaff()) {
			if(clan.getOwner() == null)
				return;
			if (!clan.getOwner().getUsername().equals(player.getUsername())) {
				player.getPacketSender().sendMessage("Only the owner of the channel has the power to do this.");
				return;
			}
		}
		if(clan.getLastAction().elapsed(5000) || player.getRights().isStaff()) {
			if (clan.getName().equalsIgnoreCase("Necrotic")) {
				player.getPacketSender().sendMessage("You can't change Lootshare settings in this clan.");
				return;
			}
			clan.setLootShare(!clan.getLootShare());
			sendMessage(clan, "<col=16777215>[<col=255>"+clan.getName() +"<col=16777215>] <col=3300CC>"+player.getUsername()+" has "+(clan.getLootShare() ? "enabled" : "disabled")+" Lootshare.");
			for (Player member : clan.getMembers()) {
				if (member != null) {
					member.getPacketSender().sendString(29454, "Lootshare: "+getLootshareStatus(clan));
				}
			}
			clan.getLastAction().reset();
		} else {
			player.getPacketSender().sendMessage("You need to wait a few seconds between every clanchat action.");
		}
	}

	private static String getLootshareStatus(ClanChat clan) {
		return clan.getLootShare() ? "@gre@On" : "Off";
	}

	private static int getIndex() {
		for (int i = 0; i < clans.length; i++) {
			if (clans[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public static boolean handleClanChatSetupButton(Player player, int id) { //check
		if(player.getInterfaceId() == 40172) {
			final ClanChat clan = getClanChatChannel(player);
			if (clan == null) {
				return true;
			}
			int l = -17529 - id;
			switch(id) {
			case -18281:// anyone
			case -17529: // friend 0
			case -17530:// recruit 1
			case -17531:// corp 2
			case -17532:// srg 3
			case -17533:// lt 4
			case -17534:// cap 5
			case -17535:// general 6
			case -17536:// only me 7
				System.out.println(l + " is the value of I");
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_ENTER, id == -18281 ? null : ClanChatRank.forId(l));
				player.getPacketSender().sendMessage("You have changed your clanchat channel's settings.");
				if (clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER] != null) {
					for(Player member : clan.getMembers()) {
						if(member == null)
							continue;
						ClanChatRank rank = clan.getRank(member);
						if (rank == null || clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER].ordinal() > rank.ordinal() && rank != ClanChatRank.STAFF) {
							member.getPacketSender().sendMessage("Your rank is not high enough to be in this channel.");
							leave(member, false);
							player.getPacketSender().sendMessage("@red@Warning! Changing that setting kicked the player "+member.getUsername()+" from the chat because").sendMessage("@red@ they do not have the required rank to be in the chat.");;
						}
					}
				}
				clanChatSetupInterface(player, false);
				writeFile(clan);
				return true;
			/*	System.out.println(player.getUsername()+" just clicked the button to set joining to "+ClanChatRank.forId(l));
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_ENTER, ClanChatRank.forId(l));
				clanChatSetupInterface(player, false);
				writeFile(clan);
				return true;*/
			case -18278:
			case -17519:
			case -17520:
			case -17521:
			case -17522:
			case -17523:
			case -17524:
			case -17525:
			case -17526:
				l = -17519 - id;
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_TALK, id == -18278 ? null : ClanChatRank.forId(l));
				System.out.println(player.getUsername()+" just clicked the button to set rank 2 talk"+ClanChatRank.forId(l));
				player.getPacketSender().sendMessage("You have changed your clanchat channel's settings.");
				clanChatSetupInterface(player, false);
				writeFile(clan);
				return true;
			case -18275:
			case -17510:
			case -17511:
			case -17512:
			case -17513:
			case -17514:
			case -17515:
				l = (-17510 - id) + 1;
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_KICK, id == -18275 ? null : ClanChatRank.forId(l));
				System.out.println(player.getUsername()+" just clicked the button to set rank to kick "+ClanChatRank.forId(l));
				player.getPacketSender().sendMessage("You have changed your clanchat channel's settings.");
				clanChatSetupInterface(player, false);
				updateList(clan);
				writeFile(clan);
				return true;
			}
		}
		return false;
	}

	public static void clanChatSetupInterface(Player player, boolean check) {
		player.getPacketSender().sendInterfaceRemoval();
		ClanChat channel = getClanChatChannel(player);
		if(check) {
			if(channel == null) {
				player.getPacketSender().sendMessage("You have not created a clanchat channel yet.");
				return;
			}
		}
		player.getPacketSender().sendString(47814, channel.getName());
		if (channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER] == null) {
			player.getPacketSender().sendString(47815, "Anyone");
		} else {
			player.getPacketSender().sendString(47815, Misc.formatText(channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER].name().toLowerCase())+"+");
		}

		if (channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK] == null) {
			player.getPacketSender().sendString(47816, "Anyone");
		} else {
			player.getPacketSender().sendString(47816, Misc.formatText(channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK].name().toLowerCase())+"+");
		}

		if (channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK] == null) {
			player.getPacketSender().sendString(47817, "Only me");
		} else {
			player.getPacketSender().sendString(47817, Misc.formatText(channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK].name().toLowerCase())+"+");
		}

		player.getPacketSender().sendInterface(40172);
	}

	public static void handleLogin(Player player) {
		resetInterface(player);
		ClanChatManager.join(player, player.getClanChatName());
	}

	public static void resetInterface(Player player) {
		player.getPacketSender().sendString(29340, "Talking in: N/A");
		player.getPacketSender().sendString(29450, "Owner: N/A");
		player.getPacketSender().sendString(29454, "Lootshare: N/A");
		for (int i = 29344; i < 29444; i++) {
			player.getPacketSender().sendString(i, "");
		}
	}

	public static Player getPlayer(int index, ClanChat clan) {
		int clanIndex = 0;
		for (Player members : clan.getMembers()) {
			if (members != null) {
				if(clanIndex == index) {
					return members;
				}
				clanIndex++;
			}
		}
		return null;
	}
}
