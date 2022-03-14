package com.ruse.world.content;

import java.util.HashMap;
import java.util.Map;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Flag;
import com.ruse.model.Graphic;
import com.ruse.model.Item;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Equipment;
import com.ruse.util.Misc;
import com.ruse.world.content.skill.SkillManager;
import com.ruse.world.entity.impl.player.Player;

/**
 * Handles emotes
 * @author Gabriel Hannason
 */
public class Emotes {

	public enum EmoteData {
		YES(168, new Animation(855), null),
		NO(169, new Animation(856), null),
		BOW(164, new Animation(858), null),
		ANGRY(165, new Animation(859), null),
		THINK(162, new Animation(857), null),
		WAVE(163, new Animation(863), null),
		SHRUG(13370, new Animation(2113), null),
		CHEER(171, new Animation(862), null),
		BECKON(167, new Animation(864), null),
		LAUGH(170, new Animation(861), null),
		JUMP_FOR_JOY(13366, new Animation(2109), null),
		YAWN(13368, new Animation(2111), null),
		DANCE(166, new Animation(866), null),
		JIG(13363, new Animation(2106), null),
		SPIN(13364, new Animation(2107), null),
		HEADBANG(13365, new Animation(2108), null),
		CRY(161, new Animation(860), null),
		KISS(11100, new Animation(1374), new Graphic(1702)),
		PANIC(13362, new Animation(2105), null),
		RASPBERRY(13367, new Animation(2110), null),
		CRAP(172, new Animation(865), null),
		SALUTE(13369, new Animation(2112), null),
		GOBLIN_BOW(13383, new Animation(2127), null),
		GOBLIN_SALUTE(13384, new Animation(2128), null),
		GLASS_BOX(667, new Animation(1131), null),
		CLIMB_ROPE(6503, new Animation(1130), null),
		LEAN(6506, new Animation(1129), null),
		GLASS_WALL(666, new Animation(1128), null),
		ZOMBIE_WALK(18464, new Animation(3544), null),
		ZOMBIE_DANCE(18465, new Animation(3543), null),
		SCARED(15166, new Animation(2836), null),
		RABBIT_HOP(18686, new Animation(6111), null),
		
		/*ZOMBIE_HAND(15166, new Animation(7272), new Graphic(1244)),
		SAFETY_FIRST(6540, new Animation(8770), new Graphic(1553)),
		AIR_GUITAR(11101, new Animation(2414), new Graphic(1537)),
		SNOWMAN_DANCE(11102, new Animation(7531), null),
		FREEZE(11103, new Animation(11044), new Graphic(1973))*/;

		EmoteData(int button, Animation animation, Graphic graphic) {
			this.button = button;
			this.animation = animation;
			this.graphic = graphic;
		}

		private int button;
		public Animation animation;
		public Graphic graphic;

		private static EmoteData forButton(int button) {
			for(EmoteData data : EmoteData.values()) {
				if(data != null && data.button == button)
					return data;
			}
			return null;
		}
		
		public static EmoteData getRandomEmote() {
			int randomEmote = Misc.getRandom(EmoteData.values().length -1);
			return EmoteData.values()[randomEmote];
		}
	}


	public static boolean doEmote(final Player player, int buttonId) {
		EmoteData emoteData = EmoteData.forButton(buttonId);
		//Normal emotes
		if(emoteData != null) {
			animation(player, emoteData.animation, emoteData.graphic);
			return true;
		//Skillcapes
		} else if(buttonId == 154) {
			Item cape = player.getEquipment().getItems()[Equipment.CAPE_SLOT];
			Skillcape_Data data = Skillcape_Data.dataMap.get(cape.getId());
			if (data != null) {
				player.getMovementQueue().reset();
				if (data != Skillcape_Data.QUEST_POINT) {
					Skill skill = Skill.forId(data.ordinal());
					if (data == Skillcape_Data.DUNGEONEERING_MASTER)
						skill = Skill.DUNGEONEERING;
					int level = SkillManager.getMaxAchievingLevel(skill);
					if (player.getSkillManager().getMaxLevel(skill) < level) {
						player.getPacketSender().sendMessage("You need "+Misc.anOrA(skill.getName())+" " + Misc.formatPlayerName(skill.getName().toLowerCase()) + " level of at least "+ level + " to do this emote.");
						return false;
					}
				}
				if(!player.getEmoteDelay().elapsed(data.delay * 1000)) {
					player.getPacketSender().sendMessage("You must wait a bit before performing another skillcape emote.");
					return true;
				}
				final boolean lock = player.getMovementQueue().isLockMovement();
				player.getMovementQueue().setLockMovement(true);
				player.getEmoteDelay().reset();
				if (data != Skillcape_Data.DUNGEONEERING && data != Skillcape_Data.DUNGEONEERING_MASTER) {
					player.performAnimation(data.animation);
					player.performGraphic(data.graphic);
					TaskManager.submit(new Task(data.delay, player, false) {
						@Override
						public void execute() {
							player.getMovementQueue().setLockMovement(lock);
							stop();
						}
					});
				} else {
					TaskManager.submit(new Task(1, player, true) {
						int tick = 0;
						@Override
						protected void execute() {
							switch(tick) {
							case 0:
								player.performAnimation(new Animation(13190));
								player.performGraphic(new Graphic(2442));
								break;
							case 1:
								player.setNpcTransformationId(11228);
								player.getUpdateFlag().flag(Flag.APPEARANCE);
								player.performAnimation(new Animation(13192));
								break;
							case 6:
								player.setNpcTransformationId(11227);
								player.getUpdateFlag().flag(Flag.APPEARANCE);
								player.performAnimation(new Animation(13193));
								break;
							case 10:
								player.performGraphic(new Graphic(2442));
								break;
							case 11:
								player.setNpcTransformationId(-1);
								player.getUpdateFlag().flag(Flag.APPEARANCE);
								player.performGraphic(new Graphic(-1));
								player.getMovementQueue().setLockMovement(lock);
								stop();
								break;
							/*case 10: ranger looks fucked
								player.setNpcTransformationId(11229);
								player.getUpdateFlag().flag(Flag.APPEARANCE);
								player.performAnimation(new Animation(13194));
								break;
							case 14:
								player.setNpcTransformationId(-1);
								player.getUpdateFlag().flag(Flag.APPEARANCE);
								player.getPacketSender().sendAnimationReset();
								player.getMovementQueue().setLockMovement(lock);
								stop();
								break;
								*/
							}
							tick++;
						}
					});
				}
				return true;
			} else
				player.getPacketSender().sendMessage("You must be wearing a Skillcape in order to use this emote.");
			return true;
		}
		return false;
	}

	public static void animation(Player player, Animation anim, Graphic graphic) {
		if(player.getCombatBuilder().isAttacking() || player.getCombatBuilder().isBeingAttacked()) {
			player.getPacketSender().sendMessage("You cannot do this right now.");
			return;
		}
		if(PlayerPunishment.Jail.isJailed(player)){
			player.getPacketSender().sendMessage("Use your words.");
			return;
		}
		if (!player.getEmoteDelay().elapsed(3100)) {
			player.getPacketSender().sendMessage("You must wait a bit before performing another emote.");
			return;
		}
		player.getMovementQueue().reset();
		if(anim != null)
			player.performAnimation(anim);
		if(graphic != null)
			player.performGraphic(graphic);
		player.getEmoteDelay().reset();
	}

	/**
	 * All Skillcape Configurations and data
	 */
	private enum Skillcape_Data {
		ATTACK(new int[] {9747, 9748, 10639},
				4959, 823, 7),
				DEFENCE(new int[] {9753, 9754, 10641},
						4961, 824, 10),
						STRENGTH(new int[] {9750, 9751, 10640},
								4981, 828, 25),
								CONSTITUTION(new int[] {9768, 9769, 10647},
										14242, 2745, 12),
										RANGED(new int[] {9756, 9757, 10642},
												4973, 832, 12),
												PRAYER(new int[] {9759, 9760, 10643},
														4979, 829, 15),
														MAGIC(new int[] {9762, 9763, 10644},
																4939, 813, 6),
																COOKING(new int[] {9801, 9802, 10658},
																		4955, 821, 36),
																		WOODCUTTING(new int[] {9807, 9808, 10660},
																				4957, 822, 25),
																				FLETCHING(new int[] {9783, 9784, 10652},
																						4937, 0, 20),
																						FISHING(new int[] {9798, 9799, 10657},
																								4951, 819, 19),
																								FIREMAKING(new int[] {9804, 9805, 10659},
																										4975, 831, 14),
																										CRAFTING(new int[] {9780, 9781, 10651},
																												4949, 818, 15),
																												SMITHING(new int[] {9795, 9796, 10656},
																														4943, 815, 23),
																														MINING(new int[] {9792, 9793, 10655},
																																4941, 814, 8),
																																HERBLORE(new int[] {9774, 9775, 10649},
																																		4969, 835, 16),
																																		AGILITY(new int[] {9771, 9772, 10648},
																																				4977, 830, 8),
																																				THIEVING(new int[] {9777, 9778, 10650},
																																						4965, 826, 16),
																																						SLAYER(new int[] {9786, 9787, 10653},
																																								4967, 1656, 8),
																																								FARMING(new int[] {9810, 9811, 10661},
																																										4963, -1, 16),
																																										RUNECRAFTING(new int[] {9765, 9766, 10645},
																																												4947, 817, 10),
																																												CONSTRUCTION(new int[] {9789, 9790, 10654},
																																														4953, 820, 16),
																																												HUNTER(new int[] {9948, 9949, 10646},
																																														5158, 907, 14),
																																																SUMMONING(new int[] {12169, 12170, 12524},
																																																		8525, 1515, 10),
																																																		DUNGEONEERING(new int[] {18508, 18509},
																																																				-1, -1, 15),
																																																				DUNGEONEERING_MASTER(new int[] {19709, 19710},
																																																						-1, -1, 15),
																																																						QUEST_POINT(new int[] {9813, 9814, 10662},
																																																								4945, 816, 19);

		private Skillcape_Data(int[] itemId, int animationId, int graphicId, int delay) {
			item = new Item[itemId.length];
			for (int i = 0; i < itemId.length; i++) {
				item[i] = new Item(itemId[i]);
			}
			animation = new Animation(animationId);
			graphic = new Graphic(graphicId);
			this.delay = delay;
		}

		private final Item[] item;

		private final Animation animation;

		private final Graphic graphic;

		private final int delay;

		private static Map<Integer, Skillcape_Data> dataMap = new HashMap<Integer, Skillcape_Data>();

		static {
			for (Skillcape_Data data : Skillcape_Data.values()) {
				for (Item item : data.item) {
					dataMap.put(item.getId(), data);
				}
			}
		}
	}
}
