package com.ruse.world.content.skill.impl.firemaking;

import com.ruse.world.entity.impl.player.Player;


public class Logdata {

	public static enum logData {
		
		LOG(1511, 1, 40, 30, 2732),
		LOG_RED(7404, 1, 50, 30, 11404),
		LOG_BLUE(7406, 1, 50, 30, 11406),
		LOG_GREEN(7405, 1, 50, 30, 11405),
		LOG_PURPLE(10329, 1, 50, 30, 20001),
		LOG_WHITE(10328, 1, 50, 30, 20000),
		ACHEY(2862, 1, 40, 30, 2732),
		OAK(1521, 15, 60, 40, 2732),
		WILLOW(1519, 30, 90, 45, 2732),
		TEAK(6333, 35, 105, 45, 2732),
		ARCTIC_PINE(10810, 42, 125, 45, 2732),
		MAPLE(1517, 45, 143, 45, 2732),
		MAHOGANY(22060, 50, 158, 45, 2732),
		EUCALYPTUS(12581, 58, 194, 45, 2732),
		YEW(1515, 60, 203, 50, 2732),
		MAGIC(1513, 75, 304, 50, 2732);
		
		private int logId, level, burnTime, xp, gameObject;
		
		private logData(int logId, int level, int xp, int burnTime, int gameObject) {
			this.logId = logId;
			this.level = level;
			this.xp = xp;
			this.burnTime = burnTime;
			this.gameObject = gameObject;
		}
		
		public int getLogId() {
			return logId;
		}
		
		public int getLevel() {
			return level;
		}
		
		public int getXp() {
			return xp;
		}		
		
		public int getBurnTime() {
			return this.burnTime;
		}
		
		public int getGameObject() {
			return this.gameObject;
		}
	}

	public static logData getLogData(Player p, int log) {
		for (final Logdata.logData l : Logdata.logData.values()) {
			if(log == l.getLogId() || log == -1 && p.getInventory().contains(l.getLogId())) {
				return l;
			}
		}
		return null;
	}

}