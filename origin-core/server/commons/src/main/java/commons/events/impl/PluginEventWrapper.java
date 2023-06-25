package commons.events.impl;

import commons.ReflectUtil;
import commons.events.api.EventRegistry;
import commons.events.api.impl.PluginEventRegistry;
import commons.events.impl.bukkit.BukkitEventListener;
import commons.events.impl.packet.PacketEventListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vadim
 */
public class PluginEventWrapper {

	private final Plugin plugin;
	private final EventRegistry events;
	private final PacketEventListener packets;
	private final Map<Class<? extends Event>, BukkitEventListener<?>> bukkit = new HashMap<>();

	public PluginEventWrapper(Plugin plugin) {
		this.plugin = plugin;
		this.events = new PluginEventRegistry();
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
				bukkit.computeIfAbsent((Class<? extends Event>) event, e -> {
					BukkitEventListener<?> el = new BukkitEventListener<>(e);
					el.startListen(plugin, events);
					return el;
				});
				int len = ReflectUtil.getPublicMethodsByReturnType(event, Player.class).length;
				if (len < 1)
					ReflectUtil.serr("WARN: Bukkit event class " + event.getCanonicalName() + " does not involve a player!\n" +
									 "WARN: Functionality of methods expecting PlayerEventContext with this event is undefined.");
				if (len > 1)
					ReflectUtil.serr("WARN: Bukkit event class " + event.getCanonicalName() + " involves multiple players!\n" +
									 "WARN: Functionality of methods expecting PlayerEventContext with this event is undefined.");
			}
		});
		packets.startListen(plugin, events);
	}

	public void disable() {
		packets.ceaseListen();
		bukkit.values().forEach(EventListener::ceaseListen);
	}

}
