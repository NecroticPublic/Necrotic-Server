package com.ruse.model;

import com.ruse.engine.task.impl.BonusExperienceTask;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

/**
 * Created by brandon on 4/8/2017.
 * Audited by the NSA
 */
public class VoteRewardHandler {

	public static final int minutes = 10;
	
    public static void voteRewards(Player player, boolean claimAll) {
        	
        	
        	if (!player.getInventory().contains(19670) || player.getInventory().getAmount(19670) < 1) {
        		return;
        	}
        	
        	int amt = player.getInventory().getAmount(19670);
        	
            player.getInventory().delete(19670, (claimAll ? amt : 1));
			player.getPacketSender()
					.sendMessage("You are rewarded "
					+ (claimAll ? Misc.format(amt) : 1) + " vote " + (claimAll && amt > 1 ? "points" : "point")
					+ " " + "and " + (claimAll ? Misc.format(amt*minutes) : minutes) + " minutes of 30% bonus XP!");
            BonusExperienceTask.addBonusXp(player, (claimAll ? amt*minutes : minutes)); //minutes);
            player.getPointsHandler().incrementVotingPoints(claimAll ? amt : 1);        	
         
            player.getClickDelay().reset();
        }

}