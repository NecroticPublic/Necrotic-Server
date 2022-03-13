package com.ruse.world.content.skill.impl.woodcutting;

import java.util.HashMap;
import java.util.Map;

import com.ruse.model.Skill;
import com.ruse.model.container.impl.Equipment;
import com.ruse.world.entity.impl.player.Player;

public class WoodcuttingData {
	
	public static enum Hatchet {
		BRONZE(1351, 1, 879, 1.0),
		IRON(1349, 1, 877, 1.3),
		STEEL(1353, 6, 875, 1.5),
		BLACK(1361, 6, 873, 1.7),
		MITHRIL(1355, 21, 871, 1.9),
		ADAMANT(1357, 31, 869, 2),
		RUNE(1359, 41, 867, 2.2),
		ADZE(13661, 61, 406, 2.33), //old anim = 10227
		DRAGON(6739, 61, 2846, 2.5),
		SACRED(14140, 61, 406, 2.5);

		private int id, req, anim;
		private double speed;

		private Hatchet(int id, int level, int animation, double speed) {
			this.id = id;
			this.req = level;
			this.anim = animation;
			this.speed = speed;
		}

		public static Map<Integer, Hatchet> hatchets = new HashMap<Integer, Hatchet>();


		public static Hatchet forId(int id) {
			return hatchets.get(id);
		}

		static {
			for(Hatchet hatchet : Hatchet.values()) {
				hatchets.put(hatchet.getId(), hatchet);
			}
		}

		public int getId() {
			return id;
		}

		public int getRequiredLevel() {
			return req;
		}

		public int getAnim() {
			return anim;
		}
		
		public double getSpeed() {
			return speed;
		}
	}

	public static enum Trees {
		NORMAL(1, 25, 1511, new int[] { 1276, 1277, 1278, 1279, 1280, 1282, 1283, 1284, 1285, 1286, 1289, 1290, 1291, 1315, 1316, 1318, 1319, 1330, 1331, 1332, 1365, 1383, 1384, 3033, 3034, 3035, 3036, 3881, 3882, 3883, 5902, 5903, 5904 }, 4, false),
		ACHEY(1, 25, 2862, new int[] { 2023 }, 4, false),
		OAK(15, 38, 1521, new int[] { 1281, 3037 }, 5, true),
		WILLOW(30, 68, 1519, new int[] { 1308, 5551, 5552, 5553 }, 6, true),
		TEAK(35, 85, 6333, new int[] { 9036 }, 7, true),
		DRAMEN(36, 25, 771, new int[] { 1292 }, 7, true),
		MAPLE(45, 100, 1517, new int[] { 1307, 4677, 4674 }, 7, true),
		MAHOGANY(50, 125, 22060, new int[] { 9034 }, 7, true),
		YEW(60, 175, 1515, new int[] { 1309 }, 14, true),
		MAGIC(75, 250, 1513, new int[] { 1306 }, 20, true);

		private int[] objects;
		private int req, xp, log, ticks;
		private boolean multi;

		private Trees(int req, int xp, int log, int[] obj, int ticks, boolean multi) {
			this.req = req;
			this.xp = xp;
			this.log = log;
			this.objects = obj;
			this.ticks = ticks;
			this.multi = multi;
		}
		
		public boolean isMulti() {
			return multi;
		}
		
		public int getTicks() {
			return ticks;
		}

		public int getReward() {
			return log;
		}

		public int getXp() {
			return xp;
		}

		public int getReq() {
			return req;
		}

		private static final Map<Integer, Trees> tree = new HashMap<Integer, Trees>();

		public static Trees forId(int id) {
			return tree.get(id);
		}

		static {
			for (Trees t : Trees.values()) {
				for (int obj : t.objects) {
					tree.put(obj, t);
				}
			}
		}
	}

	public static int getHatchet(Player p) {
		for (Hatchet h : Hatchet.values()) {
			if (p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == h.getId()) {
				return h.getId();
			} else if (p.getInventory().contains(h.getId())) {
				return h.getId();
			}
		}
		return -1;
	}

	public static int getChopTimer(Player player, Hatchet h) {
		int skillReducement = (int) (player.getSkillManager().getMaxLevel(Skill.WOODCUTTING) * 0.05);
		int axeReducement = (int) h.getSpeed();
		return skillReducement + axeReducement;
	}
}
