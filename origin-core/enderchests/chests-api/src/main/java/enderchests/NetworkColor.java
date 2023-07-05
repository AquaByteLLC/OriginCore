package enderchests;

import org.bukkit.ChatColor;
import org.bukkit.Color;

/**
 * @author vadim
 */
public enum NetworkColor {

	RED(ChatColor.DARK_RED),
	GOLD(ChatColor.GOLD),
	LIME(ChatColor.GREEN),
	AQUA(ChatColor.DARK_AQUA),
	PINK(ChatColor.LIGHT_PURPLE),
	PURPLE(ChatColor.DARK_PURPLE),
	WHITE(ChatColor.WHITE);

	public final ChatColor chatColor;

	NetworkColor(ChatColor chatColor) {
		this.chatColor = chatColor;
	}

	public java.awt.Color toColor() {
		return chatColor.asBungee().getColor();
	}

	public org.bukkit.Color toBukkit() {
		return org.bukkit.Color.fromRGB(toColor().getRed(), toColor().getGreen(), toColor().getBlue());
	}

}
