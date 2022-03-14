package com.ruse;

import java.math.BigInteger;

import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.net.security.ConnectionHandler;

public class GameSettings {

	/**
	 * Change this and CTRL+SHIFT+F in IDEA to search through the project.
	 * Ctrl + Click on variable names or datatypes for references.
	 * Ctrl + F12 for an overview of this package.
	 */
	public static final String RSPS_NAME = "Necrotic";

	/**
	 * The game port
	 */
	public static final int GAME_PORT = 43594;

	public static final boolean BCRYPT_HASH_PASSWORDS = false;
	public static final int BCRYPT_ROUNDS = Integer.parseInt("04"); //add a 0 for numbers less than 10. IE: 04, 05, 06, 07, 08, 09, 10, 11, etc.

	/**
	 * The game version
	 */
	public static final int GAME_VERSION = 55;
	public static final int GAME_UID = 23; //don't change

	/**
	 *
	 * The default position
	 */
	public static final Position DEFAULT_POSITION = new Position(3669, 2980);
	public static final Position HOME_CORDS = new Position(3669, 2980, 0);

	/**
	 * Processing the engine
	 */
	public static final int ENGINE_PROCESSING_CYCLE_RATE = 600;//200;
	public static final int GAME_PROCESSING_CYCLE_RATE = 600;

	public static final long tempMuteInterval = 86400000;
	public static final int BaseImplingExpMultiplier = 2;
	/**
	 * Shop Buy Limit (at one time)
	 */
	public static final int Shop_Buy_Limit = 25000;
	public static final int Spec_Restore_Cooldown = 180000; //in milliseconds (180000 = 3 seconds * 60 * 1000)
	public static final int Vote_Announcer = 9; //1,2,3,4,5,6,7,8,9 = 9 total.
	public static final int massMessageInterval = 180000; //in milliseconds (180000 = 3 seconds * 60 * 1000)
	public static final int charcterBackupInterval = 3600000;
	public static final int charcterSavingInterval = 60000;
	public static final int playerCharacterListCapacity = 2500; //was 1000
	public static final int npcCharacterListCapacity = 20000; //was 2027

	/**
	 * The maximum amount of players that can be logged in on a single game
	 * sequence.
	 */
	public static final int LOGIN_THRESHOLD = 100;

	/**
	 * The maximum amount of players that can be logged in on a single game
	 * sequence.
	 */
	public static final int LOGOUT_THRESHOLD = 100;
	
	/**
	 * The maximum amount of players who can receive rewards on a single game
	 * sequence.
	 */
	public static final int VOTE_REWARDING_THRESHOLD = 15;

	/**
	 * The maximum amount of connections that can be active at a time, or in
	 * other words how many clients can be logged in at once per connection.
	 * (0 is counted too)
	 */
	public static final int CONNECTION_AMOUNT = 3;

	/**
	 * The throttle interval for incoming connections accepted by the
	 * {@link ConnectionHandler}.
	 */
	public static final long CONNECTION_INTERVAL = 1000;

	/**
	 * The number of seconds before a connection becomes idle.
	 */
	public static final int IDLE_TIME = 15;
	
	/**
	 * The keys used for encryption on login
	 */
	//REGENERATE RSA
	/*This is the server, so in your PrivateKeys.txt
	Copy the ONDEMAND_MODULUS's value to RSA_MODULUS below.
	Copy the ONDEMAND_EXPONENT's value to RSA_EXPONENT below.
	*/
	public static final BigInteger RSA_MODULUS = new BigInteger("92714122021553179775366524500108363230742886412562460353371834211639950528415299652617724152900849886190308772072042859622861358879128131094768196236228636841986869596552685912224862654196445056305117750007439275285163469435632717305809526968825980718429695794961208950600403227465945624833677031718369278551");
	public static final BigInteger RSA_EXPONENT = new BigInteger("17270457934283247916408662756875091907180816291935281077772301937160695729906678336804510070015618282963994224475571039722231586267244399719317792081600911904257877789746374953394931142670477059430461930776817938913032616330757618436079334868861188547638041295900487999415794052309697768262125408141162427009");

	/**
	 * The maximum amount of messages that can be decoded in one sequence.
	 */
	public static final int DECODE_LIMIT = 30;

	public static final String BCRYPT_EXAMPLE_SALT = "$2a$09$kCPIaYJ6vJmzJM/zq8vuSO";

	/**
	 * WEB URLS
	 */
	public static final String DomainUrl = "www.Necrotic.org/";
	public static final String ForumUrl = DomainUrl+"forums";
	public static final String StoreUrl = DomainUrl+"store";
	public static final String VoteUrl = DomainUrl+"vote";
	public static final String HiscoreUrl = DomainUrl+"hiscores";
	public static final String ReportUrl = DomainUrl+"forums/forumdisplay.php?fid=28";
	public static final String RuleUrl = DomainUrl+"forums/showthread.php?tid=17";
	public static final String CommandsUrl = DomainUrl+"forums/showthread.php?tid=73";
	public static final String RankFeaturesUrl = DomainUrl+"forums/showthread.php?tid=147"; //http://Ruseps.com/forums/index.php?/topic/2-member-rank-features/
	public static final String WikiaUrl = "www.necrotic.wikia.com"; //http://necrotic.wikia.com/wiki/Special:Search?search=balls&fulltext=Search
	public static final String IronManModesUrl = DomainUrl+"forums/showthread.php?tid=12"; //http://Ruseps.com/forums/index.php?/topic/3-ironman-modes/
	public static final String HexUrl = "www.colorpicker.com";
	public static final String DiscordUrl = DomainUrl+"discord";
	public static final String ThreadUrl = ForumUrl+"/showthread.php?tid=";
	public static final String RickRoll = "www.youtube.com/watch?v=C1R2oE7mvXw";
	public static final String DifficultyUrl = WikiaUrl+"/wiki/Difficulties";

	public static final int[] MASSACRE_ITEMS =
		{4587, 20051, 13634, 13632, 11842, 11868, 14525, 13734, 10828, 1704, 15365, 15363, 7462, 3842
		};

	public static boolean Halloween = false;
	public static boolean Christmas2016 = false;
	public static boolean newYear2017 = false;
	public static boolean FridayThe13th = false;
	public static final Position CORP_CORDS =  new Position(2900, 4384);
	public static final Position EDGE_CORDS = new Position(3094, 3503, 0);
	public static final Position TRIO_CORDS = new Position(3025, 5231, 0);
	public static final Position KFC_CORDS = new Position(2606, 4774, 4);
	public static final Position TRADE_CORDS = new Position(3164, 3485, 0);
	public static final Position CHILL_CORDS = new Position(2856, 3812, 1);
	public static final Position MEMBER_ZONE = new Position(2851, 3348);
	public static final int[] hweenIds2016 = {9922, 9921, 22036, 22037, 22038, 22039, 22040};
	
	public static final int MAX_STARTERS_PER_IP = 5;
	
	public static final Item nullItem = new Item(-1, 0);
	
	/**
	 * Untradeable items
	 * Items which cannot be traded or staked
	 */


	public static final int[] UNTRADEABLE_ITEMS = 
		{13661, 13262, 13727, 20079, 6500, 20692,
		7509, 7510, 1580, //ROCK CAKES, Ice gloves
		22053, //ecumenical keys
		19748, //ardy cape 4
		1561, //pet return
		
		/* EVENT ITEMS */
		7329, 7330, 7331, 10326, 10327, 7404, 10329, 7406, 7405, 10328, 2946, // Firelighters, colored logs, gold tinderbox

		//easter 2017
		22051, 4565, 1037,

				//dice bag
				15084,

		/* xmas event 2016 */
		15420,
		13101,
		14595,
		14603,
		22043,
		14602,
		14605,
		/* end xmas event 2016 */
		
		 9013, 13150, //friday the 13th items (may 2016)
		 9922, 9921, 22036, 22037, 22038, 22039, 22040, //hween 2k16
		 /* DONE EVENT ITEMS */
		 
		2724, //clue casket
		15707, //ring of kinship
		22014, 22015, 22016, 22017, 22018, 22019, 22020, 22021, 22022, 22023, 22024, 22025, 22026, 22027, 22028, 22029, 22030, 22031, 22032, //skilling pets
		14130, 14131, 14140, 14141, //sacred clay tools
		6797, //unlimited watering can
		16691, 9704, 17239, 16669, 6068, 9703, // Iron Man Items
		773, //perfect ring
		6529, 16127, 2996, 2677, 2678, 2679, 2680, 2682, 20081,
		2683, 2684, 2685, 2686, 2687, 2688, 2689, 2690, 11180, 
		6570, 12158, 12159, 12160, 12163, 12161, 12162,
		19143, 19149, 19146, 4155,
		8850, 10551, 8839, 8840, 8842, 11663, 11664, 19712,
		11665, 8844, 8845, 8846, 8847, 
		8848, 8849, 8850, 7462, 7461, 7460, 
		7459, 7458, 7457, 7456, 7455, 7454, 7453, 11665, 10499, 9748, 
		9754, 9751, 9769, 9757, 9760, 9763, 9802, 9808,
		9784, 9799, 9805, 9781, 9796, 9793, 9775, 9772,
		9778, 9787, 9811, 9766, 9749, 9755, 9752, 9770, 
		9758, 9761, 9764, 9803, 9809, 9785, 9800, 9806, 20072,
		9782, 9797, 9794, 9776, 9773, 9779, 9788, 9812, 
		9767, 9747, 9753, 9750, 9768, 9756, 9759, 9762,
		9801, 9807, 9783, 9798, 9804, 9780, 9795, 9792, 
		9774, 9771, 9777, 9786, 9810, 9765, 9948, 9949,
		9950, 12169, 12170, 12171, 14641, 14642,
		6188, 10954, 10956, 10958,
		3057,
		5512, 5509, 5514, 5510, //rc pouches
		14076, 14077, 14081,
		9925, 9924, 9923, 9922, 9921,
		4565,
		14595, 14603, 14602, 14605, 	11789,
		19708, 19706, 19707,
		4860, 4866, 4872, 4878, 4884, 4896, 4890, 4896, 4902,
		4932, 4938, 4944, 4950, 4908, 4914, 4920, 4926, 4956,
		4926, 4968, 4994, 4980, 4986, 4992, 4998,
		18778, 18779, 18780, 18781,
		13450, 13444, 13405, 15502, 
		10548, 10549, 10550, 10555, 10552, 10553,
		20747, 
		18365, 18373, 18371, 15246, 12964, 12971, 12978, 14017,
		8851,
		13855, 13848, 13849, 13857, 13856, 13854, 13853, 13852, 13851, 13850, 5509, 13653, 14021, 14020, 19111, 14019, 14022,
		19785, 19786, 18782, 18351, 18349, 18353, 18357, 18355, 18359, 18335, 11977, 11978, 11979, 11980, 11981, 11982, 11983, 11984, 11985, 11986, 11987, 11988, 11989, 11990, 11991, 11992, 11993, 11994, 11995, 11996, 11997, 11999, 12001, 12002, 12003, 12004, 12005, 15103, 15104, 15106, 
		15105,
		13613, 13619, 13622, 13623, 13616, 13614, 13617, 13618, 13626, 13624, 13627, 13628, //runecrafting shit
		22052, //member cape
		22033, 22049, 22050, //zulrah pets
				//begin (deg) ancient armour
		13898, 13886, 13892, 13904,
		13889, 13895, 13901, 13907,
		13866, 13860, 13863, 13869,
		13878, 13872, 13875
	};

	/**
	 * Unsellable items
	 * Items which cannot be sold to shops
	 */
	public static final int UNSELLABLE_ITEMS[] = new int[] {
		2724, //clue casket
		15492, 13263, 13281, 14019, 14022, 19785, 19786, 1419, 16127, 4084, 15403, 10887, 13727, 20079, 20081, 1959, 1960,
		6199, 15501, 11848, 11850, 11856, 11854, 11852, 11846, 15018,
		15019, 15020, 15220, 14000, 14001, 14002, 14003, 2577, 19335, 15332, 19336, 19337, 19338, 19339, 19340, 9813, 20084,
		8851,6529, 14641, 14642, 14017, 2996, 10941, 10939, 14938, 10933, 14936, 10940, 18782,
		14021, 14020, 13653, 10942, 10934, 10935, 10943, 10944, 7774, 7775, 7776, 10936, 6769,
		1038, 1040,	1042, 1044, 1046, 1048, //Phats
		1053, 1055, 1057, //Hween
		1050, //Santa
		19780, //Korasi's
		20671, //Brackish
		20135, 20139, 20143, 20147, 20151, 20155, 20159, 20163, 20167, //Nex armors
		6570, 15103, 15104, 15106, 15105, //Fire cape
		19143, 19146, 19149, //God bows
		8844, 8845, 8846, 8847, 8848, 8849, 8850, 13262, //Defenders
		20135, 20139, 20143, 20147, 20151, 20155, 20159, 20163, 20167, // Torva, pernix, virtus
		13746, 13747, 13748, 13749, 13750, 13751, 13752, 13738, 13739, 13740, 13741, 13742, 13743, 13744, //Spirit shields & Sigil
		/* 11694, 11696, 11698, 11700, 11702, 11704, 11706, 11708, 11686, 11688, 11690, 11692, 11710, 11712, 11714, //Godswords, hilts, pieces 
		15486, //sol
		11730, //ss
		11718, 11720, 11722, //armadyl
		11724, 11726, 11728, //bandos */
		22055, // wildywyrm pet
		11286, 11283, //dfs & visage
		14472, 14474, 14476, 14479, //dragon pieces and plate
		14484, //dragon claws
		13887, 13888, 13893, 13895, 13899, 13901, 13905, 13907, 13911, 13913, 13917, 13919, 13923, 13925, 13929, 13931, //Vesta's
		13884, 13886, 13890, 13892, 13896, 13898, 13902, 13904, 13908, 13910, 13914, 13916, 13920, 13922, 13926, 13928, //Statius's
		13870, 13872, 13873, 13875, 13876, 13878, 13879, 13880, 13881, 13882, 13883, 13944, 13946, 13947, 13949, 13950, 13952, 13953, 13954, 13955, 13956, 13957, //Morrigan's
		13858, 13860, 13861, 13863, 13864, 13866, 13867, 13869, 13932, 13934, 13935, 13937, 13938, 13940, 13941, 13943, //Zuriel's
		20147, 20149, 20151, 20153, 20155, 20157, //Pernix
		20159, 20161, 20163, 20165, 20167, 20169, //Virtus
		20135, 20137, 20139, 20141, 20143, 20145, //Torva
		11335, //D full helm
		6731, 6733, 6735, 19111,//warrior ring, seers ring, archer ring
		962, //Christmas Cracker
		21787, 21790, 21793, //Steadfast, glaiven, ragefire
		20674,//Something something..... pvp armor, statuettes
		13958,13961,13964,13967,13970,13973,13976,13979,13982,13985,13988,13908,13914,13926,13911,13917,13923,13929,13932,13935,13938,13941,13944,13947,13950,13953,13957,13845,13846,13847,13848,13849,13850,13851,13852,13853,13854,13855,13856,13857, //Le corrupted items
		11995, 19670, 20000, 20001, 20002,
		11996, 18782, 18351, 18349, 18353, 18357, 18355, 18359, 18335,
		11997, 19712,
		12001,
		12002,
		12003,
		12005,
		12006,
		11990,
		11991,
		11992,
		11993,
		11994,
		11989,
		11988,
		11987,
		11986,
		11985,
		11984,
		11983,
		11982,
		11981,
		11979

	};

	public static final int 
	ATTACK_TAB = 0, 
	SKILLS_TAB = 1, 
	QUESTS_TAB = 2, 
	ACHIEVEMENT_TAB = 15,
	INVENTORY_TAB = 3, 
	EQUIPMENT_TAB = 4, 
	PRAYER_TAB = 5, 
	MAGIC_TAB = 6,

	SUMMONING_TAB = 13, 
	FRIEND_TAB = 8, 
	IGNORE_TAB = 9, 
	CLAN_CHAT_TAB = 10,
	LOGOUT = 14,
	OPTIONS_TAB = 11,
	EMOTES_TAB = 12;
}
