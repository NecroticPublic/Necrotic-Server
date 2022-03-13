package com.ruse.world.content;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Position;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

public class Sounds {

	public enum Sound {
		ROTATING_CANNON(new int[] {941}),
		FIRING_CANNON(new int[] {341}),
		LEVELUP(new int[] {51}),
		DRINK_POTION(new int[] {334}),
		EAT_FOOD(new int[] {317}),
		EQUIP_ITEM(new int[] {319, 320}),
		DROP_ITEM(new int[] {376}),
		PICKUP_ITEM(new int[] {358, 359}),
		SMITH_ITEM(new int[] {464, 468}),
		SMELT_ITEM(new int[] {352}),
		MINE_ITEM(new int[] {429, 431, 432}),
		FLETCH_ITEM(new int[] {375}),
		WOODCUT(new int[] {471, 472, 473}),
		LIGHT_FIRE(new int[] {811}),
		TELEPORT(new int[] {202, 201}),
		ACTIVATE_PRAYER_OR_CURSE(new int[] {433}),
		DEACTIVATE_PRAYER_OR_CURSE(new int[] {435}),
		RUN_OUT_OF_PRAYER_POINTS(new int[] {438}),
		BURY_BONE(new int[] {380});

		Sound(int[] sounds) {
			this.sounds = sounds;
		}

		private int[] sounds;

		public int[] getSounds() {
			return sounds;
		}

		public int getSound() {
			return sounds[Misc.getRandom(getSounds().length - 1)];
		}
	}

	public static boolean handleButton(Player player, int id) {
		if(id >= 930 && id <= 934) {
			player.setMusicActive(id != 930);
			if(id <= 931) {
				sendSound(player, 319);
			}
			PlayerPanel.refreshPanel(player);
			return true;
		}
		if(id >= 941 && id <= 945) {
			player.setSoundsActive(id != 941);
			sendSound(player, 319);
			PlayerPanel.refreshPanel(player);
			return true;
		}
		return false;
	}

	public static void handleRegionChange(Player player) {
		if(player.musicActive()) {
			int songId = getSongID(getAreaID(player));
			if(songId > 0) {
				player.getPacketSender().sendSong(songId);
			}
		}
	}

	public static void sendSound(Player player, int id) {
		if(player.soundsActive()) {
			player.getPacketSender().sendSound(id, 10, 0);	
		}
	}

	public static void sendSound(Player player, Sound sound) {
		sendSound(player, sound.getSound());
	}

	public static void sendGlobalSound(final Player player, final Sound sound) {
		for(Player p : Misc.getCombinedPlayerList(player)) {
			if(p == null) 
				continue;
			sendSound(p, sound.getSound());
		}
	}

	public static void sendGlobalSoundTask(final Player player, final Sound sound) {
		TaskManager.submit(new Task(1, player, true) {
			@Override
			protected void execute() {
				sendGlobalSound(player, sound);
				stop();
			}
		});
	}

	public static int getNpcAttackSounds(int NPCID)
	{
		String npc = NpcDefinition.forId(NPCID) == null ? "" : NpcDefinition.forId(NPCID).getName().toLowerCase();
		if (npc.contains("bat")) {
			return 1;
		}
		if (npc.contains("cow")) {
			return 4;
		}
		if (npc.contains("imp"))
		{
			return 11;
		}
		if (npc.contains("rat"))
		{
			return 17;
		}
		if (npc.contains("duck"))
		{
			return 26;
		}
		if (npc.contains("wolf") || npc.contains("bear"))
		{
			return 28;
		}
		if (npc.contains("dragon"))
		{
			return 47;
		}
		if (npc.contains("ghost"))
		{
			return 57;
		}
		if (npc.contains("goblin"))
		{
			return 88;
		}
		if (npc.contains("skeleton") || npc.contains("demon") || npc.contains("ogre") || npc.contains("giant") || npc.contains("tz-") || npc.contains("jad"))
		{
			return 48;
		}
		if (npc.contains("zombie"))
		{
			return 1155;
		}
		if (npc.contains("man") || npc.contains("woman") || npc.contains("monk"))
		{
			return 417;
		}
		return Misc.getRandom(6) > 3 ? 398 : 394;
	}


	public static int getNpcBlockSound(int NPCID)
	{
		String npc = NpcDefinition.forId(NPCID) == null ? "" : NpcDefinition.forId(NPCID).getName().toLowerCase();
		if (npc.contains("bat")) {
			return 7;
		}
		if (npc.contains("cow")) {
			return 5;
		}
		if (npc.contains("imp"))
		{
			return 11;
		}
		if (npc.contains("rat"))
		{
			return 16;
		}
		if (npc.contains("duck"))
		{
			return 24;
		}
		if (npc.contains("wolf") || npc.contains("bear"))
		{
			return 34;
		}
		if (npc.contains("dragon"))
		{
			return 45;
		}
		if (npc.contains("ghost"))
		{
			return 53;
		}
		if (npc.contains("goblin"))
		{
			return 87;
		}
		if (npc.contains("skeleton") || npc.contains("demon") || npc.contains("ogre") || npc.contains("giant") || npc.contains("tz-") || npc.contains("jad"))
		{
			return 1154;
		}
		if (npc.contains("zombie"))
		{
			return 1151;
		}
		if (npc.contains("man") && !npc.contains("woman"))
		{
			return 816;
		}
		if (npc.contains("monk"))
		{
			return 816;
		}

		if (!npc.contains("man") && npc.contains("woman"))
		{
			return 818;
		}
		return 791;
	}

	public static int getNpcDeathSounds(int NPCID)
	{
		String npc = NpcDefinition.forId(NPCID) == null ? "" : NpcDefinition.forId(NPCID).getName().toLowerCase();
		if (npc.contains("bat")) {
			return 7;
		}
		if (npc.contains("cow")) {
			return 3;
		}
		if (npc.contains("imp"))
		{
			return 9;
		}
		if (npc.contains("rat"))
		{
			return 15;
		}
		if (npc.contains("duck"))
		{
			return 25;
		}
		if (npc.contains("wolf") || npc.contains("bear"))
		{
			return 35;
		}
		if (npc.contains("dragon"))
		{
			return 44;
		}
		if (npc.contains("ghost"))
		{
			return 60;
		}
		if (npc.contains("goblin"))
		{
			return 125;
		}
		if (npc.contains("skeleton") || npc.contains("demon") || npc.contains("ogre") || npc.contains("giant") || npc.contains("tz-") || npc.contains("jad"))
		{
			return 70;
		}
		if (npc.contains("zombie"))
		{
			return 1140;
		}
		return 70;

	}


	public static int getPlayerBlockSounds(int wepId) {

		int blockSound = 511;

		if (wepId == 2499 ||
				wepId == 2501 ||
				wepId == 2503 ||
				wepId == 4746 ||
				wepId == 4757 ||
				wepId == 10330) {//Dragonhide sound
			blockSound = 24;
		}
		else if (wepId == 10551 ||//Torso
				wepId == 10438) {//3rd age
			blockSound = 32;//Weird sound
		}
		else if (wepId == 10338 ||//3rd age
				wepId == 7399 ||//Enchanted
				wepId == 6107 ||//Ghostly
				wepId == 4091 ||//Mystic
				wepId == 4101 ||//Mystic
				wepId == 4111 ||//Mystic
				wepId == 1035 ||//Zamorak
				wepId == 12971) {//Combat
			blockSound = 14;//Robe sound
		}
		else if (wepId == 1101 ||//Chains
				wepId == 1103||
				wepId == 1105||
				wepId == 1107||
				wepId == 1109||
				wepId == 1111||
				wepId == 1113||
				wepId == 1115|| //Plates
				wepId == 1117||
				wepId == 1119||
				wepId == 1121||
				wepId == 1123||
				wepId == 1125||
				wepId == 1127||
				wepId == 4720|| //Barrows armour
				wepId == 4728||
				wepId == 4749||
				wepId == 4712||
				wepId == 11720||//Godwars armour
				wepId == 11724||
				wepId == 3140||//Dragon
				wepId == 2615||//Fancy
				wepId == 2653||
				wepId == 2661||
				wepId == 2669||
				wepId == 2623||
				wepId == 3841||
				wepId == 1127) {//Metal armour sound
			blockSound = 511;
		}
		return blockSound;
	}

	public static int getPlayerAttackSound(Player c)	{

		String wep = ItemDefinition.forId(c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId()).getName().toLowerCase();
		if(wep.contains("bow")) {
			return 370;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4718) {//Dharok's Greataxe
			return 1320;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4734) {//Karil's Crossbow
			return 1081;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4747) {//Torag's Hammers
			return 1330;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4710) {//Ahrim's Staff
			return 2555;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4755) {//Verac's Flail
			return 1323;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4726) {//Guthan's Warspear
			return 2562;
		}

		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 772
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1379
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1381
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1383
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1385
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1387
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1389
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1391
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1393
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1395
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1397
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1399
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1401
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1403
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1405
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1407
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1409
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9100) { //Staff wack
			return 394;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 839
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 841
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 843
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 845
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 847
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 849
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 851
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 853
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 855
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 857
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 859
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 861
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4734
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 2023 //RuneC'Bow
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4212
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4214
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4934
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9104
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9107) { //Bows/Crossbows
			return 370;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1363
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1365
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1367
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1369
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1371
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1373
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1375
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1377
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1349
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1351
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1353
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1355
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1357
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1359
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1361
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9109) { //BattleAxes/Axes
			return 399;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4718 || c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 7808)
		{ //Dharok GreatAxe
			return 400;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 6609
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1307
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1309
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1311
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1313
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1315
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1317
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1319) { //2h
			return 425;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1321
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1323
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1325
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1327
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1329
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1331
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1333
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4587) { //Scimitars
			return 396;
		}
		if (wep.contains("halberd"))
		{
			return 420;
		}
		if (wep.contains("long"))
		{
			return 396;
		}
		if (wep.contains("knife"))
		{
			return 368;
		}
		if (wep.contains("javelin"))
		{
			return 364;
		}

		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9110) {
			return 401;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4755) {
			return 1059;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4153) {
			return 1079;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9103) {
			return 385;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == -1) { // fists
			return 417;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 2745 || c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 2746 || c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 2747 || c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 2748) { // Godswords
			return 390;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4151) {
			return 1080;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 22008) {
			return 1080;
		} else {
			return 398; //Daggers(this is enything that isn't added)
		}
	}


	public static int specialSounds(int id)
	{
		if (id == 4151) //whip
		{
			return 1081;
		}
		if (id == 5698) //dds
		{
			return 1082;
		}
		if (id == 1434)//Mace
		{
			return 387;
		}
		if (id == 3204) //halberd
		{
			return 420;
		}
		if (id == 4153) //gmaul
		{
			return 1082;
		}
		if (id == 7158) //d2h
		{
			return 426;
		}
		if (id == 4587) //dscim
		{
			return 1305;
		}
		if (id == 1215) //Dragon dag
		{
			return 1082;
		}
		if (id == 1305) //D Long
		{
			return 390;
		}
		if (id == 861) //MSB
		{
			return 386;
		}
		if (id == 11235) //DBow
		{
			return 386;
		}
		if (id == 6739) //D Axe
		{
		}
		if (id == 1377) //DBAxe
		{
			return 389;
		}
		return -1;
	}

	/**
	 * takes area IDs as switch, and returns the song ID for that area
	 * @param area
	 * @return
	 */
	public static int getSongID(int area) {

		switch(area){
		case 1:
			return 62;
		case 2:
			return 318;
		case 3:
			return 381;
		case 4:
			return 380;
		case 5:
			return 96;
		case 6:
			return 99;
		case 7:
			return 98;
		case 8:
			return 3;
		case 9:
			return 587;
		case 10:
			return 50;
		case 11:
			return 76;
		case 12:
			return 72;
		case 13:
			return 473;
		case 14:
			//TODO find pirate music
		case 15:
			return 141;
		case 16:
			//TODO find abbys music
		case 17:
			return 172;
		case 18:
			//TODO find bandit camp music
		case 19:
			return 66;
		case 20:
			//TODO find bedibin camp music lol wut
		case 21:
			//TODO find the song "morooned"
		case 22:
			return 119;
		case 23:
			return 87;
		case 24:
			//TODO find burthrope music
		case 25:
			return 104;
		case 26:
			//TODO find canifis "village" music
		case 27:
			//TODO find maze music (random
		case 28:
			//TODO find cranador music lol
		case 29:
			return 151;
		case 30:
			return 47;
		case 31:
			return 179;
		case 32:
			return 150;
		case 33:
			return 23;
		case 34:
			//TODO find ham hideout music
		case 35:
			return 114;
		case 36:
			return 412;
		case 37:
			//TODO find misc music
		case 38:
			return 286;
		case 39:
			//TODO find port Phasmatys music
		case 40:
			return 35;
		case 41:
			//TODO find long way home song
		case 42:
			return 7;
		case 43:
			return 90;
		case 44:
			return 18;
		case 45:
			return 23;
		case 46:
			return 469;
		case 47:
			return 125;
		case 48:
			return 185;
		case 49:
			return 314;
		case 50:
			return 318;
		case 51:
			return 318;
		case 52:
			return 28;
		case 53:
			//TODO find kalphite queen music
		case 54:
			return 2;
		case 55:
			return 111;
		case 56:
			return 123;
		case 57:
			return 36;
		case 58:
			return 122;
		case 59:
			return 541;
		case 60:
			return 64;
		case 61:
			return 327;
		case 62:
			return 163;
		case 63:
			return 333;
		case 64:
			return 116;
		case 65:
			return 157;
		case 66:
			return 177;
		case 67:
			return 93;
		case 68:
			return 48;
		case 69:
			return 107;
		case 70:
			return 49;
		case 71:
			return 186;
		default:
			return 3;
		}
	}

	/**
	 * Sets area id by integer in MusicHandler
	 * @param c
	 * @return
	 */
	public static int getAreaID(Player player) {
		final Position location = player.getPosition().copy();
		if (location.getX() >= 2625 && location.getX() <= 2687 && location.getY() >= 4670 && location.getY() <= 4735) 
			return 1;
		if ((location.getX() >= 2368 && location.getX() <= 2376 && location.getY() >= 3127 && location.getY() <= 3135 && location.getZ() == 1) || (location.getX() >= 2423 && location.getX() <= 2431 && location.getY() >= 3072 && location.getY() <= 3080 && location.getZ() == 1)) 
			return 2;
		if (location.getX() > 3520 && location.getX() < 3598 && location.getY() > 9653 && location.getY() < 9750) 
			return 3;
		if (location.getX() >= 3542 && location.getX() <= 3583 && location.getY() >= 3265 && location.getY() <= 3322) 
			return 4;
		if(location.getX() > 2941 && location.getX() < 3392 && location.getY() > 3518 && location.getY() < 3966 ||
				location.getX() > 3343 && location.getX() < 3384 && location.getY() > 9619 && location.getY() < 9660 ||
				location.getX() > 2941 && location.getX() < 3392 && location.getY() > 9918 && location.getY() < 10366) 	
			return 5;
		if (location.getX() > 2558 && location.getX() < 2729 && location.getY() > 3263 && location.getY() < 3343) 
			return 6;
		if (location.getX() > 3084 && location.getX() < 3111 && location.getY() > 3483 && location.getY() < 3509) 
			return 7;
		if (location.getX() > 2935 && location.getX() < 3061 && location.getY() > 3308 && location.getY() < 3396) 
			return 8;
		if (location.getX() >= 2659 && location.getX() <= 2664 && location.getY() >= 2637 && location.getY() <= 2644 || location.getX() >= 2623 && location.getX() <= 2690 && location.getY() >= 2561 && location.getY() <= 2688) 
			return 9;
		if (location.getX() > 3270 && location.getX() < 3455 && location.getY() > 2880 && location.getY() < 3330) 
			return 10;
		if (location.getX() > 3187 && location.getX() < 3253 && location.getY() > 3189 && location.getY() < 3263) 
			return 11;
		if (location.getX() > 3002 && location.getX() < 3004 && location.getY() > 3002 && location.getY() < 3004) 
			return 12;
		if (location.getX() >= 2360 && location.getX() <= 2445 && location.getY() >= 5045 && location.getY() <= 5125) 
			return 13;
		if (location.getX() >= 3038 && location.getX() <= 3044 && location.getY() >= 3949 && location.getY() <= 3959) 
			return 14;
		if (location.getX() >= 3060 && location.getX() <= 3099 && location.getY() >= 3399 && location.getY() <= 3450) 
			return 15;
		if (location.getX() >= 3008 && location.getX() <= 3071 && location.getY() >= 4800 && location.getY() <= 4863) 
			return 16;
		if (location.getX() >= 2691 && location.getX() <= 2826 && location.getY() >= 2690 && location.getY() <= 2831) 
			return 17;
		if (location.getX() >= 3155 && location.getX() <= 3192 && location.getY() >= 2962 && location.getY() <= 2994) 
			return 18;
		if (location.getX() >= 2526 && location.getX() <= 2556 && location.getY() >= 3538 && location.getY() <= 3575) 
			return 19;
		if (location.getX() >= 3165 && location.getX() <= 3199 && location.getY() >= 3022 && location.getY() <= 3054) 
			return 20;
		if (location.getX() >= 2785 && location.getX() <= 2804 && location.getY() >= 3312 && location.getY() <= 3327) 
			return 21;
		if ((location.getX() >= 2792 && location.getX() <= 2829 && location.getY() >= 3412 && location.getY() <= 3472) ||
				(location.getX() > 2828 && location.getX() < 2841 && location.getY() > 3430 && location.getY() < 3459) ||
				(location.getX() > 2839 && location.getX() < 2861 && location.getY() > 3415 && location.getY() < 3441))
			return 22;
		if (location.getX() >= 2850 && location.getX() <= 2879 && location.getY() >= 3446 && location.getY() <= 3522)
			return 23;
		if (location.getX() >= 2878 && location.getX() <= 2937 && location.getY() >= 3524 && location.getY() <= 3582) 
			return 24;
		if (location.getX() >= 2744 && location.getX() <= 2787 && location.getY() >= 3457 && location.getY() <= 3519) 
			return 25;
		if (location.getX() >= 3425 && location.getX() <= 3589 && location.getY() >= 3256 && location.getY() <= 3582) 
			return 26;
		if (location.getX() >= 2883 && location.getX() <= 2942 && location.getY() >= 4547 && location.getY() <= 4605) 
			return 27;
		if (location.getX() >= 2819 && location.getX() <= 2859 && location.getY() >= 3224 && location.getY() <= 3312) 
			return 28;
		if (location.getX() >= 3067 && location.getX() <= 3134 && location.getY() >= 3223 && location.getY() <= 3297) 
			return 29;
		if (location.getX() >= 3324 && location.getX() <= 3392 && location.getY() >= 3196 && location.getY() <= 3329) 
			return 30;
		if (location.getX() >= 2800 && location.getX() <= 2869 && location.getY() >= 3324 && location.getY() <= 3391) 
			return 31;
		if (location.getX() >= 2492 && location.getX() <= 2563 && location.getY() >= 3132 && location.getY() <= 3203) 
			return 32;
		if (location.getX() >= 2945 && location.getX() <= 2968 && location.getY() >= 3477 && location.getY() <= 3519) 
			return 33;
		if (location.getX() >= 3136 && location.getX() <= 3193 && location.getY() >= 9601 && location.getY() <= 9664) 
			return 34;
		if (location.getX() >= 2816 && location.getX() <= 2958 && location.getY() >= 3139 && location.getY() <= 3175) 
			return 35;
		if (location.getX() >= 2334 && location.getX() <= 2341 && location.getY() >= 4743 && location.getY() <= 4751) 
			return 36;
		if (location.getX() >= 2495 && location.getX() <= 2625 && location.getY() >= 3836 && location.getY() <= 3905) 
			return 37;
		if (location.getX() >= 3465 && location.getX() <= 3520 && location.getY() >= 3266 && location.getY() <= 3309) 
			return 38;
		if (location.getX() >= 3585 && location.getX() <= 3705 && location.getY() >= 3462 && location.getY() <= 3539) 
			return 39;
		if (location.getX() >= 2985 && location.getX() <= 3064 && location.getY() >= 3164 && location.getY() <= 3261) 
			return 40;
		if (location.getX() >= 2913 && location.getX() <= 2989 && location.getY() >= 3185 && location.getY() <= 3267) 
			return 41;
		if (location.getX() >= 2639 && location.getX() <= 2740 && location.getY() >= 3391 && location.getY() <= 3503) 
			return 42;
		if (location.getX() >= 2816 && location.getX() <= 2879 && location.getY() >= 2946 && location.getY() <= 3007) 
			return 43;
		if (location.getX() >= 2874 && location.getX() <= 2934 && location.getY() >= 3390 && location.getY() <= 3492) 
			return 44;
		if (location.getX() >= 2413 && location.getX() <= 2491 && location.getY() >= 3386 && location.getY() <= 3515) 
			return 45;
		if (location.getX() >= 2431 && location.getX() <= 2495 && location.getY() >= 5117 && location.getY() <= 5180) 
			return 46;
		if (location.getX() >= 3168 && location.getX() <= 3291 && location.getY() >= 3349 && location.getY() <= 3514) 
			return 47;
		if (location.getX() >= 2532 && location.getX() <= 2621 && location.getY() >= 3071 && location.getY() <= 3112) 
			return 48;
		if (location.getX() >= 2368 && location.getX() <= 2430 && location.getY() >= 3073 && location.getY() <= 3135) 
			return 49;
		if (location.getX() >= 2440 && location.getX() <= 2444 && location.getY() >= 3083 && location.getY() <= 3095) 
			return 50;
		if (location.getX() >= 2359 && location.getX() <= 2440 && location.getY() >= 9466 && location.getY() <= 9543) 
			return 51;
		if (location.getX() >= 2251 && location.getX() <= 2295 && location.getY() >= 4675 && location.getY() <= 4719) 
			return 52;
		if (location.getX() >= 3463 && location.getX() <= 3515 && location.getY() >= 9469 && location.getY() <= 9524) 
			return 53;
		if (location.getX() >= 3200 && location.getX() <= 3303 && location.getY() >= 3273 && location.getY() <= 3353) 
			return 54;;
			if (location.getX() >= 3274 && location.getX() <= 3328 && location.getY() >= 3315 && location.getY() <= 3353) 
				return 55;
			if (location.getX() >= 3274 && location.getX() <= 3266 && location.getY() >= 3323 && location.getY() <= 3327) 
				return 56;
			if (location.getX() >= 3274 && location.getX() <= 3200 && location.getY() >= 3323 && location.getY() <= 3265) 
				return 57;
			if (location.getX() >= 3324 && location.getX() <= 3263 && location.getY() >= 3408 && location.getY() <= 3285) 
				return 58;
			if (location.getX() >= 3324 && location.getX() <= 3286 && location.getY() >= 3408 && location.getY() <= 3327) 
				return 59;
			if (location.getX() >= 3136 && location.getX() <= 3136 && location.getY() >= 3193 && location.getY() <= 3199) 
				return 60; 
			if (location.getX() >= 3121 && location.getX() <= 3200 && location.getY() >= 3199 && location.getY() <= 3268) 
				return 61; 
			if (location.getX() >= 3121 && location.getX() <= 3269 && location.getY() >= 3199 && location.getY() <= 3314)
				return 62;
			if (location.getX() >= 3066 && location.getX() <= 3315 && location.getY() >= 3147 && location.getY() <= 3394) 
				return 63;
			if (location.getX() >= 3200 && location.getX() <= 3354 && location.getY() >= 3315 && location.getY() <= 3394) 
				return 64;
			if (location.getX() >= 3248 && location.getX() <= 3395 && location.getY() >= 3328 && location.getY() <= 3468) 
				return 65;
			if (location.getX() >= 3111 && location.getX() <= 3469 && location.getY() >= 3264 && location.getY() <= 3524) 
				return 66;
			if (location.getX() >= 3265 && location.getX() <= 3469 && location.getY() >= 3328 && location.getY() <= 3524) 
				return 67;
			if (location.getX() >= 3329 && location.getX() <= 3447 && location.getY() >= 3418 && location.getY() <= 3524) 
				return 68;
			if (location.getX() >= 2889 && location.getX() <= 3265 && location.getY() >= 2940 && location.getY() <= 3324) 
				return 69;
			if (location.getX() >= 3014 && location.getX() <= 3261 && location.getY() >= 3065 && location.getY() <= 3324) 
				return 70; 
			if (location.getX() >= 2880 && location.getX() <= 3325 && location.getY() >= 2935 && location.getY() <= 3394) 
				return 71; 
			return 0;
	}
}
