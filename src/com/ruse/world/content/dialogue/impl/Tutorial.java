package com.ruse.world.content.dialogue.impl;

import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Direction;
import com.ruse.model.GameMode;
import com.ruse.model.Position;
import com.ruse.net.security.ConnectionHandler;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.entity.impl.player.Player;

/**
 * Represents a Dungeoneering party invitation dialogue
 * 
 * @author Gabriel Hannason
 */

public class Tutorial {

	public static Dialogue get(Player p, int stage) {
		Dialogue dialogue = null;
		switch(stage) {
		case -2:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"Great! Now you need to choose a difficulty.", "This is a multiplier applied to all EXP you gain.", "It can make the game easier, or harder.", "Be sure though, as difficulties can't be changed."};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case -1:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.OPTION;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"Easy (25% faster)", "Regular (base)", "Hard (25% slower)", "Extreme (50% slower)", "More info..."};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public Dialogue nextDialogue() {
					return null;
				}
				
				@Override
				public void specialAction() {
					p.setDialogueActionId(87);
				}
			};
			break;
		case 0:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"You're "+Misc.anOrA(p.getGameMode().toString())+" "+Misc.capitalizeString(Misc.formatText(p.getGameMode().toString()))+" mode character. ", "You're playing on the "+Misc.capitalizeString(Misc.formatText(p.getDifficulty().toString()))+" difficulty!", GameSettings.RSPS_NAME+" is yours to explore.", "This is your adventure, @red@"+p.getUsername()+"@bla@. Embrace it!"};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, 13);//stage + 1);
				}
			};
			break;
		case 1:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"You can earn money doing many different things in", GameSettings.RSPS_NAME+". For example, see those Thieving stalls infront of ", "you? You can steal items from them and sell them to the", "merchant who is standing over there."};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3671, 2973, 4));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 2:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"You can sell your stolen goods to the Merchant!"};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3677, 2973, 4));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 3:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.CONFUSED;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"Or you can trade other players in the ::trade area!"};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3164, 3485, 4));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 4:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"The next important thing you need to learn is navigating.", "All important teleports can be found at the top of the", "Spellbook. Take a look, I've opened it for you!"};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3676, 2981, 4));
					p.setDirection(Direction.SOUTH);
					p.getPacketSender().sendTab(GameSettings.MAGIC_TAB);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 5:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"If you wish to navigate to a skill's training location,", "simply press the on the respective skill in the skill tab."};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.getPacketSender().sendTab(GameSettings.SKILLS_TAB);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 6:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"Enough of the boring stuff, let's show you some creatures!", "There are a bunch of bosses to fight in "+GameSettings.RSPS_NAME+".", "Every boss drops unique and good gear when killed.", "One example is the mighty Pohenix!"};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(2833, 9560));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 7:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"Ah.. The Ghost Town..", "Here, you can find a bunch of revenants.", "You can also fight other players."};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3652, 3486));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 8:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{GameSettings.RSPS_NAME+" also has a lot of enjoyable minigames.", "This is the Graveyard Arena, an area that's been run over", "by Zombies. Your job is to simply to kill them all.", "Sounds like fun, don't you think?"};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3503, 3569));
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 9:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"This is the member's zone.", "Players who have a Member rank can teleport here", "and take advantage of the resources that it has." };
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.moveTo(GameSettings.MEMBER_ZONE);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 10:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"To receive a member rank, you need to claim", "a member scroll, which is a one-time purchase.", "Scrolls can be purchased from the store", "which can be opened using the ::store command." };
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.moveTo(GameSettings.MEMBER_ZONE);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 11:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{GameSettings.RSPS_NAME+" is a competitive game. Next to you is a scoreboard", "which you can use to track other players and their progress."};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.moveTo(new Position(3689, 2968, 4));
					p.setDirection(Direction.WEST);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 12:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"That was almost all.", "I just want to remind you to vote for us on various", "gaming toplists. To do so, simply use the ::vote command.", "You will be rewarded!"};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public void specialAction() {
					p.moveTo(GameSettings.DEFAULT_POSITION.copy());
					p.setDirection(Direction.SOUTH);
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 13:
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"If you have any more questions, ask in the clan chat.", "You may also wish to check the ::forums and ::wikia", " Have fun playing "+GameSettings.RSPS_NAME+"!"};//"If you have any more questions, simply use the ::help", "command and a staff member should get back to you.", "You can also join the clanchat channel 'help' and ask", "other players for help there too. Have fun playing "+GameSettings.RSPS_NAME+"!"};
				};

				@Override
				public int npcId() {
					return 2590;
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			break;
		case 14:
			
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public String[] dialogue() {
					return new String[]{"If you have any more questions, ask in the clan chat.", "You may also wish to check the ::forums and ::wikia", " Have fun playing "+GameSettings.RSPS_NAME+"!"};//"If you have any more questions, simply use the ::help", "command and a staff member should get back to you.", "You can also join the clanchat channel 'help' and ask", "other players for help there too. Have fun playing "+GameSettings.RSPS_NAME+"!"};
				};

				@Override
				public int npcId() {
					return 2590;
				}
				
				@Override
				public void specialAction() {
					p.setNewPlayer(false);
					if(ConnectionHandler.getStarters(p.getHostAddress()) <= GameSettings.MAX_STARTERS_PER_IP && p.getGameMode() == GameMode.NORMAL ) {
							p.getInventory().add(995, 1000000).add(1153, 1).add(1115, 1).add(1067, 1).add(1323, 1).add(1191, 1).add(841, 1).add(882, 1000).add(1167, 1).add(1129, 1).add(1095, 1).add(1063, 1).add(579, 1).add(577, 1).add(1011, 1).add(1379, 1).add(556, 1000).add(558, 1000).add(557, 1000).add(554, 1000).add(555, 1000).add(1351, 1).add(1265, 1).add(1712, 1).add(11118, 1).add(1007, 1).add(386, 100).add(9003, 1);
							p.setReceivedStarter(true);
							p.getPacketSender().sendMessage("You claim your normal starter kit!");
							ConnectionHandler.addStarter(p.getHostAddress(), true);
						} else {
						if(p.getGameMode() == GameMode.NORMAL) {
							p.getInventory().add(995, 1).add(1153, 1).add(1115, 1).add(1067, 1).add(1323, 1).add(1191, 1).add(841, 1).add(882, 1).add(1167, 1).add(1129, 1).add(1095, 1).add(1063, 1).add(579, 1).add(577, 1).add(1011, 1).add(1379, 1).add(556, 1).add(558, 50).add(557, 50).add(555, 50).add(554, 50).add(1351, 1).add(1265, 1).add(1007, 1).add(362, 50).add(9003, 1);
							p.setReceivedStarter(true);
							p.getPacketSender().sendMessage("You claim a normal starter kit, but without valuables...");
							p.getPacketSender().sendMessage("This is because you've created more than "+GameSettings.MAX_STARTERS_PER_IP+" accounts.");
						} if(p.getGameMode() == GameMode.IRONMAN) {
							p.getInventory().add(1351, 1).add(1265, 1).add(16691, 1).add(9704, 1).add(17239, 1).add(16669, 1).add(16339, 1).add(6068, 1).add(9703, 1).add(9003, 1);
							p.setReceivedStarter(true);
							p.getPacketSender().sendMessage("You claim your Ironman starter kit!");
						} if(p.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
							p.getInventory().add(1351, 1).add(1265, 1).add(9003, 1);
							p.setReceivedStarter(true);
							p.getPacketSender().sendMessage("You claim your Ultimate Ironman starter kit!");
						}
					}
					World.sendMessage("<shad=1><col=F5FF3B><img=10> "+p.getUsername()+" has just logged in for the first time!");
					p.getPacketSender().sendInterface(3559);
					p.getAppearance().setCanChangeAppearance(true);
					p.setPlayerLocked(false);
					TaskManager.submit(new Task(20, p, false) {
						@Override
						protected void execute() {
							if(p != null && p.isRegistered()) {
								p.getPacketSender().sendMessage("<img=10> @blu@The home area includes shops and skills for you to start with.");
								if (p.getGameMode() == GameMode.ULTIMATE_IRONMAN) 
									p.getPacketSender().sendMessage("<img=10> @gre@"+p.getUsername()+", you can un-note items by using them on a banker or bank!");
							}
							stop();
						}
					});
					p.save();
				}

				@Override
				public Dialogue nextDialogue() {
					return get(p, stage + 1);
				}
			};
			
			break;
		}
		
		return dialogue;
	}


}