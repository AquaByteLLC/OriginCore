package commons;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author vadim
 */
public class StringUtil {

	public static String convertToUserFriendlyCase(String input) {
		String[] words = input.split("_");
		StringBuilder result = new StringBuilder();

		for (String word : words) {
			if (word.length() > 0) {
				String firstLetter = word.substring(0, 1);
				String restOfWord = word.substring(1).toLowerCase();
				result.append(firstLetter).append(restOfWord).append(" ");
			}
		}

		return result.toString().trim();
	}


	@SuppressWarnings("deprecation")
	public static String colorize(String string) {
		return string == null ? null : ChatColor.translateAlternateColorCodes('&', string);
	}

	public static void send(CommandSender sender, String... messages){
		for (String message : messages)
			sender.sendMessage(colorize(message));
	}

}
