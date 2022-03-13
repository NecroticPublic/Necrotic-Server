package com.ruse.world.content.combat.magic;

import com.ruse.model.Skill;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.entity.impl.player.Player;

public class Autocasting {

	private static int[] spellIds = { 1152, 1154, 1156, 1158, 1160, 1163, 1166, 1169,
		1171, 1172, 1175, 1177, 1181, 1183, 1185, 1188, 1189, 1539, 12037, 1190, 1191, 1192, 12939,
		12987, 12901, 12861, 12963, 13011, 13023, 12919, 12881, 12951, 12999, 12911,
		12871, 12975, 12929, 12891, 21744, 22168, 21745, 21746, 50163,
		50211, 50119, 50081, 50151, 50199, 50111, 50071, 50175, 50223,
		50129, 50091 };

	public static boolean handleAutocast(final Player p, int actionButtonId) {
		switch(actionButtonId) {
		case 6666:
			resetAutocast(p, true);
			return true;
		case 6667:
			resetAutocast(p, false);
			return true;
		}		
		for (int i = 0; i < spellIds.length; i++) {
			if (actionButtonId == spellIds[i]) {
				CombatSpell cbSpell = CombatSpells.getSpell(actionButtonId);
				if(cbSpell == null) {
					p.getMovementQueue().reset();
					return true;
				}
				if(cbSpell.levelRequired() > p.getSkillManager().getCurrentLevel(Skill.MAGIC)) {
					p.getPacketSender().sendMessage("You need a Magic level of at least "+cbSpell.levelRequired()+" to cast this spell.");
					resetAutocast(p, true);
					return true;
				}
				if (p.isSpecialActivated()) {
					p.setSpecialActivated(false);
					CombatSpecial.updateBar(p);
				}
				p.setAutocast(true);
				p.setAutocastSpell(cbSpell);
				p.setCastSpell(cbSpell);
				p.getPacketSender().sendAutocastId(p.getAutocastSpell().spellId());
				p.getPacketSender().sendConfig(108, 1);
				return true;
			}
		}
		return false;
	}
	
	public static void onLogin(Player player) {
		if(player.getAutocastSpell() == null || !player.isAutocast())
			resetAutocast(player, true);
		else {
			player.getPacketSender().sendAutocastId(player.getAutocastSpell().spellId());
		}
	}
	
	public static void resetAutocast(Player p, boolean clientReset) {
		if(clientReset)
			p.getPacketSender().sendAutocastId(-1);
		p.setAutocast(false);
		p.setAutocastSpell(null);
		p.setCastSpell(null);
		p.getPacketSender().sendConfig(108, 3);
	}
}
