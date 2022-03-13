package com.ruse.world.content.combat.magic;

import java.util.Optional;

import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.GraphicHeight;
import com.ruse.model.Item;
import com.ruse.model.Projectile;
import com.ruse.model.Skill;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.effect.CombatTeleblockEffect;
import com.ruse.world.content.combat.effect.CombatPoisonEffect.PoisonType;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * Holds all of the {@link CombatSpell}s that can be cast by an {@link Character}.
 * 
 * @author lare96
 */
public enum CombatSpells {

	/** Normal spellbook spells. */
	WIND_STRIKE(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14221));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2699, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2700, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 29;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 6;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556), new Item(558) });
		}

		@Override
		public int levelRequired() {
			return 1;
		}

		@Override
		public int spellId() {
			return 1152;
		}
	}),
	CONFUSE(new CombatEffectSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(716));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 103, 44, 3, 43, 31,
					0));
		}

		@Override
		public void spellEffect(Character cast, Character castOn) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;

				if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the player has already been weakened.");
					}
					return;
				}
				int decrease = (int) (0.05 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
				player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease);
				player.getSkillManager().updateSkill(Skill.ATTACK);

				player.getPacketSender().sendMessage(
						"You feel slightly weakened.");
			} else if (castOn.isNpc()) {
				NPC npc = (NPC) castOn;

				if (npc.getDefenceWeakened()[0] || npc.getStrengthWeakened()[0]) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the NPC has already been weakened.");
					}
					return;
				}

				npc.getDefenceWeakened()[0] = true;
			}
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(104));
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(102, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 13;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555, 3), new Item(557, 2),
					new Item(559) });
		}

		@Override
		public int levelRequired() {
			return 3;
		}

		@Override
		public int spellId() {
			return 1153;
		}
	}),
	WATER_STRIKE(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14220));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2703, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2708, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 49;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2701));
		}

		@Override
		public int baseExperience() {
			return 8;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555), new Item(556),
					new Item(558) });
		}

		@Override
		public int levelRequired() {
			return 5;
		}

		@Override
		public int spellId() {
			return 1154;
		}
	}),
	EARTH_STRIKE(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14222));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2718, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2723, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 69;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2713));
		}

		@Override
		public int baseExperience() {
			return 10;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 1), new Item(558, 1),
					new Item(557, 2) });
		}

		@Override
		public int levelRequired() {
			return 9;
		}

		@Override
		public int spellId() {
			return 1156;
		}
	}),
	WEAKEN(new CombatEffectSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(716));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 106, 44, 3, 43, 31,
					0));
		}

		@Override
		public void spellEffect(Character cast, Character castOn) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;

				if (player.getSkillManager().getCurrentLevel(Skill.STRENGTH) < player.getSkillManager().getMaxLevel(Skill.STRENGTH)) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the player has already been weakened.");
					}
					return;
				}

				int decrease = (int) (0.05 * (player.getSkillManager().getCurrentLevel(Skill.STRENGTH)));
				player.getSkillManager().setCurrentLevel(Skill.STRENGTH, player.getSkillManager().getCurrentLevel(Skill.STRENGTH) - decrease);
				player.getSkillManager().updateSkill(Skill.STRENGTH);
				player.getPacketSender().sendMessage(
						"You feel slightly weakened.");
			} else if (castOn.isNpc()) {
				NPC npc = (NPC) castOn;

				if (npc.getDefenceWeakened()[1] || npc.getStrengthWeakened()[1]) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the NPC has already been weakened.");
					}
					return;
				}

				npc.getDefenceWeakened()[1] = true;
			}
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(107));
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(105, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 21;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555, 3), new Item(557, 2),
					new Item(559, 1) });
		}

		@Override
		public int levelRequired() {
			return 11;
		}

		@Override
		public int spellId() {
			return 1157;
		}
	}),
	FIRE_STRIKE(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14223));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2729, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2737, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 89;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2728));
		}

		@Override
		public int baseExperience() {
			return 12;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 1), new Item(558, 1),
					new Item(554, 3) });
		}

		@Override
		public int levelRequired() {
			return 13;
		}

		@Override
		public int spellId() {
			return 1158;
		}
	}),
	WIND_BOLT(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14221));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2699, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2700, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 99;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 14;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 2), new Item(562, 1) });
		}

		@Override
		public int levelRequired() {
			return 17;
		}

		@Override
		public int spellId() {
			return 1160;
		}
	}),
	CURSE(new CombatEffectSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(710));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 109, 44, 3, 43, 31,
					0));
		}

		@Override
		public void spellEffect(Character cast, Character castOn) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;

				if (player.getSkillManager().getCurrentLevel(Skill.DEFENCE) < player.getSkillManager().getMaxLevel(Skill.DEFENCE)) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the player has already been weakened.");
					}
					return;
				}

				int decrease = (int) (0.05 * (player.getSkillManager().getCurrentLevel(Skill.DEFENCE)));
				player.getSkillManager().setCurrentLevel(Skill.DEFENCE, player.getSkillManager().getCurrentLevel(Skill.DEFENCE) - decrease);
				player.getSkillManager().updateSkill(Skill.DEFENCE);

				player.getPacketSender().sendMessage(
						"You feel slightly weakened.");
			} else if (castOn.isNpc()) {
				NPC npc = (NPC) castOn;

				if (npc.getDefenceWeakened()[2] || npc.getStrengthWeakened()[2]) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the NPC has already been weakened.");
					}
					return;
				}

				npc.getDefenceWeakened()[2] = true;
			}
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(110));
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(108, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 29;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555, 2), new Item(557, 3),
					new Item(559, 1) });
		}

		@Override
		public int levelRequired() {
			return 19;
		}

		@Override
		public int spellId() {
			return 1161;
		}
	}),
	BIND(new CombatEffectSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(710));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 178, 44, 3, 43, 31,
					0));
		}

		@Override
		public void spellEffect(Character cast, Character castOn) {
			if (castOn.isFrozen()) {
				if (cast.isPlayer()) {
					((Player) cast).getPacketSender().sendMessage(
							"The spell has no effect because they are already frozen.");
				}
				return;
			}

			castOn.getMovementQueue().freeze(5);
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(181, GraphicHeight.HIGH));
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(177, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 30;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555, 3), new Item(557, 3),
					new Item(561, 2) });
		}

		@Override
		public int levelRequired() {
			return 20;
		}

		@Override
		public int spellId() {
			return 1572;
		}
	}),
	WATER_BOLT(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14220));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2704, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2709, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 109;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2702));
		}

		@Override
		public int baseExperience() {
			return 17;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 2), new Item(562, 1),
					new Item(555, 2) });
		}

		@Override
		public int levelRequired() {
			return 23;
		}

		@Override
		public int spellId() {
			return 1163;
		}
	}),
	EARTH_BOLT(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14222));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2719, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2724, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 119;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2714));
		}

		@Override
		public int baseExperience() {
			return 20;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 2), new Item(562, 1),
					new Item(557, 3) });
		}

		@Override
		public int levelRequired() {
			return 29;
		}

		@Override
		public int spellId() {
			return 1166;
		}
	}),
	FIRE_BOLT(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14223));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2731, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2738, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 129;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2728));
		}

		@Override
		public int baseExperience() {
			return 23;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 3), new Item(562, 1),
					new Item(554, 4) });
		}

		@Override
		public int levelRequired() {
			return 35;
		}

		@Override
		public int spellId() {
			return 1169;
		}
	}),
	CRUMBLE_UNDEAD(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(724));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 146, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(147));
		}

		@Override
		public int maximumHit() {
			return 159;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(145, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 25;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 2), new Item(562, 1),
					new Item(557, 2) });
		}

		@Override
		public int levelRequired() {
			return 39;
		}

		@Override
		public int spellId() {
			return 1171;
		}
	}),
	WIND_BLAST(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14221));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2699, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2700, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 139;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 26;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 3), new Item(560, 1) });
		}

		@Override
		public int levelRequired() {
			return 41;
		}

		@Override
		public int spellId() {
			return 1172;
		}
	}),
	WATER_BLAST(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14220));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2705, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2710, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 149;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2702));
		}

		@Override
		public int baseExperience() {
			return 29;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555, 3), new Item(556, 3),
					new Item(560, 1) });
		}

		@Override
		public int levelRequired() {
			return 47;
		}

		@Override
		public int spellId() {
			return 1175;
		}
	}),
	IBAN_BLAST(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(708));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 88, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(89));
		}

		@Override
		public int maximumHit() {
			return 259;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(87, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 30;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.of(new Item[] { new Item(1409) });
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(560, 1), new Item(554, 5) });
		}

		@Override
		public int levelRequired() {
			return 50;
		}

		@Override
		public int spellId() {
			return 1539;
		}
	}),
	SNARE(new CombatEffectSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(710));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 178, 44, 3, 43, 31,
					0));
		}

		@Override
		public void spellEffect(Character cast, Character castOn) {
			if (castOn.isFrozen()) {
				if (cast.isPlayer()) {
					((Player) cast).getPacketSender().sendMessage(
							"The spell has no effect because they are already frozen.");
				}
				return;
			}

			castOn.getMovementQueue().freeze(10);
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(180, GraphicHeight.HIGH));
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(177, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 60;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555, 3), new Item(557, 4),
					new Item(561, 3) });
		}

		@Override
		public int levelRequired() {
			return 50;
		}

		@Override
		public int spellId() {
			return 1582;
		}
	}),
	MAGIC_DART(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1576));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 328, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(329));
		}

		@Override
		public int maximumHit() {
			return 199;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(327, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 30;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.of(new Item[] { new Item(4170) });
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(558, 4), new Item(560, 1) });
		}

		@Override
		public int levelRequired() {
			return 50;
		}

		@Override
		public int spellId() {
			return 12037;
		}
	}),
	EARTH_BLAST(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14222));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2720, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2725, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 159;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2715, GraphicHeight.MIDDLE));
		}

		@Override
		public int baseExperience() {
			return 53;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 3), new Item(560, 1),
					new Item(557, 4) });
		}

		@Override
		public int levelRequired() {
			return 53;
		}

		@Override
		public int spellId() {
			return 1177;
		}
	}),
	FIRE_BLAST(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14223));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2733, 44, 3, 43, 31,	0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2739, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 169;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2728));
		}

		@Override
		public int baseExperience() {
			return 35;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 4), new Item(560, 1),
					new Item(554, 5) });
		}

		@Override
		public int levelRequired() {
			return 59;
		}

		@Override
		public int spellId() {
			return 1181;
		}
	}),
	SARADOMIN_STRIKE(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(811));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(76));
		}

		@Override
		public int maximumHit() {
			return 209;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 35;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.of(new Item[] { new Item(2415) });
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 4), new Item(565, 2),
					new Item(554, 2) });
		}

		@Override
		public int levelRequired() {
			return 60;
		}

		@Override
		public int spellId() {
			return 1190;
		}
	}),
	CLAWS_OF_GUTHIX(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(811));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(77));
		}

		@Override
		public int maximumHit() {
			return 209;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 35;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.of(new Item[] { new Item(2416) });
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 4), new Item(565, 2),
					new Item(554, 2) });
		}

		@Override
		public int levelRequired() {
			return 60;
		}

		@Override
		public int spellId() {
			return 1191;
		}
	}),
	FLAMES_OF_ZAMORAK(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(811));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(78));
		}

		@Override
		public int maximumHit() {
			return 209;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 35;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.of(new Item[] { new Item(2417) });
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 4), new Item(565, 2),
					new Item(554, 2) });
		}

		@Override
		public int levelRequired() {
			return 60;
		}

		@Override
		public int spellId() {
			return 1192;
		}
	}),
	WIND_WAVE(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14221));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2699, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2700, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 179;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 36;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 5), new Item(565, 1) });
		}

		@Override
		public int levelRequired() {
			return 62;
		}

		@Override
		public int spellId() {
			return 1183;
		}
	}),
	WATER_WAVE(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14220));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2706, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2711, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 189;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2702));
		}

		@Override
		public int baseExperience() {
			return 38;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 5), new Item(565, 1),
					new Item(555, 7) });
		}

		@Override
		public int levelRequired() {
			return 65;
		}

		@Override
		public int spellId() {
			return 1185;
		}
	}),
	VULNERABILITY(new CombatEffectSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(729));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 168, 44, 3, 43, 31,
					0));
		}

		@Override
		public void spellEffect(Character cast, Character castOn) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;

				if (player.getSkillManager().getCurrentLevel(Skill.DEFENCE) < player.getSkillManager().getMaxLevel(Skill.DEFENCE)) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the player is already weakened.");
					}
					return;
				}

				int decrease = (int) (0.10 * (player.getSkillManager().getCurrentLevel(Skill.DEFENCE)));
				player.getSkillManager().setCurrentLevel(Skill.DEFENCE, player.getSkillManager().getCurrentLevel(Skill.DEFENCE) - decrease);
				player.getSkillManager().updateSkill(Skill.DEFENCE);
				player.getPacketSender().sendMessage(
						"You feel slightly weakened.");
			} else if (castOn.isNpc()) {
				NPC npc = (NPC) castOn;

				if (npc.getDefenceWeakened()[2] || npc.getStrengthWeakened()[2]) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the NPC is already weakened.");
					}
					return;
				}

				npc.getStrengthWeakened()[2] = true;
			}
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(169));
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(167, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 76;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(557, 5), new Item(555, 5),
					new Item(566, 1) });
		}

		@Override
		public int levelRequired() {
			return 66;
		}

		@Override
		public int spellId() {
			return 1542;
		}
	}),
	EARTH_WAVE(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14222));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2721, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2726, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 199;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2715));
		}

		@Override
		public int baseExperience() {
			return 40;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 5), new Item(565, 1),
					new Item(557, 7) });
		}

		@Override
		public int levelRequired() {
			return 70;
		}

		@Override
		public int spellId() {
			return 1188;
		}
	}),
	ENFEEBLE(new CombatEffectSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(729));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 171, 44, 3, 43, 31,
					0));
		}

		@Override
		public void spellEffect(Character cast, Character castOn) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;

				if (player.getSkillManager().getCurrentLevel(Skill.STRENGTH) < player.getSkillManager().getMaxLevel(Skill.STRENGTH)) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the player is already weakened.");
					}
					return;
				}

				int decrease = (int) (0.10 * (player.getSkillManager().getCurrentLevel(Skill.STRENGTH)));
				player.getSkillManager().setCurrentLevel(Skill.STRENGTH, player.getSkillManager().getCurrentLevel(Skill.STRENGTH) - decrease);
				player.getSkillManager().updateSkill(Skill.STRENGTH);

				player.getPacketSender().sendMessage(
						"You feel slightly weakened.");
			} else if (castOn.isNpc()) {
				NPC npc = (NPC) castOn;

				if (npc.getDefenceWeakened()[1] || npc.getStrengthWeakened()[1]) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the NPC is already weakened.");
					}
					return;
				}

				npc.getStrengthWeakened()[1] = true;
			}
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(172));
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(170, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 83;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(557, 8), new Item(555, 8),
					new Item(566, 1) });
		}

		@Override
		public int levelRequired() {
			return 73;
		}

		@Override
		public int spellId() {
			return 1543;
		}
	}),
	FIRE_WAVE(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(14223));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 2735, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(2740, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 209;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(2728));
		}

		@Override
		public int baseExperience() {
			return 43;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 5), new Item(565, 1),
					new Item(554, 7) });
		}

		@Override
		public int levelRequired() {
			return 75;
		}

		@Override
		public int spellId() {
			return 1189;
		}
	}),
	ENTANGLE(new CombatEffectSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(710));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 178, 44, 3, 43, 31,
					0));
		}

		@Override
		public void spellEffect(Character cast, Character castOn) {
			if (castOn.isFrozen()) {
				if (cast.isPlayer()) {
					((Player) cast).getPacketSender().sendMessage(
							"The spell has no effect because they are already frozen.");
				}
				return;
			}

			castOn.getMovementQueue().freeze(12);
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(179, GraphicHeight.HIGH));
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(177, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 89;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555, 5), new Item(557, 5),
					new Item(561, 4) });
		}

		@Override
		public int levelRequired() {
			return 79;
		}

		@Override
		public int spellId() {
			return 1592;
		}
	}),
	STUN(new CombatEffectSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(729));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 174, 44, 3, 43, 31,
					0));
		}

		@Override
		public void spellEffect(Character cast, Character castOn) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;

				if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the player is already weakened.");
					}
					return;
				}

				int decrease = (int) (0.10 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
				player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease);
				player.getSkillManager().updateSkill(Skill.ATTACK);
				player.getPacketSender().sendMessage(
						"You feel slightly weakened.");
			} else if (castOn.isNpc()) {
				NPC npc = (NPC) castOn;

				if (npc.getDefenceWeakened()[0] || npc.getStrengthWeakened()[0]) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the NPC is already weakened.");
					}
					return;
				}

				npc.getStrengthWeakened()[0] = true;
			}
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(107));
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(173, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 90;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(557, 12),
					new Item(555, 12), new Item(556, 1) });
		}

		@Override
		public int levelRequired() {
			return 80;
		}

		@Override
		public int spellId() {
			return 1562;
		}
	}),
	TELEBLOCK(new CombatEffectSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(10503));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 1842, 44, 3, 43, 31,
					0));
		}

		@Override
		public void spellEffect(Character cast, Character castOn) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;

				if (player.getTeleblockTimer() > 0) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the player is already teleblocked.");
					}
					return;
				}

				player.setTeleblockTimer(600);
				TaskManager.submit(new CombatTeleblockEffect(player));
				player.getPacketSender().sendMessage(
						"You have just been teleblocked!");
			} else if (castOn.isNpc()) {
				if (cast.isPlayer()) {
					((Player) cast).getPacketSender().sendMessage(
							"All NPCs are completely immune to this particular spell.");
				}
			}
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(1843));
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(1841));
		}

		@Override
		public int baseExperience() {
			return 80;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(563, 1), new Item(562, 1),
					new Item(560, 1) });
		}

		@Override
		public int levelRequired() {
			return 85;
		}

		@Override
		public int spellId() {
			return 12445;
		}
	}),
	
	/** Lunar Spellbook spells. */
	VENGEANCE_OTHER(new CombatEffectSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(4411));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public void spellEffect(Character cast, Character castOn) {
			/*if (castOn.isPlayer()) {
				
				Player player = (Player) castOn;
				Player caster = (Player) cast;
				
				caster.setPositionToFace(player.getPosition());

				
				if(!player.getLocation().isAidingAllowed() || player.getLocation() == Location.DUEL_ARENA || player.hasVengeance() || !caster.getLastVengeance().elapsed(30000)) {
					caster.getPacketSender().sendMessage("Vengeance other failed.");
				} else {
					caster.getPacketSender().sendMessage("You cast Vengeance on "+player.getUsername()+".");
					player.getPacketSender().sendMessage("Your were given Vengeance by "+caster.getUsername());
					caster.getLastVengeance().reset();
					player.performGraphic(new Graphic(725));
					player.setHasVengeance(true);
				}
				
			
			} 
			*/
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 108;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(557, 10), new Item(560, 2),
					new Item(9075, 3) });
		}

		@Override
		public int levelRequired() {
			return 93;
		}

		@Override
		public int spellId() {
			return 30298;
		}
	}),

	/** Ancient spellbook spells. */
	SMOKE_RUSH(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			CombatFactory.poisonEntity(castOn, PoisonType.MILD);
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1978));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 384, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(385));
		}

		@Override
		public int maximumHit() {
			return 139;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 30;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 1), new Item(554, 1),
					new Item(562, 2), new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 50;
		}

		@Override
		public int spellId() {
			return 12939;
		}
	}),
	SHADOW_RUSH(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;

				if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
					return;
				}

				int decrease = (int) (0.1 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
				player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease);
				player.getSkillManager().updateSkill(Skill.ATTACK);
			}
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1978));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 378, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(379));
		}

		@Override
		public int maximumHit() {
			return 149;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 31;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 1), new Item(566, 1),
					new Item(562, 2), new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 52;
		}

		@Override
		public int spellId() {
			return 12987;
		}
	}),
	BLOOD_RUSH(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			if (damage < 1) {
				return;
			}
			cast.heal((int) (damage * 0.7));
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1978));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 372, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(373));
		}

		@Override
		public int maximumHit() {
			return 159;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 33;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(565, 1), new Item(562, 2),
					new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 56;
		}

		@Override
		public int spellId() {
			return 12901;
		}
	}),
	ICE_RUSH(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			castOn.getMovementQueue().freeze(7);
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1978));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 360, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(361));
		}

		@Override
		public int maximumHit() {
			return 189;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 34;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555, 2), new Item(562, 2),
					new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 58;
		}

		@Override
		public int spellId() {
			return 12861;
		}
	}),
	SMOKE_BURST(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			CombatFactory.poisonEntity(castOn, PoisonType.MILD);
		}

		@Override
		public int spellRadius() {
			return 1;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1979));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(389));
		}

		@Override
		public int maximumHit() {
			return 139;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 36;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 2), new Item(554, 2),
					new Item(562, 4), new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 62;
		}

		@Override
		public int spellId() {
			return 12963;
		}
	}),
	SHADOW_BURST(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;

				if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
					return;
				}

				int decrease = (int) (0.1 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
				player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease);
				player.getSkillManager().updateSkill(Skill.ATTACK);
			}
		}

		@Override
		public int spellRadius() {
			return 1;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1979));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(382));
		}

		@Override
		public int maximumHit() {
			return 189;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 37;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 1), new Item(566, 2),
					new Item(562, 4), new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 64;
		}

		@Override
		public int spellId() {
			return 13011;
		}
	}),
	BLOOD_BURST(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			if (damage < 1) {
				return;
			}
			cast.heal((int) (damage * 0.10));
		}

		@Override
		public int spellRadius() {
			return 1;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1979));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(376));
		}

		@Override
		public int maximumHit() {
			return 219;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 39;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(565, 2), new Item(562, 4),
					new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 68;
		}

		@Override
		public int spellId() {
			return 12919;
		}
	}),
	ICE_BURST(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			castOn.getMovementQueue().freeze(9);
		}

		@Override
		public int spellRadius() {
			return 1;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1979));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(363));
		}

		@Override
		public int maximumHit() {
			return 229;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 40;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555, 4), new Item(562, 4),
					new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 70;
		}

		@Override
		public int spellId() {
			return 12881;
		}
	}),
	SMOKE_BLITZ(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			CombatFactory.poisonEntity(castOn, PoisonType.EXTRA);
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1978));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 386, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(387));
		}

		@Override
		public int maximumHit() {
			return 239;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 42;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 2), new Item(554, 2),
					new Item(565, 2), new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 74;
		}

		@Override
		public int spellId() {
			return 12951;
		}
	}),
	SHADOW_BLITZ(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;

				if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
					return;
				}

				int decrease = (int) (0.15 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
				player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease);
				player.getSkillManager().updateSkill(Skill.ATTACK);
			}
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1978));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 380, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(381));
		}

		@Override
		public int maximumHit() {
			return 249;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 43;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 2), new Item(566, 2),
					new Item(565, 2), new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 76;
		}

		@Override
		public int spellId() {
			return 12999;
		}
	}),
	BLOOD_BLITZ(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			if (damage < 1) {
				return;
			}
			cast.heal((int) (damage * 0.16));
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1978));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 374, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(375));
		}

		@Override
		public int maximumHit() {
			return 259;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 45;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(565, 4), new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 80;
		}

		@Override
		public int spellId() {
			return 12911;
		}
	}),
	ICE_BLITZ(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			castOn.getMovementQueue().freeze(10);
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1978));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(367));
		}

		@Override
		public int maximumHit() {
			return 269;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.of(new Graphic(366, GraphicHeight.HIGH));
		}

		@Override
		public int baseExperience() {
			return 46;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555, 3), new Item(565, 2),
					new Item(560, 2) });
		}

		@Override
		public int levelRequired() {
			return 82;
		}

		@Override
		public int spellId() {
			return 12871;
		}
	}),
	SMOKE_BARRAGE(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			CombatFactory.poisonEntity(castOn, PoisonType.SUPER);
		}

		@Override
		public int spellRadius() {
			return 1;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1979));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(391));
		}

		@Override
		public int maximumHit() {
			return 279;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 48;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 4), new Item(554, 4),
					new Item(565, 2), new Item(560, 4) });
		}

		@Override
		public int levelRequired() {
			return 86;
		}

		@Override
		public int spellId() {
			return 12975;
		}
	}),
	SHADOW_BARRAGE(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;

				if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
					return;
				}

				int decrease = (int) (0.15 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
				player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease);
				player.getSkillManager().updateSkill(Skill.ATTACK);
			}
		}

		@Override
		public int spellRadius() {
			return 1;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1979));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(383));
		}

		@Override
		public int maximumHit() {
			return 289;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 48;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(556, 4), new Item(566, 3),
					new Item(565, 2), new Item(560, 4) });
		}

		@Override
		public int levelRequired() {
			return 88;
		}

		@Override
		public int spellId() {
			return 13023;
		}
	}),
	BLOOD_BARRAGE(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			if (damage < 1) {
				return;
			}
			cast.heal((int) (damage * 0.20));
		}

		@Override
		public int spellRadius() {
			return 1;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1979));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(377));
		}

		@Override
		public int maximumHit() {
			return 299;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 51;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(560, 4), new Item(566, 1),
					new Item(565, 4) });
		}

		@Override
		public int levelRequired() {
			return 92;
		}

		@Override
		public int spellId() {
			return 12929;
		}
	}),
	ICE_BARRAGE(new CombatAncientSpell() {
		@Override
		public void spellEffect(Character cast, Character castOn, int damage) {
			castOn.getMovementQueue().freeze(15);
		}

		@Override
		public int spellRadius() {
			return 1;
		}

		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(1979));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.empty();
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(369));
		}

		@Override
		public int maximumHit() {
			return 310;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 52;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] { new Item(555, 6), new Item(565, 2),
					new Item(560, 4) });
		}

		@Override
		public int levelRequired() {
			return 94;
		}

		@Override
		public int spellId() {
			return 12891;
		}
	}),
	DAGANNOTH_PRIME(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.of(new Animation(2853));
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 500, 44, 3, 43, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(502, GraphicHeight.MIDDLE));
		}

		@Override
		public int maximumHit() {
			return 550;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return 0;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public int levelRequired() {
			return 0;
		}

		@Override
		public int spellId() {
			return 2881;
		}
	}),
	BABY_SCORPION(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.empty();
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 605, 44, 3, 31, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(606));
		}

		@Override
		public int maximumHit() {
			return 140;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return -1;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public int levelRequired() {
			return 1;
		}

		@Override
		public int spellId() {
			return 109;
		}
	}),
	
	CHICKENNUGGET(new CombatNormalSpell() {
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.empty();
		}

		@Override
		public Optional<Projectile> castProjectile(Character cast, Character castOn) {
			return Optional.of(new Projectile(cast, castOn, 605, 44, 3, 31, 31,
					0));
		}

		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.of(new Graphic(606));
		}

		@Override
		public int maximumHit() {
			return 190;
		}

		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}

		@Override
		public int baseExperience() {
			return -1;
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public int levelRequired() {
			return 1;
		}

		@Override
		public int spellId() {
			return 265;
		}
	});
	
	/** The combat spell that can be casted. */
	private CombatSpell spell;

	/**
	 * Create a new {@link CombatSpells}.
	 * 
	 * @param spell
	 *            the combat spell that can be casted.
	 */
	private CombatSpells(CombatSpell spell) {
		this.spell = spell;
	}

	/**
	 * Gets the combat spell that can be casted.
	 * 
	 * @return the combat spell that can be casted.
	 */
	public CombatSpell getSpell() {
		return spell;
	}

	/**
	 * Gets the spell constant by its spell id.
	 * 
	 * @param spellId
	 *            the spell to retrieve.
	 * @return the spell constant with that spell id.
	 */
	public static CombatSpell getSpell(int spellId) {
		for (CombatSpells spell : CombatSpells.values()) {
			if (spell.getSpell() == null) {
				continue;
			}

			if (spell.getSpell().spellId() == spellId) {
				return spell.getSpell();
			}
		}
		return null;
	}
}
