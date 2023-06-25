package enderchests;

import org.bukkit.ChatColor;

/**
 * @author vadim
 */
public enum NetworkColor {

	RED(ChatColor.RED),
	DARK_RED(ChatColor.DARK_RED),
	YELLOW(ChatColor.YELLOW),
	DARK_YELLOW(ChatColor.GOLD),
	GREEN(ChatColor.GREEN),
	DARK_GREEN(ChatColor.DARK_GREEN),
	BLUE(ChatColor.BLUE),
	DARK_BLUE(ChatColor.DARK_BLUE),
	AQUA(ChatColor.AQUA),
	DARK_AQUA(ChatColor.DARK_AQUA),
	PURPLE(ChatColor.LIGHT_PURPLE),
	DARK_PURPLE(ChatColor.DARK_PURPLE),
	WHITE(ChatColor.WHITE),
	GRAY(ChatColor.GRAY),
	DARK_GRAY(ChatColor.DARK_GRAY),
	BLACK(ChatColor.BLACK);

	public final ChatColor chatColor;

	NetworkColor(ChatColor chatColor) {
		this.chatColor = chatColor;
	}

}
