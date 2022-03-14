package com.ruse.world.content.dialogue.impl;

import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.entity.impl.player.Player;

public class Mandrith {

	public static Dialogue getDialogue(Player player) {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}

			@Override
			public int npcId() {
				return 8725;
			}

			@Override
			public String[] dialogue() {
				String KDR = "N/A";
				int kc = player.getPlayerKillingAttributes().getPlayerKills();
				int dc = player.getPlayerKillingAttributes().getPlayerDeaths();
				if(kc >= 5 && dc >= 5) {
					KDR = String.valueOf((double) (kc/dc));
				}
				return new String[]{"You have killed "+player.getPlayerKillingAttributes().getPlayerKills()+" players. You have died "+player.getPlayerKillingAttributes().getPlayerDeaths()+" times.", "You currently have a killstreak of "+player.getPlayerKillingAttributes().getPlayerKillStreak()+" and your", "KDR is currently "+KDR+"."};
			}

			public Dialogue nextDialogue() {
				return DialogueManager.getDialogues().get(19);
			}
		};
	}
}
