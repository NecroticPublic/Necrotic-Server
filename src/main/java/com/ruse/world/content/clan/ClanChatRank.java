package com.ruse.world.content.clan;

public enum ClanChatRank {

	FRIEND(-1),
	RECRUIT(8),
	CORPORAL(7),
	SERGEANT(6),
	LIEUTENANT(5),
	CAPTAIN(4),
	GENERAL(3),
	OWNER(-1),
	STAFF(-1);
	
	ClanChatRank(int actionMenuId) {
		this.actionMenuId = actionMenuId;
	}
	
	private int actionMenuId;
	
	public static ClanChatRank forId(int id) {
	//	System.out.println(id + "LOOK FOR THIS NIGGA@@@@@@@@@@@@@@@@@@@");
		for (ClanChatRank rank : ClanChatRank.values()) {
			if (rank.ordinal() == id) {
				return rank;
			}
		}
		return null;
	}
	
	public static ClanChatRank forMenuId(int id) {
		for (ClanChatRank rank : ClanChatRank.values()) {
			if (rank.actionMenuId == id) {
				return rank;
			}
		}
		return null;
	}
	
	public static ClanChatRank forString(String rank) {
		for (ClanChatRank ranks : ClanChatRank.values()) {
			if (ranks.toString().equalsIgnoreCase(rank)) {
				return ranks;
			}
		}
		return null;
	}
	
	public static ClanChatRank forGuild(String rank) {
		for (ClanChatRank ranks : ClanChatRank.values()) {
			if (ranks.toString().equalsIgnoreCase(rank)) {
				return ranks;
			}
		}
		return null;
	}
	
}
