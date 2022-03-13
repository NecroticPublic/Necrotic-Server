package com.ruse.world.content.skill.impl.summoning;

import java.util.concurrent.TimeUnit;

import com.ruse.util.Misc;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.player.Player;

public class SummoningTab {
	
	public static void handleDismiss(Player c, boolean dismiss) {
		if(!dismiss && c.busy()) {
			c.getPacketSender().sendMessage("Please finish what you're doing first.");
			return;
		}
		c.getPacketSender().sendInterfaceRemoval();
		if (c.getSummoning().getFamiliar() == null) {
			c.getPacketSender().sendMessage("You don't have a familiar to dismiss.");
			return;
		}
		if(dismiss) {
			if(c.getSummoning().getFamiliar() != null) {
				c.getSummoning().unsummon(true, true);
				c.getPacketSender().sendMessage("You've dismissed your familiar.");
			} else {
				c.getPacketSender().sendMessage("You don't have a familiar to dismiss.");
			}
		} else {
			DialogueManager.start(c, 4);
			c.setDialogueActionId(4);
		}
	}
	
	public static void handleSpecialAttack(Player c) {
		/*if(c.getSkills().getSummoning().getFamiliar() == null) {
			c.getPacketSender().sendMessage("You do not have a familiar.");
			return;
		}
		if(SummoningData.calculateScrolls(c) == 0) {
			c.getPacketSender().sendMessage("You do not have any scrolls to use.");
			return;
		}
		if(System.currentTimeMillis() - c.summonSpecDelay < 30000) {
			c.getPacketSender().sendMessage("You must wait another "+(int)((30 - (System.currentTimeMillis() - c.summonSpecDelay) * 0.001))+" seconds before being able to do this again.");
			return;
		}
		FamiliarData fam = Summoning.FamiliarData.forNPCId(c.getSkills().getSummoning().getFamiliar().getSummonNpc().getId());
		int scrollId = fam.scroll;
		NPC follower = c.getSkills().getSummoning().getFamiliar().getSummonNpc();
		GameCharacter enemy = c.getFields().getCombatAttributes().enemy;
		if(enemy == null)
			enemy = c.getFields().getCombatAttributes().lastAttacker;
		@SuppressWarnings("unused")
		boolean inCB = c.getFields().getCombatAttributes().attacking;
		boolean inM = Locations.inMulti(c);
		boolean didNothing = true;
		if(fam == Summoning.FamiliarData.SPIRIT_WOLF) {
			if(!inM) {
				c.getPacketSender().sendMessage("You must be in a multi-combat-area to use this scroll.");
				return;
			}
			if(enemy == null) {
				c.getPacketSender().sendMessage("This scroll can only be cast in Combat.");
				return;
			}
			c.getPacketSender().sendProjectile(new Projectile(follower.getPosition(), enemy.getPosition(), new Graphic(1333), 6, 3, 10), enemy);
			c.getInventory().delete(scrollId, 1);
			enemy.setDamage(new Damage(new Hit(Misc.getRandom(40), CombatIcon.MAGIC, Hitmask.RED)));
			c.getSkills().getSummoning().getFamiliar().getSummonNpc().getFields().getCombatAttributes().setAttackDelay(5);
			enemy.getMovementQueue().walkStep(-6, 0, true);
			didNothing = false;
		}
		if(fam == Summoning.FamiliarData.DREADFOWL) {
			if(!inM) {
				c.getPacketSender().sendMessage("You must be in a multi-combat-area to use this scroll.");
				return;
			}
			if(enemy == null) {
				c.getPacketSender().sendMessage("This scroll can only be cast in Combat.");
				return;
			}
			c.getInventory().delete(scrollId, 1);
			follower.setPositionToFace(enemy.getPosition());
			follower.performGraphic(new Graphic(1323));
			enemy.setDamage(new Damage(new Hit(Misc.getRandom(30), CombatIcon.MAGIC, Hitmask.RED)));
			enemy.performGraphic(new Graphic(1325));
			follower.setPositionToFace(c.getPosition());
			didNothing = false;
		}
		if(fam == Summoning.FamiliarData.SPIRIT_SPIDER) {
			c.getInventory().delete(scrollId, 1);
			Position randomSpawn1 = new Position(c.getPosition().getX() -1, c.getPosition().getY() - 1, c.getPosition().getZ());
			Position randomSpawn2 = new Position(c.getPosition().getX() + 1, c.getPosition().getY() + 1, c.getPosition().getZ());
			Position randomSpawn3 = new Position(c.getPosition().getX(), c.getPosition().getY() + 1, c.getPosition().getZ());
			Position randomSpawn4 = new Position(c.getPosition().getX() + 1, c.getPosition().getY(), c.getPosition().getZ());
			if(Misc.getRandom(2) == 1) {
				c.getPacketSender().sendGraphic(new Graphic(1342), randomSpawn1);
				c.getPacketSender().sendGraphic(new Graphic(1342), randomSpawn2);
				GroundItemManager.spawnGroundItem(c, new GroundItem(new Item(223), randomSpawn1, c.getUsername(), false, 80, true, 80));
				GroundItemManager.spawnGroundItem(c, new GroundItem(new Item(223), randomSpawn2, c.getUsername(), false, 80, true, 80));
			} else if(Misc.getRandom(3) == 2) {
				c.getPacketSender().sendGraphic(new Graphic(1342), randomSpawn3);
				c.getPacketSender().sendGraphic(new Graphic(1342), randomSpawn4);
				GroundItemManager.spawnGroundItem(c, new GroundItem(new Item(223), randomSpawn3, c.getUsername(), false, 80, true, 80));
				GroundItemManager.spawnGroundItem(c, new GroundItem(new Item(223), randomSpawn4, c.getUsername(), false, 80, true, 80));
			}
			didNothing = false;
		}
		if(fam == Summoning.FamiliarData.THORNY_SNAIL) {
			didNothing = false;
			c.getInventory().delete(scrollId, 1);
			enemy.setDamage(new Damage(new Hit(Misc.getRandom(40), CombatIcon.MAGIC, Hitmask.RED)));
			enemy.performGraphic(new Graphic(73, GraphicHeight.HIGH));
			c.getSkills().getSummoning().getFamiliar().getSummonNpc().getFields().getCombatAttributes().setAttackDelay(5);
		}
		if(fam == Summoning.FamiliarData.SPIRIT_TERRORBIRD) {
			c.getInventory().delete(scrollId, 1);
			follower.performGraphic(new Graphic(1521));
			c.performGraphic(new Graphic(1300));
			c.getFields().setRunEnergy(100);
			c.getPacketSender().sendMessage("Your familiar boosts your energy.");
			didNothing = false;
		}
		if(fam == Summoning.FamiliarData.WAR_TORTOISE) {
			c.getInventory().delete(scrollId, 1);
			follower.performGraphic(new Graphic(1414));
			c.getSkillManager().setCurrentLevel(Skill.DEFENCE, c.getSkillManager().getCurrentLevel(Skill.DEFENCE) + 7);
			if(c.getSkillManager().getCurrentLevel(Skill.DEFENCE) >= 125)
				c.getSkillManager().setCurrentLevel(Skill.DEFENCE, 120);
			c.getPacketSender().sendMessage("Your familiar boosts your Defence level.");
			didNothing = false;
		}
		if(fam.name().contains("TITAN")) {
			if(enemy == null) {
				c.getPacketSender().sendMessage("To use this scroll, you must be in multi-zone while attacking.");
				return;
			}
			c.getInventory().delete(scrollId, 1);
			follower.performGraphic(new Graphic(1449));
			enemy.setDamage(new Damage(new Hit(Misc.getRandom(250), CombatIcon.MAGIC, Hitmask.CRITICAL)));
			didNothing = false;
		}
		if(didNothing) {
			c.getPacketSender().sendMessage("This familiar does currently not have a special attack added.");
			return;
		}
		c.performAnimation(new Animation(7660));
		c.performGraphic(new Graphic(1316));
		c.summonSpecDelay = System.currentTimeMillis();*/
	}
	
	public static void callFollower(final Player c) {
		if(c.getSummoning().getFamiliar() != null && c.getSummoning().getFamiliar().getSummonNpc() != null) {
			if(!c.getLastSummon().elapsed(30000)) {
				c.getPacketSender().sendMessage("You must wait another "+Misc.getTimeLeft(c.getLastSummon().elapsed(), 30, TimeUnit.SECONDS)+" seconds before being able to do this again.");
				return;
			}
			c.getSummoning().moveFollower(false);
		} else {
			c.getPacketSender().sendMessage("You don't have a familiar to call.");
		}
	}

	public static void renewFamiliar(Player c) {
		if(c.getSummoning().getFamiliar() != null) {
			int pouchRequired = FamiliarData.forNPCId(c.getSummoning().getFamiliar().getSummonNpc().getId()).getPouchId();
			if(c.getInventory().contains(pouchRequired)) {
				c.getSummoning().summon(FamiliarData.forNPCId(c.getSummoning().getFamiliar().getSummonNpc().getId()), true, false);
			} else {
				c.getPacketSender().sendMessage("You don't have the pouch required to renew this familiar.");
			}
		} else {
			c.getPacketSender().sendMessage("You don't have a familiar to renew.");
		}
	}
}
