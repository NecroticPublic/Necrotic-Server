package com.ruse.world.content.skill.impl.mining;

import com.ruse.model.Skill;
import com.ruse.model.container.impl.Equipment;
import com.ruse.world.entity.impl.player.Player;

public class MiningData {

	public static final int[] RANDOM_GEMS = {1623,1621,1619,1617,1631};

	public static enum Pickaxe {

		Bronze(1265, 1, 625, 1.0), //625
		Iron(1267, 1, 626, 1.05),//626
		Steel(1269, 6, 627, 1.1),//627
		Mithril(1273, 21, 628, 1.2), //628
		Adamant(1271, 31, 629, 1.25), //629
		Rune(1275, 41, 624, 1.3), //624
		Adze(13661, 80, 10226, 1.60),//10226
		Dragon(15259, 61, 12188, 1.65),//12188
		Sacred(14130, 61, 11019, 1.60); //12003

		private int id, req, anim;
		private double speed;
		
		private Pickaxe(int id, int req, int anim, double speed) {
			this.id = id;
			this.req = req;
			this.anim = anim;
			this.speed = speed;
		}

		public int getId() {
			return id;
		}
		public int getReq() {
			return req;
		}

		public int getAnim() {
			return anim;
		}
		
		public double getSpeed() {
			return this.speed;
		}
	}

	public static Pickaxe forPick(int id) {
		for (Pickaxe p : Pickaxe.values()) {
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}

	public static enum Ores {
		Rune_essence(new int[]{24444}, 1, 10, 1436, 2, -1),
		Pure_essence(new int[]{24445}, 17, 25, 7936, 3, -1),
		
		Clay(new int[]{9711, 9712, 9713, 15503, 15504, 15505}, 1, 5, 434, 3, 2),
		Copper(new int[]{3042, 9708, 9709, 9710, 11936, 11960, 11961, 11962, 11189, 11190, 11191, 29231, 29230, 2090}, 1, 18, 436, 4, 4),
		Tin(new int[]{3043, 9714, 9715, 9716, 11933, 11957, 11958, 11959, 11186, 11187, 11188, 2094, 29227, 29229}, 1, 18, 438, 4, 4),
		Iron(new int[]{9717, 9718, 9719, 2093, 2092, 11954, 11955, 11956, 29221, 29222, 29223}, 15, 35, 440, 5, 5),
		Silver(new int[]{2100, 2101, 29226, 29225, 11948, 11949}, 20, 40, 442, 5, 7),
		Coal(new int[]{2097, 5770, 29216, 29215, 29217, 11965, 11964, 11963, 11930, 11931, 11932}, 30, 50, 453, 5, 7),
		Gold(new int[]{9720, 9721, 9722, 11951, 11183, 11184, 11185, 2099}, 40, 65, 444, 5, 10),
		Mithril(new int[]{25370, 25368, 5786, 5784, 11942, 11943, 11944, 11945, 11946, 29236, 11947, 11942, 11943}, 50, 80, 447, 6, 11),
		Adamantite(new int[]{11941, 11939, 29233, 29235}, 70, 95, 449, 7, 14),
		Runite(new int[]{14859, 4860, 2106, 2107}, 85, 125, 451, 7, 45),
		
		CRASHED_STAR(new int[]{38660}, 80, 52, 13727, 7, -1);


		private int objid[];
		private int itemid, req, xp, ticks, respawnTimer;

		private Ores(int[] objid, int req, int xp, int itemid, int ticks, int respawnTimer) {
			this.objid = objid;
			this.req = req;
			this.xp = xp;
			this.itemid = itemid;
			this.ticks = ticks;
			this.respawnTimer = respawnTimer;
		}

		public int getRespawn() {
			return respawnTimer;
		}

		public int getLevelReq(){
			return req;
		}

		public int getXpAmount(){
			return xp;
		}

		public int getItemId(){
			return itemid;
		}

		public int getTicks() {
			return ticks;
		}
	}

	public static Ores forRock(int id) {
		for (Ores ore : Ores.values()) {
			for (int obj : ore.objid) {
				if (obj == id) {
					return ore;
				}
			}
		}
		return null;
	}

	public static int getPickaxe(final Player plr) {
		for (Pickaxe p : Pickaxe.values()) {
			if (plr.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == p.getId())
				return p.getId();
			else if (plr.getInventory().contains(p.getId()))
				return p.getId();
		}
		return -1;
	}
	
	public static boolean isHoldingPickaxe(final Player player) {
		for (Pickaxe p : Pickaxe.values()) {
			if (player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == p.getId()) {
				return true;
			}
		}
		return false;
	}
	
	public static int getReducedTimer(final Player plr, Pickaxe pickaxe) {
		int skillReducement = (int) (plr.getSkillManager().getMaxLevel(Skill.MINING) * 0.03);
		int pickaxeReducement = (int) pickaxe.getSpeed();
		return skillReducement + pickaxeReducement;
	}
}