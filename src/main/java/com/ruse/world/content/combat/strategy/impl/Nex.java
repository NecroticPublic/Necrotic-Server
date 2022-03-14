package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.CombatIcon;
import com.ruse.model.Flag;
import com.ruse.model.GameObject;
import com.ruse.model.Graphic;
import com.ruse.model.Hit;
import com.ruse.model.Hitmask;
import com.ruse.model.Position;
import com.ruse.model.Projectile;
import com.ruse.model.Skill;
import com.ruse.model.Locations.Location;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.CustomObjects;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.HitQueue.CombatHit;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.npc.NPCMovementCoordinator.Coordinator;
import com.ruse.world.entity.impl.player.Player;

public class Nex implements CombatStrategy {

	public static NPC NEX;

	public static NPC FUMUS, UMBRA, CRUOR, GLACIES;

	private static boolean[] magesKilled = new boolean[4];
	private static boolean[] magesAttackable = new boolean[4];
	private static boolean[] attacks = new boolean[18];
	private static boolean zarosStage;
	private static int phase, prayerType, prayerTimer;

	public static void spawn() {

		phase = prayerType = prayerTimer = 0;
		zarosStage = false;

		for(int i = 0; i < 4; i++) {
			magesKilled[i] = magesAttackable[i] = false;
		}

		for(int i = 0; i < 18; i++) {
			attacks[i] = false;
		}

		despawn(true);

		NEX = new NPC(13447, new Position(2925, 5203));
		NEX.getMovementCoordinator().setCoordinator(new Coordinator(true, 3));

		FUMUS = new NPC(13451, new Position(2916, 5213));
		UMBRA = new NPC(13452, new Position(2934, 5213));
		CRUOR = new NPC(13453, new Position(2915, 5193));
		GLACIES = new NPC(13454, new Position(2935, 5193));

		FUMUS.getMovementCoordinator().setCoordinator(new Coordinator(false, -1));
		UMBRA.getMovementCoordinator().setCoordinator(new Coordinator(false, -1));
		CRUOR.getMovementCoordinator().setCoordinator(new Coordinator(false, -1));
		GLACIES.getMovementCoordinator().setCoordinator(new Coordinator(false, -1));


		World.register(NEX);
		World.register(FUMUS);
		World.register(UMBRA);
		World.register(CRUOR);
		World.register(GLACIES);
	}

	public static void despawn(boolean nex) {
		if(nex) {
			if(NEX != null && NEX.isRegistered())
				World.deregister(NEX);
		}
		if(FUMUS != null && FUMUS.isRegistered())
			World.deregister(FUMUS);
		if(UMBRA != null && UMBRA.isRegistered())
			World.deregister(UMBRA);
		if(CRUOR != null && CRUOR.isRegistered())
			World.deregister(CRUOR);
		if(GLACIES != null && GLACIES.isRegistered())
			World.deregister(GLACIES);
	}

	public static void death(final int id) {
		if(nexMinion(id)) {
			int index = id - 13451;
			if(index >= 0) {
				magesKilled[index] = true;
			}
			return;
		} else {
			TaskManager.submit(new Task(65) {
				@Override
				protected void execute() {
					spawn();
					stop();
				}
			});
		}
	}

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return victim.isPlayer() && ((Player)victim).getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom();
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		Player p = (Player)victim;
		if(p.getConstitution() <= 0 || NEX.getConstitution() <= 0) {
			return true;
		}
		if(NEX.isChargingAttack() || p.getConstitution() <= 0) {
			return true;
		}
		if(phase == 0)
		{
			int rnd = Misc.getRandom(10);
			if(rnd <= 2)
			{
				NEX.forceChat("Let the virus flow through you!");
				cough(p);
				NEX.performAnimation(new Animation(6986));
				new CombatHit(NEX.getCombatBuilder(), new CombatContainer(NEX, p, 1, CombatType.MAGIC, true)).handleAttack();
				return true;
			}
			if(p.getPosition().distanceToPoint(NEX.getPosition().getX(), NEX.getPosition().getY()) <= 2 && Misc.getRandom(1) == 0)
			{
				NEX.performAnimation(new Animation(6354));
				TaskManager.submit(new Task(1, NEX, false) {
					@Override
					public void execute() {
						new CombatHit(NEX.getCombatBuilder(), new CombatContainer(NEX, p, 1, CombatType.MELEE, true)).handleAttack();
						stop();
					}
				});
				return true;
			} else {
				NEX.performAnimation(new Animation(6326));
				p.performGraphic(new Graphic(383));
				new CombatHit(NEX.getCombatBuilder(), new CombatContainer(NEX, p, 1, CombatType.MAGIC, true)).handleAttack();
				return true;
			}
		}
		if(phase == 1)
		{
			int rnd = Misc.getRandom(20);
			if(rnd < 2 && !attacks[3])
			{
				NEX.forceChat("Fear the shadow!");
				attacks[3] = true;
				NEX.setChargingAttack(true);
				for(final Player p_ : Misc.getCombinedPlayerList(p))
				{
					if(p_ == null || p_.getLocation() != Location.GODWARS_DUNGEON)
						continue;
					TaskManager.submit(new Task(1, NEX, false) {
						int origX, origY;
						int ticks;
						@Override
						public void execute()
						{

							if(ticks == 0)
							{
								origX = p_.getPosition().getX();
								origY = p_.getPosition().getY();
							}
							if(ticks == 5)
							{
								if(origX == p_.getPosition().getX() && origY == p_.getPosition().getY())
								{
									p_.dealDamage(new Hit(100 + Misc.getRandom(100), Hitmask.RED, CombatIcon.NONE));
									p_.getPacketSender().sendMessage("The shadows begin to damage you!");
									this.stop();
								}
							}

							if(ticks == 10) {
								this.stop();
							}

							ticks++;
						}

						@Override
						public void stop() {
							setEventRunning(false);
							attacks[3] = false;
							NEX.setChargingAttack(false);
						}
					});
				}
			}else if(rnd >= 5 && rnd <= 7 && !attacks[4])
			{
				NEX.forceChat("Embrace darkness!");
				attacks[4] = true;
				NEX.setChargingAttack(true);
				for(Player p_ : Misc.getCombinedPlayerList(p))
				{
					if(p_ == null || p_.getLocation() != Location.GODWARS_DUNGEON)
						continue;
					TaskManager.submit(new Task(1, NEX, false) {
						int ticks = 0;
						@Override
						public void execute()
						{
							if(ticks == 10)
								setShadow(p_, 250);
							else {
								double dist = p_.getPosition().distanceToPoint(NEX.getPosition().getX(), NEX.getPosition().getY());
								if(dist < 3)
								{
									p_.getPacketSender().sendMessage("The shadows begin to consume you!");
									p_.dealDamage(new Hit(10, Hitmask.RED, CombatIcon.NONE));
									setShadow(p_, 20);
								}
								if(dist >= 3 && dist < 5)
									setShadow(p_, 40);
								if(dist > 5)
									setShadow(p_, 90);
							}
							if(ticks >= 10)
							{
								this.stop();
							}
							ticks++;
						}

						@Override
						public void stop() {
							setEventRunning(false);
							attacks[4] = false;
							NEX.setChargingAttack(false);
						}
					});
				}
				NEX.performAnimation(new Animation(6984));
				new CombatHit(NEX.getCombatBuilder(), new CombatContainer(NEX, p, 1, CombatType.MAGIC, true)).handleAttack();
				return true;
			} else {
				if(p.getPosition().distanceToPoint(NEX.getPosition().getX(), NEX.getPosition().getY()) <= 2 && Misc.getRandom(1) == 0)
				{
					NEX.performAnimation(new Animation(6354));
					TaskManager.submit(new Task(1, NEX, false) {
						@Override
						public void execute() {
							new CombatHit(NEX.getCombatBuilder(), new CombatContainer(NEX, p, 1, CombatType.MELEE, true)).handleAttack();
							stop();
						}
					});
					return true;
				} else {
					NEX.performAnimation(new Animation(6326));
					NEX.performGraphic(new Graphic(378));
					new CombatHit(NEX.getCombatBuilder(), new CombatContainer(NEX, p, 1, CombatType.MAGIC, true)).handleAttack();
					return true;
				}
			}
		}
		if(phase == 2) {
			if(p.getPosition().distanceToPoint(NEX.getPosition().getX(), NEX.getPosition().getY()) <= 2 && Misc.getRandom(1) == 0)
			{
				NEX.performAnimation(new Animation(6354));
				TaskManager.submit(new Task(1, NEX, false) {
					@Override
					public void execute() {
						new CombatHit(NEX.getCombatBuilder(), new CombatContainer(NEX, p, 1, CombatType.MELEE, true)).handleAttack();
						stop();
					}
				});
				return true;
			}
			return true;
		}
		if(phase == 4)
		{
			prayerTimer++;
			if(prayerTimer == 4)
			{
				if(prayerType == 0) {
					prayerType = 1;
					NEX.setTransformationId(13448);
					NEX.getUpdateFlag().flag(Flag.TRANSFORM);
				} else {
					prayerType = 0; 
					NEX.setTransformationId(13450);
					NEX.getUpdateFlag().flag(Flag.TRANSFORM);
				}
				prayerTimer = 0;
			}
			if(p.getPosition().distanceToPoint(NEX.getPosition().getX(), NEX.getPosition().getY()) <= 2 && Misc.getRandom(1) == 0)
			{
				NEX.performAnimation(new Animation(6354));
				TaskManager.submit(new Task(1, NEX, false) {
					@Override
					public void execute() {
						new CombatHit(NEX.getCombatBuilder(), new CombatContainer(NEX, p, 1, CombatType.MELEE, true)).handleAttack();
						stop();
					}
				});
				return true;
			} else {
				NEX.performAnimation(new Animation(6326));
				NEX.performGraphic(new Graphic(373));
				new CombatHit(NEX.getCombatBuilder(), new CombatContainer(NEX, p, 1, CombatType.MAGIC, true)).handleAttack();
				return true;
			}
		}
		if(phase == 3) {
			int rnd = Misc.getRandom(15);
			if(rnd >= 0 && rnd <= 3 && !attacks[0])
			{
				attacks[0] = true;
				NEX.forceChat("Die now, in a prison of ice!");
				NEX.setChargingAttack(true);
				final int origX = p.getPosition().getX();
				final int origY = p.getPosition().getY();
				p.getMovementQueue().reset();
				for(int x = origX-1; x < origX+1; x++)
				{
					for(int y = origY-1; y < origY+1; y++)
					{
						CustomObjects.globalObjectRemovalTask(new GameObject(57263, new Position(x, y)), 5);
					}
				}
				TaskManager.submit(new Task(10, NEX, false) {
					@Override
					public void execute()
					{
						if(p.getPosition().getX() == origX && p.getPosition().getY() == origY)
						{
							p.dealDamage(new Hit(250 + Misc.getRandom(150), Hitmask.RED, CombatIcon.NONE));
						}
						for(int x = origX-1; x < origX+1; x++)
						{
							for(int y = origY-1; y < origY+1; y++)
							{
								CustomObjects.globalObjectRemovalTask(new GameObject(6951, new Position(x, y)), 5);
							}
						}
						this.stop();
					}

					@Override
					public void stop() {
						setEventRunning(false);
						attacks[0] = false;
						NEX.setChargingAttack(false);
					}
				});
			} else if(rnd > 3 && rnd <= 5 && !attacks[1]) {
				NEX.forceChat("Contain this!");
				NEX.setChargingAttack(true);
				attacks[1] = true;
				final int origX = NEX.getPosition().getX(), origY = NEX.getPosition().getY();
				for(int x = origX-2; x < origX+2; x++)
				{
					for(int y = origY-2; y < origY+2; y++)
					{
						if(x == origX-2 || x == origX+2 || y == origY-2 || y == origY+2) {
							CustomObjects.globalObjectRemovalTask(new GameObject(57262, new Position(x, y)), 5);
						}
					}
				}
				TaskManager.submit(new Task(1, NEX, false) {
					int ticks = 0;
					@Override
					public void execute()
					{
						for(int x = origX-ticks; x < origX+ticks; x++)
						{
							for(int y = origY-ticks; y < origY+ticks; y++)
							{
								if(x == origX-ticks || y == origY-ticks || x == origX+ticks || y == origY+ticks)
								{
									p.getPacketSender().sendGraphic(new Graphic(366), new Position(x, y));
									for(Player p_ : Misc.getCombinedPlayerList(p))
									{
										if(p_ == null || p_.getLocation() != Location.GODWARS_DUNGEON)
											continue;
										if(p_.getPosition().getX() == x && p_.getPosition().getY() == y)
											p_.dealDamage(new Hit(150 + Misc.getRandom(110), Hitmask.RED, CombatIcon.NONE));
									}
								}
							}
						}
						if(ticks == 6) {
							attacks[1] = false;
							NEX.setChargingAttack(false);
							this.stop();
						}
						ticks++;
					}
				});
			}  else {
				if(p.getPosition().distanceToPoint(NEX.getPosition().getX(), NEX.getPosition().getY()) <= 2 && Misc.getRandom(1) == 0)
				{
					NEX.performAnimation(new Animation(6354));
					TaskManager.submit(new Task(1, NEX, false) {
						@Override
						public void execute() {
							new CombatHit(NEX.getCombatBuilder(), new CombatContainer(NEX, p, 1, CombatType.MELEE, true)).handleAttack();
							stop();
						}
					});
				} else {
					p.performGraphic(new Graphic(366));
					p.getMovementQueue().freeze(5);
					NEX.performAnimation(new Animation(6326));
					new CombatHit(NEX.getCombatBuilder(), new CombatContainer(NEX, p, 1, CombatType.MAGIC, true)).handleAttack();
				}
			}
		}
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return phase == 2 ? 2 : 8;
	}


	/** MISC **/

	public static void dealtDamage(final Player p, final int damage) {
		if(phase == 4)
		{
			if(prayerType == 0 && damage != 0)
			{
				new Projectile(NEX, p, 2263, 44, 3, 43, 43, 0).sendProjectile();
				TaskManager.submit(new Task(2, NEX, false) {
					@Override
					public void execute()
					{
						NEX.setConstitution(NEX.getConstitution() + (damage/5));
						p.getSkillManager().setCurrentLevel(Skill.PRAYER, p.getSkillManager().getCurrentLevel(Skill.PRAYER) - (damage/85));
						if(p.getSkillManager().getCurrentLevel(Skill.PRAYER) < 0)
							p.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
						p.performGraphic(new Graphic(2264));
						new Projectile(NEX, p, 2263, 44, 3, 43, 43, 0).sendProjectile();
						this.stop();
					}
				});
			}
		}
	}

	public static void takeDamage(Player from, int damage) {
		if(phase == 4 && damage > 0)
		{
			if(prayerType == 0)
			{
				NEX.setConstitution(NEX.getConstitution() + (damage/2));
			}
		}
		if(phase == 3)
		{
			if(NEX.getConstitution() <= 4000)
			{
				if(magesKilled[3]) {
					phase = 4;
					NEX.forceChat("NOW, THE POWER OF ZAROS!");
					zarosStage = true;
				}
				if(!magesAttackable[3]) {
					NEX.forceChat("Don't fail me, Glacies!");
					sendGlobalMsg(from, "@red@Glacies is now attackable! You need to defeat him to weaken Nex!");
					NEX.setConstitution(4000);
					magesAttackable[3] = true;
				}
				if(magesAttackable[3] && !magesKilled[3]) {
					NEX.setConstitution(4000);
					from.getPacketSender().sendMessage("You need to kill Glacies before being able to damage Nex further!");
					from.getCombatBuilder().reset(true);
				}
			}
		}
		if(phase == 2)
		{
			if(NEX.getConstitution() <= 8000)
			{
				if(magesKilled[2])
					phase = 3;
				if(!magesAttackable[2]) {
					NEX.forceChat("Don't fail me, Cruor!");
					sendGlobalMsg(from, "@red@Cruor is now attackable! You need to defeat him to weaken Nex!");
					NEX.setConstitution(8000);
					magesAttackable[2] = true;
				}
				if(magesAttackable[2] && !magesKilled[2]) {
					NEX.setConstitution(8000);
					from.getPacketSender().sendMessage("You need to kill Cruor before being able to damage Nex further!");
					from.getCombatBuilder().reset(true);
				}
			}
		}
		if(phase == 1)
		{
			if(NEX.getConstitution() <= 12000)
			{
				if(magesKilled[1])
					phase = 2;
				if(!magesAttackable[1]) {
					NEX.forceChat("Don't fail me, Umbra!");
					sendGlobalMsg(from, "@red@Umbra is now attackable! You need to defeat him to weaken Nex!");
					magesAttackable[1] = true;
				}
				if(magesAttackable[1] && !magesKilled[1]) {
					NEX.setConstitution(12000);
					from.getPacketSender().sendMessage("You need to kill Umbra before being able to damage Nex further!");
					from.getCombatBuilder().reset(true);
				}
			}
		}
		if(phase == 0)
		{
			if(NEX.getConstitution() <= 16000)
			{
				if(magesKilled[0])
					phase = 1;
				if(!magesAttackable[0]) {
					NEX.forceChat("Don't fail me, Fumus!");
					sendGlobalMsg(from, "@red@Fumus is now attackable! You need to defeat her to weaken Nex!");
					magesAttackable[0] = true;
				}
				if(magesAttackable[0] && !magesKilled[0]) {
					NEX.setConstitution(16000);
					from.getPacketSender().sendMessage("You need to kill Fumus before being able to damage Nex further!");
					from.getCombatBuilder().reset(true);
				}
			}
		}
	}

	public static void handleDeath() {
		phase = 0;
		despawn(false);
		NEX.forceChat("Taste my wrath!");
		final int x = NEX.getPosition().getX(), y = NEX.getPosition().getY();
		TaskManager.submit(new Task(4) {
			@Override
			public void execute() {
				for(Player p : World.getPlayers())
				{
					if(p == null)
						continue;
					if(p.getPosition().distanceToPoint(x, y) <= 3)
					{
						p.dealDamage(new Hit(150, Hitmask.RED, CombatIcon.NONE));
					}	
					if(p.getPosition().distanceToPoint(x, y) <= 20)
					{

						for(int x_ = x-2; x_ < x+2; x_++)
						{
							for(int y_ = y-2; y_ < y+2; y_++)
							{
								p.getPacketSender().sendGraphic(new Graphic(2259), new Position(x_, y));
							}
						}
					}
				}
				this.stop();
			}
		});
	}

	public static void sendGlobalMsg(Player original, String message) {
		for(Player p : Misc.getCombinedPlayerList(original)) {
			if(p != null) {
				p.getPacketSender().sendMessage(message);
			}
		}
	}

	public static boolean zarosStage() {
		return zarosStage;
	}

	public static boolean nexMob(int id) {
		return id == 13447 || nexMinion(id);
	}

	public static boolean nexMinion(int id) {
		return id >= 13451 && id <= 13454;
	}

	public static boolean checkAttack(Player p, int npc) {
		if(npc == 13447) {
			for(int i = 0; i < magesAttackable.length; i++) {
				if(magesAttackable[i] && !magesKilled[i]) {
					int index = 13451+i;
					p.getPacketSender().sendMessage("You need to kill "+NpcDefinition.forId(index).getName()+" before being able to damage Nex further!");
					return false;
				}
			}
			return true;
		}
		int index = npc - 13451;
		if(!magesAttackable[index] && !magesKilled[index]) {
			p.getPacketSender().sendMessage(""+NpcDefinition.forId(npc).getName()+" is currently being protected by Nex!");
			return false;
		}
		return true;
	}

	public static void cough(final Player p) {
		if (p.isCoughing())
			return;
		p.getPacketSender().sendMessage("You've been infected with a virus!");
		p.setCoughing(true);
		TaskManager.submit(new Task(1, p , false) {
			int ticks = 0;
			@Override
			public void execute() {
				if (ticks >= 5 || p.getConstitution() <= 0 ||p.getLocation() != Location.GODWARS_DUNGEON) {
					this.stop();
					return;
				}
				p.forceChat("Cough..");
				for(Skill skill : Skill.values()) {
					if(skill != Skill.CONSTITUTION && skill != Skill.PRAYER) {
						p.getSkillManager().setCurrentLevel(skill, p.getSkillManager().getCurrentLevel(skill)-1);
						if(p.getSkillManager().getCurrentLevel(skill) < 1)
							p.getSkillManager().setCurrentLevel(skill, 1);
					}
				}
				for (Player p2 : p.getLocalPlayers()) {
					if (p2 == null || p2 == p)
						continue;
					if (p2.getPosition().distanceToPoint(p.getPosition().getX(), p.getPosition().getY()) == 1 && p2.getConstitution() > 0 && p2.getLocation() == Location.GODWARS_DUNGEON) {
						cough(p2);
					}
				}
				ticks++;
			}

			@Override
			public void stop() {
				setEventRunning(false);
				p.setCoughing(false);
			}
		});
	}

	public static void setShadow(Player p, int shadow) {
		if(p.getShadowState() == shadow)
			return;
		p.setShadowState(shadow);
		p.getPacketSender().sendShadow().sendMessage("@whi@Nex calls upon shadows to endarken your vision!");
	}
	
	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}
