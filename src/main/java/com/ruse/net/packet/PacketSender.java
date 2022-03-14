package com.ruse.net.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ruse.GameSettings;
import com.ruse.model.Animation;
import com.ruse.model.DamageDealer;
import com.ruse.model.GameObject;
import com.ruse.model.Graphic;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.MessageType;
import com.ruse.model.PlayerInteractingOption;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.impl.Shop;
import com.ruse.net.packet.Packet.PacketType;
import com.ruse.world.content.CustomObjects;
import com.ruse.world.content.skill.impl.construction.ConstructionData.Furniture;
import com.ruse.world.content.skill.impl.construction.Palette;
import com.ruse.world.content.skill.impl.construction.Palette.PaletteTile;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * This class manages making the packets that will be sent (when called upon) onto
 * the associated player's client.
 * 
 * @author relex lawl & Gabbe
 */

public class PacketSender {

	/**
	 * Sends information about the player to the client.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendDetails() {
		PacketBuilder out = new PacketBuilder(249);
		out.put(1, ValueType.A);
		out.putShort(player.getIndex());
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends the map region a player is located in and also
	 * sets the player's first step position of said region as their
	 * {@code lastKnownRegion}.
	 * @return	The PacketSender instance.
	 */
	public PacketSender sendMapRegion() {
		player.setRegionChange(true).setAllowRegionChangePacket(true);
		player.setLastKnownRegion(player.getPosition().copy());
		PacketBuilder out = new PacketBuilder(73);
		out.putShort(player.getPosition().getRegionX() + 6, ValueType.A);
		out.putShort(player.getPosition().getRegionY() + 6);
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends the logout packet for the player.
	 * @return	The PacketSender instance.
	 */
	public PacketSender sendLogout() {
		PacketBuilder out = new PacketBuilder(109);
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Requests a reload of the region
	 */
	public PacketSender sendRegionReload() {
		PacketBuilder out = new PacketBuilder(89);
		player.getSession().queueMessage(out);
		return this;
	}
	/**
	 * Sets the world's system update time, once timer is 0, everyone will be disconnected.
	 * @param time	The amount of seconds in which world will be updated in.
	 * @return		The PacketSender instance.
	 */
	public PacketSender sendSystemUpdate(int time) {
		PacketBuilder out = new PacketBuilder(114);
		out.putShort(time, ByteOrder.LITTLE);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendSound(int soundId, int volume, int delay) {
		PacketBuilder out = new PacketBuilder(175);
		out.putShort(soundId, ValueType.A, ByteOrder.LITTLE).put(volume).putShort(delay);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendSong(int id) {
		PacketBuilder out = new PacketBuilder(74);
		out.putShort(id, ByteOrder.LITTLE);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendAutocastId(int id) {
		PacketBuilder out = new PacketBuilder(38);
		out.putShort(id);
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends a game message to a player in the server.
	 * @param message	The message they will receive in chat box.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendMessage(String message) {
		PacketBuilder out = new PacketBuilder(253, PacketType.BYTE);
		out.putString(message);
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends a game message to a player in the server.
	 * @param messageType The type of message the player will receive
	 * @param message	The message they will receive in chat box.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendMessage(MessageType type, String message) {
		PacketBuilder out = new PacketBuilder(253, PacketType.BYTE);
		out.putString(type.getPrefix() + type.getColor() + message);
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends skill information onto the client, to calculate things such as
	 * constitution, prayer and summoning orb and other configurations.
	 * @param skill		The skill being sent.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendSkill(Skill skill) {		
		PacketBuilder out = new PacketBuilder(134);
		out.put(skill.ordinal());
		out.putInt(player.getSkillManager().getExperience(skill), ByteOrder.MIDDLE);
		out.putShort(player.getSkillManager().getCurrentLevel(skill));
		out.putShort(player.getSkillManager().getMaxLevel(skill));
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends a configuration button's state.
	 * @param configId	The id of the configuration button.
	 * @param state		The state to set it to.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendConfig(int id, int state) {
		PacketBuilder out = new PacketBuilder(36);
		out.putShort(id, ByteOrder.LITTLE);
		out.put(state);
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends a interface child's toggle. 
	 * @param id		The id of the child.
	 * @param state		The state to set it to.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendToggle(int id, int state) {
		PacketBuilder out = new PacketBuilder(87);
		out.putShort(id, ByteOrder.LITTLE);
		out.putInt(state, ByteOrder.MIDDLE);
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends the state in which the player has their chat options, such as public, private, friends only.
	 * @param publicChat	The state of their public chat.
	 * @param privateChat	The state of their private chat.
	 * @param tradeChat		The state of their trade chat.
	 * @return				The PacketSender instance.
	 */
	public PacketSender sendChatOptions(int publicChat, int privateChat, int tradeChat) {
		PacketBuilder out = new PacketBuilder(206);
		out.put(publicChat).put(privateChat).put(tradeChat);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendRunEnergy(int energy) {
		PacketBuilder out = new PacketBuilder(110);
		out.put(energy);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender updateSpecialAttackOrb() {
		/* spec orb is disabled.. */
		/*
		PacketBuilder out = new PacketBuilder(111);
		out.put(player.getSpecialPercentage());
		player.getSession().queueMessage(out);
		out = new PacketBuilder(108);
		out.put(player.isSpecialActivated() ? 1 : 0);
		player.getSession().queueMessage(out);*/
		return this;
	}

	public PacketSender sendDungeoneeringTabIcon(boolean show) {
		PacketBuilder out = new PacketBuilder(103);
		out.put(show ? 1 : 0);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendHeight() {
		player.getSession().queueMessage(new PacketBuilder(86).put(player.getPosition().getZ()));
		return this;
	}

	public PacketSender sendIronmanMode(int ironmanMode) {
		PacketBuilder out = new PacketBuilder(112);
		out.put(ironmanMode);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendClanChatListOptionsVisible(int config) {
		PacketBuilder out = new PacketBuilder(115);
		out.put(config); //0 = no right click options, 1 = Kick only, 2 = demote/promote & kick
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendRunStatus() {
		PacketBuilder out = new PacketBuilder(113);
		out.put(player.isRunning() ? 1 : 0);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendWeight(int weight) {
		PacketBuilder out = new PacketBuilder(240);
		out.putShort(weight);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender commandFrame(int i) {
		PacketBuilder out = new PacketBuilder(28);
		out.put(i);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendInterface(int id) {
		PacketBuilder out = new PacketBuilder(97);
		out.putShort(id);
		player.getSession().queueMessage(out);
		player.setInterfaceId(id);
		return this;
	}

	public PacketSender sendWalkableInterface(int interfaceId) {
		player.setWalkableInterfaceId(interfaceId);
		PacketBuilder out = new PacketBuilder(208);
		out.putShort(interfaceId);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendInterfaceDisplayState(int interfaceId, boolean hide) {
		PacketBuilder out = new PacketBuilder(171);
		out.put(hide ? 1 : 0);
		out.putShort(interfaceId);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendPlayerHeadOnInterface(int id) {
		PacketBuilder out = new PacketBuilder(185);
		out.putShort(id, ValueType.A, ByteOrder.LITTLE);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendNpcHeadOnInterface(int id, int interfaceId) {
		PacketBuilder out = new PacketBuilder(75);
		out.putShort(id, ValueType.A, ByteOrder.LITTLE);
		out.putShort(interfaceId, ValueType.A, ByteOrder.LITTLE);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendEnterAmountPrompt(String title) {
		PacketBuilder out = new PacketBuilder(27, PacketType.BYTE);
		out.putString(title);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendEnterInputPrompt(String title) {
		PacketBuilder out = new PacketBuilder(187, PacketType.BYTE);
		out.putString(title);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendInterfaceReset() {
		PacketBuilder out = new PacketBuilder(68);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendInterfaceComponentMoval(int x, int y, int id) {
		PacketBuilder out = new PacketBuilder(70);
		out.putShort(x);
		out.putShort(y);
		out.putShort(id, ByteOrder.LITTLE);
		player.getSession().queueMessage(out);
		return this;
	}

	/*public PacketSender sendBlinkingHint(String title, String information, int x, int y, int speed, int pause, int type, final int time) {
		player.getSession().queueMessage(new PacketBuilder(179, PacketType.SHORT).putString(title).putString(information).putShort(x).putShort(y).put(speed).put(pause).put(type));
		if(type > 0) {
			TaskManager.submit(new Task(time, player, false) {
				@Override
				public void execute() {
					player.getPacketSender().sendBlinkingHint("", "", 0, 0, 0, 0, -1, 0);
					stop();
				}
			});
		}
		return this;
	}
	 */
	public PacketSender sendInterfaceAnimation(int interfaceId,  Animation animation) {
		PacketBuilder out = new PacketBuilder(200);
		out.putShort(interfaceId);
		out.putShort(animation.getId());
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendInterfaceModel(int interfaceId, int itemId, int zoom) {
		PacketBuilder out = new PacketBuilder(246);
		out.putShort(interfaceId, ByteOrder.LITTLE);
		out.putShort(zoom).putShort(itemId);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendTabInterface(int tabId, int interfaceId) {
		PacketBuilder out = new PacketBuilder(71);
		out.putShort(interfaceId);
		out.put(tabId, ValueType.A);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendTabs() {
		sendTabInterface(GameSettings.ATTACK_TAB, 2423);
		sendTabInterface(GameSettings.SKILLS_TAB, 3917);//31110);
		sendTabInterface(GameSettings.QUESTS_TAB, 639);
		sendTabInterface(GameSettings.ACHIEVEMENT_TAB, 37000);
		sendTabInterface(GameSettings.INVENTORY_TAB, 3213);
		sendTabInterface(GameSettings.EQUIPMENT_TAB, 15000);
		sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId());
		sendTabInterface(GameSettings.PRAYER_TAB, player.getPrayerbook().getInterfaceId());
		//Row 2
		sendTabInterface(GameSettings.FRIEND_TAB, 5065);
		sendTabInterface(GameSettings.IGNORE_TAB, 5715);
		sendTabInterface(GameSettings.CLAN_CHAT_TAB, 29328);
		sendTabInterface(GameSettings.LOGOUT, 2449);
		sendTabInterface(GameSettings.OPTIONS_TAB, 904);
		sendTabInterface(GameSettings.EMOTES_TAB, 147);
		sendTabInterface(GameSettings.SUMMONING_TAB, 54017);
		return this;
	}

	public PacketSender sendTab(int id) {
		PacketBuilder out = new PacketBuilder(106);
		out.put(id, ValueType.C);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendFlashingSidebar(int id) {
		PacketBuilder out = new PacketBuilder(24);
		out.put(id, ValueType.S);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendChatboxInterface(int id) {
		if(player.getInterfaceId() <= 0)
			player.setInterfaceId(55);
		PacketBuilder out = new PacketBuilder(164);
		out.putShort(id, ByteOrder.LITTLE);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendMapState(int state) {
		PacketBuilder out = new PacketBuilder(99);
		out.put(state);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendCameraAngle(int x, int y, int level, int speed, int angle) {
		PacketBuilder out = new PacketBuilder(177);
		out.put(x / 64);
		out.put(y / 64);
		out.putShort(level);
		out.put(speed);
		out.put(angle);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendCameraShake(int verticalAmount, int verticalSpeed, int horizontalAmount, int horizontalSpeed) {
		PacketBuilder out = new PacketBuilder(35);
		out.put(verticalAmount);
		out.put(verticalSpeed);
		out.put(horizontalAmount);
		out.put(horizontalSpeed);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendCameraSpin(int x, int y, int z, int speed, int angle) {
		PacketBuilder out = new PacketBuilder(166);
		out.put(x / 64);
		out.put(y / 64);
		out.putShort(z);
		out.put(speed);
		out.put(angle);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendGrandExchangeUpdate(String s) {
		PacketBuilder out = new PacketBuilder(244, PacketType.BYTE);
		out.putString(s);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendCameraNeutrality() {
		PacketBuilder out = new PacketBuilder(107);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendInterfaceRemoval() {
		if(player.isBanking()) {
			sendClientRightClickRemoval();
			player.setBanking(false);
		}
		if(player.isShopping()) {
			sendClientRightClickRemoval().sendItemsOnInterface(Shop.INTERFACE_ID, new Item[]{new Item(-1)});
			player.setShopping(false);
		}
		if(player.getPriceChecker().isOpen()) {
			player.getPriceChecker().exit();
		}
		if(player.getTrading().inTrade()) {
			sendClientRightClickRemoval();
			player.getTrading().declineTrade(true);
		}
		if(player.getDueling().inDuelScreen && player.getDueling().duelingStatus != 5) {
			sendClientRightClickRemoval();
			player.getDueling().declineDuel(player.getDueling().duelingWith >= 0 ? true : false);
		}
		if(player.isResting()) {
			player.setResting(false);
			player.performAnimation(new Animation(11788));
		}
		/*	
		if(player.getMinigameAttributes().getFishingTrawlerAttributes().isViewingInterface()) {
			sendClientRightClickRemoval().sendItemsOnInterface(Shop.INTERFACE_ID, new Item[]{new Item(-1)});
			player.getMinigameAttributes().getFishingTrawlerAttributes().setViewingInterface(false).getRewards().clear();
		}
		if(player.getAdvancedSkills().getSummoning().isStoring()) {
			sendClientRightClickRemoval();
			player.getAdvancedSkills().getSummoning().setStoring(false);
		}
		if(player.isPriceChecking()) {
			sendClientRightClickRemoval();
			PriceChecker.closePriceChecker(player);
		}
		if(player.getBankSearchingAttribtues().isSearchingBank())
			BankSearchAttributes.stopSearch(player, false);
		if(player.isBanking()) {
			sendClientRightClickRemoval();
			player.setBanking(false);
		}*/
		player.setDialogueActionId(-1);
		player.setInterfaceId(-1);
		player.getAppearance().setCanChangeAppearance(false);
		player.getSession().queueMessage(new PacketBuilder(219));
		return this;
	}

	public PacketSender sendInterfaceSet(int interfaceId, int sidebarInterfaceId) {
		PacketBuilder out = new PacketBuilder(248);
		out.putShort(interfaceId, ValueType.A);
		out.putShort(sidebarInterfaceId);
		player.getSession().queueMessage(out);
		player.setInterfaceId(interfaceId);
		return this;
	}

	public PacketSender sendItemContainer(ItemContainer container, int interfaceId) {
		PacketBuilder out = new PacketBuilder(53, PacketType.SHORT);
		out.putShort(interfaceId);
		out.putShort(container.capacity());
		for (Item item: container.getItems()) {
			if(item == null) {
				out.put(0);
				out.putShort(0, ValueType.A, ByteOrder.LITTLE);
				continue;
			}
			if (item.getAmount() > 254) {
				out.put((byte)255);
				out.putInt(item.getAmount(), ByteOrder.INVERSE_MIDDLE);
			} else {
				out.put(item.getAmount());
			}
			out.putShort(item.getId() + 1, ValueType.A, ByteOrder.LITTLE);
		}
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendItemOnInterface(int frame, int item, int slot, int amount) {
		PacketBuilder out = new PacketBuilder(34, PacketType.SHORT);
		out.putShort(frame);
		out.put(slot);
		out.putShort(item + 1);
		if (amount > 254) {
			out.put(255);
			out.putInt(amount);
		} else {
			out.put(amount);
		}
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendDuelEquipment() {
		for (int i = 0; i < player.getEquipment().getItems().length; i++) {
			PacketBuilder out = new PacketBuilder(34, PacketType.SHORT);
			out.putShort(13824);
			out.put(i);
			out.putShort(player.getEquipment().getItems()[i].getId()+1);
			out.put(255);
			out.putInt(player.getEquipment().getItems()[i].getAmount());
			player.getSession().queueMessage(out);
		}
		return this;
	}

	public PacketSender sendSmithingData(int id, int slot, int column, int amount) {
		PacketBuilder out = new PacketBuilder(34, PacketType.SHORT);
		out.putShort(column);
		out.put(4);
		out.putInt(slot);
		out.putShort(id+1);
		out.put(amount);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendInterfaceItems(int interfaceId, CopyOnWriteArrayList<Item> items) {
		PacketBuilder out = new PacketBuilder(53, PacketType.SHORT);
		out.putShort(interfaceId);
		out.putShort(items.size());
		int current = 0;
		for (Item item : items) {
			if (item.getAmount() > 254) {
				out.put((byte)255);
				out.putInt(item.getAmount(), ByteOrder.INVERSE_MIDDLE);
			} else {
				out.put(item.getAmount());
			}
			out.putShort(item.getId() + 1, ValueType.A, ByteOrder.LITTLE);
			current++;
		}
		if (current < 27) {
			for (int i = current; i < 28; i++) {
				out.put(1);
				out.putShort(-1, ValueType.A, ByteOrder.LITTLE);
			}
		}
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendItemOnInterface(int interfaceId, int item, int amount) {
		if(item <= 0)
			item = -1;
		if(amount <= 0)
			amount = 1;
		if(interfaceId <= 0)
			return this;
		PacketBuilder out = new PacketBuilder(53, PacketType.SHORT);
		out.putShort(interfaceId);
		out.putShort(1);
		if (amount > 254) {
			out.put((byte)255);
			out.putInt(amount, ByteOrder.INVERSE_MIDDLE);
		} else {
			out.put(amount);
		}
		out.putShort(item + 1, ValueType.A, ByteOrder.LITTLE);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendItemsOnInterface(int interfaceId, Item[] items) {
		PacketBuilder out = new PacketBuilder(53, PacketType.SHORT);
		if (items == null) {
			out.putShort(0);
			out.put(0);
			out.putShort(0, ValueType.A, ByteOrder.LITTLE);
			player.getSession().queueMessage(out);
			return this;
		}
		out.putShort(items.length);
		for (Item item : items) {
			if (item != null) {
				if (item.getAmount() > 254) {
					out.put(255);
					out.putInt(item.getAmount(), ByteOrder.INVERSE_MIDDLE);
				} else {
					out.put(item.getAmount());
				}
				out.putShort(item.getId() + 1, ValueType.A, ByteOrder.LITTLE);
			} else {
				out.put(0);
				out.putShort(0, ValueType.A, ByteOrder.LITTLE);
			}
		}
		player.getSession().queueMessage(out);
		return this;
	}

	/*public PacketSender sendConstructionInterfaceItems(ArrayList<Furniture> items) {
		PacketBuilder builder = new PacketBuilder(53, PacketType.SHORT);
		builder.writeShort(38274);
		builder.writeShort(items.size());
		for (int i = 0; i < items.size(); i++) {
			builder.writeByte(1);
			builder.writeLEShortA(items.get(i).getItemId() + 1);
		}
		player.write(builder.toPacket());
		return this;
	}*/


	public PacketSender sendInteractionOption(String option, int slot, boolean top) {
		PacketBuilder out = new PacketBuilder(104, PacketType.BYTE);
		out.put(slot, ValueType.C);
		out.put(top ? 1 : 0, ValueType.A);
		out.putString(option);
		player.getSession().queueMessage(out);
		PlayerInteractingOption interactingOption = PlayerInteractingOption.forName(option);
		if(option != null)
			player.setPlayerInteractingOption(interactingOption);
		return this;
	}

	public PacketSender sendString(int id, String string) {
		if(id == 18250 && string.length() < 2)
			return this;
		if(!player.getFrameUpdater().shouldUpdate(string, id))
			return this;
		PacketBuilder out = new PacketBuilder(126, PacketType.SHORT);
		out.putString(string);
		out.putShort(id);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendClientRightClickRemoval() {
		sendString(0, "[CLOSEMENU]");
		return this;
	}


	public PacketSender sendShadow() {
		PacketBuilder out = new PacketBuilder(29);
		out.put(player.getShadowState());
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends the players rights ordinal to the client.
	 * @return	The packetsender instance.
	 */
	public PacketSender sendRights() {
		PacketBuilder out = new PacketBuilder(127);
		out.put(player.getRights().ordinal());
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends a hint to specified position.
	 * @param position			The position to create the hint.
	 * @param tilePosition		The position on the square (middle = 2; west = 3; east = 4; south = 5; north = 6)
	 * @return					The Packet Sender instance.
	 */
	public PacketSender sendPositionalHint(Position position, int tilePosition) {
		PacketBuilder out = new PacketBuilder(254);
		out.put(tilePosition);
		out.putShort(position.getX());
		out.putShort(position.getY());
		out.put(position.getZ());
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends a hint above an entity's head.
	 * @param entity	The target entity to draw hint for.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendEntityHint(Entity entity) {
		int type = entity instanceof Player ? 10 : 1;
		PacketBuilder out = new PacketBuilder(254);
		out.put(type);
		out.putShort(entity.getIndex());
		out.putInt(0, ByteOrder.TRIPLE_INT);
		player.getSession().queueMessage(out);
		return this;
	}

	/**
	 * Sends a hint removal above an entity's head.
	 * @param playerHintRemoval	Remove hint from a player or an NPC?
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendEntityHintRemoval(boolean playerHintRemoval) {
		int type = playerHintRemoval ? 10 : 1;
		PacketBuilder out = new PacketBuilder(254);
		out.put(type).putShort(-1);
		out.putInt(0, ByteOrder.TRIPLE_INT);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendMultiIcon(int value) {
		PacketBuilder out = new PacketBuilder(61);
		out.put(value);
		player.getSession().queueMessage(out);
		player.setMultiIcon(value);
		return this;
	}

	public PacketSender sendPrivateMessage(long name, PlayerRights rights, byte[] message, int size) {
		PacketBuilder out = new PacketBuilder(196, PacketType.BYTE);
		out.putLong(name);
		out.putInt(player.getRelations().getPrivateMessageId());
		out.put(rights.ordinal());
		out.putBytes(message, size);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendFriendStatus(int status) {
		PacketBuilder out = new PacketBuilder(221);
		out.put(status);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendFriend(long name, int world) {
		world = world != 0 ? world + 9 : world;
		PacketBuilder out = new PacketBuilder(50);
		out.putLong(name);
		out.put(world);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendTotalXp(long xp) {
		PacketBuilder out = new PacketBuilder(45);
		out.putLong(xp);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendIgnoreList() {
		PacketBuilder out = new PacketBuilder(214, PacketType.SHORT);
		int amount = player.getRelations().getIgnoreList().size();
		out.putShort(amount);
		for(int i = 0; i < amount; i++)
			out.putString(""+player.getRelations().getIgnoreList().get(i));
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendAnimationReset() {
		PacketBuilder out = new PacketBuilder(1);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendGraphic(Graphic graphic, Position position) {
		sendPosition(position);
		PacketBuilder out = new PacketBuilder(4);
		out.put(0);
		out.putShort(graphic.getId());
		out.put(position.getZ());
		out.putShort(graphic.getDelay());
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendGlobalGraphic(Graphic graphic, Position position) {
		sendGraphic(graphic, position);
		for(Player p : player.getLocalPlayers()) {
			if(p.getPosition().distanceToPoint(player.getPosition().getX(), player.getPosition().getY()) > 20)
				continue;
			p.getPacketSender().sendGraphic(graphic, position);
		}
		return this;
	}

	public PacketSender sendObject(GameObject object) {
		sendPosition(object.getPosition());
		PacketBuilder out = new PacketBuilder(151);
		out.put(object.getPosition().getZ(), ValueType.A);
		out.putShort(object.getId(), ByteOrder.LITTLE);
		out.put((byte) ((object.getType() << 2) + (object.getFace() & 3)), ValueType.S);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendObjectRemoval(GameObject object) {
		sendPosition(object.getPosition());
		PacketBuilder out = new PacketBuilder(101);
		out.put((object.getType() << 2) + (object.getFace() & 3), ValueType.C);
		out.put(object.getPosition().getZ());
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendObjectAnimation(GameObject object, Animation anim) {
		sendPosition(object.getPosition());
		PacketBuilder out = new PacketBuilder(160);
		out.put(0, ValueType.S);
		out.put((object.getType() << 2) + (object.getFace() & 3), ValueType.S);
		out.putShort(anim.getId(), ValueType.A);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendGroundItemAmount(Position position, Item item, int amount) {
		sendPosition(position);
		PacketBuilder out = new PacketBuilder(84);
		out.put(0);
		out.putShort(item.getId()).putShort(item.getAmount()).putShort(amount);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender createGroundItem(int itemID, int itemX, int itemY, int itemAmount) {
		sendPosition(new Position(itemX, itemY));
		PacketBuilder out = new PacketBuilder(44);
		out.putShort(itemID, ValueType.A, ByteOrder.LITTLE);
		out.putLong(itemAmount).put(0);
		//out.putShort(itemAmount).put(0);
		player.getSession().queueMessage(out);
		return this;
	}
	public PacketSender createGroundItem(int itemID, Position position, int itemAmount) {
		sendPosition(position);
		PacketBuilder out = new PacketBuilder(44);
		out.putShort(itemID, ValueType.A, ByteOrder.LITTLE);
		out.putShort(itemAmount).put(0);
		player.getSession().queueMessage(out);
		return this;
	}
	public PacketSender removeGroundItem(int itemID, int itemX, int itemY, int Amount) {
		sendPosition(new Position(itemX, itemY));
		PacketBuilder out = new PacketBuilder(156);
		out.put(0, ValueType.A);
		out.putShort(itemID);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendPosition(final Position position) {
		final Position other = player.getLastKnownRegion();		
		PacketBuilder out = new PacketBuilder(85);
		out.put(position.getY() - 8 * other.getRegionY(), ValueType.C);
		out.put(position.getX() - 8 * other.getRegionX(), ValueType.C);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendConsoleMessage(String message) {
		PacketBuilder out = new PacketBuilder(123, PacketType.BYTE);
		out.putString(message);
		player.getSession().queueMessage(out);
		return this;
	}

	public PacketSender sendInterfaceSpriteChange(int childId, int firstSprite, int secondSprite) {
		//	player.write(new PacketBuilder(140).writeShort(childId).writeByte((firstSprite << 0) + (secondSprite & 0x0)).toPacket());
		return this;
	}

	public int getRegionOffset(Position position) {
		int x = position.getX() - (position.getRegionX() << 4);
		int y = position.getY() - (position.getRegionY() & 0x7);
		int offset = ((x & 0x7)) << 4 + (y & 0x7);
		return offset;
	}

	public PacketSender(Player player) {
		this.player = player;
	}

	private Player player;

	public PacketSender sendProjectile(Position position, Position offset,
			int angle, int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time) {
		sendPosition(position);
		PacketBuilder out = new PacketBuilder(117);
		out.put(angle);
		out.put(offset.getY());
		out.put(offset.getX());
		out.putShort(lockon);
		out.putShort(gfxMoving);
		out.put(startHeight);
		out.put(endHeight);
		out.putShort(time);
		out.putShort(speed);
		out.put(16);
		out.put(64);
		player.getSession().queueMessage(out);
		return this;
	}

	public void sendAllProjectile(Position position, Position offset,
			int angle, int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time) {
		for (Player all : player.getLocalPlayers()) {
			if (all == null) {
				continue;
			}

			if (all.getPosition().isViewableFrom(position)) {
				all.getPacketSender().sendProjectile(position, offset, angle,
						speed, gfxMoving, startHeight, endHeight, lockon, time);
			}
		}
	}

	public PacketSender sendCombatBoxData(Character character) {
		PacketBuilder out = new PacketBuilder(125, PacketType.BYTE);
		out.putShort(character.getIndex());
		out.put(character.isPlayer() ? 0 : 1);
		if(character.isPlayer()) {
			player.getSession().queueMessage(out);
		} else {
			NPC npc = (NPC) character;
			boolean sendList = npc.getDefaultConstitution() >= 2500 && Location.inMulti(npc);
			out.put(sendList ? 1 : 0);
			if(sendList) {
				List<DamageDealer> list = npc.fetchNewDamageMap() ? npc.getCombatBuilder().getTopKillers(npc) : npc.getDamageDealerMap();
				if(npc.fetchNewDamageMap()) {
					npc.setDamageDealerMap(list);
					npc.setFetchNewDamageMap(false);
				}
				out.put(list.size());
				for(int i = 0; i < list.size(); i++) {
					DamageDealer dd = list.get(i);
					out.putString(dd.getPlayer().getUsername());
					out.putShort(dd.getDamage());
				}
			}
			player.getSession().queueMessage(out);
		}
		return this;
	}

	public PacketSender sendHideCombatBox() {
		player.getSession().queueMessage(new PacketBuilder(128));
		return this;
	}

	public void sendObject_cons(int objectX, int objectY, int objectId, int face, int objectType, int height) {
		sendPosition(new Position(objectX, objectY));
		PacketBuilder bldr = new PacketBuilder(152);
		if (objectId != -1) // removing
			player.getSession().queueMessage(bldr.put(0, ValueType.S).putShort(objectId, ByteOrder.LITTLE).put((objectType << 2) + (face & 3), ValueType.S).put(height));
		if (objectId == -1 || objectId == 0 || objectId == 6951) {
			CustomObjects.spawnObject(player, new GameObject(objectId, new Position(objectX, objectY, height)));
		}
	}

	/*public PacketSender constructMapRegion(Palette palette) {
		PacketBuilder bldr = new PacketBuilder(241, PacketType.SHORT);
		if(palette != null) {
			bldr.putString("palette"); //Inits map construction sequence
			bldr.putString(""+(player.getPosition().getRegionY() + 6)+"");
			bldr.putString(""+(player.getPosition().getRegionX() + 6)+"");
			for (int z = 0; z < 4; z++) {
				for (int x = 0; x < 13; x++) {
					for (int y = 0; y < 13; y++) {
						PaletteTile tile = palette.getTile(x, y, z);
						boolean b = false;
						if (x < 2 || x > 10 || y < 2 || y > 10)
							b = true;
						int toWrite = !b && tile != null ? 5 : 0;
						bldr.putString(""+toWrite+"");
						if(toWrite == 5) {
							int val = tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1;
							bldr.putString(""+val+"");
						}
					}
				}
			}
		} else {
			bldr.putString("null"); //Resets map construction sequence
		}
		player.getSession().queueMessage(bldr);
		return this;
	}
	 */
	public PacketSender constructMapRegion(Palette palette) {
		PacketBuilder bldr = new PacketBuilder(241, PacketType.SHORT);
		bldr.putShort(player.getPosition().getRegionY() + 6, ValueType.A);
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					PaletteTile tile = palette.getTile(x, y, z);
					boolean b = false;
					if (x < 2 || x > 10 || y < 2 || y > 10)
						b = true;
					int toWrite = !b && tile != null ? 5 : 0;
					bldr.put(toWrite);
					if(toWrite == 5) {
						int val = tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1;
						bldr.putString(""+val+"");
					}
				}
			}
		}
		bldr.putShort(player.getPosition().getRegionX() + 6);
		player.getSession().queueMessage(bldr);
		return this;
	}

	public PacketSender sendConstructionInterfaceItems(ArrayList<Furniture> items) {
		PacketBuilder builder = new PacketBuilder(53, PacketType.SHORT);
		builder.putShort(38274);
		builder.putShort(items.size());
		for (int i = 0; i < items.size(); i++) {
			builder.put(1);
			builder.putShort(items.get(i).getItemId() + 1, ValueType.A, ByteOrder.LITTLE);
		}
		player.getSession().queueMessage(builder);
		return this;
	}

	public PacketSender sendObjectsRemoval(int chunkX, int chunkY, int height) {
		player.getSession().queueMessage(new PacketBuilder(153).put(chunkX).put(chunkY).put(height));
		return this;
	}
}
