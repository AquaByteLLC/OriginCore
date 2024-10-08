package commons.util;

import com.google.common.base.Preconditions;
import commons.OriginModule;
import commons.interpolation.impl.InterpolationType;
import commons.math.RangeUntilKt;
import commons.math.RangesKt;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Arrays;
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
		String[]      words  = input.split("_");
		StringBuilder result = new StringBuilder();

		for (String word : words) {
			if (word.length() > 0) {
				String firstLetter = word.substring(0, 1);
				String restOfWord  = word.substring(1).toLowerCase();
				result.append(firstLetter).append(restOfWord).append(" ");
			}
		}

		return result.toString().trim();
	}

	private static final Pattern HEX = Pattern.compile("&#([A-Fa-f0-9]{6})");

	@SuppressWarnings("deprecation")
	public static String colorize(String string) {
		return string == null ? null : ChatColor.translateAlternateColorCodes('&', translateHexColorCodes(parseGradients(string)));
	}

	// todo
	public static String colorizeLegacy(String string) {
		return colorize(string);
	}

	private static String translateHexColorCodes(String message) {
		Matcher       matcher = HEX.matcher(message);
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

	private static int[] interpolate(Color color1, Color color2, InterpolationType type, int tick, int life) {
		final int blue = (int) RangesKt.interpolate(
				RangeUntilKt.rangeUntil((float) color1.getBlue(), (float) color2.getBlue()),
				RangesKt.coerceAtMost((float) tick / (float) life, 1f),
				type.getInterpolation());

		final int red = (int) RangesKt.interpolate(
				RangeUntilKt.rangeUntil((float) color1.getRed(), (float) color2.getRed()),
				RangesKt.coerceAtMost((float) tick / (float) life, 1f),
				type.getInterpolation());

		final int green = (int) RangesKt.interpolate(
				RangeUntilKt.rangeUntil((float) color1.getGreen(), (float) color2.getGreen()),
				RangesKt.coerceAtMost((float) tick / (float) life, 1f),
				type.getInterpolation());

		final int alpha = (int) RangesKt.interpolate(
				RangeUntilKt.rangeUntil((float) color1.getAlpha(), (float) color2.getAlpha()),
				RangesKt.coerceAtMost((float) tick / (float) life, 1f),
				type.getInterpolation());

		return new int[] { red, green, blue, alpha };
	}

	public static String interpolateColor(String msg, Color color1, Color color2, InterpolationType type) {
		final StringBuilder builder = new StringBuilder();
		int                 tick    = 0;
		int                 life    = msg.toCharArray().length;

		for (char c : msg.toCharArray()) {
			int[]  values = interpolate(color1, color2, type, tick++, life);
			String hex    = String.format("#%02x%02x%02x", values[0], values[1], values[2]);
			builder.append("&").append(hex).append(c);
		}

		return builder.toString();
	}

	public static String interpolateColors(String msg, Color[] colors, float[] portions, InterpolationType type) {
		final float[] p;
		if (portions == null) {
			p = new float[colors.length - 1];
			Arrays.fill(p, 1 / (float) p.length);
		} else {
			p = portions;
		}

		Preconditions.checkArgument(colors.length >= 2);
		Preconditions.checkArgument(p.length == colors.length - 1);

		final StringBuilder builder = new StringBuilder();

		int strIndex = 0;
		for (int i = 0; i < colors.length - 1; i++) {
			builder.append(interpolateColor(
					msg.substring(strIndex, strIndex + (int) Math.ceil(p[i] * msg.length())),
					colors[i],
					colors[i + 1],
					type));
			strIndex += (int) Math.ceil(p[i] * msg.length());
		}

		return builder.toString();
	}

	private static final Pattern STRIP_AMP = Pattern.compile("(?i)[&" + COLOR_CHAR + "][0-9A-FK-ORX]");
	private static final Pattern STRIP_HEX = Pattern.compile("(?i)&#([A-Fa-f0-9]{6})");

	public static String stripColor(String string) {
		if (string == null) return null;
		string = STRIP_HEX.matcher(string).replaceAll("");
		string = STRIP_AMP.matcher(string).replaceAll("");
		return string;
	}

	private static final Pattern GRADIENT = Pattern.compile("&#<(?<hex>(?:[0-9A-Fa-f]{6}:)*[0-9A-Fa-f]{6});(?<mode>[a-zA-Z]+)(?:\\((?<args>(?:[.\\d]+,)*[.\\d]+)\\))?>(?<str>.+?)(&r|&#|$)");

	public static String parseGradients(String message) {
		System.out.println();
		if (message == null) return null;
		int end = -1;

		StringBuilder builder = new StringBuilder();

		Matcher matcher = GRADIENT.matcher(message);
		while (matcher.find()) {
			String[]          hex  = matcher.group("hex").split(":");
			InterpolationType mode = InterpolationType.fromString(matcher.group("mode"));
			String            args = matcher.group("args");
			String            str  = matcher.group("str");

			Color[] colors = new Color[hex.length];
			for (int i = 0; i < hex.length; i++)
				 colors[i] = new Color(Integer.valueOf(hex[i], 16));

			float[] portions;
			if (args != null) {
				String[] split = args.split(",");
				portions = new float[split.length];

				for (int i = 0; i < split.length; i++)
					 portions[i] = Float.parseFloat(split[i]);
			} else
				portions = null;

			System.out.printf(">> %s %s(%s): %s%n", Arrays.toString(hex), mode, args, str);
			if (end > -1)
				builder.append(message, end, matcher.start());
			builder.append(StringUtil.interpolateColors(str, colors, portions, mode));
			end = matcher.end();
		}

		if (end > -1)
			builder.append(message, end, message.length());

		return builder.toString();
	}

	public static void send(CommandSender sender, String... messages) {
		for (String message : messages)
			sender.sendMessage(colorize(message));
	}

	public static String formatModuleName(OriginModule module) {
		return module.getName().split("-")[0];
	}

}
