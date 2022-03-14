package com.ruse.world.content.minigames;

import com.ruse.world.content.skill.impl.dungeoneering.DungeoneeringParty;

/**
 * Holds different minigame attributes for a player
 * @author Gabriel Hannason
 */
public class MinigameAttributes {

	private final BarrowsMinigameAttributes barrowsMinigameAttributes = new BarrowsMinigameAttributes();
	private final WarriorsGuildAttributes warriorsGuildAttributes = new WarriorsGuildAttributes();
	private final PestControlAttributes pestControlAttributes = new PestControlAttributes();
	private final RecipeForDisasterAttributes rfdAttributes = new RecipeForDisasterAttributes();
	private final NomadAttributes nomadAttributes = new NomadAttributes();
	//private final SoulWarsAttributes soulWarsAttributes = new SoulWarsAttributes();
	private final GodwarsDungeonAttributes godwarsDungeonAttributes = new GodwarsDungeonAttributes();
	private final GraveyardAttributes graveyardAttributes = new GraveyardAttributes();
	private final DungeoneeringAttributes dungeoneeringAttributes = new DungeoneeringAttributes();
	private final trioAttributes trioAttributes = new trioAttributes();
	private final ZulrahAttributes zulrahAttributes = new ZulrahAttributes();

	public class GraveyardAttributes {

		private int wave;
		private int requiredKills;
		private int level;
		private boolean entered;

		public int getWave() {
			return wave;
		}

		public int getLevel() {
			return level;
		}

		public GraveyardAttributes setWave(int wave) {
			this.wave = wave;
			return this;
		}

		public int incrementAndGetWave() {
			return this.wave++;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public void incrementLevel() {
			this.level++;
		}

		public int getRequiredKills() {
			return requiredKills;
		}

		public int decrementAndGetRequiredKills() {
			return this.requiredKills--;
		}

		public void setRequiredKills(int requiredKills) {
			this.requiredKills = requiredKills;
		}

		public boolean hasEntered() {
			return entered;
		}

		public GraveyardAttributes setEntered(boolean entered) {
			this.entered = entered;
			return this;
		}
	}

	public class PestControlAttributes {

		public PestControlAttributes() {

		}

		private int damageDealt;

		public int getDamageDealt() {
			return damageDealt;
		}

		public void setDamageDealt(int damageDealt) {
			this.damageDealt = damageDealt;
		}

		public void incrementDamageDealt(int damageDealt) {
			this.damageDealt += damageDealt;
		}
	}
	
	public class ZulrahAttributes {
	
		private int redFormDamage;
		
		public int getRedFormDamage() {
			return redFormDamage;
		}
		
		public void setRedFormDamage(int value, boolean add) {
			if (add) {
				redFormDamage += value;
			} else {
				redFormDamage = value;
			}
		}
		
	}

	public class WarriorsGuildAttributes {

		private boolean hasSpawnedArmour;
		private boolean enteredTokenRoom;

		public boolean hasSpawnedArmour() {
			return hasSpawnedArmour;
		}

		public void setSpawnedArmour(boolean hasSpawnedArmour) {
			this.hasSpawnedArmour = hasSpawnedArmour;
		}

		public boolean enteredTokenRoom() {
			return enteredTokenRoom;
		}

		public void setEnteredTokenRoom(boolean enteredTokenRoom) {
			this.enteredTokenRoom = enteredTokenRoom;
		}

	}

	public class trioAttributes {
		private boolean joinedBossRoom;

		public boolean joinedBossRoom() {
			return joinedBossRoom;
		}
		public void setJoinedBossRoom(boolean joinedBossRoom) {
			this.joinedBossRoom = joinedBossRoom;
		}
	}

	public class BarrowsMinigameAttributes {

		private int killcount, randomCoffin, riddleAnswer = -1;

		public int getKillcount() {
			return killcount;
		}

		public void setKillcount(int killcount) {
			this.killcount = killcount;
		}

		public int getRandomCoffin() {
			return randomCoffin;
		}

		public void setRandomCoffin(int randomCoffin) {
			this.randomCoffin = randomCoffin;
		}

		public int getRiddleAnswer() {
			return riddleAnswer;
		}

		public void setRiddleAnswer(int riddleAnswer) {
			this.riddleAnswer = riddleAnswer;
		}

		private int[][] barrowsData = { //NPCID, state
				{ 2030, 0}, // verac
				{ 2029, 0 }, // toarg
				{ 2028, 0 }, // karil
				{ 2027, 0 }, // guthan
				{ 2026, 0 }, // dharok
				{ 2025, 0 } // ahrim
		};

		public int[][] getBarrowsData() {
			return barrowsData;
		}

		public void setBarrowsData(int[][] barrowsData) {
			this.barrowsData = barrowsData;
		}
	}

	public class RecipeForDisasterAttributes {
		private int wavesCompleted;
		private boolean[] questParts = new boolean[9];

		public int getWavesCompleted() {
			return wavesCompleted;
		}

		public void setWavesCompleted(int wavesCompleted) {
			this.wavesCompleted = wavesCompleted;
		}

		public boolean hasFinishedPart(int index) {
			return questParts[index];
		}

		public void setPartFinished(int index, boolean finished) {
			questParts[index] = finished;
		}

		public boolean[] getQuestParts() {
			return questParts;
		}

		public void setQuestParts(boolean[] questParts) {
			this.questParts = questParts;
		}

		public void reset() {
			questParts = new boolean[9];
			wavesCompleted = 0;
		}
	}

	public class NomadAttributes {
		private boolean[] questParts = new boolean[2];

		public boolean hasFinishedPart(int index) {
			return questParts[index];
		}

		public void setPartFinished(int index, boolean finished) {
			questParts[index] = finished;
		}

		public boolean[] getQuestParts() {
			return questParts;
		}

		public void setQuestParts(boolean[] questParts) {
			this.questParts = questParts;
		}

		public void reset() {
			questParts = new boolean[2];
		}
	}

	/*public class SoulWarsAttributes {
		private int activity = 30;
		private int productChosen = -1;
		private int team = -1;

		public int getActivity() {
			return activity;
		}

		public void setActivity(int activity) {
			this.activity = activity;
		}

		public int getProductChosen() {
			return productChosen;
		}

		public void setProductChosen(int prodouctChosen) {
			this.productChosen = prodouctChosen;
		}

		public int getTeam() {
			return team;
		}

		public void setTeam(int team) {
			this.team = team;
		}
	}*/

	public class GodwarsDungeonAttributes {
		private int[] killcount = new int[4]; // 0 = armadyl, 1 = bandos, 2 = saradomin, 3 = zamorak
		private boolean enteredRoom;
		private long altarDelay;

		public int[] getKillcount() {
			return killcount;
		}

		public void setKillcount(int[] killcount) {
			this.killcount = killcount;
		}

		public boolean hasEnteredRoom() {
			return enteredRoom;
		}

		public void setHasEnteredRoom(boolean enteredRoom) {
			this.enteredRoom = enteredRoom;
		}

		public long getAltarDelay() {
			return altarDelay;
		}

		public GodwarsDungeonAttributes setAltarDelay(long altarDelay) {
			this.altarDelay = altarDelay;
			return this;
		}
	}

	public class DungeoneeringAttributes {
		private DungeoneeringParty party;
		private DungeoneeringParty invitation;
		private long lastInvitation;
		private int[] boundItems = new int[5];
		private int damageDealt;
		private int deaths;

		public DungeoneeringParty getParty() {
			return party;
		}

		public void setParty(DungeoneeringParty dungeoneeringParty) {
			this.party = dungeoneeringParty;
		}		

		public int[] getBoundItems() {
			return boundItems;
		}

		public void setBoundItems(int[] boundItems) {
			this.boundItems = boundItems;
		}

		public long getLastInvitation() {
			return lastInvitation;
		}

		public void setLastInvitation(long lastInvitation) {
			this.lastInvitation = lastInvitation;
		}

		public DungeoneeringParty getPartyInvitation() {
			return invitation;
		}

		public void setPartyInvitation(DungeoneeringParty partyInvitation) {
			this.invitation = partyInvitation;
		}

		public void incrementDamageDealt(int damage) {
			this.damageDealt += damage;
		}

		public void setDamageDealt(int damage) {
			this.damageDealt = damage;
		}

		public int getDamageDealt() {
			return this.damageDealt;
		}

		public void setDeaths(int deaths) {
			this.deaths = deaths;
		}

		public void incrementDeaths() {
			this.deaths++;
		}

		public int getDeaths() {
			return deaths;
		}
	}

	public BarrowsMinigameAttributes getBarrowsMinigameAttributes() {
		return barrowsMinigameAttributes;
	}

	public WarriorsGuildAttributes getWarriorsGuildAttributes() {
		return warriorsGuildAttributes;
	}

	public trioAttributes getTrioAttuibutes() {
		return trioAttributes;
	}

	public PestControlAttributes getPestControlAttributes() {
		return pestControlAttributes;
	}

	public RecipeForDisasterAttributes getRecipeForDisasterAttributes() {
		return rfdAttributes;
	}

	public NomadAttributes getNomadAttributes() {
		return nomadAttributes;
	}

	public GraveyardAttributes getGraveyardAttributes() {
		return graveyardAttributes;
	}

	public GodwarsDungeonAttributes getGodwarsDungeonAttributes() {
		return godwarsDungeonAttributes;
	}

	public DungeoneeringAttributes getDungeoneeringAttributes() {
		return dungeoneeringAttributes;
	}
	
	public ZulrahAttributes getZulrahAttributes() {
		return zulrahAttributes;
	}


	public MinigameAttributes() {}

}
