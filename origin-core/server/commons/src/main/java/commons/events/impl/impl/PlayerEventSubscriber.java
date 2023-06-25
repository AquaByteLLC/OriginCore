package commons.events.impl.impl;

import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.PlayerEventContext;
import commons.events.api.Subscriber;
import commons.events.impl.EventSubscriber;
import org.bukkit.event.Event;

/**
 * @author vadim
 */
public class GenericEventSubscriber<T> implements EventSubscriber {

	private final Subscriber<EventContext, T> subscriber;
	private final Class<T> clazz;

	public GenericEventSubscriber(Class<T> clazz, Subscriber<EventContext, T> subscriber) {
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
