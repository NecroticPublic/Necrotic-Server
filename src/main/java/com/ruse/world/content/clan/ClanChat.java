package com.ruse.world.content.clan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.model.RegionInstance;
import com.ruse.util.Stopwatch;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

/**
 * An instance of a clanchat channel, holding all fields.
 * @author Gabriel Hannason
 */
public class ClanChat {

	
	/*Ipublic static void RRTE(Player player, ClanChatRank rank) {
		player.getCurrentClanChat();
		if (ClanChat.RANK_REQUIRED_TO_ENTER == -1) {
			player.getPacketSender().sendMessage("GaergaegreG");
		}
	}*/

	public ClanChat(Player owner, String name, int index) {
		this.owner = owner;
		this.name = name;
		this.index = index;
		this.ownerName = owner.getUsername();
	}

	public ClanChat(String ownerName, String name, int index) {
		this.owner = World.getPlayerByName(ownerName);
		this.ownerName = ownerName;
		this.name = name;
		this.index = index;
	}

	private String name;
	private Player owner;
	private String ownerName;
	private final int index;
	private boolean lootShare;
	private boolean guild;
	private Stopwatch lastAction = new Stopwatch();

	private ClanChatRank[] rankRequirement = new ClanChatRank[7];
	private CopyOnWriteArrayList<Player> members = new CopyOnWriteArrayList<Player>();
	private CopyOnWriteArrayList<String> bannedNames = new CopyOnWriteArrayList<String>();
	private Map<String, ClanChatRank> rankedNames = new HashMap<String, ClanChatRank>();

	private boolean doingClanBarrows;
	private int height;
	private RegionInstance regionInstance;
	
	public Player getOwner() {
		return owner;
	}

	public ClanChat setOwner(Player owner) {
		this.owner = owner;
		return this;
	}

	public String getOwnerName() {
		if(name.equalsIgnoreCase("help")) {
			return "Support";
		}
		if(name.equalsIgnoreCase("necrotic")) {
			return "Crimson";
		}
		return ownerName;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public ClanChat setName(String name) {
		this.name = name;
		return this;
	}

	public boolean getLootShare() {
		return lootShare;
	}

	public void setLootShare(boolean lootShare) {
		this.lootShare = lootShare;
	}
	
	public boolean getGuild() {
		return guild;
	}
	
	public void setGuild(boolean guild) {
		this.guild = guild;
	}

	public Stopwatch getLastAction() {
		return lastAction;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public boolean doingClanBarrows() {
		return doingClanBarrows;
	}
	
	public void setDoingClanBarrows(boolean doingClanBarrows) {
		this.doingClanBarrows = doingClanBarrows;
	}
	
	public RegionInstance getRegionInstance() {
		return this.regionInstance;
	}
	
	public void setRegionInstance(RegionInstance regionInstance) {
		this.regionInstance = regionInstance;
	}

	public ClanChat addMember(Player member) {
		members.add(member);
		return this;
	}

	public ClanChat removeMember(String name) {
		for(int i = 0; i < members.size(); i++) {
			Player member = members.get(i);
			if(member == null)
				continue;
			if(member.getUsername().equals(name)) {
				members.remove(i);
				break;
			}
		}
		return this;
	}

	public ClanChatRank getRank(Player player) {
		return rankedNames.get(player.getUsername());
	}

	public ClanChat giveRank(Player player, ClanChatRank rank) {
		rankedNames.put(player.getUsername(), rank);
		return this;
	}

	public CopyOnWriteArrayList<Player> getMembers() {
		return members;
	}

	public Map<String, ClanChatRank> getRankedNames() {
		return rankedNames;
	}

	public CopyOnWriteArrayList<String> getBannedNames() {
		return bannedNames;
	}

	public void addBannedName(String name) {
		if(!bannedNames.contains(name)) {
			bannedNames.add(name);
			TaskManager.submit(new Task(1) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick == 2000) { // 20 minutes
						stop();
						return;
					}
					tick++;
				}

				@Override
				public void stop() {
					setEventRunning(false);
					bannedNames.remove(name);
				}
			});
		}
	}
	
	public ArrayList<Player> getClosePlayers(Position t) {
		ArrayList<Player> list = new ArrayList<Player>();
		
		for(Player p : getMembers()) {
			if(p == null || !Locations.goodDistance(p.getPosition(), t, 7)) {
				continue;
			}
			list.add(p);
		}
		
		return list;
	}

	public boolean isBanned(String name) {
		return bannedNames.contains(name);
	}

	public ClanChatRank[] getRankRequirement() {
		return rankRequirement;
	}

	public ClanChat setRankRequirements(int index, ClanChatRank rankRequirement) {
		this.rankRequirement[index] = rankRequirement;
		return this;
	}
	public static final int RANK_REQUIRED_TO_ENTER = 0, RANK_REQUIRED_TO_KICK = 1, RANK_REQUIRED_TO_TALK = 2, RANK_REQUIRED_TO_VISIT_GUILD = 3;
}