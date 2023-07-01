package enderchests;

import org.bukkit.ChatColor;

/**
 * @author vadim
 */
public enum NetworkColor {

	RED(ChatColor.RED),
//	DARK_RED(ChatColor.DARK_RED),
	YELLOW(ChatColor.YELLOW),
//	DARK_YELLOW(ChatColor.GOLD),
	GREEN(ChatColor.GREEN),
//	DARK_GREEN(ChatColor.DARK_GREEN),
	AQUA(ChatColor.AQUA),
//	DARK_AQUA(ChatColor.DARK_AQUA),
	BLUE(ChatColor.BLUE),
//	DARK_BLUE(ChatColor.DARK_BLUE),
	PURPLE(ChatColor.LIGHT_PURPLE),
//	DARK_PURPLE(ChatColor.DARK_PURPLE),
	GRAY(ChatColor.GRAY),
//	DARK_GRAY(ChatColor.DARK_GRAY),
	WHITE(ChatColor.WHITE),
//	BLACK(ChatColor.BLACK),
	;
	public final ChatColor chatColor;

	NetworkColor(ChatColor chatColor) {
		this.chatColor = chatColor;
	}

}
