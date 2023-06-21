package commons.events.impl;

import commons.ReflectUtil;
import commons.events.api.EventRegistry;
import commons.events.api.impl.PlayerEventRegistry;
import commons.events.impl.bukkit.BukkitEventListener;
import commons.events.impl.packet.PacketEventListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

/**
 * @author vadim
 */
public class PluginEventWrapper {

	private final Plugin              plugin;
	private final EventRegistry       events;
	private final PacketEventListener packets;

	public PluginEventWrapper(Plugin plugin) {
		this.plugin = plugin;
		this.events = new PlayerEventRegistry();
		packets     = new PacketEventListener();
	}

	public EventRegistry getEventRegistry() {
		return events;
	}

	@SuppressWarnings("unchecked")
	public void enable() {
		// special publishers for EventRegistry
		// the underlying impls work differently
		// - for Bukkit events you have to register it per event class
		// - for Packet events it's best to only register one Injector per plyaer
		events.addSubscriptionHook(event -> {
			if (org.bukkit.event.Event.class.isAssignableFrom(event)) {
				new BukkitEventListener<>((Class<Event>) event).startListen(plugin, events);
				int len = ReflectUtil.getPublicMethodsByReturnType(event, Player.class).length;
				if (len < 1)
					throw new IllegalArgumentException("Bukkit event class " + event.getCanonicalName() + " does not involve a player!");
				if (len > 1)
					ReflectUtil.serr("WARN: Bukkit event class " + event.getCanonicalName() + " involves multiple players! EventContext does not gurantee which player will be selected.");
			}
		});
		packets.startListen(plugin, events);
	}

	public void disable() {
		packets.ceaseListen();
	}

}
