package com.ruse.world.content.minigames.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.CombatIcon;
import com.ruse.model.Hit;
import com.ruse.model.Hitmask;
import com.ruse.model.Locations.Location;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Projectile;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.movement.MovementQueue;
import com.ruse.model.movement.PathFinder;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/** 
 * Pest control minigame
 * @author Gabriel Hannason
 */
public class PestControl {

	public static int TOTAL_PLAYERS = 0;
	private static int PLAYERS_IN_BOAT = 0;

	/**
	 * @note Stores player and State
	 */
	private static Map<Player, String> playerMap = new HashMap<Player, String>();

	/*
	 * Stores npcs
	 */
	private static CopyOnWriteArrayList<NPC> npcList = new CopyOnWriteArrayList<NPC>();

	/**
	 * @return HashMap Value
	 */
	public static String getState(Player player) {
		return playerMap.get(player);
	}

	/**
	 * @note States of minigames
	 */
	public static final String PLAYING = "PLAYING";
	public static final String WAITING = "WAITING";

	/**
	 * Is a game running?
	 */
	private static boolean gameRunning;

	/**
	 * Moves a player in to the boat (waiting area)
	 * and adds the player to the map.
	 * @param p			The player entering
	 */
	public static void boardBoat(Player p) {
		if(p.getSummoning().getFamiliar() != null) {
			p.getPacketSender().sendMessage("Familiars are not allowed on the boat.");
			return;
		}
		if (p.getSkillManager().getCombatLevel() < 30) {
			p.getPacketSender().sendMessage("You must have a combat level of at least 30 to play this minigame.");
			return;
		}
		if(getState(p) == null) {
			playerMap.put(p, WAITING);
			TOTAL_PLAYERS++;
			PLAYERS_IN_BOAT++;
		}
		p.getSession().clearMessages();
		p.moveTo(new Position(2661, 2639, 0));
		p.getPacketSender().sendString(21117, "");
		p.getPacketSender().sendString(21118, "");
		p.getPacketSender().sendString(21008, "(Need 2 to 25 players)");
		p.getMovementQueue().setLockMovement(false).reset();
	}

	/**
	 * Moves the player out of the boat (waiting area)
	 * and removes the player from the map.
	 * @param p			The player leaving
	 */
	public static void leave(Player p, boolean fromList) {
		final String state = getState(p);
		if(state != null) {
			if(fromList) {
				playerMap.remove(p);
			}
			TOTAL_PLAYERS--;
			if(state == WAITING) {
				PLAYERS_IN_BOAT--;
			}
		}
		p.getPacketSender().sendInterfaceRemoval();
		p.getSession().clearMessages();
		p.moveTo(new Position(2657, 2639, 0));
		p.getMovementQueue().setLockMovement(false).reset();
	}

	/**
	 * Handles the static process required.
	 */
	public static void sequence() {
		if(TOTAL_PLAYERS == 0 && !gameRunning)
			return;
		updateBoatInterface();
		if(waitTimer > 0)
			waitTimer--;
		if(waitTimer <= 0) {
			if(!gameRunning)
				startGame();
			else {
				for (Player p : playerMap.keySet()) {
					if(p == null)
						continue;
					String state = getState(p);
					if(state != null && state.equals(WAITING)) {
						p.getPacketSender().sendMessage("A new Pest control game will be started once the current one has finished.");
					}
				}
			}
			waitTimer = WAIT_TIMER;
		}
		if(gameRunning) {
			updateIngameInterface();
			if(Math.random() < 0.1)
				spawnRandomNPC();
			processNPCs();
			if(knight == null || (knight != null && knight.getConstitution() <= 0)) {
				endGame(false);
				waitTimer = WAIT_TIMER;
			} else if (allPortalsDead()) {
				endGame(true);
				waitTimer = WAIT_TIMER;
			}
		}
	}

	public static String[] KNIGHT_CHAT = {
		"We must not fail!", 
		"Take down the portals", 
		"The Void Knights will not fall!", 
		"Hail the Void Knights!", 
		"We are beating these scum!"
	};

	/**
	 * Updates the boat (waiting area) interface for every player in it.
	 */
	private static void updateBoatInterface() {
		for (Player p : playerMap.keySet()) {
			if(p == null)
				continue;
			String state = getState(p);
			if(state != null && state.equals(WAITING)) {
				p.getPacketSender().sendString(21006, "Next Departure: "+waitTimer+"");
				p.getPacketSender().sendString(21007, "Players Ready: "+PLAYERS_IN_BOAT+"");
				p.getPacketSender().sendString(21009, "Commendations: "+p.getPointsHandler().getCommendations());
			}
		}
	}

	/**
	 * Updates the game interface for every player.
	 */
	private static void updateIngameInterface() {
		for (Player p : playerMap.keySet()) {
			if(p == null)
				continue;
			String state = getState(p);
			if(state != null && state.equals(PLAYING)) {
				p.getPacketSender().sendString(21111, getPortalText(0));
				p.getPacketSender().sendString(21112, getPortalText(1));
				p.getPacketSender().sendString(21113, getPortalText(2));
				p.getPacketSender().sendString(21114, getPortalText(3));
				String prefix = knight.getConstitution() < 500 ? "@red@" : knight.getConstitution() < 800 ? "@yel@" : "@gre@";
				p.getPacketSender().sendString(21115, knight != null && knight.getConstitution() > 0 ? prefix+"Knight's health: "+knight.getConstitution() : "Dead");
				prefix = p.getMinigameAttributes().getPestControlAttributes().getDamageDealt() == 0 ? "@red@" : p.getMinigameAttributes().getPestControlAttributes().getDamageDealt() < 100 ? "@yel@" : "@gre@";
				p.getPacketSender().sendString(21116, prefix+"Your damage : "+p.getMinigameAttributes().getPestControlAttributes().getDamageDealt()+"/100");
			}
		}
	}


	/**
	 * Starts a game and moves players in to the game.
	 */
	private static void startGame() {
		boolean startGame = !gameRunning && PLAYERS_IN_BOAT >= 2;
		if(startGame) {
			gameRunning = true;
			spawnMainNPCs();
		}
		for (Player player : playerMap.keySet()) {
			if (player != null) {
				String state = getState(player);
				if(state != null && state.equals(WAITING)) {
					if(startGame) {
						movePlayerToIsland(player);
						playerMap.put(player, PLAYING);
					} else
						player.getPacketSender().sendMessage("There must be at least 3 players in the boat before a game can start.");
				}
			}
		}
	}

	/**
	 * Teleports the player in to the game
	 */
	private static void movePlayerToIsland(Player p) {
		p.getPacketSender().sendInterfaceRemoval();
		p.getSession().clearMessages();
		p.moveTo(new Position(2658, 2611, 0));
		p.getMovementQueue().setLockMovement(false).reset();
		DialogueManager.start(p, 26);
		PLAYERS_IN_BOAT--;
	}

	/**
	 * Ends a game and rewards players.
	 * @param won	Did the players manage to win the game?
	 */
	private static void endGame(boolean won) {
		for (Iterator<Player> it = playerMap.keySet().iterator(); it.hasNext();) {
			Player p = (Player) it.next();
			if(p == null)
				continue;
			String state = getState(p);
			if(state != null && state.equals(PLAYING)) {
				leave(p, false);
				if (won && p.getMinigameAttributes().getPestControlAttributes().getDamageDealt() >= 50) {
					p.getPacketSender().sendMessage("The portals were successfully closed. You've been rewarded for your effort.");
					p.getPacketSender().sendMessage("You've received 15 Commendations and "+p.getSkillManager().getCombatLevel() * 50+" coins.");
					p.getPointsHandler().setCommendations(15, true);
					PlayerPanel.refreshPanel(p);
					p.getInventory().add(995, p.getSkillManager().getCombatLevel() * 80);
					p.restart();
				} else if (won)
					p.getPacketSender().sendMessage("You didn't participate enough to receive a reward.");
				else {
					p.getPacketSender().sendMessage("You failed to kill all the portals in time.");
					DialogueManager.start(p, 356);
				}
				p.getMinigameAttributes().getPestControlAttributes().setDamageDealt(0);
			}
			it.remove();
		}
		playerMap.clear();
		PLAYERS_IN_BOAT = 0;
		for(Player p : World.getPlayers()) {
			if(p != null && p.getLocation() == Location.PEST_CONTROL_BOAT) {
				playerMap.put(p, WAITING);
				PLAYERS_IN_BOAT++;
			}
		}
		for(NPC n : npcList) {
			if(n == null || !n.isRegistered())
				continue;
			if (n.getLocation() == Location.PEST_CONTROL_GAME){
				World.deregister(n);
				n = null;
			}
		}
		npcList.clear();
		for(int i = 0; i < portals.length; i++)
			portals[i] = null;
		knight = null;
		gameRunning = false;
	}

	/*==========================================================================================================*/
	/*NPC STUFF*/
	/**
	 * Spawns the game's key/main NPC's on to the map
	 */
	private static void spawnMainNPCs() {
		int knightHealth = 3000 - (PLAYERS_IN_BOAT * 14);
		int portalHealth = getDefaultPortalConstitution();
		knight = spawnPCNPC(3782, new Position(2656,2592), knightHealth); //knight
		portals[0] = spawnPCNPC(6142, new Position(2628,2591), portalHealth); //purple
		portals[1] = spawnPCNPC(6143, new Position(2680,2588), portalHealth); //red
		portals[2] = spawnPCNPC(6144, new Position(2669,2570), portalHealth); //blue
		portals[3] = spawnPCNPC(6145, new Position(2645,2569), portalHealth); //yellow
		npcList.add(knight);
		for(NPC n : portals) {
			npcList.add(n);
		}
	}

	public static int getDefaultPortalConstitution() {
		return 1600 + (PLAYERS_IN_BOAT * 190);
	}

	/**
	 * Gets the text which shall be sent on to a player's interface
	 * @param i		The portal index to get information about
	 * @return		Information about the portal with the index specified
	 */
	private static String getPortalText(int i) {
		return (portals[i] != null && (portals[i].getConstitution() > 0 && portals[i].getConstitution() > 0)) ? Integer.toString(portals[i].getConstitution()) : "Dead";
	}

	/**
	 * Checks if all portals are dead (if true, the game will end and the players will win)
	 * @return		true if all portals are dead, otherwise false
	 */
	private static boolean allPortalsDead() {
		int count = 0;
		for(int i = 0; i < portals.length; i++) {
			if(portals[i] != null) {
				if(portals[i].getConstitution() <= 0 || portals[i].getConstitution() <= 0) {
					count++;
				}
			}
		}
		return count >= 4;	
	}

	/**
	 * Processes all NPC's within Pest control
	 */
	private static void processNPCs() {
		for(NPC npc : npcList) {
			if(npc == null)
				continue;
			if(npc.getLocation() == Location.PEST_CONTROL_GAME && npc.getConstitution() > 0) {
				for(PestControlNPC PCNPC: PestControlNPC.values()){
					if(npc.getId() >= PCNPC.lowestNPCID && npc.getId() <= PCNPC.highestNPCID){
						processPCNPC(npc, PCNPC);
						break;
					}
				}
			}
		}
		if(knight != null && knight.getConstitution() > 0 && Misc.getRandom(10) == 4) {
			knight.forceChat(KNIGHT_CHAT[Misc.getRandom(KNIGHT_CHAT.length - 1)]);
		}
	}

	/**
	 * Spawns a random NPC onto the map
	 */
	private static void spawnRandomNPC(){
		for(int i = 0; i < portals.length; i++){
			if(portals[i] != null && Math.random() > 0.5){
				PestControlNPC luckiest = PestControlNPC.values()[((int)(Math.random()*PestControlNPC.values().length))];
				if(luckiest != null){
					npcList.add(spawnPCNPC(luckiest.getLowestNPCID()+((int) (Math.random()*(luckiest.getHighestNPCID()-luckiest.getLowestNPCID()))), new Position(portals[i].getPosition().getX(), portals[i].getPosition().getY() - 1 , 0), 400));
				}
			}
		}
	}

	private static void processPCNPC(NPC npc, PestControlNPC _npc) {
		if(knight == null || npc == null || _npc == null)
			return;
		switch(_npc){
		case SPINNER:
			processSpinner(npc);
			break;
		case SHIFTER:
			processShifter(npc, _npc);
			break;
		case TORCHER:
			processDefiler(npc, _npc);
			break;
		case DEFILER:
			processDefiler(npc, _npc);
			break;
		}
	}

	/**
	 * Processes the spinner NPC
	 * Finds the closest portal, walks to it and heals it if injured.
	 * @param npc	The Spinner NPC
	 */
	private static void processSpinner(NPC npc) {
		NPC closestPortal = null;
		int distance = Integer.MAX_VALUE;
		for(int i = 0; i < portals.length; i++){
			if(portals[i] != null && portals[i].getConstitution() > 0 && portals[i].getConstitution() > 0){
				int distanceCandidate = distance(npc.getPosition().getX(), npc.getPosition().getY(), portals[i].getPosition().getX(), portals[i].getPosition().getY());
				if(distanceCandidate < distance){
					closestPortal = portals[i];
					distance = distanceCandidate;
				}
			}
		}
		if(closestPortal == null)
			return;
		npc.setEntityInteraction(closestPortal);
		if(distance <= 3 && closestPortal.getConstitution() < getDefaultPortalConstitution()){
			npc.performAnimation(new Animation(3911));
			closestPortal.setConstitution(closestPortal.getConstitution() + 2);
			if(closestPortal.getConstitution() > getDefaultPortalConstitution())
				closestPortal.setConstitution(getDefaultPortalConstitution());
		} else if(closestPortal != null){
			PathFinder.findPath(npc, closestPortal.getPosition().getX(), closestPortal.getPosition().getY() - 1, true, 1, 1);
			return;
		}
	}

	private static void processShifter(NPC npc, PestControlNPC npc_) {
		if(npc != null && knight != null) {
			if(isFree(npc, npc_)){
				if(distance(npc.getPosition().getX(), npc.getPosition().getY(), knight.getPosition().getX(), knight.getPosition().getY()) > 5){
					int npcId = npc.getId();
					Position pos = new Position(knight.getPosition().getX()+Misc.getRandom(3), knight.getPosition().getY()+Misc.getRandom(2), npc.getPosition().getZ());
					World.deregister(npc);
					npcList.remove(npc);
					npcList.add(spawnPCNPC(npcId, pos, 200));
				} else {
					if(distance(npc.getPosition().getX(), npc.getPosition().getY(), knight.getPosition().getX(), knight.getPosition().getY()) > 1){
						PathFinder.findPath(npc, knight.getPosition().getX(), knight.getPosition().getY() - 1, true, 1, 1);
					} else {
						npc.getCombatBuilder().reset(true);
						int max = 5 + (npc.getDefinition().getCombatLevel() / 9);
						attack(npc, knight, 3901, max, CombatIcon.MELEE);
					}
				}
			}
			if(npc.getPosition().copy().equals(knight.getPosition().copy()))
				MovementQueue.stepAway(npc);
		}
	}

	private static void processDefiler(final NPC npc, final PestControlNPC npc_) {
		if(npc != null) {
			if(isFree(npc, npc_)){
				if(distance(npc.getPosition().getX(), npc.getPosition().getY(), knight.getPosition().getX(), knight.getPosition().getY()) > 5){
					PathFinder.findPath(npc, knight.getPosition().getX(), knight.getPosition().getY() - 1, true, 1, 1);
				} else {
					if(Math.random() <= 0.04)
						for(Player p : playerMap.keySet()) {
							if(p != null) {
								String state = getState(p);
								if(state.equals(PLAYING))
									new Projectile(npc, knight, 1508, 80, 3, 43, 31, 0).sendProjectile();
							}
						}
					TaskManager.submit(new Task(1) {
						@Override
						public void execute() {
							int max = 7 + (npc.getDefinition().getCombatLevel() / 9);
							attack(npc, knight, npc_ == PestControlNPC.DEFILER ? 3920 : 3882, max, npc_ == PestControlNPC.DEFILER ? CombatIcon.RANGED : CombatIcon.MAGIC);
							stop();
						}
					});
				}
			}
		}
	}

	private static boolean attack(NPC npc, NPC knight, int anim, int maxhit, CombatIcon icon) {
		if(knight == null || npc == null)
			return false;
		npc.setEntityInteraction(knight);
		npc.setPositionToFace(knight.getPosition());
		if(npc.getCombatBuilder().getAttackTimer() == 0) {
			int damage = ((int) (Math.random()*maxhit));
			npc.performAnimation(new Animation(anim));
			knight.dealDamage(new Hit(damage, Hitmask.RED, icon));
			knight.getLastCombat().reset();
			npc.getCombatBuilder().setAttackTimer(3 + Misc.getRandom(3));
			npc.getLastCombat().reset();
			return true;
		}
		return false;
	}

	private static int distance(int x, int y, int dx, int dy){
		int xdiff = x - dx;
		int ydiff = y - dy;
		return (int) Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	private static boolean isFree(NPC npc, PestControlNPC npc_) {
		if(!npc.getCombatBuilder().isAttacking()) {
			return true;
		} else {
			if(npc_.tries++ >= 12){
				npc_.tries = 0;
				npc.getCombatBuilder().reset(true);
				return true;
			} else {
				return false;
			}
		}
	}

	public static PestControl[] runningGames = new PestControl[1];
	private int id;

	public int getId() {
		return id;
	}

	public PestControl(int id){
		this.id = id;
	}

	public enum PestControlNPC{
		SPINNER(3747, 3751),
		//SPLATTER(3727, 3731),
		SHIFTER(3732, 3741),
		TORCHER(3752, 3761),
		DEFILER(3762, 3771);
		//BRAWLER(3772, 3776);

		private final int lowestNPCID, highestNPCID;

		PestControlNPC(int lowestNPCID, int highestNPCID){
			this.lowestNPCID = lowestNPCID;
			this.highestNPCID = highestNPCID;
		}

		public int getLowestNPCID() {
			return lowestNPCID;
		}

		public int getHighestNPCID() {
			return highestNPCID;
		}

		public int tries;

	}

	public static final int WAIT_TIMER = 20;

	public static int waitTimer = WAIT_TIMER;
	private static NPC[] portals = new NPC[4];
	public static NPC knight;

	/**
	 * Handles the shop
	 * @param p			The player buying something from the shop
	 * @param item		The item which the player is buying
	 * @param id		The id of the item/skill which the player is buying
	 * @param amount	The amount of the item/skill xp which the player is buying
	 * @param cost		The amount it costs to buy this item
	 */
	public static void buyFromShop(Player p, boolean item, int id, int amount, int cost) {
		if(p.getPointsHandler().getCommendations() < cost && p.getRights() != PlayerRights.DEVELOPER) {
			p.getPacketSender().sendMessage("You don't have enough Commendations to purchase this.");
			return;
		}
		if(!p.getClickDelay().elapsed(500))
			return;
		String name = ItemDefinition.forId(id).getName();
		final String comm = cost > 1 ? "Commendations" : "Commendation";
		if (!item) {
			p.getPointsHandler().setCommendations((p.getPointsHandler().getCommendations() - cost), false);
			Skill skill = Skill.forId(id);
			int xp = amount * cost;
			p.getSkillManager().addExperience(skill, xp);
			p.getPacketSender().sendMessage("You have purchased "+xp+" "+Misc.formatText(skill.toString().toLowerCase())+" XP.");
		} else {
			if(p.getInventory().getFreeSlots() == 0) {
				p.getInventory().full();
				return;
			}
			int id2 = 0;
			if(id > 19784 && id < 19787) {
				if(id == 19785)
					id2 = 8839;
				else if(id == 19786)
					id2 = 8840;
				if(p.getInventory().contains(id2)) {
					p.getInventory().delete(id2, 1);
				} else {
					name = ItemDefinition.forId(id2).getName();
					p.getPacketSender().sendMessage("You need to have "+Misc.anOrA(name)+" "+name+" to purchase this uppgrade.");
					return;
				}
			}
			p.getPointsHandler().setCommendations((p.getPointsHandler().getCommendations() - cost), false);
			p.getInventory().add(id, amount);
			PlayerPanel.refreshPanel(p);
			p.getPacketSender().sendMessage("You have purchased "+Misc.anOrA(name)+" "+name+" for "+cost+" "+comm+".");
		}
		p.getPacketSender().sendString(18729, "Commendations: "+Integer.toString(p.getPointsHandler().getCommendations()));
		p.getClickDelay().reset();
	}

	public static boolean handleInterface(Player player, int id) {
		if(player.getInterfaceId() == 18730 || player.getInterfaceId() == 18746) {
			switch(id) {
			/**
			 * Pest control reward interface
			 */
			//PC Equipment Tab
			case 18733:PestControl.buyFromShop(player, true, 11665, 1, 200); return true;//melee helm
			case 18735:PestControl.buyFromShop(player, true, 11664, 1, 200); return true;//ranger helm
			case 18741:PestControl.buyFromShop(player, true, 11663, 1, 200); return true;//mage helm
			case 18734:PestControl.buyFromShop(player, true, 8839, 1, 250); return true;//top
			case 18737:PestControl.buyFromShop(player, true, 8840, 1, 250); return true;//robes
			case 18742:PestControl.buyFromShop(player, true, 8842, 1, 150); return true;//gloves
			case 18740:PestControl.buyFromShop(player, true, 19712, 1, 350); return true;//deflector
			case 18745:PestControl.buyFromShop(player, true, 19780, 1, 2000); return true;//korasi
			//ENCHANCE
			case 18749:PestControl.buyFromShop(player, true, 19785, 1, 125); return true;//elite top
			case 18750:PestControl.buyFromShop(player, true, 19786, 1, 125); return true;//elite legs
			//INTERFACE
			case 18743:
				player.getPacketSender().sendInterface(18746);
				return true;
			case 18748:
				player.getPacketSender().sendInterface(18730);
				return true;
			case 18728:
				player.getPacketSender().sendInterfaceRemoval();
				return true;
			}
		}
		return false;
	}

	public static NPC spawnPCNPC(int id, Position pos, int constitution) {
		NPC np = new NPC(id, pos);
		np.setConstitution(constitution);
		np.setDefaultConstitution(constitution);
		World.register(np);
		return np;
	}

}