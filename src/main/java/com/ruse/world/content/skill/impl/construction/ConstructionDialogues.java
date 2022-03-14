 package com.ruse.world.content.skill.impl.construction;

import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.content.skill.impl.construction.ConstructionData.Butlers;
import com.ruse.world.entity.impl.player.Player;

/**
 * Construction dialogues
 * @author Gabriel Hannason
 */
public class ConstructionDialogues {

	public static Dialogue mainPortalDialogue() {
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
				return new String[] {"Enter your house", "Enter your house (building mode)", "Enter friend's house"};
			}
		};
	}

	public static Dialogue gloryTeleportDialogue() {
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
				return new String[] {"Edgeville", "Karamja",
						"Draynor village", "Al-kharid"};
			}
		};
	}

	public static Dialogue servantDialogue(final Player p, final int npc) {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.CALM;
			}

			@Override
			public int npcId() {
				return npc;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"How can I be of service?"};
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.PLAYER_STATEMENT;
					}

					@Override
					public DialogueExpression animation() {
						return DialogueExpression.NORMAL;
					}

					@Override
					public int npcId() {
						return npc;
					}

					@Override
					public String[] dialogue() {
						return new String[] {"I'd like to hire you!", "Never mind."};
					}

					@Override
					public void specialAction() {
						p.setDialogueActionId(444);
					}
				};
			}
		};
	}

	public static Dialogue hireServantDeclineDialogue(final Player p, final int npc, final String errorReq) {
		return new Dialogue() {
			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.CONFUSED;
			}

			@Override
			public int npcId() {
				return npc;
			}

			@Override
			public String[] dialogue() {
				if(errorReq.equalsIgnoreCase("room"))
					return new String[] {"You don't expect me to sleep on the floor do you?", "Come back when you have built at least", "2 bedrooms in your house."};
				else //if(errorReq.equalsIgnoreCase("lvlreq"))
					return new String[] {"Your house is not worth serving!",
						"Come back when you have a Construction level", "of at least "
								+ Butlers.forId(npc).getConsLevel() + "."};

			}
		};
	}

	public static Dialogue hireServantMakeDealDialogue(final Player p, final int npc) {
		return new Dialogue() {
			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.CALM;
			}

			@Override
			public int npcId() {
				return npc;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"Alright, how does " + Butlers.forId(npc).getLoanCost() + "gp/8 services sound?"};
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {
					@Override
					public DialogueType type() {
						return DialogueType.OPTION;
					}

					@Override
					public String[] dialogue() {
						return new String[] {"You're hired!", "Never mind."};
					}

					@Override
					public DialogueExpression animation() {
						return null;
					}

					@Override
					public void specialAction() {
						p.setDialogueActionId(448);
					}
				};
			}
		};
	}

	public static Dialogue finalServantDealDialogue(final boolean accepted) {
		return new Dialogue() {
			@Override
			public DialogueType type() {
				return DialogueType.PLAYER_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return accepted ? DialogueExpression.NORMAL : DialogueExpression.MIDLY_ANGRY;
			}

			@Override
			public String[] dialogue() {
				return new String[] {accepted ? "You're hired!" : "Never mind."};
			}

		};
	}

	public static Dialogue rotateObjectDialogue(final Player p) {
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
				return new String[] {"Rotate clockwise",
						"Rotate counter-clockwise", "Remove"};
			}

			@Override
			public void specialAction() {
				p.setDialogueActionId(642);
			}

		};
	}

	public static Dialogue withdrawSuppliesDialogue(final Player p, final int type) {
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
				String[] s = null;
				switch(type) {
				case 423:
					s = new String[] {"Kettle", "Teapot", "Clay cup"};
					break;
				case 424:
					s = new String[] {"Kettle", "Teapot", "Clay cup", "Beer glass"};
					break;
				case 425:
					s = new String[] {"Kettle", "Teapot", "Porcelain cup", "Beer glass", "Cake tin"};
					break;
				case 426:
					s = new String[] {"Kettle", "Teapot", "Clay cup", "Beer glass", "Bowl"};
					break;
				case 427:
					s = new String[] {"Kettle", "Teapot", "Porcelain cup", "Beer glass", "Next"};
					break;
				case 428:
					s = new String[] {"Bowl", "Cake tin", "Back"};
					break;
				case 429:
					s = new String[] {"Kettle", "Teapot", "Porcelain cup", "Beer glass", "Next"};
					break;
				case 430:
					s = new String[] {"Bowl", "Pie dish", "Empty pot", "Back"};
					break;
				case 431:
					s = new String[] {"Kettle", "Teapot", "Porcelain cup", "Beer glass", "Next"};
					break;
				case 432:
					s = new String[] {"Bowl", "Pie dish", "Empty pot", "Chef's hat", "Back"};
					break;
				case 433:
					s = new String[] {"Tea leaves", "Bucket of milk"};
					break;
				case 434:
					s = new String[] {"Tea leaves", "Bucket of milk", "Egg", "Pot of flour"};
					break;
				case 435:
					s = new String[] {"Tea leaves", "Bucket of milk", "Egg", "Pot of flour", "Next"};
					break;
				case 436:
					s = new String[] {"Potato", "Garlic", "Onion", "Cheese", "Back"};
					break;
				}
				return s;
			}

			@Override
			public void specialAction() {
				p.setDialogueActionId(type);
			}

		};
	}

	public static Dialogue servantOptions(final Player p, final int type) {
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
				if(type == 455) {
					return new String[] {"Bolt of cloth", "Gold leaf", "Marble block", "Magic stone", "Previous"};
				} else if(type == 454) {
					return new String[] {"Soft clay", "Limestone brick", "Steel bar", "Previous", "Next"};
				} else if(type == 453) {
					return new String[] {"Plank", "Oak plank", "Teak plank", "Mahogany plank", "Next"};
				} else if (type == 452 && p.getRegionInstance() != null) {
					final Servant servant = (p.getRegionInstance() instanceof House ? (House) p.getRegionInstance() : ((HouseDungeon) p.getRegionInstance()).getHouse()).getButler();
					String follow = "Follow me, Jeeves.";
					if (servant.getSpawnedFor() == p)
						follow = "Stop following me, Jeeves.";
					return new String[] {"Fetch me some tea.", "Fetch me something from my bank.", "Greet my visitors.", follow, "You're fired!"
					};
				}
				return null;
			}

			@Override
			public void specialAction() {
				p.setDialogueActionId(type);
			}
		};
	}

	public static Dialogue notPlayersButler(final int butler) {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.CALM;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"I'm terribly sorry,", "but I am only toperform these actions for the", "house's owner."};
			}
		};
	}

	public static Dialogue redirectPortalsDialogue() {
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
				return new String[] {"Right portal", "Middle portal", "Left portal"};
			}
		};
	}

	public static Dialogue buildTrapdoor(final Player p) {
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
				return new String[] {"This trapdoor lead nowhere.", "Would you like to build an oubliette under this room?"};
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
						return new String[] {"Yes", "No"};
					}

					@Override
					public void specialAction() {
						p.setDialogueActionId(438);
					}
				};
			}
		};
	}

	public static Dialogue crawlingHandDialogue1() {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.PLAYER_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.GOOFY_LAUGH;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"Hey, I was going to make some furniture,",
				"do you think you could lend a HAND?"};
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.NPC_STATEMENT;
					}

					@Override
					public int npcId() {
						return 4226;
					}

					@Override
					public DialogueExpression animation() {
						return DialogueExpression.CALM;
					}

					@Override
					public String[] dialogue() {
						return new String[] {"Very funny."};
					}
				};
			}
		};
	}

	public static Dialogue crawlingHandDialogue2() {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.PLAYER_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.GOOFY_LAUGH;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"Hey, hand, do you want to know,", "how I slayed you?"};
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.NPC_STATEMENT;
					}

					@Override
					public int npcId() {
						return 4226;
					}

					@Override
					public DialogueExpression animation() {
						return DialogueExpression.CALM;
					}

					@Override
					public String[] dialogue() {
						return new String[] {"I don't know. Sure, go ahead."};
					}

					@Override
					public Dialogue nextDialogue() {
						return new Dialogue() {

							@Override
							public DialogueType type() {
								return DialogueType.PLAYER_STATEMENT;
							}

							@Override
							public DialogueExpression animation() {
								return DialogueExpression.GOOFY_LAUGH;
							}

							@Override
							public String[] dialogue() {
								return new String[] {"Because you're just a hand! ",
								"You're ARMLESS!"};
							}
						};
					}
				};
			}
		};
	}

	public static Dialogue crawlingHandDialogue3() {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.PLAYER_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"Hey, you're just a hand, right? ", "So what do you eat?"};
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.NPC_STATEMENT;
					}

					@Override
					public int npcId() {
						return 4226;
					}

					@Override
					public DialogueExpression animation() {
						return DialogueExpression.GOOFY_LAUGH;
					}

					@Override
					public String[] dialogue() {
						return new String[] {"Fingers, and silly adventurers!"};
					}
				};
			}
		};
	}

	public static Dialogue crawlingHandDialogue4(final Player p) {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.PLAYER_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.GOOFY_LAUGH;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"Hey, a Crawling Hand!"};
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.NPC_STATEMENT;
					}

					@Override
					public int npcId() {
						return 4226;
					}

					@Override
					public DialogueExpression animation() {
						return DialogueExpression.CALM;
					}

					@Override
					public String[] dialogue() {
						return new String[] {"Yes, and?"};
					}

					@Override
					public Dialogue nextDialogue() {
						return new Dialogue() {

							@Override
							public DialogueType type() {
								return DialogueType.PLAYER_STATEMENT;
							}

							@Override
							public DialogueExpression animation() {
								return DialogueExpression.GOOFY_LAUGH;
							}

							@Override
							public String[] dialogue() {
								return new String[] {""+p.getRegionInstance().getOwner().getUsername()
										+ " must be pretty handy to have slayed that!"};
							}
						};
					}
				};
			}
		};
	}
	
	public static Dialogue cockatriceDialogue1() {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}
			
			@Override
			public int npcId() {
				return 4227;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.ANGRY;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"You deaded me!"};
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.PLAYER_STATEMENT;
					}

					@Override
					public DialogueExpression animation() {
						return DialogueExpression.CALM;
					}

					@Override
					public String[] dialogue() {
						return new String[] {"Well yes. But only because a", "Slayer master told me to!"};
					}

					@Override
					public Dialogue nextDialogue() {
						return new Dialogue() {

							@Override
							public DialogueType type() {
								return DialogueType.PLAYER_STATEMENT;
							}

							@Override
							public DialogueExpression animation() {
								return DialogueExpression.GOOFY_LAUGH;
							}

							@Override
							public String[] dialogue() {
								return new String[] {"Don't take it personally.. You look great", "...on my wall!"};
							}
							
							@Override
							public Dialogue nextDialogue() {
								return new Dialogue() {

									@Override
									public DialogueType type() {
										return DialogueType.NPC_STATEMENT;
									}

									@Override
									public DialogueExpression animation() {
										return DialogueExpression.ANGRY;
									}

									@Override
									public int npcId() {
										return 4227;
									}
									
									@Override
									public String[] dialogue() {
										return new String[] {"Bah!"};
									}
								};
							}
						};
					}
				};
			}
		};
	}
	
	public static Dialogue cockatriceDialogue2() {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}
			
			@Override
			public int npcId() {
				return 4227;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.ANGRY;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"You deaded me!"};
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.PLAYER_STATEMENT;
					}

					@Override
					public DialogueExpression animation() {
						return DialogueExpression.CALM;
					}

					@Override
					public String[] dialogue() {
						return new String[] {"Well yes. But only because a", "Slayer master told me to!"};
					}

					@Override
					public Dialogue nextDialogue() {
						return new Dialogue() {

							@Override
							public DialogueType type() {
								return DialogueType.PLAYER_STATEMENT;
							}

							@Override
							public DialogueExpression animation() {
								return DialogueExpression.GOOFY_LAUGH;
							}

							@Override
							public String[] dialogue() {
								return new String[] {"Don't take it personally.. You look great", "...on my wall!"};
							}
							
							@Override
							public Dialogue nextDialogue() {
								return new Dialogue() {

									@Override
									public DialogueType type() {
										return DialogueType.NPC_STATEMENT;
									}

									@Override
									public DialogueExpression animation() {
										return DialogueExpression.ANGRY;
									}

									@Override
									public int npcId() {
										return 4227;
									}
									
									@Override
									public String[] dialogue() {
										return new String[] {"Bah!"};
									}
								};
							}
						};
					}
				};
			}
		};
	}

	public static Dialogue sendConstructionStatement(final String s) {
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
				return new String[] {""+s+""};
			}
		};
	}
}
