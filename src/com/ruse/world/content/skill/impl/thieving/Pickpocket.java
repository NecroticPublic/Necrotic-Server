package com.ruse.world.content.skill.impl.thieving;

import com.ruse.model.Animation;
import com.ruse.model.Flag;
import com.ruse.model.Graphic;
import com.ruse.model.Hit;
import com.ruse.model.Skill;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;


public class Pickpocket {
	
	/**
	 * Use this method when an NPC is pickpocketed.
	 * @author Crimson 
	 * @since Aug 12, 2017
	 * @param player - the person trying to steal
	 * @param npc - the npc being stolen from
	 */
	public static void handleNpc(Player player, NPC npc) {
		PickpocketData data = PickpocketData.forNpc(npc.getId());
		
		player.setPositionToFace(npc.getPosition());
				
		if(player.isFrozen() || player.isStunned()) {
			return;
		}
		
		if (player.getCombatBuilder().isAttacking() || player.getCombatBuilder().isBeingAttacked()) {
			return;
		}
		
		if (player.getSkillManager().getMaxLevel(Skill.THIEVING) < data.getRequirement()) {
			player.getPacketSender().sendMessage("You need a thieving level of "+data.getRequirement()+" to steal from there.");
			return;
		}
		
		if (player.getInventory().isFull()) {
			player.getPacketSender().sendMessage("You need some inventory space to hold anything more.");
			return;
		}
		
		player.performAnimation(new Animation(881));
		
		if (shouldFail(player, data.getRequirement())) {
			
			player.getMovementQueue().stun(5);
			
			npc.forceChat(data.getFailMessage());
			npc.setPositionToFace(player.getPosition());
			if (npc.getDefinition().getAttackAnimation() > 0) {
				npc.performAnimation(new Animation(npc.getDefinition().getAttackAnimation()));
			} else {
				npc.performAnimation(new Animation(422)); //punch anim
			}
			player.performGraphic(new Graphic(254));
			
			player.dealDamage(new Hit(data.getDamage()));
			player.getCombatBuilder().addDamage(player, data.getDamage());
			player.getUpdateFlag().isUpdateRequired();
			player.getUpdateFlag().flag(Flag.SINGLE_HIT);
			
			
			return;
		}
		
		player.getInventory().add(data.getReward());
		player.getSkillManager().addExperience(Skill.THIEVING, data.getExperience());
		player.getPacketSender().sendMessage("You steal from the "+npc.getDefinition().getName()+"'s pocket.");
		
	}
	
	public static boolean shouldFail(Player player, int levelReq) {
        return player.getSkillManager().getCurrentLevel(Skill.THIEVING) - levelReq < Misc.getRandom(levelReq);
    }
	
}

