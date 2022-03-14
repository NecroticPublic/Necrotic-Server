package com.ruse.world.content.randomevents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.GameObject;
import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.util.Misc;
import com.ruse.util.Stopwatch;
import com.ruse.world.World;
import com.ruse.world.content.Achievements;
import com.ruse.world.content.Achievements.AchievementData;
import com.ruse.world.content.CustomObjects;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.content.skill.impl.woodcutting.BirdNests;
import com.ruse.world.content.skill.impl.woodcutting.Woodcutting;
import com.ruse.world.content.skill.impl.woodcutting.WoodcuttingData;
import com.ruse.world.content.skill.impl.woodcutting.WoodcuttingData.Hatchet;
import com.ruse.world.content.skill.impl.woodcutting.WoodcuttingData.Trees;
import com.ruse.world.entity.impl.player.Player;

public final class EvilTree {
	
	/**
	 * @author Crimson 6/10/2017
	 */

	private static int TIME = 1000*60*60;//1000000;
	private static int tickDelay = 30, maxCut = 805;
	private static Stopwatch timer = new Stopwatch().reset();
	public static SpawnedTree SPAWNED_TREE = null;
	private static LocationData LAST_LOCATION = null;
	public static final int[] RANDOM_LOG = {1511, 1521, 1519, 1517, 1515, 1513};
	private static boolean firstTime = true;
	
	public static void handleCutWood(Player player, GameObject object, Hatchet h, EvilTreeDef t2) {

		if (t2 != null) {
			player.setEntityInteraction(object);
			if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) >= t2.getWoodcuttingLevel()) {
				player.performAnimation(new Animation(h.getAnim()));
				int delay = Misc.getRandom(tickDelay - WoodcuttingData.getChopTimer(player, h)) +1;
				player.setCurrentTask(new Task(1, player, false) {
					int cycle = 0, reqCycle = delay >= 2 ? delay : Misc.getRandom(1) + 1;
					@Override
					public void execute() {
						if(player.getInventory().getFreeSlots() == 0) {
							player.performAnimation(new Animation(65535));
							player.getPacketSender().sendMessage("You don't have enough free inventory space.");
							this.stop();
							return;
						}
						if (cycle != reqCycle) {
							cycle++;
							player.performAnimation(new Animation(h.getAnim()));
						} else if (cycle >= reqCycle) {
							
							List<Trees> validTrees = new ArrayList<Trees>();
							for (int i = 0; i < Trees.values().length; i++) {
								if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) >= Trees.values()[i].getReq()) {
									if (Trees.values()[i] == Trees.DRAMEN) { //we don't want to give out dramen branches lol
										continue;
									}
									validTrees.add(Trees.values()[i]);
									//System.out.println("Added: "+Trees.values()[i].toString()+" to rewards list.");
								}
							}
							
							Trees reward = Misc.randomElement(validTrees);
							int xp = reward.getXp();
							
							if (Woodcutting.lumberJack(player))
								xp *= 1.5;
							player.getSkillManager().addExperience(Skill.WOODCUTTING, (int) (xp));
							cycle = 0;
							BirdNests.dropNest(player);
							this.stop();
							Woodcutting.cutWood(player, object, true);

							SPAWNED_TREE.getTreeObject().incrementPickAmount();
							//System.out.println(EvilTree.SPAWNED_TREE.getTreeObject().getPickAmount() + "increments");
							player.getInventory().add(Item.getNoted(reward.getReward()), 1);
							player.getPacketSender().sendMessage("You get some "+StringUtils.capitalize(reward.toString().toLowerCase())+" logs...");
							
							if(reward == Trees.OAK) {
								Achievements.finishAchievement(player, AchievementData.CUT_AN_OAK_TREE);
							} else if(reward == Trees.MAGIC) {
								Achievements.doProgress(player, AchievementData.CUT_100_MAGIC_LOGS);
								Achievements.doProgress(player, AchievementData.CUT_5000_MAGIC_LOGS);
							}

						}
						Sounds.sendSound(player, Sound.WOODCUT);
					}
				});
				TaskManager.submit(player.getCurrentTask());
			} else {
				player.getPacketSender().sendMessage("You need a Woodcutting level of at least "+t2.getWoodcuttingLevel()+" to cut this tree.");
			}
		}
		

	}

	public static class SpawnedTree {

		public SpawnedTree(GameObject treeObject, LocationData treeLocation) {
			this.treeObject = treeObject;
			this.treeLocation = treeLocation;
		}

		private GameObject treeObject;
		private LocationData treeLocation;

		public GameObject getTreeObject() {
			return treeObject;
		}

		public LocationData getTreeLocation() {
			return treeLocation;
		}
	}

	public static enum EvilTreeDef {

		//NORMAL_EVIL_TREE("Evil tree", 11435, 1511, 1, 1, 1, 200, 15, 20, 200),
		//EVIL_OAK_TREE("Oak Evil Tree", 11437, 1521, 15, 7, 15, 300, 32, 45, 300),
		//EVIL_WILLOW_TREE("Willow Evil Tree", 11441, 1519, 30, 15, 30, 400, 45, 66, 450),
		//EVIL_MAPLE_TREE("Maple Evil Tree", 11444, 1517, 45, 22, 45, 500, 55, 121, 675),
		//EVIL_YEW_TREE("Yew Evil Tree", 11916, 1515, 60, 30, 60, 650, 64, 172, 1012),
		//EVIL_MAGIC_TREE("Magic Evil Tree", 11919, 1513, 75, 37, 75, 800, 70, 311, 1517),
		EVIL_ELDER_TREE("Elder Evil Tree", 11922, 1513, 1, 42, 90, 1000, 77, maxCut, 2000);

		private String treeName;
		private int id;
		private int log;
		private int woodcuttingLevel;
		private int farmingLevel;
		private int firemakingLevel;
		private int maximumCuttingAmount;
		private int woodcuttingXp;
		private int farmingXp;
		private int firemakingXp;

		private EvilTreeDef(String treeName, int id, int log, int woodcuttingLevel, int farmingLevel, int firemakingLevel, int maxCutAmount, int woodcuttingXp,
				int farmingXp, int firemakingXp) {
			this.treeName = treeName;
			this.id = id;
			this.log = log;
			this.woodcuttingLevel = woodcuttingLevel;
			this.farmingLevel = farmingLevel;
			this.firemakingLevel = firemakingLevel;
			this.maximumCuttingAmount = maxCutAmount;
			this.woodcuttingXp = woodcuttingXp;
			this.farmingXp = farmingXp;
			this.firemakingXp = firemakingXp;
		}

		public String getTreeName(){
			return treeName;
		}
		public int getId() {
			return id;
		}

		/**
		 * @return the log
		 */
		public int getLog() {
			return log;
		}

		public int getWoodcuttingLevel() {
			return woodcuttingLevel;
		}

		public int getFarmingLevel() {
			return farmingLevel;
		}

		public int getFiremakingLevel() {
			return firemakingLevel;
		}

		public int getMaximumCutAmount() {
			return maximumCuttingAmount;
		}

		public int getWoodcuttingXp() {
			return woodcuttingXp;
		}

		public int getFarmingXp() {
			return farmingXp;
		}

		public int getFiremakingXp() {
			return firemakingXp;
		}
		private static final Map<Integer, EvilTreeDef> tree = new HashMap<Integer, EvilTreeDef>();

		public static EvilTreeDef forId(int id) {
			return tree.get(id);
		}

		
		static {
			for (EvilTreeDef w : EvilTreeDef.values())

				tree.put(w.id, w);

		}

	}


	public static enum LocationData {
		CANIFIS(new Position(3492, 3491), "in a place called Canifis", "Canifis"),
		WARRIORS(new Position(2881, 3550), "outside of the Warrior's Guild", "Warrior's Guild"),
		YAKS(new Position(3222, 3263), "east of yaks near a bridge", "Yaks"),
		LUMBRIDGE(new Position(3210, 3205), "south of Lumbridge Castle", "Lumbridge Castle"),
		SLAYER(new Position(3435, 3525), "outside of Slayer Tower", "Slayer Tower"),
		BARROWS(new Position(3563, 3320), "north of Barrows", "Barrows"),
		DRAYNOR(new Position(3110, 3253), "near a place often called Draynor", "Draynor");

		private LocationData(Position spawnPos, String clue, String playerPanelFrame) {
			this.spawnPos = spawnPos;
			this.clue = clue;
			this.playerPanelFrame = playerPanelFrame;
		}

		private Position spawnPos;
		private String clue;
		public String playerPanelFrame;
	}
	public static LocationData getRandom() {
		LocationData tree = LocationData.values()[Misc.getRandom(LocationData.values().length - 1)];
		return tree;
	}


	public static EvilTreeDef getRandomTree() {
		EvilTreeDef tree = EvilTreeDef.values()[Misc.getRandom(EvilTreeDef.values().length - 1)];
		return tree;
	}
	public static void sequence() {
		//System.out.println("SPAWNED_TREE == null ? "+(boolean) (SPAWNED_TREE == null)+".");
		if(SPAWNED_TREE == null) {
			if(timer.elapsed(TIME) || firstTime) {
				if (firstTime == true) {
					firstTime = false;
				}
				LocationData locationData = getRandom();
				EvilTreeDef tree = getRandomTree();
				if(LAST_LOCATION != null) {
					if(locationData == LAST_LOCATION) {
						locationData = getRandom();
					}
				}
				LAST_LOCATION = locationData;
				//maxCut = tree.maximumCuttingAmount;
				SPAWNED_TREE = new SpawnedTree(new GameObject(tree.getId(), locationData.spawnPos), locationData);
				CustomObjects.spawnGlobalObject(SPAWNED_TREE.treeObject);
				World.sendMessage("<img=10> <shad=1><col=FF9933>An Evil tree has spawned "+locationData.clue+".");
				World.getPlayers().forEach(p -> PlayerPanel.refreshPanel(p));
				//World.getPlayers().forEach(p -> p.getPacketSender().sendString(39162, "@or2@Crashed star: @yel@"+ShootingStar.CRASHED_STAR.getStarLocation().playerPanelFrame+""));
				timer.reset();
			}
		} else {
			if(SPAWNED_TREE.treeObject.getPickAmount() >= maxCut) {
				//System.out.println("HEY WE CUT THE TREE DOWN CXCXCXCXCXXC");
				despawn(true);
				timer.reset();
			}
		}
	}

	public static void despawn(boolean respawn) {
		if(respawn) {
			timer.reset(0);
		} else {
			timer.reset();
		}
		if(SPAWNED_TREE != null) {
			for(Player p : World.getPlayers()) {
				if(p == null) {
					continue;
				}
				//p.getPacketSender().sendString(39162, "@or2@Evil Tree: @or2@[ @yel@N/A@or2@ ]");
				if(p.getInteractingObject() != null && p.getInteractingObject().getId() == SPAWNED_TREE.treeObject.getId()) {
					p.performAnimation(new Animation(65535));
					p.getPacketSender().sendClientRightClickRemoval();
					p.getSkillManager().stopSkilling();
					//p.getPacketSender().sendMessage("The evil tree has been cut down.");
				}
			}
			CustomObjects.deleteGlobalObject(SPAWNED_TREE.treeObject);
			SPAWNED_TREE = null;
			for(Player p : World.getPlayers()) {
				if (p == null) {
					continue;
				}
				PlayerPanel.refreshPanel(p);
			}
		}
	}
}