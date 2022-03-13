package com.ruse.world.content.dialogue.impl;

import com.ruse.model.Difficulty;
import com.ruse.model.Skill;
import com.ruse.model.input.impl.BuyAgilityExperience;
import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.entity.impl.player.Player;

public class AgilityTicketExchange {

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
				return 437;
			}
			
			@Override
			public String[] dialogue() {
				return new String[]{"@bla@How many tickets would you like to exchange", "for experience? One ticket currently grants", "@red@"+BuyAgilityExperience.experience*Difficulty.getDifficultyModifier(player, Skill.AGILITY)+"@bla@ Agility experience."};
			}
			
			public Dialogue nextDialogue() {
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
						return 437;
					}
					
					@Override
					public String[] dialogue() {
						return new String[]{"@bla@How many tickets would you like to exchange", "for experience? One ticket currently grants", +BuyAgilityExperience.experience*Difficulty.getDifficultyModifier(player, Skill.AGILITY)+"@bla@ Agility experience."};
					}
				
					@Override
					public void specialAction() {
						player.getPacketSender().sendInterfaceRemoval();
						player.setInputHandling(new BuyAgilityExperience());
						player.getPacketSender().sendEnterAmountPrompt("How many tickets would you like to exchange?");
					}
				};
				
			}
		};
	}
	
}
