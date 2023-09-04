package commons.util;

import commons.util.reflect.ReflectUtil;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

/**
 * Multi-version {@link DyeColor} and {@link ChatColor} link.
 *
 * @author vadim
 * @see ChatColor
 * @see net.md_5.bungee.api.ChatColor
 * @see DyeColor
 * @see Color
 */
public final class XColor {

	private static <T> T serverTooOld() {
		throw new UnsupportedOperationException("Your server does not support this method call.");
	}

	private static final Map<Color, XColor> BY_COLOR = new HashMap<>(); // place this above the other values so as not to throw exception in constructor

	public static final XColor BLACK = new XColor(DyeColor.BLACK, "BLACK", new Color(0x000000), new Color(0x1D1D21));
	public static final XColor DARK_BLUE = new XColor(DyeColor.BLUE, "DARK_BLUE", new Color(0x0000AA), new Color(0x3C44AA));
	public static final XColor DARK_GREEN = new XColor(DyeColor.GREEN, "DARK_GREEN", new Color(0x00AA00), new Color(0x5E7C16));
	public static final XColor DARK_AQUA = new XColor(DyeColor.CYAN, "DARK_AQUA", new Color(0x00AAAA), new Color(0x169C9C));
	public static final XColor DARK_PURPLE = new XColor(DyeColor.PURPLE, "DARK_PURPLE", new Color(0xAA00AA), new Color(0x8932B8));
	public static final XColor GOLD = new XColor(DyeColor.ORANGE, "GOLD", new Color(0xFFAA00), new Color(0xF9801D));
	public static final XColor GRAY = new XColor(ReflectUtil.getEnum(DyeColor.class, "LIGHT_GRAY", "SILVER"), "LIGHT_GRAY", new Color(0xAAAAAA), new Color(0x9D9D97), ChatColor.GRAY, "SILVER");
	public static final XColor DARK_GRAY = new XColor(DyeColor.GRAY, "DARK_GRAY", new Color(0x555555), new Color(0x474F52));
	public static final XColor BLUE = new XColor(DyeColor.BLUE, "BLUE", new Color(0x05555FF), new Color(0x3C44AA));
	public static final XColor GREEN = new XColor(DyeColor.LIME, "GREEN", new Color(0x55FF55), new Color(0x80C71F));
	public static final XColor AQUA = new XColor(DyeColor.LIGHT_BLUE, "AQUA", new Color(0x55FFFF), new Color(0x3AB3DA));
	public static final XColor RED = new XColor(DyeColor.RED, "RED", new Color(0xFF5555), new Color(0xB02E26));
	public static final XColor LIGHT_PURPLE = new XColor(DyeColor.MAGENTA, "LIGHT_PURPLE", new Color(0xFF55FF), new Color(0xC74EBD));
	public static final XColor YELLOW = new XColor(DyeColor.YELLOW, "YELLOW", new Color(0xFFFF55), new Color(0xFED83D));
	public static final XColor WHITE = new XColor(DyeColor.WHITE, "WHITE", new Color(0xFFFFFF), new Color(0xF9FFFE));
	public static final XColor BROWN = new XColor(DyeColor.BROWN, "BROWN", new Color(0x835432), ChatColor.GOLD);
	public static final XColor PINK = new XColor(DyeColor.PINK, "PINK", new Color(0xF38BAA), ChatColor.LIGHT_PURPLE);

	private static final XColor[] values = new XColor[] {
			BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, BROWN, PINK
	};

	private final DyeColor dyeColor;
	private final ChatColor chatColor;
	private final String legacyName;
	private final String name;
	private final Color dye;
	private final Color chat;

	private XColor(final Color color, final DyeColor dyeColor, final ChatColor chatColor) {
		this(dyeColor,
			 dyeColor.name().equalsIgnoreCase("PINK")
			 ? "PINK"
			 : dyeColor.name().equalsIgnoreCase("BROWN")
			   ? "BROWN"
			   : chatColor.name(), color, color, chatColor);
	}

	private XColor(final DyeColor dyeColor, final String name, final Color chat, final Color dye, final ChatColor chatColor) {
		this(dyeColor, name, chat, dye, chatColor, null);
	}

	private XColor(final DyeColor dyeColor, final String name, final Color chat, final Color dye, final ChatColor chatColor, final String legacyName) {
		this.dyeColor   = dyeColor;
		this.name       = name;
		this.chat       = chat;
		this.dye        = dye;
		this.chatColor  = chatColor != null
						  ? chatColor
						  : ReflectUtil.getEnum(ChatColor.class, this.name, "BLACK");
		this.legacyName = legacyName == null ? "" : legacyName;

		BY_COLOR.put(this.chat, this);
	}

	private XColor(final DyeColor dyeColor, final String name, final Color dye, final ChatColor chatColor) {
		this(dyeColor, name, dye, dye, chatColor);
	}

	private XColor(final DyeColor dyeColor, final String name, final Color chat, final Color dye) {
		this(dyeColor, name, chat, dye, null);
	}

	/**
	 * @return an array of all the default values.
	 */
	public static XColor[] values() {
		return values;
	}

	/**
	 * Returns an {@link XColor} from an {@link ChatColor}.
	 *
	 * @param chatColor the {@link ChatColor} to convert
	 *
	 * @return a corresponding {@link XColor}
	 */
	public static XColor fromChatColor(@NonNull final ChatColor chatColor) {
		for (final XColor x : values()) {
			if (x.chatColor == chatColor || x.legacyName.equals(chatColor.name())) {
				return x;
			}
		}

		try {
			if (!chatColor.isFormat()) {
				return fromColor(chatColor.asBungee().getColor());
			}

			throw new IllegalStateException(chatColor.name());
		} catch (NoSuchMethodError e) {
			return serverTooOld();
		}
	}

	/**
	 * Creates an {@link XColor} instance with {@code color} as its color and finds the closest {@link ChatColor} and
	 * {@link DyeColor}.
	 *
	 * @param color the {@link Color} to convert
	 *
	 * @return the corresponding {@link XColor}
	 */
	public static XColor fromColor(@NonNull final Color color) {

		XColor xColor = BY_COLOR.get(color);

		if (xColor != null) {//return cached value
			System.out.println("cached");
			return xColor;
		}

		double diff = Double.MAX_VALUE;
		xColor = XColor.BLACK;
		for (final XColor x : values()) {//find the nearest xColor to get dye and chat colors from
			final double d = min(min(difference(x.dye, color), difference(x.chat, color)), diff);
			if (d < diff) {
				diff   = d;
				xColor = x;
			}
		}

		return new XColor(color, xColor.getDyeColor(), xColor.getChatColor());
	}

	/**
	 * Returns an {@link XColor} from a {@link org.bukkit.Color}.
	 *
	 * @param color the {@link org.bukkit.Color} to convert
	 *
	 * @return the corresponding {@link XColor}
	 */
	public static XColor fromBukkitColor(final org.bukkit.Color color) {
		return fromColor(convertBukkitColor(color));
	}

	/**
	 * Returns an {@link XColor} from an {@link net.md_5.bungee.api.ChatColor}.
	 *
	 * @param chatColor the {@link net.md_5.bungee.api.ChatColor} to convert
	 *
	 * @return a corresponding {@link XColor}
	 */
	public static XColor fromBungee(@NonNull final net.md_5.bungee.api.ChatColor chatColor) {
		for (final XColor x : values()) {
			if (x.getBungee() == chatColor || x.legacyName.equals(chatColor.name())) {
				return x;
			}
		}
		try {
			return fromColor(chatColor.getColor());
		} catch (NoSuchMethodError e) {
			return serverTooOld();
		}
	}

	/**
	 * Returns an {@link XColor} from an {@link DyeColor}.
	 *
	 * @param dyeColor the {@link DyeColor} to convert
	 *
	 * @return the corresponding {@link XColor}
	 */
	public static XColor fromDyeColor(@NonNull final DyeColor dyeColor) {
		for (final XColor x : values()) {
			if (x.dyeColor == dyeColor || x.legacyName.equals(dyeColor.name())) {
				return x;
			}
		}

		try {
			return fromColor(convertBukkitColor(dyeColor.getColor()));
		} catch (NoSuchMethodError e) {
			return serverTooOld();
		}
	}

	/**
	 * Attempts to parse a string and return the corresponding {@link XColor}.
	 *
	 * @param name the string to parse
	 *
	 * @return the corresponding {@link XColor} or {@code null}
	 */
	public static XColor fromName(final String name) {
		if (name == null) { return null; }
		for (final XColor x : values()) {
			if (x.name().equalsIgnoreCase(name) ||
				x.chatColor.name().equalsIgnoreCase(name) ||
				x.dyeColor.name().equalsIgnoreCase(name) ||
				x.legacyName.equalsIgnoreCase(name)) {

				return x;
			}
		}

		return null;
	}

	/**
	 * Compares two colors and returns the {@link Color} {@code toCompare} is closer to.
	 *
	 * @param some      the first option
	 * @param other     the other option
	 * @param toCompare the color to compare
	 *
	 * @return {@code some} if the {@link XColor#difference(Color, Color) difference} between {@code some} and {@code toCompare} is less than or equal to the
	 *        {@link XColor#difference(Color, Color) difference} between {@code other} and {@code toCompare}, or if {@code some} and {@code other} are equal; otherwise
	 *        {@code other} is returned
	 *
	 * @see XColor#difference(Color, Color)
	 */
	public static Color compare(@NonNull final Color some, @NonNull final Color other, @NonNull final Color toCompare) {
		if (some.equals(other)) {
			return some;
		}

		return difference(some, toCompare) <= difference(other, toCompare) ? some : other;
	}

	/**
	 * Returns the difference in RGBA color space between the two colors.
	 *
	 * @param some  one color
	 * @param other another color
	 *
	 * @return the difference between the two colors
	 */
	public static double difference(@NonNull final Color some, @NonNull final Color other) {
		final int r = some.getRed() - other.getRed();
		final int g = some.getGreen() - other.getGreen();
		final int b = some.getBlue() - other.getBlue();
		final int a = some.getAlpha() - other.getAlpha();

		return max(pow(r, 2), pow(r - a, 2)) +
			   max(pow(g, 2), pow(g - a, 2)) +
			   max(pow(b, 2), pow(b - a, 2));
	}

	/**
	 * Returns an {@link Color} from {@link org.bukkit.Color Bukkit's color representation}.
	 *
	 * @param color the {@link org.bukkit.Color bukkit color}
	 *
	 * @return the {@link Color}
	 */
	public static Color convertBukkitColor(final org.bukkit.Color color) {
		return new Color(color.asRGB());
	}

	/**
	 * @return the {@link ChatColor} representation of the color
	 */
	public ChatColor getChatColor() { return this.chatColor; }

	/**
	 * @return the {@link net.md_5.bungee.api.ChatColor bungee ChatColor} representation of the color
	 */
	public net.md_5.bungee.api.ChatColor getBungee() {
		try {
			return net.md_5.bungee.api.ChatColor.of(getColor());
		} catch (NoSuchMethodError e) {
			return this.chatColor.asBungee();
		}
	}

	/**
	 * @return the {@link DyeColor} representation of the color
	 */
	public DyeColor getDyeColor() { return this.dyeColor; }

	/**
	 * @return the {@link Color} representation of the color
	 */
	public Color getColor() { return this.chat; }//return chat_color as default; if only dye_color was provided then they're the same

	/**
	 * @return the {@link org.bukkit.Color Bukkit's  representation of the color}
	 */
	public org.bukkit.Color getBukkitColor() { return org.bukkit.Color.fromRGB(getColor().getRed(), getColor().getGreen(), getColor().getBlue()); }

	/**
	 * Returns the name of the color.
	 *
	 * @return the name of the color
	 *
	 * @deprecated here from migration from enum
	 */
	@Deprecated
	public String name() {
		return this.name;
	}

	/**
	 * toString overridden for ease of concatenation within colored messages
	 *
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return this.chatColor.toString();
	}

}