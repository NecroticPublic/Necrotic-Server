package com.ruse.world.content.skill.impl.hunter;

import com.ruse.model.Skill;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.npc.NPC;

/**
 * 
 * @author Rene
 */
public class TrapExecution {

	/**
	 * Handles Trap's with a state of 'set'
	 * 
	 * @param trap
	 */
	public static void setTrapProcess(Trap trap) {
		for (final NPC npc : Hunter.HUNTER_NPC_LIST) {
			if (npc == null || !npc.isVisible()) {
				continue;
			}
			if(trap instanceof BoxTrap && npc.getId() != 5079 && npc.getId() != 5080)
				continue;
			if(trap instanceof SnareTrap && (npc.getId() == 5079 || npc.getId() == 5080))
				continue;
			if (npc.getPosition().isWithinDistance(trap.getGameObject().getPosition(), 1)) {
				if (Misc.getRandom(100) < successFormula(trap, npc)) {
					Hunter.catchNPC(trap, npc);
					return;
				}
			}
		}
	}

	public static int successFormula(Trap trap, NPC npc) {
		if (trap.getOwner() == null)
			return 0;
		int chance = 70;
		if (Hunter.hasLarupia(trap.getOwner()))
			chance = chance + 10;
		chance = chance
				+ (int) (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) / 1.5)
				+ 10;

		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 25)
			chance = (int) (chance * 1.5) + 8;
		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 40)
			chance = (int) (chance * 1.4) + 3;
		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 50)
			chance = (int) (chance * 1.3) + 1;
		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 55)
			chance = (int) (chance * 1.2);
		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 60)
			chance = (int) (chance * 1.1);
		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 65)
			chance = (int) (chance * 1.05) + 3;

		return chance;
	}

	/**
	 * Handles the cycle management of each traps timer
	 * 
	 * @param trap
	 *            is the given trap we are managing
	 * @return false if the trap is too new to have caught
	 */
	public static boolean trapTimerManagement(Trap trap) {
		if (trap.getTicks() > 0)
			trap.setTicks(trap.getTicks() - 1);
		if (trap.getTicks() <= 0) {
			Hunter.deregister(trap);
			if (trap.getOwner() != null)
				trap
				.getOwner()
				.getPacketSender()
				.sendMessage(
						"You left your trap for too long, and it collapsed.");
		}
		return true;
	}
}
