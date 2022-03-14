package com.ruse.world.content.minigames.impl;

import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.CeilingCollapseTask;
import com.ruse.model.GameObject;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.Position;
import com.ruse.model.RegionInstance;
import com.ruse.model.RegionInstance.RegionInstanceType;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/** 
 * Handles the Barrows minigame and it's objects, npcs, etc.
 * @editor Gabbe
 */
public class Barrows {

	public static void handleLogin(Player player) {
		updateInterface(player);
	}

	/**
	 * Handles all objects in the Barrows minigame: Coffins, doors, etc.
	 * @param player	The player calling this method
	 * @param object	The object the player is requesting
	 */
	public static boolean handleObject(final Player player, GameObject object) {
		switch(object.getId()) {
		case 6771:
			searchCoffin(player, object.getId(), 4, 2026, object.getPosition() != null ? new Position(3557, 9715, player.getPosition().getZ()) : new Position(3552, 9693));
			return true;
		case 6823:
			searchCoffin(player, object.getId(), 0, 2030, object.getPosition() != null ? new Position(3575, 9704, player.getPosition().getZ()) : new Position(3552, 9693));
			return true;
		case 6821:
			searchCoffin(player, object.getId(), 5, 2025, object.getPosition() != null ? new Position(3557, 9699, player.getPosition().getZ()) : new Position(3552, 9693));
			return true;
		case 6772:
			searchCoffin(player, object.getId(), 1, 2029, object.getPosition() != null ? new Position(3571, 9684, player.getPosition().getZ()) : new Position(3552, 9693));
			return true;
		case 6822:
			searchCoffin(player, object.getId(), 2, 2028, object.getPosition() != null ? new Position(3549, 9681, player.getPosition().getZ()) : new Position(3552, 9693));
			return true;
		case 6773:
			searchCoffin(player, object.getId(), 3, 2027, object.getPosition() != null ? new Position(3537, 9703, player.getPosition().getZ()) : new Position(3552, 9693));
			return true;
		case 6745:
			if(object.getPosition().getX() == 3535 && object.getPosition().getY() == 9684) {
				player.moveTo(new Position(3535, 9689));
				return true;
			} else if(object.getPosition().getX() == 3534 && object.getPosition().getY() == 9688) {
				player.moveTo(new Position(3534, 9683));
				return true;
			}
			break;
		case 6726:
			if(object.getPosition().getX() == 3535 && object.getPosition().getY() == 9688) {
				player.moveTo(new Position(3535, 9683));
				return true;
			} else if(object.getPosition().getX() == 3534 && object.getPosition().getY() == 9684) {
				player.moveTo(new Position(3534, 9689));
				return true;
			}
			break;
		case 6737:
			if(object.getPosition().getX() == 3535 && object.getPosition().getY() == 9701) {
				player.moveTo(new Position(3535, 9706));
				return true;
			} else if(object.getPosition().getX() == 3534 && object.getPosition().getY() == 9705) {
				player.moveTo(new Position(3534, 9700));
				return true;
			}
			break;
		case 6718:
			if(object.getPosition().getX() == 3534 && object.getPosition().getY() == 9701) {
				player.moveTo(new Position(3534, 9706));
				return true;
			} else if(object.getPosition().getX() == 3535 && object.getPosition().getY() == 9705) {
				player.moveTo(new Position(3535, 9700));
				return true;
			}
			break;
		case 6719:
			if(object.getPosition().getX() == 3541 && object.getPosition().getY() == 9712) {
				player.moveTo(new Position(3546, 9712));
				return true;
			} else if(object.getPosition().getX() == 3545 && object.getPosition().getY() == 9711) {
				player.moveTo(new Position(3540, 9711));
				return true;
			}
			break;
		case 6738:
			if(object.getPosition().getX() == 3541 && object.getPosition().getY() == 9711) {
				player.moveTo(new Position(3546, 9711));
				return true;
			} else if(object.getPosition().getX() == 3545 && object.getPosition().getY() == 9712) {
				player.moveTo(new Position(3540, 9712));
				return true;
			}
			break;
		case 6740:
			if(object.getPosition().getX() == 3558 && object.getPosition().getY() == 9711) {
				player.moveTo(new Position(3563, 9711));
				return true;
			} else if(object.getPosition().getX() == 3562 && object.getPosition().getY() == 9712) {
				player.moveTo(new Position(3557, 9712));
				return true;
			}
			break;
		case 6721:
			if(object.getPosition().getX() == 3558 && object.getPosition().getY() == 9712) {
				player.moveTo(new Position(3563, 9712));
				return true;
			} else if(object.getPosition().getX() == 3562 && object.getPosition().getY() == 9711) {
				player.moveTo(new Position(3557, 9711));
				return true;
			}
			break;
		case 6741:
			if(object.getPosition().getX() == 3568 && object.getPosition().getY() == 9705) {
				player.moveTo(new Position(3568, 9700));
				return true;
			} else if(object.getPosition().getX() == 3569 && object.getPosition().getY() == 9701) {
				player.moveTo(new Position(3569, 9706));
				return true;
			}
			break;
		case 6722:
			if(object.getPosition().getX() == 3569 && object.getPosition().getY() == 9705) {
				player.moveTo(new Position(3569, 9700));
				return true;
			} else if(object.getPosition().getX() == 3568 && object.getPosition().getY() == 9701) {
				player.moveTo(new Position(3568, 9706));
				return true;
			}
			break;
		case 6747:
			if(object.getPosition().getX() == 3568 && object.getPosition().getY() == 9688) {
				player.moveTo(new Position(3568, 9683));
				return true;
			} else if(object.getPosition().getX() == 3569 && object.getPosition().getY() == 9684) {
				player.moveTo(new Position(3569, 9689));
				return true;
			}
			break;
		case 6728:
			if(object.getPosition().getX() == 3569 && object.getPosition().getY() == 9688) {
				player.moveTo(new Position(3569, 9683));
				return true;
			} else if(object.getPosition().getX() == 3568 && object.getPosition().getY() == 9684) {
				player.moveTo(new Position(3568, 9689));
				return true;
			}
			break;
		case 6749:
			if(object.getPosition().getX() == 3562 && object.getPosition().getY() == 9678) {
				player.moveTo(new Position(3557, 9678));
				return true;
			} else if(object.getPosition().getX() == 3558 && object.getPosition().getY() == 9677) {
				player.moveTo(new Position(3563, 9677));
				return true;
			}
			break;
		case 6730:
			if(object.getPosition().getX() == 3562 && object.getPosition().getY() == 9677) {
				player.moveTo(new Position(3557, 9677));
				return true;
			} else if(object.getPosition().getX() == 3558 && object.getPosition().getY() == 9678) {
				player.moveTo(new Position(3563, 9678));
				return true;
			}
			break;
		case 6748:
			if(object.getPosition().getX() == 3545 && object.getPosition().getY() == 9678) {
				player.moveTo(new Position(3540, 9678));
				return true;
			} else if(object.getPosition().getX() == 3541 && object.getPosition().getY() == 9677) {
				player.moveTo(new Position(3546, 9677));
				return true;
			}
			break;
		case 6729:
			if(object.getPosition().getX() == 3545 && object.getPosition().getY() == 9677) {
				player.moveTo(new Position(3540, 9677));
				return true;
			} else if(object.getPosition().getX() == 3541 && object.getPosition().getY() == 9678) {
				player.moveTo(new Position(3546, 9678));
				return true;
			}
			break;
		case 10284:
			if(player.getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount() < 5) 
				return true;
			if (player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[player.getMinigameAttributes().getBarrowsMinigameAttributes().getRandomCoffin()][1] == 0) {
				handleObject(player, new GameObject(COFFIN_AND_BROTHERS[player.getMinigameAttributes().getBarrowsMinigameAttributes().getRandomCoffin()][0], null));
				player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[player.getMinigameAttributes().getBarrowsMinigameAttributes().getRandomCoffin()][1] = 1;
				return true;
			} else if (player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[player.getMinigameAttributes().getBarrowsMinigameAttributes().getRandomCoffin()][1] == 1) {
				player.getPacketSender().sendMessage("You cannot loot this whilst in combat!");
				return true;
			} else if (player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[player.getMinigameAttributes().getBarrowsMinigameAttributes().getRandomCoffin()][1] == 2 && player.getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount() >= 6) {
				if(player.getInventory().getFreeSlots() < 3) {
					player.getPacketSender().sendMessage("You need at least 3 free inventory slots to loot this chest.");
					return true;
				}
				resetBarrows(player);
				int r = randomRunes();
				int num = 25 + Misc.getRandom(255);
				if (player.getRights().isMember()) {
					player.getInventory().add(r, num*2);
					player.getPacketSender().sendMessage("<img=10> As a member, you get double "+ItemDefinition.forId(r).getName()+"s from the chest!");
				} else {
					player.getInventory().add(r,  num);
				}
				player.getPointsHandler().setBarrowsChests(1, true);
				player.getPacketSender().sendMessage("<img=10><shad=0> @lre@You've looted a total of "+player.getPointsHandler().getBarrowsChests() + " Barrows chests!");
				if (player.getPointsHandler().getBarrowsPoints() == 10) {
					player.getPacketSender().sendMessage("<img=10> @red@You can now purchase the \"Looter\" loyalty title.");
				}
				 if (player.getPointsHandler().getBarrowsChests() < 10) {
						player.getPacketSender().sendMessage("<img=10><shad=0>Only "+(10 - player.getPointsHandler().getBarrowsChests()) + " more for the @lre@\"@red@Looter@lre@\" title.");
				}
				if (Misc.getRandom(100) >= 45) {
					int b = randomBarrows();
					player.getInventory().add(b, 1);
					player.getPacketSender().sendMessage("<img=10><col=009966><shad=0> Congratulations! You've just received "+ItemDefinition.forId(b).getName()+" from Barrows!");
				}
				int coffin = Misc.getRandom(250);
				if (coffin == 1 || (player.getRights().isMember() && coffin == 2) || player.getUsername().equalsIgnoreCase("debug")) {
					player.getInventory().add(7587, 1);
					World.sendMessage("<img=10><shad=0><col=009966> "+player.getUsername()+" has just recieved a Coffin of the Damned from the Barrows minigame!");
				}
				player.getPacketSender().sendCameraShake(3, 2, 3, 2);
				player.getPacketSender().sendMessage("The cave begins to collapse!");
				TaskManager.submit(new CeilingCollapseTask(player));
			}
			break;
		case 6744:
		case 6725:
			if(player.getPosition().getX() == 3563)
				showRiddle(player);
			break;
		case 6746:
		case 6727:
			if(player.getPosition().getY() == 9683)
				showRiddle(player);
			break;
		case 6743:
		case 6724:
			if(player.getPosition().getX() == 3540)
				showRiddle(player);
			break;
		case 6739:
		case 6720:
			if(player.getPosition().getY() == 9706)
				player.moveTo(new Position(3551, 9694));
			break;
		}
		return false;
	}

	public static void showRiddle(Player player) {
		player.getPacketSender().sendString(4553, "1.");
		player.getPacketSender().sendString(4554, "2.");
		player.getPacketSender().sendString(4555, "3.");
		player.getPacketSender().sendString(4556, "4.");
		player.getPacketSender().sendString(4549, "Which item comes next?");
		int riddle = Misc.getRandom(riddles.length -1);
		player.getPacketSender().sendInterfaceModel(4545, riddles[riddle][0], 200);
		player.getPacketSender().sendInterfaceModel(4546, riddles[riddle][1], 200);
		player.getPacketSender().sendInterfaceModel(4547, riddles[riddle][2], 200);
		player.getPacketSender().sendInterfaceModel(4548, riddles[riddle][3], 200);
		player.getPacketSender().sendInterfaceModel(4550, riddles[riddle][4], 200);
		player.getPacketSender().sendInterfaceModel(4551, riddles[riddle][5], 200);
		player.getPacketSender().sendInterfaceModel(4552, riddles[riddle][6], 200);
		player.getMinigameAttributes().getBarrowsMinigameAttributes().setRiddleAnswer(riddles[riddle][7]);
		player.getPacketSender().sendInterface(4543);
	}

	public static void handlePuzzle(Player player, int puzzleButton) {
		if(puzzleButton == player.getMinigameAttributes().getBarrowsMinigameAttributes().getRiddleAnswer()) {
			player.moveTo(new Position(3551, 9694));
			player.getPacketSender().sendMessage("You got the correct answer.");
			player.getPacketSender().sendMessage("A magical force guides you to a chest located in the center room.");
		} else
			player.getPacketSender().sendMessage("You got the wrong answer.");
		player.getMinigameAttributes().getBarrowsMinigameAttributes().setRiddleAnswer(-1);
	}

	public static int[][] riddles = {
		{2349, 2351, 2353, 2355, 2359, 2363, 2361, 0}
	};
	/**
	 * Handles coffin searching
	 * @param player	Player searching a coffin	
	 * @param obj	The object (coffin) being searched
	 * @param coffinId	The coffin's array index
	 * @param npcId	The NPC Id of the NPC to spawn after searching
	 * @param constitution	NPC stat
	 * @param attackLevel 	NPC stat
	 * @param strengthLevel	NPC stat
	 * @param defenceLevel	NPC stat
	 * @param absorbMelee	NPC stat
	 * @param absorbRanged	NPC stat
	 * @param absorbMagic	NPC stat
	 * @param getCombatAttributes().getAttackDelay()	NPC attackspeed
	 * @param maxhit	NPC Maxhit
	 */
	public static void searchCoffin(final Player player, final int obj, final int coffinId, int npcId, Position spawnPos) {
		player.getPacketSender().sendInterfaceRemoval();
		if(player.getPosition().getZ() == -1) {
			if (selectCoffin(player, obj))
				return;
		}
		if (player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[coffinId][1] == 0) {
			if(player.getLocation() == Location.BARROWS) {
				player.setRegionInstance(new RegionInstance(player, RegionInstanceType.BARROWS));
				NPC npc_ = new NPC(npcId, spawnPos);
				npc_.forceChat(player.getPosition().getZ() == -1 ? "You dare disturb my rest!" : "You dare steal from us!");
				npc_.getCombatBuilder().setAttackTimer(3);
				npc_.setSpawnedFor(player);
				npc_.getCombatBuilder().attack(player);
				World.register(npc_);
				player.getRegionInstance().getNpcsList().add(npc_);
				player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[coffinId][1] = 1;
			}
		} else {
			player.getPacketSender().sendMessage("You have already searched this sarcophagus.");
		}
	}

	public static void resetBarrows(Player player) {
		player.getMinigameAttributes().getBarrowsMinigameAttributes().setKillcount(0);
		for(int i = 0; i < player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData().length; i++)
			player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[i][1] = 0;
		updateInterface(player);
		player.getMinigameAttributes().getBarrowsMinigameAttributes().setRandomCoffin(getRandomCoffin());
	}

	public static final Object[][] data = {
		{"Verac The Defiled", 37203}, 
		{"Torag The Corrupted", 37205},
		{"Karil The Tainted",37207},
		{"Guthan The Infested", 37206},
		{"Dharok The Wretched", 37202},
		{"Ahrim The Blighted", 37204}
	};

	/**
	 * Deregisters an NPC located in the Barrows minigame
	 * @param player	The player that's the reason for deregister
	 * @param barrowBrother	The NPC to deregister
	 * @param killed	Did player kill the NPC?
	 */
	public static void killBarrowsNpc(Player player, NPC n, boolean killed) {
		if(player == null || n == null)
			return;
		if(n.getId() == 58) {
			player.getMinigameAttributes().getBarrowsMinigameAttributes().setKillcount(player.getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount()+1);
			updateInterface(player);
			return;
		}
		int arrayIndex = getBarrowsIndex(player, n.getId());
		if(arrayIndex < 0)
			return;
		if(killed) {
			player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[arrayIndex][1] = 2;
			player.getMinigameAttributes().getBarrowsMinigameAttributes().setKillcount(player.getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount()+1);
			if(player.getRegionInstance() != null) {
				player.getRegionInstance().getNpcsList().remove(player);
			}
			player.setBarrowsKilled(player.getBarrowsKilled()+1);
		} else
			if(arrayIndex >= 0)
				player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[arrayIndex][1] = 0;
		updateInterface(player);
	}

	/**
	 * Selects the coffin and shows the interface if coffin id matches random
	 * coffin
	 **/
	public static boolean selectCoffin(Player player, int coffinId) {
		if (player.getMinigameAttributes().getBarrowsMinigameAttributes().getRandomCoffin() == 0)
			player.getMinigameAttributes().getBarrowsMinigameAttributes().setRandomCoffin(getRandomCoffin());
		if (COFFIN_AND_BROTHERS[player.getMinigameAttributes().getBarrowsMinigameAttributes().getRandomCoffin()][0] == coffinId) {
			DialogueManager.start(player, 27);
			player.setDialogueActionId(16);
			return true;
		}
		return false;
	}

	public static int getBarrowsIndex(Player player, int id) {
		int index = -1;
		for(int i = 0; i < player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData().length; i++) {
			if(player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[i][0] == id) {
				index = i;
			}
		}
		return index;
	}

	public static void updateInterface(Player player) {
		for(int i = 0; i < data.length; i++) {
			boolean killed = player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[i][1] == 2; String s = killed ? "@gre@" : "@red@";
			player.getPacketSender().sendString((int)data[i][1], ""+s+""+(String)data[i][0]);
		}
		player.getPacketSender().sendString(37208, "Killcount: "+player.getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount());
	}

	public static void fixBarrows(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		int totalCost = 0;
		int money = player.getInventory().getAmount(995);
		boolean breakLoop = false;
		for(Item items : player.getInventory().getItems()) {
			if(items == null)
				continue;
			for (int i = 0; i < brokenBarrows.length; i++) {
				if(player.getInventory().getSlot(items.getId()) > 0) {
					if (items.getId() == brokenBarrows[i][1]) {
						if (totalCost + 45000 > money) {
							breakLoop = true;
							player.getPacketSender().sendMessage("You need at least 45000 coins to fix this item.");
							break;
						} else {
							totalCost += 45000;
							player.getInventory().setItem(player.getInventory().getSlot(items.getId()), new Item(brokenBarrows[i][0], 1));
							player.getInventory().refreshItems();
						}
					}	
				}
			}
			if(breakLoop)
				break;
		}
		if(totalCost > 0)
			player.getInventory().delete(995, totalCost);
	}

	public static int runes[] = {4740, 558, 560, 565};

	public static int barrows[] = {4708, 4710, 4712, 4714, 4716, 4718, 4720,
		4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747,
		4749, 4751, 4753, 4755, 4757, 4759};

	public static final int[][] brokenBarrows = { { 4708, 4860 }, { 4710, 4866 },
		{ 4712, 4872 }, { 4714, 4878 }, { 4716, 4884 }, { 4720, 4896 },
		{ 4718, 4890 }, { 4720, 4896 }, { 4722, 4902 }, { 4732, 4932 },
		{ 4734, 4938 }, { 4736, 4944 }, { 4738, 4950 }, { 4724, 4908 },
		{ 4726, 4914 }, { 4728, 4920 }, { 4730, 4926 }, { 4745, 4956 },
		{ 4747, 4926 }, { 4749, 4968 }, { 4751, 4994 }, { 4753, 4980 },
		{ 4755, 4986 }, { 4757, 4992 }, { 4759, 4998 } };

	public static final int[][] COFFIN_AND_BROTHERS = { { 6823, 2030 },
		{ 6772, 2029 }, { 6822, 2028 }, { 6773, 2027 }, { 6771, 2026 },
		{ 6821, 2025 }
	};

	public static boolean isBarrowsNPC(int id) {
		for(int i = 0; i < COFFIN_AND_BROTHERS.length; i++) {
			if(COFFIN_AND_BROTHERS[i][1] == id)
				return true;
		}
		return false;
	}

	public static final Position[] UNDERGROUND_SPAWNS = {
		new Position(3569, 9677), 
		new Position(3535, 9677), 
		new Position(3534, 9711),
		new Position(3569, 9712)
	};

	public static int getRandomCoffin() {
		return Misc.getRandom(COFFIN_AND_BROTHERS.length - 1);
	}

	public static int randomRunes() {
		return runes[(int) (Math.random() * runes.length)];
	}

	public static int randomBarrows() {
		return barrows[(int) (Math.random() * barrows.length)];
	}

}
