package com.ruse.util;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.jboss.netty.buffer.ChannelBuffer;

import com.ruse.GameSettings;
import com.ruse.model.Flag;
import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.WeaponAnimations;
import com.ruse.model.definitions.WeaponInterfaces;
import com.ruse.world.World;
import com.ruse.world.content.BonusManager;
import com.ruse.world.content.combat.CombatContainer.ContainerHit;
import com.ruse.world.entity.impl.player.Player;

public class Misc {
	
	private static boolean OWNER = false;


	/** Random instance, used to generate pseudo-random primitive types. */
	public static final Random RANDOM = new Random(System.currentTimeMillis());

	private static ZonedDateTime zonedDateTime;
	public static final int HALF_A_DAY_IN_MILLIS = 43200000;

	public static ContainerHit[] concat(ContainerHit[] a, ContainerHit[] b) {
		int aLen = a.length;
		int bLen = b.length;
		ContainerHit[] c= new ContainerHit[aLen+bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}
	
	public static void sendEmptyStrings(Player player, int maxString) {
		System.out.println(player.getUsername()+" called sendEmptyStrings, maxString: "+maxString);
		for (int i = 0; i < maxString; i++) {
			player.getPacketSender().sendString(i, String.valueOf(i));
		}
		System.out.println(player.getUsername()+" completed sendEmptyStrings up to "+maxString);
	}

	public static List<Player> getCombinedPlayerList(Player p) {
		List<Player> plrs = new LinkedList<Player>();
		for(Player localPlayer : p.getLocalPlayers()) {
			if(localPlayer != null)
				plrs.add(localPlayer);
		}
		plrs.add(p);
		return plrs;
	}
	
	public static String capitalizeString(String string) {
		  char[] chars = string.toLowerCase().toCharArray();
		  boolean found = false;
		  for (int i = 0; i < chars.length; i++) {
		    if (!found && Character.isLetter(chars[i])) {
		      chars[i] = Character.toUpperCase(chars[i]);
		      found = true;
		    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
		      found = false;
		    }
		  }
		  return String.valueOf(chars);
		}


	public static String getCurrentServerTime() {
		zonedDateTime = ZonedDateTime.now();
		int hour = zonedDateTime.getHour();
		String hourPrefix = hour < 10 ? "0"+hour+"" : ""+hour+"";
		int minute = zonedDateTime.getMinute();
		String minutePrefix = minute < 10 ? "0"+minute+"" : ""+minute+"";
		return ""+hourPrefix+":"+minutePrefix+"";
	}

	public static String getTimePlayed(long totalPlayTime) {
		final int sec = (int) (totalPlayTime / 1000), h = sec / 3600, m = sec / 60 % 60, s = sec % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
	}
	
	public static boolean canAddItemToInventory(Player player, int itemId) {
		return player.getInventory().getFreeSlots() > 0 || ItemDefinition.forId(itemId).isStackable() && player.getInventory().contains(itemId);
	}
	
	/**
	 * 
	 * @author Crimson 
	 * @since Aug 23, 2017
	 * @param input - either current salt
	 * @return
	 */
	public static int findBcryptRounds(String input) {
		if (input.length() < 6) {
			return -1;
		}
		
		return Integer.parseInt(input.substring(4, 6));
	}
	
	public static boolean needsNewSalt(String currentSalt) {
		if (!validString(currentSalt)) {
			System.out.println("validstring did it");
			return true;
		}
		int bcr = findBcryptRounds(currentSalt);
		if (bcr >= 4) { //bcrypt requires minimum 4 rounds, max 30
			if (bcr == GameSettings.BCRYPT_ROUNDS) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean validString(String target) {
		if (target == null) {
			return false;
		}
		if (target.equalsIgnoreCase("")) {
			return false;
		}
		if (target.equalsIgnoreCase("null")) {
			return false;
		}
		return true;
	}
	

	public static int getMinutesPlayed(Player p) {
		long totalPlayTime = p.getTotalPlayTime() + (p.getRecordedLogin().elapsed());
		int sec = (int) (totalPlayTime / 1000), h = sec / 3600, m = sec / 60 % 60;
		for(int i = 0; i < h; i++)
			m+=60;
		return m;
	}

	public static String getHoursPlayed(long totalPlayTime) {
		final int sec = (int) (totalPlayTime / 1000), h = sec / 3600;
		return (h < 10 ? "0" + h : h) + "h";
	}
	
	public static boolean isStringNumeric(String str) {
		DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
		char localeMinusSign = currentLocaleSymbols.getMinusSign();

		if (!Character.isDigit(str.charAt(0)) && str.charAt(0) != localeMinusSign)
			return false;

		boolean isDecimalSeparatorFound = false;
		char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();

		for (char c : str.substring(1).toCharArray()) {
			if (!Character.isDigit(c)) {
				if (c == localeDecimalSeparator && !isDecimalSeparatorFound) {
					isDecimalSeparatorFound = true;
					continue;
				}
				return false;
			}
		}
		return true;
	}

	public static int getMinutesPassed(long t) {
		int seconds=(int) ((t/1000)%60);
		int minutes=(int) (((t-seconds)/1000)/60);
		return minutes;
	}

	public static Item[] concat(Item[] a, Item[] b) {
		int aLen = a.length;
		int bLen = b.length;
		Item[] c= new Item[aLen+bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

	public static Player getCloseRandomPlayer(List<Player> plrs) {
		int index = Misc.getRandom(plrs.size() - 1);
		if(index > 0) 
			return plrs.get(index);
		return null;
	}

	public static byte directionDeltaX[] = new byte[]{ 0, 1, 1, 1, 0,-1,-1,-1 };
	public static byte directionDeltaY[] = new byte[]{ 1, 1, 0,-1,-1,-1, 0, 1 };	
	public static byte xlateDirectionToClient[] = new byte[]{ 1, 2, 4, 7, 6, 5, 3, 0 };
	
	public static void listUntradeables() {
		System.out.println("--Untradeable Items--");
		for (int i : GameSettings.UNTRADEABLE_ITEMS) {
			if (ItemDefinition.forId(i) != null && ItemDefinition.forId(i).getName() != null) {
				System.out.println(ItemDefinition.forId(i).getName()+","+i+",UNTRADEABLE");
			} else {
				System.out.println("INVALID,"+i+",UNTRADEABLE");
			}
		}
		System.out.println("\n--Unsellable Items--");
		for (int i : GameSettings.UNSELLABLE_ITEMS) {
			if (ItemDefinition.forId(i) != null && ItemDefinition.forId(i).getName() != null) {
				System.out.println(ItemDefinition.forId(i).getName()+","+i+",UNSELLABLE");
			} else {
				System.out.println("INVALID,"+i+",UNSELLABLE");
			}
		}
		System.out.println("\n--Finish--");
	}

	public static int getRandom(int range) {
		return (int) (java.lang.Math.random() * (range + 1));
	}

	public static int direction(int srcX, int srcY, int x, int y) {
		double dx = (double) x - srcX, dy = (double) y - srcY;
		double angle = Math.atan(dy / dx);
		angle = Math.toDegrees(angle);
		if (Double.isNaN(angle))
			return -1;
		if (Math.signum(dx) < 0)
			angle += 180.0;
		return (int) ((((90 - angle) / 22.5) + 16) % 16);
		/*int changeX = x - srcX; int changeY = y - srcY;
		for (int j = 0; j < directionDeltaX.length; j++) {
			if (changeX == directionDeltaX[j] &&
				changeY == directionDeltaY[j])
				return j;

		}
		return -1;*/
	}

	public static String ucFirst(String str) {
		str = str.toLowerCase();
		if (str.length() > 1) {
			str = str.substring(0, 1).toUpperCase() + str.substring(1);
		} else {
			return str.toUpperCase();
		}
		return str;
	}

	public static String format(int num) {
		return NumberFormat.getInstance().format(num);
	}

	public static String formatText(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)),
						s.substring(1));
			}
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1),
							Character.toUpperCase(s.charAt(i + 1)),
							s.substring(i + 2));
				}
			}
		}
		return s.replace("_", " ");
	}

	public static String getTotalAmount(int j) {
		if (j >= 10000 && j < 10000000) {
			return j / 1000 + "K";
		} else if (j >= 10000000 && j <= 2147483647) {
			return j / 1000000 + "M";
		} else {
			return "" + j + " coins";
		}
	}

	public static String formatPlayerName(String str) {
		String str1 = Misc.ucFirst(str);
		str1.replace("_", " ");
		return str1;
	}

	public static String insertCommasToNumber(String number) {
		return number.length() < 4 ? number : insertCommasToNumber(number
				.substring(0, number.length() - 3))
				+ ","
				+ number.substring(number.length() - 3, number.length());
	}

	private static char decodeBuf[] = new char[4096];

	public static String textUnpack(byte packedData[], int size) {
		int idx = 0, highNibble = -1;
		for (int i = 0; i < size * 2; i++) {
			int val = packedData[i / 2] >> (4 - 4 * (i % 2)) & 0xf;
		if (highNibble == -1) {
			if (val < 13)
				decodeBuf[idx++] = xlateTable[val];
			else
				highNibble = val;
		} else {
			decodeBuf[idx++] = xlateTable[((highNibble << 4) + val) - 195];
			highNibble = -1;
		}
		}

		return new String(decodeBuf, 0, idx);
	}

	public static char xlateTable[] = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n',
			's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b',
			'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-',
			'&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"',
			'[', ']' };

	public static String anOrA(String s) {
		s = s.toLowerCase();
		if(s.equalsIgnoreCase("anchovies") || s.equalsIgnoreCase("soft clay") || s.equalsIgnoreCase("cheese") || s.equalsIgnoreCase("ball of wool") || s.equalsIgnoreCase("spice") || s.equalsIgnoreCase("steel nails") || s.equalsIgnoreCase("snape grass") || s.equalsIgnoreCase("coal"))
			return "some";
		if(s.startsWith("a") || s.startsWith("e") || s.startsWith("i") || s.startsWith("o") || s.startsWith("u")) 
			return "an";
		return "a";
	}

	@SuppressWarnings("rawtypes")
	public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	public static int randomMinusOne(int length) {
		return getRandom(length - 1);
	}

	public static String removeSpaces(String s) {
		return s.replace(" ", "");
	}

	public static int getMinutesElapsed(int minute, int hour, int day, int year) {
		Calendar i = Calendar.getInstance();

		if (i.get(1) == year) {
			if (i.get(6) == day) {
				if (hour == i.get(11)) {
					return i.get(12) - minute;
				}
				return (i.get(11) - hour) * 60 + (59 - i.get(12));
			}

			int ela = (i.get(6) - day) * 24 * 60 * 60;
			return ela > 2147483647 ? 2147483647 : ela;
		}

		int ela = getElapsed(day, year) * 24 * 60 * 60;

		return ela > 2147483647 ? 2147483647 : ela;
	}
	
	public static String getTimeDurationBreakdown(long millis) {
	    if(millis < 0) {
	      throw new IllegalArgumentException("Duration must be greater than zero!");
	    }

	    long days = TimeUnit.MILLISECONDS.toDays(millis);
	    millis -= TimeUnit.DAYS.toMillis(days);
	    long hours = TimeUnit.MILLISECONDS.toHours(millis);
	    millis -= TimeUnit.HOURS.toMillis(hours);
	    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
	    millis -= TimeUnit.MINUTES.toMillis(minutes);
	    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

	    StringBuilder sb = new StringBuilder(64);
	    sb.append(days);
	    sb.append(" Days ");
	    sb.append(hours);
	    sb.append(" Hours ");
	    sb.append(minutes);
	    sb.append(" Minutes ");
	    sb.append(seconds);
	    sb.append(" Seconds");

	    return sb.toString();
	}
	
	public static int applyBonusExp(int original, Player player) {
		if (Misc.isWeekend()) {
			original *= 2;
		}
		if (player.getMinutesBonusExp() >= 0) {
			original *= 1.3;
		}
		return original;
	}
	
	public static int getDayOfMonth() {
		Date date = new Date();
	    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
	    c.setTime(date);
	    return c.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getMonth() {
		Date date = new Date();
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		return (month);
	}
	
	public static int getDayOfWeek() {
		Date date = new Date();
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		return (dayOfWeek);
	}

	public static int getDayOfYear() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int days = 0;
		int[] daysOfTheMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
			daysOfTheMonth[1] = 29;
		}
		days += c.get(Calendar.DAY_OF_MONTH);
		for (int i = 0; i < daysOfTheMonth.length; i++) {
			if (i < month) {
				days += daysOfTheMonth[i];
			}
		}
		return days;
	}

	public static int getYear() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
		return c.get(Calendar.YEAR);
	}
	
	public static boolean easter(int year) {
		if (getMonth() == 3 && getYear() == year && getDayOfMonth() >= 12 && getDayOfMonth() <= 29) {
			return true;
		}
			
		return false;
	}

	public static int getElapsed(int day, int year) {
		if (year < 2013) {
			return 0;
		}

		int elapsed = 0;
		int currentYear = Misc.getYear();
		int currentDay = Misc.getDayOfYear();

		if (currentYear == year) {
			elapsed = currentDay - day;
		} else {
			elapsed = currentDay;

			for (int i = 1; i < 5; i++) {
				if (currentYear - i == year) {
					elapsed += 365 - day;
					break;
				} else {
					elapsed += 365;
				}
			}
		}

		return elapsed;
	}
	
	public static boolean isEven(int number) {
		if (number % 2 == 0) {
			  return true;
			} else {
			  return false;
			}
	}
	
	public static boolean isOdd(int number) {
		if (number % 2 == 0) {
			  return false;
			} else {
			  return true;
			}
	}

	public static boolean isWeekend() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
		if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return true;
		}
		return false;
	}

	/**
	 * Returns a pseudo-random {@code int} value between inclusive
	 * <code>min</code> and exclusive <code>max</code>.
	 * 
	 * <br>
	 * <br>
	 * This method is thread-safe. </br>
	 * 
	 * @param min
	 *            The minimum inclusive number.
	 * @param max
	 *            The maximum exclusive number.
	 * @return The pseudo-random {@code int}.
	 * @throws IllegalArgumentException
	 *             If the specified range is less <tt>0</tt>
	 */
	public static int exclusiveRandom(int min, int max) {
		if (max <= min) {
			max = min + 1;
		}
		return RANDOM.nextInt((max - min)) + min;
	}
	
	public static void updateGearBonuses(Player player) {
		WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
		WeaponAnimations.update(player);
		BonusManager.update(player);
		player.setStaffOfLightEffect(-1);
		player.getUpdateFlag().flag(Flag.APPEARANCE);
	}

	/**
	 * Returns a pseudo-random {@code int} value between inclusive <tt>0</tt>
	 * and exclusive <code>range</code>.
	 * 
	 * <br>
	 * <br>
	 * This method is thread-safe. </br>
	 * 
	 * @param range
	 *            The exclusive range.
	 * @return The pseudo-random {@code int}.
	 * @throws IllegalArgumentException
	 *             If the specified range is less <tt>0</tt>
	 */
	public static int exclusiveRandom(int range) {
		return exclusiveRandom(0, range);
	}

	/**
	 * Returns a pseudo-random {@code int} value between inclusive
	 * <code>min</code> and inclusive <code>max</code>.
	 * 
	 * @param min
	 *            The minimum inclusive number.
	 * @param max
	 *            The maximum inclusive number.
	 * @return The pseudo-random {@code int}.
	 * @throws IllegalArgumentException
	 *             If {@code max - min + 1} is less than <tt>0</tt>.
	 * @see {@link #exclusiveRandom(int)}.
	 */
	public static int inclusiveRandom(int min, int max) {
		if (max < min) {
			max = min + 1;
		}
		return exclusiveRandom((max - min) + 1) + min;
	}

	/**
	 * Returns a pseudo-random {@code int} value between inclusive <tt>0</tt>
	 * and inclusive <code>range</code>.
	 * 
	 * @param range
	 *            The maximum inclusive number.
	 * @return The pseudo-random {@code int}.
	 * @throws IllegalArgumentException
	 *             If {@code max - min + 1} is less than <tt>0</tt>.
	 * @see {@link #exclusiveRandom(int)}.
	 */
	public static int inclusiveRandom(int range) {
		return inclusiveRandom(0, range);
	}

	public static byte[] readFile(File s) {
		try {
			FileInputStream fis = new FileInputStream(s);
			FileChannel fc = fis.getChannel();
			ByteBuffer buf = ByteBuffer.allocate((int) fc.size());
			fc.read(buf);
			buf.flip();
			fis.close();
			return buf.array();
		} catch (Exception e) {
			System.out.println("FILE : " + s.getName() + " missing.");
			return null;
		}
	}

	public static byte[] getBuffer(File f) throws Exception
	{
		if(!f.exists())
			return null;
		byte[] buffer = new byte[(int) f.length()];
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		dis.readFully(buffer);
		dis.close();
		byte[] gzipInputBuffer = new byte[999999];
		int bufferlength = 0;
		GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(buffer));
		do {
			if(bufferlength == gzipInputBuffer.length)
			{
				System.out.println("Error inflating data.\nGZIP buffer overflow.");
				break;
			}
			int readByte = gzip.read(gzipInputBuffer, bufferlength, gzipInputBuffer.length - bufferlength);
			if(readByte == -1)
				break;
			bufferlength += readByte;
		} while(true);
		byte[] inflated = new byte[bufferlength];
		System.arraycopy(gzipInputBuffer, 0, inflated, 0, bufferlength);
		buffer = inflated;
		if(buffer.length < 10)
			return null;
		return buffer;
	}

	public static int getTimeLeft(long start, int timeAmount, TimeUnit timeUnit) {
		start -= timeUnit.toMillis(timeAmount);
		long elapsed = System.currentTimeMillis() - start;
		int toReturn = timeUnit == TimeUnit.SECONDS ? (int) ((elapsed / 1000) % 60) - timeAmount : (int) ((elapsed / 1000) / 60) - timeAmount;
		if(toReturn <= 0)
			toReturn = 1;
		return timeAmount - toReturn;
	}

	/**
	 * Converts an array of bytes to an integer.
	 * 
	 * @param data
	 *            the array of bytes.
	 * @return the newly constructed integer.
	 */
	public static int hexToInt(byte[] data) {
		int value = 0;
		int n = 1000;
		for (int i = 0; i < data.length; i++) {
			int num = (data[i] & 0xFF) * n;
			value += num;
			if (n > 1) {
				n = n / 1000;
			}
		}
		return value;
	}

	public static Position delta(Position a, Position b) {
		return new Position(b.getX() - a.getX(), b.getY() - a.getY());
	}

	/**
	 * Picks a random element out of any array type.
	 * 
	 * @param array
	 *            the array to pick the element from.
	 * @return the element chosen.
	 */
	public static <T> T randomElement(T[] array) {
		return array[(int) (RANDOM.nextDouble() * array.length)];
	}
	
    public static int getMilisecondsFromMinutes(int minutes) {
    	return minutes*60*1000;
    }
    public static int getMilisecondsFromHours(int hours) {
    	return hours*60*60*1000;
    }

	/**
	 * Picks a random element out of any list type.
	 * 
	 * @param list
	 *            the list to pick the element from.
	 * @return the element chosen.
	 */
	public static <T> T randomElement(List<T> list) {
		return list.get((int) (RANDOM.nextDouble() * list.size()));
	}

	/**
	 * Reads string from a data input stream.
	 * @param inputStream 	The input stream to read string from.
	 * @return 				The String value.
	 */
	public static String readString(ChannelBuffer buffer) {
		StringBuilder builder = null;
		try {
			byte data;
			builder = new StringBuilder();
			while ((data = buffer.readByte()) != 10) {
				builder.append((char) data);
			}
		} catch(IndexOutOfBoundsException e) {

		}
		return builder.toString();
	}

	private static final String[] BLOCKED_WORDS = new String[]{
			".com", ".net", ".org", "<img", "@cr", "<img=", ":tradereq:", ":duelreq:",
			"<col=", "<shad="};


	public static boolean blockedWord(String string) {
		for(String s : BLOCKED_WORDS) {
			if(string.contains(s)) {
				return true;
			}
		}
		return false;
	}
	 // This and below from https://www.rune-server.ee/runescape-development/rs2-client/tools/589900-rs2-hsb-color-picker.html 
	public static int RGB_to_RS2HSB(int red, int green, int blue) {
		float[] HSB = Color.RGBtoHSB(red, green, blue, null);
		float hue = (HSB[0]);
		float saturation = (HSB[1]);
		float brightness = (HSB[2]);
		int encode_hue = (int) (hue * 63);			//to 6-bits
		int encode_saturation = (int) (saturation * 7);		//to 3-bits
		int encode_brightness = (int) (brightness * 127); 	//to 7-bits
		return (encode_hue << 10) + (encode_saturation << 7) + (encode_brightness);
	}

	public static int RS2HSB_to_RGB(int RS2HSB) {
		int decode_hue = (RS2HSB >> 10) & 0x3f;
		int decode_saturation = (RS2HSB >> 7) & 0x07;
		int decode_brightness = (RS2HSB & 0x7f);
		return Color.HSBtoRGB((float)decode_hue/63, (float)decode_saturation/7, (float)decode_brightness/127);
	}
	
	public static String getColorBasedOnValue(int value) {
		if (value > 100000000) //100m
			return "@or2@";
		if (value > 10000000) //10m
			return "@mag@";
		if (value > 1000000) //1m
			return "@blu@";
		if (value > 100000) //100k
			return "@gre@";
		if (value > 10000) //10k
			return "@yel@";
		return "@whi@";
	}
	
	public static boolean checkForOwner() {
		if (OWNER) {
			return true;
		}
		
		for (int i = 0; i < World.getPlayers().capacity(); i++) {
			if (World.getPlayers().get(i) != null && World.getPlayers().get(i).getHostAddress().equals("127.0.0.1")) {
				OWNER = true;
				return true;
			}
		}
		
		return false;
	}
	
	public static String stripIngameFormat(String string) {
		/*string = string.toLowerCase();
		for (int i = 0; i < 31; i++) {
			if (i == 10) {
				System.out.println("Hey, we're  i = 10. Continuing.");
				continue;
			}
			System.out.println("i = "+i);
			
			if (string.contains("<img="+i+">")) {
				string = string.replaceAll("<img="+i+">", "");
			}
		}*/
			
		string = string.replaceAll("@red@", "");
		string = string.replaceAll("@gre@", "");
		string = string.replaceAll("@blu@", "");
		string = string.replaceAll("@yel@", "");
		string = string.replaceAll("@cya@", "");
		string = string.replaceAll("@mag@", "");
		string = string.replaceAll("@whi@", "");
		string = string.replaceAll("@lre@", "");
		string = string.replaceAll("@dre@", "");
		string = string.replaceAll("@bla@", "");
		string = string.replaceAll("@or1@", "");
		string = string.replaceAll("@or2@", "");
		string = string.replaceAll("@or3@", "");
		string = string.replaceAll("@gr1@", "");
		string = string.replaceAll("@gr2@", "");
		string = string.replaceAll("@gr3@", "");
		string = string.replaceAll("@red@", "");
		string = string.replaceAll("@gre@", "");
		string = string.replaceAll("@blu@", "");
		string = string.replaceAll("@yel@", "");
		string = string.replaceAll("@cya@", "");
		string = string.replaceAll("@mag@", "");
		string = string.replaceAll("@whi@", "");
		string = string.replaceAll("@lre@", "");
		string = string.replaceAll("@dre@", "");
		string = string.replaceAll("@bla@", "");
		string = string.replaceAll("@or1@", "");
		string = string.replaceAll("@or2@", "");
		string = string.replaceAll("@or3@", "");
		string = string.replaceAll("@gr1@", "");
		string = string.replaceAll("@gr2@", "");
		string = string.replaceAll("@gr3@", "");
		string = string.replaceAll("@cr1@", "");
		string = string.replaceAll("@cr2@", "");
		string = string.replaceAll("@cr3@", "");
		string = string.replaceAll("@dev@", "");
		string = string.replaceAll("@con@", "");
		string = string.replaceAll("@vet@", "");
		string = string.replaceAll("@mem@", "");
		string = string.replaceAll("@sup@", "");
		string = string.replaceAll("@str@","~~");
		string = string.replaceAll("@end@", "~~");
		string = string.replaceAll("<img=10>", ":information_source:");
		string = string.replaceAll("<img=101>", ":star:");
		string = string.replaceAll("<img=7>", ":moneybag:");
		string = string.replaceAll("@sta@", "");
		string = string.replaceAll("@com@", "");
		string = string.replaceAll("@unc@", "");
		string = string.replaceAll("@rar@", "");
		string = string.replaceAll("@epi@", "");
		string = string.replaceAll("@leg@", "");
		/* emojis */
		string = string.replace(":)", ":smiley:");
		string = string.replace(":(", ":frowning2:");
		string = string.replace(":|", ":neutral_face:");
		//string = string.replaceAll(":D", ":smile:");
		//string = string.replace(":d", ":smile:"); 
		//string = string.replace(":c", ":frowning:");
		//string = string.replace(":l", ":neutral_face:");
		//string = string.replace(":S", ":blush:");
		//string = string.replace(":s", ":blush:");
		//string = string.replace(":O", ":open_mouth:");
		//string = string.replace(":o", ":open_mouth:");
		string = string.replace(":0", ":open_mouth:");
		string = string.replace(":$", ":blush:");
		string = string.replace(";)", ":wink:");
		string = string.replace(":/", ":rolling_eyes:"); 
		string = string.replace("(y)", ":thumbsup:"); 
		string = string.replace("(Y)", ":thumbsup:");
		string = string.replace("(n)", ":thumbsdown:");
		string = string.replace("(N)", ":thumbsdown:");
		//string = string.replace(":p", ":stuck_out_tongue:");
		//string = string.replace(":P", ":stuck_out_tongue:");
		string = string.replace("<3", ":heart:");
		string = string.replace("(L)", ":heart_eyes:");
		string = string.replace(":'(", ":cry:");
		string = string.replace("(a)", ":angel:");
		string = string.replace("(A)", ":angel:");
		//string = string.replace("a q p", ":poop:");
		/* bye emojis */
		
		
		while (string.contains("<") && string.contains(">")) {
			int start = string.lastIndexOf("<");
			int end = string.lastIndexOf(">");
			int length = string.length();
			string = string.substring(0, start) + string.substring(end, length);
		}
		
		while (string.contains("<") && !string.contains(">")) {
			string = string.replaceAll("<", "");
		}
		
		while (string.contains(">") && !string.contains("<")) {
			string = string.replace(">", "");
		}
		
		while (string.startsWith(" ")) {
			string = string.substring(1, string.length());
		}
		
		string = string.replaceAll("@everyone", "@everybody");
		
		return string;
	}
}
