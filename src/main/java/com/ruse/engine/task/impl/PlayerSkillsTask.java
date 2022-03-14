package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.model.Skill;
import com.ruse.world.content.BonusManager;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.entity.impl.player.Player;

/**
 * Represents a player's skill normalizing task, this handles the skills'
 * current level to set to their maximum level, whether it's done through
 * decreasing or increasing their current level.
 * 
 * Redone, now has support for the berserk curse
 * @author Gabriel Hannason
 */

public class PlayerSkillsTask extends Task {

	/*
	 * The task's delay
	 */
	private static final int TIME_WITHOUT_BERSERKER_CURSE = 130, TIME_WITH_BERSERKER_CURSE = 150;
	

	/**
	 * The PlayerSkillsTask constructor.
	 * @param player	The player to normalize skills for.
	 */
	public PlayerSkillsTask(Player player) {
		super(player.getCurseActive()[CurseHandler.BERSERKER] ? TIME_WITH_BERSERKER_CURSE : TIME_WITHOUT_BERSERKER_CURSE, player, false);
		this.player = player;
	}

	/**
	 * The player associated with this task.
	 */
	private Player player;

	@Override
	public void execute() {
		if (player == null || !player.isRegistered()) {
			stop();
			return;
		}
		if(getDelay() == TIME_WITHOUT_BERSERKER_CURSE && player.getCurseActive()[CurseHandler.BERSERKER] || !player.getCurseActive()[CurseHandler.BERSERKER] && getDelay() == TIME_WITH_BERSERKER_CURSE)
			setDelay(player.getCurseActive()[CurseHandler.BERSERKER] ? TIME_WITH_BERSERKER_CURSE : TIME_WITHOUT_BERSERKER_CURSE);
		if(!player.isDying() && player.getConstitution() > 0 && player.getOverloadPotionTimer() == 0) {
			for (Skill skill : Skill.values()) {
				if (skill == Skill.CONSTITUTION || skill == Skill.PRAYER || skill == Skill.SUMMONING)
					continue;
				if (player.getSkillManager().getCurrentLevel(skill) != player.getSkillManager().getMaxLevel(skill)) {
					int difference = player.getSkillManager().getCurrentLevel(skill) - player.getSkillManager().getMaxLevel(skill);
					int toRestore = (difference > 0 ? (player.getSkillManager().getCurrentLevel(skill) - 1) : player.getSkillManager().getCurrentLevel(skill) + 1);
					player.getSkillManager().setCurrentLevel(skill, toRestore);
				}
			}
		}
		for(int i = 0; i < player.getLeechedBonuses().length; i++) {
			if(player.getLeechedBonuses()[i] > 0) {
				player.getLeechedBonuses()[i] -= 1;
				BonusManager.sendCurseBonuses(player);
			}
		}
	}
}
