package com.ruse.world.content.skill.impl.fletching;

public enum BowData {
	//Log Id, Unstrung Bow Id, Xp, Level Req, PRODUCTID
	SHORTBOW(1511, 50, 5, 5, 841),
	LONGBOW(1511, 48, 10, 10, 839),

	OAK_SHORTBOW(1521, 54, 17, 20, 843),
	OAK_LONGBOW(1521, 56, 25, 25, 845),

	WILLOW_SHORTBOW(1519, 60, 34, 35, 849),
	WILLOW_LONGBOW(1519, 58, 42, 40, 847),

	MAPLE_SHORTBOW(1517, 64, 50, 50, 853),
	MAPLE_LONGBOW(1517, 62, 59, 55, 851),

	YEW_SHORTBOW(1515, 68, 68, 65, 857),
	YEW_LONGBOW(1515, 66, 75, 70, 855),

	MAGIC_SHORTBOW(1513, 72, 84, 80, 861),
	MAGIC_LONGBOW(1513, 70, 92, 85, 859);

	public int logID, unstrungBow, xp, levelReq, bowId;

	private BowData(int logID, int unstrungBow, int xp, int levelReq, int bowId) {
		this.logID = logID;
		this.unstrungBow = unstrungBow;
		this.xp = xp;
		this.levelReq = levelReq;
		this.bowId = bowId;
	}

	public int getLogID() {
		return logID;
	}

	public int getBowID() {
		return unstrungBow;
	}

	public int getXp() {
		return xp;
	}

	public int getLevelReq() {
		return levelReq;
	}
	
	public int getFullBowId() {
		return bowId;
	}
	

	public static BowData forBow(int id) {
		for (BowData fl : BowData.values()) {
			if (fl.getBowID() == id) {
				return fl;
			}
		}
		return null;
	}
	
	public static BowData forLog(int log) {
		for (BowData fl : BowData.values()) {
			if (fl.getLogID() == log) {
				return fl;
			}
		}
		return null;
	}
	
	public static BowData forLog(int log, boolean shortbow) {
		for (BowData fl : BowData.values()) {
			if (fl.getLogID() == log) {
				if(shortbow && fl.toString().toLowerCase().contains("shortbow") || !shortbow && fl.toString().toLowerCase().contains("longbow")) {
					return fl;
				}
			}
		}
		return null;
	}
	
	public static BowData forId(int id) {
		for (BowData fl : BowData.values()) {
			if (fl.ordinal() == id) {
				return fl;
			}
		}
		return null;
	}
}
