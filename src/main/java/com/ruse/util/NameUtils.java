package com.ruse.util;

/**
 * This utility file is used for name purposes, such as checking if
 * the name is a valid player name upon login and converting the string from a long
 * to a name and vice-versa.
 * 
 * @author relex lawl
 */

public class NameUtils {
	
	/**
	 * Capitalized all words split by a space char.
	 * @param name	The string to format.
	 */
	public static String capitalizeWords(String name) {
		StringBuilder builder = new StringBuilder(name.length());
		String[] words = name.split("\\s");
		for(int i = 0, l = words.length; i < l; ++i) {
			if(i > 0)
				builder.append(" ");      
			builder.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1));

		}
		return builder.toString();
	}
	
	/**
	 * Capitalizes the first letter in said string.
	 * @param name	The string to capitalize.
	 * @return		The string with the first char capitalized.
	 */
	public static String capitalize(String name) {
		if(name.length() < 1)
			return "";
		StringBuilder builder = new StringBuilder(name.length());
		char first = Character.toUpperCase(name.charAt(0));
		builder.append(first).append(name.toLowerCase().substring(1));
		return builder.toString();
	}
	
	/**
	 * Formats the name by checking if it starts with a vowel.
	 * @param name	The string to format.
	 */
	public static String getVowelFormat(String name) {
		char letter = name.charAt(0);
		boolean vowel = letter == 'a' || letter == 'e' || letter == 'i' || letter == 'o' || letter == 'u';
		String other = vowel ? "an" : "a";
		return other + " " + name;
	}
	
	/**
	 * Checks if a name is valid according to the {@code VALID_PLAYER_CHARACTERS} array.
	 * @param name	The name to check.
	 * @return 		The name is valid.
	 */
	public static boolean isValidName(String name) {
		return formatNameForProtocol(name).matches("[a-z0-9_]+");
	}

	/**
	 * Converts a name to a long value.
	 * @param string 	The string to convert to long.
	 * @return 			The long value of the string.
	 */
	public static long stringToLong(String string) {
		long l = 0L;
		for(int i = 0; i < string.length() && i < 12; i++) {
			char c = string.charAt(i);
			l *= 37L;
			if(c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
			else if(c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
			else if(c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}
		while(l % 37L == 0L && l != 0L)
			l /= 37L;
		return l;
	}
	
	/**
	 * Converts a long to a string.
	 * @param l 	The long value to convert to a string.
	 * @return 		The string value.
	 */
	public static String longToString(long l) {
		int i = 0;
		char ac[] = new char[12];
		while (l != 0L) {
			long l1 = l;
			l /= 37L;
			ac[11 - i++] = VALID_CHARACTERS[(int) (l1 - l * 37L)];
		}
		return new String(ac, 12 - i, i);
	}
	
	/**
	 * Formats a name for use in the protocol.
	 * @param name 	The name to format.
	 * @return 		The formatted name.
	 */
	public static String formatNameForProtocol(String name) {
		return name.toLowerCase().replace(" ", "_");
	}

	/**
	 * Formats a name for in-game display.
	 * @param name	The name to format.
	 * @return 		The formatted name.
	 */
	public static String formatName(String name) {
		return fixName(name.replace(" ", "_"));
	}
	
	/**
	 * Formats a player's name, i.e sets upper case letters after a space.
	 * @param name 	The name to format.
	 * @return 		The formatted name.
	 */
	private static String fixName(String name) {
		if(name.length() > 0) {
			final char ac[] = name.toCharArray();
			for(int j = 0; j < ac.length; j++)
				if(ac[j] == '_') {
					ac[j] = ' ';
					if((j + 1 < ac.length) && (ac[j + 1] >= 'a')
							&& (ac[j + 1] <= 'z')) {
						ac[j + 1] = (char) ((ac[j + 1] + 65) - 97);
					}
				}

			if((ac[0] >= 'a') && (ac[0] <= 'z')) {
				ac[0] = (char) ((ac[0] + 65) - 97);
			}
			return new String(ac);
		} else {
			return name;
		}
	}
	
	/**
	 * An array containing valid player name characters.
	 */
	public static final char VALID_PLAYER_CHARACTERS[] = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
		'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '[', ']', '/', '-', ' ' };
	
	/**
	 * An array containing valid characters that may be used on the server.
	 */
	public static final char VALID_CHARACTERS[] = { '_', 'a', 'b', 'c', 'd',
		'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
		'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
		'4', '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&',
		'*', '(', ')', '-', '+', '=', ':', ';', '.', '>', '<', ',', '"',
		'[', ']', '|', '?', '/', '`' 
	};
}
