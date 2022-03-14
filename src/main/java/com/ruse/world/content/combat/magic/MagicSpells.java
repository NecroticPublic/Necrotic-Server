package com.ruse.world.content.combat.magic;

import java.util.Optional;

import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.GraphicHeight;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.Skill;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.player.Player;

/**
 * Holds data for all no-combat spells
 * @author Gabriel Hannason
 */
public enum MagicSpells {
	
	BONES_TO_BANANAS(new Spell() {

		@Override
		public int spellId() {
			return 1159;
		}

		@Override
		public int levelRequired() {
			return 15;
		}

		@Override
		public int baseExperience() {
			return 25;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(561), new Item(555, 2), new Item(557, 2)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	LOW_ALCHEMY(new Spell() {

		@Override
		public int spellId() {
			return 1162;
		}

		@Override
		public int levelRequired() {
			return 21;
		}

		@Override
		public int baseExperience() {
			return 31;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(554, 3), new Item(561)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}
		
		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	TELEKINETIC_GRAB(new Spell() {

		@Override
		public int spellId() {
			return 1168;
		}

		@Override
		public int levelRequired() {
			return 33;
		}

		@Override
		public int baseExperience() {
			return 43;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(563), new Item(556)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}
		
		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	SUPERHEAT_ITEM(new Spell() {

		@Override
		public int spellId() {
			return 1173;
		}

		@Override
		public int levelRequired() {
			return 43;
		}

		@Override
		public int baseExperience() {
			return 53;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(554, 4), new Item(561)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}
		
		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	ENCHANT_SAPPHIRE(new Spell() {

		@Override
		public int spellId() {
			return 1155;
		}

		@Override
		public int levelRequired() {
			return 7;
		}

		@Override
		public int baseExperience() {
			return 18;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(564, 1), new Item(555, 1)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	ENCHANT_EMERALD(new Spell() {

		@Override
		public int spellId() {
			return 1165;
		}

		@Override
		public int levelRequired() {
			return 27;
		}

		@Override
		public int baseExperience() {
			return 37;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(564, 1), new Item(556, 3)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	ENCHANT_RUBY(new Spell() {

		@Override
		public int spellId() {
			return 1176;
		}

		@Override
		public int levelRequired() {
			return 49;
		}

		@Override
		public int baseExperience() {
			return 59;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(564, 1), new Item(554, 5)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	ENCHANT_DIAMOND(new Spell() {

		@Override
		public int spellId() {
			return 1180;
		}

		@Override
		public int levelRequired() {
			return 57;
		}

		@Override
		public int baseExperience() {
			return 67;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(564, 1), new Item(557, 10)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	ENCHANT_DRAGONSTONE(new Spell() {

		@Override
		public int spellId() {
			return 1187;
		}

		@Override
		public int levelRequired() {
			return 68;
		}

		@Override
		public int baseExperience() {
			return 78;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(564, 1), new Item(557, 15), new Item(555, 15)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	ENCHANT_ONYX(new Spell() {

		@Override
		public int spellId() {
			return 6003;
		}

		@Override
		public int levelRequired() {
			return 87;
		}

		@Override
		public int baseExperience() {
			return 97;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(564, 1), new Item(554, 20), new Item(557, 20)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	HIGH_ALCHEMY(new Spell() {

		@Override
		public int spellId() {
			return 1178;
		}

		@Override
		public int levelRequired() {
			return 55;
		}

		@Override
		public int baseExperience() {
			return 65;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(554, 5), new Item(561)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	BONES_TO_PEACHES(new Spell() {

		@Override
		public int spellId() {
			return 15877;
		}

		@Override
		public int levelRequired() {
			return 60;
		}

		@Override
		public int baseExperience() {
			return 65;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(561, 2), new Item(555, 4), new Item(557, 4)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
		
	}),
	BAKE_PIE(new Spell() {

		@Override
		public int spellId() {
			return 30017;
		}

		@Override
		public int levelRequired() {
			return 65;
		}

		@Override
		public int baseExperience() {
			return 60;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(9075, 1), new Item(554, 5), new Item(555, 4)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
	}),
	VENGEANCE_OTHER(new Spell() {

		@Override
		public int spellId() {
			return 30298;
		}

		@Override
		public int levelRequired() {
			return 93;
		}

		@Override
		public int baseExperience() {
			return 108;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[] {new Item(9075, 3), new Item(557, 10), new Item(560, 2)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
	}),
	VENGEANCE(new Spell() {

		@Override
		public int spellId() {
			return 30306;
		}

		@Override
		public int levelRequired() {
			return 94;
		}

		@Override
		public int baseExperience() {
			return 112;
		}

		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.of(new Item[]{new Item(9075, 4), new Item(557, 10), new Item(560, 2)});
		}

		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}

		@Override
		public void startCast(Character cast, Character castOn) {
			
			
		}
	});
	
	MagicSpells(Spell spell) {
		this.spell = spell;
	}
	
	private Spell spell;
	
	public Spell getSpell() {
		return spell;
	}
	
	public static MagicSpells forSpellId(int spellId) {
		for(MagicSpells spells : MagicSpells.values()) {
			if(spells.getSpell().spellId() == spellId)
				return spells;
		}
		return null;
	}
	
	
	@SuppressWarnings("incomplete-switch")
	public static boolean handleMagicSpells(Player p, int buttonId) {
		MagicSpells spell = forSpellId(buttonId);
		if(!spell.getSpell().canCast(p, false))
			return true;
		switch(spell) {
		case BONES_TO_PEACHES:
		case BONES_TO_BANANAS:
			Item item = new Item(526);
			String sa = !item.getDefinition().getName().endsWith("s") ? "s" : "";
			if(!p.getInventory().contains(item.getId())) {
				p.getPacketSender().sendMessage("You do not have any "+item.getDefinition().getName()+""+sa+" in your inventory.");
				return true;
			}
			p.getInventory().delete(557, spell == BONES_TO_PEACHES ? 4 : 2).delete(555, spell == BONES_TO_PEACHES ? 4 : 2).delete(561, spell == BONES_TO_PEACHES ? 2 : 1);
			int i = 0;
			for(Item invItem : p.getInventory().getValidItems()) {
				if(invItem.getId() == item.getId()) {
					p.getInventory().delete(item.getId(), 1).add(spell == BONES_TO_PEACHES ? 6883 : 1963, 1);
					i++;
				}
			}
			p.performGraphic(new Graphic(141, GraphicHeight.MIDDLE));
			p.performAnimation(new Animation(722));
			p.getSkillManager().addExperience(Skill.MAGIC, spell.getSpell().baseExperience() * i);
			break;
		case VENGEANCE:
			if(p.getLocation() == Location.DUEL_ARENA) {
				p.getPacketSender().sendMessage("This spell cannot be cast here.");
				return true;
			}
			if(p.hasVengeance()) {
				p.getPacketSender().sendMessage("You already have Vengeance active.");
				return true;
			}
			if(!p.getLastVengeance().elapsed(30000) && !Misc.checkForOwner()) {
				p.getPacketSender().sendMessage("This spell can only be cast once every 30 seconds.");
				return true;
			}
			p.getPacketSender().sendString(0, "vngt:49");
			System.out.println("Sent veng timer packer; vngt:49");
			p.getPacketSender().sendMessage("<shad=330099>You cast Vengeance.");
			p.getInventory().deleteItemSet(spell.getSpell().itemsRequired(p));
			p.performAnimation(new Animation(4410));
			p.performGraphic(new Graphic(726, GraphicHeight.HIGH));
			p.getLastVengeance().reset();
			p.setHasVengeance(true);
			break;
		}
		return true;
	}
}
