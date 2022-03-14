package com.ruse.net.packet.impl;

import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.util.NameUtils;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

/**
 * This packet listener is called when a player is doing something relative
 * to their friends or ignore list, such as adding or deleting a player from said list.
 * 
 * @author relex lawl
 */

public class PlayerRelationPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		try {
			long username = packet.readLong();
			switch (packet.getOpcode()) {
			case ADD_FRIEND_OPCODE:
				player.getRelations().addFriend(username);
				break;
			case ADD_IGNORE_OPCODE:
				player.getRelations().addIgnore(username);
				break;
			case REMOVE_FRIEND_OPCODE:
				player.getRelations().deleteFriend(username);
				break;
			case REMOVE_IGNORE_OPCODE:
				player.getRelations().deleteIgnore(username);
				break;
			case SEND_PM_OPCODE:
				Player friend = World.getPlayerByName(Misc.formatText(NameUtils.longToString(username)).replaceAll("_", " "));
				int size = packet.getSize();
				byte[] message = packet.readBytes(size);
				player.getRelations().message(friend, message, size);
				break;
			}
		} catch (Exception e) {

		}
	}

	public static final int ADD_FRIEND_OPCODE = 188;

	public static final int REMOVE_FRIEND_OPCODE = 215;

	public static final int ADD_IGNORE_OPCODE = 133;

	public static final int REMOVE_IGNORE_OPCODE = 74;

	public static final int SEND_PM_OPCODE = 126;
}
