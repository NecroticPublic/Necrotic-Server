package com.ruse.world.content.minigames.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ruse.model.GroundItem;
import com.ruse.world.entity.impl.player.Player;

public class FallyMassacre {

	public static int TOTAL_PLAYERS = 0;
	public static int PLAYERS_ALIVE = 0;
	public static int GRIDCHANGES = 0;
	public static int GRIDTOTAL = 0;
	public static int GRIDCURRENT = 0;

	/**
	 * @note Stores player and State
	 */
	private static Map<Player, String> playerMap = new HashMap<Player, String>();

	/*
	 * Stores items
	 */
	private static CopyOnWriteArrayList<GroundItem> itemList = new CopyOnWriteArrayList<GroundItem>();

	/**
	 * @return HashMap Value
	 */
	public static String getState(Player player) {
		return playerMap.get(player);
	}

	/**
	 * @note States of minigames
	 */
	public static final String WAITING = "WAITING";
	public static final String PLAYING = "PLAYING";
	
	/**
	 * Is a game running?
	 */
	private static boolean gameRunning;

	
}
