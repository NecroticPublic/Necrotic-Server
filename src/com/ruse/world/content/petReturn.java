package com.ruse.world.content;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.content.skill.impl.summoning.BossPets.BossPet;
import com.ruse.world.entity.impl.player.Player;

public class petReturn  implements Runnable{
	
	private static final ExecutorService SERVICE = Executors.newCachedThreadPool();


	
	public static void main(Player player){
		SERVICE.execute(() -> {
	    	try {
				player.getInventory().delete(1561, 1);
				int invSpace = player.getInventory().getFreeSlots();
				int daCount = 0;
				for (int i = 0; i < BossPet.values().length; i++) {
					//System.out.println("daCount < invSpace "+(boolean) (daCount < invSpace));
					//System.out.println("getBossPet("+i+"), "+ItemDefinition.forId(BossPet.values()[i].itemId).getName()+" = "+player.getBossPet(i));
					if (daCount < invSpace && player.getBossPet(i)) {
						player.getInventory().add(BossPet.values()[i].itemId, 1);
						player.getPacketSender().sendMessage("Returned your "+ItemDefinition.forId(BossPet.values()[i].itemId).getName()+" to your inventory.");
						daCount++;
					} else 	if (daCount >= invSpace && player.getBossPet(i) && !player.getBank(0).isFull()) {
						player.getBank(0).add(BossPet.values()[i].itemId, 1);
						player.getPacketSender().sendMessage("Returned your "+ItemDefinition.forId(BossPet.values()[i].itemId).getName()+" to your bank.");
						daCount++;
					}
				}
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
		});

	}



	@Override
	public void run() {
		System.out.println("Pet Redeem thread");
	}
	
}
