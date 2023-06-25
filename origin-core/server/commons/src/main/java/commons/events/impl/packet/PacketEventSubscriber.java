package commons.events.impl.packet;

import commons.events.api.EventRegistry;
import commons.events.api.PlayerEventContext;
import commons.events.api.Subscriber;
import commons.events.impl.EventSubscriber;
import net.minecraft.network.protocol.Packet;
import org.bukkit.plugin.Plugin;

/**
 * @author vadim
 */
@Deprecated(forRemoval = true)
public class PacketEventSubscriber<T extends Packet<?>> implements EventSubscriber {

	private final Subscriber<PlayerEventContext, T> subscriber;
	private final Class<T> clazz;

	public PacketEventSubscriber(Class<T> clazz, Subscriber<PlayerEventContext, T> subscriber) {
		this.clazz = clazz;
		this.subscriber = subscriber;
	}

	@Override
	public void bind(EventRegistry registry) {
		registry.subscribeOne(this, subscriber, clazz);
	}

	@Override
	public void unbind(EventRegistry registry) {
		registry.unsubscribe(this);
	}

}
