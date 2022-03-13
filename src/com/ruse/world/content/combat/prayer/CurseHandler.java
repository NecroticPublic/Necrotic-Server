package com.ruse.world.content.combat.prayer;

import java.util.HashMap;
import java.util.Map;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Prayerbook;
import com.ruse.model.Skill;
import com.ruse.model.Locations.Location;
import com.ruse.model.container.impl.Equipment;
import com.ruse.util.Misc;
import com.ruse.util.NameUtils;
import com.ruse.world.content.BonusManager;
import com.ruse.world.content.ItemsKeptOnDeath;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.minigames.impl.Dueling;
import com.ruse.world.content.minigames.impl.Dueling.DuelRule;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.player.Player;

public class CurseHandler {

	public static boolean isButton(Player player, int buttonId) {
		if (CurseData.buttons.containsKey(buttonId)) {
			CurseData curse = CurseData.buttons.get(buttonId);
			if (player.getCurseActive()[curse.ordinal()])
				deactivateCurse(player, curse);
			else
				activateCurse(player, curse);
			return true;
		}
		return false;
	}

	public static int getProtectingPrayer(CombatType type) {
		switch (type) {
		case MELEE:
			return DEFLECT_MELEE;
		case MAGIC:
		case DRAGON_FIRE:
			return DEFLECT_MAGIC;
		case RANGED:
			return DEFLECT_MISSILES;
		default:
			throw new IllegalArgumentException("Invalid combat type: " + type);
		}
	}

	public static boolean isActivated(Player player, int prayer) {
		return player.getCurseActive()[prayer];
	}

	public static void handleLeech(Character p, int index, int amount, int maxAmount, boolean decrease) {
		if(!p.isPlayer())
			return;
		Player target = (Player) p;
		if(target.getLeechedBonuses()[index] > maxAmount) {
			if(decrease)
				target.getLeechedBonuses()[index] -= Misc.getRandom(amount);
			else
				target.getLeechedBonuses()[index] -= Misc.getRandom(amount);
			BonusManager.sendCurseBonuses(target);
		}
	}

	public static void activateCurse(Player player, int curseId) {
		if(player.getPrayerbook() == Prayerbook.NORMAL)
			return;
		CurseData data = CurseData.ids.get(curseId);
		if (data != null)
			activateCurse(player, data);
	}

	public static void deactivateCurse(Player player, int curseId) {
		CurseData data = CurseData.ids.get(curseId);
		if (data != null)
			deactivateCurse(player, data);
	}

	public static void activateCurse(Player player, CurseData curse) {
		if(player.getPrayerbook() == Prayerbook.NORMAL)
			return;
		if (player.getCurseActive()[curse.ordinal()])
			return;
		if(Dueling.checkRule(player, DuelRule.NO_PRAYER)) {
			player.getPacketSender().sendMessage("Prayer has been disabled in this duel.");		
			CurseHandler.deactivateAll(player);
			PrayerHandler.deactivateAll(player);
			return;
		}
		if(player.getLocation() == Location.RECIPE_FOR_DISASTER) {
			player.getPacketSender().sendMessage("For some reason, your prayers do not have any effect here.");		
			CurseHandler.deactivateAll(player);
			PrayerHandler.deactivateAll(player);
			return;
		}
		if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) <= 0) {
			player.getPacketSender().sendConfig(curse.configId, 0);
			player.getPacketSender().sendMessage("You do not have enough Prayer points. You can recharge your points at an altar.");
			return;
		}
		if (player.getSkillManager().getMaxLevel(Skill.PRAYER) < (curse.requirement * 10)) {
			player.getPacketSender().sendConfig(curse.configId, 0);
			//DialogueManager.sendOneStringStatement(p, "               " + "You need a @blu@Prayer <col=0>level of " + pd.levelRequirement +" to use @blu@" + pd.getPrayerName());
			player.getPacketSender().sendMessage("You need a Prayer level of at least "+curse.requirement+" to use " + curse.name + ".");
			return;
		}
		if (curse == CurseData.TURMOIL && player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 30) {
			player.getPacketSender().sendConfig(curse.configId, 0);
			player.getPacketSender().sendMessage("You need a Defence level of at least 30 to use Turmoil.");
			return;
		}
		switch (curse) {
		case SAP_WARRIOR:
		case LEECH_ATTACK:
			deactivateCurses(player, ACCURACY_CURSES);
			break;
		case SAP_RANGER:
		case LEECH_RANGED:
			deactivateCurses(player, RANGED_CURSES);
			break;
		case SAP_MAGE:
		case LEECH_MAGIC:
			deactivateCurses(player, MAGIC_CURSES);
			break;
		case SAP_SPIRIT:
			deactivateCurses(player, SPECIAL_ATTACK_CURSES);
			break;
		case LEECH_SPECIAL_ATTACK:
			deactivateCurses(player, SPECIAL_ATTACK_CURSES);
			deactivateCurse(player, CurseData.TURMOIL);
			break;
		case LEECH_DEFENCE:
			deactivateCurses(player, DEFENSE_CURSES);
			break;
		case LEECH_STRENGTH:
			deactivateCurses(player, STRENGTH_CURSES);
			break;
		case DEFLECT_SUMMONING:
			deactivateCurses(player, NON_DEFLECT_OVERHEAD_CURSES);
			break;
		case WRATH:
		case SOUL_SPLIT:
			deactivateCurses(player, OVERHEAD_CURSES);
			deactivateCurse(player, CurseData.DEFLECT_SUMMONING);
			break;
		case DEFLECT_MAGIC:
		case DEFLECT_MISSILES:
		case DEFLECT_MELEE:
			deactivateCurses(player, OVERHEAD_CURSES);
			break;
		case TURMOIL:
			deactivateCurses(player, COMBAT_CURSES);
			deactivateCurse(player, CurseData.LEECH_ENERGY);
			deactivateCurses(player, SPECIAL_ATTACK_CURSES);
			break;
		case BERSERKER:
			break;
		case LEECH_ENERGY:
			deactivateCurse(player, CurseData.TURMOIL);
			break;
		case PROTECT_ITEM:
			break;
		default:
			break;
		}
		/*if (player.isPrayerInjured()) {
			if (curse == CurseData.DEFLECT_MAGIC || curse == CurseData.DEFLECT_MELEE ||
					curse == CurseData.DEFLECT_MISSILES || curse == CurseData.DEFLECT_SUMMONING) {
				player.getPacketSender().sendMessage("You have been injured and cannot use this prayer!");
				player.getPacketSender().sendConfig(curse.configId, 0);
				return;
			}
		}
		SoundEffects.sendSoundEffect(player, SoundEffects.SoundData.ACTIVATE_PRAYER_OR_CURSE, 10, 0);*/
		if (curse.prayerAnimation != null) {
			player.performAnimation(curse.prayerAnimation.animation);
			player.performGraphic(curse.prayerAnimation.graphic);
		}
		player.setCurseActive(curse.ordinal(), true);
		player.getPacketSender().sendConfig(curse.configId, 1);
		int hintId = getHeadHint(player);
		if (hintId != -1)
			player.getAppearance().setHeadHint(hintId);
		if (noActiveCurse(player, curse) && !player.isDrainingPrayer())
			startDrain(player);
		BonusManager.sendCurseBonuses(player);
		if(player.getInterfaceId() == 17100 && curse == CurseData.PROTECT_ITEM)
			ItemsKeptOnDeath.sendInterface(player);
		Sounds.sendSound(player, Sound.ACTIVATE_PRAYER_OR_CURSE);
	}

	public static void deactivateCurse(Player player, CurseData curse) {
		if (!player.getCurseActive()[curse.ordinal()]) {
			return;
		}
		//	SoundEffects.sendSoundEffect(player, SoundEffects.SoundData.DEACTIVATE_PRAYER_OR_CURSE, 10, 0);
		player.getPacketSender().sendConfig(curse.configId, 0);
		player.setCurseActive(curse.ordinal(), false);
		player.getAppearance().setHeadHint(getHeadHint(player));
		BonusManager.sendCurseBonuses(player);
		if(player.getInterfaceId() == 17100 && curse == CurseData.PROTECT_ITEM)
			ItemsKeptOnDeath.sendInterface(player);
		Sounds.sendSound(player, Sound.DEACTIVATE_PRAYER_OR_CURSE);
	}

	public static void deactivateCurses(Player player) {
		for (CurseData curse : CurseData.values()) {
			if (player.getCurseActive()[curse.ordinal()]) {
				deactivateCurse(player, curse);
			}
		}
	}

	public static void deactivateAll(Player player) {
		for (CurseData curse : CurseData.values()) {
			player.getPacketSender().sendConfig(curse.configId, 0);
			player.setCurseActive(curse.ordinal(), false);
			player.getAppearance().setHeadHint(getHeadHint(player));
			BonusManager.sendCurseBonuses(player);
		}
	}

	private static void deactivateCurses(Player player, CurseData[] curses) {
		for (CurseData curse : curses) {
			if (player.getCurseActive()[curse.ordinal()]) {
				deactivateCurse(player, curse);
			}
		}
	}

	private static boolean noActiveCurse(Player player, CurseData exception) {
		for (CurseData data : CurseData.values()) {
			if (player.getCurseActive()[data.ordinal()] && data != exception) {
				return false;
			}
		}
		return true;
	}

	public static int getHeadHint(Player player) {
		boolean[] active = player.getCurseActive();
		if (active[CurseData.DEFLECT_SUMMONING.ordinal()]) {
			if (active[CurseData.DEFLECT_MELEE.ordinal()])
				return 13;
			if (active[CurseData.DEFLECT_MISSILES.ordinal()])
				return 14;
			if (active[CurseData.DEFLECT_MAGIC.ordinal()])
				return 15;
			return 12;
		}
		if (active[CurseData.DEFLECT_MELEE.ordinal()])
			return 9;
		if (active[CurseData.DEFLECT_MAGIC.ordinal()])
			return 10;
		if (active[CurseData.DEFLECT_MISSILES.ordinal()])
			return 11;
		if (active[CurseData.WRATH.ordinal()])
			return 16;
		if (active[CurseData.SOUL_SPLIT.ordinal()])
			return 17;
		return -1;
	}

	/**
	 * Initializes the player's prayer drain once a first prayer
	 * has been selected.
	 * @param player	The player to start prayer drain for.
	 */
	private static void startDrain(final Player player) {
		if (getDrain(player) <= 0 || player.isDrainingPrayer())
			return;
		player.setDrainingPrayer(true);
		TaskManager.submit(new Task(1, player, true) {
			@Override
			public void execute() {
				if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) <= 0) {
					for (CurseData curse : CurseData.values()) {
						if (player.getCurseActive()[curse.ordinal()]) {
							deactivateCurse(player, curse);
						}
					}
					Sounds.sendSound(player, Sound.RUN_OUT_OF_PRAYER_POINTS);
					player.getPacketSender().sendMessage("You have run out of Prayer points!");
					stop();
					return;
				}
				double drain = getDrain(player);
				if(player.getLocation() != Location.WILDERNESS) {
					if(player.getEquipment().get(Equipment.CAPE_SLOT).getId() == 19748) {
						drain = 0;
					}
				}
				if (drain <= 0 && !player.checkItem(Equipment.CAPE_SLOT, 19748)) {
					stop();
					return;
				}
				int total = (int) (player.getSkillManager().getCurrentLevel(Skill.PRAYER) - drain);
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
		double toRemove = 0;
		for (CurseData curse : CurseData.values()) {
			if (player.getCurseActive()[curse.ordinal()]) {
				toRemove += curse.drainRate;
			}
		}
		if (toRemove > 0) {
			toRemove /= (1 + (0.05 * player.getBonusManager().getOtherBonus()[2]));		
		}
		return toRemove;
	}

	private enum CurseData {
		PROTECT_ITEM(50, .1, 32503, 610, new PrayerAnimation(new Animation(12567), new Graphic(2213))),
		SAP_WARRIOR(50, .3, 32505, 611),
		SAP_RANGER(52, .3, 32507, 612),
		SAP_MAGE(54, .3, 32509, 613),
		SAP_SPIRIT(56, .3, 32511, 614),
		BERSERKER(59, .4, 32513, 615, new PrayerAnimation(new Animation(12589), new Graphic(2266))),
		DEFLECT_SUMMONING(62, .5, 32515, 616),
		DEFLECT_MAGIC(65, .5, 32517, 617),
		DEFLECT_MISSILES(68, .5, 32519, 618),
		DEFLECT_MELEE(71, .5, 32521, 619),
		LEECH_ATTACK(74, 1, 32523, 620),
		LEECH_RANGED(76, 1, 32525, 621),
		LEECH_MAGIC(78, 1, 32527, 622),
		LEECH_DEFENCE(80, 1, 32529, 623),
		LEECH_STRENGTH(82, 1, 32531, 624),
		LEECH_ENERGY(84, 1, 32533, 625),
		LEECH_SPECIAL_ATTACK(86, 1, 32535, 626),
		WRATH(89, 1.2, 32537, 627),
		SOUL_SPLIT(92, 1.5, 32539, 628),
		TURMOIL(95, 3, 32541, 629, new PrayerAnimation(new Animation(12565), new Graphic(2226)));

		private CurseData(int requirement, double drainRate, int buttonId, int configId, PrayerAnimation...animations)  {
			this.requirement = requirement;
			this.drainRate = drainRate;
			this.buttonId = buttonId;
			this.configId = configId;
			this.prayerAnimation = animations.length > 0 ? animations[0] : null;
			this.name = NameUtils.capitalizeWords(toString().toLowerCase().replaceAll("_", " "));
		}

		private final int requirement;

		private final double drainRate;

		private final int buttonId;

		private final int configId;

		private final PrayerAnimation prayerAnimation;

		private final String name;

		private static Map<Integer, CurseData> buttons = new HashMap<Integer, CurseData>();

		private static Map<Integer, CurseData> ids = new HashMap<Integer, CurseData>();

		static {
			for (CurseData data : CurseData.values()) {
				buttons.put(data.buttonId, data);
				ids.put(data.ordinal(), data);
			}
		}
	}

	private static final CurseData[] ACCURACY_CURSES = {
		CurseData.SAP_WARRIOR,
		CurseData.LEECH_ATTACK,
		CurseData.TURMOIL
	};

	private static final CurseData[] RANGED_CURSES = {
		CurseData.SAP_RANGER,
		CurseData.LEECH_RANGED,
		CurseData.TURMOIL
	};

	private static final CurseData[] MAGIC_CURSES = {
		CurseData.SAP_MAGE,
		CurseData.LEECH_MAGIC,
		CurseData.TURMOIL
	};

	private static final CurseData[] SPECIAL_ATTACK_CURSES = {
		CurseData.SAP_SPIRIT,
		CurseData.LEECH_SPECIAL_ATTACK
	};

	private static final CurseData[] DEFENSE_CURSES = {
		CurseData.LEECH_DEFENCE,
		CurseData.TURMOIL
	};

	private static final CurseData[] STRENGTH_CURSES = {
		CurseData.LEECH_STRENGTH,
		CurseData.TURMOIL
	};

	public static final CurseData[] OVERHEAD_CURSES = {
		CurseData.DEFLECT_MAGIC,
		CurseData.DEFLECT_MISSILES,
		CurseData.DEFLECT_MELEE,
		CurseData.WRATH,
		CurseData.SOUL_SPLIT
	};

	private static final CurseData[] NON_DEFLECT_OVERHEAD_CURSES = {
		CurseData.WRATH,
		CurseData.SOUL_SPLIT
	};

	private static final CurseData[] COMBAT_CURSES = {
		CurseData.SAP_WARRIOR,
		CurseData.LEECH_ATTACK,
		CurseData.SAP_RANGER,
		CurseData.LEECH_RANGED,
		CurseData.SAP_MAGE,
		CurseData.LEECH_MAGIC,
		CurseData.LEECH_DEFENCE,
		CurseData.LEECH_STRENGTH
	};

	public static final int PROTECT_ITEM = CurseData.PROTECT_ITEM.ordinal(), SAP_WARRIOR = CurseData.SAP_WARRIOR.ordinal(), SAP_RANGER = CurseData.SAP_RANGER.ordinal(), SAP_MAGE = CurseData.SAP_MAGE.ordinal(), SAP_SPIRIT = CurseData.SAP_SPIRIT.ordinal(),
			BERSERKER = CurseData.BERSERKER.ordinal(), DEFLECT_SUMMONING = CurseData.DEFLECT_SUMMONING.ordinal(), DEFLECT_MAGIC = CurseData.DEFLECT_MAGIC.ordinal(), DEFLECT_MISSILES = CurseData.DEFLECT_MISSILES.ordinal(),
			DEFLECT_MELEE = CurseData.DEFLECT_MELEE.ordinal(), LEECH_ATTACK = CurseData.LEECH_ATTACK.ordinal(), LEECH_RANGED = CurseData.LEECH_RANGED.ordinal(), LEECH_MAGIC = CurseData.LEECH_MAGIC.ordinal(),
			LEECH_DEFENCE = CurseData.LEECH_DEFENCE.ordinal(), LEECH_STRENGTH = CurseData.LEECH_STRENGTH.ordinal(), LEECH_ENERGY = CurseData.LEECH_ENERGY.ordinal(), LEECH_SPECIAL_ATTACK = CurseData.LEECH_SPECIAL_ATTACK.ordinal(),
			WRATH = CurseData.WRATH.ordinal(), SOUL_SPLIT = CurseData.SOUL_SPLIT.ordinal(), TURMOIL = CurseData.TURMOIL.ordinal();

	public static CurseData forId(int id) {
		for(CurseData data: CurseData.values()) {
			if(data.ordinal() == id)
				return data;
		}
		return null;
	}

	private static class PrayerAnimation {

		private PrayerAnimation(Animation animation, Graphic graphic) {
			this.animation = animation;
			this.graphic = graphic;
		}

		private final Animation animation;

		private final Graphic graphic;
	}
}
