package commons.events.impl.packet;

import commons.events.api.EventRegistry;
import commons.events.api.Subscriber;
import commons.events.impl.EventSubscriber;
import net.minecraft.network.protocol.Packet;
import org.bukkit.plugin.Plugin;

/**
 * @author vadim
 */
public class PacketEventSubscriber<T extends Packet<?>> implements EventSubscriber {

	private final Subscriber<T> subscriber;
	private final Class<T> clazz;

	public PacketEventSubscriber(Class<T> clazz, Subscriber<T> subscriber) {
		this.clazz = clazz;
		this.subscriber = subscriber;
	}

	@Override
	public void bind(Plugin plugin, EventRegistry registry) {
		registry.subscribeOne(this, subscriber, clazz);
	}

	@Override
	public void unbind(EventRegistry registry) {
		registry.unsubscribe(this);
	}

}
