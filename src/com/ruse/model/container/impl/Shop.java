package com.ruse.model.container.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruse.GameSettings;
import com.ruse.ReducedSellPrice;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.ShopRestockTask;
import com.ruse.model.GameMode;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.Skill;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.input.impl.EnterAmountToBuyFromShop;
import com.ruse.model.input.impl.EnterAmountToSellToShop;
import com.ruse.util.JsonLoader;
import com.ruse.util.Misc;
import com.ruse.webhooks.discord.DiscordMessager;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.content.minigames.impl.RecipeForDisaster;
import com.ruse.world.content.skill.impl.dungeoneering.UltimateIronmanHandler;
import com.ruse.world.content.skill.impl.summoning.BossPets.BossPet;
import com.ruse.world.entity.impl.player.Player;

/**
 * Messy but perfect Shop System
 * @author Gabriel Hannason
 */

public class Shop extends ItemContainer {

	/*
	 * The shop constructor
	 */
	public Shop(Player player, int id, String name, Item currency, Item[] stockItems) {
		super(player);
		if (stockItems.length > 42)
			throw new ArrayIndexOutOfBoundsException("Stock cannot have more than 40 items; check shop[" + id + "]: stockLength: " + stockItems.length);
		this.id = id;
		this.name = name.length() > 0 ? name : "General Store";
		this.currency = currency;
		this.originalStock = new Item[stockItems.length];
		for(int i = 0; i < stockItems.length; i++) {
			Item item = new Item(stockItems[i].getId(), stockItems[i].getAmount());
			add(item, false);
			this.originalStock[i] = item;
		}
	}

	private final int id;

	private String name;

	private Item currency;

	private Item[] originalStock;

	public Item[] getOriginalStock() {
		return this.originalStock;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public Shop setName(String name) {
		this.name = name;
		return this;
	}
	public Item getCurrency() {
		return currency;
	}

	public Shop setCurrency(Item currency) {
		this.currency = currency;
		return this;
	}

	private boolean restockingItems;

	public boolean isRestockingItems() {
		return restockingItems;
	}

	public void setRestockingItems(boolean restockingItems) {
		this.restockingItems = restockingItems;
	}

	/**
	 * Opens a shop for a player
	 * @param player	The player to open the shop for
	 * @return			The shop instance
	 */
	public Shop open(Player player) {
		if(player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
			if(id != RECIPE_FOR_DISASTER_STORE 
			&& id != VOTING_REWARDS_STORE 
			&& id != PKING_REWARDS_STORE 
			&& id != 81
			&& id != 9 
			&& id != 10 
			&& id != 8 
			&& id != 43 
			&& id != 79
			&& id != 85 //trader shop
			&& id != 31 //member druid
			&& id != 82 //teleports shop
			&& id != GENERAL_STORE
			&& id != 40 
			&& id != 47 //slayer shop
			&& id != STARDUST_EXCHANGE_STORE 
			&& id != GAMBLING_STORE 
			&& id != 83
			&& id != PRESTIGE_STORE 
			&& id != 45 //45 = dung guy
			&& id != MEMBERS_STORE_I 
			&& id != MEMBERS_STORE_II 
			&& id != MEMBERS_STORE_III 
			&& id != 17 
			&& id != 2 //17 = brother jerid's ironman items, 2 = amunition store
			&& id != 33 
			&& id != 39 
			&& id != 11 
			&& id != 34 
			&& id != 14 
			&& id != 13 
			&& id != 18 
			&& id != 15 
			&& id != 21 
			&& id != 44//33=energy fragment, 39 = agilit, 11=merchant's store, 34 = crafting items, 14 = wilfred's items, 13 = mining instructor's store, 18 = fishing, 15 = firemaking, 21 = farm, 44 = dung
			&& id != 22 
			&& id != 42 
			&& id != 35 
			&& id != 32 
			&& id != 23 
			&& id != 38
			&& id != 91
			&& id != 92
			&& id != 93
			&& id != 94
					&& id != 95
					&& id != 96
					&& id != 97
					&& id != 98
			&& id != 30) { //22 + 23 == pikkupstix's materials, 38 = hunting store, 30 = herblore store
				player.getPacketSender().sendMessage("You're unable to access this shop as a "+player.getGameMode().toString().toLowerCase().replace("_", " ")+" player.");
				return this;
			}
		}
		setPlayer(player);
		getPlayer().getPacketSender().sendInterfaceRemoval().sendClientRightClickRemoval();
		getPlayer().setShop(ShopManager.getShops().get(id)).setInterfaceId(INTERFACE_ID).setShopping(true);
		refreshItems();
		if(Misc.getMinutesPlayed(getPlayer()) <= 190)
			getPlayer().getPacketSender().sendMessage("Note: When selling an item to a store, it loses 15% of its original value.");
		return this;
	}

	/**
	 * Refreshes a shop for every player who's viewing it
	 */
	public void publicRefresh() {
		Shop publicShop = ShopManager.getShops().get(id);
		if (publicShop == null)
			return;
		publicShop.setItems(getItems());
		for (Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if (player.getShop() != null && player.getShop().id == id && player.isShopping())
				player.getShop().setItems(publicShop.getItems());
		}
	}

	/**
	 * Checks a value of an item in a shop
	 * @param player		The player who's checking the item's value
	 * @param slot			The shop item's slot (in the shop!)
	 * @param sellingItem	Is the player selling the item?
	 */
	public void checkValue(Player player, int slot, boolean sellingItem) {
		this.setPlayer(player);
		if (UltimateIronmanHandler.hasItemsStored(player) && player.getLocation() != Location.DUNGEONEERING) {
			player.getPacketSender().sendMessage("<shad=0>@red@You cannot use the shop until you claim your stored Dungeoneering items.");
			return;
		}
		Item shopItem = new Item(getItems()[slot].getId());
		if(!player.isShopping()) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		Item item = sellingItem ? player.getInventory().getItems()[slot] : getItems()[slot];
		if(item.getId() == 995)
			return;
		if(sellingItem) {
			if(!shopBuysItem(id, item)) {
				player.getPacketSender().sendMessage("You cannot sell this item to this store.");
				return;
			}
		}
		int finalValue = 0;
		String finalString = sellingItem ? ""+ItemDefinition.forId(item.getId()).getName()+": shop will buy for " : ""+ItemDefinition.forId(shopItem.getId()).getName()+" currently costs ";
		String s = currency.getDefinition().getName().toLowerCase().endsWith("s") ? currency.getDefinition().getName().toLowerCase() : currency.getDefinition().getName().toLowerCase() + "s";
		if(getCurrency().getId() != -1) {
			finalValue = ItemDefinition.forId(item.getId()).getValue();
			/** CUSTOM CURRENCY, CUSTOM SHOP VALUES **/
			if(id == TOKKUL_EXCHANGE_STORE || id == STARDUST_EXCHANGE_STORE || id == ENERGY_FRAGMENT_STORE || id == AGILITY_TICKET_STORE || id == GRAVEYARD_STORE || id == BARROWS_STORE || id == MEMBERS_STORE_I ||id == MEMBERS_STORE_II || id == MEMBERS_STORE_III) {
				Object[] obj = ShopManager.getCustomShopData(id, item.getId());
				if(obj == null)
					return;
				finalValue = (int) obj[0];
				s = (String) obj[1];
			}
			if(sellingItem) {
				if(finalValue != 1) {
					finalValue = (int) (finalValue * 0.85);	
				}
			}
			finalString += ""+(int) finalValue+" "+s+""+shopPriceEx((int) finalValue)+".";
		} else {
			Object[] obj = ShopManager.getCustomShopData(id, item.getId());
			if(obj == null)
				return;
			finalValue = (int) obj[0];
			if(sellingItem) {
				if(finalValue != 1) {
					finalValue = (int) (finalValue * 0.85);	
				}
			}
			finalString += ""+finalValue+" " + (String) obj[1] + ".";
		}
		
		if (sellingItem) {
			for (ReducedSellPrice r : ReducedSellPrice.values()) {
				if (r.getUnNotedId() == item.getId() || r.getNotedId() == item.getId()) {
					finalString = ItemDefinition.forId(item.getId()).getName()+": shop will buy for "+r.getSellValue()+" "+s+""+shopPriceEx(r.getSellValue())+".";
					//finalString = item.getDefinition().getName() + ": shop will buy for 1000 coins (1K).";
				}
			}

		}
		
		if(player!= null && finalValue > 0) {
			player.getPacketSender().sendMessage(finalString);
			return;
		}
	}

	public void sellItem(Player player, int slot, int amountToSell) {
		this.setPlayer(player);
		if(!player.isShopping() || player.isBanking()) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		/*if(id == GENERAL_STORE) {
			if(player.getRights() == PlayerRights.ADMINISTRATOR) {
				player.getPacketSender().sendMessage("You cannot sell items as a staff member who can spawn.");
				return;
			}
		}*/
		if(!player.isShopping() || player.isBanking()) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		} 
		if (UltimateIronmanHandler.hasItemsStored(player) && player.getLocation() != Location.DUNGEONEERING) {
			player.getPacketSender().sendMessage("<shad=0>@red@You cannot use the shop until you claim your stored Dungeoneering items.");
			return;
		}
		Item itemToSell = player.getInventory().getItems()[slot];
		if(!itemToSell.sellable()) {
			player.getPacketSender().sendMessage("This item cannot be sold.");
			return;
		}
		if(!shopBuysItem(id, itemToSell)) {
			player.getPacketSender().sendMessage("You cannot sell this item to this store.");
			return;
		}
		if(!player.getInventory().contains(itemToSell.getId()) || itemToSell.getId() == 995)
			return;
		if(this.full(itemToSell.getId()))
			return;
		if(player.getInventory().getAmount(itemToSell.getId()) < amountToSell)
			amountToSell = player.getInventory().getAmount(itemToSell.getId());
		if(amountToSell == 0)
			return;
		/*	if(amountToSell > 300) {
			String s = ItemDefinition.forId(itemToSell.getId()).getName().endsWith("s") ? ItemDefinition.forId(itemToSell.getId()).getName() : ItemDefinition.forId(itemToSell.getId()).getName() + "s";
			player.getPacketSender().sendMessage("You can only sell 300 "+s+" at a time."); 
			return;
		}*/
		int itemId = itemToSell.getId();
		boolean customShop = this.getCurrency().getId() == -1;
		boolean inventorySpace = customShop ? true : false;
		if(!customShop) {
			if(!itemToSell.getDefinition().isStackable()) {
				if(!player.getInventory().contains(this.getCurrency().getId()))
					inventorySpace = true;
			}
			if(player.getInventory().getFreeSlots() <= 0 && player.getInventory().getAmount(this.getCurrency().getId()) > 0)
				inventorySpace = true;
			if(player.getInventory().getFreeSlots() > 0 || player.getInventory().getAmount(this.getCurrency().getId()) > 0)
				inventorySpace = true;
		}
		int itemValue = 0;
		if(getCurrency().getId() > 0) {
			itemValue = ItemDefinition.forId(itemToSell.getId()).getValue();
		} else {
			Object[] obj = ShopManager.getCustomShopData(id, itemToSell.getId());
			if(obj == null)
				return;
			itemValue = (int) obj[0];
		}
		if(itemValue <= 0)
			return;
		itemValue = (int) (itemValue * 0.85);
		if(itemValue <= 0) {
			itemValue = 1;
		}
		for (int i = amountToSell; i > 0; i--) {
			itemToSell = new Item(itemId);
			if(this.full(itemToSell.getId()) || !player.getInventory().contains(itemToSell.getId()) || !player.isShopping())
				break;
			if(!itemToSell.getDefinition().isStackable()) {
				if(inventorySpace) {
					super.switchItem(player.getInventory(), this, itemToSell.getId(), -1);
					if(!customShop) {
						if (ReducedSellPrice.forId(itemToSell.getId()) != null) {
							player.getInventory().add(new Item(getCurrency().getId(), ReducedSellPrice.forId(itemToSell.getId()).getSellValue()), false);
						} else {
							player.getInventory().add(new Item(getCurrency().getId(), itemValue), false);
						}
					} else {
						//Return points here
					}
				} else {
					player.getPacketSender().sendMessage("Please free some inventory space before doing that.");
					break;
				}
			} else {
				if(inventorySpace) {
					super.switchItem(player.getInventory(), this, itemToSell.getId(), amountToSell);
					if (!customShop) {
						if (itemToSell.reducedPrice()) {
							player.getInventory().add(new Item(getCurrency().getId(), ReducedSellPrice.forId(itemToSell.getId()).getSellValue() * amountToSell), false);
						} else {
							player.getInventory().add(new Item(getCurrency().getId(), itemValue * amountToSell), false);
						}
					} else {
						// Return points here
					}
					break;
				} else {
					player.getPacketSender().sendMessage("Please free some inventory space before doing that.");
					break;
				}
			}
			amountToSell--;
		}
		if(customShop) {
			PlayerPanel.refreshPanel(player);
		}
		player.getInventory().refreshItems();
		fireRestockTask();
		refreshItems();
		publicRefresh();
	}

	/**
	 * Buying an item from a shop
	 */
	@Override
	public Shop switchItem(ItemContainer to, Item item, int slot, boolean sort, boolean refresh) {
		final Player player = getPlayer();
		if(player == null)
			return this;
		if(!player.isShopping() || player.isBanking()) {
			player.getPacketSender().sendInterfaceRemoval();
			return this;
		}
		if(this.id == GENERAL_STORE) {
			if(player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
				player.getPacketSender().sendMessage("Ironman-players are not allowed to buy items from the General store.");
				return this;
			}
		}
		if (!shopSellsItem(item))
			return this;
		if(getItems()[slot].getAmount() <= 1 && id != GENERAL_STORE) {
			player.getPacketSender().sendMessage("The shop has run out of stock for this item.");
			return this;
		}
		if (item.getAmount() > getItems()[slot].getAmount())
			item.setAmount(getItems()[slot].getAmount());
		int amountBuying = item.getAmount();
		if(amountBuying == getItems()[slot].getAmount() && id != GENERAL_STORE) {
				amountBuying = getItems()[slot].getAmount()-1;
				player.getPacketSender().sendMessage("You buy the maximum amount you can from the shop.");
		}
		if(amountBuying <= 0)
			return this;
		if(amountBuying > GameSettings.Shop_Buy_Limit) {
			player.getPacketSender().sendMessage("You can only buy "+GameSettings.Shop_Buy_Limit+" "+ItemDefinition.forId(item.getId()).getName()+"s at a time.");
			return this;
		}
		if (UltimateIronmanHandler.hasItemsStored(player) && player.getLocation() != Location.DUNGEONEERING) {
			player.getPacketSender().sendMessage("You must claim your stored items at Dungeoneering first.");
			return this;
		}
		if (UltimateIronmanHandler.hasItemsStored(player) && id == DUNGEONEERING_STORE) {
			player.getPacketSender().sendMessage("You must claim your stored items at Dungeoneering first.");
			return this;
		}
		boolean customShop = getCurrency().getId() == -1;
		boolean usePouch = false;
		int playerCurrencyAmount = 0;
		int value = ItemDefinition.forId(item.getId()).getValue();
		String currencyName = "";
		if(getCurrency().getId() != -1) {
			playerCurrencyAmount = player.getInventory().getAmount(currency.getId());
			currencyName = ItemDefinition.forId(currency.getId()).getName().toLowerCase();
			if(currency.getId() == 995) {
				if(player.getMoneyInPouch() >= value) {
					playerCurrencyAmount = player.getMoneyInPouchAsInt();
					if(!(player.getInventory().getFreeSlots() == 0 && player.getInventory().getAmount(currency.getId()) == value)) {
						usePouch = true;
					}
				}
			} else {
				/** CUSTOM CURRENCY, CUSTOM SHOP VALUES **/
				if(id == TOKKUL_EXCHANGE_STORE || id == STARDUST_EXCHANGE_STORE || id == ENERGY_FRAGMENT_STORE || id == AGILITY_TICKET_STORE || id == GRAVEYARD_STORE || id == BARROWS_STORE || id == MEMBERS_STORE_I || id == MEMBERS_STORE_II || id == MEMBERS_STORE_III) {
					value = (int) ShopManager.getCustomShopData(id, item.getId())[0];
				}
			}
		} else {
			Object[] obj = ShopManager.getCustomShopData(id, item.getId());
			if(obj == null)
				return this;
			value = (int) obj[0];
			currencyName = (String) obj[1];
			if(id == PKING_REWARDS_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getPkPoints();
			} else if(id == VOTING_REWARDS_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getVotingPoints();
			} else if(id == DUNGEONEERING_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getDungeoneeringTokens();
			} else if(id == PRESTIGE_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getPrestigePoints();
			} else if(id == SLAYER_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getSlayerPoints();
			} else if(id == BARROWS_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getBarrowsPoints();
			} else if(id == MEMBERS_STORE_I || id == MEMBERS_STORE_II || id == MEMBERS_STORE_III) {
				playerCurrencyAmount = player.getPointsHandler().getMemberPoints();
			}
		}
		if(value <= 0) {
			return this;
		}
		if(!hasInventorySpace(player, item, getCurrency().getId(), value)) {
			player.getPacketSender().sendMessage("You do not have any free inventory slots.");
			return this;
		}
		if (playerCurrencyAmount <= 0 || playerCurrencyAmount < value) {
			player.getPacketSender().sendMessage("You do not have enough " + ((currencyName.endsWith("s") ? (currencyName) : (currencyName + "s"))) + " to purchase this item.");
			return this;
		}
		if(id == SKILLCAPE_STORE_1 || id == SKILLCAPE_STORE_2 || id == SKILLCAPE_STORE_3) {
			for(int i = 0; i < item.getDefinition().getRequirement().length; i++) {
				int req = item.getDefinition().getRequirement()[i];
				if((i == 3 || i == 5) && req == 99)
					req *= 10;
				if(req > player.getSkillManager().getMaxLevel(i)) {
					player.getPacketSender().sendMessage("You need to have at least level 99 in "+Misc.formatText(Skill.forId(i).toString().toLowerCase())+" to buy this item.");
					return this;
				}
			}
		} else if (id == 83) {
			if (!player.getClickDelay().elapsed(3000)) {
				player.getPacketSender().sendMessage("Please wait 3 seconds before doing that again.");
				return this;
			}
			int b = 0;
			for (int i = 0; i < BossPet.values().length; i++) {
				if (player.getBossPet(i)) {
					b++;
				}
			}
			if (b < 1) {
				player.getPacketSender().sendMessage("You must have unlocked a Boss pet first.");
				return this;
			}
			player.getPacketSender().sendMessage("You can use the 'Pet return' item to get your pets back."); //Have inventory/bank space.");
			player.getPacketSender().sendMessage("You have "+b+" pets unlocked, have inventory/bank space available.");
			player.getClickDelay().reset();
		} else if (id == GAMBLING_STORE) {
			if (item.getId() == 15084 || item.getId() == 299) {
				player.getPacketSender().sendMessage("Gambling isn't ready yet.");
				return this;
			}
		} else if (id == 32 && (item.getId() == 5510 || item.getId() == 5512 || item.getId() == 5514 || item.getId() == 5509)) {
			if (!player.getRights().isMember()) {
				player.getPacketSender().sendMessage("You must be a Member to purchase from this store.");
				return this;
			}
			if (item.getId() == 5510 && player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < 25) {
				player.getPacketSender().sendMessage("You must have at least 25 Runecrafting to buy this pouch.");
				return this;
			}
			if (item.getId() == 5512 && player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < 50) {
				player.getPacketSender().sendMessage("You must have at least 50 Runecrafting to buy this pouch.");
				return this;
			}
			if (item.getId() == 5514 && player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < 75) {
				player.getPacketSender().sendMessage("You must have at least 75 Runecrafting to buy this pouch.");
				return this;
			}
		} else if (id == 33) { //id == 33
			if (item.getId() == 5509 || item.getId() == 5510 || item.getId() == 5512 || item.getId() == 5514) {
				for (int i = 0; i < 3; i++) {
					if (player.getInventory().contains(item.getId())) {
						player.getPacketSender().sendMessage("You already have that pouch!");
						return this;
					}
					for (Bank b : player.getBanks()) {
						if (b.contains(item.getId())) {
							player.getPacketSender().sendMessage("You have that pouch in your bank!");
							return this;
						}
					}
				}
			}
			if (item.getId() == 5510 && player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < 25) {
				player.getPacketSender().sendMessage("You must have at least 25 Runecrafting to buy this pouch.");
				return this;
			}
			if (item.getId() == 5512 && player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < 50) {
				player.getPacketSender().sendMessage("You must have at least 50 Runecrafting to buy this pouch.");
				return this;
			}
			if (item.getId() == 5514 && player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < 75) {
				player.getPacketSender().sendMessage("You must have at least 75 Runecrafting to buy this pouch.");
				return this;
			}
		} else if (id == 21) {
			if (item.getId() == 6797) {
				if (!player.getRights().isMember()){
					player.getPacketSender().sendMessage("That item can only be bought by Members.");
					return this;
				}
			}
		} else if(id == 90) {
			PlayerLogs.log("1 - Vote Shop", player.getUsername()+" purchased "+item.getDefinition().getName()+" from vote shop.");
			DiscordMessager.sendDebugMessage(":bangbang: "+player.getUsername()+" has purchased "+item.getDefinition().getName()+" from vote shop.");
		} else if(id == 81) {
			if(item.getId() == 9013 || item.getId() == 13150) {
				if (!player.didFriday13May2016()) {
					player.getPacketSender().sendMessage("You must have participated in a Friday the 13th event.");
					return this;
				}
			}
			switch (item.getId()) {
				//start hween 2016 list	
				case 22036:
				case 22037:
				case 22038:
				case 22039:
				case 22040:
				case 9922:
				case 9921:
					for (int i = 0; i < GameSettings.hweenIds2016.length; i++) {
						if (GameSettings.hweenIds2016[i] == item.getId() && !player.getHween2016(i)) {
							player.getPacketSender().sendMessage("You must have participated in the Halloween 2016 event.");
							return this;
						}
					}
					break;
				case 15420:
					if (player.getChristmas2016() < 7) {
						player.getPacketSender().sendMessage("You must have completed the Christmas 2016 event to unlock this.");
						return this;
					}
					break;
				case 2946:
					if (player.getNewYear2017() < 1) {
						player.getPacketSender().sendMessage("You must have completed the New Year 2017 mini-event to unlock this.");
					}
					break;
			}
		}
		

		for (int i = amountBuying; i > 0; i--) {
			if (!shopSellsItem(item)) {
				break;
			}
			if(getItems()[slot].getAmount() <= 1 && id != GENERAL_STORE) {
				player.getPacketSender().sendMessage("The shop has run out of stock for this item.");
				break;
			}
			if(!item.getDefinition().isStackable()) {
				if(playerCurrencyAmount >= value && hasInventorySpace(player, item, getCurrency().getId(), value)) {

					if(!customShop) {
						if(usePouch) {
							player.setMoneyInPouch((player.getMoneyInPouch() - value));
						} else {
							player.getInventory().delete(currency.getId(), value, false);
						}
					} else {
						if(id == PKING_REWARDS_STORE) {
							player.getPointsHandler().setPkPoints(-value, true);
						} else if(id == VOTING_REWARDS_STORE) {
							player.getPointsHandler().setVotingPoints(-value, true);
						} else if(id == DUNGEONEERING_STORE) {
							player.getPointsHandler().setDungeoneeringTokens(-value, true);
						} else if(id == PRESTIGE_STORE) {
							player.getPointsHandler().setPrestigePoints(-value, true);
						} else if(id == SLAYER_STORE) {
							player.getPointsHandler().setSlayerPoints(-value, true);
						} else if(id == BARROWS_STORE) {
							player.getPointsHandler().setBarrowsPoints(-value, true);
						} else if(id == MEMBERS_STORE_I || id == MEMBERS_STORE_II || id == MEMBERS_STORE_III) {
							player.getPointsHandler().setMemberPoints(-value, true);
						}
					}

					super.switchItem(to, new Item(item.getId(), 1), slot, false, false);

					playerCurrencyAmount -= value;
				} else {
					break;
				}
			} else {
				if(playerCurrencyAmount >= value && hasInventorySpace(player, item, getCurrency().getId(), value)) {

					int canBeBought = playerCurrencyAmount / (value);
					if(canBeBought >= amountBuying) {
						canBeBought = amountBuying;
					}
					if(canBeBought == 0)
						break;

					if(!customShop) {
						if(usePouch) {
							player.setMoneyInPouch((player.getMoneyInPouch() - (value * canBeBought)));
						} else {
							player.getInventory().delete(currency.getId(), value * canBeBought, false);
						}
					} else {
						if(id == PKING_REWARDS_STORE) {
							player.getPointsHandler().setPkPoints(-value * canBeBought, true);
						} else if(id == VOTING_REWARDS_STORE) {
							player.getPointsHandler().setVotingPoints(-value * canBeBought, true);
						} else if(id == DUNGEONEERING_STORE) {
							player.getPointsHandler().setDungeoneeringTokens(-value * canBeBought, true);
						} else if(id == PRESTIGE_STORE) {
							player.getPointsHandler().setPrestigePoints(-value * canBeBought, true);
						} else if(id == SLAYER_STORE) {
							player.getPointsHandler().setSlayerPoints(-value * canBeBought, true);
						} else if(id == BARROWS_STORE) {
							player.getPointsHandler().setBarrowsPoints(-value * canBeBought, true);
						} else if(id == MEMBERS_STORE_I || id == MEMBERS_STORE_II || id == MEMBERS_STORE_III) {
							player.getPointsHandler().setMemberPoints(-value * canBeBought, true);
						}
					}
					super.switchItem(to, new Item(item.getId(), canBeBought), slot, false, false);
					playerCurrencyAmount -= value;
					break;
				} else {
					break;
				}
			}
			amountBuying--;
		}
		if(!customShop) {
			if(usePouch) {
				player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch()); //Update the money pouch
			}
		} else {
			PlayerPanel.refreshPanel(player);
		}
		player.getInventory().refreshItems();
		fireRestockTask();
		refreshItems();
		publicRefresh();
		return this;
	}

	/**
	 * Checks if a player has enough inventory space to buy an item
	 * @param item	The item which the player is buying
	 * @return		true or false if the player has enough space to buy the item
	 */
	public static boolean hasInventorySpace(Player player, Item item, int currency, int pricePerItem) {
		if(player.getInventory().getFreeSlots() >= 1) {
			return true;
		}
		if(item.getDefinition().isStackable()) {
			if(player.getInventory().contains(item.getId())) {
				return true;
			}
		}
		if(currency != -1) {
			if(player.getInventory().getFreeSlots() == 0 && player.getInventory().getAmount(currency) == pricePerItem) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Shop add(Item item, boolean refresh) {
		super.add(item, false);
		if(id != RECIPE_FOR_DISASTER_STORE)
			publicRefresh();
		return this;
	}

	@Override
	public int capacity() {
		return 42;
	}

	@Override
	public StackType stackType() {
		return StackType.STACKS;
	}

	@Override
	public Shop refreshItems() {
		if(id == RECIPE_FOR_DISASTER_STORE) {
			RecipeForDisaster.openRFDShop(getPlayer());
			return this;
		}
		for (Player player : World.getPlayers()) {
			if (player == null || !player.isShopping() || player.getShop() == null || player.getShop().id != id)
				continue;
			player.getPacketSender().sendItemContainer(player.getInventory(), INVENTORY_INTERFACE_ID);
			player.getPacketSender().sendItemContainer(ShopManager.getShops().get(id), ITEM_CHILD_ID);
			player.getPacketSender().sendString(NAME_INTERFACE_CHILD_ID, name);
			if(player.getInputHandling() == null || !(player.getInputHandling() instanceof EnterAmountToSellToShop || player.getInputHandling() instanceof EnterAmountToBuyFromShop))
				player.getPacketSender().sendInterfaceSet(INTERFACE_ID, INVENTORY_INTERFACE_ID - 1);
		}
		return this;
	}

	@Override
	public Shop full() {
		getPlayer().getPacketSender().sendMessage("The shop is currently full. Please come back later.");
		return this;
	}

	public String shopPriceEx(int shopPrice) {
		String ShopAdd = "";
		if (shopPrice >= 1000 && shopPrice < 1000000) {
			ShopAdd = " (" + (shopPrice / 1000) + "K)";
		} else if (shopPrice >= 1000000) {
			ShopAdd = " (" + (shopPrice / 1000000) + "M)";
		}
		return ShopAdd;
	}

	private boolean shopSellsItem(Item item) {
		return contains(item.getId());
	}

	public void fireRestockTask() {
		if(isRestockingItems() || fullyRestocked())
			return;
		setRestockingItems(true);
		TaskManager.submit(new ShopRestockTask(this));
	}

	public boolean fullyRestocked() {
		if(id == GENERAL_STORE) {
			return getValidItems().size() == 0;
		} else if(id == RECIPE_FOR_DISASTER_STORE) {
			return true;
		}
		if(getOriginalStock() != null) {
			for(int shopItemIndex = 0; shopItemIndex < getOriginalStock().length; shopItemIndex++) {
				if(getItems()[shopItemIndex].getAmount() != getOriginalStock()[shopItemIndex].getAmount())
					return false;
			}
		}
		return true;
	}

	public static boolean shopBuysItem(int shopId, Item item) {
		if(shopId == GENERAL_STORE)
			return true;
		if(shopId == DUNGEONEERING_STORE || shopId == PKING_REWARDS_STORE || shopId == STARDUST_EXCHANGE_STORE || shopId == VOTING_REWARDS_STORE || shopId == RECIPE_FOR_DISASTER_STORE || shopId == ENERGY_FRAGMENT_STORE || shopId == AGILITY_TICKET_STORE || shopId == GRAVEYARD_STORE || shopId == TOKKUL_EXCHANGE_STORE || shopId == PRESTIGE_STORE || shopId == SLAYER_STORE || shopId == BARROWS_STORE || shopId == MEMBERS_STORE_I || shopId == MEMBERS_STORE_II || shopId == MEMBERS_STORE_III)
			return false;
		Shop shop = ShopManager.getShops().get(shopId);
		if(shop != null && shop.getOriginalStock() != null) {
			for(Item it : shop.getOriginalStock()) {
				if(it != null && it.getId() == item.getId())
					return true;
			}
		}
		return false;
	}

	public static class ShopManager {

		private static Map<Integer, Shop> shops = new HashMap<Integer, Shop>();

		public static Map<Integer, Shop> getShops() {
			return shops;
		}

		public static JsonLoader parseShops() {
			return new JsonLoader() {
				@Override
				public void load(JsonObject reader, Gson builder) {
					int id = reader.get("id").getAsInt();
					String name =  reader.get("name").getAsString();
					Item[] items = builder.fromJson(reader.get("items").getAsJsonArray(), Item[].class);
					Item currency = new Item(reader.get("currency").getAsInt());
					shops.put(id, new Shop(null, id, name, currency, items));
				}

				@Override
				public String filePath() {
					return "./data/def/json/world_shops.json";
				}
			};
		}

		public static Object[] getCustomShopData(int shop, int item) {
			if(shop == VOTING_REWARDS_STORE) {
				switch(item) {
					case 22053:
					case 15682:
					return new Object[]{3, "Voting points"};
					case 15332:
						return new Object[]{2, "Voting points"};
					case 6570:
					case 4151:
						return new Object[]{20, "Voting points"};
					case 15018:
					case 15019:
					case 15020:
					case 15220:
					case 18353:
					case 18349:
					case 18351:
					case 18355:
					case 18357:
					case 13754:
					case 13734:
						return new Object[]{100, "Voting points"};
					case 17273:
						return new Object[]{140, "Voting points"};
					case 13996:
					case 11724:
					case 11726:
					case 11718:
					case 11720:
					case 11722:
					case 16753:
					case 17235:
					case 16863:
						return new Object[]{160, "Voting points"};
					case 19111:
					case 11702:
					case 14484:
					case 12926:
					case 22007:
					case 20000:
					case 20001:
					case 20002:
						return new Object[]{200, "Voting points"};
					case 13752:
					case 13746:
						return new Object[]{400, "Voting points"};
					case 13750:
						return new Object[]{1000, "Voting points"};
					case 13748:
						return new Object[]{1500, "Voting points"};
				}
			} else if(shop == PKING_REWARDS_STORE) {
				switch(item) {
				case 6918:
				case 6914:
				case 6889:
				case 2579:
					return new Object[]{25, "Pk points"};
				case 6916:
				case 6924:
					return new Object[]{30, "Pk points"};
				case 6920:
				case 6922:
					return new Object[]{20, "Pk points"};
				case 2581:
				case 11730:
					return new Object[]{100, "Pk points"};
				case 2577:
					return new Object[]{100, "Pk points"};
				case 15486:
				case 19111:
					return new Object[]{250, "Pk points"};
				case 13879:
				case 13883:
				case 15243:
				case 15332:
					return new Object[]{4, "Pk points"};
				case 15241:
				case 17273:
					return new Object[]{200, "Pk points"};
				case 10548:
				case 10547:
				case 10551:
					return new Object[]{150, "Pk points"};
				case 6570:
				case 11235:
				case 4151:
				case 13262:
					return new Object[]{80, "Pk points"};
				case 11696:
				case 11698:
				case 11700:
					return new Object[]{500, "Pk points"};
				case 14484:
				case 19780:
					return new Object[]{750, "Pk points"};
				case 11728:
				case 15018:
				case 15019:
				case 15020:
				case 15220:
					return new Object[]{50, "Pk points"};
				case 11694:
					return new Object[]{600, "Pk points"};
				}
			} else if(shop == ENERGY_FRAGMENT_STORE) {
				switch(item) {
				case 5509:
					return new Object[]{25, "energy fragments"};
				case 5510:
					return new Object[]{100, "energy fragments"};
				case 5512:
					return new Object[]{250, "energy fragments"};
				case 5514:
					return new Object[]{500, "energy fragments"};
				case 13613: //hats
				case 13616:
				case 13626:
					return new Object[]{1500, "energy fragments"};
				case 13619: //bodies
				case 13614:
				case 13624:
					return new Object[]{1000, "energy fragments"};
				case 13622: //legs
				case 13617:
				case 13627:
					return new Object[]{1000, "energy fragments"};
				case 13623: //gloves
				case 13618:
				case 13628:
					return new Object[]{500, "energy fragments"};
				}
			} else if(shop == AGILITY_TICKET_STORE) {
				switch(item) {
				case 14936:
				case 14938:
					return new Object[]{200, "agility tickets"};
				case 10941:
				case 10939:
				case 10940:
				case 10933:
					return new Object[]{100, "agility tickets"};
				case 13661:
					return new Object[]{1000, "agility tickets"};
				}
			} else if(shop == STARDUST_EXCHANGE_STORE) {
				switch(item) {
				case 6180:
				case 6181:
				case 6182:
				case 9945:
				case 9472:
				case 10394:
				case 13674:
				case 13673:
				case 19735:
				case 18776:
					return new Object[]{2500, "stardust"};
				case 7409:
				case 9944:
					return new Object[]{3500, "stardust"};
				case 6666:
				case 13661:
				case 2997:
				case 2651:
				case 13672:
					return new Object[]{5000, "stardust"};
				case 453:
					return new Object[]{5, "stardust"};
				case 9185:
					return new Object[]{250, "stardust"};
				case 5609:
				case 5608:
					return new Object[]{4000, "stardust"};
				case 9005:
					return new Object[]{750, "stardust"};
				case 5607:
					return new Object[]{7500, "stardust"};
				case 13675:
					return new Object[]{1000, "stardust"};
				}
			} else if(shop == GRAVEYARD_STORE) {
				switch(item) {
				case 18337:
					return new Object[]{350, "zombie fragments"};
				case 10551:
					return new Object[]{500, "zombie fragments"};
				case 10548:
				case 10549:
				case 10550:
					return new Object[]{200, "zombie fragments"};
				case 7592:
				case 7593:
				case 7594:
				case 7595:
				case 7596:
					return new Object[]{25, "zombie fragments"};
				case 15241:
					return new Object[]{500, "zombie fragments"};
				case 15243:
					return new Object[]{2, "zombie fragments"};
				}
			} else if(shop == TOKKUL_EXCHANGE_STORE) {
				switch(item) {
				case 11978:
					return new Object[]{300000, "tokkul"};
				case 438:
				case 436:
					return new Object[]{10, "tokkul"};
				case 440:
					return new Object[]{25, "tokkul"};
				case 453:
					return new Object[]{30, "tokkul"};
				case 442:
					return new Object[]{30, "tokkul"};
				case 444:
					return new Object[]{40, "tokkul"};
				case 447:
					return new Object[]{70, "tokkul"};
				case 449:
					return new Object[]{120, "tokkul"};
				case 451:
					return new Object[]{250, "tokkul"};
				case 1623:
					return new Object[]{20, "tokkul"};
				case 1621:
					return new Object[]{40, "tokkul"};
				case 1619:
					return new Object[]{70, "tokkul"};
				case 1617:
					return new Object[]{150, "tokkul"};
				case 1631:
					return new Object[]{1600, "tokkul"};
				case 6571:
					return new Object[]{50000, "tokkul"};
				case 11128:
					return new Object[]{22000, "tokkul"};
				case 6522:
					return new Object[]{20, "tokkul"};
				case 6524:
				case 6523:
				case 6526:
					return new Object[]{5000, "tokkul"};
				case 6528:
				case 6568:
					return new Object[]{800, "tokkul"};
				}
			} else if(shop == DUNGEONEERING_STORE) {
				switch(item) {
				case 18351:
				case 18349:
				case 18353:
				case 18357:
				case 18355:
				case 18359:
				case 18361:
				case 18363:
				case 6500:
				case 18337:
					return new Object[]{200000, "Dungeoneering tokens"};
				case 18344:
					return new Object[]{153000, "Dungeoneering tokens"};
				case 18839:
					return new Object[]{140000, "Dungeoneering tokens"};
				case 18335:
					return new Object[]{75000, "Dungeoneering tokens"};
				}
			} else if(shop == PRESTIGE_STORE) {
				switch(item) {
				case 19333:
					return new Object[]{20, "Prestige points"};
				case 15220:
				case 15020:
				case 15019:
				case 15018:
					return new Object[]{20, "Prestige points"};
				case 20000:
				case 20001:
				case 20002:
					return new Object[]{50, "Prestige points"};
				case 4084:
					return new Object[]{170, "Prestige points"};
				case 13857:
				case 13855:
				case 13848:
				case 13856:
				case 13854:
				case 13853:
				case 13852:
				case 13851:
				case 13850:
				case 13849:
					return new Object[]{5, "Prestige points"};
				case 10400:
				case 10402:
				case 10416:
				case 10418:
				case 10408:
				case 10410:
				case 10412:
				case 10414:
				case 10404:
				case 10406:
					return new Object[]{2, "Prestige points"};
				case 14595:
				case 14603:
					return new Object[]{5, "Prestige points"};
				case 14602:
				case 14605:
					return new Object[]{3, "Prestige points"};
				}
			} else if(shop == SLAYER_STORE) {
				switch(item) {
				case 13263:
					return new Object[]{250, "Slayer points"};
				case 13281:
					return new Object[]{5, "Slayer points"};
				case 15403:
				case 11730:
				case 10887:
				case 15241:
					return new Object[]{300, "Slayer points"};
				case 11235:
				case 4151:
				case 15486:
					return new Object[]{250, "Slayer points"};
				case 15243:
					return new Object[]{3, "Slayer points"};
				case 10551:
					return new Object[]{200, "Slayer points"};
				case 20000:
				case 20001:
				case 20002:
					return new Object[]{450, "Slayer points"};
				}
			} else if(shop == BARROWS_STORE) {
				switch(item) {
				case 4716:
				case 4718:
				case 4720:
				case 4722:
					return new Object[]{110, "Barrows points"};
				case 4753:
				case 4732:
				case 4724:
				case 4708:
					return new Object[]{80, "Barrows points"};
				case 4755:
				case 4747:
				case 4726:
				case 4734:
				case 4710:
				case 4745:
					return new Object[]{90, "Barrows points"};
				case 4757:
				case 4749:
				case 4728:
				case 4736:
				case 4712:
				case 4759:
				case 4751:
				case 4730:
				case 4738:
				case 4714:
					return new Object[]{100, "Barrows points"};
				}
			} else if(shop == MEMBERS_STORE_I) {
				switch(item) {
				case 20171:
					return new Object[]{20, "Member Points"};
				case 19143:
				case 19146:
				case 19149:
					return new Object[]{10, "Member Points"};
				case 14023:
				case 14024: 
				case 13655:
					return new Object[]{15, "Member Points"};
				case 18359:
				case 18363:
				case 18361:
					return new Object[]{10, "Member Points"};
				case 18335:
				case 18337:
					return new Object[]{5, "Member Points"};
				case 11696:
				case 11700:
				case 11698:
					return new Object[]{7, "Member Points"};
				case 11694:
					return new Object[]{10, "Member Points"};
				case 14008:
				case 14009:
				case 14010:
					return new Object[]{15, "Member Points"};
				case 14011:
				case 14012:
				case 14013:
				case 14014:
				case 14015:
				case 14016:
					return new Object[]{10, "Member Points"};
				case 14094:
				case 14095:
				case 14096:
					return new Object[]{40, "Member Points"};
				case 11718:
				case 11720:
				case 11722:
					return new Object[]{8, "Member Points"};
				case 6585:
					return new Object[]{5, "Member Points"};
				case 13996:
				case 11724:
				case 11726:
				case 16753:
				case 17235:
				case 16863:
					return new Object[]{8, "Member Points"};
				case 11728:
					return new Object[]{5, "Member Points"};
				case 15486:
					return new Object[]{10, "Member Points"};
				case 19111:
					return new Object[]{10, "Member Points"};
				case 11730:
				case 14484:
					return new Object[]{10, "Member Points"};
				case 19780:
					return new Object[]{20, "Member Points"};
				}
			}  else if(shop == MEMBERS_STORE_II) {
				switch(item) {
				case 12926:
				case 22008:
				case 12931:
					return new Object[]{10, "Member Points"};
				case 18744:
				case 18745:
				case 18746:
					return new Object[]{15, "Member Points"};
				case 1048:
				case 1046:
				case 1042:
				case 1038:
				case 1044:
				case 1040:
					return new Object[]{30, "Member Points"};
				case 962:
					return new Object[]{100, "Member Points"};
				case 1055:
				case 1053:
				case 1057:
				case 19293:
					return new Object[]{25, "Member Points"};
				case 1050:
				case 10284:
					return new Object[]{60, "Member Points"};
				case 13744:
				case 13738:
					return new Object[]{20, "Member Points"};
				case 2572:
					return new Object[]{25, "Member Points"};
				case 12601:
				case 12603:
				case 12605:
					return new Object[]{10, "Member Points"};
				case 15018:
				case 15019:
				case 15020:
				case 15220:
					return new Object[]{5, "Member Points"};
				case 20080:
				case 15441:
				case 15442:
				case 15443:
				case 15444:
					return new Object[]{5, "Member Points"};
				case 20000:
				case 20001:
				case 20002:
					return new Object[]{10, "Member Points"};
				case 11995:
				case 11996:
				case 11997:
				case 12001:
				case 12002:
				case 11991:
				case 11992:
				case 11987:
				case 11989:
				case 12004:
					return new Object[]{10, "Member Points"};
				case 13740:
					return new Object[]{75, "Member Points"};
				case 11858:
				case 11860:
				case 11862:
				case 19580:
					return new Object[]{40, "Member Points"};
				case 13742:
					return new Object[]{50, "Member Points"};
				}
			}  else if(shop == MEMBERS_STORE_III) {
				switch(item) {
				case 7630: //Zulrah crate
					return new Object[]{2, "Member Points"};
				case 10942: //$10 scroll
				case 10944: //mem scroll
					return new Object[]{10, "Member Points"};
				case 6769: //$5 scroll
				case 18351: //long
				case 18349: //rapier 
				case 18353: //maul
				case 18355: //chaotic staff
				case 18357: //ccbow
				case 6500: //charming imp
					return new Object[]{5, "Member Points"};
				case 10934: //$25 scroll
					return new Object[]{25, "Member Points"};
				case 10935: //$50 scroll
					return new Object[]{50, "Member Points"};
				case 10943: //$100 scroll
					return new Object[]{100, "Member Points"};
				case 7587: //coffin
				case 18719: //potion of flight
					return new Object[]{3, "Member Points"};
				case 17273:
					return new Object[]{7, "Member Points"};
				}
			}
			return null;
		}
	}

	/**
	 * The shop interface id.
	 */
	public static final int INTERFACE_ID = 3824;

	/**
	 * The starting interface child id of items.
	 */
	public static final int ITEM_CHILD_ID = 3900;

	/**
	 * The interface child id of the shop's name.
	 */
	public static final int NAME_INTERFACE_CHILD_ID = 3901;

	/**
	 * The inventory interface id, used to set the items right click values
	 * to 'sell'.
	 */
	public static final int INVENTORY_INTERFACE_ID = 3823;

	/*
	 * Declared shops
	 */
	public static final int GENERAL_STORE = 12;
	public static final int RECIPE_FOR_DISASTER_STORE = 36;

	private static final int VOTING_REWARDS_STORE = 90;
	private static final int PKING_REWARDS_STORE = 26;
	private static final int ENERGY_FRAGMENT_STORE = 33;
	private static final int AGILITY_TICKET_STORE = 39;
	private static final int GRAVEYARD_STORE = 42;
	private static final int TOKKUL_EXCHANGE_STORE = 43;
	private static final int STARDUST_EXCHANGE_STORE = 78;
	private static final int SKILLCAPE_STORE_1 = 8;
	private static final int SKILLCAPE_STORE_2 = 9;
	private static final int SKILLCAPE_STORE_3 = 10;
	private static final int GAMBLING_STORE = 41;
	private static final int DUNGEONEERING_STORE = 44;
	private static final int PRESTIGE_STORE = 46;
	private static final int SLAYER_STORE = 47;
	private static final int BARROWS_STORE = 79;
	private static final int MEMBERS_STORE_I = 24;
	private static final int MEMBERS_STORE_II = 25;
	private static final int MEMBERS_STORE_III = 80;
}
