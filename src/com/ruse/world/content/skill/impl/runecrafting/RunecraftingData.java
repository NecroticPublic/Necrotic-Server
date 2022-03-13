package com.ruse.world.content.skill.impl.runecrafting;

import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

public class RunecraftingData {
	
	public enum TalismanData {
	
		AIR_TALISMAN(1438, 1, new Position(2841, 4828)),
		MIND_TALISMAN(1448, 2, new Position(2793, 4827)),
		WATER_TALISMAN(1444, 5, new Position(3482, 4834)),
		EARTH_TALISMAN(1440, 9, new Position(2655, 4829)),
		FIRE_TALISMAN(1442, 14, new Position(2576, 4846)),
		BODY_TALISMAN(1446, 20, new Position(2522, 4833)),
		COSMIC_TALISMAN(1454, 27, new Position(2163, 4833)),
		CHAOS_TALISMAN(1452, 35, new Position(2282, 4837)),
		ASTRAL_TALISMAN(-1, 40, null),
		NATURE_TALISMAN(1462, 44, new Position(2400, 4834)),
		LAW_TALISMAN(1458, 54, new Position(2464, 4817)),
		DEATH_TALISMAN(1456, 65, new Position(2208, 4829)),
		BLOOD_TALISMAN(1450, 77, new Position(2468, 4888, 1)),
		ARMADYL_TALISMAN(1460, 77, new Position(2465, 4771));
		
		TalismanData(int talismanId, int levelReq, Position location) {
			this.talismanId = talismanId;
			this.levelReq = levelReq;
			this.location = location;
		}
		
		private int talismanId;
		private int levelReq;
		private Position location;
		
		public int getTalismanID() {
			return this.talismanId;
		}
		
		public int getLevelRequirement() {
			return this.levelReq;
		}
		
		public Position getLocation() {
			return this.location.copy();
		}
		
		public static TalismanData forId(int talismanId) {
			for(TalismanData data : TalismanData.values()) {
				if(data.getTalismanID() == talismanId) {
					return data;
				}
			}
			return null;
		}
	}
	
	public enum RuneData {
		  AIR_RUNE(556, 1, 5, 2478, false),
		  MIND_RUNE(558, 2, 6, 2479, false),
		  WATER_RUNE(555, 5, 7, 2480, false),
		  EARTH_RUNE(557, 9, 8, 2481, false),
		  FIRE_RUNE(554, 14, 10, 2482, false),
		  BODY_RUNE(559, 20, 11, 2483, false),
		  COSMIC_RUNE(564, 27, 12, 2484, true),
		  CHAOS_RUNE(562, 35, 13, 2487, true),
		  ASTRAL_RUNE(9075, 40, 14, 17010, true),
		  NATURE_RUNE(561, 44, 15, 2486, true),
		  LAW_RUNE(563, 54, 16, 2485, true),
		  DEATH_RUNE(560, 65, 17, 2488, true),
		  BLOOD_RUNE(565, 75, 24, 30624, true),
		  ARMADYL_RUNE(21083, 77, 30, 47120, true);
		
		RuneData(int rune, int levelReq, int xpReward, int altarObjectID, boolean pureRequired) {
			this.runeID = rune;
			this.levelReq = levelReq;
			this.xpReward = xpReward;
			this.altarObjectID = altarObjectID;
			this.pureRequired = pureRequired;
		}
		
		private int runeID;
		private int levelReq;
		private int xpReward;
		private int altarObjectID;
		private boolean pureRequired;
		
		public int getRuneID() {
			return this.runeID;
		}
		
		public int getLevelRequirement() {
			return this.levelReq;
		}
		
		public int getXP() {
			return this.xpReward;
		}
		
		public int getAltarID() {
			return this.altarObjectID;
		}
		
		public boolean pureRequired() {
			return this.pureRequired;
		}
		
		public String getName() {
			return ItemDefinition.forId(runeID).getName();
		}
		public static RunecraftingData.RuneData forId(int objectId) {
			for(RunecraftingData.RuneData runes : RunecraftingData.RuneData.values()) {
				if(runes.getAltarID() == objectId) {
					return runes;
				}
			}
			return null;
		}
	}
	
	public static int getMakeAmount(RuneData rune, Player player) {
		int amount = 1;
		switch(rune) {
		case AIR_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 11)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 22)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 33)
				amount = 4;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 44)
				amount = 5;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 55)
				amount = 6;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 66)
				amount = 7;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 77)
				amount = 8;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 88)
				amount = 9;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 99)
				amount = 10;
			break;
		case ASTRAL_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 82)
				amount = 2;
			break;
		case BLOOD_RUNE:
			break;
		case BODY_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 46)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 92)
				amount = 3;
			break;
		case CHAOS_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 74)
				amount = 2;
			break;
		case COSMIC_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 59)
				amount = 2;
			break;
		case DEATH_RUNE:
			break;
		case EARTH_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 26)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 52)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 78)
				amount = 4;
			break;
		case FIRE_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 35)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 70)
				amount = 3;
			break;
		case LAW_RUNE:
			break;
		case MIND_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 14)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 28)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 42)
				amount = 4;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 56)
				amount = 5;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 70)
				amount = 6;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 84)
				amount = 7;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 98)
				amount = 8;
			break;
		case NATURE_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 91)
				amount = 2;
			break;
		case WATER_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 19)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 38)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 57)
				amount = 4;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 76)
				amount = 5;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 95)
				amount = 6;
			break;
		default:
			break;
		}
		return amount;
	}
}
