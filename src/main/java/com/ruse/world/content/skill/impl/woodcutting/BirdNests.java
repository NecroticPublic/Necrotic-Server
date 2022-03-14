package com.ruse.world.content.skill.impl.woodcutting;

import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.player.Player;


/**
 * @author Optimum
 * I do not give permission to 
 * release this anywhere else
 */

public class BirdNests {

	/**
	 * Ints.
	 */

	public static final int[] BIRD_NEST_IDS = {5070, 5071, 5072, 5073, 5074};
	public static final int[] SEED_REWARDS = {5312, 5313, 5314, 5315, 5316, 5283, 5284, 5285, 5286, 5287, 5288, 5289, 5290, 5317};
	public static final int[] RING_REWARDS = {1635, 1637, 1639, 1641, 1643};
	public static final int EMPTY = 5075;
	public static final int RED = 5076;
	public static final int BLUE = 5077;
	public static final int GREEN = 5078;
	public static final int AMOUNT = 1;


	/**
	 * Check if the item is a nest
	 *
	 */
	public static boolean isNest(final int itemId) {
		for(int nest : BIRD_NEST_IDS) {
			if(nest == itemId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Generates the random drop and creates a ground item
	 * where the player is standing
	 */

	public static void dropNest(Player p){
		if(p.getPosition().getZ() > 0) {
			return;
		}
		if(Misc.getRandom(60) == 1) {
			Item nest = null;
			int r = Misc.getRandom(1000);
			if(r >= 0 && r <= 640){
				nest = new Item(5073);
			}
			else if(r >= 641 && r <= 960){
				nest = new Item(5074);
			}
			else if(r >= 961){
				int random = Misc.getRandom(2);
				if(random == 1){
					nest = new Item(5072);
				}else if (random == 2){
					nest = new Item(5071);
				}else{
					nest = new Item(5070);
				}
			}
			if(nest != null) {
				nest.setAmount(1);
				GroundItemManager.spawnGroundItem(p, new GroundItem(nest, p.getPosition().copy(), p.getUsername(), false, 80, true, 80));
				p.getPacketSender().sendMessage("A bird's nest falls out of the tree!");
			}
		}
	}

	/**
	 * 
	 * Searches the nest.
	 * 
	 */

	public static final void searchNest(Player p, int itemId) {
		if(p.getInventory().getFreeSlots() <= 0) {
			p.getPacketSender().sendMessage("You do not have enough free inventory slots to do this.");
			return;
		}
		p.getInventory().delete(itemId, 1);
		eggNest(p, itemId);
		seedNest(p, itemId);
		ringNest(p, itemId);
		p.getInventory().add(EMPTY, AMOUNT);
	}

	/**
	 * 
	 * Determines what loot you get
	 *  from ring bird nests
	 *  
	 */
	public static final void ringNest(Player p, int itemId){
		if(itemId == 5074){
			int random = Misc.getRandom(1000);
			if(random >= 0 && random <= 340){
				p.getInventory().add(RING_REWARDS[0], AMOUNT);
			}else if (random >= 341 && random <= 750){
				p.getInventory().add(RING_REWARDS[1], AMOUNT);
			}else if (random >= 751 && random <= 910){
				p.getInventory().add(RING_REWARDS[2], AMOUNT);
			}else if (random >= 911 && random <= 989){
				p.getInventory().add(RING_REWARDS[3], AMOUNT);
			}else if (random >= 990){
				p.getInventory().add(RING_REWARDS[4], AMOUNT);
			}
		}
	}

	/**
	 * 
	 * Determines what loot you get
	 *  from seed bird nests
	 *  
	 */

	private static final void seedNest(Player p, int itemId){
		if(itemId == 5073){
			int random = Misc.getRandom(1000);
			if(random >= 0 && random <= 220){
				p.getInventory().add(SEED_REWARDS[0], AMOUNT);
			}
			else if(random >= 221 && random <= 350){
				p.getInventory().add(SEED_REWARDS[1], AMOUNT);
			}
			else if(random >= 351 && random <= 400){
				p.getInventory().add(SEED_REWARDS[2], AMOUNT);
			}
			else if(random >= 401 && random <= 430){
				p.getInventory().add(SEED_REWARDS[3], AMOUNT);
			}
			else if(random >= 431 && random <= 440){
				p.getInventory().add(SEED_REWARDS[4], AMOUNT);
			}
			else if(random >= 441 && random <= 600){
				p.getInventory().add(SEED_REWARDS[5], AMOUNT);
			}
			else if(random >= 601 && random <= 700){
				p.getInventory().add(SEED_REWARDS[6], AMOUNT);
			}
			else if(random >= 701 && random <= 790){
				p.getInventory().add(SEED_REWARDS[7], AMOUNT);
			}
			else if(random >= 791 && random <= 850){
				p.getInventory().add(SEED_REWARDS[8], AMOUNT);
			}
			else if(random >= 851 && random <= 900){
				p.getInventory().add(SEED_REWARDS[9], AMOUNT);
			}
			else if(random >= 901 && random <= 930){
				p.getInventory().add(SEED_REWARDS[10], AMOUNT);
			}
			else if(random >= 931 && random <= 950){
				p.getInventory().add(SEED_REWARDS[11], AMOUNT);
			}
			else if(random >= 951 && random <= 970){
				p.getInventory().add(SEED_REWARDS[12], AMOUNT);
			}
			else if(random >= 971 && random <= 980){
				p.getInventory().add(SEED_REWARDS[13], AMOUNT);
			} else {
				p.getInventory().add(SEED_REWARDS[0], AMOUNT);
			}
		}
	}

	/**
	 * 
	 * Egg nests
	 * 
	 */

	public static final void eggNest(Player p, int itemId){
		if(itemId == 5070){
			p.getInventory().add(RED, AMOUNT);
		}
		if(itemId == 5071){
			p.getInventory().add(GREEN, AMOUNT);
		}
		if(itemId == 5072){
			p.getInventory().add(BLUE, AMOUNT);
		}
	}

}