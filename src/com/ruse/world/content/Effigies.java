package com.ruse.world.content;

import com.ruse.model.Animation;
import com.ruse.model.Difficulty;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.entity.impl.player.Player;

public class Effigies {

	public static void handleEffigy(Player player, int effigy) {
		if(player == null)
			return;
		if(player.getInterfaceId() > 0) {
			player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			return;
		} else
			DialogueManager.start(player, cleanEffigy(player, effigy));
	}

	public static Dialogue cleanEffigy(final Player player, final int effigy) {
		return new Dialogue() {
			final String name = Misc.formatText(ItemDefinition.forId(effigy).getName());

			@Override
			public DialogueType type() {
				return DialogueType.ITEM_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return null;
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						"You clean off the dust off of the Ancient effigy.",
						"The relic begins to make some sort of weird noises.",
						"I think there may be something inside here."
				};
			}

			@Override
			public String[] item() {
				return new String[] {
						""+effigy+"",
						"180",
						""+name+""
				};
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.ITEM_STATEMENT;
					}

					@Override
					public DialogueExpression animation() {
						return null;
					}

					@Override
					public String[] dialogue() {
						switch(effigy) {
						case 18778:
							return new String[] {"This will require at least a level of 91 in one of the two", "skills to investigate. After investigation it becomes nourished,", "rewarding 15,000 experience in the skill used."};
						case 18779:
							return new String[] {"This will require at least a level of 93 in one of the two", "skills to investigate. After investigation it becomes sated,", "rewarding 30,000 experience in the skill used."};
						case 18780:
							return new String[] {"This will require at least a level of 95 in one of the two", "skills to investigate. After investigation it becomes gordged,", "rewarding 45,000 experience in the skill used."};
						case 18781:
							return new String[] {"This will require at least a level of 97 in one of the two", "skills to investigate. After investigation it provides 60,000 ", "experience in the skill used and, then crumbles to dust,", "leaving behind a dragonkin lamp."};
						}
						return new String[1];
					}

					@Override
					public String[] item() {
						return new String[] {
								""+effigy+"",
								"180",
								""+name+""
						};
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
								String[] pairs = new String[2];
								int r = 1 + Misc.getRandom(6);
								boolean newEffigy = player.getEffigy() == 0;
								if(!newEffigy)
									r = player.getEffigy();
								else
									player.setEffigy(r);
								switch(r) {
								case 1:
									pairs = new String[] {"Crafting", "Agility"};
									player.setDialogueActionId(49);
									break;
								case 2:
									pairs = new String[] {"Runecrafting", "Thieving"};
									player.setDialogueActionId(50);
									break;
								case 3:
									pairs = new String[] {"Cooking", "Firemaking"};
									player.setDialogueActionId(51);
									break;
								case 4:
									pairs = new String[] {"Farming", "Fishing"};
									player.setDialogueActionId(52);
									break;
								case 5:
									pairs = new String[] {"Fletching", "Woodcutting"};
									player.setDialogueActionId(53);
									break;
								case 6:
									pairs = new String[] {"Herblore", "Prayer"};
									player.setDialogueActionId(54);
									break;
								case 7:
									pairs = new String[] {"Smithing", "Mining"};
									player.setDialogueActionId(55);
									break;
								}
								return pairs;
							}

							@Override
							public String[] item() {
								return new String[] {
										""+effigy+"",
										"180",
										""+name+""
								};
							}
						};
					}
				};
			}
		};
	}

	public static boolean handleEffigyAction(Player player, int action) {
		if(player.getInteractingItem() == null || player.getInteractingItem() != null && !isEffigy(player.getInteractingItem().getId())) {
			return false;
		}
		switch(action) {
		case 2461:
			if (player.getDialogueActionId() == 49) {
				openEffigy(player, 12);
				return true;
			} else if (player.getDialogueActionId() == 50) {
				openEffigy(player, 20);
				return true;
			} else if (player.getDialogueActionId() == 51) {
				openEffigy(player, 7);
				return true;
			} else if (player.getDialogueActionId() == 52) {
				openEffigy(player, 19);
				return true;
			} else if (player.getDialogueActionId() == 53) {
				openEffigy(player, 9);
				return true;
			} else if (player.getDialogueActionId() == 54) {
				openEffigy(player, 15);
				return true;
			} else if (player.getDialogueActionId() == 55) {
				openEffigy(player, 13);
				return true;
			}
			break;
		case 2462:
			if (player.getDialogueActionId() == 49) {
				openEffigy(player, 16);
				return true;
			} else if (player.getDialogueActionId() == 50) {
				openEffigy(player, 17);
				return true;
			} else if (player.getDialogueActionId() == 51) {
				openEffigy(player, 11);
				return true;
			} else if (player.getDialogueActionId() == 52) {
				openEffigy(player, 10);
				return true;
			} else if (player.getDialogueActionId() == 53) {
				openEffigy(player, 8);
				return true;
			} else if (player.getDialogueActionId() == 54) {
				openEffigy(player, 5);
				return true;
			} else if (player.getDialogueActionId() == 55) {
				openEffigy(player, 14);
				return true;
			}
			break;
		}
		return false;
	}
	
	public static boolean checkRequirement(Player player, int skillId, int req) {
		if(player.getSkillManager().getCurrentLevel(Skill.forId(skillId)) >= req) {
			return true;
		} else {
			String skill = Misc.formatText(Skill.forId(skillId).name().toLowerCase());
			player.getPacketSender().sendInterfaceRemoval().sendMessage("You need "+Misc.anOrA(skill)+" "+skill+" level of at least "+req+" to do that.");
			return false;
		}
	}

	public static void openEffigy(Player player, int skillId) {
		int[] levelReq = {91, 93, 95, 97};
		if(player.getInteractingItem() == null)
			return;
		if (!player.getClickDelay().elapsed(4000)) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(player.getInteractingItem().getId() == 18778) {
			if(checkRequirement(player, skillId, levelReq[0]) && player.getInventory().contains(18778)) {
				player.getInventory().delete(18778, 1);
				player.getInventory().add(18779, 1);	
				//int difficulty = (int) Difficulty.getDifficultyModifier(player, Skill.forId(skillId));
				int exp = 60000; // / Difficulty.getDifficultyModifier(player, Skill.forId(skillId));
				player.getSkillManager().addExperience(Skill.forId(skillId), exp);
				player.getClickDelay().reset();
				player.performAnimation(new Animation(7368));
				player.getPacketSender().sendInterfaceRemoval();
				player.setEffigy(0);
				//player.setInteractingItem(GameSettings.nullItem);
				return;
			}
		}
		if (player.getInteractingItem().getId() == 18779) {
			if(checkRequirement(player, skillId, levelReq[1]) && player.getInventory().contains(18779)) {
				player.getInventory().delete(18779, 1);
				player.getInventory().add(18780, 1);
				player.getSkillManager().addExperience(Skill.forId(skillId), (int) (65000/Difficulty.getDifficultyModifier(player, Skill.forId(skillId))));
				player.getClickDelay().reset();
				player.performAnimation(new Animation(7368));
				player.getPacketSender().sendInterfaceRemoval();
				player.setEffigy(0);
				//player.setInteractingItem(GameSettings.nullItem);
				return;
			}
		}
		if (player.getInteractingItem().getId() == 18780) {
			if(checkRequirement(player, skillId, levelReq[2]) && player.getInventory().contains(18780)) {
				player.getInventory().delete(18780, 1);
				player.getInventory().add(18781, 1);
				player.getSkillManager().addExperience(Skill.forId(skillId), (int) (70000/Difficulty.getDifficultyModifier(player, Skill.forId(skillId))));
				player.getClickDelay().reset();
				player.performAnimation(new Animation(7368));
				player.getPacketSender().sendInterfaceRemoval();
				player.setEffigy(0);
				//player.setInteractingItem(GameSettings.nullItem);
				return;
			}
		}
		if (player.getInteractingItem().getId() == 18781) {
			if(checkRequirement(player, skillId, levelReq[3]) && player.getInventory().contains(18781)) {
				player.getInventory().delete(18781, 1);
				player.getInventory().add(18782, 1);
				player.getSkillManager().addExperience(Skill.forId(skillId), (int) (75000/Difficulty.getDifficultyModifier(player, Skill.forId(skillId))));
				player.getClickDelay().reset();
				player.performAnimation(new Animation(7368));
				player.getPacketSender().sendInterfaceRemoval();
				player.setEffigy(0);
				//player.setInteractingItem(GameSettings.nullItem);
				return;
			}
		}
	}

	public static boolean isEffigy(int item) {
		return item >= 18778 && item <= 18781;
	}
}
