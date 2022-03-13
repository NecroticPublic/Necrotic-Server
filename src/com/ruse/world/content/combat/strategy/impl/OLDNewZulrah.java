package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.util.Stopwatch;
import com.ruse.world.World;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.npc.NPCMovementCoordinator.Coordinator;

public class OLDNewZulrah implements CombatStrategy {

	public static NPC ZULRAH;
	private static int zulrahId, zulrahHp;
	private static boolean firstForm, isDiving, firstCall, venom;
	
	private static Stopwatch venomTime = new Stopwatch();
	
	private static boolean[] snakelingsKilled;
	
	private static int form;
	
	private static int defaultConstitution = 10000;
	
	private static int phase, prayerType, prayerTimer;

	private static int[] moveX = {3366, 3360, 3370, 3363, 3356, 3369};
	private static int[] moveY = {3800, 3801, 3805, 3818, 3812, 3809};
	
	private static int randomCoord;
	private static int dir = -1;
	private static Position zulrahPosition, venomPosition, snakelingPosition;
	
	private static NPC TILE, TILE2, SNAKELING;
	//private static TilePointer tile;
	//private static ArrayList<TilePointer> poisonedTiles = new ArrayList<TilePointer>();
	
	private Animation shoot = new Animation(5069);
	private Animation charge = new Animation(5806);
	private Animation melee_attack = new Animation(5807);
	private static Animation dive = new Animation(5072);
	private static Animation rise = new Animation(5073);
	private Graphic toxic_cloud = new Graphic(310);
	private Graphic fire = new Graphic(78);
	private Graphic snakeling_summon = new Graphic(281);
	
	/**
	 * Handles the spawning of {@link OLDZulrah}.
	 * 
	 * @param firstSpawn 
	 * 				Determines whether this is the first time the method is being called.
	 */
	public static void spawn() {
		if (ZULRAH != null && ZULRAH.isRegistered()) {
			System.out.println("Could not spawn another, as ZULRAH is registered.");
		} else {
			getDir();
			zulrahId = 2042;
			zulrahHp = 2000;
			zulrahPosition = new Position(moveX[dir], moveY[dir]);
			firstForm = true;
			ZULRAH = new NPC(zulrahId, zulrahPosition);
			ZULRAH.getMovementCoordinator().setCoordinator(new Coordinator(true, 3));
			World.register(ZULRAH);
			ZULRAH.performAnimation(rise);
			ZULRAH.setDefaultConstitution(defaultConstitution);
			ZULRAH.setConstitution(defaultConstitution);
			form = 1;
			System.out.println("Spawned completed");
		}
	}
	
	public static void despawn() {
		System.out.println("Despawn called");
		ZULRAH.performAnimation(dive);
		TaskManager.submit(new Task(1, ZULRAH, false) {
			int tick = 0;
			@Override
			public void execute() {
				if(tick == 3){
					zulrahHp = ZULRAH.getConstitution();
					phase = 2;
				if(ZULRAH != null && ZULRAH.isRegistered())
					World.deregister(ZULRAH);
					dir = -1;
					form = -1;
				this.stop();
				}
				tick++;
			} 
		});
		System.out.println("Despawn completed");
	}
	
	public static void move() {
		if(ZULRAH != null && ZULRAH.isRegistered()){
			zulrahHp = ZULRAH.getConstitution();
			World.deregister(ZULRAH);
		}
		System.out.println("deregistered zulrah, zulrahHp = "+zulrahHp);
		TaskManager.submit(new Task(4, ZULRAH, false) {
			@Override
			public void execute() {
					int newpos = newDir();
					zulrahPosition = new Position(moveX[newpos], moveY[newpos]);
					zulrahId = newForm();
					phase = prayerType = prayerTimer = 0;
					System.out.println("Zulrah: "+ zulrahId +", phase = "+phase+", Moved to newpos: "+newpos + ", x: "+moveX[newpos] + ", y: " +moveY[newpos]);
					isDiving = false;
					System.out.println("IsDiving false");
					ZULRAH = new NPC(zulrahId, zulrahPosition);
					System.out.println("Declared new NPC");
					ZULRAH.getMovementCoordinator().setCoordinator(new Coordinator(true, 10));
					System.out.println("Set ZULRAH movement");
					World.register(ZULRAH);
					System.out.println("Registered zulrah");
					ZULRAH.performAnimation(rise);
					System.out.println("zulrahHp = "+zulrahHp + ", Zulrah constitution "+ZULRAH.getConstitution());
					ZULRAH.setDefaultConstitution(zulrahHp);
					ZULRAH.setConstitution(zulrahHp);
					System.out.println("done moving " +ZULRAH.getConstitution());
					ZULRAH.setForcedChat("HISSSS!");
					stop();						
			}
		});
	}
	
	private static int getForm() {
		if (ZULRAH == null) {
			return -1;
		}
		if (!ZULRAH.isRegistered()) {
			return -1;
		}
		if (ZULRAH.getId() == 2042) {
			return 1; //green
		} else if (ZULRAH.getId() == 2043) {
			return 2; //red
		} else if (ZULRAH.getId() == 2044) {
			return 3; //blue
		} else {
			return -404;//ERROR
		}
	}
	
	private static int newForm() {
		int current = getForm();
		int aNewForm = 2042+Misc.getRandom(2);
		if (current == aNewForm) {
						return newForm();//TODO FIX
		}
		return aNewForm;
	}
	
	private static int getDir() {
		if (dir == -1) {
			int newdir = Misc.getRandom(moveX.length-1);
			dir = newdir;
		}
		if (ZULRAH != null && ZULRAH.isRegistered() && ZULRAH.getPosition().getX() != moveX[dir]) {
			System.out.println("Error. Dir = "+dir+ ", Zulrah's pos = "+ZULRAH.getPosition().getX() + ", moveX["+dir+"] = "+moveX[dir]);
		}
		return dir;
	}
	
	private static int newDir() {
		int current = getDir();
		int newdir = Misc.getRandom(moveX.length-1);
		if (ZULRAH != null && ZULRAH.isRegistered() && ZULRAH.getPosition().getX() != moveX[current]) {
			System.out.println("[ERROR 666] Zulrah's X = "+ZULRAH.getPosition().getX() + ", dir = "+current+ ", moveX["+current+"] = "+moveX[current]);
			return 666;
		}
		if (newdir == current) {
			System.out.println("[ERROR 2] Newdir == current ("+current+"). Running again.");
			return newDir();//TODO FIX
		}
		return newdir;
	}

	@Override
	public boolean canAttack(Character entity, Character victim) {
		//System.out.println("canAttack called");
		return true;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		// TODO Auto-generated method stub
		//System.out.println("attack called");
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		// TODO 
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		//System.out.println("attackDelay called");
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		//System.out.println("attackDistance called");
		return 15;
	}

	@Override
	public CombatType getCombatType() {
		//System.out.println("combatType called");
		return CombatType.MIXED;
	}

}
