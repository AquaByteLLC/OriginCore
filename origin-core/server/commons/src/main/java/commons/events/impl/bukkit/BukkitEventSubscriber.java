package commons.events.impl.bukkit;

import commons.events.api.EventContext;
import commons.events.api.PlayerEventContext;
import commons.events.api.Subscriber;
import commons.events.api.EventRegistry;
import commons.events.impl.EventSubscriber;
import org.bukkit.event.Event;

/**
 * @author vadim
 */
@Deprecated(forRemoval = true)
public class BukkitEventSubscriber<T extends Event> implements EventSubscriber {

	private final Subscriber<PlayerEventContext, T> subscriber;
	private final Class<T>      clazz;

	public BukkitEventSubscriber(Class<T> clazz, Subscriber<PlayerEventContext, T> subscriber) {
		this.clazz      = clazz;
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
