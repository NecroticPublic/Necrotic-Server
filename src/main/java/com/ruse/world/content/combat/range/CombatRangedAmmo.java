package com.ruse.world.content.combat.range;

import com.ruse.model.container.impl.Equipment;
import com.ruse.world.entity.impl.player.Player;

/**
 * A table of constants that hold data for all ranged ammo.
 * 
 * @author lare96
 */
public class CombatRangedAmmo {

	//TODO: Add poisonous ammo
	public enum RangedWeaponData {

		LONGBOW(new int[] {839}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW}, RangedWeaponType.LONGBOW),
		SHORTBOW(new int[] {841}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW}, RangedWeaponType.SHORTBOW),
		OAK_LONGBOW(new int[] {845}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW}, RangedWeaponType.LONGBOW),
		OAK_SHORTBOW(new int[] {843}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW}, RangedWeaponType.SHORTBOW),
		WILLOW_LONGBOW(new int[] {847}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW}, RangedWeaponType.LONGBOW),
		WILLOW_SHORTBOW(new int[] {849}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW}, RangedWeaponType.SHORTBOW),
		MAPLE_LONGBOW(new int[] {851}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW}, RangedWeaponType.LONGBOW),
		MAPLE_SHORTBOW(new int[] {853}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW}, RangedWeaponType.SHORTBOW),
		YEW_LONGBOW(new int[] {855}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW, AmmunitionData.ICE_ARROW}, RangedWeaponType.LONGBOW),
		YEW_SHORTBOW(new int[] {857}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW, AmmunitionData.ICE_ARROW}, RangedWeaponType.SHORTBOW),
		MAGIC_LONGBOW(new int[] {859}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW, AmmunitionData.ICE_ARROW, AmmunitionData.BROAD_ARROW}, RangedWeaponType.LONGBOW),
		MAGIC_SHORTBOW(new int[] {861, 6724}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW, AmmunitionData.ICE_ARROW, AmmunitionData.BROAD_ARROW}, RangedWeaponType.SHORTBOW),
		GODBOW(new int[] {19143, 19149, 19146}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW, AmmunitionData.BROAD_ARROW, AmmunitionData.DRAGON_ARROW}, RangedWeaponType.SHORTBOW),
		ZARYTE_BOW(new int[] {20171}, new AmmunitionData[] {AmmunitionData.ZARYTE_AMMO}, RangedWeaponType.SHORTBOW),
		BSOAT(new int[] {6818}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW, AmmunitionData.BROAD_ARROW, AmmunitionData.DRAGON_ARROW, AmmunitionData.ZULRAH_SCALES, AmmunitionData.HAND_CANNON_SHOT}, RangedWeaponType.BSOAT),
		ARMADYL_CROSSBOW(new int[] {22034}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT, AmmunitionData.IRON_BOLT, AmmunitionData.JADE_BOLT, AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT, AmmunitionData.MITHRIL_BOLT, AmmunitionData.TOPAZ_BOLT, AmmunitionData.ADAMANT_BOLT, AmmunitionData.SAPPHIRE_BOLT, AmmunitionData.EMERALD_BOLT, AmmunitionData.RUBY_BOLT, AmmunitionData.RUNITE_BOLT, AmmunitionData.BROAD_BOLT, AmmunitionData.DIAMOND_BOLT, AmmunitionData.ONYX_BOLT, AmmunitionData.DRAGON_BOLT}, RangedWeaponType.ARMADYLXBOW),
		
		GINREI_KOJAKU(new int[]{22010}, new AmmunitionData[] {AmmunitionData.ZARYTE_AMMO}, RangedWeaponType.BSOAT),

		DARK_BOW(new int[] {11235, 13405, 15701, 15702, 15703, 15704}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW, AmmunitionData.DRAGON_ARROW}, RangedWeaponType.DARK_BOW),

		BRONZE_CROSSBOW(new int[] {9174}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT}, RangedWeaponType.CROSSBOW),
		IRON_CROSSBOW(new int[] {9177}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT, AmmunitionData.IRON_BOLT}, RangedWeaponType.CROSSBOW),
		STEEL_CROSSBOW(new int[] {9179}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT, AmmunitionData.IRON_BOLT, AmmunitionData.JADE_BOLT, AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT}, RangedWeaponType.CROSSBOW),
		MITHRIL_CROSSBOW(new int[] {9181}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT, AmmunitionData.IRON_BOLT, AmmunitionData.JADE_BOLT, AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT, AmmunitionData.MITHRIL_BOLT, AmmunitionData.TOPAZ_BOLT}, RangedWeaponType.CROSSBOW),
		ADAMANT_CROSSBOW(new int[] {9183}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT, AmmunitionData.IRON_BOLT, AmmunitionData.JADE_BOLT, AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT, AmmunitionData.MITHRIL_BOLT, AmmunitionData.TOPAZ_BOLT, AmmunitionData.ADAMANT_BOLT, AmmunitionData.SAPPHIRE_BOLT, AmmunitionData.EMERALD_BOLT, AmmunitionData.RUBY_BOLT}, RangedWeaponType.CROSSBOW),
		/* Crossbows who accept all ammo */HIGHEST_CROSSBOWS(new int[] {9185, 18357, 14684}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT, AmmunitionData.IRON_BOLT, AmmunitionData.JADE_BOLT, AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT, AmmunitionData.MITHRIL_BOLT, AmmunitionData.TOPAZ_BOLT, AmmunitionData.ADAMANT_BOLT, AmmunitionData.SAPPHIRE_BOLT, AmmunitionData.EMERALD_BOLT, AmmunitionData.RUBY_BOLT, AmmunitionData.RUNITE_BOLT, AmmunitionData.BROAD_BOLT, AmmunitionData.DIAMOND_BOLT, AmmunitionData.ONYX_BOLT, AmmunitionData.DRAGON_BOLT}, RangedWeaponType.CROSSBOW),

		BRONZE_DART(new int[] {806}, new AmmunitionData[] {AmmunitionData.BRONZE_DART}, RangedWeaponType.THROW),
		IRON_DART(new int[] {807}, new AmmunitionData[] {AmmunitionData.IRON_DART}, RangedWeaponType.THROW),
		STEEL_DART(new int[] {808}, new AmmunitionData[] {AmmunitionData.STEEL_DART}, RangedWeaponType.THROW),
		MITHRIL_DART(new int[] {809}, new AmmunitionData[] {AmmunitionData.MITHRIL_DART}, RangedWeaponType.THROW),
		ADAMANT_DART(new int[] {810}, new AmmunitionData[] {AmmunitionData.ADAMANT_DART}, RangedWeaponType.THROW),
		RUNE_DART(new int[] {811}, new AmmunitionData[] {AmmunitionData.RUNE_DART}, RangedWeaponType.THROW),
		DRAGON_DART(new int[] {11230}, new AmmunitionData[] {AmmunitionData.DRAGON_DART}, RangedWeaponType.THROW),
		DEATH_DART(new int[] {13997}, new AmmunitionData[] {AmmunitionData.DRAGON_DART, AmmunitionData.IRON_DART}, RangedWeaponType.THROW),

		TOXIC_BLOWPIPE(new int[]{12926}, new AmmunitionData[] {AmmunitionData.BRONZE_DART, AmmunitionData.IRON_DART, AmmunitionData.STEEL_DART, AmmunitionData.MITHRIL_DART, AmmunitionData.ADAMANT_DART, AmmunitionData.RUNE_DART, AmmunitionData.DRAGON_DART }, RangedWeaponType.BLOWPIPE),
		
		BRONZE_KNIFE(new int[] {864, 870, 5654}, new AmmunitionData[] {AmmunitionData.BRONZE_KNIFE}, RangedWeaponType.THROW),
		IRON_KNIFE(new int[] {863, 871, 5655}, new AmmunitionData[] {AmmunitionData.IRON_KNIFE}, RangedWeaponType.THROW),
		STEEL_KNIFE(new int[] {865, 872, 5656}, new AmmunitionData[] {AmmunitionData.STEEL_KNIFE}, RangedWeaponType.THROW),
		BLACK_KNIFE(new int[] {869, 874, 5658}, new AmmunitionData[] {AmmunitionData.BLACK_KNIFE}, RangedWeaponType.THROW),
		MITHRIL_KNIFE(new int[] {866, 873, 5657}, new AmmunitionData[] {AmmunitionData.MITHRIL_KNIFE}, RangedWeaponType.THROW),
		ADAMANT_KNIFE(new int[] {867, 875, 5659}, new AmmunitionData[] {AmmunitionData.ADAMANT_KNIFE}, RangedWeaponType.THROW),
		RUNE_KNIFE(new int[] {868, 876, 5660, 5667}, new AmmunitionData[] {AmmunitionData.RUNE_KNIFE}, RangedWeaponType.THROW),

		BRONZE_THROWNAXE(new int[] {800}, new AmmunitionData[] {AmmunitionData.BRONZE_THROWNAXE}, RangedWeaponType.THROW),
		IRON_THROWNAXE(new int[] {801}, new AmmunitionData[] {AmmunitionData.IRON_THROWNAXE}, RangedWeaponType.THROW),
		STEEL_THROWNAXE(new int[] {802}, new AmmunitionData[] {AmmunitionData.STEEL_THROWNAXE}, RangedWeaponType.THROW),
		MITHRIL_THROWNAXE(new int[] {803}, new AmmunitionData[] {AmmunitionData.MITHRIL_THROWNAXE}, RangedWeaponType.THROW),
		ADAMANT_THROWNAXE(new int[] {804}, new AmmunitionData[] {AmmunitionData.ADAMANT_THROWNAXE}, RangedWeaponType.THROW),
		RUNE_THROWNAXE(new int[] {805}, new AmmunitionData[] {AmmunitionData.RUNE_THROWNAXE}, RangedWeaponType.THROW),
		MORRIGANS_THROWNAXE(new int[] {13883, 13957}, new AmmunitionData[] {AmmunitionData.MORRIGANS_THROWNAXE}, RangedWeaponType.THROW),
		
		TOKTZ_XIL_UL(new int[] {6522}, new AmmunitionData[] {AmmunitionData.TOKTZ_XIL_UL}, RangedWeaponType.THROW),
		
		BRONZE_JAVELIN(new int[] {825}, new AmmunitionData[] {AmmunitionData.BRONZE_JAVELIN}, RangedWeaponType.THROW),
		IRON_JAVELIN(new int[] {826}, new AmmunitionData[] {AmmunitionData.IRON_JAVELIN}, RangedWeaponType.THROW),
		STEEL_JAVELIN(new int[] {827}, new AmmunitionData[] {AmmunitionData.STEEL_JAVELIN}, RangedWeaponType.THROW),
		MITHRIL_JAVELIN(new int[] {828}, new AmmunitionData[] {AmmunitionData.MITHRIL_JAVELIN}, RangedWeaponType.THROW),
		ADAMANT_JAVELIN(new int[] {829}, new AmmunitionData[] {AmmunitionData.ADAMANT_JAVELIN}, RangedWeaponType.THROW),
		RUNE_JAVELIN(new int[] {830}, new AmmunitionData[] {AmmunitionData.RUNE_JAVELIN}, RangedWeaponType.THROW),
		MORRIGANS_JAVELIN(new int[] {13879, 13953}, new AmmunitionData[] {AmmunitionData.MORRIGANS_JAVELIN}, RangedWeaponType.THROW),

		CHINCHOMPA(new int[] {10033}, new AmmunitionData[] {AmmunitionData.CHINCHOMPA}, RangedWeaponType.THROW),
		RED_CHINCHOMPA(new int[] {10034}, new AmmunitionData[] {AmmunitionData.RED_CHINCHOMPA}, RangedWeaponType.THROW),

		HAND_CANNON(new int[] {15241}, new AmmunitionData[] {AmmunitionData.HAND_CANNON_SHOT}, RangedWeaponType.HAND_CANNON),

		KARILS_CROSSBOW(new int[]{4734}, new AmmunitionData[] {AmmunitionData.BOLT_RACK}, RangedWeaponType.CROSSBOW);

	//	TOXIC_BLOWPIPE(new int[]{12926}, new AmmunitionData[] {AmmunitionData.ZULRAH_SCALES}, RangedWeaponType.BLOWPIPE);

		RangedWeaponData(int[] weaponIds, AmmunitionData[] ammunitionData, RangedWeaponType type) {
			this.weaponIds = weaponIds;
			this.ammunitionData = ammunitionData;
			this.type = type;
		}

		private int[] weaponIds;
		private AmmunitionData[] ammunitionData;
		private RangedWeaponType type;

		public int[] getWeaponIds() {
			return weaponIds;
		}

		public AmmunitionData[] getAmmunitionData() {
			return ammunitionData;
		}

		public RangedWeaponType getType() {
			return type;
		}

		public static RangedWeaponData getData(Player p) {
			int weapon = p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
			for(RangedWeaponData data : RangedWeaponData.values()) {
				for(int i : data.getWeaponIds()) {
					if(i == weapon)
						return data;
				}
			}
			return null;
		}

		public static AmmunitionData getAmmunitionData(Player p) {
			RangedWeaponData data = p.getRangedWeaponData();
			if(data != null) {
				int ammunition = p.getEquipment().getItems()[data.getType() == RangedWeaponType.THROW ? Equipment.WEAPON_SLOT : Equipment.AMMUNITION_SLOT].getId();
				for(AmmunitionData ammoData : AmmunitionData.values()) {
					for(int i : ammoData.getItemIds()) {
						if(i == ammunition)
							return ammoData;
					}
				}
			}
			return AmmunitionData.BRONZE_ARROW;
		}
	}

	public enum AmmunitionData {
	//AmnitionData(int[] itemIds, int startGfxId, int projectileId, int projectileSpeed, int projectileDelay, int strength, int startHeight, int endHeight) {
		//ENERGY(new int[] {-1}, 250, 249, 1, 33, 400, 43, 31), 
		ZARYTE_AMMO(new int[] {78}, 250, 249, 3, 44, 200, 43, 31), 

		BRONZE_ARROW(new int[] {882}, 19, 10, 3, 44, 1, 43, 31),
		IRON_ARROW(new int[] {884}, 18, 9, 3, 44, 10, 43, 31),
		STEEL_ARROW(new int[] {886}, 20, 11, 3, 44, 16, 43, 31),
		MITHRIL_ARROW(new int[] {888}, 21, 12, 3, 44, 22, 43, 31),
		ADAMANT_ARROW(new int[] {890}, 22, 13, 3, 44, 31, 43, 31),
		RUNE_ARROW(new int[] {892}, 24, 15, 3, 44, 50, 43, 31),
		ICE_ARROW(new int[]{78}, 25, 16, 3, 44, 58, 34, 31),
		BROAD_ARROW(new int[] {4160}, 20, 11, 3, 44, 58, 43, 31),
		DRAGON_ARROW(new int[] {11212}, 1111, 1120, 3, 44, 65, 43, 31),

		BRONZE_BOLT(new int[] {877}, -1, 27, 3, 44, 13, 43, 31),
		OPAL_BOLT(new int [] {879, 9236}, -1, 27, 3, 44, 20, 43, 31),
		IRON_BOLT(new int[] {9140}, -1, 27, 3, 44, 28, 43, 31),
		JADE_BOLT(new int[] {9335, 9237}, -1, 27, 3, 44, 31, 43, 31),
		STEEL_BOLT(new int[] {9141}, -1, 27, 3, 44, 35, 43, 31),
		PEARL_BOLT(new int[] {880, 9238}, -1, 27, 3, 44, 38, 43, 31),
		MITHRIL_BOLT(new int[] {9142}, -1, 27, 3, 44, 40, 43, 31),
		TOPAZ_BOLT(new int[] {9336, 9239}, -1, 27, 3, 44, 50, 43, 31),
		ADAMANT_BOLT(new int[] {9143}, -1, 27, 3, 44, 60, 43, 31),
		SAPPHIRE_BOLT(new int[] {9337, 9240}, -1, 27, 3, 44, 65, 43, 31),
		EMERALD_BOLT(new int[] {9338, 9241}, -1, 27, 3, 44, 70, 43, 31),
		RUBY_BOLT(new int[] {9339, 9242}, -1, 27, 3, 44, 75, 43, 31),
		RUNITE_BOLT(new int[] {9144}, -1, 27, 3, 44, 84, 43, 31),
		BROAD_BOLT(new int[] {13280}, -1, 27, 3, 44, 88, 43, 31),
		DIAMOND_BOLT(new int[] {9340, 9243}, -1, 27, 3, 44, 88, 43, 31),
		ONYX_BOLT(new int[] {9342, 9245}, -1, 27, 3, 44, 90, 43, 31),
		DRAGON_BOLT(new int[] {9341, 9244}, -1, 27, 3, 44, 90, 43, 31),

		
		BRONZE_KNIFE(new int[] {864, 870, 5654}, 219, 212, 4, 33, 8, 45, 37),
		IRON_KNIFE(new int[] {863, 871, 5655}, 220, 213, 4, 33, 12, 45, 37),
		STEEL_KNIFE(new int[] {865, 872, 5656}, 221, 214, 4, 33, 15, 45, 37),
		BLACK_KNIFE(new int[] {869, 874, 5658}, 222, 215, 4, 33, 17, 45, 37),
		MITHRIL_KNIFE(new int[] {866, 873, 5657}, 223, 215, 4, 33, 19, 45, 37),
		ADAMANT_KNIFE(new int[] {867, 875, 5659}, 224, 217, 4, 33, 24, 45, 37),
		RUNE_KNIFE(new int[] {868, 876, 5660, 5667}, 225, 218, 4, 33, 30, 48, 37),
		
		BRONZE_THROWNAXE(new int[] {800}, 43, 36, 3, 44, 7, 43, 31),
		IRON_THROWNAXE(new int[] {801}, 42, 35, 3, 44, 9, 43, 31),
		STEEL_THROWNAXE(new int[] {802}, 44, 37, 3, 44, 11, 43, 31),
		MITHRIL_THROWNAXE(new int[] {803}, 45, 38, 3, 44, 13, 43, 31),
		ADAMANT_THROWNAXE(new int[] {804}, 46, 39, 3, 44, 15, 43, 31),
		RUNE_THROWNAXE(new int[] {805}, 48, 41, 3, 44, 17, 43, 31),
		MORRIGANS_THROWNAXE(new int[] {13883, 13957}, 1856, -1, 3, 44, 100, 43, 31),

		BRONZE_JAVELIN(new int[] {825}, 206, 200, 2, 40, 7, 45, 37),
		IRON_JAVELIN(new int[] {826}, 207, 201, 2, 40, 9, 45, 37),
		STEEL_JAVELIN(new int[] {827}, 208, 202, 2, 40, 11, 45, 37),
		MITHRIL_JAVELIN(new int[] {828}, 209, 203, 2, 40, 13, 45, 37),
		ADAMANT_JAVELIN(new int[] {829}, 210, 204, 2, 40, 15, 45, 37),
		RUNE_JAVELIN(new int[] {830}, 211, 205, 2, 40, 17, 45, 37),
		MORRIGANS_JAVELIN(new int[] {13879, 13953}, 1855, -1, 2, 40, 100, 45, 37),

		TOKTZ_XIL_UL(new int[]{6522}, -1, 442, 2, 40, 58, 51, 37),
		
		CHINCHOMPA(new int[] {10033}, -1, -1, 17, 8, 50, 45, 37),
		RED_CHINCHOMPA(new int[] {10034}, -1, -1, 17, 8, 80, 45, 37),

		HAND_CANNON_SHOT(new int[] {15243}, 2138, 2143, 3, 8, 115, 43, 31),
		BOLT_RACK(new int[] {4740}, -1, 27, 3, 33, 70, 43, 31),
		
		ZULRAH_SCALES(new int[] {12934}, -1, 27, 1, 33, 115, 43, 31),

		BRONZE_DART(new int[] {806}, -1, 226, 4, 33, 2, 20, 28),
		IRON_DART(new int[] {807}, -1, 227, 4, 33, 5, 20, 28),
		STEEL_DART(new int[] {808}, -1, 228, 4, 33, 8, 20, 28),
		MITHRIL_DART(new int[] {809}, -1, 229, 4, 33, 10, 20, 28),
		ADAMANT_DART(new int[] {810}, -1, 230, 4, 33, 15, 20, 28),
		RUNE_DART(new int[] {811}, -1, 231, 4, 33, 20, 20, 28),
		DRAGON_DART(new int[] {11230}, -1, 226, 4, 33, 115, 20, 28);

		;

		AmmunitionData(int[] itemIds, int startGfxId, int projectileId, int projectileSpeed, int projectileDelay, int strength, int startHeight, int endHeight) {
			this.itemIds = itemIds;
			this.startGfxId = startGfxId;
			this.projectileId = projectileId;
			this.projectileSpeed = projectileSpeed;
			this.projectileDelay = projectileDelay;
			this.strength = strength;
			this.startHeight = startHeight;
			this.endHeight = endHeight;		
		}

		private int[] itemIds;
		private int startGfxId;
		private int projectileId;
		private int projectileSpeed;
		private int projectileDelay;
		private int strength;
		private int startHeight;
		private int endHeight;
		
		public int[] getItemIds() {
			return itemIds;
		}

		public boolean hasSpecialEffect() {
			return getItemIds().length >= 2;
		}

		public int getStartGfxId() {
			return startGfxId;
		}

		public int getProjectileId() {
			return projectileId;
		}

		public int getProjectileSpeed() {
			return projectileSpeed;
		}

		public int getProjectileDelay() {
			return projectileDelay;
		}

		public int getStrength() {
			return strength;
		}
		
		public int getStartHeight() {
			return startHeight;
		}
		
		public int getEndHeight() {
			return endHeight;
		}
	}

	public enum RangedWeaponType {

		LONGBOW(5, 5),
		SHORTBOW(5, 4),
		CROSSBOW(5, 5),
		ARMADYLXBOW(5, 5),
		THROW(4, 3),
		DARK_BOW(5, 5),
		HAND_CANNON(5, 4),
		BLOWPIPE(4, 3),
		BSOAT(8, 1);

		RangedWeaponType(int distanceRequired, int attackDelay) {
			this.distanceRequired = distanceRequired;
			this.attackDelay = attackDelay;
		}

		private int distanceRequired;
		private int attackDelay;

		public int getDistanceRequired() {
			return distanceRequired;
		}

		public int getAttackDelay() {
			return attackDelay;
		}
	}
}
