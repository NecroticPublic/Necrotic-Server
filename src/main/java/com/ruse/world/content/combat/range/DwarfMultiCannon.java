package com.ruse.world.content.combat.range;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.*;
import com.ruse.model.Locations.Location;
import com.ruse.util.Misc;
import com.ruse.world.content.Achievements;
import com.ruse.world.content.CustomObjects;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.Achievements.AchievementData;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.npc.NPCMovementCoordinator.CoordinateState;
import com.ruse.world.entity.impl.player.Player;

/**
 * Handles the Dwarf multi Cannon
 * @author Gabriel Hannason
 *
 */
public class DwarfMultiCannon {

	private static final int 
	/**Start of cannon object ids**/
	CANNON_BASE = 7, CANNON_STAND = 8,
	CANNON_BARRELS = 9, CANNON = 6,
	/**Start of cannon item ids**/ 
	CANNONBALL = 2, CANNON_BASE_ID = 6,
	CANNON_STAND_ID = 8, CANNON_BARRELS_ID = 10,
	CANNON_FURNACE_ID = 12;

	public static void setupCannon(final Player c) {
		if(!canSetupCannon(c))
			return;
		c.getMovementQueue().reset();
		c.setSettingUpCannon(true);
		c.getSkillManager().stopSkilling();
		final boolean movementLock = c.getMovementQueue().isLockMovement();
		c.getMovementQueue().setLockMovement(true);
		final GameObject object = new GameObject(CANNON_BASE, c.getPosition().copy());
		final GameObject object2 = new GameObject(CANNON_STAND, c.getPosition().copy());
		final GameObject object3 = new GameObject(CANNON_BARRELS, c.getPosition().copy());
		final GameObject object4 = new GameObject(CANNON, c.getPosition().copy());
		c.getInventory().delete(CANNON_FURNACE_ID, 1);
		c.getInventory().delete(CANNON_BARRELS_ID, 1);
		c.getInventory().delete(CANNON_STAND_ID, 1);
		c.getInventory().delete(CANNON_BASE_ID, 1);
		TaskManager.submit(new Task(2, c, true) {
			int setupTicks = 5;
			@Override
			public void execute() {
				switch(setupTicks) {

				case 5:
					c.performAnimation(new Animation(827));
					CustomObjects.spawnGlobalObject(object);
					break;

				case 4:
					c.performAnimation(new Animation(827));
					CustomObjects.deleteGlobalObject(object);
					CustomObjects.spawnGlobalObject(object2);
					break;

				case 3:
					c.performAnimation(new Animation(827));
					CustomObjects.deleteGlobalObject(object2);
					CustomObjects.spawnGlobalObject(object3);
					break;

				case 2:
					c.performAnimation(new Animation(827));
					CustomObjects.deleteGlobalObject(object3);
					DwarfCannon cannon = new DwarfCannon(c.getIndex(), object4);
					c.setCannon(cannon);
					CustomObjects.spawnGlobalObject(c.getCannon().getObject());
					break;

				case 1:
					setupTicks = 5;
					Achievements.finishAchievement(c, AchievementData.SET_UP_A_CANNON);
					c.setSettingUpCannon(false);
					stop();
					break;
				}
				setupTicks--;
			}

			@Override
			public void stop() {
				c.getMovementQueue().setLockMovement(movementLock);
				setEventRunning(false);
			}
		});
	}

	public static boolean canSetupCannon(Player c) {
		if(c.getSkillManager().getCurrentLevel(Skill.RANGED) < 75) {
			c.getPacketSender().sendMessage("You need a Ranged level of atleast 75 to setup the dwarf-cannon.");
			return false;
		}
		if (!c.getInventory().contains(CANNON_BASE_ID)
				|| !c.getInventory().contains(CANNON_STAND_ID)
				|| !c.getInventory().contains(CANNON_BARRELS_ID)
				|| !c.getInventory().contains(CANNON_FURNACE_ID)) {
			c.getPacketSender().sendMessage("You don't have the required items to setup the dwarf-cannon.");
			return false;
		}
		if(c.getCannon() != null) {
			c.getPacketSender().sendMessage("You can only have one dwarf-cannon setup at once.");
			return false;
		}
		if (c.getLocation() == Location.WILDERNESS || c.getLocation() == Location.DUEL_ARENA || c.getLocation() == Location.FREE_FOR_ALL_ARENA || c.getLocation() == Location.CORPOREAL_BEAST) {
			c.getPacketSender().sendMessage("You cannot setup a cannon here.");
			return false;
		}
		if(!c.getMovementQueue().canWalk(3, 3) || CustomObjects.objectExists(c.getPosition().copy()) || !c.getLocation().isCannonAllowed() || c.getPosition().getZ() != 0) {
			c.getPacketSender().sendMessage("The dwarf-cannon cannot be setup here. Try moving around a bit.");
			return false;
		}
		if(c.isSettingUpCannon() || c.getConstitution() <= 0 || c.getConstitution() <= 0)
			return false;
		c.getPacketSender().sendMessage("The animations are broken, but the cannon works");
		return true;
	}

	public static void pickupCannon(Player c, DwarfCannon cannon, boolean force) {
		if(c.isSettingUpCannon())
			return;
		boolean deleted = false;
		boolean addCannonballs = cannon.getCannonballs() > 0;
		if(force) { //Logout
			if(c.getInventory().getFreeSlots() >= 5) {
				c.getInventory().add(CANNON_BASE_ID, 1);
				c.getInventory().add(CANNON_STAND_ID, 1);
				c.getInventory().add(CANNON_BARRELS_ID, 1);
				c.getInventory().add(CANNON_FURNACE_ID, 1);
				if(addCannonballs)
					c.getInventory().add(CANNONBALL, cannon.getCannonballs());
				deleted = true;
			} else {
				c.getBank(c.getCurrentBankTab()).add(CANNON_BASE_ID, 1);
				c.getBank(c.getCurrentBankTab()).add(CANNON_STAND_ID, 1);
				c.getBank(c.getCurrentBankTab()).add(CANNON_BARRELS_ID, 1);
				c.getBank(c.getCurrentBankTab()).add(CANNON_FURNACE_ID, 1);
				if(addCannonballs)
					c.getBank(c.getCurrentBankTab()).add(CANNONBALL, cannon.getCannonballs());
				deleted = true;
			}
		} else {
			if(c.getInventory().getFreeSlots() >= 5) {
				c.getInventory().add(CANNON_BASE_ID, 1);
				c.getInventory().add(CANNON_STAND_ID, 1);
				c.getInventory().add(CANNON_BARRELS_ID, 1);
				c.getInventory().add(CANNON_FURNACE_ID, 1);
				if(addCannonballs)
					c.getInventory().add(CANNONBALL, cannon.getCannonballs());
				deleted = true;
			} else {
				c.getPacketSender().sendMessage("You don't have enough free inventory space.");
				deleted = false;
			}
		}
		if(deleted) {
			cannon.setCannonballs(0);
			cannon.setCannonFiring(false);
			cannon.setRotations(0);
			CustomObjects.deleteGlobalObject(c.getCannon().getObject());
			c.setCannon(null).setSettingUpCannon(false);
		}
	}

	public static void startFiringCannon(Player c, DwarfCannon cannon) {
		if(cannon.cannonFiring() && cannon.getCannonballs() > 15) {
			c.getPacketSender().sendMessage("Your cannon is already firing.");
			return;
		}
		if(cannon.getCannonballs() <= 15) {
			int playerCannonballs = c.getInventory().getAmount(2) > 30 ? 30: c.getInventory().getAmount(2);
			int cannonballsToAdd = playerCannonballs - cannon.getCannonballs();
			if(playerCannonballs < 1) {
				c.getPacketSender().sendMessage("You do not have any cannonballs in your inventory to fire the cannon with.");
				return;
			}
			c.getInventory().delete(CANNONBALL, cannonballsToAdd);
			cannon.setCannonballs(cannonballsToAdd);
			if(!cannon.cannonFiring())
				fireCannon(c, cannon);
		}
	}

	public static void fireCannon(final Player c, final DwarfCannon cannon) {
		if(cannon.cannonFiring())
			return;
		TaskManager.submit(new Task(1, c, true) {
			@Override
			public void execute() {
				if(c.getCannon() == null) {
					this.stop();
					return;
				}
				if(cannon.getCannonballs() > 0) {
					rotateCannon(c, cannon);
					cannon.setCannonFiring(true);
					attack(c);
				} else {
					c.getPacketSender().sendMessage("Your cannon has run out of cannonballs.");
					cannon.setCannonballs(0);
					cannon.setCannonFiring(false);
					cannon.setRotations(0);
					this.stop();
				}
			}

		});
	}

	private static void rotateCannon(final Player c, DwarfCannon cannon) {
		final GameObject object = c.getCannon().getObject();
		cannon.addRotation(1);
		switch(cannon.getRotations()) {
		case 1: // north 
	//		object.performAnimation(new Animation(516));
			break;
		case 2: // north-east
		//	object.performAnimation(new Animation(517));
			break;
		case 3: // east
		//	object.performAnimation(new Animation(518));
			break;
		case 4: // south-east
		//	object.performAnimation(new Animation(519));
			break;
		case 5: // south
		//	object.performAnimation(new Animation(520));
			break;
		case 6: // south-west
		//	object.performAnimation(new Animation(521));
			break;
		case 7: // west
		//	object.performAnimation(new Animation(514));
			break;
		case 8: // north-west
		//	object.performAnimation(new Animation(515));
			cannon.setRotations(0);
			break;
		}
		Sounds.sendGlobalSound(c, Sound.ROTATING_CANNON);
	}

	public static void attack(Player player) { //TODO: Bad hardcoding of setting damage, switch to combathittask 
		DwarfCannon cannon = player.getCannon();
		if(cannon == null)
			return;
		NPC n = getTarget(player, cannon);
		if(n == null)
			return;
		Hit dmg = new Hit(Misc.getRandom(266) - Misc.getRandom(n.getDefinition().getDefenceRange()), Hitmask.RED, CombatIcon.RANGED);
		new Projectile(cannon.getObject(), n, 53, 44, 3, 43, 31, 0).sendProjectile();
		n.dealDamage(dmg);
		n.getLastCombat().reset();
		n.performAnimation(new Animation(n.getDefinition().getDefenceAnimation()));
		n.getCombatBuilder().addDamage(player, dmg.getDamage());
		Achievements.doProgress(player, AchievementData.DEAL_EASY_DAMAGE_USING_RANGED, dmg.getDamage());
		Achievements.doProgress(player, AchievementData.DEAL_MEDIUM_DAMAGE_USING_RANGED, dmg.getDamage());
		Achievements.doProgress(player, AchievementData.DEAL_HARD_DAMAGE_USING_RANGED, dmg.getDamage());
		player.getSkillManager().addExperience(Skill.RANGED, (int) (((dmg.getDamage() * .50))));
		player.getSkillManager().addExperience(Skill.CONSTITUTION, (int) ((dmg.getDamage() * .30)));
		if(!n.getCombatBuilder().isAttacking()) {
			if(n.getMovementCoordinator().getCoordinateState() == CoordinateState.HOME)
				n.getCombatBuilder().attack(player);
		}
		cannon.setCannonballs(cannon.getCannonballs() - 1);
		if(cannon.getCannonballs() == 15) {
			player.getPacketSender().sendMessage(MessageType.NPC_ALERT, "Your cannon has " + cannon.getCannonballs() + " Cannonballs left.");
		}
		if(cannon.getCannonballs() == 5) {
			player.getPacketSender().sendMessage(MessageType.NPC_ALERT, "Your cannon has " + cannon.getCannonballs() + " Cannonballs left.");
		}
		Achievements.doProgress(player, AchievementData.FIRE_500_CANNON_BALLS);
		Achievements.doProgress(player, AchievementData.FIRE_5000_CANNON_BALLS);
		Sounds.sendGlobalSound(player, Sound.FIRING_CANNON);
	}

	private static NPC getTarget(Player p, DwarfCannon cannon) {
		for(NPC n : p.getLocalNpcs()) {
			if(n == null)
				continue;
			if(n.isSummoningNpc())
				continue;
			if(!n.getLocation().isCannonAllowed())
				continue;
			int myX = cannon.getObject().getPosition().getX();
			int myY = cannon.getObject().getPosition().getY();
			int theirX = n.getPosition().getX(); 
			int theirY = n.getPosition().getY();
			if (n.getDefinition().isAttackable() && n.getConstitution() > 0 && Locations.goodDistance(cannon.getObject().getPosition(), n.getPosition(), 8)) {
				if(!Location.inMulti(n) && n.getCombatBuilder().isBeingAttacked() && n.getCombatBuilder().getLastAttacker() != p) {
					continue;
				}
				if(n.getDefinition().getName().contains("Revenant")) {
					continue;
				}
				switch (cannon.getRotations()) {
				case 1: // north
					if (theirY > myY && theirX >= myX - 1
					&& theirX <= myX + 1)
						return n;
					break;
				case 2: // north-east
					if (theirX >= myX + 1 && theirY >= myY + 1)
						return n;
					break;
				case 3: // east
					if (theirX > myX && theirY >= myY - 1
					&& theirY <= myY + 1)
						return n;
					break;
				case 4: // south-east
					if (theirY <= myY - 1 && theirX >= myX + 1)
						return n;
					break;
				case 5: // south
					if (theirY < myY && theirX >= myX - 1
					&& theirX <= myX + 1)
						return n;
					break;
				case 6: // south-west
					if (theirX <= myX - 1 && theirY <= myY - 1)
						return n;
					break;
				case 7: // west
					if (theirX < myX && theirY >= myY - 1
					&& theirY <= myY + 1)
						return n;
					break;
				case 8: // north-west
					if (theirX <= myX - 1 && theirY >= myY + 1)
						return n;
					break;
				}
			}
		}
		return null;
	}
}
