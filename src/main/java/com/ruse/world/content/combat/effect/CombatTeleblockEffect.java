package com.ruse.world.content.combat.effect;

import com.ruse.engine.task.Task;
import com.ruse.world.entity.impl.player.Player;

/**
 * A {@link Task} implementation that will unteleblock the player after the
 * counter reaches 0.
 * 
 * @author lare96
 */
public class CombatTeleblockEffect extends Task {

    /** The player attached to this task. */
    private Player player;

    /**
     * Create a new {@link CombatTeleblockEffect}.
     * 
     * @param player
     *            the player attached to this task.
     */
    public CombatTeleblockEffect(Player player) {
        super(1, false);
        super.bind(player);
        this.player = player;
    }

    @Override
    public void execute() {

        // Timer is at or below 0 so send them a message saying they're not
        // blocked anymore
        if (player.getTeleblockTimer() <= 0) {
            player.getPacketSender().sendMessage(
                "You are no longer teleblocked.");
            this.stop();
            return;
        }

        // Otherwise just decrement the timer.
        player.decrementTeleblockTimer();
    }
}
