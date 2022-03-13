package com.ruse.net.packet.impl;

import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Locations.Location;
import com.ruse.model.Skill;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.world.World;
import com.ruse.world.content.combat.magic.CombatSpell;
import com.ruse.world.content.combat.magic.CombatSpells;
import com.ruse.world.content.skill.impl.dungeoneering.UltimateIronmanHandler;
import com.ruse.world.entity.impl.player.Player;

public class MagicOnPlayerPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int playerIndex = packet.readShortA();
		if(playerIndex < 0 || playerIndex > World.getPlayers().capacity())
			return;
		int spellId = packet.readLEShort();
		if (spellId < 0) {
			return;
		}

		Player attacked = World.getPlayers().get(playerIndex);
		
		if (UltimateIronmanHandler.hasItemsStored(player) && player.getLocation() != Location.DUNGEONEERING) {
			player.getPacketSender().sendMessage("You must claim your stored items at Dungeoneering first.");
			return;
		}

		if (player != null && attacked != null && player.getRights().OwnerDeveloperOnly()) {
			player.getPacketSender().sendMessage("Used spell id: "+spellId+" on target: "+attacked.getUsername());
		}
		
		if (attacked == null || attacked.equals(player)) {
			player.getMovementQueue().reset();
			return;
		}

		CombatSpell spell = CombatSpells.getSpell(spellId);
		if(spell == null) {
			player.getMovementQueue().reset();
			return;
		}
		
		if(attacked.getConstitution() <= 0) {
			player.getMovementQueue().reset();
			return;
		}
		
		// Start combat!
		if (spell == CombatSpells.VENGEANCE_OTHER.getSpell()) {
			player.getMovementQueue().reset();
			player.setPositionToFace(attacked.getPosition());
			
			if(player.getSkillManager().getCurrentLevel(Skill.MAGIC) < 93) {
				player.getPacketSender().sendMessage("You need a Magic level of at least 93 to cast this spell.");
			} else if (!player.getInventory().containsAll(557, 560, 9075)) {
				player.getPacketSender().sendMessage("You do not have the required items to cast this spell.");
			} else if (!(player.getInventory().getAmount(557) >= 10 && player.getInventory().getAmount(560) >= 2 && player.getInventory().getAmount(9075) >= 3)) {
				player.getPacketSender().sendMessage("You don't have the required amount of runes to cast this spell.");
			} else if(!attacked.getLocation().isAidingAllowed() || attacked.getLocation() == Location.DUEL_ARENA || attacked.getDueling().duelingStatus > 1) {
				player.getPacketSender().sendMessage("This spell cannot be cast here.");
			} else if(attacked.hasVengeance()) {
				player.getPacketSender().sendMessage("That player already has Vengeance active.");
			} else if(!player.getLastVengeance().elapsed(30000)) {
				player.getPacketSender().sendMessage("You can only cast a Vengeance once every 30 seconds.");
			} else {

				player.getInventory().delete(557, 10);
				player.getInventory().delete(560, 2);
				player.getInventory().delete(9075, 3);
				
				player.performAnimation(new Animation(4411));
				
				player.getPacketSender().sendMessage("You cast Vengeance on "+attacked.getUsername()+".");
				attacked.getPacketSender().sendMessage("Your were given Vengeance by "+player.getUsername());
				player.getLastVengeance().reset();
				attacked.performGraphic(new Graphic(725));
				attacked.setHasVengeance(true);
				
				player.getSkillManager().addExperience(Skill.MAGIC, 108);
			}
		} else {
			player.setPositionToFace(attacked.getPosition());
			player.getCombatBuilder().resetCooldown();
			player.setCastSpell(spell);
			player.getCombatBuilder().attack(attacked);
		}
	}

}
