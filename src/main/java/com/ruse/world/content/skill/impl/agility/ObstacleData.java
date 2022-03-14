package com.ruse.world.content.skill.impl.agility;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.CombatIcon;
import com.ruse.model.Flag;
import com.ruse.model.Hit;
import com.ruse.model.Hitmask;
import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.player.Player;
/**
 * Messy as fuck, what ever
 * @author Gabriel Hannason
 */
public enum ObstacleData {

	/* GNOME COURSE */
	LOG(2295, true) {
		@Override
		public void cross(final Player player) {
			player.setSkillAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.moveTo(new Position(2474, 3436));
			player.getPacketSender().sendMessage("You attempt to walk across the log..");
			TaskManager.submit(new Task(1, player, false) {
				int tick = 7;
				@Override
				public void execute() {
					tick--;
					player.getMovementQueue().walkStep(0, -1);
					if(tick <= 0)
						stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.setCrossedObstacle(0, true).setCrossingObstacle(false).setSkillAnimation(-1);
					Agility.addExperience(player, 60);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					player.getPacketSender().sendMessage("You manage to safely make your way across the log.");
				}
			});
		}
	},
	NET(2285, false) {
		@Override
		public void cross(final Player player) {
			player.performAnimation(new Animation(828));
			player.getPacketSender().sendMessage("You climb the net..");
			TaskManager.submit(new Task(1, player, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick == 2) {
						player.moveTo(new Position(2473, 3423, 1));
						Agility.addExperience(player, 40);
					} else if(tick == 3) {
						player.setCrossedObstacle(1, true).setCrossingObstacle(false);
						stop();
					}
					tick++;
				}
			});
		}
	},
	BRANCH(2313, false) {
		@Override
		public void cross(final Player player) {
			player.performAnimation(new Animation(828));
			player.getPacketSender().sendMessage("You climb the branch..");
			TaskManager.submit(new Task(2, player, false) {
				@Override
				public void execute() {
					player.moveTo(new Position(2473, 3420, 2));
					Agility.addExperience(player, 42);
					player.setCrossedObstacle(2, true).setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	ROPE(2312, true) {
		@Override
		public void cross(final Player player) {
			player.setSkillAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.getPacketSender().sendMessage("You attempt to walk across the rope..");
			TaskManager.submit(new Task(1, player, false) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(1, 0);
					if(tick >= 6)
						stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.setCrossedObstacle(3, true).setCrossingObstacle(false).setSkillAnimation(-1);
					Agility.addExperience(player, 25);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					player.getPacketSender().sendMessage("You manage to safely walk across the rope.");
				}
			});
		}
	},
	BRANCH_2(2314, false) {
		@Override
		public void cross(final Player player) {
			player.performAnimation(new Animation(828));
			player.getPacketSender().sendMessage("You climb the branch..");
			TaskManager.submit(new Task(2, player, false) {
				@Override
				public void execute() {
					player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 0));
					Agility.addExperience(player, 42);
					player.setCrossedObstacle(4, true).setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	NETS_2(2286, false) {
		@Override
		public void cross(final Player player) {
			if(player.getPosition().getY() != 3425) {
				player.setCrossingObstacle(false);
				return;
			}
			player.getPacketSender().sendMessage("You climb the net..");
			player.performAnimation(new Animation(828));
			TaskManager.submit(new Task(2, player, false) {
				@Override
				public void execute() {
					player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY() + 2, 0));
					Agility.addExperience(player, 15);
					player.setCrossedObstacle(5, true).setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	PIPE_1(4058, true) {
		@Override
		public void cross(final Player player) {
			player.moveTo(new Position(2487, 3430));
			player.getPacketSender().sendMessage("You attempt to go through the pipe..");
			TaskManager.submit(new Task(1, player, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick < 3 || tick >= 4) {
						if(player.getSkillAnimation() != 844) {
							player.setSkillAnimation(844);
							player.getUpdateFlag().flag(Flag.APPEARANCE);
						}
					} else {
						if(player.getSkillAnimation() != -1) {
							player.setSkillAnimation(-1);
							player.getUpdateFlag().flag(Flag.APPEARANCE);
						}
					}

					tick++;
					player.getMovementQueue().walkStep(0, 1);
					if(tick >= 4)
						stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.moveTo(new Position(2487, 3437));
					player.setCrossedObstacle(6, true).setCrossingObstacle(false).setSkillAnimation(-1);
					player.getClickDelay().reset();
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					if(Agility.passedAllObstacles(player)) {
						DialogueManager.start(player, DialogueManager.getDialogues().get(57 + Misc.getRandom(2)));
						player.getInventory().add(2996, 2);
						Agility.addExperience(player, 60);
					} else {
						DialogueManager.start(player, DialogueManager.getDialogues().get(56));
					}
					Agility.resetProgress(player);
					player.getPacketSender().sendMessage("You manage to make your way through the pipe.");
				}
			});
		}
	},
	PIPE_2(154, true) {
		@Override
		public void cross(final Player player) {
			player.moveTo(new Position(2484, 3430));
			player.getPacketSender().sendMessage("You attempt to go through the pipe..");
			TaskManager.submit(new Task(1, player, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick < 3 || tick >= 4) {
						if(player.getSkillAnimation() != 844) {
							player.setSkillAnimation(844);
							player.getUpdateFlag().flag(Flag.APPEARANCE);
						}
					} else {
						if(player.getSkillAnimation() != -1) {
							player.setSkillAnimation(-1);
							player.getUpdateFlag().flag(Flag.APPEARANCE);
						}
					}

					tick++;
					player.getMovementQueue().walkStep(0, 1);
					if(tick >= 4)
						stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.moveTo(new Position(2483, 3437));
					player.setCrossedObstacle(6, true).setCrossingObstacle(false).setSkillAnimation(-1);
					player.getClickDelay().reset();
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					if(Agility.passedAllObstacles(player)) {
						DialogueManager.start(player, DialogueManager.getDialogues().get(57 + Misc.getRandom(2)));
						player.getInventory().add(2996, 2);
						Agility.addExperience(player, 220);
					} else {
						DialogueManager.start(player, DialogueManager.getDialogues().get(56));
					}
					player.getPacketSender().sendMessage("You manage to make your way through the pipe.");
					Agility.resetProgress(player);
				}
			});
		}

	},



	/* BARBARIAN OUTPOST COURSE */
	ROPE_SWING(2282, true) {
		@Override
		public void cross(final Player player) {
			Agility.resetProgress(player);
			player.getPacketSender().sendMessage("You attempt to swing on the ropeswing..");
			player.moveTo(new Position(2551, 3554));
			player.performAnimation(new Animation(751));
			final boolean success = Agility.isSucessive(player);
			TaskManager.submit(new Task(1, player, false) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					if(tick == 1)
						player.moveTo(new Position(player.getPosition().getX(), 3553, 0));
					if(!success) {
						player.moveTo(new Position(2550, 9950, 0));
						Agility.addExperience(player, 18);
						player.dealDamage(new Hit(Misc.getRandom(50), Hitmask.RED, CombatIcon.NONE));
						player.getPacketSender().sendMessage("You failed to swing your way across.");
						stop();
						return;
					}
					if(tick >= 3) {
						player.moveTo(new Position(player.getPosition().getX(), 3549, 0));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.setCrossedObstacle(0, success ? true : false).setCrossingObstacle(false);
					Agility.addExperience(player, 75 * 3);
					player.getPacketSender().sendMessage("You manage to swing yourself across.");
				}
			});
		}
	},
	LOG_2(2294, true) {
		@Override
		public void cross(final Player player) {
			final boolean fail = !Agility.isSucessive(player);
			player.getPacketSender().sendMessage("You attempt to walk-over the log..");
			player.setSkillAnimation(762);
			player.moveTo(new Position(2550, 3546, 0));
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(-1, 0);
					if(tick >= 9 || player == null)
						stop();
					if(tick == 5 && fail) {
						stop();
						tick = 0;
						player.getMovementQueue().reset();
						player.performAnimation(new Animation(764));
						TaskManager.submit(new Task(1, player, true) {
							int tick2 = 0;
							public void execute() {
								if(tick2 == 0) {
									player.moveTo(new Position(2546, 3547));
									player.dealDamage(new Hit(Misc.getRandom(50), Hitmask.RED, CombatIcon.NONE));
								}
								tick2++;
								player.setSkillAnimation(772);
								player.getUpdateFlag().flag(Flag.APPEARANCE);
								player.getMovementQueue().walkStep(0, 1);
								if(tick2 >= 4) {
									player.getPacketSender().sendMessage("You are unable to make your way across the log.");
									player.setCrossedObstacle(1, false).setCrossingObstacle(false).setSkillAnimation(-1);
									Agility.addExperience(player, 5);
									player.getUpdateFlag().flag(Flag.APPEARANCE);
									stop();
									return;
								}
							}
						});
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					if(!fail) {
						player.setCrossedObstacle(1, true).setCrossingObstacle(false).setSkillAnimation(-1);
						Agility.addExperience(player, fail ? 5 * 3 : 60 * 3);
						player.getUpdateFlag().flag(Flag.APPEARANCE);
						player.moveTo(new Position(2541, 3546));
						player.getPacketSender().sendMessage("You safely make your way across the log.");
					}
				}
			});
		}
	},
	NET_3(2284, false) {
		@Override
		public void cross(final Player player) {
			player.performAnimation(new Animation(828));
			player.getPacketSender().sendMessage("You climb the net..");
			TaskManager.submit(new Task(2, player, false) {
				@Override
				public void execute() {
					player.moveTo(new Position(2537 + Misc.getRandom(1), 3546 + Misc.getRandom(1), 1));
					Agility.addExperience(player, 30 * 3);
					player.setCrossedObstacle(2, true).setSkillAnimation(-1).setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	BALANCE_LEDGE(2302, true) {
		@Override
		public void cross(final Player player) {
			if(player.getPosition().getX() != 2536) {
				player.setCrossingObstacle(false);
				return;
			}
			player.getPacketSender().sendMessage("You attempt to make your way across the ledge..");

			final boolean fallDown = !Agility.isSucessive(player);
			player.setCrossingObstacle(true);
			player.moveTo(new Position(2536, 3547, 1));
			TaskManager.submit(new Task(1, player, false) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.setSkillAnimation(756);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					player.getMovementQueue().walkStep(-1, 0);
					if(tick == 3 && fallDown) {
						player.performAnimation(new Animation(761));
						stop();
						TaskManager.submit(new Task(1) {
							@Override
							public void execute() {
								player.moveTo(new Position(2535, 3546, 0));
								player.dealDamage(new Hit(Misc.getRandom(50), Hitmask.RED, CombatIcon.NONE));
								player.getMovementQueue().walkStep(0, -1);
								player.setCrossedObstacle(3, false).setSkillAnimation(-1);
								player.getUpdateFlag().flag(Flag.APPEARANCE);
								Agility.addExperience(player, 6 * 3);
								player.getPacketSender().sendMessage("You accidently slip and fall down!");
								TaskManager.submit(new Task(1) {
									@Override
									public void execute() {
										player.setCrossingObstacle(false);
										stop();
									}
								});
								stop();
							}
						});
					} else if(tick == 4) {
						player.setCrossedObstacle(3, true).setSkillAnimation(-1).setCrossingObstacle(false);
						player.getUpdateFlag().flag(Flag.APPEARANCE);
						Agility.addExperience(player, 40 * 3);
						player.getPacketSender().sendMessage("You safely move across the ledge.");
						stop();
					}
				}
			});
		}
	},
	LADDER(3205, false) {
		@Override
		public void cross(final Player player) {
			player.performAnimation(new Animation(827));
			player.getPacketSender().sendMessage("You climb down the ladder...");
			TaskManager.submit(new Task(1, player, false) {
				@Override
				public void execute() {
					player.moveTo(new Position(2532, 3546, 0));
					player.setCrossedObstacle(4, true).setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	RAMP(1948, false) {

		@Override
		public void cross(final Player player) {
			if(player.getPosition().getX() != 2535 && player.getPosition().getX() != 2538 && player.getPosition().getX() != 2542 && player.getPosition().getX() != 2541) {
				player.getPacketSender().sendMessage("You cannot jump over the wall from this side!");
				player.setCrossingObstacle(false);
				return;
			}
			final boolean first = player.getPosition().getX() == 2535;
			final boolean oneStep = player.getPosition().getX() == 2537 || player.getPosition().getX() == 2542;
			player.setPositionToFace(player.getInteractingObject().getPosition().copy());
			player.getPacketSender().sendMessage("You attempt to jump over the wall...");
			player.performAnimation(new Animation(1115));
			TaskManager.submit(new Task(1, player, false) {
				@Override
				public void execute() {
					player.getPacketSender().sendClientRightClickRemoval();
					player.moveTo(new Position(player.getPosition().getX() + (oneStep ? 1 : 2), 3553));
					player.setCrossingObstacle(false).setCrossedObstacle(first ? 5 : 6, true);
					if(player.getPosition().getX() == 2543 && player.getPosition().getY() == 3553) {
						if(Agility.passedAllObstacles(player)) {
							DialogueManager.start(player, 57);
							player.getInventory().add(2996, 4);
							Agility.addExperience(player, 265);
							Agility.resetProgress(player);
						} else {
							DialogueManager.start(player, 56);
						}
						player.getPacketSender().sendMessage("You manage to jump over the wall.");
					}
					stop();
				}
			});
		}
	},
	ROPESWING_LADDER(1759, false) {
		@Override
		public void cross(final Player player) {
			player.performAnimation(new Animation(827));
			player.getPacketSender().sendMessage("You climb the ladder..");
			TaskManager.submit(new Task(1) {
				@Override
				public void execute() {
					player.setCrossingObstacle(false);
					player.moveTo(new Position(player.getPosition().getX() > 2610 ? 2209: 2546, player.getPosition().getY() < 3550 ? 5348: 9951, 0));
					stop();
				}
			});
		}
	},

	/* WILD COURSE */

	GATE_1(2309, true) {
		@Override
		public void cross(final Player player) {
			player.moveTo(new Position(2998, 3917));
			player.setSkillAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.getPacketSender().sendMessage("You enter the gate and begin walking across the narrow path..");
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(0, 1);
					if(player.getPosition().getY() == 3930 || tick >= 15) {
						player.moveTo(new Position(2998, 3931, 0));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.setCrossingObstacle(false).setSkillAnimation(-1);
					Agility.addExperience(player, 15);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					Agility.resetProgress(player);
					player.getPacketSender().sendMessage("You manage to make your way to the other side.");
				}
			});
		}
	},
	GATE_2(2308, true) {
		@Override
		public void cross(final Player player) {
			player.getPacketSender().sendMessage("You enter the gate and begin walking across the narrow path..");
			player.moveTo(new Position(2998, 3930));
			player.setSkillAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(0, -1);
					if(player.getPosition().getY() == 3917 || tick >= 15) {
						player.moveTo(new Position(2998, 3916));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.setCrossingObstacle(false).setSkillAnimation(-1);
					Agility.addExperience(player, 15);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					Agility.resetProgress(player);
					player.getPacketSender().sendMessage("You manage to make your way to the other side.");
				}
			});
		}
	},
	GATE_3(2308, true) {
		@Override
		public void cross(final Player player) {
			player.getPacketSender().sendMessage("You enter the gate and begin walking across the narrow path..");
			player.moveTo(new Position(2998, 3930));
			player.setSkillAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(0, -1);
					if(player.getPosition().getY() == 3917 || tick >= 15) {
						player.moveTo(new Position(2998, 3916));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.setCrossingObstacle(false).setSkillAnimation(-1);
					Agility.addExperience(player, 15);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					Agility.resetProgress(player);
					player.getPacketSender().sendMessage("You manage to make your way to the other side.");
				}
			});
		}
	},
	PIPE_3(2288, true) {
		@Override
		public void cross(final Player player) {
			player.moveTo(new Position(3004, 3937));
			player.setSkillAnimation(844);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.getPacketSender().sendMessage("You attempt to squeeze through the pipe..");
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(0, 1);
					if(tick == 4)
						player.moveTo(new Position(3004, 3947));
					else if (tick == 7)
						stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.setCrossedObstacle(0, true).setCrossedObstacle(1, true).setCrossedObstacle(2, true).setCrossingObstacle(false).setSkillAnimation(-1);
					Agility.addExperience(player, 175);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					player.getPacketSender().sendMessage("You manage to squeeze through the pipe.");
				}
			});
		}
	},
	ROPE_SWING_2(2283, true) {
		@Override
		public void cross(final Player player) {
			if(player.getPosition().getY() > 3953) {
				player.getPacketSender().sendMessage("You must be positioned infront of the Ropeswing to do that.");
				player.setCrossingObstacle(false);
				return;
			}
			player.getPacketSender().sendMessage("You attempt to swing on the ropeswing..");
			player.moveTo(new Position(3005, 3953));
			player.performAnimation(new Animation(751));
			player.setPositionToFace(new Position(3005, 3960, 0));
			final boolean fail = !Agility.isSucessive(player);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					if(tick == 2) {
						if(fail) {
							player.moveTo(new Position(3004, 10356));
							player.dealDamage(new Hit(Misc.getRandom(60), Hitmask.RED, CombatIcon.NONE));
							Agility.addExperience(player, 40);
							player.getPacketSender().sendMessage("You failed to swing your way across.");
							stop();
							return;
						} else {
							player.setPositionToFace(new Position(3005, 3960, 0));
							player.moveTo(new Position(player.getPosition().getX(), 3954, 0));
						}
					}
					if(tick >= 3) {
						player.moveTo(new Position(player.getPosition().getX(), 3958, 0));
						player.setPositionToFace(new Position(3005, 3960, 0));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getPacketSender().sendMessage("You manage to swing yourself across.");
					player.setCrossedObstacle(3, fail ? false : true).setCrossingObstacle(false);
					Agility.addExperience(player, fail ? 10 : 250);
				}
			});
		}
	},
	STEPPING_STONES(9326, true) {
		@Override
		public void cross(final Player player) {
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.getPacketSender().sendMessage("You attempt to pass the stones..");

			TaskManager.submit(new Task(1, player, true) {
				int tick = 1;
				@Override
				public void execute() {
					tick++;
					player.performAnimation(new Animation(769));
					if(tick == 4 || tick == 7 || tick == 10 || tick == 13 || tick == 16) {
						player.moveTo(new Position(player.getPosition().getX() - 1, player.getPosition().getY()));
					} else if(tick >= 17) {
						player.moveTo(new Position(2996, 3960, 0));
						//Agility.addExperience(player, 250);
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.setCrossedObstacle(4, true).setCrossingObstacle(false);
					Agility.addExperience(player, 300);
					player.getPacketSender().sendMessage("You manage to pass the stones.");
				}
			});
		}
	},
	BALANCE_LEDGE_2(2297, true) {
		@Override
		public void cross(final Player player) {
			player.moveTo(new Position(3001, 3945, 0));
			player.setSkillAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			final boolean fail = !Agility.isSucessive(player);
			player.getPacketSender().sendMessage("You attempt to make your way over the log..");
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(-1, 0);
					if(tick >= 7)
						stop();
					else if(fail && tick >= 3) {
						player.moveTo(new Position(3000, 10346));
						player.dealDamage(new Hit(Misc.getRandom(60), Hitmask.RED, CombatIcon.NONE));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.setCrossedObstacle(5, fail ? false : true).setCrossingObstacle(false).setSkillAnimation(-1);
					Agility.addExperience(player, fail ? 10 : 250);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					player.getPacketSender().sendMessage("You manage to safely make your way over the log.");
				}
			});
		}
	},
	CLIMB_WALL(2994, false) {
		@Override
		public void cross(final Player player) {
			player.performAnimation(new Animation(828));
			player.getPacketSender().sendMessage("You attempt to climb up the wall..");
			TaskManager.submit(new Task(2, player, false) {
				@Override
				public void execute() {
					player.getPacketSender().sendClientRightClickRemoval();
					player.moveTo(new Position(2996, 3933, 0));
					stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.setCrossedObstacle(6, true).setCrossingObstacle(false);
					Agility.addExperience(player, 100);
					if(Agility.passedAllObstacles(player)) {
						DialogueManager.start(player, 57);
						player.getInventory().add(2996, 6);
						Agility.addExperience(player, 350);
					} else {
						DialogueManager.start(player, 56);
					}
					player.getPacketSender().sendMessage("You manage to climb up the wall.");
					Agility.resetProgress(player);
				}
			});
		}
	},
	LADDER_2(14758, false) {
		@Override
		public void cross(final Player player) {
			player.performAnimation(new Animation(827));
			player.setEntityInteraction(null);
			player.setCrossingObstacle(false);
			TaskManager.submit(new Task(1) {
				@Override
				public void execute() {
					player.moveTo(new Position(3005, 10362, 0));
					stop();
				}
			});
		}
	},

	/**MISC**/
	RED_DRAGON_LOG_BALANCE(5088, false) {
		@Override
		public void cross(final Player player) {
			player.setCrossingObstacle(true).setSkillAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			final int moveX = player.getPosition().getX() > 2683 ? 2686 : 2683;
			player.moveTo(new Position(moveX, 9506));
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick < 4)
						player.getMovementQueue().walkStep(moveX == 2683 ? +1 : -1, 0);
					else if(tick == 4) {
						player.setSkillAnimation(-1).setCrossingObstacle(false);
						player.getUpdateFlag().flag(Flag.APPEARANCE);
						Agility.addExperience(player, 32);
						stop();
					}
					tick++;
				}
			});
		}
	},
	;

	ObstacleData(int object, boolean mustWalk) {
		this.object = object;
		this.mustWalk = mustWalk;
	}

	private int object;
	private boolean mustWalk;

	public int getObject() {
		return object;
	}

	public boolean mustWalk() {
		return mustWalk;
	}

	public void cross(final Player player) {

	}

	public static ObstacleData forId(int object) {
		if(object == 2993 || object == 2328 || object == 2995 || object == 2994)
			return CLIMB_WALL;
		else if(object == 2307)
			return GATE_2;
		else if(object == 5088 || object == 5090)
			return RED_DRAGON_LOG_BALANCE;
		for(ObstacleData obstacleData : ObstacleData.values()) {
			if(obstacleData.getObject() == object)
				return obstacleData;
		}
		return null;
	}
}