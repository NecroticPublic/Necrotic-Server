package com.ruse.world.content;

import com.ruse.model.Item;
import com.ruse.model.Prayerbook;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.entity.impl.player.Player;

public class BonusManager {

	public static void update(Player player) {
		double[] bonuses = new double[18];
		for (Item item : player.getEquipment().getItems()) {
			ItemDefinition definition = ItemDefinition.forId(item.getId());
			for (int i = 0; i < definition.getBonus().length; i++) {
				bonuses[i] += definition.getBonus()[i];
			}
		}
		for (int i = 0; i < STRING_ID.length; i++) {
			if (i <= 4) {
				player.getBonusManager().attackBonus[i] = bonuses[i];
			} else if (i <= 13) {
				int index = i - 5;
				player.getBonusManager().defenceBonus[index] = bonuses[i];
				/*if(player.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 11283 && !STRING_ID[i][1].contains("Magic")) {
					if(player.getDfsCharges() > 0) {
						player.getBonusManager().defenceBonus[index] += player.getDfsCharges();
						bonuses[i] += player.getDfsCharges();
					}
				}*/
			} else if (i <= 17) {
				int index = i - 14;
				player.getBonusManager().otherBonus[index] = bonuses[i];
			}
			player.getPacketSender().sendString(Integer.valueOf(STRING_ID[i][0]), STRING_ID[i][1] + ": " + bonuses[i]);
		}
	}

	public double[] getAttackBonus() {
		return attackBonus;
	}

	public double[] getDefenceBonus() {
		return defenceBonus;
	}

	public double[] getOtherBonus() {
		return otherBonus;
	}

	private double[] attackBonus = new double[5];

	private double[] defenceBonus = new double[9];

	private double[] otherBonus = new double[4];

	private static final String[][] STRING_ID = {
		{"1675", "Stab"},
		{"1676", "Slash"},
		{"1677", "Crush"},
		{"1678", "Magic"},
		{"1679", "Range"},

		{"1680", "Stab"},
		{"1681", "Slash"},
		{"1682", "Crush"},
		{"1683", "Magic"},
		{"1684", "Range"},
		{"16522", "Summoning"},
		{"16523", "Absorb Melee"},
		{"16524", "Absorb Magic"},
		{"16525", "Absorb Ranged"},

		{"1686", "Strength"},
		{"16526", "Ranged Strength"},
		{"1687", "Prayer"},
		{"16527", "Magic Damage"}
	};

	public static final int 
	ATTACK_STAB = 0, 
	ATTACK_SLASH = 1,
	ATTACK_CRUSH = 2, 
	ATTACK_MAGIC = 3, 
	ATTACK_RANGE = 4, 

	DEFENCE_STAB = 0, 
	DEFENCE_SLASH = 1, 
	DEFENCE_CRUSH = 2, 
	DEFENCE_MAGIC = 3,
	DEFENCE_RANGE = 4, 
	DEFENCE_SUMMONING = 5,
	ABSORB_MELEE = 6, 
	ABSORB_MAGIC = 7, 
	ABSORB_RANGED = 8,

	BONUS_STRENGTH = 0, 
	RANGED_STRENGTH = 1,
	BONUS_PRAYER = 2,
	MAGIC_DAMAGE = 3;

	/** CURSES **/

	public static void sendCurseBonuses(final Player p) {
		if(p.getPrayerbook() == Prayerbook.CURSES) {
			sendAttackBonus(p);
			sendDefenceBonus(p);
			sendStrengthBonus(p);
			sendRangedBonus(p);
			sendMagicBonus(p);
		}
	}

	public static void sendAttackBonus(Player p) {
		double boost = p.getLeechedBonuses()[0];
		int bonus = 0;
		if(p.getCurseActive()[CurseHandler.LEECH_ATTACK]) {
			bonus = 5;
		} else if(p.getCurseActive()[CurseHandler.TURMOIL])
			bonus = 15;
		bonus += boost;
		if(bonus < -25)
			bonus = -25;
		p.getPacketSender().sendString(690, ""+getColor(bonus)+""+bonus+"%");
	}

	public static void sendRangedBonus(Player p) {
		double boost = p.getLeechedBonuses()[4];
		int bonus = 0;
		if(p.getCurseActive()[CurseHandler.LEECH_RANGED])
			bonus = 5;
		bonus += boost;
		if(bonus < -25)
			bonus = -25;
		p.getPacketSender().sendString(693, ""+getColor(bonus)+""+bonus+"%");
	}

	public static void sendMagicBonus(Player p) {
		double boost = p.getLeechedBonuses()[6];
		int bonus = 0;
		if(p.getCurseActive()[CurseHandler.LEECH_MAGIC])
			bonus = 5;
		bonus += boost;
		if(bonus < -25)
			bonus = -25;
		p.getPacketSender().sendString(694, ""+getColor(bonus)+""+bonus+"%");
	}

	public static void sendDefenceBonus(Player p) {
		double boost = p.getLeechedBonuses()[1];
		int bonus = 0;		
		if(p.getCurseActive()[CurseHandler.LEECH_DEFENCE])
			bonus = 5;
		else if(p.getCurseActive()[CurseHandler.TURMOIL])
			bonus = 15;
		bonus += boost;
		if(bonus < -25)
			bonus = -25;
		p.getPacketSender().sendString(692, ""+getColor(bonus)+""+bonus+"%");
	}

	public static void sendStrengthBonus(Player p) {
		double boost = p.getLeechedBonuses()[2];
		int bonus = 0;
		if(p.getCurseActive()[CurseHandler.LEECH_STRENGTH])
			bonus = 5;
		else if(p.getCurseActive()[CurseHandler.TURMOIL])
			bonus = 23;
		bonus += boost;
		if(bonus < -25) 
			bonus = -25;
		p.getPacketSender().sendString(691, ""+getColor(bonus)+""+bonus+"%");
	}

	public static String getColor(int i) {
		if(i > 0)
			return "@gre@+";
		if(i < 0)
			return "@red@";
		return "";
	}
}
