package com.ruse.net.packet.impl;

import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.webhooks.discord.DiscordMessager;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPunishment.Jail;
import com.ruse.world.entity.impl.player.Player;

public class BadPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		player.getSkillManager().stopSkilling();
		World.sendMessage(player.getUsername()+" sent packet 109! Please report this to a Developer.");
		DiscordMessager.sendStaffMessage("PACKET 109 was sent by "+player.getUsername()+", badlistener jailed & kicked.");
		PlayerLogs.log(player.getUsername(), "Sent PACKET 109 and was jailed/kicked by badlistener.");
		if (!Jail.isJailed(player)) {
			Jail.jailPlayer(player);
		}
		World.deregister(player);
		return;
	}

}
