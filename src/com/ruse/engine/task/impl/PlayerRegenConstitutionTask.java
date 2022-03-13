package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.model.Locations.Location;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Equipment;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.entity.impl.player.Player;

public class PlayerRegenConstitutionTask extends Task {
	
	public static final int TIMER = 101;
	
	public PlayerRegenConstitutionTask(Player player) {
		super(TIMER, player, false);
		this.player = player;
	}
	
	private Player player;
	
	@Override
	public void execute() {
		if (player == null || !player.isRegistered()) {
			stop();
			return;
		}
		if (player.getConstitution() == 0 || player.isDying() || player.getLocation() == Location.DUEL_ARENA) {
			return;
		}
		
		//int max = player.getSkillManager().getMaxLevel(Skill.CONSTITUTION);
		//int current = player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION);
		int heal = 10;
		
		if (player.getEquipment().getItems()[Equipment.HANDS_SLOT].getId() == 11133) //regen bracelet
			heal *= 10;
		if (player.getSkillManager().skillCape(Skill.CONSTITUTION) || PrayerHandler.isActivated(player, PrayerHandler.RAPID_HEAL))
			heal *= 2;
		
		player.heal(heal);
		
		/*if (current < max) {
			
			if (current + heal > max) {
				player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, max);
			} else {
				player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, current+heal);
			}
			
		}*/
		
	}

}
