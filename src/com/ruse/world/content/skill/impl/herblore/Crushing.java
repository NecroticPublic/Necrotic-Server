package com.ruse.world.content.skill.impl.herblore;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

/**
 * Given life by Crimson on 5/14/2017.
 * Made functional by crimson tho u fucking goof
 */
public enum Crushing {
        MUDRUNE(4698, 9594),
        UNICORNHORN(237, 235),
        CHOCOLATEBAR(1973, 1975),
        GOATHORN(9735, 9736),
        KEBBIT(10109, 10111);


        private int input, output;

    private Crushing(int input, int output) {
            this.input = input;
            this.output = output;
        }

    public int getInput() {
        return input;
    }

    public int getOutput() {
        return output;
    }
    
    public static int pestle = 233;

    public static void handleCrushing(Player player, int index) {
        if(!player.getInventory().contains(pestle)) {
            player.getPacketSender().sendMessage("You will need a pestle first.");
        	return;
        }
        if (Crushing.values()[index] == null) {
        	return;
        }

        player.getSkillManager().stopSkilling();

       player.setCurrentTask(new Task(1, player, false) {
 
            @Override
            public void execute() {
            	if (!player.getInventory().contains(pestle) || !player.getInventory().contains(Crushing.values()[index].getInput())) {
            		stop();
            		return;
            	}
            	if (player.getInventory().isFull() && ItemDefinition.forId(Crushing.values()[index].getInput()).isStackable()) {
            		player.getPacketSender().sendMessage("Your inventory is full, please make room first.");
            		stop();
            		return;
            	}
            	
                player.getInventory().delete(Crushing.values()[index].getInput(), 1);
                player.performAnimation(new Animation(364));
                player.getInventory().add(Crushing.values()[index].getOutput(), 1);
            }
        });
        TaskManager.submit(player.getCurrentTask());
        return;
    }

   }

