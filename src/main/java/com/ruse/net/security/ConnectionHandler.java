package com.ruse.net.security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruse.GameServer;
import com.ruse.GameSettings;
import com.ruse.net.login.LoginDetailsMessage;
import com.ruse.net.login.LoginResponses;
import com.ruse.world.content.PlayerPunishment;
import com.ruse.world.entity.impl.player.Player;

/**
 * A lot of connection-related stuff.
 * Really messy.
 */

public class ConnectionHandler {
		
	public static void init() {
		loadHostBlacklist();
		loadBannedComputers();
		loadStarters();
	}
	
	public static int getResponse(Player player, LoginDetailsMessage msg) {

		String host = msg.getHost();
		String serial_number = msg.getSerialNumber();

		if (PlayerPunishment.banned(player.getUsername())) {
			return LoginResponses.LOGIN_DISABLED_ACCOUNT;
		}

		if(isBlocked(host)) {
			return LoginResponses.LOGIN_REJECT_SESSION;
		}
		
		if (PlayerPunishment.IPBanned(host)) {
			return LoginResponses.LOGIN_DISABLED_IP;
		}

		if(!isLocal(host)) {
			if(CONNECTIONS.get(host) != null) {
				if(CONNECTIONS.get(host) >= GameSettings.CONNECTION_AMOUNT) {
					System.out.println("Connection limit reached: "+player.getUsername()+". Host: "+host);
					return LoginResponses.LOGIN_SUCCESSFUL;//LoginResponses.LOGIN_CONNECTION_LIMIT;
				}
			}
		}
		return LoginResponses.LOGIN_SUCCESSFUL;
	}

	/** BLACKLISTED CONNECTIONS SUCH AS PROXIES **/
	private static final String BLACKLIST_DIR = "./data/saves/blockedhosts.txt";
	private static List<String> BLACKLISTED_HOSTNAMES = new ArrayList<String>();

	/** BLACKLISTED HARDWARE NUMBERS **/
	private static final String BLACKLISTED_SERIAL_NUMBERS_DIR = "./data/saves/blockedhardwares.txt";
	private static List<String> BLACKLISTED_SERIAL_NUMBERS = new ArrayList<String>();

	
	/**
	 * The concurrent map of registered connections.
	 */
	private static final Map<String, Integer> CONNECTIONS = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	/** SAVED STARTERS **/
	private static final String STARTER_FILE = "./data/saves/starters.txt";

	/**
	 * The concurrent map of registered connections.
	 */
	private static final Map<String, Integer> STARTERS = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	private static void loadHostBlacklist() {
		String word = null;
		try {
			BufferedReader in = new BufferedReader(
					new FileReader(BLACKLIST_DIR));
			while ((word = in.readLine()) != null)
				BLACKLISTED_HOSTNAMES.add(word.toLowerCase());
			in.close();
			in = null;
		} catch (final Exception e) {
			System.out.println("Could not load blacklisted hosts.");
		}
	}

	public static boolean isBlocked(String host) {
		return BLACKLISTED_HOSTNAMES.contains(host.toLowerCase());
	}

	private static void loadBannedComputers() {
		String line = null;

		try {

			BufferedReader in = new BufferedReader(
					new FileReader(BLACKLISTED_SERIAL_NUMBERS_DIR));
			while ((line = in.readLine()) != null) {

				if (line.contains("="))
					BLACKLISTED_SERIAL_NUMBERS.add(String.valueOf(line.substring(line.indexOf("=") + 1)));
			}
			in.close();
			in = null;
		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println("Could not load blacklisted hadware numbers.");
		}
	}

	public static void banComputer(String playername, String serial_number) {
		if(BLACKLISTED_SERIAL_NUMBERS.contains(serial_number))
			return;
		BLACKLISTED_SERIAL_NUMBERS.add(serial_number);
		GameServer.getLoader().getEngine().submit(() -> {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(BLACKLISTED_SERIAL_NUMBERS_DIR, true));
				writer.write(""+playername+"="+serial_number);
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private static void loadUUIDBans() {
		String line = null;
		try {
			BufferedReader in = new BufferedReader(
					new FileReader(BLACKLISTED_SERIAL_NUMBERS_DIR));
			while ((line = in.readLine()) != null) {
				if(line.contains("="))
					BLACKLISTED_SERIAL_NUMBERS.add(String.valueOf(line.substring(line.indexOf("=")+1)));
			}
			in.close();
			in = null;
		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println("Could not load blacklisted hadware numbers.");
		}
	}

	public static void reloadUUIDBans() {
		BLACKLISTED_SERIAL_NUMBERS.clear();
		loadUUIDBans();
	}


	public static void loadStarters() {
		try {
			BufferedReader r = new BufferedReader(new FileReader(STARTER_FILE));
			while(true) {
				String line = r.readLine();
				if(line == null) {
					break;
				} else {
					line = line.trim();
				}
				addStarter(line, false);
			}
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public static void add(String host) {
		if(!isLocal(host)) {
			if(CONNECTIONS.get(host) == null) {
				CONNECTIONS.put(host, 1);
			} else {
				int amt = CONNECTIONS.get(host) + 1;
				CONNECTIONS.put(host, amt);
			}
		}
	}

	public static void remove(String host) {
		if(!isLocal(host)) {
			if(CONNECTIONS.get(host) != null) {
				int amt = CONNECTIONS.get(host) - 1;
				if(amt == 0) {
					CONNECTIONS.remove(host);
				} else {
					CONNECTIONS.put(host, amt);
				}
			}
		}
	}
	
	public static int getStarters(String host) {
		if(host == null) {
			return GameSettings.MAX_STARTERS_PER_IP;
		}
		if(!ConnectionHandler.isLocal(host)) {
			if(STARTERS.get(host) != null) {
				return STARTERS.get(host);
			}
		}
		return 0;
	}

	public static void addStarter(String host, boolean write) {
		if(!ConnectionHandler.isLocal(host)) {
			if(STARTERS.get(host) == null) {
				STARTERS.put(host, 1);
			} else {
				int amt = STARTERS.get(host) + 1;
				STARTERS.put(host, amt);
			}
			if(write) {
				GameServer.getLoader().getEngine().submit(() -> {
					try {
						BufferedWriter writer = new BufferedWriter(new FileWriter(STARTER_FILE, true));
						writer.write(""+host+"");
						writer.newLine();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		}
	}

	/**
	 * Determines if the specified host is connecting locally.
	 *
	 * @param host
	 *            the host to check if connecting locally.
	 * @return {@code true} if the host is connecting locally, {@code false}
	 *         otherwise.
	 */
	public static boolean isLocal(String host) {
		return host == null || host.equals("null") || host.equals("127.0.0.1") || host.equals("localhost");
	}
}
