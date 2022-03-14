
package com.ruse.world.entity.impl.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.PlayerDeathTask;
import com.ruse.engine.task.impl.WalkToTask;
import com.ruse.model.Animation;
import com.ruse.model.Appearance;
import com.ruse.model.CharacterAnimations;
import com.ruse.model.ChatMessage;
import com.ruse.model.Difficulty;
import com.ruse.model.DwarfCannon;
import com.ruse.model.Flag;
import com.ruse.model.GameMode;
import com.ruse.model.GameObject;
import com.ruse.model.Item;
import com.ruse.model.MagicSpellbook;
import com.ruse.model.PlayerInteractingOption;
import com.ruse.model.PlayerRelations;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Prayerbook;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.Bank.BankSearchAttributes;
import com.ruse.model.container.impl.DungeoneeringIronEquipment;
import com.ruse.model.container.impl.DungeoneeringIronInventory;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.container.impl.Inventory;
import com.ruse.model.container.impl.PriceChecker;
import com.ruse.model.container.impl.Shop;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.WeaponAnimations;
import com.ruse.model.definitions.WeaponInterfaces;
import com.ruse.model.definitions.WeaponInterfaces.WeaponInterface;
import com.ruse.model.input.Input;
import com.ruse.mysql.Hiscores;
import com.ruse.net.PlayerSession;
import com.ruse.net.SessionState;
import com.ruse.net.packet.PacketSender;
import com.ruse.util.FrameUpdater;
import com.ruse.util.Misc;
import com.ruse.util.Stopwatch;
import com.ruse.world.World;
import com.ruse.world.content.Achievements.AchievementAttributes;
import com.ruse.world.content.BankPin.BankPinAttributes;
import com.ruse.world.content.BonusManager;
import com.ruse.world.content.DropLog.DropLogEntry;
import com.ruse.world.content.KillsTracker.KillsEntry;
import com.ruse.world.content.LoyaltyProgramme;
import com.ruse.world.content.LoyaltyProgramme.LoyaltyTitles;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.content.PointsHandler;
import com.ruse.world.content.TeleportInterface.TeleportInterfaceData;
import com.ruse.world.content.Trading;
import com.ruse.world.content.clan.ClanChat;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.effect.CombatPoisonEffect.CombatPoisonData;
import com.ruse.world.content.combat.magic.CombatSpell;
import com.ruse.world.content.combat.magic.CombatSpells;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.pvp.PlayerKillingAttributes;
import com.ruse.world.content.combat.range.CombatRangedAmmo.RangedWeaponData;
import com.ruse.world.content.combat.strategy.CombatStrategies;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.combat.weapon.FightType;
import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.grandexchange.GrandExchangeSlot;
import com.ruse.world.content.minigames.MinigameAttributes;
import com.ruse.world.content.minigames.impl.Dueling;
import com.ruse.world.content.skill.SkillManager;
import com.ruse.world.content.skill.impl.construction.HouseFurniture;
import com.ruse.world.content.skill.impl.construction.Portal;
import com.ruse.world.content.skill.impl.construction.Room;
import com.ruse.world.content.skill.impl.farming.Farming;
import com.ruse.world.content.skill.impl.slayer.Slayer;
import com.ruse.world.content.skill.impl.summoning.Pouch;
import com.ruse.world.content.skill.impl.summoning.Summoning;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;


public class Player extends Character {

	public Player(PlayerSession playerIO) {
		super(GameSettings.DEFAULT_POSITION.copy());
		this.session = playerIO;
	}
	
	private String mac;
	private String uuid;
	
	public String getMac() {
		return mac;
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public Player setMac(String mac) {
		this.mac = mac;
		return this;
	}
	
	public Player setUUID(String uuid) {
		this.uuid = uuid;
		return this;
	}


	@Override
	public void appendDeath() {
		if(!isDying) {
			isDying = true;
			TaskManager.submit(new PlayerDeathTask(this));
		}
	}

	@Override
	public int getConstitution() {
		return getSkillManager().getCurrentLevel(Skill.CONSTITUTION);
	}

	@Override
	public Character setConstitution(int constitution) {
		if(isDying) {
			return this;
		}
		skillManager.setCurrentLevel(Skill.CONSTITUTION, constitution);
		packetSender.sendSkill(Skill.CONSTITUTION);
		if(getConstitution() <= 0 && !isDying)
			appendDeath();
		return this;
	}
	
	public boolean couldHeal() {
		boolean nexEffect = getEquipment().wearingNexAmours();
		int level = skillManager.getMaxLevel(Skill.CONSTITUTION);
		int nexHp = level + 400;
		int currentlevel = skillManager.getCurrentLevel(Skill.CONSTITUTION);
		
		if (currentlevel >= level && !nexEffect) {
			return false;
		}
		if (currentlevel >= nexHp && nexEffect) {
			return false;
		}
		
		return true;
	}

	@Override
	public void heal(int amount) {
		boolean nexEffect = getEquipment().wearingNexAmours();
		int level = skillManager.getMaxLevel(Skill.CONSTITUTION);
		int nexHp = level + 400;
		int currentlevel = skillManager.getCurrentLevel(Skill.CONSTITUTION);
		
		if (currentlevel >= level && !nexEffect) {
			return;
		}
		if (currentlevel >= nexHp && nexEffect) {
			return;
		}
		
		if ((currentlevel + amount) >= (nexEffect ? nexHp : level)) {
			setConstitution(nexEffect ? nexHp : level);
		} else if ((currentlevel + amount) < (nexEffect ? nexHp : level)) {
			setConstitution(currentlevel + amount);
		}
		
		getSkillManager().updateSkill(Skill.CONSTITUTION);
	}

	@Override
	public int getBaseAttack(CombatType type) {
		if (type == CombatType.RANGED)
			return skillManager.getCurrentLevel(Skill.RANGED);
		else if (type == CombatType.MAGIC)
			return skillManager.getCurrentLevel(Skill.MAGIC);
		return skillManager.getCurrentLevel(Skill.ATTACK);
	}

	@Override
	public int getBaseDefence(CombatType type) {
		if (type == CombatType.MAGIC)
			return skillManager.getCurrentLevel(Skill.MAGIC);
		return skillManager.getCurrentLevel(Skill.DEFENCE);
	}

	@Override
	public int getAttackSpeed() {
		int speed = weapon.getSpeed();
		String weapon = equipment.get(Equipment.WEAPON_SLOT).getDefinition().getName();
		if(getCurrentlyCasting() != null) {
			if(getCurrentlyCasting() == CombatSpells.BLOOD_BLITZ.getSpell() || getCurrentlyCasting() == CombatSpells.SHADOW_BLITZ.getSpell() || getCurrentlyCasting() == CombatSpells.SMOKE_BLITZ.getSpell() || getCurrentlyCasting() == CombatSpells.ICE_BLITZ.getSpell()) {
				return 5;
			} else {
				return 6;
			}
		}
		int weaponId = equipment.get(Equipment.WEAPON_SLOT).getId();
		/*if(weaponId == 1419) {
			speed -= 2;
		}*/
		if (fightType == FightType.CROSSBOW_RAPID || fightType == FightType.LONGBOW_RAPID || weaponId == 6522 && fightType == FightType.KNIFE_RAPID || weapon.contains("rapier")) {
			if(weaponId != 11235) {
				speed--;
			}
		} else if(weaponId != 6522 && weaponId != 15241 && (fightType == FightType.SHORTBOW_RAPID || fightType == FightType.DART_RAPID || fightType == FightType.KNIFE_RAPID || fightType == FightType.THROWNAXE_RAPID || fightType == FightType.JAVELIN_RAPID) || weaponId == 11730) {
			speed -= 2;
		}
		return speed;
		//	return DesolaceFormulas.getAttackDelay(this);
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Player)) {
			return false;
		}

		Player p = (Player) o;
		return p.getIndex() == getIndex() || p.getUsername().equals(username);
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public void poisonVictim(Character victim, CombatType type) {
		if (type == CombatType.MELEE || weapon == WeaponInterface.DART || weapon == WeaponInterface.KNIFE || weapon == WeaponInterface.THROWNAXE || weapon == WeaponInterface.JAVELIN) {
			CombatFactory.poisonEntity(victim, CombatPoisonData.getPoisonType(equipment.get(Equipment.WEAPON_SLOT)));
		} else if (type == CombatType.RANGED) {
			CombatFactory.poisonEntity(victim, CombatPoisonData.getPoisonType(equipment.get(Equipment.AMMUNITION_SLOT)));
		}
	}

	public CombatStrategy getStrategy(int npc){
		return CombatStrategies.getStrategy(npc);
	}
	@Override
	public CombatStrategy determineStrategy() {
		if (specialActivated && castSpell == null) {

			if (combatSpecial.getCombatType() == CombatType.MELEE) {
				return CombatStrategies.getDefaultMeleeStrategy();
			} else if (combatSpecial.getCombatType() == CombatType.RANGED) {
				setRangedWeaponData(RangedWeaponData.getData(this));
				return CombatStrategies.getDefaultRangedStrategy();
			} else if (combatSpecial.getCombatType() == CombatType.MAGIC) {
				return CombatStrategies.getDefaultMagicStrategy();
			}
		}

		if (castSpell != null || autocastSpell != null) {
			return CombatStrategies.getDefaultMagicStrategy();
		}

		RangedWeaponData data = RangedWeaponData.getData(this);
		if (data != null) {
			setRangedWeaponData(data);
			return CombatStrategies.getDefaultRangedStrategy();
		}

		return CombatStrategies.getDefaultMeleeStrategy();
	}

	public void process() {
		process.sequence();
	}

	public void dispose() {
		//save();
		packetSender.sendLogout();
	}

	public void save() {
		if (session.getState() != SessionState.LOGGED_IN && session.getState() != SessionState.LOGGING_OUT) {
			return;
		}
		PlayerSaving.save(this);
	}

	public boolean logout() {
		if (getCombatBuilder().isBeingAttacked()) {
			getPacketSender().sendMessage("You must wait a few seconds after being out of combat before doing this.");
			return false;
		}
		if(getConstitution() <= 0 || isDying || settingUpCannon || crossingObstacle) {
			getPacketSender().sendMessage("You cannot log out at the moment.");
			return false;
		}
		new Thread(new Hiscores(this)).start();
		return true;
	}

	public void restart() { //@TODO TO-DO brandon pk update shit here ???
		setStunDelay(0);
		setFreezeDelay(0);
		setOverloadPotionTimer(0);
		setPrayerRenewalPotionTimer(0);
		setSpecialPercentage(100);
		setSpecialActivated(false);
		CombatSpecial.updateBar(this);
		setHasVengeance(false);
		setSkullTimer(0);
		setSkullIcon(0);
		setTeleblockTimer(0);
		setPoisonDamage(0);
		setStaffOfLightEffect(0);
		performAnimation(new Animation(65535));
		WeaponInterfaces.assign(this, getEquipment().get(Equipment.WEAPON_SLOT));
		WeaponAnimations.update(this);
		PrayerHandler.deactivateAll(this);
		CurseHandler.deactivateAll(this);
		getEquipment().refreshItems();
		getInventory().refreshItems();
		for (Skill skill : Skill.values())
			getSkillManager().setCurrentLevel(skill, getSkillManager().getMaxLevel(skill));
		setRunEnergy(100);
		setDying(false);
		getMovementQueue().setLockMovement(false).reset();
		getUpdateFlag().flag(Flag.APPEARANCE);
	}

	public boolean busy() {
		return interfaceId > 0 || isBanking || shopping || trading.inTrade() || dueling.inDuelScreen || isResting;
	}

	/*
	 * Fields
	 */

	/*** STRINGS ***/
	private String username;
	private String password;
	private String salt;
	private String serial_number;
	private String emailAddress;
	private String hostAddress;
	private String clanChatName;
	private String yellHex;
	private String strippedJewelryName;
	private String yellTitle;

	/*** LONGS **/
	private Long longUsername;
	private long moneyInPouch;
	private long totalPlayTime;
	//Timers (Stopwatches)
	private final Stopwatch sqlTimer = new Stopwatch();
	private final Stopwatch foodTimer = new Stopwatch();
	private final Stopwatch potionTimer = new Stopwatch();
	private final Stopwatch lastRunRecovery = new Stopwatch();
	private final Stopwatch clickDelay = new Stopwatch();
	private final Stopwatch lastItemPickup = new Stopwatch();
	private final Stopwatch lastYell = new Stopwatch();
	private final Stopwatch lastVoteClaim = new Stopwatch();
	private final Stopwatch lastVengeance = new Stopwatch();
	private final Stopwatch emoteDelay = new Stopwatch();
	private final Stopwatch specialRestoreTimer = new Stopwatch();
	private final Stopwatch lastSummon = new Stopwatch();
	private final Stopwatch recordedLogin = new Stopwatch();
	private final Stopwatch creationDate = new Stopwatch();
	private final Stopwatch tolerance = new Stopwatch();
	private final Stopwatch lougoutTimer = new Stopwatch();
	private final Stopwatch lastDfsTimer = new Stopwatch();

	/*** INSTANCES ***/
	private final CopyOnWriteArrayList<KillsEntry> killsTracker = new CopyOnWriteArrayList<KillsEntry>();
	private final CopyOnWriteArrayList<DropLogEntry> dropLog = new CopyOnWriteArrayList<DropLogEntry>();
	private final CopyOnWriteArrayList<NPC> npc_faces_updated = new CopyOnWriteArrayList<NPC>();
	private ArrayList<HouseFurniture> houseFurniture = new ArrayList<HouseFurniture>();
	private ArrayList<Portal> housePortals = new ArrayList<>();
	private final List<Player> localPlayers = new LinkedList<Player>();
	private final List<NPC> localNpcs = new LinkedList<NPC>();

	private PlayerSession session;
	private final PlayerProcess process = new PlayerProcess(this);
	private final PlayerKillingAttributes playerKillingAttributes = new PlayerKillingAttributes(this);
	private final MinigameAttributes minigameAttributes = new MinigameAttributes();
	private final BankPinAttributes bankPinAttributes = new BankPinAttributes();
	private final BankSearchAttributes bankSearchAttributes = new BankSearchAttributes();
	private final AchievementAttributes achievementAttributes = new AchievementAttributes();
	private CharacterAnimations characterAnimations = new CharacterAnimations();
	private final BonusManager bonusManager = new BonusManager();
	private final PointsHandler pointsHandler = new PointsHandler(this);
	private final PacketSender packetSender = new PacketSender(this);
	private final Appearance appearance = new Appearance(this);
	private final FrameUpdater frameUpdater = new FrameUpdater();
	private PlayerRights rights = PlayerRights.PLAYER;
	private SkillManager skillManager = new SkillManager(this);
	private PlayerRelations relations = new PlayerRelations(this);
	private ChatMessage chatMessages = new ChatMessage();
	private Inventory inventory = new Inventory(this);
	private Equipment equipment = new Equipment(this);
	private Equipment preSetEquipment = new Equipment(this);
	private DungeoneeringIronInventory dungeoneeringIronInventory = new DungeoneeringIronInventory(this);
	private DungeoneeringIronEquipment dungeoneeringIronEquipment = new DungeoneeringIronEquipment(this);
	private PriceChecker priceChecker = new PriceChecker(this);
	private Trading trading = new Trading(this);
	private Dueling dueling = new Dueling(this);
	private Slayer slayer = new Slayer(this);
	private Farming farming = new Farming(this);
	private Summoning summoning = new Summoning(this);
	private Bank[] bankTabs = new Bank[9];
	private Room[][][] houseRooms = new Room[5][13][13];
	private PlayerInteractingOption playerInteractingOption = PlayerInteractingOption.NONE;
	private GameMode gameMode = GameMode.NORMAL;
	private Difficulty difficulty = Difficulty.MODERN; //modern now default difficulty, was extreme
	private CombatType lastCombatType = CombatType.MELEE;
	private FightType fightType = FightType.UNARMED_PUNCH;
	private Prayerbook prayerbook = Prayerbook.NORMAL;
	private MagicSpellbook spellbook = MagicSpellbook.NORMAL;
	private LoyaltyTitles loyaltyTitle = LoyaltyTitles.NONE;
	private ClanChat currentClanChat;
	private Input inputHandling;
	private WalkToTask walkToTask;
	private Shop shop;
	private GameObject interactingObject;
	private Item interactingItem;
	private Dialogue dialogue;
	private DwarfCannon cannon;
	private CombatSpell autocastSpell, castSpell, previousCastSpell;
	private RangedWeaponData rangedWeaponData;
	private CombatSpecial combatSpecial;
	private WeaponInterface weapon;
	private Item untradeableDropItem;
	private Object[] usableObject;
	private GrandExchangeSlot[] grandExchangeSlots = new GrandExchangeSlot[6];
	private Task currentTask;
	private Position resetPosition;
	private Pouch selectedPouch;

	/*** INTS ***/
	private int[] brawlerCharges = new int[11];
	private int[] ancientArmourCharges = new int[15];
	private int[] forceMovement = new int[7];
	private int[] leechedBonuses = new int[7];
	private int[] ores = new int[2];
	private int[] constructionCoords;
	private int[] previousTeleports = new int[] {0, 0, 0, 0};
	private int recoilCharges;
	private int forgingCharges;
	private int blowpipeCharges;
	private int runEnergy = 100;
	private int currentBankTab;
	private int interfaceId, walkableInterfaceId, multiIcon;
	private int dialogueActionId;
	private int overloadPotionTimer, prayerRenewalPotionTimer;
	private int fireImmunity, fireDamageModifier;
	private int amountDonated;
	private int wildernessLevel;
	private int fireAmmo;
	private int specialPercentage = 100;
	private int skullIcon = -1, skullTimer;
	private int teleblockTimer;
	private int dragonFireImmunity;
	private int poisonImmunity;
	private int shadowState;
	private int effigy;
	private int dfsCharges;
	private int playerViewingIndex;
	private int staffOfLightEffect;
	private int minutesBonusExp = -1;
	private int selectedGeSlot = -1;
	private int selectedGeItem = -1;
	private int geQuantity;
	private int gePricePerItem;
	private int selectedSkillingItem;
	private int currentBookPage;
	private int storedRuneEssence, storedPureEssence;
	private int trapsLaid;
	private int skillAnimation;
	private int houseServant;
	private int houseServantCharges;
	private int servantItemFetch;
	private int portalSelected;
	private int constructionInterface;
	private int buildFurnitureId;
	private int buildFurnitureX;
	private int buildFurnitureY;
	private int combatRingType;
	private int barrowsKilled;
	private int clueProgress;
	private int christmas2016;
	private int newYear2017;
	private int lastTomed;
	private int selectedSkillingItemTwo;
	private int easter2017 = 0;

	/*** BOOLEANS ***/
	private boolean unlockedLoyaltyTitles[] = new boolean[12];
	private boolean[] crossedObstacles = new boolean[7];
	private boolean processFarming;
	private boolean crossingObstacle;
	private boolean targeted;
	private boolean isBanking, noteWithdrawal, swapMode;
	private boolean regionChange, allowRegionChangePacket;
	private boolean isDying;
	private boolean isRunning = true, isResting;
	private boolean experienceLocked;
	private boolean clientExitTaskActive;
	private boolean drainingPrayer;
	private boolean shopping;
	private boolean settingUpCannon;
	private boolean hasVengeance;
	private boolean killsTrackerOpen;
	private boolean acceptingAid;
	private boolean autoRetaliate;
	private boolean autocast;
	private boolean specialActivated;
	private boolean isCoughing;
	private boolean playerLocked;
	private boolean recoveringSpecialAttack;
	private boolean soundsActive, musicActive;
	private boolean newPlayer;
	private boolean openBank;
	private boolean inActive;
	private boolean inConstructionDungeon;
	private boolean isBuildingMode;
	private boolean voteMessageSent;
	private boolean receivedStarter;
	private boolean fri13may16;
	private boolean toggledglobalmessages;
	private boolean spiritdebug;
	private boolean reffered;
	private boolean indung;
	private boolean doingClanBarrows;
	private boolean flying;
	private boolean canFly;
	private boolean ghostWalking;
	private boolean canGhostWalk;
	private boolean[] hween2016  = new boolean[7];
	private boolean doneHween2016;
	private boolean[] bossPets = new boolean[100]; //[BossPet.values().length]
	private boolean bonecrushEffect = true;
	private TeleportInterfaceData tid;
	private List<Integer> lootList;
	private boolean clickToTeleport;



	/*
	 * Getters & Setters
	 */

	public PlayerSession getSession() {
		return session;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public DungeoneeringIronInventory getDungeoneeringIronInventory() {
		return dungeoneeringIronInventory;
	}

	public void setDungeoneeringStorage(DungeoneeringIronInventory dungeoneeringIronInventory) {
		this.dungeoneeringIronInventory = dungeoneeringIronInventory;
	}

	public DungeoneeringIronEquipment getDungeoneeringIronEquipment() {
		return dungeoneeringIronEquipment;
	}

	public void setDungeoneeringIronEquipment(DungeoneeringIronEquipment dungeoneeringIronEquipment) {
		this.dungeoneeringIronEquipment = dungeoneeringIronEquipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public Equipment getPreSetEquipment() {
		return preSetEquipment;
	}

	public void setPreSetEquipment(Equipment equipment) {
		this.preSetEquipment = equipment;
	}

	public PriceChecker getPriceChecker() {
		return priceChecker;
	}

	/*
	 * Getters and setters
	 */

	public String getUsername() {
		return username;
	}

	public Player setUsername(String username) {
		this.username = username;
		return this;
	}

	public Long getLongUsername() {
		return longUsername;
	}


	public void rspsdata(Player player, String username) {
		try{
			username = username.replaceAll(" ","_");
		    String secret = "8898fc10c4f04db8b0c26796e5fbb1a1"; //YOUR SECRET KEY!
			URL url = new URL("http://app.gpay.io/api/runescape/"+username+"/"+secret);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String results = reader.readLine();
				if(results.toLowerCase().contains("!error:")){
					PlayerLogs.log(player.getUsername(), "[GPAY] "+results);
					//Logger.log(this, "[GPAY]"+results);
				} else {
				String[] ary = results.split(",");
				for (int i = 0; i < ary.length; i++) {
					switch (ary[i]) {
					case "0":
						player.getPacketSender().sendMessage("There are no pending rewards found...");
						player.getPacketSender().sendMessage("If you didn't get any items, but paid -");
						player.getPacketSender()
								.sendMessage("please try again in 5 minutes <col=ff000>BEFORE <col=0>seeking help.");
						break;
					case "20075": // product ids can be found on the webstore
									// page
						player.getPacketSender().sendMessage("You've redeemed a Member Scroll!"); // add
																									// items
																									// for
																									// the
																									// first
																									// product
						player.getInventory().add(10944, 1);
						World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
								+ " has just purchased a Member Scroll!");
						PlayerLogs.log(player.getUsername(),
								player.getUsername() + " has just purchased: " + ItemDefinition.forId(10944).getName()
										+ ". on IP address: " + player.getHostAddress());
						break;
					case "20076":
						player.getPacketSender().sendMessage("You've redeemed a $5 Scroll!");
						player.getInventory().add(6769, 1);
						World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
								+ " has just purchased a $5 Scroll!");
						PlayerLogs.log(player.getUsername(), player.getUsername() + " has just purchased: "
								+ ItemDefinition.forId(6769).getName() + ". on IP address: " + player.getHostAddress());
						break;
					case "20077":
						player.getPacketSender().sendMessage("You've redeemed a $10 Scroll!");
						player.getInventory().add(10942, 1);
						World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
								+ " has just purchased a $10 Scroll!");
						PlayerLogs.log(player.getUsername(),
								player.getUsername() + " has just purchased: " + ItemDefinition.forId(10942).getName()
										+ ". on IP address: " + player.getHostAddress());
						break;
					case "20078":
						player.getPacketSender().sendMessage("You've redeemed a $25 Scroll!");
						player.getInventory().add(10934, 1);
						World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
								+ " has just purchased a $25 Scroll!");
						PlayerLogs.log(player.getUsername(),
								player.getUsername() + " has just purchased: " + ItemDefinition.forId(10934).getName()
										+ ". on IP address: " + player.getHostAddress());
						break;
					case "20079":
						player.getPacketSender().sendMessage("You've redeemed a $50 Scroll!");
						player.getInventory().add(10935, 1);
						World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
								+ " has just purchased a $50 Scroll!");
						PlayerLogs.log(player.getUsername(),
								player.getUsername() + " has just purchased: " + ItemDefinition.forId(10935).getName()
										+ ". on IP address: " + player.getHostAddress());
						break;
					case "20080":
						player.getPacketSender().sendMessage("You've redeemed a $100 Scroll!");
						player.getInventory().add(10943, 1);
						World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
								+ " has just purchased a $100 Scroll!");
						PlayerLogs.log(player.getUsername(),
								player.getUsername() + " has just purchased: " + ItemDefinition.forId(10943).getName()
										+ ". on IP address: " + player.getHostAddress());
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Player setLongUsername(Long longUsername) {
		this.longUsername = longUsername;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public String getEmailAddress() {
		return this.emailAddress;
	}

	public void unlockPkTitles() {
		if (this.getPlayerKillingAttributes().getPlayerKills() >= 1) {
			LoyaltyProgramme.unlock(this, LoyaltyTitles.KILLER);
		}
		if (this.getPlayerKillingAttributes().getPlayerKills() >= 20) {
			LoyaltyProgramme.unlock(this, LoyaltyTitles.SLAUGHTERER);
		}
		if (this.getPlayerKillingAttributes().getPlayerKills() >= 50) {
			LoyaltyProgramme.unlock(this, LoyaltyTitles.GENOCIDAL);
		}
		if (this.getPlayerKillingAttributes().getPlayerKillStreak() >= 15) {
			LoyaltyProgramme.unlock(this, LoyaltyTitles.IMMORTAL);
		}
		PlayerPanel.refreshPanel(this);
	}

	public void setEmailAddress(String address) {
		this.emailAddress = address;
	}
	
	public void updateGearBonuses() {
		Misc.updateGearBonuses(this);
	}

	public void newStance() {
		WeaponAnimations.update(this);
		this.getUpdateFlag().flag(Flag.APPEARANCE);
		return;
	}

	public Player setPassword(String password) {
		this.password = password;
		return this;
	}


	public String getHostAddress() {
		return hostAddress;
	}

	public Player setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
		return this;
	}

	public String getSerialNumber() {
		return serial_number;
	}

	public Player setSerialNumber(String serial_number) {
		this.serial_number = serial_number;
		return this;
	}

	public FrameUpdater getFrameUpdater() {
		return this.frameUpdater;
	}

	public PlayerRights getRights() {
		return rights;
	}

	public Player setRights(PlayerRights rights) {
		this.rights = rights;
		return this;
	}

	public ChatMessage getChatMessages() {
		return chatMessages;
	}

	public PacketSender getPacketSender() {
		return packetSender;
	}

	public SkillManager getSkillManager() {
		return skillManager;
	}

	public Appearance getAppearance() {
		return appearance;
	}
	public PlayerRelations getRelations() {
		return relations;
	}

	public PlayerKillingAttributes getPlayerKillingAttributes() {
		return playerKillingAttributes;
	}

	public PointsHandler getPointsHandler() {
		return pointsHandler;
	}

	public boolean isImmuneToDragonFire() {
		return dragonFireImmunity > 0;
	}

	public int getDragonFireImmunity() {
		return dragonFireImmunity;
	}

	public void setDragonFireImmunity(int dragonFireImmunity) {
		this.dragonFireImmunity = dragonFireImmunity;
	}

	public void incrementDragonFireImmunity(int amount) {
		dragonFireImmunity += amount;
	}

	public void decrementDragonFireImmunity(int amount) {
		dragonFireImmunity -= amount;
	}

	public int getPoisonImmunity() {
		return poisonImmunity;
	}

	public void setPoisonImmunity(int poisonImmunity) {
		this.poisonImmunity = poisonImmunity;
	}

	public void incrementPoisonImmunity(int amount) {
		poisonImmunity += amount;
	}

	public void decrementPoisonImmunity(int amount) {
		poisonImmunity -= amount;
	}

	public boolean isAutoRetaliate() {
		return autoRetaliate;
	}

	public void setAutoRetaliate(boolean autoRetaliate) {
		this.autoRetaliate = autoRetaliate;
	}

	/**
	 * @return the castSpell
	 */
	public CombatSpell getCastSpell() {
		return castSpell;
	}

	/**
	 * @param castSpell
	 *            the castSpell to set
	 */
	public void setCastSpell(CombatSpell castSpell) {
		this.castSpell = castSpell;
	}

	public CombatSpell getPreviousCastSpell() {
		return previousCastSpell;
	}

	public void setPreviousCastSpell(CombatSpell previousCastSpell) {
		this.previousCastSpell = previousCastSpell;
	}

	/**
	 * @return the autocast
	 */
	public boolean isAutocast() {
		return autocast;
	}

	/**
	 * @param autocast
	 *            the autocast to set
	 */
	public void setAutocast(boolean autocast) {
		this.autocast = autocast;
	}

	public boolean checkItem(int slot, int id) {
		if (this.getEquipment().getItems()[slot].getId() == id) {
			return true;
		} else {
			return false;
		}
	} // (player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 15492

	/**
	 * @return the skullTimer
	 */
	public int getSkullTimer() {
		return skullTimer;
	}

	/**
	 * @param skullTimer
	 *            the skullTimer to set
	 */
	public void setSkullTimer(int skullTimer) {
		this.skullTimer = skullTimer;
	}

	public void decrementSkullTimer() {
		skullTimer -= 50;
	}

	/**
	 * @return the skullIcon
	 */
	public int getSkullIcon() {
		return skullIcon;
	}

	/**
	 * @param skullIcon
	 *            the skullIcon to set
	 */
	public void setSkullIcon(int skullIcon) {
		this.skullIcon = skullIcon;
	}

	/**
	 * @return the teleblockTimer
	 */
	public int getTeleblockTimer() {
		return teleblockTimer;
	}

	/**
	 * @param teleblockTimer
	 *            the teleblockTimer to set
	 */
	public void setTeleblockTimer(int teleblockTimer) {
		this.teleblockTimer = teleblockTimer;
	}

	public void decrementTeleblockTimer() {
		teleblockTimer--;
	}

	/**
	 * @return the autocastSpell
	 */
	public CombatSpell getAutocastSpell() {
		return autocastSpell;
	}

	/**
	 * @param autocastSpell
	 *            the autocastSpell to set
	 */
	public void setAutocastSpell(CombatSpell autocastSpell) {
		this.autocastSpell = autocastSpell;
	}

	/**
	 * @return the specialPercentage
	 */
	public int getSpecialPercentage() {
		return specialPercentage;
	}

	/**
	 * @param specialPercentage
	 *            the specialPercentage to set
	 */
	public void setSpecialPercentage(int specialPercentage) {
		this.specialPercentage = specialPercentage;
	}

	/**
	 * @return the fireAmmo
	 */
	public int getFireAmmo() {
		return fireAmmo;
	}

	/**
	 * @param fireAmmo
	 *            the fireAmmo to set
	 */
	public void setFireAmmo(int fireAmmo) {
		this.fireAmmo = fireAmmo;
	}

	public int getWildernessLevel() {
		return wildernessLevel;
	}

	public void setWildernessLevel(int wildernessLevel) {
		this.wildernessLevel = wildernessLevel;
	}

	/**
	 * @return the combatSpecial
	 */
	public CombatSpecial getCombatSpecial() {
		return combatSpecial;
	}

	/**
	 * @param combatSpecial
	 *            the combatSpecial to set
	 */
	public void setCombatSpecial(CombatSpecial combatSpecial) {
		this.combatSpecial = combatSpecial;
	}

	/**
	 * @return the specialActivated
	 */
	public boolean isSpecialActivated() {
		return specialActivated;
	}

	/**
	 * @param specialActivated
	 *            the specialActivated to set
	 */
	public void setSpecialActivated(boolean specialActivated) {
		this.specialActivated = specialActivated;
	}

	public void decrementSpecialPercentage(int drainAmount) {
		this.specialPercentage -= drainAmount;

		if (specialPercentage < 0) {
			specialPercentage = 0;
		}
	}

	public void incrementSpecialPercentage(int gainAmount) {
		this.specialPercentage += gainAmount;

		if (specialPercentage > 100) {
			specialPercentage = 100;
		}
	}

	/**
	 * @return the rangedAmmo
	 */
	public RangedWeaponData getRangedWeaponData() {
		return rangedWeaponData;
	}

	/**
	 * @param rangedAmmo
	 *            the rangedAmmo to set
	 */
	public void setRangedWeaponData(RangedWeaponData rangedWeaponData) {
		this.rangedWeaponData = rangedWeaponData;
	}

	/**
	 * @return the weapon.
	 */
	public WeaponInterface getWeapon() {
		return weapon;
	}

	/**
	 * @param weapon
	 *            the weapon to set.
	 */
	public void setWeapon(WeaponInterface weapon) {
		this.weapon = weapon;
	}

	/**
	 * @return the fightType
	 */
	public FightType getFightType() {
		return fightType;
	}

	/**
	 * @param fightType
	 *            the fightType to set
	 */
	public void setFightType(FightType fightType) {
		this.fightType = fightType;
	}

	public Bank[] getBanks() {
		return bankTabs;
	}

	public Bank getBank(int index) {
		return bankTabs[index];
	}

	public Player setBank(int index, Bank bank) {
		this.bankTabs[index] = bank;
		return this;
	}

	public boolean isAcceptAid() {
		return acceptingAid;
	}
	public void setAcceptAid(boolean acceptingAid) {
		this.acceptingAid = acceptingAid;
	}

	public Trading getTrading() {
		return trading;
	}

	public Dueling getDueling() {
		return dueling;
	}

	public CopyOnWriteArrayList<KillsEntry> getKillsTracker() {
		return killsTracker;
	}

	public CopyOnWriteArrayList<DropLogEntry> getDropLog() {
		return dropLog;
	}

	public void setWalkToTask(WalkToTask walkToTask) {
		this.walkToTask = walkToTask;
	}

	public WalkToTask getWalkToTask() {
		return walkToTask;
	}

	public Player setSpellbook(MagicSpellbook spellbook) {
		this.spellbook = spellbook;
		return this;
	}


	public MagicSpellbook getSpellbook() {
		return spellbook;
	}

	public Player setPrayerbook(Prayerbook prayerbook) {
		this.prayerbook = prayerbook;
		return this;
	}

	public Prayerbook getPrayerbook() {
		return prayerbook;
	}

	/**
	 * The player's local players list.
	 */
	public List<Player> getLocalPlayers() {
		return localPlayers;
	}

	/**
	 * The player's local npcs list getter
	 */
	public List<NPC> getLocalNpcs() {
		return localNpcs;
	}

	public CopyOnWriteArrayList<NPC> getNpcFacesUpdated() {
		return npc_faces_updated;
	}

	public Player setInterfaceId(int interfaceId) {
		this.interfaceId = interfaceId;
		return this;
	}

	public int getInterfaceId() {
		return this.interfaceId;
	}

	public boolean isDying() {
		return isDying;
	}

	public void setDying(boolean isDying) {
		this.isDying = isDying;
	}

	public int[] getForceMovement() {
		return forceMovement;
	}

	public Player setForceMovement(int[] forceMovement) {
		this.forceMovement = forceMovement;
		return this;
	}

	/**
	 * @return the equipmentAnimation
	 */
	public CharacterAnimations getCharacterAnimations() {
		return characterAnimations;
	}

	/**
	 * @return the equipmentAnimation
	 */
	public void setCharacterAnimations(CharacterAnimations equipmentAnimation) {
		this.characterAnimations = equipmentAnimation.clone();
	}

	public LoyaltyTitles getLoyaltyTitle() {
		return loyaltyTitle;
	}

	public void setLoyaltyTitle(LoyaltyTitles loyaltyTitle) {
		this.loyaltyTitle = loyaltyTitle;
	}

	public void setWalkableInterfaceId(int interfaceId2) {
		this.walkableInterfaceId = interfaceId2;
	}

	public PlayerInteractingOption getPlayerInteractingOption() {
		return playerInteractingOption;
	}

	public Player setPlayerInteractingOption(PlayerInteractingOption playerInteractingOption) {
		this.playerInteractingOption = playerInteractingOption;
		return this;
	}

	public int getMultiIcon() {
		return multiIcon;
	}

	public Player setMultiIcon(int multiIcon) {
		this.multiIcon = multiIcon;
		return this;
	}

	public int getWalkableInterfaceId() {
		return walkableInterfaceId;
	}

	public boolean soundsActive() {
		return soundsActive;
	}

	public void setSoundsActive(boolean soundsActive) {
		this.soundsActive = soundsActive;
	}

	public boolean musicActive() {
		return musicActive;
	}

	public void setMusicActive(boolean musicActive) {
		this.musicActive = musicActive;
	}

	public BonusManager getBonusManager() {
		return bonusManager;
	}

	public int getRunEnergy() {
		return runEnergy;
	}

	public Player setRunEnergy(int runEnergy) {
		this.runEnergy = runEnergy;
		return this;
	}

	public Stopwatch getLastRunRecovery() {
		return lastRunRecovery;
	}

	public Player setRunning(boolean isRunning) {
		this.isRunning = isRunning;
		return this;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public Player setResting(boolean isResting) {
		this.isResting = isResting;
		return this;
	}

	public boolean isResting() {
		return isResting;
	}

	public void setMoneyInPouch(long moneyInPouch) {
		this.moneyInPouch = moneyInPouch;
	}

	public long getMoneyInPouch() {
		return moneyInPouch;
	}

	public int getMoneyInPouchAsInt() {
		return moneyInPouch > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)moneyInPouch;
	}

	public boolean experienceLocked() {
		return experienceLocked;
	}

	public void setExperienceLocked(boolean experienceLocked) {
		this.experienceLocked = experienceLocked;
	}

	public void setClientExitTaskActive(boolean clientExitTaskActive) {
		this.clientExitTaskActive = clientExitTaskActive;
	}

	public boolean isClientExitTaskActive() {
		return clientExitTaskActive;
	}

	public Player setCurrentClanChat(ClanChat clanChat) {
		this.currentClanChat = clanChat;
		return this;
	}

	public ClanChat getCurrentClanChat() {
		return currentClanChat;
	}

	public String getClanChatName() {
		return clanChatName;
	}

	public String getYellHex() {
		return yellHex;
	}

	public Player setYellHex(String yellHex) {
		this.yellHex = yellHex;
		return this;
	}

	public Player setClanChatName(String clanChatName) {
		this.clanChatName = clanChatName;
		return this;
	}

	public void setInputHandling(Input inputHandling) {
		this.inputHandling = inputHandling;
	}

	public Input getInputHandling() {
		return inputHandling;
	}

	public boolean isDrainingPrayer() {
		return drainingPrayer;
	}

	public void setDrainingPrayer(boolean drainingPrayer) {
		this.drainingPrayer = drainingPrayer;
	}

	public Stopwatch getClickDelay() {
		return clickDelay;
	}

	public int[] getLeechedBonuses() {
		return leechedBonuses;
	}

	public Stopwatch getLastItemPickup() {
		return lastItemPickup;
	}

	public Stopwatch getLastSummon() {
		return lastSummon;
	}

	public BankSearchAttributes getBankSearchingAttribtues() {
		return bankSearchAttributes;
	}

	public AchievementAttributes getAchievementAttributes() {
		return achievementAttributes;
	}

	public BankPinAttributes getBankPinAttributes() {
		return bankPinAttributes;
	}

	public int getCurrentBankTab() {
		return currentBankTab;
	}

	public Player setCurrentBankTab(int tab) {
		this.currentBankTab = tab;
		return this;
	}

	public boolean isBanking() {
		return isBanking;
	}

	public Player setBanking(boolean isBanking) {
		this.isBanking = isBanking;
		return this;
	}

	public void setNoteWithdrawal(boolean noteWithdrawal) {
		this.noteWithdrawal = noteWithdrawal;
	}

	public boolean withdrawAsNote() {
		return noteWithdrawal;
	}

	public void setSwapMode(boolean swapMode) {
		this.swapMode = swapMode;
	}

	public boolean swapMode() {
		return swapMode;
	}
	public boolean isShopping() {
		return shopping;
	}

	public void setShopping(boolean shopping) {
		this.shopping = shopping;
	}

	public Shop getShop() {
		return shop;
	}

	public Player setShop(Shop shop) {
		this.shop = shop;
		return this;
	}

	public GameObject getInteractingObject() {
		return interactingObject;
	}

	public Player setInteractingObject(GameObject interactingObject) {
		this.interactingObject = interactingObject;
		return this;
	}

	public Item getInteractingItem() {
		return interactingItem;
	}

	public void setInteractingItem(Item interactingItem) {
		this.interactingItem = interactingItem;
	}

	public Dialogue getDialogue() {
		return this.dialogue;
	}

	public void setDialogue(Dialogue dialogue) {
		this.dialogue = dialogue;
	}

	public int getDialogueActionId() {
		return dialogueActionId;
	}

	public void setDialogueActionId(int dialogueActionId) {
		this.dialogueActionId = dialogueActionId;
	}

	public void setSettingUpCannon(boolean settingUpCannon) {
		this.settingUpCannon = settingUpCannon;
	}

	public boolean isSettingUpCannon() {
		return settingUpCannon;
	}

	public Player setCannon(DwarfCannon cannon) {
		this.cannon = cannon;
		return this;
	}

	public DwarfCannon getCannon() {
		return cannon;
	}

	public int getOverloadPotionTimer() {
		return overloadPotionTimer;
	}

	public void setOverloadPotionTimer(int overloadPotionTimer) {
		this.overloadPotionTimer = overloadPotionTimer;
	}

	public int getPrayerRenewalPotionTimer() {
		return prayerRenewalPotionTimer;
	}

	public void setPrayerRenewalPotionTimer(int prayerRenewalPotionTimer) {
		this.prayerRenewalPotionTimer = prayerRenewalPotionTimer;
	}

	public Stopwatch getSpecialRestoreTimer() {
		return specialRestoreTimer;
	}

	public boolean[] getUnlockedLoyaltyTitles() {
		return unlockedLoyaltyTitles;
	}

	public void setUnlockedLoyaltyTitles(boolean[] unlockedLoyaltyTitles) {
		this.unlockedLoyaltyTitles = unlockedLoyaltyTitles;
	}

	public void setUnlockedLoyaltyTitle(int index) {
		unlockedLoyaltyTitles[index] = true;
	}

	public Stopwatch getEmoteDelay() {
		return emoteDelay;
	}

	public MinigameAttributes getMinigameAttributes() {
		return minigameAttributes;
	}

	public int getFireImmunity() {
		return fireImmunity;
	}

	public Player setFireImmunity(int fireImmunity) {
		this.fireImmunity = fireImmunity;
		return this;
	}

	public int getFireDamageModifier() {
		return fireDamageModifier;
	}

	public Player setFireDamageModifier(int fireDamageModifier) {
		this.fireDamageModifier = fireDamageModifier;
		return this;
	}

	public boolean hasVengeance() {
		return hasVengeance;
	}

	public void setHasVengeance(boolean hasVengeance) {
		this.hasVengeance = hasVengeance;
	}

	public Stopwatch getLastVengeance() {
		return lastVengeance;
	}

	public Stopwatch getTolerance() {
		return tolerance;
	}

	public boolean isTargeted() {
		return targeted;
	}

	public void setTargeted(boolean targeted) {
		this.targeted = targeted;
	}

	public Stopwatch getLastYell() {
		return lastYell;
	}

	public Stopwatch getLastVoteClaim() {
		return lastVoteClaim;
	}

	public int getAmountDonated() {
		return amountDonated;
	}

	public void incrementAmountDonated(int amountDonated) {
		this.amountDonated += amountDonated;
	}

	public long getTotalPlayTime() {
		return totalPlayTime;
	}

	public void setTotalPlayTime(long amount) {
		this.totalPlayTime = amount;
	}

	public Stopwatch getRecordedLogin() {
		return recordedLogin;
	}

	public Player setRegionChange(boolean regionChange) {
		this.regionChange = regionChange;
		return this;
	}

	public boolean isChangingRegion() {
		return this.regionChange;
	}

	public void setAllowRegionChangePacket(boolean allowRegionChangePacket) {
		this.allowRegionChangePacket = allowRegionChangePacket;
	}

	public boolean isAllowRegionChangePacket() {
		return allowRegionChangePacket;
	}

	public boolean isKillsTrackerOpen() {
		return killsTrackerOpen;
	}

	public void setKillsTrackerOpen(boolean killsTrackerOpen) {
		this.killsTrackerOpen = killsTrackerOpen;
	}

	public boolean isCoughing() {
		return isCoughing;
	}

	public void setCoughing(boolean isCoughing) {
		this.isCoughing = isCoughing;
	}

	public int getShadowState() {
		return shadowState;
	}

	public void setShadowState(int shadow) {
		this.shadowState = shadow;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public int getGameModeIconId() {
		if (this.getGameMode() == GameMode.IRONMAN) {
			return 840;
		}
		if (this.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
			return 839;
		}
		return -1;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public boolean isPlayerLocked() {
		return playerLocked;
	}

	public Player setPlayerLocked(boolean playerLocked) {
		this.playerLocked = playerLocked;
		return this;
	}

	public Stopwatch getSqlTimer() {
		return sqlTimer;
	}

	public Stopwatch getFoodTimer() {
		return foodTimer;
	}

	public Stopwatch getPotionTimer() {
		return potionTimer;
	}

	public Item getUntradeableDropItem() {
		return untradeableDropItem;
	}

	public void setUntradeableDropItem(Item untradeableDropItem) {
		this.untradeableDropItem = untradeableDropItem;
	}

	public boolean isRecoveringSpecialAttack() {
		return recoveringSpecialAttack;
	}

	public void setRecoveringSpecialAttack(boolean recoveringSpecialAttack) {
		this.recoveringSpecialAttack = recoveringSpecialAttack;
	}

	public CombatType getLastCombatType() {
		return lastCombatType;
	}

	public void setLastCombatType(CombatType lastCombatType) {
		this.lastCombatType = lastCombatType;
	}

	public int getEffigy() {
		return this.effigy;
	}

	public void setEffigy(int effigy) {
		this.effigy = effigy;
	}

	public int getDfsCharges() {
		return dfsCharges;
	}

	public void incrementDfsCharges(int amount) {
		this.dfsCharges += amount;
	}
	
	public void setDfsCharges(int amount) {
		this.dfsCharges = amount;
	}

	public void setNewPlayer(boolean newPlayer) {
		this.newPlayer = newPlayer;
	}

	public boolean newPlayer() {
		return newPlayer;
	}

	public Stopwatch getLogoutTimer() {
		return lougoutTimer;
	}

	public Player setUsableObject(Object[] usableObject) {
		this.usableObject = usableObject;
		return this;
	}

	public Player setUsableObject(int index, Object usableObject) {
		this.usableObject[index] = usableObject;
		return this;
	}

	public Object[] getUsableObject() {
		return usableObject;
	}

	public int getPlayerViewingIndex() {
		return playerViewingIndex;
	}

	public void setPlayerViewingIndex(int playerViewingIndex) {
		this.playerViewingIndex = playerViewingIndex;
	}

	public boolean hasStaffOfLightEffect() {
		return staffOfLightEffect > 0;
	}

	public int getStaffOfLightEffect() {
		return staffOfLightEffect;
	}

	public void setStaffOfLightEffect(int staffOfLightEffect) {
		this.staffOfLightEffect = staffOfLightEffect;
	}

	public void decrementStaffOfLightEffect() {
		this.staffOfLightEffect--;
	}

	public boolean openBank() {
		return openBank;
	}

	public void setOpenBank(boolean openBank) {
		this.openBank = openBank;
	}

	public int getMinutesBonusExp() {
		return minutesBonusExp;
	}

	public void setMinutesBonusExp(int minutesBonusExp, boolean add) {
		this.minutesBonusExp = (add ? this.minutesBonusExp + minutesBonusExp : minutesBonusExp);
	}

	public void setInactive(boolean inActive) {
		this.inActive = inActive;
	}

	public boolean isInActive() {
		return inActive;
	}

	public int getSelectedGeItem() {
		return selectedGeItem;
	}

	public void setSelectedGeItem(int selectedGeItem) {
		this.selectedGeItem = selectedGeItem;
	}

	public int getGeQuantity() {
		return geQuantity;
	}

	public void setGeQuantity(int geQuantity) {
		this.geQuantity = geQuantity;
	}

	public int getGePricePerItem() {
		return gePricePerItem;
	}

	public void setGePricePerItem(int gePricePerItem) {
		this.gePricePerItem = gePricePerItem;
	}

	public GrandExchangeSlot[] getGrandExchangeSlots() {
		return grandExchangeSlots;
	}

	public void setGrandExchangeSlots(GrandExchangeSlot[] GrandExchangeSlots) {
		this.grandExchangeSlots = GrandExchangeSlots;
	}

	public void setGrandExchangeSlot(int index, GrandExchangeSlot state) {
		this.grandExchangeSlots[index] = state;
	}

	public void setSelectedGeSlot(int slot) {
		this.selectedGeSlot = slot;
	}

	public int getSelectedGeSlot() {
		return selectedGeSlot;
	}

	public Task getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(Task currentTask) {
		this.currentTask = currentTask;
	}

	public int getSelectedSkillingItem() {
		return selectedSkillingItem;
	}

	public void setSelectedSkillingItem(int selectedItem) {
		this.selectedSkillingItem = selectedItem;
	}

	public void setSelectedSkillingItemTwo(int selectedItem) {
		this.selectedSkillingItemTwo = selectedItem;
	}

	public int getSelectedSkillingItemTwo() {
		return selectedSkillingItemTwo;
	}

	public boolean shouldProcessFarming() {
		return processFarming;
	}

	public void setProcessFarming(boolean processFarming) {
		this.processFarming = processFarming;
	}

	public Pouch getSelectedPouch() {
		return selectedPouch;
	}

	public void setSelectedPouch(Pouch selectedPouch) {
		this.selectedPouch = selectedPouch;
	}

	public int getCurrentBookPage() {
		return currentBookPage;
	}

	public void setCurrentBookPage(int currentBookPage) {
		this.currentBookPage = currentBookPage;
	}


	public int getStoredRuneEssence() {
		return storedRuneEssence;
	}

	public void setStoredRuneEssence(int storedRuneEssence) {
		this.storedRuneEssence = storedRuneEssence;
	}

	public int getStoredPureEssence() {
		return storedPureEssence;
	}

	public void setStoredPureEssence(int storedPureEssence) {
		this.storedPureEssence = storedPureEssence;
	}

	public int getTrapsLaid() {
		return trapsLaid;
	}

	public void setTrapsLaid(int trapsLaid) {
		this.trapsLaid = trapsLaid;
	}

	public boolean isCrossingObstacle() {
		return crossingObstacle;
	}

	public Player setCrossingObstacle(boolean crossingObstacle) {
		this.crossingObstacle = crossingObstacle;
		return this;
	}

	public boolean[] getCrossedObstacles() {
		return crossedObstacles;
	}

	public boolean[] getHween2016All() {
		return hween2016;
	}

	public int getChristmas2016() {
		return christmas2016;
	}

	public boolean[] getBossPetsAll() {
		return bossPets;
	}

	public boolean getCrossedObstacle(int i) {
		return crossedObstacles[i];
	}

	public boolean getHween2016(int i) {
		return hween2016[i];
	}

	public boolean getBossPet(int i) {
		return bossPets[i];
	}

	public Player setBossPet(int i, boolean completed) {
		bossPets[i] = completed;
		return this;
	}

	public void setEaster2017(int easter2017) {
		this.easter2017 = easter2017;
	}

	public int getEaster2017() {
		return this.easter2017;
	}

	public Player setCrossedObstacle(int i, boolean completed) {
		crossedObstacles[i] = completed;
		return this;
	}

	public Player setHween2016(int i, boolean completed) {
		hween2016[i] = completed;
		return this;
	}

	public void setCrossedObstacles(boolean[] crossedObstacles) {
		this.crossedObstacles = crossedObstacles;
	}

	public void setHween2016All(boolean[] boolAray) {
		this.hween2016 = boolAray;
	}

	public void setBossPetsAll(boolean[] boolAray) {
		this.bossPets = boolAray;
	}

	public int getSkillAnimation() {
		return skillAnimation;
	}

	public Player setSkillAnimation(int animation) {
		this.skillAnimation = animation;
		return this;
	}

	public int[] getOres() {
		return ores;
	}

	public void setOres(int[] ores) {
		this.ores = ores;
	}

	public void setResetPosition(Position resetPosition) {
		this.resetPosition = resetPosition;
	}

	public Position getResetPosition() {
		return resetPosition;
	}

	public Slayer getSlayer() {
		return slayer;
	}

	public Summoning getSummoning() {
		return summoning;
	}

	public Farming getFarming() {
		return farming;
	}

	public boolean inConstructionDungeon() {
		return inConstructionDungeon;
	}

	public void setInConstructionDungeon(boolean inConstructionDungeon) {
		this.inConstructionDungeon = inConstructionDungeon;
	}

	public int getHouseServant() {
		return houseServant;
	}

	public void setHouseServant(int houseServant) {
		this.houseServant = houseServant;
	}

	public int getHouseServantCharges() {
		return this.houseServantCharges;
	}

	public void setHouseServantCharges(int houseServantCharges) {
		this.houseServantCharges = houseServantCharges;
	}

	public void incrementHouseServantCharges() {
		this.houseServantCharges++;
	}

	public int getServantItemFetch() {
		return servantItemFetch;
	}

	public void setServantItemFetch(int servantItemFetch) {
		this.servantItemFetch = servantItemFetch;
	}

	public int getPortalSelected() {
		return portalSelected;
	}

	public void setPortalSelected(int portalSelected) {
		this.portalSelected = portalSelected;
	}

	public boolean isBuildingMode() {
		return this.isBuildingMode;
	}

	public void setIsBuildingMode(boolean isBuildingMode) {
		this.isBuildingMode = isBuildingMode;
	}

	public int[] getConstructionCoords() {
		return constructionCoords;
	}

	public void setConstructionCoords(int[] constructionCoords) {
		this.constructionCoords = constructionCoords;
	}

	public int getBuildFurnitureId() {
		return this.buildFurnitureId;
	}

	public void setBuildFuritureId(int buildFuritureId) {
		this.buildFurnitureId = buildFuritureId;
	}

	public void setchristmas2016(int christmas2016) {
		this.christmas2016 = christmas2016;
	}

	public void setNewYear2017(int newYear2017) {
		this.newYear2017 = newYear2017;
	}

	public void setLastTomed(int lastTomed) {
		this.lastTomed = lastTomed;
	}

	public int getLastTomed() {
		return this.lastTomed;
	}

	public int getNewYear2017() {
		return this.newYear2017;
	}

	public int getBuildFurnitureX() {
		return this.buildFurnitureX;
	}

	public void setBuildFurnitureX(int buildFurnitureX) {
		this.buildFurnitureX = buildFurnitureX;
	}

	public int getBuildFurnitureY() {
		return this.buildFurnitureY;
	}

	public void setBuildFurnitureY(int buildFurnitureY) {
		this.buildFurnitureY = buildFurnitureY;
	}

	public int getCombatRingType() {
		return this.combatRingType;
	}

	public void setCombatRingType(int combatRingType) {
		this.combatRingType = combatRingType;
	}

	public Room[][][] getHouseRooms() {
		return houseRooms;
	}

	public ArrayList<Portal> getHousePortals() {
		return housePortals;
	}

	public ArrayList<HouseFurniture> getHouseFurniture() {
		return houseFurniture;
	}

	public int getConstructionInterface() {
		return this.constructionInterface;
	}

	public void setConstructionInterface(int constructionInterface) {
		this.constructionInterface = constructionInterface;
	}

	public int[] getBrawlerChargers() {
		return this.brawlerCharges;
	}

	public void setBrawlerCharges(int[] brawlerCharges) {
		this.brawlerCharges = brawlerCharges;
	}

	public int[] getAncientArmourCharges() {
		return this.ancientArmourCharges;
	}

	public void setAncientArmourCharges(int[] ancientArmourCharges) {
		this.ancientArmourCharges = ancientArmourCharges;
	}

	public int getRecoilCharges() {
		return this.recoilCharges;
	}

	public int setRecoilCharges(int recoilCharges) {
		return this.recoilCharges = recoilCharges;
	}

	public int getBlowpipeCharges() {
		return this.blowpipeCharges;
	}

	public int setBlowpipeCharges(int blowpipeCharges) {
		return this.blowpipeCharges = blowpipeCharges;
	}

	public boolean voteMessageSent() {
		return this.voteMessageSent;
	}

	public void setVoteMessageSent(boolean voteMessageSent) {
		this.voteMessageSent = voteMessageSent;
	}

	public boolean didReceiveStarter() {
		return receivedStarter;
	}

	public boolean didFriday13May2016() {
		return fri13may16;
	}

	public boolean isFlying() {
		return flying;
	}

	public boolean canFly() {
		return canFly;
	}

	public boolean isGhostWalking() {
		return ghostWalking;
	}

	public boolean canGhostWalk() {
		return canGhostWalk;
	}

	public boolean doneHween2016() {
		return doneHween2016;
	}

	public boolean toggledGlobalMessages() {
		return toggledglobalmessages;
	}

	public boolean isSpiritDebug() {
		return spiritdebug;
	}

	public boolean isInDung() {
		return indung;
	}

	public boolean gotReffered() {
		return reffered;
	}

	public void setReceivedStarter(boolean receivedStarter) {
		this.receivedStarter = receivedStarter;
	}

	public void setFriday13May2016(boolean fri13may16) {
		this.fri13may16 = fri13may16;
	}

	public void setFlying(boolean flying) {
		this.flying = flying;
	}

	public void setCanFly(boolean canFly) {
		this.canFly = canFly;
	}

	public void setCanGhostWalk(boolean canGhostWalk) {
		this.canGhostWalk = canGhostWalk;
	}

	public void setDoneHween2016(boolean isDone) {
		this.doneHween2016 = isDone;
	}

	public void setGhostWalking(boolean ghostWalking) {
		this.ghostWalking = ghostWalking;
	}

	public void setToggledGlobalMessages(boolean toggledglobalmessages){
		this.toggledglobalmessages = toggledglobalmessages;
	}

	public void setSpiritDebug(boolean spiritdebug) {
		this.spiritdebug = spiritdebug;
	}

	public void setInDung(boolean indung) {
		this.indung = indung;
	}

	public void setReffered(boolean reffered) {
		this.reffered = reffered;
	}

	public boolean doingClanBarrows() {
		return doingClanBarrows;
	}

	public void setDoingClanBarrows(boolean doingClanBarrows) {
		this.doingClanBarrows = doingClanBarrows;
	}

	public int getBarrowsKilled() {
		return barrowsKilled;
	}

	public int getClueProgress() {
		return clueProgress;
	}

	public void setBarrowsKilled(int barrowsKilled) {
		this.barrowsKilled = barrowsKilled;
	}

	public void setClueProgress(int clueProgress) {
		this.clueProgress = clueProgress;
	}

	private boolean chargingAttack;
	public boolean isChargingAttack() {
		return chargingAttack;
	}

	public void setHouseRooms(Room[][][] houseRooms) {
		this.houseRooms = houseRooms;
	}

	public void setHousePortals(ArrayList<Portal> housePortals) {
		this.housePortals = housePortals;
	}

	public void setHouseFurniture(ArrayList<HouseFurniture> houseFurniture) {
		this.houseFurniture = houseFurniture;

	}


	public String getStrippedJewelryName() {
		return strippedJewelryName;
	}


	public void setStrippedJewelryName(String strippedJewelryName) {
		this.strippedJewelryName = strippedJewelryName;
	}


	public int getForgingCharges() {
		return forgingCharges;
	}


	public int setForgingCharges(int forgingCharges) {
		return this.forgingCharges = forgingCharges;
	}


	public boolean getBonecrushEffect() {
		return bonecrushEffect;
	}


	public void setBonecrushEffect(boolean bonecrushEffect) {
		this.bonecrushEffect = bonecrushEffect;
	}


	public int[] getPreviousTeleports() {
		return previousTeleports;
	}
	
	public int getPreviousTeleportsIndex(int index) {
		return previousTeleports[index];
	}

	public void setPreviousTeleports(int[] previousTeleports) {
		this.previousTeleports = previousTeleports;
	}
	
	public void setPreviousTeleportsIndex(int index, int previousTeleport) {
		this.previousTeleports[index] = previousTeleport;
	}


	public TeleportInterfaceData getTeleportInterfaceData() {
		return tid;
	}


	public void setTeleportInterfaceData(TeleportInterfaceData tid) {
		this.tid = tid;
	}


	public String getYellTitle() {
		return yellTitle;
	}


	public void setYellTitle(String yellTitle) {
		if (yellTitle.equalsIgnoreCase("null")) {
			this.yellTitle = "";
		}
		this.yellTitle = yellTitle;
	}

	public List<Integer> getLootList() {
		return lootList;
	}

	public void setLootList(List<Integer> lootList) {
		this.lootList = lootList;
	}

	public boolean isClickToTeleport() {
		return clickToTeleport;
	}

	public void setClickToTeleport(boolean clickToTeleport) {
		this.clickToTeleport = clickToTeleport;
	}

	public Stopwatch getLastDfsTimer() {
		return lastDfsTimer;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	
}
