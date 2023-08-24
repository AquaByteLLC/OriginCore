package commons.events.impl.impl;

import commons.events.api.EventRegistry;
import commons.events.api.Subscriber;
import commons.events.impl.EventSubscriber;

/**
 * Default {@link EventSubscriber} impl. Callers must control the lifecycle of this subscriber.
 *
 * @param <T>
 */
public class DetachedSubscriber<T> implements EventSubscriber {

	protected final Class<T> clazz;
	protected final Subscriber<T> subscriber;

	public DetachedSubscriber(Class<T> clazz, Subscriber<T> subscriber) {
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
