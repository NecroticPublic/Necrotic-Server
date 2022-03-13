package com.ruse.world.content.skill.impl.thieving;

import com.ruse.model.Animation;
import com.ruse.model.Skill;
import com.ruse.world.content.Achievements;
import com.ruse.world.content.Achievements.AchievementData;
import com.ruse.world.entity.impl.player.Player;

public class Stalls {

	public static void stealFromStall(Player player, int lvlreq, int xp, int reward, String message) {
		if(player.getInventory().getFreeSlots() < 1) {
			player.getPacketSender().sendMessage("You need some more inventory space to do this.");
			return;
		}
		if (player.getCombatBuilder().isBeingAttacked()) {
			player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat before doing this.");
			return;
		}
		if(!player.getClickDelay().elapsed(2500))
			return;
		if(player.getSkillManager().getMaxLevel(Skill.THIEVING) < lvlreq) {
			player.getPacketSender().sendMessage("You need a Thieving level of at least " + lvlreq + " to steal from this stall.");
			return;
		}
		player.performAnimation(new Animation(881));
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().addExperience(Skill.THIEVING, xp);
		player.getClickDelay().reset();
		if(player.getSkillManager().skillCape(Skill.THIEVING)) {
			player.getPacketSender().sendMessage("Your cape quietly converts the stolen item into cash.");
			if(reward == 18199){//banana
				player.getInventory().add(995, 1275);
			} else if(reward == 15009){//gold ring
				player.getInventory().add(995, 2763);
			} else if(reward == 17401){//hammer
				player.getInventory().add(995, 4888);
			} else if(reward == 1389){//staff
				player.getInventory().add(995, 6163);
			} else if(reward == 11998){//scimitar
				player.getInventory().add(995, 8713);
			} else if(reward == 13003){//rune gaunts
				player.getInventory().add(995, 3570);
			} else if(reward == 4131){//rune boots
				player.getInventory().add(995, 3910);
			} else if(reward == 1113){//chain
				player.getInventory().add(995, 8925);
			} else if(reward == 1147){//med helm
				player.getInventory().add(995, 5525);
			} else if(reward == 1163){//full helm
				player.getInventory().add(995, 17000);
			} else if(reward == 1079){//legs
				player.getInventory().add(995, 25500);
			} else if(reward == 1201){//kite
				player.getInventory().add(995, 21250);
			} else if(reward == 1127){//legs
				player.getInventory().add(995, 34000);
			} else { 
				player.getPacketSender().sendMessage(message);
				player.getInventory().add(reward, 1);
			}
		} else {
			player.getPacketSender().sendMessage(message);
			player.getInventory().add(reward, 1);
		}
		player.getSkillManager().stopSkilling();
		if(reward == 15009)
			Achievements.finishAchievement(player, AchievementData.STEAL_A_RING);
		else if(reward == 11998) {
			Achievements.doProgress(player, AchievementData.STEAL_140_SCIMITARS);
			Achievements.doProgress(player, AchievementData.STEAL_5000_SCIMITARS);
		}
	}

}
