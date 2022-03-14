package com.ruse.world.content.minigames.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.GameObject;
import com.ruse.model.Graphic;
import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.RegionInstance;
import com.ruse.model.Locations.Location;
import com.ruse.model.RegionInstance.RegionInstanceType;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class WarriorsGuild {

	/*
	 * The armors required to animate an NPC
	 */
	private static final int[][] ARMOR_DATA = {{1075, 1117, 1155, 4278}, {1067, 1115, 1153, 4279}, {1069, 1119, 1157, 4280}, {1077, 1125, 1165, 4281}, {1071, 1121, 1159, 4282}, {1073, 1123, 1161, 4283}, {1079, 1127, 1163, 4284}};
	private static final int[] ANIMATED_ARMOUR_NPCS = {4278, 4279, 4280, 4281, 4282, 4283, 4284};
	private static final int[] TOKEN_REWARDS = {5, 10, 15, 20, 26, 32, 40};
	/*
	 * The available defenders which players can receive from this minigame.
	 */
	private static final int[] DEFENDERS = new int[]{8844, 8845, 8846, 8847, 8848, 8849, 8850, 13262};

	/**
	 * Handles what happens when a player uses an item on the animator.
	 * @param player	The player
	 * @param item		The item the player is using
	 * @param object	That animator object which the player is using an item on
	 */
	public static boolean itemOnAnimator(final Player player, final Item item, final GameObject object) {
		if(player.getMinigameAttributes().getWarriorsGuildAttributes().hasSpawnedArmour() && player.getRights() != PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage("You have already spawned some animated armour.");
			return true;
		} else {
			for(int i = 0; i < ARMOR_DATA.length; i++) {
				for(int f = 0; f < ARMOR_DATA[0].length; f++) {
					if(item.getId() == ARMOR_DATA[i][f]) {
						if(player.getInventory().contains(ARMOR_DATA[i][0]) && player.getInventory().contains(ARMOR_DATA[i][1]) && player.getInventory().contains(ARMOR_DATA[i][2])) {
							final int items[] = new int[] {ARMOR_DATA[i][0], ARMOR_DATA[i][1], ARMOR_DATA[i][2]};
							if(items != null) {
								for(int a = 0; a < items.length; a++)
									player.getInventory().delete(items[a], 1);
								player.setRegionInstance(new RegionInstance(player, RegionInstanceType.WARRIORS_GUILD));
								player.getMinigameAttributes().getWarriorsGuildAttributes().setSpawnedArmour(true);
								player.performAnimation(new Animation(827));
								player.getPacketSender().sendMessage("You place some armor on the animator..");
								object.performGraphic(new Graphic(1930));
								final int index = i;
								TaskManager.submit(new Task(2) {
									@Override
									public void execute() {
										NPC npc_ = new NPC(ARMOR_DATA[index][3], new Position(player.getPosition().getX(), player.getPosition().getY() + 1));
										npc_.forceChat("I'M ALIVE!!!!").setEntityInteraction(player).getCombatBuilder().setAttackTimer(2);
										npc_.setSpawnedFor(player).getCombatBuilder().attack(player);
										player.setPositionToFace(npc_.getPosition());
										World.register(npc_);
										player.getRegionInstance().getNpcsList().add(npc_);
										stop();
									}
								});
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Handles a drop after an NPC is slain in the Warriors guild
	 * @param player	The player to handle the drop for
	 * @param npc		The npc which will drop something
	 */
	public static void handleDrop(Player player, NPC npc) {
		if(player == null || npc == null)
			return;
		/*
		 * Tokens
		 */
		if(npc.getId() >= 4278 && npc.getId() <= 4284) {
			if(player.getRegionInstance() != null)
				player.getRegionInstance().getNpcsList().remove(npc);
			int[] armour = null;
			for(int i = 0; i < ARMOR_DATA.length; i++) {
				if(ARMOR_DATA[i][3] == npc.getId()) {
					armour = new int[] {ARMOR_DATA[i][0], ARMOR_DATA[i][1], ARMOR_DATA[i][2]};
					break;
				}
			}
			if(armour != null) {
				for(int i : armour)
					GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(i), npc.getPosition().copy(), player.getUsername(), false, 80, true, 80));
				player.getMinigameAttributes().getWarriorsGuildAttributes().setSpawnedArmour(false);
				GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(8851, getTokenAmount(npc.getId())), npc.getPosition().copy(), player.getUsername(), false, 80, true, 80));
				armour = null;
			}
		} else if(npc.getId() == 4291 && player.getPosition().getZ() == 2) {
			if(Misc.getRandom(20) <= 4) {
				GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(getDefender(player)), npc.getPosition().copy(), player.getUsername(), false, 100, false, -1));
			}
		}
	}

	/**
	 * Gets the player's best defender
	 * @param player	The player to search items for
	 * @return			The best defender's item id
	 */
	public static int getDefender(Player player) {
		int foundIndex = -1;
		for(int i = 0; i < DEFENDERS.length; i++) {
			if(player.getInventory().contains(DEFENDERS[i]) || player.getEquipment().contains(DEFENDERS[i])) {
				foundIndex = i;
			}
		}		
		if(foundIndex != 7) {
			foundIndex++;
		}
		return DEFENDERS[foundIndex];
	}

	/**
	 * Warriors guild dialogue, handles what Kamfreena says.
	 * @param Player		The player to show the dialogue for acording to their stats.
	 */
	public static Dialogue warriorsGuildDialogue(final Player player) {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}

			@Override
			public String[] dialogue() {
				int defender = getDefender(player);
				return new String[] {"I'll release some Cyclops which might drop", ""+ItemDefinition.forId(defender).getName().replace(" defender", "")+" Defenders for you.", "Good luck warrior!"};
			}

			@Override
			public int npcId() {
				return 2948;
			}

		};
	}

	/**
	 * The warriors guild task
	 */
	public static void handleTokenRemoval(final Player player) {
		if(player.getMinigameAttributes().getWarriorsGuildAttributes().enteredTokenRoom())
			return;
		player.getMinigameAttributes().getWarriorsGuildAttributes().setEnteredTokenRoom(true);
		TaskManager.submit(new Task(160, player, false) {
			@Override
			public void execute() {
				if(!player.getMinigameAttributes().getWarriorsGuildAttributes().enteredTokenRoom()) {
					this.stop();
					return;
				}
				if(player.getLocation() != Location.WARRIORS_GUILD || player.getPosition().getZ() != 2) {
					player.getMinigameAttributes().getWarriorsGuildAttributes().setEnteredTokenRoom(false);
					this.stop();
					return;
				}
				if(player.getInventory().contains(8851)) {
					player.getInventory().delete(8851, 10);
					player.performGraphic(new Graphic(1368));
					player.getPacketSender().sendMessage("Some of your tokens crumble to dust..");
				} else {
					player.getMinigameAttributes().getWarriorsGuildAttributes().setEnteredTokenRoom(false);
					player.getCombatBuilder().cooldown(true);
					player.getMovementQueue().reset();
					player.moveTo(new Position(2844, 3539, 2));
					player.getPacketSender().sendMessage("You have run out of tokens.");
					resetCyclopsCombat(player);
					this.stop();
				}
			}
		});
	}

	/**
	 * Gets the amount of tokens to receive from an npc
	 * @param npc	The npc to check how many tokens to receive from
	 * @return		The amount of tokens to receive as a drop
	 */
	private static int getTokenAmount(int npc) {
		for(int f = 0; f < ANIMATED_ARMOUR_NPCS.length; f++) {
			if (npc == ANIMATED_ARMOUR_NPCS[f]) {
				return TOKEN_REWARDS[f];
			}
		}
		return 5;
	}

	/**
	 * Resets any cyclops's combat who are in combat with player
	 * @param player	The player to check if cyclop is in combat with
	 */
	public static void resetCyclopsCombat(Player player) {
		for(NPC n : player.getLocalNpcs()) {
			if(n == null)
				continue;
			if(n.getId() == 4291 && n.getLocation() == Location.WARRIORS_GUILD && n.getCombatBuilder().getVictim() != null && n.getCombatBuilder().getVictim() == player) {
				n.getCombatBuilder().cooldown(true);
				n.getMovementQueue().reset();
			}
		}
	}
}
