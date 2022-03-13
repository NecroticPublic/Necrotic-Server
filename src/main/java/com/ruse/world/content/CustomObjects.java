package com.ruse.world.content;

import java.util.concurrent.CopyOnWriteArrayList;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.GameObject;
import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.world.World;
import com.ruse.world.clip.region.RegionClipping;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.player.Player;

/**
 * Handles customly spawned objects (mostly global but also privately for players)
 * @author Gabriel Hannason
 */
public class CustomObjects {
	
	private static final int DISTANCE_SPAWN = 70; //Spawn if within 70 squares of distance
	private static final CopyOnWriteArrayList<GameObject> CUSTOM_OBJECTS = new CopyOnWriteArrayList<GameObject>();
	private static final int[] HOME_OBJECTS = {4764, 4772};

	
	public static void init() {
		for(int i = 0; i < CUSTOM_OBJECTS_SPAWNS.length; i++) {
			int id = CUSTOM_OBJECTS_SPAWNS[i][0];
			int x = CUSTOM_OBJECTS_SPAWNS[i][1];
			int y = CUSTOM_OBJECTS_SPAWNS[i][2];
			int z = CUSTOM_OBJECTS_SPAWNS[i][3];
			int face = CUSTOM_OBJECTS_SPAWNS[i][4];
			GameObject object = new GameObject(id, new Position(x, y, z));
			object.setFace(face);
			CUSTOM_OBJECTS.add(object);
			World.register(object);
		}
		for(int i = 0; i < CLIENT_OBJECTS.length; i++) {
			int id = CLIENT_OBJECTS[i][0];
			int x = CLIENT_OBJECTS[i][1];
			int y = CLIENT_OBJECTS[i][2];
			int z = CLIENT_OBJECTS[i][3];
			int face = CLIENT_OBJECTS[i][4];
			GameObject object = new GameObject(id, new Position(x, y, z));
			object.setFace(face);
			RegionClipping.addObject(object);
		}
	}
	
	private static void handleList(GameObject object, String handleType) {
		switch(handleType.toUpperCase()) {
		case "DELETE":
			for(GameObject objects : CUSTOM_OBJECTS) {
				if(objects.getId() == object.getId() && object.getPosition().equals(objects.getPosition())) {
					CUSTOM_OBJECTS.remove(objects);
				}
			}
			break;
		case "ADD":
			if(!CUSTOM_OBJECTS.contains(object)) {
				CUSTOM_OBJECTS.add(object);
			}
			break;
		case "EMPTY":
			CUSTOM_OBJECTS.clear();
			break;
		}
	}

	public static void spawnObject(Player p, GameObject object) {
		if(object.getId() != -1) {
			p.getPacketSender().sendObject(object);
			if(!RegionClipping.objectExists(object)) {
				RegionClipping.addObject(object);
			}
		} else {
			deleteObject(p, object);
		}
	}
	
	public static void deleteObject(Player p, GameObject object) {
		p.getPacketSender().sendObjectRemoval(object);
		if(RegionClipping.objectExists(object)) {
			RegionClipping.removeObject(object);
		}
	}
	
	public static void deleteGlobalObject(GameObject object) {
		handleList(object, "delete");
		World.deregister(object);
	}

	public static void spawnGlobalObject(GameObject object) {
		handleList(object, "add");
		World.register(object);
	}
	
	public static void spawnGlobalObjectWithinDistance(GameObject object) {
		for(Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if(object.getPosition().isWithinDistance(player.getPosition(), DISTANCE_SPAWN)) {
				spawnObject(player, object);
			}
		}
	}
	
	public static void deleteGlobalObjectWithinDistance(GameObject object) {
		for(Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if(object.getPosition().isWithinDistance(player.getPosition(), DISTANCE_SPAWN)) {
				deleteObject(player, object);
			}
		}
	}
	
	public static boolean objectExists(Position pos) {
		return getGameObject(pos) != null;
	}

	public static GameObject getGameObject(Position pos) {
		for(GameObject objects : CUSTOM_OBJECTS) {
			if(objects != null && objects.getPosition().equals(pos)) {
				return objects;
			}
		}
		return null;
	}

	public static void handleRegionChange(Player p) {
		for(GameObject object: CUSTOM_OBJECTS) {
			if(object == null)
				continue;
			if(object.getPosition().isWithinDistance(p.getPosition(), DISTANCE_SPAWN)) {
				spawnObject(p, object);
			}
		}
	}
	
	public static void objectRespawnTask(Player p, final GameObject tempObject, final GameObject mainObject, final int cycles) {
		deleteObject(p, mainObject);
		spawnObject(p, tempObject);
		TaskManager.submit(new Task(cycles) {
			@Override
			public void execute() {
				deleteObject(p, tempObject);
				spawnObject(p, mainObject);
				this.stop();
			}
		});
	}
	
	public static void globalObjectRespawnTask(final GameObject tempObject, final GameObject mainObject, final int cycles) {
		deleteGlobalObject(mainObject);
		spawnGlobalObject(tempObject);
		TaskManager.submit(new Task(cycles) {
			@Override
			public void execute() {
				deleteGlobalObject(tempObject);
				spawnGlobalObject(mainObject);
				this.stop();
			}
		});
	}

	public static void globalObjectRemovalTask(final GameObject object, final int cycles) {
		spawnGlobalObject(object);
		TaskManager.submit(new Task(cycles) {
			@Override
			public void execute() {
				deleteGlobalObject(object);
				this.stop();
			}
		});
	}

	public static void globalFiremakingTask(final GameObject fire, final Player player, final int cycles) {
		spawnGlobalObject(fire);
		TaskManager.submit(new Task(cycles) {
			@Override
			public void execute() {
				deleteGlobalObject(fire);
				if(player.getInteractingObject() != null && player.getInteractingObject().getId() == 2732) {
					player.setInteractingObject(null);
				}
				this.stop();
			}
			@Override
			public void stop() {
				setEventRunning(false);
				GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(592), fire.getPosition(), player.getUsername(), false, 150, true, 100));
			}
		});
	}
	
	/**
	 * Contains
	 * @param ObjectId - The object ID to spawn
	 * @param absX - The X position of the object to spawn
	 * @param absY - The Y position of the object to spawn
	 * @param Z - The Z position of the object to spawn
	 * @param face - The position the object will face
	 */
	
	//Only adds clips to these objects, they are spawned clientsided
	//NOTE: You must add to the client's customobjects array to make them spawn, this is just clipping!
	private static final int[][] CLIENT_OBJECTS = {
    		/* Start of Woodcutting Area Fixes */
    		{1315, 2558, 3869, 0, 0},
    		{1315, 2553, 3863, 0, 0},
    		{1315, 2551, 3860, 0, 0},
    		{1315, 2551, 3871, 0, 0},
    		{1315, 2543, 3859, 0, 0},
    		{1315, 2536, 3866, 0, 0},
    		/* End of Woodcutting Area Fixes */
			
			{1864, 2383, 4715, 0, 0}, //RoT6 Barrier
			{1864, 2382, 4715, 0, 0}, //RoT6 Barrier
			{1864, 2384, 4715, 0, 0}, //RoT6 Barrier
			
			{409, 3239, 3608, 0, 0}, //altar at chaos
			{423, 3295, 3202, 0, 1}, //cot in ardy
			
			/*
			// Christmas 2016 event
			{28295, 3674, 2981, 0, 1}, //Christmas Snowman
			{41745, 2786, 3861, 0, 3}, //sleigh
			{41723, 2791, 3862, 0, 3}, //reindeer
			{41723, 2791, 3861, 0, 3}, //reindeer2
			{47758, 2786, 3863, 0, 3}, //green present
			{47762, 2785, 3862, 0, 3}, //presents
			{47760, 2785, 3861, 0, 3}, //presents
			{19038, 3673, 2975, 0, 1}, //Christmas Tree
			{19038, 3683, 2973, 0, 2}, //Wintumber Tree
			{33183, 3677, 2986, 0, 1}, //Ice Light
			{33183, 3675, 2982, 0, 1}, //Ice Light
			{33183, 3675, 2977, 0, 1}, //Ice Light
			{33183, 3681, 2976, 0, 1}, //Ice Light
			{33183, 3684, 2978, 0, 1}, //Ice Light
			{33183, 3684, 2975, 0, 1}, //Ice Light
			{33183, 3679, 2971, 0, 1}, //Ice Light
			{47745, 3675, 2974, 0, 2}, //Minotaur Sculture
			{47746, 3687, 2970, 0, 1}, //Draoon Sculture
			//end of xmas
			 */
			
			{28742, 3209, 3216, 0, 1},
			
			{11666, 3078, 9495, 0, 3},
			/* beanstalk */
			{-1, 2139, 5518, 3, 1},
			{-1, 2139, 5520, 2, 1},
			{-1, 2141, 5522, 2, 1},
			/* Cooking Guild */
			{4483, 3145, 3450, 0, 2},
			{4483, 3146, 3450, 0, 2},
			{-1, 3145, 3453, 0, 1},
			{12269, 3145, 3453, 0, 1},
			{19230, 3148, 3449, 0, 1},
			{19230, 3147, 3449, 0, 1},
			
			/* Fishing Guild */
			{10091, 2612, 3411, 0, 2},
			
			/* Varrock Smith*/
			{-1, 3231, 3441, 0, 2},
			{-1, 3230, 3441, 0, 2},
			{2783, 3231, 3442, 0, 2},
			{6189, 3231, 3440, 0, 2},
			/* Remmington random shit */
			{-1, 2978, 3239, 0, 1},
			{-1, 2980, 3240, 0, 1},
			{-1, 2979, 3241, 0, 1},
			{-1, 2972, 3245, 0, 1},
			{-1, 2972, 3246, 0, 1},
			/* Reminton ore */
			//rune
			{14859, 2986, 3229, 0, 1},
			{14859, 2978, 3227, 0, 1},
			{14859, 2969, 3232, 0, 1},
			{14859, 2964, 3238, 0, 1},
			{14859, 2968, 3246, 0, 1},
			{14859, 3273, 3250, 0, 1},
			{14859, 2982, 3251, 0, 1},
			{14859, 2989, 3245, 0, 1},
			{14859, 2989, 3236, 0, 1},
			//addy
			{11941, 2964, 3241, 0, 1},
			{11941, 2965, 3242, 0, 1},
			{11941, 2965, 3243, 0, 1},
			{11941, 2966, 3244, 0, 1},
			{11941, 2966, 3245, 0, 1},
			//gold
			{11951, 2983, 3250, 0, 1},
			{11951, 2982, 3250, 0, 1},
			{11951, 2984, 3250, 0, 1},
			{11951, 2985, 3249, 0, 1},
			{11951, 2986, 3248, 0, 1},
			{11951, 2987, 3247, 0, 1},
			{11951, 2988, 3245, 0, 1},
			{11951, 2986, 3245, 0, 1},
			{11951, 2984, 3246, 0, 1},
			{11951, 2982, 3248, 0, 1},
			//mith
			{11947, 2990, 3244, 0, 1},
			{11947, 2990, 3243, 0, 1},
			{11947, 2989, 3242, 0, 1},
			{11947, 2990, 3242, 0, 1},
			{11947, 2990, 3240, 0, 1},
			{11947, 2990, 3239, 0, 1},
			{11947, 2989, 3239, 0, 1},
			{11947, 2986, 3244, 0, 1},
			//coal
			{11963, 2972, 3229, 0, 1},
			{11963, 2973, 3230, 0, 1},
			{11963, 2975, 3230, 0, 1},
			{11963, 2976, 3229, 0, 1},
			{11963, 2977, 3230, 0, 1},
			{11963, 2978, 3229, 0, 1},
			{11963, 2980, 3228, 0, 1},
			{11963, 2982, 3227, 0, 1},
			{11963, 2983, 3227, 0, 1},
			{11963, 2984, 3228, 0, 1},
			{11963, 2984, 3230, 0, 1},
			{11963, 2982, 3230, 0, 1},
			{11963, 2980, 3231, 0, 1},
			{11963, 2978, 3231, 0, 1},
			//iron
			{9717, 2982, 3232, 0, 1},
			{9717, 2983, 3232, 0, 1},
			{9717, 2983, 3234, 0, 1},
			{9717, 2980, 3234, 0, 1},
			{9717, 2980, 3235, 0, 1},
			{9717, 2982, 3235, 0, 1},
			{9717, 2967, 3241, 0, 1},
			{9717, 2970, 3243, 0, 1},
			{9717, 2971, 3243, 0, 1},
			{9717, 2971, 3241, 0, 1},
			{9717, 2972, 3242, 0, 1},
			//begin woodcutting area

			{5553, 2566, 3866, 0, 1},
			{5552, 2565, 3864, 0, 1},
			{5551, 2565, 3861, 0, 1},

			{1281, 2567, 3874, 0, 1},
			{1281, 2564, 3875, 0, 1},
			{1281, 2569, 3880, 0, 1},
			{1281, 2563, 3879, 0, 1},

			{1309, 2551, 3884, 0, 1},
			{1309, 2559, 3879, 0, 1},
			{1309, 2554, 3880, 0, 1},

			{1306, 2555, 3884, 0, 1},
			{1306, 2559, 3886, 0, 1},
			{1306, 2556, 3889, 0, 1},

			{11338, 2575, 3879, 0, 1},
			{11338, 2575, 3880, 0, 1},

			//woodcutting firemaking area
			{2732, 2539, 3891, 0, 1},
			
			//	test fog
			{860, 2250, 3315, 0, 1},
			{860, 2250, 3321, 0, 1},
			{860, 2256, 3321, 0, 1},
			{860, 2256, 3315, 0, 1},

		/* Removing Entrana Things {-1, x, y, 0, 1}, */
		{-1, 2857, 3350, 0, 1},
		{-1, 2857, 3347, 0, 1},
		{-1, 2853, 3355, 0, 1},
		{-1, 2851, 3353, 0, 1},
		{-1, 2849, 3355, 0, 1},
		{-1, 2849, 3354, 0, 1},
		{-1, 2849, 3353, 0, 1},
		{-1, 2853, 3344, 0, 1},
		{-1, 2853, 3342, 0, 1},
		{-1, 2852, 3345, 0, 1},
		{-1, 2852, 3344, 0, 1},
		{-1, 2852, 3342, 0, 1},
		{-1, 2850, 3342, 0, 1},
		{-1, 2849, 3343, 0, 1},
		{-1, 2849, 3342, 0, 1},
		{-1, 2848, 3350, 0, 1},
		{-1, 2846, 3350, 0, 1},
		{-1, 2844, 3350, 0, 1},
		{-1, 2842, 3350, 0, 1},
		{-1, 2848, 3346, 0, 1},
		{-1, 2846, 3346, 0, 1},
		{-1, 2844, 3346, 0, 1},
		{-1, 2842, 3346, 0, 1},
		{-1, 2847, 3345, 0, 1},
		{-1, 2845, 3345, 0, 1},
		{-1, 2843, 3345, 0, 1},
		{-1, 2841, 3345, 0, 1},
		{-1, 2847, 3352, 0, 1},
		{-1, 2845, 3352, 0, 1},
		{-1, 2843, 3352, 0, 1},
		{-1, 2841, 3352, 0, 1},
		/* Adding Entrana Member Zone */
		{409, 2853, 3348, 0, 1},
		{8749, 2853, 3353, 0, 1},
		{47180, 2853, 3343, 0, 0},
		{8702, 2853, 3344, 0, 0},
		{4483, 2853, 3345, 0, 1},
		{4483, 2853, 3352, 0, 1},
		{13493, 2849, 3350, 0, 1},
		{13493, 2849, 3351, 0, 1},
		{11356, 2849, 3346, 0, 1},
		{38700, 2849, 3353, 0, 1},
		{14859, 2854, 3338, 0, 0},
		{14859, 2853, 3339, 0, 0},
		{14859, 2852, 3339, 0, 0},
		{14859, 2851, 3339, 0, 0},
		{14859, 2850, 3339, 0, 0},
		{1306, 2853, 3335, 0, 0},
		{1306, 2852, 3335, 0, 0},
		{1306, 2851, 3335, 0, 0},
		{1306, 2850, 3335, 0, 0},
		{1306, 2849, 3335, 0, 0},
			//ffa portal
		{4483, 2818, 5510, 0, 2},
		{38700, 3668, 2976, 0, 0},
		/* //Friday the 13th
		{2470, 2464, 4782, 0, 0},
		{2470, 3674, 2981, 0, 0}, //EVENT PORTAL @ HOME
		*/
		
		/* Zulrah Objects */
		{17953, 3414, 2778, 0, 1},
		{17953, 3412, 2794, 0, 0},
		{4483, 3407, 2796, 0, 0},
		{-1, 3412, 2795, 0, 0},
		{-1, 3415, 2778, 0, 0},
		{-1, 3417, 2779, 0, 0},
		{-1, 3419, 2780, 0, 0},
		{-1, 3424, 2779, 0, 0},
		{-1, 3426, 2780, 0, 0},
		{-1, 3430, 2779, 0, 0},
		{-1, 3426, 2777, 0, 0},
		{-1, 3424, 2776, 0, 0},
		{-1, 3423, 2774, 0, 0},
		{-1, 3422, 2774, 0, 0},
		{-1, 3419, 2773, 0, 0},
		/* Server sided Zulrah objects */
		{-1, 3429, 2777, 0, 0},
		{-1, 3427, 2777, 0, 0},
		{-1, 3424, 2774, 0, 0},
		{-1, 3423, 2773, 0, 0},
		{-1, 3421, 2773, 0, 0},
		{-1, 3420, 2773, 0, 0},
		{-1, 3416, 2773, 0, 0},
		{-1, 3416, 2775, 0, 0},
		{-1, 3416, 2776, 0, 0},
		{-1, 3415, 2779, 0, 0},
		{-1, 3416, 2779, 0, 0},
		{-1, 3419, 2782, 0, 0},
		{-1, 3422, 2780, 0, 0},
		
		{2274, 3652, 3488, 0, 0}, //ghost town portal
		//unrelated
		{15478, 2601, 3092, 0, 1},
		{-1, 2601, 3094, 0, 0},
		{-1, 2602, 3092, 0, 0},
		{-1, 2601, 3096, 0, 0},
		{-1, 2268, 3067, 0, 0},
		{401, 3503, 3567, 0, 0},
		{401, 3504, 3567, 0, 0},
		{1309, 2715, 3465, 0, 0},
		{1309, 2709, 3465, 0, 0},
		{1309, 2709, 3458, 0, 0},
		{1306, 2705, 3465, 0, 0},
		{1306, 2705, 3458, 0, 0},
		{-1, 2715, 3466, 0, 0},
		{-1, 2712, 3466, 0, 0},
		{-1, 2713, 3464, 0, 0},
		{-1, 2711, 3467, 0, 0},
		{-1, 2710, 3465, 0, 0},
		{-1, 2709, 3464, 0, 0},
		{-1, 2708, 3466, 0, 0},
		{-1, 2707, 3467, 0, 0},
		{-1, 2704, 3465, 0, 0},
		{-1, 2714, 3466, 0, 0},
		{-1, 2705, 3457, 0, 0},
		{-1, 2709, 3461, 0, 0},
		{-1, 2709, 3459, 0, 0},
		{-1, 2708, 3458, 0, 0},
		{-1, 2710, 3457, 0, 0},
		{-1, 2711, 3461, 0, 0},
		{-1, 2713, 3461, 0, 0},
		{-1, 2712, 3459, 0, 0},
		{-1, 2712, 3457, 0, 0},
		{-1, 2714, 3458, 0, 0},
		{-1, 2705, 3459, 0, 0},
		{-1, 2705, 3464, 0, 0},
		{2274, 2912, 5300, 2, 0},
		{2274, 2914, 5300, 1, 0},
		{2274, 2919, 5276, 1, 0},
		{2274, 2918, 5274, 0, 0},
		{2274, 3001, 3931, 0, 0},
		{2274, 3553, 9695, 0, 0},
		{-1, 2884, 9797, 0, 2},
		{4483, 2909, 4832, 0, 3},
		{4483, 2901, 5201, 0, 2},
		{4483, 2902, 5201, 0, 2},
		{9326, 3001, 3960, 0, 0},
		{1662, 3112, 9677, 0, 2},
		{1661, 3114, 9677, 0, 2},
		{1661, 3122, 9664, 0, 1},
		{1662, 3123, 9664, 0, 2},
		{1661, 3124, 9664, 0, 3},
		{4483, 2918, 2885, 0, 3},
		{2644, 2737, 3444, 0, 2},
		{-1, 2608, 4777, 0, 0},
		{-1, 2601, 4774, 0, 0},
		{-1, 2611, 4776, 0, 0},
		/**New Member Zone*/
		{2344, 3421, 2908, 0, 0}, //Rock blocking
        {2345, 3438, 2909, 0, 0},
        {2344, 3435, 2909, 0, 0},
        {2344, 3432, 2909, 0, 0},
        {2345, 3431, 2909, 0, 0},
        {2344, 3428, 2921, 0, 1},
        {2344, 3428, 2918, 0, 1},
        {2344, 3428, 2915, 0, 1},
        {2344, 3428, 2912, 0, 1},
        {2345, 3428, 2911, 0, 1},
        {2344, 3417, 2913, 0, 1},
        {2344, 3417, 2916, 0, 1},
        {2344, 3417, 2919, 0, 1},
        {2344, 3417, 2922, 0, 1},
        {2345, 3417, 2912, 0, 0},
        {2346, 3418, 2925, 0, 0},
        {10378, 3426, 2907, 0, 0},
        {8749, 3426, 2923, 0, 2}, //Altar
        {-1, 3420, 2909, 0, 10}, //Remove crate by mining
        {-1, 3420, 2923, 0, 10}, //Remove Rockslide by Woodcutting
        {14859, 3421, 2909, 0, 0}, //Mining
        {14859, 3419, 2909, 0, 0},
        {14859, 3418, 2910, 0, 0},
        {14859, 3418, 2911, 0, 0},
        {14859, 3422, 2909, 0, 0},
        {1306, 3418, 2921, 0, 0}, //Woodcutting
        {1306, 3421, 2924, 0, 0},
        {1306, 3420, 2924, 0, 0},
        {1306, 3419, 2923, 0, 0},
        {1306, 3418, 2922, 0, 0},
		{-1, 3430, 2912, 0, 2}, 
		{13493, 3424, 2916, 0, 1},//Armour  stall
        /**New Member Zone end*/
		{-1, 3098, 3496, 0, 1},
		{-1, 3095, 3498, 0, 1},
		{-1, 3088, 3509, 0, 1},
		{-1, 3095, 3499, 0, 1},
		{-1, 3085, 3506, 0, 1},
		{-1, 3206, 3263, 0, 0},
		{-1, 2794, 2773, 0, 0},
		{2, 2692, 3712, 0, 3},
		{2, 2688, 3712, 0, 1},
		{4875, 3671, 2972, 0, 0},
		{4874, 3672, 2972, 0, 0},
		{4876, 3673, 2972, 0, 0},
		{4877, 3674, 2972, 0, 0},
		{4878, 3675, 2972, 0, 0},
		{409, 3678, 2983, 0, 1},
		{411, 3678, 2980, 0, 1},
		{26945, 3678, 2978, 0, 0},
		{1746, 3674, 2979, 0, 0},
		{29942, 3674, 2978, 0, 0},
		{172, 3671, 2976, 0, 0},
		{6552, 3680, 2979, 0, 2},
		
		{410, 3680, 2985, 0, 2},
		{2273, 2384, 4719, 0, 2},
		{ 11758, 3019, 9740, 0, 0},
		{ 11758, 3020, 9739, 0, 1},
		{ 11758, 3019, 9738, 0, 2},
		{ 11758, 3018, 9739, 0, 3},
		{ 11933, 3028, 9739, 0, 1},
		{ 11933, 3032, 9737, 0, 2},
		{ 11933, 3032, 9735, 0, 0},
		{ 11933, 3035, 9742, 0, 3},
		{ 11933, 3034, 9739, 0, 0},
		{ 11936, 3028, 9737, 0, 1},
		{ 11936, 3029, 9734, 0, 2},
		{ 11936, 3031, 9739, 0, 0},
		{ 11936, 3032, 9741, 0, 3},
		{ 11936, 3035, 9734, 0, 0},
		{ 11954, 3037, 9739, 0, 1},
		{ 11954, 3037, 9735, 0, 2},
		{ 11954, 3037, 9733, 0, 0},
		{ 11954, 3039, 9741, 0, 3},
		{ 11954, 3039, 9738, 0, 0},
		{ 11963, 3039, 9733, 0, 1},
		{ 11964, 3040, 9732, 0, 2},
		{ 11965, 3042, 9734, 0, 0},
		{ 11965, 3044, 9737, 0, 3},
		{ 11963, 3042, 9739, 0, 0},
		{ 11963, 3045, 9740, 0, 1},
		{ 11965, 3043, 9742, 0, 2},
		{ 11964, 3045, 9744, 0, 0},
		{ 11965, 3048, 9747, 0, 3},
		{ 11951, 3048, 9743, 0, 0},
		{ 11951, 3049, 9740, 0, 1},
		{ 11951, 3047, 9737, 0, 2},
		{ 11951, 3050, 9738, 0, 0},
		{ 11951, 3052, 9739, 0, 3},
		{ 11951, 3051, 9735, 0, 0},
		{ 11947, 3049, 9735, 0, 1},
		{ 11947, 3049, 9734, 0, 2},
		{ 11947, 3047, 9733, 0, 0},
		{ 11947, 3046, 9733, 0, 3},
		{ 11947, 3046, 9735, 0, 0},
		{ 11941, 3053, 9737, 0, 1},
		{ 11939, 3054, 9739, 0, 2},
		{ 11941, 3053, 9742, 0, 0},
		{ 14859, 3038, 9748, 0, 3},
		{ 14859, 3044, 9753, 0, 0},
		{ 14859, 3048, 9754, 0, 1},
		{ 14859, 3054, 9746, 0, 2},
		{ 4306, 3026, 9741, 0, 0},
		{ 6189, 3022, 9742, 0, 1},
		{ 75 , 2914, 3452, 0, 2},
		{ 10091 , 2352, 3703, 0, 2},
		{ 11758, 3449, 3722, 0, 0},
		{ 11758, 3450, 3722, 0, 0},
		{ 50547, 3445, 3717, 0, 3},
		{ -1, 3090, 3496, 0, 0},
		{ -1, 3090, 3494, 0, 0},
		{ -1, 3092, 3496, 0, 0},
		{ -1, 3659, 3508, 0, 0},
		{ 4053, 3660, 3508, 0, 0},
		{ 4051, 3659, 3508, 0, 0},
		{ 1, 3649, 3506, 0, 0},
		{ 1, 3650, 3506, 0, 0},
		{ 1, 3651, 3506, 0, 0},
		{ 1, 3652, 3506, 0, 0},
		{ 8702, 3423, 2911, 0, 0},
		{ 47180, 3422, 2918, 0, 0}, 
		{ 11356, 3418, 2917, 0, 1},
		{ -1, 2860, 9734, 0, 1},
		{ -1, 2857, 9736, 0, 1},
		{ 664, 2859, 9742, 0, 1},
		{ 1167, 2860, 9742, 0, 1},
		{ 5277, 3691, 3465, 0, 2},
		{ 5277, 3690, 3465, 0, 2},
		{ 5277, 3688, 3465, 0, 2},
		{ 5277, 3687, 3465, 0, 2},
// Begin home deleted objects
		{ -1, 3671, 2981, 0, 2},
		{ -1, 3671, 2981, 0, 2},
		{ -1, 3672, 2981, 0, 2},
		{ -1, 3671, 2980, 0, 2},
		{ -1, 3670, 2981, 0, 2},
		{ -1, 3671, 2982, 0, 2},
		{ -1, 3671, 2981, 0, 2},
		
		{ -1, 3668, 2980, 0, 2},
		{ -1, 3667, 2979, 0, 2},
		{ -1, 3668, 2978, 0, 2},
		{ -1, 3669, 2979,0, 2},
		{ -1, 3665, 2983, 0, 2},
		{ -1, 3665, 2981, 0, 2},
		{ -1, 3665, 2979, 0, 2},
		
		{ -1, 3668, 2983, 0, 2},
		{ -1, 3669, 2983, 0, 2},
		{ -1, 3668, 2977, 0, 2},
		
		{ -1, 3668, 2977, 0, 2},
		{ -1, 3670, 2976, 0, 2},
		{ -1, 3667, 2977, 0, 2},
		{ -1, 3665, 2977, 0, 2},
		
		{ -1, 3668, 2979, 0, 2},
		{ 737, 2887, 4377, 0, 2},
		{ 699, 2885, 4378, 0, 2},
		
		{ -1, 3664, 2976, 0, 2},
		{ -1, 3664, 2977, 0, 2},
		{ -1, 3664, 2978, 0, 2},
		{ -1, 3664, 2979, 0, 2},
		{ -1, 3664, 2980, 0, 2},
		{ -1, 3664, 2981, 0, 2},
		{ -1, 3664, 2982, 0, 2},
		{ -1, 3664, 2983, 0, 2},
		{ -1, 3664, 2984, 0, 2},
		
		{ 11338, 3665, 2976, 0, 1},
		{ 11338, 3665, 2977, 0, 1},
		{ 11338, 3665, 2978, 0, 1},
		{ 11338, 3665, 2979, 0, 1},
		{ 11338, 3665, 2980, 0, 1},
		{ 11338, 3665, 2981, 0, 1},
		{ 11338, 3665, 2982, 0, 1},
		{ 11338, 3665, 2983, 0, 1},
		{ 11338, 3665, 2984, 0, 1},
		
		{ 11339, 3662, 2977, 0, 1},
		{ 11339, 3662, 2980, 0, 1},
		{ 11339, 3662, 2983, 0, 1},
		
		{ -1, 3683, 2982, 0, 2},
		{ -1, 3683, 2984, 0, 2},
		
		{ -1, 3681, 2981, 0, 2},
		{ -1, 3681, 2982, 0, 2},
		{ -1, 3681, 2983, 0, 2},
		{ -1, 3666, 2991, 0, 0},
		{ -1, 3666, 2992, 0, 0},
		{ 2732, 2848, 3335, 0, 0 }, //unlimited fire
		{ 2732, 2711, 3438, 0, 0 }, //unlimited fire
       // zammy portal
		//{ 4388, 3687, 2971, 0, 2},
		
		
	};
	
	
	/**
	 * Contains
	 * @param ObjectId - The object ID to spawn
	 * @param absX - The X position of the object to spawn
	 * @param absY - The Y position of the object to spawn
	 * @param Z - The Z position of the object to spawn
	 * @param face - The position the object will face
	 */
	
	//Objects that are handled by the server on regionchange
	private static final int[][] CUSTOM_OBJECTS_SPAWNS = {
		//id, x, y, z, face
		/* Dungeoneering bullshit */
		{45803, 2455, 4940, 0, 0}, //exit portal
		{12269, 2448, 4941, 0, 0}, //range
		{1767, 2476, 4940, 0, 0},
		/* End of Dung Bullshit*/
	};
}
