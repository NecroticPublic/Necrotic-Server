package com.ruse.world.entity.updating;

import java.util.Iterator;

import com.ruse.model.Appearance;
import com.ruse.model.Direction;
import com.ruse.model.Flag;
import com.ruse.model.Gender;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.UpdateFlag;
import com.ruse.model.ChatMessage.Message;
import com.ruse.model.Locations.Location;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.movement.MovementQueue;
import com.ruse.net.packet.ByteOrder;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.net.packet.ValueType;
import com.ruse.net.packet.Packet.PacketType;
import com.ruse.net.packet.PacketBuilder.AccessType;
import com.ruse.world.World;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.impl.player.Player;

/**
 * Represents the associated player's player updating.
 * 
 * @author relex lawl
 */

public class PlayerUpdating {

	/**
	 * The max amount of players that can be added per cycle.
	 */
	private static final int MAX_NEW_PLAYERS_PER_CYCLE = 25;

	/**
	 * Loops through the associated player's {@code localPlayer} list and updates them.
	 * @return	The PlayerUpdating instance.
	 */

	public static void update(final Player player) {
		PacketBuilder update = new PacketBuilder();
		PacketBuilder packet = new PacketBuilder(81, PacketType.SHORT);
		packet.initializeAccess(AccessType.BIT);
		updateMovement(player, packet);
		appendUpdates(player, update, player, false, true);
		packet.putBits(8, player.getLocalPlayers().size());
		for (Iterator<Player> playerIterator = player.getLocalPlayers().iterator(); playerIterator.hasNext();) {
			Player otherPlayer = playerIterator.next();
			if (World.getPlayers().get(otherPlayer.getIndex()) != null && otherPlayer.getPosition().isWithinDistance(player.getPosition()) && !otherPlayer.isNeedsPlacement()) {
				updateOtherPlayerMovement(packet, otherPlayer);
				if (otherPlayer.getUpdateFlag().isUpdateRequired()) {
					appendUpdates(player, update, otherPlayer, false, false);
				}
			} else {
				playerIterator.remove();
				packet.putBits(1, 1);
				packet.putBits(2, 3);
			}
		}
		int playersAdded = 0;
		
		for(Player otherPlayer : World.getPlayers()) {
			if (player.getLocalPlayers().size() >= 79 || playersAdded > MAX_NEW_PLAYERS_PER_CYCLE)
				break;
			if (otherPlayer == null || otherPlayer == player || player.getLocalPlayers().contains(otherPlayer) || !otherPlayer.getPosition().isWithinDistance(player.getPosition()))
				continue;
			player.getLocalPlayers().add(otherPlayer);
			addPlayer(player, otherPlayer, packet);
			appendUpdates(player, update, otherPlayer, true, false);
			playersAdded++;
		}
		
		if (update.buffer().writerIndex() > 0) {
			packet.putBits(11, 2047);
			packet.initializeAccess(AccessType.BYTE);
			packet.putBytes(update.buffer());
		} else {
			packet.initializeAccess(AccessType.BYTE);
		}
		player.getSession().queueMessage(packet);
	}

	/**
	 * Adds a new player to the associated player's client.
	 * @param target	The player to add to the other player's client.
	 * @param builder	The packet builder to write information on.
	 * @return			The PlayerUpdating instance.
	 */
	private static void addPlayer(Player player, Player target, PacketBuilder builder) {
		builder.putBits(11, target.getIndex());
		builder.putBits(1, 1);
		builder.putBits(1, 1);
		int yDiff = target.getPosition().getY() - player.getPosition().getY();
		int xDiff = target.getPosition().getX() - player.getPosition().getX();
		builder.putBits(5, yDiff);
		builder.putBits(5, xDiff);
	}

	/**
	 * Updates the associated player's movement queue.
	 * @param builder	The packet builder to write information on.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateMovement(Player player, PacketBuilder builder) {
		/*
		 * Check if the player is teleporting.
		 */
		if (player.isNeedsPlacement() || player.isChangingRegion()) {
			/*
			 * They are, so an update is required.
			 */
			builder.putBits(1, 1);

			/*
			 * This value indicates the player teleported.
			 */
			builder.putBits(2, 3);

			/*
			 * This is the new player height.
			 */
			builder.putBits(2, player.getPosition().getZ());

			/*
			 * This indicates that the client should discard the walking queue.
			 */
			builder.putBits(1, player.isResetMovementQueue() ? 1 : 0);

			/*
			 * This flag indicates if an update block is appended.
			 */
			builder.putBits(1, player.getUpdateFlag().isUpdateRequired() ? 1 : 0);

			/*
			 * These are the positions.
			 */
			builder.putBits(7,
					player.getPosition().getLocalY(player.getLastKnownRegion()));
			builder.putBits(7,
					player.getPosition().getLocalX(player.getLastKnownRegion()));
		} else /*
		 * Otherwise, check if the player moved.
		 */
			if (player.getPrimaryDirection().toInteger() == -1) {
				/*
				 * The player didn't move. Check if an update is required.
				 */
				if (player.getUpdateFlag().isUpdateRequired()) {
					/*
					 * Signifies an update is required.
					 */
					builder.putBits(1, 1);

					/*
					 * But signifies that we didn't move.
					 */
					builder.putBits(2, 0);
				} else
					/*
					 * Signifies that nothing changed.
					 */
					builder.putBits(1, 0);
			} else /*
			 * Check if the player was running.
			 */
				if (player.getSecondaryDirection().toInteger() == -1) {

					/*
					 * The player walked, an update is required.
					 */
					builder.putBits(1, 1);

					/*
					 * This indicates the player only walked.
					 */
					builder.putBits(2, 1);

					/*
					 * This is the player's walking direction.
					 */

					builder.putBits(3, player.getPrimaryDirection().toInteger());

					/*
					 * This flag indicates an update block is appended.
					 */
					builder.putBits(1, player.getUpdateFlag().isUpdateRequired() ? 1 : 0);
				} else {

					/*
					 * The player ran, so an update is required.
					 */
					builder.putBits(1, 1);

					/*
					 * This indicates the player ran.
					 */
					builder.putBits(2, 2);

					/*
					 * This is the walking direction.
					 */
					builder.putBits(3, player.getPrimaryDirection().toInteger());

					/*
					 * And this is the running direction.
					 */
					builder.putBits(3, player.getSecondaryDirection().toInteger());

					/*
					 * And this flag indicates an update block is appended.
					 */
					builder.putBits(1, player.getUpdateFlag().isUpdateRequired() ? 1 : 0);
				}
	}

	/**
	 * Updates another player's movement queue.
	 * @param builder			The packet builder to write information on.
	 * @param target			The player to update movement for.
	 * @return					The PlayerUpdating instance.
	 */
	private static void updateOtherPlayerMovement(PacketBuilder builder, Player target) {
		/*
		 * Check which type of movement took place.
		 */
		if (target.getPrimaryDirection().toInteger() == -1) {
			/*
			 * If no movement did, check if an update is required.
			 */
			if (target.getUpdateFlag().isUpdateRequired()) {
				/*
				 * Signify that an update happened.
				 */
				builder.putBits(1, 1);

				/*
				 * Signify that there was no movement.
				 */
				builder.putBits(2, 0);
			} else
				/*
				 * Signify that nothing changed.
				 */
				builder.putBits(1, 0);
		} else if (target.getSecondaryDirection().toInteger() == -1) {
			/*
			 * The player moved but didn't run. Signify that an update is
			 * required.
			 */
			builder.putBits(1, 1);

			/*
			 * Signify we moved one tile.
			 */
			builder.putBits(2, 1);

			/*
			 * Write the primary sprite (i.e. walk direction).
			 */
			builder.putBits(3, target.getPrimaryDirection().toInteger());

			/*
			 * Write a flag indicating if a block update happened.
			 */
			builder.putBits(1, target.getUpdateFlag().isUpdateRequired() ? 1 : 0);
		} else {
			/*
			 * The player ran. Signify that an update happened.
			 */
			builder.putBits(1, 1);

			/*
			 * Signify that we moved two tiles.
			 */
			builder.putBits(2, 2);

			/*
			 * Write the primary sprite (i.e. walk direction).
			 */
			builder.putBits(3, target.getPrimaryDirection().toInteger());

			/*
			 * Write the secondary sprite (i.e. run direction).
			 */
			builder.putBits(3, target.getSecondaryDirection().toInteger());

			/*
			 * Write a flag indicating if a block update happened.
			 */
			builder.putBits(1, target.getUpdateFlag().isUpdateRequired() ? 1 : 0);
		}
	}

	/**
	 * Appends a player's update mask blocks.
	 * @param builder				The packet builder to write information on.
	 * @param target				The player to update masks for.
	 * @param updateAppearance		Update the player's appearance without the flag being set?
	 * @param noChat				Do not allow player to chat?
	 * @return						The PlayerUpdating instance.
	 */
	private static void appendUpdates(Player player, PacketBuilder builder, Player target, boolean updateAppearance, boolean noChat) {
		if (!target.getUpdateFlag().isUpdateRequired() && !updateAppearance)
			return;
	/*	if (player.getCachedUpdateBlock() != null && !player.equals(target) && !updateAppearance && !noChat) {
			builder.putBytes(player.getCachedUpdateBlock());
			return;
		}*/
	//	synchronized (target) {
			final UpdateFlag flag = target.getUpdateFlag();
			int mask = 0;
			if (flag.flagged(Flag.GRAPHIC) && target.getGraphic() != null) {
				mask |= 0x100;
			}
			if (flag.flagged(Flag.ANIMATION) && target.getAnimation() != null) {
				mask |= 0x8;
			}
			if (flag.flagged(Flag.FORCED_CHAT) && target.getForcedChat().length() > 0) {
				mask |= 0x4;
			}
			if (flag.flagged(Flag.CHAT) && !noChat && !player.getRelations().getIgnoreList().contains(target.getLongUsername())) {
				mask |= 0x80;
			}
			if (flag.flagged(Flag.ENTITY_INTERACTION)) {
				mask |= 0x1;
			}
			if (flag.flagged(Flag.APPEARANCE) || updateAppearance) {
				mask |= 0x10;
			}
			if (flag.flagged(Flag.FACE_POSITION)) {
				mask |= 0x2;
			}
			if (flag.flagged(Flag.SINGLE_HIT)) {
				mask |= 0x20;
			}
			if (flag.flagged(Flag.DOUBLE_HIT)) {
				mask |= 0x200;
			}
			if (flag.flagged(Flag.FORCED_MOVEMENT) && target.getForceMovement() != null) {
				mask |= 0x400;
			}
			if (mask >= 0x100) {
				mask |= 0x40;			
				builder.put((mask & 0xFF));
				builder.put((mask >> 8));
			} else {
				builder.put(mask);
			}
			if (flag.flagged(Flag.GRAPHIC) && target.getGraphic() != null) {
				updateGraphics(builder, target);
			}
			if (flag.flagged(Flag.ANIMATION) && target.getAnimation() != null) {
				updateAnimation(builder, target);
			}
			if (flag.flagged(Flag.FORCED_CHAT) && target.getForcedChat().length() > 0) {
				updateForcedChat(builder, target);
			}
			if (flag.flagged(Flag.CHAT) && !noChat && !player.getRelations().getIgnoreList().contains(target.getLongUsername())) {
				updateChat(builder, target);
			}
			if (flag.flagged(Flag.ENTITY_INTERACTION)) {
				updateEntityInteraction(builder, target);
			}
			if (flag.flagged(Flag.APPEARANCE) || updateAppearance) {
				updateAppearance(player, builder, target);
			}
			if (flag.flagged(Flag.FACE_POSITION)) {
				updateFacingPosition(builder, target);
			}
			if (flag.flagged(Flag.SINGLE_HIT)) {
				updateSingleHit(builder, target);
			}
			if (flag.flagged(Flag.DOUBLE_HIT)) {
				updateDoubleHit(builder, target);
			}
			if (flag.flagged(Flag.FORCED_MOVEMENT) && target.getForceMovement() != null) {
				updateForcedMovement(player, builder, target);
			}
			/*if (!player.equals(target) && !updateAppearance && !noChat) {
				player.setCachedUpdateBlock(cachedBuffer.buffer());
			}*/
	//	}
	}

	/**
	 * This update block is used to update player chat.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update chat for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateChat(PacketBuilder builder, Player target) {
		Message message = target.getChatMessages().get();
		byte[] bytes = message.getText();
		builder.putShort(((message.getColour() & 0xff) << 8) | (message.getEffects() & 0xff), ByteOrder.LITTLE);
		builder.put(target.getRights().ordinal());
		builder.put(target.getGameMode().ordinal()); 
		builder.put(bytes.length, ValueType.C);
		builder.putBytesReverse(bytes);
	}

	/**
	 * This update block is used to update forced player chat. 
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update forced chat for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateForcedChat(PacketBuilder builder, Player target) {
		builder.putString(target.getForcedChat());
	}

	/**
	 * This update block is used to update forced player movement.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update forced movement for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateForcedMovement(Player player, PacketBuilder builder, Player target) {
		Position position = target.getPosition();
		Position myPosition = player.getLastKnownRegion();
		builder.put((position.getLocalX(myPosition) + target.getForceMovement()[MovementQueue.FIRST_MOVEMENT_X]), ValueType.C);
		builder.put((position.getLocalY(myPosition) + target.getForceMovement()[MovementQueue.FIRST_MOVEMENT_Y]), ValueType.S);
		builder.put((position.getLocalX(myPosition) + target.getForceMovement()[MovementQueue.SECOND_MOVEMENT_X]), ValueType.S);
		builder.put((position.getLocalX(myPosition) + target.getForceMovement()[MovementQueue.SECOND_MOVEMENT_Y]), ValueType.C);
		builder.putShort(target.getForceMovement()[MovementQueue.MOVEMENT_SPEED]);
		builder.putShort(target.getForceMovement()[MovementQueue.MOVEMENT_REVERSE_SPEED], ValueType.A);
		builder.put(target.getForceMovement()[MovementQueue.MOVEMENT_DIRECTION]);
	}

	/**
	 * This update block is used to update a player's animation.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update animations for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateAnimation(PacketBuilder builder, Player target) {
		builder.putShort(target.getAnimation().getId(), ByteOrder.LITTLE);
		builder.put(target.getAnimation().getDelay(), ValueType.C);
	}

	/**
	 * This update block is used to update a player's graphics.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update graphics for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateGraphics(PacketBuilder builder, Player target) {
		builder.putShort(target.getGraphic().getId(), ByteOrder.LITTLE);
		builder.putInt(((target.getGraphic().getHeight().ordinal() * 50) << 16) + (target.getGraphic().getDelay() & 0xffff));
	}

	/**
	 * This update block is used to update a player's single hit.
	 * @param builder	The packet builder used to write information on.
	 * @param target	The player to update the single hit for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateSingleHit(PacketBuilder builder, Player target) {
		builder.putShort(target.getPrimaryHit().getDamage(), ValueType.A);
		builder.put(target.getPrimaryHit().getHitmask().ordinal());
		builder.put(target.getPrimaryHit().getCombatIcon().ordinal() - 1);
		builder.putShort(target.getPrimaryHit().getAbsorb(), ValueType.A);
		builder.putShort(target.getConstitution(), ValueType.A);
		builder.putShort(target.getSkillManager().getMaxLevel(Skill.CONSTITUTION), ValueType.A);
	}

	/**
	 * This update block is used to update a player's double hit.
	 * @param builder	The packet builder used to write information on.
	 * @param target	The player to update the double hit for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateDoubleHit(PacketBuilder builder, Player target) {
		builder.putShort(target.getSecondaryHit().getDamage(), ValueType.A);
		builder.put(target.getSecondaryHit().getHitmask().ordinal());
		builder.put(target.getSecondaryHit().getCombatIcon().ordinal() - 1);
		builder.putShort(target.getPrimaryHit().getAbsorb(), ValueType.A);
		builder.putShort(target.getConstitution(), ValueType.A);
		builder.putShort(target.getSkillManager().getMaxLevel(Skill.CONSTITUTION), ValueType.A);
	}

	/**
	 * This update block is used to update a player's face position.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update face position for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateFacingPosition(PacketBuilder builder, Player target) {
		final Position position = target.getPositionToFace();
		int x = position == null ? 0 : position.getX(); 
		int y = position == null ? 0 : position.getY();
		builder.putShort(x * 2 + 1, ValueType.A, ByteOrder.LITTLE);
		builder.putShort(y * 2 + 1, ByteOrder.LITTLE);
	}

	/**
	 * This update block is used to update a player's entity interaction.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update entity interaction for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateEntityInteraction(PacketBuilder builder, Player target) {
		Entity entity = target.getInteractingEntity();
		if (entity != null) {
			int index = entity.getIndex();
			if (entity instanceof Player)
				index += + 32768;
			builder.putShort(index, ByteOrder.LITTLE);
		} else {
			builder.putShort(-1, ByteOrder.LITTLE);
		}
	}

	/**
	 * This update block is used to update a player's appearance, this includes 
	 * their equipment, clothing, combat level, gender, head icons, user name and animations.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update appearance for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateAppearance(Player player, PacketBuilder out, Player target) {	
		Appearance appearance = target.getAppearance();
		Equipment equipment = target.getEquipment();
		PacketBuilder properties = new PacketBuilder();
		properties.put(appearance.getGender().ordinal());
		properties.put(appearance.getHeadHint());
		properties.put(target.getLocation() == Location.WILDERNESS ? appearance.getBountyHunterSkull() : -1);
		properties.putShort(target.getSkullIcon());
		if (player.getNpcTransformationId() <= 0) {
			int[] equip = new int[equipment.capacity()];
			for (int i = 0; i < equipment.capacity(); i++) {
				equip[i] = equipment.getItems()[i].getId();
			}
			if (equip[Equipment.HEAD_SLOT] > -1) {
				properties.putShort(0x200 + equip[Equipment.HEAD_SLOT]);
			} else {
				properties.put(0);
			}
			if (equip[Equipment.CAPE_SLOT] > -1) {
				properties.putShort(0x200 + equip[Equipment.CAPE_SLOT]);
			} else {
				properties.put(0);
			}
			if (equip[Equipment.AMULET_SLOT] > -1) {
				properties.putShort(0x200 + equip[Equipment.AMULET_SLOT]);
			} else {
				properties.put(0);
			}
			if (equip[Equipment.WEAPON_SLOT] > -1) {
				properties.putShort(0x200 + equip[Equipment.WEAPON_SLOT]);
			} else {
				properties.put(0);
			}
			if (equip[Equipment.BODY_SLOT] > -1) {
				properties.putShort(0x200 + equip[Equipment.BODY_SLOT]);
			} else {
				properties.putShort(0x100 + appearance.getLook()[Appearance.CHEST]);
			}
			if (equip[Equipment.SHIELD_SLOT] > -1) {
				properties.putShort(0x200 + equip[Equipment.SHIELD_SLOT]);
			} else {
				properties.put(0);
			}

			if (ItemDefinition.forId(equip[Equipment.BODY_SLOT]).isFullBody()) {
				properties.put(0);
			} else {
				properties.putShort(0x100 + appearance.getLook()[Appearance.ARMS]);
			}

			if (equip[Equipment.LEG_SLOT] > -1) {
				properties.putShort(0x200 + equip[Equipment.LEG_SLOT]);
			} else {
				properties.putShort(0x100 + appearance.getLook()[Appearance.LEGS]);
			}

			if (ItemDefinition.forId(equip[Equipment.HEAD_SLOT]).isFullHelm()) {
				properties.put(0);
			} else {
				properties.putShort(0x100 + appearance.getLook()[Appearance.HEAD]);
			}
			if (equip[Equipment.HANDS_SLOT] > -1) {
				properties.putShort(0x200 + equip[Equipment.HANDS_SLOT]);
			} else {
				properties.putShort(0x100 + appearance.getLook()[Appearance.HANDS]);
			}
			if (equip[Equipment.FEET_SLOT] > -1) {
				properties.putShort(0x200 + equip[Equipment.FEET_SLOT]);
			} else {
				properties.putShort(0x100 + appearance.getLook()[Appearance.FEET]);
			}
			if (appearance.getLook()[Appearance.BEARD] <= 0 || appearance.getGender().equals(Gender.FEMALE)) {
				properties.put(0);
			} else {
				properties.putShort(0x100 + appearance.getLook()[Appearance.BEARD]);
			}
		} else {
			properties.putShort(-1);
			properties.putShort(player.getNpcTransformationId());
		}
		properties.put(appearance.getLook()[Appearance.HAIR_COLOUR]);
		properties.put(appearance.getLook()[Appearance.TORSO_COLOUR]);
		properties.put(appearance.getLook()[Appearance.LEG_COLOUR]);
		properties.put(appearance.getLook()[Appearance.FEET_COLOUR]);
		properties.put(appearance.getLook()[Appearance.SKIN_COLOUR]);

		int skillAnim = target.getSkillAnimation();
		if(skillAnim > 0) {
			for(int i = 0; i < 7; i++)
				properties.putShort(skillAnim);
		} else {
			properties.putShort(target.getCharacterAnimations().getStandingAnimation());
			properties.putShort(target.getCharacterAnimations().getTurnIndex());
			properties.putShort(target.getCharacterAnimations().getWalkingAnimation());
			properties.putShort(target.getCharacterAnimations().getTurn180Index());
			properties.putShort(target.getCharacterAnimations().getTurn90CWIndex());
			properties.putShort(target.getCharacterAnimations().getTurn90CCWIndex());
			properties.putShort(target.getCharacterAnimations().getRunningAnimation());
		}

		properties.putLong(target.getLongUsername());
		properties.put(target.getSkillManager().getCombatLevel());
		properties.putShort(target.getRights().ordinal());
		properties.putShort(target.getLoyaltyTitle().ordinal());

		out.put(properties.buffer().writerIndex(), ValueType.C);
		out.putBytes(properties.buffer());
	}

	/**
	 * Resets the associated player's flags that will need to be removed
	 * upon completion of a single update.
	 * @return	The PlayerUpdating instance.
	 */
	public static void resetFlags(Player player) {
		player.getUpdateFlag().reset();
		player.setRegionChange(false);
		player.setTeleporting(false).setForcedChat("");
		player.setResetMovementQueue(false);
		player.setNeedsPlacement(false);
		player.setPrimaryDirection(Direction.NONE);
		player.setSecondaryDirection(Direction.NONE);
	}

}