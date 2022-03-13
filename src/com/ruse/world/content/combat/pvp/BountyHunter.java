package com.ruse.world.content.combat.pvp;

import com.ruse.model.Locations.Location;
import com.ruse.world.World;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.entity.impl.player.Player;

/**
 * Handles Bounty Hunter System (PvP)
 * @author Gabriel Hannason
 */
public class BountyHunter {

	/*
	 * The minimum amount of Target Percentage required to receive a target.
	 */
	private static final int TARGET_PERCENTAGE_REQUIRED = 80; 

	/*
	 * The minimum amount of milliseconds to increase the player's target percentage
	 */
	private static final long TARGET_PERCENTAGE_INCREASEMENT_TIMER = 2000;

	/**
	 * Handles a player's logout.
	 * @param player	The player to handle the logout for.
	 */
	public static void handleLogout(Player player) {
		if(player.getPlayerKillingAttributes().getTarget() != null)
			resetTargets(player.getPlayerKillingAttributes().getTarget(), player, false, "Your target logged out and has been reset.");
	}

	public static void process(Player player) {
		if(player.getPlayerKillingAttributes().getTarget() == null) {
			if(player.getPlayerKillingAttributes().getTargetPercentage() >= TARGET_PERCENTAGE_REQUIRED) {
				Player target = findTarget(player);
				if(target != null)
					assignTargets(player, target);
			}
		}
		if(System.currentTimeMillis() - player.getPlayerKillingAttributes().getLastTargetPercentageIncrease() >= TARGET_PERCENTAGE_INCREASEMENT_TIMER) {
			addTargetPercentage(player, 1);
			player.getPlayerKillingAttributes().setLastTargetPercentageIncrease(System.currentTimeMillis());
		}
		updateInterface(player);
	}

	public static void sequence(Player player) {
		if(player.getPlayerKillingAttributes().getTarget() != null) {
			if(player.getPlayerKillingAttributes().getTarget().getLocation() != Location.WILDERNESS) {
				
				if(player.getPlayerKillingAttributes().getTarget().getPlayerKillingAttributes().getSafeTimer() == 180) {
					player.getPlayerKillingAttributes().getTarget().getPacketSender().sendMessage("You have 3 minutes to return to the Wilderness, or you will lose your target.");
					player.getPacketSender().sendMessage("Your target has 3 minutes to return to the Wilderness, or they will lose you as target.");
				} else if(player.getPlayerKillingAttributes().getTarget().getPlayerKillingAttributes().getSafeTimer() == 120) {
					player.getPlayerKillingAttributes().getTarget().getPacketSender().sendMessage("You have 2 minutes to return to the Wilderness, or you will lose your target.");
					player.getPacketSender().sendMessage("Your target has 2 minutes to return to the Wilderness, or they will lose you as target.");
				} else if(player.getPlayerKillingAttributes().getTarget().getPlayerKillingAttributes().getSafeTimer() == 60) {
					player.getPlayerKillingAttributes().getTarget().getPacketSender().sendMessage("You have 1 minute to return to the Wilderness, or you will lose your target.");
					player.getPacketSender().sendMessage("Your target has 1 minute to return to the Wilderness, or they will lose you as target.");
				}
				
				if(player.getPlayerKillingAttributes().getTarget().getPlayerKillingAttributes().getSafeTimer() > 0)
					player.getPlayerKillingAttributes().getTarget().getPlayerKillingAttributes().setSafeTimer(player.getPlayerKillingAttributes().getTarget().getPlayerKillingAttributes().getSafeTimer() - 1);
				
				if(player.getPlayerKillingAttributes().getTarget().getPlayerKillingAttributes().getSafeTimer() <= 0) {
					player.getPlayerKillingAttributes().getTarget().getPlayerKillingAttributes().setSafeTimer(0);
					BountyHunter.resetTargets(player, player.getPlayerKillingAttributes().getTarget(), false, "Your target was in safe-zone for too long and has been reset.");
				}
			}
		}
	}

	/**
	 * Finds a target for a player.
	 * @param c	The player to find a target for.
	 */
	public static Player findTarget(Player player) {
		for(Player players : World.getPlayers()) {
			if(players == null)
				continue;
			if(checkTarget(player, players))
				return players;
		}
		return null;
	}

	/**
	 * Can a player receive a target?
	 * @param player	The player to check status for.
	 * @return	Player's status.
	 */
	public static boolean checkTarget(Player player, Player target) {
		if(target.getLocation() != null && target.getLocation() == Location.WILDERNESS && player.getLocation() != null && player.getLocation() == Location.WILDERNESS && target.getPlayerKillingAttributes().getTarget() == null && player.getPlayerKillingAttributes().getTarget() == null && target.getIndex() != player.getIndex() && target.getPlayerKillingAttributes().getTargetPercentage() >= TARGET_PERCENTAGE_REQUIRED && player.getPlayerKillingAttributes().getTargetPercentage() >= TARGET_PERCENTAGE_REQUIRED) {
			int combatDifference = CombatFactory.combatLevelDifference(player.getSkillManager().getCombatLevel(), target.getSkillManager().getCombatLevel());
			return combatDifference <= player.getWildernessLevel() && combatDifference <= target.getWildernessLevel();
		}
		return false;
	}

	/**
	 * Assigns targets for two players
	 * @param c	Player1 to assign target for.
	 * @param target	Player2 to assign target for.
	 */

	public static void assignTargets(Player player, Player target) {
		if(target == null || player == null)
			return;
		player.getPlayerKillingAttributes().setTarget(target);
		player.getPacketSender().sendMessage("You've been assigned a target!");
		player.getPacketSender().sendEntityHint(target);
		player.getPlayerKillingAttributes().setSafeTimer(180);
		updateInterface(player);
		target.getPacketSender().sendMessage("You've been assigned a target!");
		target.getPacketSender().sendEntityHint(player);
		updateInterface(target);
		target.getPlayerKillingAttributes().setTarget(player);
		target.getPlayerKillingAttributes().setSafeTimer(180);
	}

	/**
	 * Resets targets, because a player logged out or was defeated.
	 * @param c	The player to reset target for.
	 * @param leaver	The leaver to reset target for.
	 * @param string 
	 */
	public static void resetTargets(Player c, Player leaver, boolean killed, String string) {
		if(!killed) { //leaver logged out
			leaver.getPlayerKillingAttributes().setTarget(null);
			leaver.getPlayerKillingAttributes().setTargetPercentage(0);
			c.getPlayerKillingAttributes().setTarget(null);
			if(string != null)
				c.getPacketSender().sendMessage(string);
			updateInterface(c);
		} else {
			leaver.getPlayerKillingAttributes().setTarget(null);
			leaver.getPlayerKillingAttributes().setTargetPercentage(0);
			c.getPlayerKillingAttributes().setTarget(null);
			c.getPlayerKillingAttributes().setTargetPercentage(0);
			updateInterface(c);
			updateInterface(leaver);
			if(string != null)
				c.getPacketSender().sendMessage(string);
		}
		c.getPacketSender().sendEntityHintRemoval(true);
		leaver.getPacketSender().sendEntityHintRemoval(true);
		c.getPlayerKillingAttributes().setSafeTimer(0);
		leaver.getPlayerKillingAttributes().setSafeTimer(0);
	}

	/**
	 * Updates the Interface for a player.
	 * @param player	The player to update the interface for.
	 */
	public static void updateInterface(Player player) {
		player.getPacketSender().sendString(42022, player.getPlayerKillingAttributes().getTarget() != null ? player.getPlayerKillingAttributes().getTarget().getUsername() : "None");
		player.getPacketSender().sendString(42024, ""+player.getPlayerKillingAttributes().getTargetPercentage()+"%");
		if(player.getAppearance().getBountyHunterSkull() == -1)
			player.getAppearance().setBountyHunterSkull(0);
	}

	/**
	 * Adds Target Percentage to the assigned Player.
	 * @param c	The player to add Target Percentage for.
	 * @param amountToAdd	How much Target Percentage the player will receive.
	 */
	public static void addTargetPercentage(Player c, int amountToAdd) {
		if(c.getPlayerKillingAttributes().getTargetPercentage() + amountToAdd > 100 || c.getPlayerKillingAttributes().getTargetPercentage() == 100) {
			c.getPlayerKillingAttributes().setTargetPercentage(100);
		} else {
			c.getPlayerKillingAttributes().setTargetPercentage(c.getPlayerKillingAttributes().getTargetPercentage() + amountToAdd);
		}
	}
}
