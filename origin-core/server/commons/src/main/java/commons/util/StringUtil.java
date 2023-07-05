package commons.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

/**
 * @author vadim
 */
public class StringUtil {

	public static String formatNumber(Number number) {
		return NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT).format(number);
	}

	// stupidass autoboxing doesn't work

	public static String formatNumber(byte num) {
		return formatNumber(Byte.valueOf(num));
	}

	public static String formatNumber(short num) {
		return formatNumber(Short.valueOf(num));
	}

	public static String formatNumber(int num) {
		return formatNumber(Integer.valueOf(num));
	}

	public static String formatNumber(long num) {
		return formatNumber(Long.valueOf(num));
	}

	public static String formatNumber(float num) {
		return formatNumber(Float.valueOf(num));
	}

	public static String formatNumber(double num) {
		return formatNumber(Double.valueOf(num));
	}

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
		return string == null ? null : ChatColor.translateAlternateColorCodes('&', translateHexColorCodes("&#", "", string));
	}

	private static String translateHexColorCodes(String startTag, String endTag, String message) {
		final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
		Matcher matcher = hexPattern.matcher(message);
		StringBuilder builder = new StringBuilder(message.length() + 4 * 8);
		while (matcher.find()) {
			String group = matcher.group(1);
			matcher.appendReplacement(builder, COLOR_CHAR + "x"
					+ COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
					+ COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
					+ COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
			);
		}
		return matcher.appendTail(builder).toString();
	}

	public static void send(CommandSender sender, String... messages) {
		for (String message : messages)
			sender.sendMessage(colorize(message));
	}

}
