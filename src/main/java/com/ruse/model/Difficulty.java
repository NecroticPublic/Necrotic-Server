package com.ruse.model;

//import com.ruse.model.container.impl.Bank;
//import com.ruse.model.container.impl.Bank;
//import com.ruse.world.content.PlayerPanel;
//import com.ruse.world.content.Achievements.AchievementData;
//import com.ruse.world.content.dialogue.DialogueManager;
//import com.ruse.world.content.dialogue.impl.Tutorial;
import com.ruse.world.entity.impl.player.Player;
//import com.ruse.world.content.skill.impl.slayer.SlayerMaster;
//import com.ruse.world.content.skill.impl.slayer.SlayerTasks;

public enum Difficulty {

	FUN,
	EASY,
	REGULAR,
	HARD,
	EXTREME,
	MODERN;

	public static void set(Player player, Difficulty newMode, boolean force) {
		Difficulty cd = player.getDifficulty();
		if (force) {
			player.setDifficulty(newMode);
		} else {
			if (player.busy()) {
				player.getPacketSender().sendMessage("You are far too busy to do that.");
				return;
			}
			if (newMode == FUN && !player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("Error code: 104153, please report to staff.");
				return;
			}
			if (cd == newMode) {
				player.getPacketSender().sendMessage("You are already on that difficulty!");
				return;
			}
			if (cd == EASY && !player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("You cannot change your Difficulty from Easy.");
				return;
			}
			if (cd == REGULAR && newMode != EASY && !player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("You can only change your difficulty to an easier mode.");
				return;
			}
			if (cd == HARD && newMode == EXTREME && !player.getRights().OwnerDeveloperOnly()) {
				player.getPacketSender().sendMessage("You can only change your difficulty to an easier mode.");
				return;
			}
			player.setDifficulty(newMode);
		}
	}
	
	public static int getDifficultyModifier(Player player, Skill skill) {
		Difficulty d = player.getDifficulty();
		if (d == FUN)
			return skill.getFunExperienceModifier();
		if (d == EASY)
			return skill.getEasyExperienceModifier();
		if (d == REGULAR)
			return skill.getRegularExperienceModifier();
		if (d == HARD)
			return skill.getHardExperienceModifier();
		if (d == EXTREME)
			return skill.getExtremeExperienceModifier();
		if (d == MODERN)
			return skill.getModernExperienceModifier();
		return 1;
	}
	
		/*if(!death && !player.getClickDelay().elapsed(1000))
			return;
		player.getClickDelay().reset();
		player.getPacketSender().sendInterfaceRemoval();
		if (player.getGameMode() == IRONMAN && newMode != NORMAL && (!(player.getRights().OwnerDeveloperOnly()))) {
			player.getPacketSender().sendMessage("As an Iron Man, you can only set your game mode to Normal mode.");
			player.setPlayerLocked(false);
			return;
		}
		if (player.getGameMode() == ULTIMATE_IRONMAN && newMode == ULTIMATE_IRONMANN && (!(player.getRights().OwnerDeveloperOnly()))) {
			player.getPacketSender().sendMessage("You already are a Ultimate Ironman, that would be pointless!");
			player.setPlayerLocked(false);
			return;
		}
		if (player.getGameMode() == ULTIMATE_IRONMAN && newMode == IRONMAN && (!(player.getRights().OwnerDeveloperOnly()))) {
			player.getBank(0).add(16691, 1).add(9704, 1).add(17239, 1).add(16669, 1).add(16339, 1).add(6068, 1).add(9703, 1);
			player.getPacketSender().sendMessage("").sendMessage("Your new Iron Man armour has been sent to your bank!");
		}
		if(newMode != player.getGameMode() || death) {
		}
		player.setGameMode(newMode);
		player.getPacketSender().sendIronmanMode(newMode.ordinal());
		if(!death) {
			player.getPacketSender().sendMessage("You've set your newgamemode to "+newMode.name().toLowerCase().replaceAll("_", " ")+".").sendMessage("If you wish to change it, please talk to the town crier at home.");
		} else {
			player.getPacketSender().sendMessage("Your account progress has been reset.");
		}
		if(player.newPlayer()) {
			player.setPlayerLocked(true);
			DialogueManager.start(player, Tutorial.get(player, -2));
		} else {
			player.setPlayerLocked(false);
		}
	}*/
}
