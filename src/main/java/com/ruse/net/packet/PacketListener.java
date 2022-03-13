package com.ruse.net.packet;

import com.ruse.world.entity.impl.player.Player;


/**
 * Represents a Packet received from client.
 * 
 * @author Gabriel Hannason
 */

public interface PacketListener {
	
	/**
	 * Executes the packet.
	 * @param player	The player to which execute the packet for.
	 * @param packet	The packet being executed.
	 */
	public void handleMessage(Player player, Packet packet);
}
