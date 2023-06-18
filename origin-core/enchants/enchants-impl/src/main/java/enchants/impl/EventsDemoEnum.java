package enchants.impl;

import commons.events.api.EventRegistry;
import commons.events.impl.EventSubscriber;
import commons.events.impl.bukkit.BukkitEventSubscriber;
import commons.events.impl.packet.PacketEventSubscriber;
import net.minecraft.network.protocol.game.PacketPlayInHeldItemSlot;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

/**
 * @author vadim
 */
public enum EventsDemoEnum {

	ORIGIN_ENCHANT_TYPE_BUKKIT(new BukkitEventSubscriber<>(PlayerQuitEvent.class, (event) -> {
		System.out.println(event.getPlayer().getName() + " has left");
	})),
	ORIGIN_ENCHANT_TYPE_PACKET(new PacketEventSubscriber<>(PacketPlayInHeldItemSlot.class, (ctx, packet) -> {
		System.out.println(ctx.getPlayer().getName() + " has scrolled to slot " + packet.a());
	}));

	private final EventSubscriber subscriber;

	EventsDemoEnum(EventSubscriber subscriber) {
		this.subscriber = subscriber;

		// EventSubscriber has an unbind method
		// EventRegistry has an unsubscribe method
		// if you ever need to stop listening
	}

	public static void bind(Plugin plugin, EventRegistry events) {
		for (EventsDemoEnum value : values())
			value.subscriber.bind(plugin, events);
	}

}
