package com.ruse.world.content.skill.impl.magic;

import java.util.HashMap;
import java.util.Map;

import com.ruse.GameSettings;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.GraphicHeight;
import com.ruse.model.Skill;
import com.ruse.world.entity.impl.player.Player;

public class Magic {

	public enum Enchant {

		SAPPHIRERING(1637, 2550, 7, 18, 719, 114, 1, 1),
		SAPPHIREAMULET(1694, 1727, 7, 18, 719, 114, 1, 1),
		SAPPHIRENECKLACE(1656, 3853, 7, 18, 719, 114, 1, 1),
		SAPPHIREBOLTS(9337, 9240, 7, 18, 712, 238, 1, 10),

		EMERALDRING(1639, 2552, 27, 37, 719, 114, 2, 1),
		EMERALDAMULET(1696, 1729, 27, 37, 719, 114, 2, 1),
		EMERALDNECKLACE(1658, 5521, 27, 37, 719, 114, 2, 1),
		EMERALDBOLTS(9338, 9241, 27, 37, 712, 238, 2, 10),

		RUBYRING(1641, 2568, 47, 59, 720, 115, 3, 1),
		RUBYAMULET(1698, 1725, 47, 59, 720, 115, 3, 1),
		RUBYNECKLACE(1660, 11194, 47, 59, 720, 115, 3, 1),
		RUBYBOLTS(9339, 9242, 27, 59, 712, 238, 3, 10),

		DIAMONDRING(1643, 2570, 57, 67, 720, 115, 4, 1),
		DIAMONDAMULET(1700, 1731, 57, 67, 720, 115, 4, 1),
		DIAMONDNECKLACE(1662, 11090, 57, 67, 720, 115, 4, 1),
		DIAMONDBOLTS(9340, 9243, 27, 67, 712, 238, 4, 10),

		DRAGONSTONERING(1645, 22045, 68, 78, 721, 116, 5, 1),
		DRAGONSTONEAMULET(1702, 1712, 68, 78, 721, 116, 5, 1),
		DRAGONSTONENECKLACE(1664, 11113, 68, 78, 721, 116, 5, 1),
		DRAGONBOLTS(9341, 9244, 27, 78, 712, 238, 5, 10),

		ONYXRING(6575, 6583, 87, 97, 721, 452, 6, 1),
		ONYXAMULET(6581, 6585, 87, 97, 721, 452, 6, 1),
		ONYXNECKLACE(6577, 11128, 87, 97, 721, 452, 6, 1),
		ONYXBOLTS(9342, 9245, 27, 97, 712, 238, 6, 10)
		;

		int unenchanted, enchanted, levelReq, xpGiven, anim, gfx, reqEnchantmentLevel, amount;
		private Enchant(int unenchanted, int enchanted, int levelReq, int xpGiven, int anim, int gfx, int reqEnchantmentLevel, int amount) {
			this.unenchanted = unenchanted;
			this.enchanted = enchanted;
			this.levelReq = levelReq;
			this.xpGiven = xpGiven;
			this.anim = anim;
			this.gfx = gfx;
			this.reqEnchantmentLevel = reqEnchantmentLevel;
			this.amount = amount;
		}

		public int getUnenchanted() {
			return unenchanted;
		}

		public int getEnchanted() {
			return enchanted;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getXp() {
			return xpGiven;
		}

		public int getAnim() {
			return anim;
		}

		public int getGFX() {
			return gfx;
		}

		public int getELevel() {
			return reqEnchantmentLevel;
		}

		public int getAmount() { return amount; }

		private static final Map <Integer,Enchant> enc = new HashMap<Integer,Enchant>();

		public static Enchant forId(int itemID) {
			return enc.get(itemID);
		}

		static {
			for (Enchant en : Enchant.values()) {
				enc.put(en.getUnenchanted(), en);
			}
		}
	}

	private enum EnchantSpell {

		SAPPHIRE(1155, 555, 1, 564, 1, -1, 0),
		EMERALD(1165, 556, 3, 564, 1, -1, 0),
		RUBY(1176, 554, 5, 564, 1, -1, 0),
		DIAMOND(1180, 557, 10, 564, 1, -1, 0), 
		DRAGONSTONE(1187, 555, 15, 557, 15, 564, 1),
		ONYX(6003, 557, 20, 554, 20, 564, 1);

		int spell, reqRune1, reqAmtRune1, reqRune2, reqAmtRune2, reqRune3, reqAmtRune3;
		private EnchantSpell(int spell, int reqRune1, int reqAmtRune1, int reqRune2, int reqAmtRune2, int reqRune3, int reqAmtRune3) {
			this.spell = spell;
			this.reqRune1 = reqRune1;
			this.reqAmtRune1 = reqAmtRune1;
			this.reqRune2 = reqRune2;
			this.reqAmtRune2 = reqAmtRune2;
			this.reqRune3 = reqRune3;
			this.reqAmtRune3 = reqAmtRune3;
		}

		public int getSpell() {
			return spell;
		}

		public int getReq1() {
			return reqRune1;
		}

		public int getReqAmt1() {
			return reqAmtRune1;
		}

		public int getReq2() {
			return reqRune2;
		}

		public int getReqAmt2() {
			return reqAmtRune2;
		}

		public int getReq3() {
			return reqRune3;
		}

		public int getReqAmt3() {
			return reqAmtRune3;
		}


		public static final Map<Integer, EnchantSpell> ens = new HashMap<Integer, EnchantSpell>();

		public static EnchantSpell forId(int id) {
			return ens.get(id);
		}

		static {
			for (EnchantSpell en : EnchantSpell.values()) {
				ens.put(en.getSpell(), en);
			}
		}

	}

	private static boolean hasRunes(Player player, int spellID) {
		if (player.getEquipment().contains(17293) || player.getEquipment().contains(15835)) {
			return true;
		}
		EnchantSpell ens = EnchantSpell.forId(spellID);
		if (ens.getReq3() == 0) {
			return player.getInventory().contains(ens.getReq1()) && player.getInventory().getAmount(ens.getReq1()) >= ens.getReqAmt1() && player.getInventory().contains(ens.getReq2()) && player.getInventory().getAmount(ens.getReq2()) >= ens.getReqAmt2() && player.getInventory().contains(ens.getReq3()) && player.getInventory().getAmount(ens.getReq3()) >= ens.getReqAmt3();
		} else {
			return player.getInventory().contains(ens.getReq1()) && player.getInventory().getAmount(ens.getReq1()) >= ens.getReqAmt1() && player.getInventory().contains(ens.getReq2()) && player.getInventory().getAmount(ens.getReq2()) >= ens.getReqAmt2();
		}
	}

	private static int getEnchantmentLevel(int spellID) {
		switch (spellID) {
		case 1155: //Lvl-1 enchant sapphire
			return 1;
		case 1165: //Lvl-2 enchant emerald
			return 2;
		case 1176: //Lvl-3 enchant ruby
			return 3;
		case 1180: //Lvl-4 enchant diamond
			return 4;
		case 1187: //Lvl-5 enchant dragonstone
			return 5;
		case 6003: //Lvl-6 enchant onyx
			return 6;
		}
		return 0;
	}
	public static void enchantItem(Player player, int itemID, int spellID) {
		Enchant enc = Enchant.forId(itemID);
		EnchantSpell ens = EnchantSpell.forId(spellID);
		if (enc == null || ens == null) {
			return;
		}
		if (player.getSkillManager().getCurrentLevel(Skill.MAGIC) >= enc.getLevelReq()) {
			if (player.getInventory().contains(enc.getUnenchanted())) {
				int toMake = enc.getAmount();
				int materials = player.getInventory().getAmount(enc.getUnenchanted());
				
				if (materials < toMake) {
					toMake = materials;
				}

				if (hasRunes(player, spellID)) {
					if (getEnchantmentLevel(spellID) == enc.getELevel()) {
						player.getInventory().delete(enc.getUnenchanted(), toMake);
						player.getInventory().add(enc.getEnchanted(), toMake);
						player.getSkillManager().addExperience(Skill.MAGIC, enc.getXp());
						player.getInventory().delete(ens.getReq1(), ens.getReqAmt1());
						player.getInventory().delete(ens.getReq2(), ens.getReqAmt2());
						player.performAnimation(new Animation(enc.getAnim()));
						player.performGraphic(new Graphic(enc.getGFX(), GraphicHeight.HIGH));
						if (ens.getReq3() != -1) {
							player.getInventory().delete(ens.getReq3(), ens.getReqAmt3());
						}
						player.getPacketSender().sendTab(GameSettings.MAGIC_TAB);
					} else {
						player.getPacketSender().sendMessage("You can only enchant this jewelry using a level-"+enc.getELevel()+" enchantment spell!");
					}
				} else {
					player.getPacketSender().sendMessage("You do not have enough runes to cast this spell.");
				}
			}
		} else {
			player.getPacketSender().sendMessage("You need a Magic level of at least "+enc.getLevelReq()+" to cast this spell.");	
		}
	}

}
