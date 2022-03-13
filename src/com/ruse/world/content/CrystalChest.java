package com.ruse.world.content;

import java.util.ArrayList;
import java.util.List;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.GameObject;
import com.ruse.model.Graphic;
import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

public class CrystalChest {

	public static void handleChest(final Player p, final GameObject chest, boolean command) {
		if(!p.getClickDelay().elapsed(3000)) 
			return;
		if(!p.getInventory().contains(989)) {
			p.getPacketSender().sendMessage("This chest can only be opened with a Crystal key.");
			return;
		}
		if (command) {
			p.performAnimation(new Animation(1818));
		} else {
			p.performAnimation(new Animation(827));
		}
		p.performGraphic(new Graphic(1322));
		p.getInventory().delete(989, 1);
		p.getPacketSender().sendMessage("You open the chest..");
		TaskManager.submit(new Task(1, p, false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 2:
					Item item = itemRewards[Misc.randomMinusOne(itemRewards.length)];
					p.getInventory().add(item);
					if (item.getDefinition() != null && item.getDefinition().getName() != null) {
						p.getPacketSender().sendMessage("..and find "+Misc.anOrA(item.getDefinition().getName())+" "+item.getDefinition().getName()+"!");
					} else {
						p.getPacketSender().sendMessage("..and find an item!");
					}
					if (chest != null && !command) {
						CustomObjects.objectRespawnTask(p, new GameObject(173 , chest.getPosition().copy(), 10, 0), chest, 10);
					}
					stop();
					break;
				}
				tick++;
			}
		});
		p.getClickDelay().reset();
	}
	
	public static void sendRewardInterface(Player player) {
		try {
			
			List<Item> list = new ArrayList<Item>();
			for (int i = 0; i < itemRewards.length; i++) {
				list.add(itemRewards[i]);
			}
			
			resetInterface(player);
			
			player.getPacketSender().sendString(8144, "Crystal Chest loot table").sendInterface(8134);
			
			int index = 0, start = 8147, cap = 8196, secondstart = 12174, secondcap = 12224, index2 = 0;
			boolean newline = false;
			
			for (int i = 0; i < list.size(); i++) {
				
				
				if (ItemDefinition.forId(list.get(i).getId()) == null || ItemDefinition.forId(list.get(i).getId()).getName() == null) {
					continue;
				}
				
				int toSend = 8147 + index;
				
				if (index + start > cap) {
					newline = true;
				}
				
				if (newline) {
					toSend = secondstart + index2;
				}
				
				if (newline && toSend >= secondcap) {
					player.getPacketSender().sendMessage("<shad=ffffff>"+list.get(i).getAmount()+"x <shad=0>"
							+ Misc.getColorBasedOnValue(ItemDefinition.forId(list.get(i).getId()).getValue()*list.get(i).getAmount())
							+ ItemDefinition.forId(list.get(i).getId()).getName()+".");
		
					continue;
				}
				player.getPacketSender().sendString(toSend, list.get(i).getAmount()+"x "
						+ Misc.getColorBasedOnValue(ItemDefinition.forId(list.get(i).getId()).getValue()*list.get(i).getAmount())
						+ ItemDefinition.forId(list.get(i).getId()).getName()+".");
				
				if (newline) {
					index2++;
				} else {
					index++;
				}
				
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void resetInterface(Player player) {
		for(int i = 8145; i < 8196; i++)
			player.getPacketSender().sendString(i, "");
		for(int i = 12174; i < 12224; i++)
			player.getPacketSender().sendString(i, "");
		player.getPacketSender().sendString(8136, "Close window");
	}

	public static final Item[] itemRewards =  {
			//shit items
			new Item(2631, 1), //highway mask
			new Item(2643, 1), //black cavalier
			new Item(9470, 1), //gnome scarf
			new Item(10589, 1), //Granite Helm
			new Item(10564, 1), //Granite Body
			new Item(6809, 1), //Granite legs
			new Item(14499, 1), //dagon'hai hat
			new Item(14497, 1), //dagon'hai robe top
			new Item(14501, 1), //dagon'hai robe bottom
			new Item(6, 1), //cannon base
			new Item(12, 1), //cannon furnace
			new Item(10, 1), //cannon barrels
			new Item(8, 1), // cannon stand
			new Item(11118, 1), //Combat Bracelet
			new Item(2570, 1), //ring of life
			new Item(11710, 1), //set 14 Godsword shard 1
			new Item(11712, 1), //set 15 Godsword shard 2
			new Item(11714, 1), //set 16 Godsword shard 3
			new Item(11732, 1), //set 17 Dragon Boots
			new Item(15332, 1), //set 23 Overload
			new Item(6918, 1), //set 24 Infinity Armor 1
			new Item(6916, 1), //set 25 Infinity Armor 2
			new Item(6924, 1), //set 26 Infinity Armor 3
			new Item(6922, 1), //set 27 Infinity Armor 4
			new Item(6920, 1), //set 28 Infinity Armor 5
			new Item(7400, 1), //echanted
			new Item(7399, 1), //echanted
			new Item(7398, 1), //enchanted
			new Item(4089, 1), //blue mystic
			new Item(4091, 1), //blu mystic
			new Item(4093, 1), //blu mystic
			new Item(4095, 1), //blu mystic
			new Item(4097, 1), //blu Mystic
			new Item(892, 100), //rune arrows
			new Item(4675, 1), //ancient staff
			new Item(386, 50), //shark
			new Item(3025, 25), //super restore
			new Item(2453, 25), //antifire
			new Item(6326, 1), //snakeskin
			new Item(6322, 1), //snake
			new Item(6324, 1), //snk
			new Item(6328, 1), //snksk
			new Item(1949, 1), //chef's hat
			new Item(685, 1), //old boot
			new Item(2460, 1), //flowers
			new Item(2462, 1),
			new Item(2464, 1),
			new Item(2466, 1),
			new Item(2468, 1),
			new Item(2470, 1),
			new Item(2472, 1),
			new Item(2474, 1),
			new Item(2476, 1), //end of flowers
			new Item(15616, 1), //donor infinity
			new Item(15612, 1), //donor infinity
			new Item(4045, 1), //explosive potion
			new Item(3751, 1), //berserker helm
			//medium items
			new Item(19670, 1), //Vote Scroll
			new Item(2, 100), //cannonball
			new Item(9244, 50), //Dragon Bolts (e)
			new Item(6199, 1), //Mystery Box
			new Item(6570, 1), //Firecapel
			//amazing items
			new Item(19670, 5), //Vote Scroll
			new Item(2579, 1), //set 41 Ranger Boots
			new Item(2581, 1), //set 42 Robin Hood Hat
			new Item(19352, 1) //dragon sq (or) kit
		};
	
}
