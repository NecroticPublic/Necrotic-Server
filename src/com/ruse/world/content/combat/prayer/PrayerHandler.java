package com.ruse.world.content.combat.prayer;

import java.util.HashMap;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Prayerbook;
import com.ruse.model.Skill;
import com.ruse.model.Locations.Location;
import com.ruse.model.container.impl.Equipment;
import com.ruse.util.NameUtils;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.minigames.impl.Dueling;
import com.ruse.world.content.minigames.impl.Dueling.DuelRule;
import com.ruse.world.entity.impl.player.Player;


/**
 * All of the prayers that can be activated and deactivated. This currently only
 * has support for prayers present in the <b>317 protocol</b>.
 * 
 * @author Gabriel
 */
public class PrayerHandler {

	/**
	 * Represents a prayer's configurations, such as their
	 * level requirement, buttonId, configId and drain rate.
	 * 
	 * @author relex lawl
	 */
	private enum PrayerData {
		THICK_SKIN(1, 1, 25000, 83),
		BURST_OF_STRENGTH(4, 1, 25002, 84),
		CLARITY_OF_THOUGHT(7, 1, 25004, 85),
		SHARP_EYE(8, 1, 25006, 601),
		MYSTIC_WILL(9, 1, 25008, 602),
		ROCK_SKIN(10, 2, 25010, 86),
		SUPERHUMAN_STRENGTH(13, 2, 25012, 87),
		IMPROVED_REFLEXES(16, 2, 25014, 88),
		RAPID_RESTORE(19, .4, 25016, 89),
		RAPID_HEAL(22, .6, 25018, 90),
		PROTECT_ITEM(25, .6, 25020, 91),
		HAWK_EYE(26, 1.5, 25022, 603),
		MYSTIC_LORE(27, 2, 25024, 604),
		STEEL_SKIN(28, 4, 25026, 92),
		ULTIMATE_STRENGTH(31, 4, 25028, 93),
		INCREDIBLE_REFLEXES(34, 4, 25030, 94),
		PROTECT_FROM_MAGIC(37, 4, 25032, 95, 2),
		PROTECT_FROM_MISSILES(40, 4, 25034, 96, 1),
		PROTECT_FROM_MELEE(43, 4, 25036, 97, 0),
		EAGLE_EYE(44, 4, 25038, 605),
		MYSTIC_MIGHT(45, 4, 25040, 606),
		RETRIBUTION(46, 1, 25042, 98, 4),
		REDEMPTION(49, 2, 25044, 99, 5),
		SMITE(52, 6, 25046, 100, 685, 6),
		CHIVALRY(60, 8, 25048, 607),
		PIETY(70, 10, 25050, 608),
		RIGOUR(80, 11, 25104, 609),
		AUGURY(80, 11, 25108, 610);

		private PrayerData(int requirement, double drainRate, int buttonId, int configId, int... hint) {
			this.requirement = requirement;
			this.drainRate = drainRate;
			this.buttonId = buttonId;
			this.configId = configId;
			if (hint.length > 0)
				this.hint = hint[0];
		}

		/**
		 * The prayer's level requirement for player to be able
		 * to activate it.
		 */
		 private int requirement;

				 /**
				  * The prayer's action button id in prayer tab.
				  */
		 private int buttonId;

		 /**
		  * The prayer's config id to switch their glow on/off by 
		  * sending the sendConfig packet.
		  */
		 private int configId;

		 /**
		  * The prayer's drain rate as which it will drain
		  * the associated player's prayer points.
		  */
		 private double drainRate;

		 /**
		  * The prayer's head icon hint index.
		  */
		 private int hint = -1;

		 /**
		  * The prayer's formatted name.
		  */
		 private String name;

		 /**
		  * Gets the prayer's formatted name.
		  * @return	The prayer's name
		  */
		 private final String getPrayerName() {
			 if (name == null)
				 return NameUtils.capitalizeWords(toString().toLowerCase().replaceAll("_", " "));
			 return name;
		 }

		 /**
		  * Contains the PrayerData with their corresponding prayerId.
		  */
		 private static HashMap <Integer, PrayerData> prayerData = new HashMap <Integer, PrayerData> ();

		 /**
		  * Contains the PrayerData with their corresponding buttonId.
		  */
		 private static HashMap <Integer, PrayerData> actionButton = new HashMap <Integer, PrayerData> ();

		 /**
		  * Populates the prayerId and buttonId maps.
		  */
		 static {
			 for (PrayerData pd : PrayerData.values()) {
				 prayerData.put(pd.ordinal(), pd);
				 actionButton.put(pd.buttonId, pd);
			 }
		 }
	}

	/**
	 * Gets the protecting prayer based on the argued combat type.
	 * 
	 * @param type
	 *            the combat type.
	 * @return the protecting prayer.
	 */
	public static int getProtectingPrayer(CombatType type) {
		switch (type) {
		case MELEE:
			return PROTECT_FROM_MELEE;
		case MAGIC:
		case DRAGON_FIRE:
			return PROTECT_FROM_MAGIC;
		case RANGED:
			return PROTECT_FROM_MISSILES;
		default:
			throw new IllegalArgumentException("Invalid combat type: " + type);
		}
	}

	public static boolean isActivated(Player player, int prayer) {
		return player.getPrayerActive()[prayer];
	}

	/**
	 * Activates a prayer with specified <code>buttonId</code>.
	 * @param player	The player clicking on prayer button.
	 * @param buttonId	The button the player is clicking.
	 */
	public static void togglePrayerWithActionButton(Player player, final int buttonId) {
		for (PrayerData pd : PrayerData.values()) {
			if (buttonId == pd.buttonId) {
				if (!player.getPrayerActive()[pd.ordinal()])
					activatePrayer(player, pd.ordinal());
				else
					deactivatePrayer(player, pd.ordinal());
			}
		}
	}

	/**
	 * Activates said prayer with specified <code>prayerId</code> and de-activates
	 * all non-stackable prayers.
	 * @param player		The player activating prayer.
	 * @param prayerId		The id of the prayer being turned on, also known as the ordinal in the respective enum.
	 */
	public static void activatePrayer(Player player, final int prayerId) {
		if(player.getPrayerbook() == Prayerbook.CURSES)
			return;
		if (player.getPrayerActive()[prayerId])
			return;
		if(Dueling.checkRule(player, DuelRule.NO_PRAYER)) {
			player.getPacketSender().sendMessage("Prayer has been disabled in this duel.");		
			CurseHandler.deactivateAll(player);
			PrayerHandler.deactivateAll(player);
			return;
		}
		if(player.getLocation() == Location.RECIPE_FOR_DISASTER) {
			player.getPacketSender().sendMessage("For some reason, your prayers do not have any effect in here.");		
			CurseHandler.deactivateAll(player);
			PrayerHandler.deactivateAll(player);
			return;
		}
		PrayerData pd = PrayerData.prayerData.get(prayerId);
		if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) <= 0) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You do not have enough Prayer points. You can recharge your points at an altar.");
			return;
		}
		if (player.getSkillManager().getMaxLevel(Skill.PRAYER) < (pd.requirement * 10)) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Prayer level of at least " + pd.requirement + " to use " + pd.getPrayerName() + ".");
			return;
		}
		if (prayerId == CHIVALRY && player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 60) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Defence level of at least 60 to use Chivalry.");
			return;
		}
		if (prayerId == PIETY && player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 70) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Defence level of at least 70 to use Piety.");
			return;
		}
		if (prayerId == RIGOUR && player.getSkillManager().getMaxLevel(Skill.DUNGEONEERING) < 78) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Dungeoneering level of at least 78 to use Rigour.");
			return;
		}
		if (prayerId == AUGURY && player.getSkillManager().getMaxLevel(Skill.DUNGEONEERING) < 78) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Dungeoneering level of at least 78 to use Augury.");
			return;
		}
		switch (prayerId) {
		case THICK_SKIN:
		case ROCK_SKIN:
		case STEEL_SKIN:
			resetPrayers(player, DEFENCE_PRAYERS, prayerId);
			break;
		case BURST_OF_STRENGTH:
		case SUPERHUMAN_STRENGTH:
		case ULTIMATE_STRENGTH:
			resetPrayers(player, STRENGTH_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case CLARITY_OF_THOUGHT:
		case IMPROVED_REFLEXES:
		case INCREDIBLE_REFLEXES:
			resetPrayers(player, ATTACK_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case SHARP_EYE:
		case HAWK_EYE:
		case EAGLE_EYE:
		case MYSTIC_WILL:
		case MYSTIC_LORE:
		case MYSTIC_MIGHT:
			resetPrayers(player, STRENGTH_PRAYERS, prayerId);
			resetPrayers(player, ATTACK_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case CHIVALRY:
		case PIETY:
			resetPrayers(player, DEFENCE_PRAYERS, prayerId);
			resetPrayers(player, STRENGTH_PRAYERS, prayerId);
			resetPrayers(player, ATTACK_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case PROTECT_FROM_MAGIC:
		case PROTECT_FROM_MISSILES:
		case PROTECT_FROM_MELEE:
			resetPrayers(player, OVERHEAD_PRAYERS, prayerId);
			break;
		case RIGOUR:
		case AUGURY:
			resetPrayers(player, DEFENCE_PRAYERS, prayerId);
			resetPrayers(player, STRENGTH_PRAYERS, prayerId);
			resetPrayers(player, ATTACK_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case RETRIBUTION:
		case REDEMPTION:
		case SMITE:
			resetPrayers(player, OVERHEAD_PRAYERS, prayerId);
			break;
		}
		/*if (player.isPrayerInjured()) {
			if (prayerId == PROTECT_FROM_MAGIC ||
					prayerId == PROTECT_FROM_MISSILES || prayerId == PROTECT_FROM_MELEE) {
				player.getPacketSender().sendMessage("You have been injured and cannot use this prayer!");
				player.getPacketSender().sendConfig(pd.configId, 0);
				return;
			}
		}
		SoundEffects.sendSoundEffect(player, SoundEffects.SoundData.ACTIVATE_PRAYER_OR_CURSE, 10, 0);*/
		player.setPrayerActive(prayerId, true);
		player.getPacketSender().sendConfig(pd.configId, 1);
		if (hasNoPrayerOn(player, prayerId) && !player.isDrainingPrayer())
			startDrain(player);
		if (pd.hint != -1) {
			int hintId = getHeadHint(player);
			player.getAppearance().setHeadHint(hintId);
		}
		Sounds.sendSound(player, Sound.ACTIVATE_PRAYER_OR_CURSE);
	}

	/**
	 * Deactivates said prayer with specified <code>prayerId</code>.
	 * @param player		The player deactivating prayer.
	 * @param prayerId		The id of the prayer being deactivated.
	 */
	public static void deactivatePrayer(Player player, int prayerId) {
		if (!player.getPrayerActive()[prayerId])
			return;
		PrayerData pd = PrayerData.prayerData.get(prayerId);
		player.getPrayerActive()[prayerId] = false;
		player.getPacketSender().sendConfig(pd.configId, 0);
		if (pd.hint != -1) {
			int hintId = getHeadHint(player);
			player.getAppearance().setHeadHint(hintId);
		}
		Sounds.sendSound(player, Sound.DEACTIVATE_PRAYER_OR_CURSE);
	}

	/**
	 * Deactivates every prayer in the player's prayer book.
	 * @param player	The player to deactivate prayers for.
	 */
	public static void deactivatePrayers(Player player) {
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i]) {
				deactivatePrayer(player, i);
			}
		}
	}

	public static void deactivateAll(Player player) {
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			PrayerData pd = PrayerData.prayerData.get(i);
			if(pd == null)
				continue;
			player.getPrayerActive()[i] = false;
			player.getPacketSender().sendConfig(pd.configId, 0);
			if (pd.hint != -1) {
				int hintId = getHeadHint(player);
				player.getAppearance().setHeadHint(hintId);
			}
		}
	}

	/**
	 * Gets the player's current head hint if they activate or deactivate
	 * a head prayer.
	 * @param player	The player to fetch head hint index for.
	 * @return			The player's current head hint index.
	 */
	private static int getHeadHint(Player player) {
		boolean[] prayers = player.getPrayerActive();
		if (prayers[PROTECT_FROM_MELEE])
			return 0;
		if (prayers[PROTECT_FROM_MISSILES])
			return 1;
		if (prayers[PROTECT_FROM_MAGIC])
			return 2;
		if (prayers[RETRIBUTION])
			return 3;
		if (prayers[SMITE])
			return 4;
		if (prayers[REDEMPTION])
			return 5;
		return -1;
	}

	/**
	 * Initializes the player's prayer drain once a first prayer
	 * has been selected.
	 * @param player	The player to start prayer drain for.
	 */
	private static void startDrain(final Player player) {
		if (getDrain(player) <= 0 && !player.isDrainingPrayer())
			return;
		player.setDrainingPrayer(true);
		TaskManager.submit(new Task(1, player, true) {
			@Override
			public void execute() {
				if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) <= 0) {
					for (int i = 0; i < player.getPrayerActive().length; i++) {
						if (player.getPrayerActive()[i])
							deactivatePrayer(player, i);
					}
					Sounds.sendSound(player, Sound.RUN_OUT_OF_PRAYER_POINTS);
					player.getPacketSender().sendMessage("You have run out of Prayer points!");
					this.stop();
					return;
				}
				double drainAmount = getDrain(player);
				if(player.getLocation() != Location.WILDERNESS) {
					if(player.getEquipment().get(Equipment.CAPE_SLOT).getId() == 19748) {
						drainAmount = 0;
					}
				}
				
				if (drainAmount <= 0 && !player.checkItem(Equipment.CAPE_SLOT, 19748)) {
					this.stop();
					return;
				}
				int total = (int) (player.getSkillManager().getCurrentLevel(Skill.PRAYER) - drainAmount);
				player.getSkillManager().setCurrentLevel(Skill.PRAYER, total, true);
			}
			@Override
			public void stop() {
				setEventRunning(false);
				player.setDrainingPrayer(false);
			}
		});
	}

	/**
	 * Gets the amount of prayer to drain for <code>player</code>.
	 * @param player	The player to get drain amount for.
	 * @return			The amount of prayer that will be drained from the player.
	 */
	private static final double getDrain(Player player) {
		double toRemove = 0.0;
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i]) {
				PrayerData prayerData = PrayerData.prayerData.get(i);
				toRemove += prayerData.drainRate / 10;
			}
		}
		if (toRemove > 0) {
			toRemove /= (1 + (0.05 * player.getBonusManager().getOtherBonus()[2]));		
		}
		return toRemove;
	}

	/**
	 * Checks if a player has no prayer on.
	 * @param player		The player to check prayer status for.
	 * @param exceptionId	The prayer id currently being turned on/activated.
	 * @return				if <code>true</code>, it means player has no prayer on besides <code>exceptionId</code>.
	 */
	private final static boolean hasNoPrayerOn(Player player, int exceptionId) {
		int prayersOn = 0;
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i] && i != exceptionId)
				prayersOn++;
		}
		return prayersOn == 0;
	}

	/**
	 * Resets <code> prayers </code> with an exception for <code> prayerID </code>
	 * 
	 * @param prayers	The array of prayers to reset
	 * @param prayerID	The prayer ID to not turn off (exception)
	 */
	public static void resetPrayers(Player player, int[] prayers, int prayerID) {
		for (int i = 0; i < prayers.length; i++) {
			if (prayers[i] != prayerID)
				deactivatePrayer(player, prayers[i]);
		}
	}

	/**
	 * Checks if action button ID is a prayer button.
	 * 
	 * @param buttonId
	 * 						action button being hit.
	 */
	public static final boolean isButton(final int actionButtonID) {
		return PrayerData.actionButton.containsKey(actionButtonID);
	}

	public static final int THICK_SKIN = 0, BURST_OF_STRENGTH = 1, CLARITY_OF_THOUGHT = 2, SHARP_EYE = 3, MYSTIC_WILL = 4,
			ROCK_SKIN = 5, SUPERHUMAN_STRENGTH = 6, IMPROVED_REFLEXES = 7, RAPID_RESTORE = 8, RAPID_HEAL = 9, 
			PROTECT_ITEM = 10, HAWK_EYE = 11, MYSTIC_LORE = 12, STEEL_SKIN = 13, ULTIMATE_STRENGTH = 14,
			INCREDIBLE_REFLEXES = 15, PROTECT_FROM_MAGIC = 16, PROTECT_FROM_MISSILES = 17,
			PROTECT_FROM_MELEE = 18, EAGLE_EYE = 19, MYSTIC_MIGHT = 20, RETRIBUTION = 21, REDEMPTION = 22, SMITE = 23, CHIVALRY = 24,
			PIETY = 25, RIGOUR = 26, AUGURY = 27;

	/**
	 * Contains every prayer that counts as a defense prayer.
	 */
	private static final int[] DEFENCE_PRAYERS = {THICK_SKIN, ROCK_SKIN, STEEL_SKIN, CHIVALRY, PIETY, RIGOUR, AUGURY};

	/**
	 * Contains every prayer that counts as a strength prayer.
	 */
	private static final int[] STRENGTH_PRAYERS = {BURST_OF_STRENGTH, SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CHIVALRY, PIETY};

	/**
	 * Contains every prayer that counts as an attack prayer.
	 */
	private static final int[] ATTACK_PRAYERS = {CLARITY_OF_THOUGHT, IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY, PIETY};

	/**
	 * Contains every prayer that counts as a ranged prayer.
	 */
	private static final int[] RANGED_PRAYERS = {SHARP_EYE, HAWK_EYE, EAGLE_EYE, RIGOUR};

	/**
	 * Contains every prayer that counts as a magic prayer.
	 */
	private static final int[] MAGIC_PRAYERS = {MYSTIC_WILL, MYSTIC_LORE, MYSTIC_MIGHT, AUGURY};

	/**
	 * Contains every prayer that counts as an overhead prayer, excluding protect from summoning.
	 */
	public static final int[] OVERHEAD_PRAYERS = {PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE, RETRIBUTION, REDEMPTION, SMITE};

}
