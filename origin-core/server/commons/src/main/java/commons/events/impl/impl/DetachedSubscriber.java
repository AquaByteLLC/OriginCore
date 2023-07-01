package commons.events.impl.impl;

import commons.events.api.EventRegistry;
import commons.events.api.Subscriber;
import commons.events.impl.EventSubscriber;

/**
 * @author vadim
 */
public class DetachedSubscriber<T> implements EventSubscriber {

	private final Class<T> clazz;
	private final Subscriber<T> subscriber;

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
