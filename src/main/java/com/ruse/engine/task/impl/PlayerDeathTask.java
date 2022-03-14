package com.ruse.engine.task.impl;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.model.Animation;
import com.ruse.model.DamageDealer;
import com.ruse.model.Flag;
import com.ruse.model.GameMode;
import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.ItemsKeptOnDeath;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * Represents a player's death task, through which the process of dying is handled,
 * the animation, dropping items, etc.
 * 
 * @author relex lawl, redone by Gabbe.
 */

public class PlayerDeathTask extends Task {

	/**
	 * The PlayerDeathTask constructor.
	 * @param player	The player setting off the task.
	 */
	public PlayerDeathTask(Player player) {
		super(1, player, false);
		this.player = player;
	}

	private Player player;
	private int ticks = 5;
	private boolean dropItems = true;
	private boolean spawnItems = true;
	Position oldPosition;
	Location loc;
	ArrayList<Item> itemsToKeep = null; 
	NPC death;

	@Override
	public void execute() {
		if(player == null) {
			stop();
			return;
		}
		try {
			switch (ticks) {
			case 5:
				player.getPacketSender().sendInterfaceRemoval();
				player.getMovementQueue().setLockMovement(true).reset();
				break;
			case 3:
				player.performAnimation(new Animation(0x900));
				player.getPacketSender().sendMessage("Oh dear, you are dead!");
				this.death = getDeathNpc(player);
				break;
			case 1:
				this.oldPosition = player.getPosition().copy();
				this.loc = player.getLocation();
				if(loc != Location.DUNGEONEERING && loc != Location.PEST_CONTROL_GAME && loc != Location.DUEL_ARENA && loc != Location.FREE_FOR_ALL_ARENA 
						&& loc != Location.FREE_FOR_ALL_WAIT && loc != Location.SOULWARS && loc != Location.FIGHT_PITS && loc != Location.FIGHT_PITS_WAIT_ROOM 
						&& loc != Location.FIGHT_CAVES && loc != Location.RECIPE_FOR_DISASTER && loc != Location.GRAVEYARD && loc != Location.ZULRAH && loc != Location.RUNESPAN) {

					DamageDealer damageDealer = player.getCombatBuilder().getTopDamageDealer(true, null);
					Player killer = damageDealer == null ? null : damageDealer.getPlayer();
					
					if(player.getRights().equals(PlayerRights.OWNER) || player.getRights().equals(PlayerRights.DEVELOPER))
						dropItems = false;
					if(loc == Location.WILDERNESS) {
						if(killer != null && (killer.getRights().equals(PlayerRights.OWNER) || killer.getRights().equals(PlayerRights.DEVELOPER))) //|| killer.getGameMode().equals(GameMode.IRONMAN) || killer.getGameMode().equals(GameMode.ULTIMATE_IRONMAN)))
							dropItems = false;
					}
					if(killer != null) {
						if( killer.getRights().equals(PlayerRights.OWNER) || killer.getRights().equals(PlayerRights.DEVELOPER)){
							dropItems = false;
						}
					}
					if(loc == Location.THE_SIX || loc == Location.NOMAD){
						spawnItems = false;//&& loc != Location.NOMAD && (loc != Location.WILDERNESS && killer.getGameMode() != GameMode.NORMAL); //&& !(loc == Location.GODWARS_DUNGEON && player.getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom());
					} else if (loc == Location.WILDERNESS && killer != null && killer.isPlayer() && killer.getGameMode() != GameMode.NORMAL) {
						spawnItems = false;
					} else {
						spawnItems = true;
					}
					//System.out.println("Location: "+loc+" | spawnItems: "+spawnItems);
					//System.out.println("Killer: "+killer.getUsername()+" | "+killer.getGameMode());
					//System.out.println("Victim: "+player.getUsername()+" | "+player.getGameMode());
					if(dropItems) { //check for item dropping
						if(spawnItems == false){
							if(loc == Location.WILDERNESS && killer != null && killer.isPlayer() && killer.getGameMode() != GameMode.NORMAL){
								killer.getPacketSender().sendMessage("As an Iron/UIM player, you cannot loot "+player.getUsername()+"...").sendMessage("To stop them from freely attacking Iron folk, their dropped items have been removed.");
								player.getPacketSender().sendMessage(killer.getUsername()+" was an Iron Man or UIM.").sendMessage("Because they cannot loot, all of your dropped items have been removed.");
								final CopyOnWriteArrayList<Item> goneItems = new CopyOnWriteArrayList<Item>();
								goneItems.addAll(player.getInventory().getValidItems());
								goneItems.addAll(player.getEquipment().getValidItems());
								for (Item item : goneItems) {
									if (item != null && item.getAmount() > 0 && item.getId() > 0){
										PlayerLogs.log(player.getUsername(), "Died to IRON: "+killer.getUsername()+", losing: "+item.getId()+" x "+item.getAmount());
									}
								}
							}
						}
						itemsToKeep = ItemsKeptOnDeath.getItemsToKeep(player);
						final ArrayList<Item> playerItems = new ArrayList<Item>();
						playerItems.addAll(player.getInventory().getValidItems());
						playerItems.addAll(player.getEquipment().getValidItems());
						/*final CopyOnWriteArrayList<Item> playerItems = new CopyOnWriteArrayList<Item>();
						playerItems.addAll(player.getInventory().getValidItems());
						playerItems.addAll(player.getEquipment().getValidItems());*/
						final Position position = player.getPosition();
						/*Collections.sort(playerItems, new Comparator<Item>() { // Despite this actually sorting properly, it does not affect how the client displays grounditems.
						    @Override
						    public int compare(Item i1, Item i2) {
						        if (((long) i1.getAmount()*i1.getDefinition().getValue()) > ((long) i2.getAmount()*i2.getDefinition().getValue())) {
						            System.out.println("r1 "+((long) i1.getAmount()*i1.getDefinition().getValue()));
						            System.out.println("r1 "+((long) i2.getAmount()*i2.getDefinition().getValue()));
						        	return 1;
						        }
						        if (((long) i1.getAmount()*i1.getDefinition().getValue()) < ((long) i2.getAmount()*i2.getDefinition().getValue())) {
						        	System.out.println("r-1 "+((long) i1.getAmount()*i1.getDefinition().getValue()));
						            System.out.println("r-1 "+((long) i2.getAmount()*i2.getDefinition().getValue()));
						            return -1;
						        }
						        return 0;
						    }
						});*/
						for (Item item : playerItems) {
							System.out.println(item.getDefinition().getName());
							//World.sendMessage("Before setting: "+item.getDefinition().getName() + " "+ item.getAmount());
							//item.setAmount(item.getAmount());
							//World.sendMessage("After setting: "+item.getDefinition().getName() + " "+ item.getAmount());
							if(!item.tradeable() || itemsToKeep.contains(item)) {
								if(!itemsToKeep.contains(item)) {
									itemsToKeep.add(item);
								}
								continue;
							}
							//World.sendMessage("spawnItems = "+spawnItems);
							if(spawnItems) {
								//if(killer.getGameMode() != GameMode.NORMAL) {
								if(item != null && item.getId() > 0 && item.getAmount() > 0) {
									PlayerLogs.log(player.getUsername(), "Died and dropped: "+(ItemDefinition.forId(item.getId()) != null && ItemDefinition.forId(item.getId()).getName() != null ? ItemDefinition.forId(item.getId()).getName() : item.getId())+", amount: "+item.getAmount());
									GroundItemManager.spawnGroundItem((killer != null && killer.getGameMode() == GameMode.NORMAL ? killer : player), new GroundItem(item, position, killer != null ? killer.getUsername() : player.getUsername(), player.getHostAddress(), false, 150, true, 150));
								}
							}
						}
						if(killer != null && player.getLocation() != Location.FREE_FOR_ALL_ARENA) {
							killer.getPlayerKillingAttributes().add(player);
							player.getPlayerKillingAttributes().setPlayerDeaths(player.getPlayerKillingAttributes().getPlayerDeaths() + 1);
							player.getPlayerKillingAttributes().setPlayerKillStreak(0);
							PlayerPanel.refreshPanel(player);
						}
						player.getInventory().resetItems().refreshItems();
						player.getEquipment().resetItems().refreshItems();
					}
				} else
					dropItems = false;
				player.getPacketSender().sendInterfaceRemoval();
				player.setEntityInteraction(null);
				player.getMovementQueue().setFollowCharacter(null);
				player.getCombatBuilder().cooldown(false);
				player.setTeleporting(false);
				player.setWalkToTask(null);
				player.getSkillManager().stopSkilling();
				break;
			case 0:
				if(dropItems) {
					if(player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
						GameMode.set(player, player.getGameMode(), true);
					} else {
						if(itemsToKeep != null) {
							for(Item it : itemsToKeep) {
								PlayerLogs.log(player.getUsername(), "Died, but KEPT: "+(ItemDefinition.forId(it.getId()) != null && ItemDefinition.forId(it.getId()).getName() != null ? ItemDefinition.forId(it.getId()).getName() : it.getId())+", amount: "+it.getAmount());
								player.getInventory().add(it.getId(), it.getAmount());
								
							}
							itemsToKeep.clear();
						}
					}
				}
				if(death != null) {
					World.deregister(death);
				}
				player.restart();
				player.getUpdateFlag().flag(Flag.APPEARANCE);
				loc.onDeath(player);
				if(loc != Location.DUNGEONEERING) {
					if(player.getPosition().equals(oldPosition))
						player.moveTo(GameSettings.DEFAULT_POSITION.copy());
				}
				player = null;
				oldPosition = null;
				stop();
				break;
			}
			ticks--;
		} catch(Exception e) {
			setEventRunning(false);
			e.printStackTrace();
			if(player != null) {
				player.moveTo(GameSettings.DEFAULT_POSITION.copy());
				player.setConstitution(player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
			}	
		}
	}

	public static NPC getDeathNpc(Player player) {
		NPC death = new NPC(2862, new Position(player.getPosition().getX() + 1, player.getPosition().getY() + 1));
		World.register(death);
		death.setEntityInteraction(player);
		death.performAnimation(new Animation(401));
		death.forceChat(randomDeath(player.getUsername()));
		return death;
	}
	

	public static String randomDeath(String name) {
		switch (Misc.getRandom(8)) {
		case 0:
			return "There is no escape, " + Misc.formatText(name)
					+ "...";
		case 1:
			return "Muahahahaha!";
		case 2:
			return "You belong to me!";
		case 3:
			return "Beware mortals, " + Misc.formatText(name)
					+ " travels with me!";
		case 4:
			return "Your time here is over, " + Misc.formatText(name)
					+ "!";
		case 5:
			return "Now is the time you die, " + Misc.formatText(name)
					+ "!";
		case 6:
			return "I claim " + Misc.formatText(name) + " as my own!";
		case 7:
			return "" + Misc.formatText(name) + " is mine!";
		case 8:
			return "Say goodbye, "
					+ Misc.formatText(name) + "!";
		case 9:
			return "I have come for you, " + Misc.formatText(name)
					+ "!";
		}
		return "";
	}

}
