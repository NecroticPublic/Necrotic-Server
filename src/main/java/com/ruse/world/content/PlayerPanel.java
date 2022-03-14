package com.ruse.world.content;

import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.randomevents.EvilTree;
import com.ruse.world.content.randomevents.ShootingStar;
import com.ruse.world.content.skill.impl.slayer.SlayerTasks;
import com.ruse.world.entity.impl.player.Player;

public class PlayerPanel {

	private static int FIRST_STRING = 39159;
	private static int LAST_STRING = 39210;
	
	public static void refreshPanel(Player player) {
		
		
		String[] Messages = new String[]{
			"@red@ - @whi@ World Overview",
			"@or2@Players Online:   @or2@[ @yel@"+(int)(World.getPlayers().size())+"@or2@ ]",
			(ShootingStar.CRASHED_STAR == null ? "@or2@Crashed Star:  @red@Cleared" : "@or2@Crashed Star:  @gre@"+ShootingStar.CRASHED_STAR.getStarLocation().playerPanelFrame),
			(EvilTree.SPAWNED_TREE == null ? "@or2@Evil Tree:  @red@Cleared" : "@or2@Evil Tree:  @gre@"+EvilTree.SPAWNED_TREE.getTreeLocation().playerPanelFrame),
			(Wildywyrm.wyrmAlive ? "@or2@WildyWyrm:  @gre@"+Wildywyrm.getPlayerPanelHint() : "@or2@WildyWyrm:  @red@Dead"),
			(WellOfGoodwill.isActive() ? "@or2@Well of Goodwill:  @gre@On" : "@or2@Well of Goodwill:  @red@Off"),
			"",
			"@or3@ - @whi@ Account Information",
			//"@yel@Difficulty: @whi@"+Misc.capitalizeString(player.getDifficulty().toString().toLowerCase()),
			"@or2@Mode:  @yel@"+Misc.capitalizeString(player.getGameMode().toString().toLowerCase().replace("_", " ")),
			"@or2@Claimed: @yel@$"+player.getAmountDonated(),
			"@or2@Time played:  @yel@"+Misc.getTimePlayed((player.getTotalPlayTime() + player.getRecordedLogin().elapsed())),
			"",
			"@or3@ - @whi@ Statistics",
			"@or2@Prestige Points: @yel@"+player.getPointsHandler().getPrestigePoints(),
			"@or2@Commendations: @yel@ "+player.getPointsHandler().getCommendations(),
			"@or2@Loyalty Points: @yel@"+(int)player.getPointsHandler().getLoyaltyPoints(),
			"@or2@Dung. Tokens: @yel@ "+player.getPointsHandler().getDungeoneeringTokens(),
			"@or2@Voting Points: @yel@ "+player.getPointsHandler().getVotingPoints(),
			"@or2@Slayer Points: @yel@"+player.getPointsHandler().getSlayerPoints(),
			"@or2@Barrows Points: @yel@"+player.getPointsHandler().getBarrowsPoints(),
			"@or2@Member Points: @yel@"+player.getPointsHandler().getMemberPoints(),
			"@or2@Pk Points: @yel@"+player.getPointsHandler().getPkPoints(),
			"@or2@Wilderness Killstreak: @yel@"+player.getPlayerKillingAttributes().getPlayerKillStreak(),
			"@or2@Wilderness Kills: @yel@"+player.getPlayerKillingAttributes().getPlayerKills(),
			"@or2@Wilderness Deaths: @yel@"+player.getPlayerKillingAttributes().getPlayerDeaths(),
			"@or2@Arena Victories: @yel@"+player.getDueling().arenaStats[0],
			"@or2@Arena Points: @yel@"+player.getDueling().arenaStats[1],
			"",
			"@or3@ - @whi@ Slayer",
			//"@or2@Open Kills Tracker",
			//"@or2@Open Drop Log",
			"@or2@Master:  @yel@"+Misc.formatText(player.getSlayer().getSlayerMaster().toString().toLowerCase().replaceAll("_", " ")),
			(player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK ? 
					"@or2@Task:  @yel@"+Misc.formatText(player.getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " ")) : 
					"@or2@Task:  @yel@"+Misc.formatText(player.getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " "))+"s"),
			"@or2@Task Streak:  @yel@"+player.getSlayer().getTaskStreak(),
			"@or2@Task Amount:  @yel@"+player.getSlayer().getAmountToSlay(),
			(player.getSlayer().getDuoPartner() != null ? 
					"@or2@Duo Partner:  @yel@"+player.getSlayer().getDuoPartner() :
					"@or2@Duo Partner:  @yel@N/A"),
				/*
			"@lre@lre",
			"@red@red",
			"@dre@dre",
			"@yel@yel",
			"@whi@whi",
			"@blu@blu",
			"@cya@cya",
			"@mag@mag",
			"@bla@bla",
			"@gre@gre",
			"@gr1@gr1",
			"@gr2@gr2",
			"@gr3@gr3",
			"@str@str",
			"@or1@or1",
			"@or2@or2",
			"@or3@or3",
			*/
		};
		
		
		for (int i = 0; i < Messages.length; i++) {
			if (i+FIRST_STRING > LAST_STRING) {
				System.out.println("PlayerPanel("+player.getUsername()+"): "+i+" is larger than max string: "+LAST_STRING+". Breaking.");
				break;
			}
			
			player.getPacketSender().sendString(i+FIRST_STRING, Messages[i]);
		
		}
	}

}