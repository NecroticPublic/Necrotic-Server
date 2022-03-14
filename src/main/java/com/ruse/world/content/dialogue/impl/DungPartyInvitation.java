package com.ruse.world.content.dialogue.impl;

import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.entity.impl.player.Player;

/**
 * Represents a Dungeoneering party invitation dialogue
 * 
 * @author Gabriel Hannason
 */

public class DungPartyInvitation extends Dialogue {

	public DungPartyInvitation(Player inviter, Player p) {
		this.inviter = inviter;
		this.p = p;
	}
/*
	public static Dialogue getDialogue(Player player) {
		return new Dialogue() {
			@Override
			public DialogueType type() {
				return DialogueType.STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return null;
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						"Who would you like to invite?"

				};

			}
		};
	}*/

	private Player inviter, p;
	
	@Override
	public DialogueType type() {
		return DialogueType.STATEMENT;
	}

	@Override
	public DialogueExpression animation() {
		return null;
	}

	@Override
	public String[] dialogue() {
		return new String[] {
			inviter.getUsername()+" has invited you to join their Dungeoneering party."
		};
	}
	
	@Override
	public int npcId() {
		return -1;
	}
	
	@Override
	public Dialogue nextDialogue() {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.OPTION;
			}

			@Override
			public DialogueExpression animation() {
				return null;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"Join "+inviter.getUsername()+"'s party", "Don't join "+inviter.getUsername()+"'s party."};
			}
			
			@Override
			public void specialAction() {
				p.getMinigameAttributes().getDungeoneeringAttributes().setPartyInvitation(inviter.getMinigameAttributes().getDungeoneeringAttributes().getParty());
				p.setDialogueActionId(66);
			}
			
		};
	}
}