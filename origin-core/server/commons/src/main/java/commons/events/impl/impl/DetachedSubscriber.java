package commons.events.impl.impl;

import commons.events.api.EventRegistry;
import commons.events.api.Subscriber;

/**
 * @author vadim
 */
public class DetachedSubscriber<T> implements commons.events.impl.EventSubscriber {

	private final Subscriber<T> subscriber;
	private final Class<T> clazz;

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
