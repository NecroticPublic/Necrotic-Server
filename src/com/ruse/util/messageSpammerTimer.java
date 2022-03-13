package com.ruse.util;

import com.ruse.GameSettings;
import com.ruse.webhooks.discord.DiscordMessager;
import com.ruse.world.World;

/**
 * Created by  Crim & overhauled on 5/06/2016. Now uses Yell_Bot_Prefix, counts and resets automatically VS the old random system.
 */
public class messageSpammerTimer {

	private static int count = 0;
	private static long massMessageTimer = System.currentTimeMillis();
	private static String prefix = "<img=10><shad=0><col=FF0077> ";
	private static boolean doubleExp = false;
	
	public static void massMessageHandler() {

		String[] messages = { 
				// h'ween "@or1@Happy Halloween from Necrotic! Pumpkins & Scythes drop this weekend!",
				// xmas "@red@Merry @gre@Christmas <col=FF0077>from Necrotic! Help Santa via the Snomwan at home.",
				// xmas "@red@Sleighs are dropping for Christmas. @gre@They'll be gone January.",
				// friday 13th "<col=1C1919><shad=ffffff>Try the *Friday the 13th* even through the red portal @ ::home"
				"Voice chat on the server? Do ::discord to check it out!",
				"Celebrate the weekends with double exp! Friday, sat & sundays.",
				"::Vote for rewards! Bonus EXP & points are a few clicks away.",
				"Shift-clicking on an item in your inventory will drop it.",
				"Check our forums to stay up to date on everything Necrotic!",
				"Make sure you ::vote every 12 hours to help the server grow!", 
				"Found a bug? Report it on the ::forums",
				"Member status keeps us alive! Visit ::shop for more info!",
				//"Thank you for playing Necrotic!",
				"Need staff assistance immediately? Use the command ::help",
				"Have ideas? Let us know on the ::forums",
				//"Please buy/sell items at ::trade to avoid spamming home!",
				//"Bored? Visit ::chill for some relaxation.",
				"Ghost Town is in level 60 Multi wild! Be careful!",
				"Read up on the ::rules to stay out of trouble!",
				"Have questions? Ask in the clanchat \"Necrotic!\"",
				"Want to opt-out of these messages? Type ::toggleglobalmessages",
		};

			int max = messages.length;
	
			if (count == max) {
				//System.out.println("Resetting count to 0");
				count = 0;
			}
	
			if (System.currentTimeMillis() - massMessageTimer > GameSettings.massMessageInterval) {
				World.sendFilteredMessage(prefix+(messages[count]));
				massMessageTimer = System.currentTimeMillis();
				count++;
				
	
				
			if (count == 10) {
					if (World.getPlayers().size() > 0) {
						DiscordMessager.sendInGameMessage(":innocent: There is currently "+World.getPlayers().size()+" "+(World.getPlayers().size() == 1 ? "player" : "players") +" online!");
					}
					if (doubleExp && Misc.isWeekend()) {
						DiscordMessager.sendInGameMessage(":mega: Come get some **DOUBLE EXP**, it's here all weekend long and __STACKS__ with vote scrolls! :lifter:");
					}
					doubleExp = !doubleExp;
			}
			
		}
	}
}