package commons.util;

import commons.CommonsPlugin;
import org.bukkit.entity.Player;

public class BungeeUtils {
	public static void send(Player player, String server) {
		CommonsPlugin.commons().getBungeeCord().connect(player, server);
	}

	public static void sendToFallback(Player player, String fallbackServer) {
		send(player, fallbackServer);
	}
}
