package com.ruse.world.content.minigames.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Graphic;
import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.world.entity.impl.player.Player;

/**
 * Created by brandon on 5/3/2016.
 */
public class trioMinigame {



    public static void handleTokenRemoval(final Player player) {
        if (player.getMinigameAttributes().getTrioAttuibutes().joinedBossRoom())
            return;
        player.getMinigameAttributes().getTrioAttuibutes().setJoinedBossRoom(true);
        //  player.getPacketSender().sendMessage("Sumbiting the task.");
        //task = 1 * 600 = taskDelay

        TaskManager.submit(new Task(75, player, false) {
            @Override
            public void execute() {
                if (!player.getMinigameAttributes().getTrioAttuibutes().joinedBossRoom()) {
               //     handleNPCdeSpawning(player);
                    this.stop();
                    return;
                    //fail safe
                }
                if (player.getLocation() != Locations.Location.TRIO_ZONE) {
                    player.getMinigameAttributes().getTrioAttuibutes().setJoinedBossRoom(false);
                    this.stop();
                    return;
                    // fail safe #2
                }
                if (player.getInventory().contains(11180)) {
                    player.getInventory().delete(11180, 1);
                    player.performGraphic(new Graphic(1386));
                    player.getPacketSender().sendMessage("You've spent 1 minute in the trio arena, one of your tokens were destroyed!");
                } else {
                    player.getCombatBuilder().cooldown(true);
                    player.getMovementQueue().reset();
                    player.moveTo(new Position(2902, 5204)); // adam change this to where ever the npc you talk to to start minigame is.
                    player.getMinigameAttributes().getTrioAttuibutes().setJoinedBossRoom(false);
                    player.getPacketSender().sendMessage("You've run out of tokens.");
                    this.stop();
                }
            }
        });
    }
}

