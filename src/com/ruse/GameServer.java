package com.ruse;

import java.util.logging.Level;

import com.ruse.GameSettings;
import java.util.logging.Logger;

import com.ruse.util.ShutdownHook;
/**
 * The starting point of Ruse.
 * @author Gabriel
 * @author Samy
 */ 
public class GameServer {

	private static final GameLoader loader = new GameLoader(GameSettings.GAME_PORT);
	private static final Logger logger = Logger.getLogger("Ruse");
	private static boolean updating;

	public static void main(String[] params) {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		try {

			logger.info("Initializing the loader...");
			loader.init();
			loader.finish();
			logger.info("The loader has finished loading utility tasks.");
			logger.info(GameSettings.RSPS_NAME+" is now online on port "+GameSettings.GAME_PORT+"!");
			//DiscordMessager.sendDebugMessage("The server has finished reloading, and is now online!");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Could not start "+GameSettings.RSPS_NAME+"! Program terminated.", ex);
			System.exit(1);
		}
		
		//PkingBots.init();
	}

	public static GameLoader getLoader() {
		return loader;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setUpdating(boolean updating) {
		GameServer.updating = updating;
	}

	public static boolean isUpdating() {
		return GameServer.updating;
	}
}