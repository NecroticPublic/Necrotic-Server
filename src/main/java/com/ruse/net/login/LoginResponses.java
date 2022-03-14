package com.ruse.net.login;

import com.ruse.GameServer;
import com.ruse.GameSettings;
import com.ruse.net.security.ConnectionHandler;
import com.ruse.util.NameUtils;
import com.ruse.webhooks.discord.DiscordMessager;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.PlayerLoading;

public final class LoginResponses {
	
	public static final int getResponse(Player player, LoginDetailsMessage msg) {
		if (World.getPlayers().isFull()) {
			return LOGIN_WORLD_FULL;
		} 
		if(GameServer.isUpdating()) {
			return LOGIN_GAME_UPDATE;
		} 
		if (!NameUtils.isValidName(player.getUsername())) {
			return LOGIN_INVALID_CREDENTIALS;
		} 
		if(player.getUsername().startsWith(" ")) {
			return USERNAME_STARTS_WITH_SPACE;
		} 
		if(msg.getClientVersion() != GameSettings.GAME_VERSION || msg.getUid() != GameSettings.GAME_UID) {
			DiscordMessager.sendDebugMessage("Client connecting with V "+msg.getClientVersion() +", UID "+msg.getUid()+" from "+msg.getHost()+" to account "+msg.getUsername()+"\nServer Version is "+GameSettings.GAME_VERSION+", Server UID is "+GameSettings.GAME_UID);
			return OLD_CLIENT_VERSION;
		}
		if(World.getPlayerByName(player.getUsername()) != null) {
			return LOGIN_ACCOUNT_ONLINE;
		}
		
		/** CHAR FILE LOADING **/
		int playerLoadingResponse = PlayerLoading.getResult(player);
		if(playerLoadingResponse != LOGIN_SUCCESSFUL && playerLoadingResponse != NEW_ACCOUNT) {
			return playerLoadingResponse;
		}
		
		/** PREVENTING IMPERSONATING **/
		if(playerLoadingResponse == LoginResponses.NEW_ACCOUNT && (msg.getUsername().toLowerCase().contains("crimson") || msg.getUsername().toLowerCase().contains("mod") || msg.getUsername().toLowerCase().contains("admin"))) {
			return LoginResponses.LOGIN_INVALID_CREDENTIALS;
		}
		
		/** BANS AND ACCESS LIMITS **/
		int hostHandlerResponse = ConnectionHandler.getResponse(player, msg);
		if(hostHandlerResponse != LOGIN_SUCCESSFUL) {
			return hostHandlerResponse;
		}
		
		return playerLoadingResponse;
	}
	
	/**
	 * This opcode is used for data (session keys, name, password, etc)
	 * exchange.
	 */
	public static final int LOGIN_EXCHANGE_DATA = 0;
	
	/**
	 * This login opcode is used for a 2000ms delay,
	 * after which a reconnection is attempted.
	 */
	public static final int LOGIN_DELAY = 1;

	/**
	 * This login opcode signifies a successful login.
	 */
	public static final int LOGIN_SUCCESSFUL = 2;
	
	/**
	 * This login opcode is used when the player
	 * has entered an invalid username and/or password.
	 */
	public static final int LOGIN_INVALID_CREDENTIALS = 3;
	
	/**
	 * This login opcode is used when the account
	 * has been disabled.
	 */
	public static final int LOGIN_DISABLED_ACCOUNT = 4;
	
	/**
	 * This login opcode is used when the player's IP
	 * has been disabled.
	 */
	public static final int LOGIN_DISABLED_COMPUTER = 22;
	
	/**
	 * This login opcode is used when the player's IP
	 * has been disabled.
	 */
	public static final int LOGIN_DISABLED_IP = 27;
	
	
	/**
	 * This login opcode is used when the account
	 * attempting to connect is already online in the server.
	 */
	public static final int LOGIN_ACCOUNT_ONLINE = 5;
	
	/**
	 * This login opcode is used when the game has been or
	 * is being updated.
	 */
	public static final int LOGIN_GAME_UPDATE = 6;
	
	/**
	 * This login opcode is used when server is launching.
	 */
	public static final int SERVER_LAUNCHING = 23;
	
	/**
	 * This login opcode is used when the world being
	 * connected to is full.
	 */
	public static final int LOGIN_WORLD_FULL = 7;
	
	/**
	 * This login opcode is used when server is offline.
	 */
	public static final int LOGIN_SERVER_OFFLINE = 8;
	
	/**
	 * This login opcode is used when the connections
	 * from an ip address has exceeded {@link org.desolace.net.NetworkConstants.MAXIMUM_CONNECTIONS}.
	 */
	public static final int LOGIN_CONNECTION_LIMIT = 9;
	
	/**
	 * This login opcode is used when a connection
	 * has received a bad session id.
	 */
	public static final int LOGIN_BAD_SESSION_ID = 10;
	
	/**
	 * This login opcode is used when the login procedure
	 * has rejected the session.
	 */
	public static final int LOGIN_REJECT_SESSION = 11;
	
	/**
	 * This login opcode is used when a non-member player
	 * is attempting to login to a members world.
	 */
	public static final int LOGIN_WORLD_MEMBER_ACCOUNT_REQUIRED = 12;
	
	/**
	 * This login opcode is used when the login procedure 
	 * could not be completed.
	 */
	public static final int LOGIN_COULD_NOT_COMPLETE = 13;
	
	/**
	 * This login opcode is used when the game is being updated.
	 */
	public static final int LOGIN_WORLD_UPDATE = 14;
	
	/**
	 * This login opcode is received upon a reconnection
	 * so that chat messages will not dissappear.
	 */
	public static final int LOGIN_RECONNECTION = 15;
	
	/**
	 * This login opcode is used when a player
	 * has exceeded their attempts to login.
	 */
	public static final int LOGIN_EXCESSIVE_ATTEMPTS = 16;
	
	/**
	 * This login opcode is used when a member is attempting
	 * to login to a non-members world in a members-only area.
	 */
	public static final int LOGIN_AREA_MEMBER_ACCOUNT_REQUIRED = 17;
	
	/**
	 * This login opcode is used when the login server
	 * is invalid.
	 */
	public static final int LOGIN_INVALID_LOGIN_SERVER = 20;
	
	/**
	 * This login opcode is used when a player has just
	 * left another world and has to wait a delay before
	 * entering a new world.
	 */
	public static final int LOGIN_WORLD_DELAY = 21;
	
	/**
	 * This login opcode is used when a player has
	 * entered invalid credentials.
	 */
	public static final int INVALID_CREDENTIALS = 28;
	
	/**
	 * This login opcode is used when a player has
	 * attempted to login with a old client.
	 */
	public static final int OLD_CLIENT_VERSION = 30;
	
	/**
	 * This login opcode is used when a player's username is started with a space
	 */
	public static final int USERNAME_STARTS_WITH_SPACE = 31;
	
	/**
	 * 
	 * The new connection login request
	 * byte data sent by the client.
	 */
	public static final int NEW_CONNECTION_LOGIN_REQUEST = 16;
	
	/**
	 * The reconnection login request
	 * byte data sent by the client.
	 */
	public static final int RECONNECTING_LOGIN_REQUEST = 18;
	
	/**
	 * New account
	 */
	public static final int NEW_ACCOUNT = -1;
	
}
