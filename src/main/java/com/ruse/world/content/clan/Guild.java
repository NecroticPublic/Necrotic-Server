package com.ruse.world.content.clan;

import com.ruse.model.Position;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.content.clan.ClanChat;
import com.ruse.world.content.clan.ClanChatManager;

public class Guild {

    public static boolean handleClanButtons(Player player, int id) {
        final ClanChat clan = ClanChatManager.getClanChatChannel(player);
        if (clan == null) {
            return false;
        }
        switch(id) {
            case 29456:
                if(player.getCurrentClanChat() == null) {
                    player.getPacketSender().sendMessage("You're not in a ClanChat.");
                    break;
                }
            	if (player.getCurrentClanChat().getGuild() == true) {
            		player.getPacketSender().sendMessage("You own a guild, but cannot use it yet.");
            		break;
            	} else if (player.getCurrentClanChat().getGuild() == false) {
            		player.getPacketSender().sendMessage("Guilds are not ready yet. Be patient!");
            		break;
            	} else {
            		player.getPacketSender().sendMessage("lmfao how did u get here?");
            	}
                int z = player.getCurrentClanChat().getIndex() * 4;
                TeleportHandler.teleportPlayer(player, new Position(2512, 3860, z), TeleportType.NORMAL);
                player.getPacketSender().sendMessage("You teleport to your guilds home.");
                break;
        }
            return false;
    }
}

