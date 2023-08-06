package commons.events.impl;

import commons.events.api.EventRegistry;
import commons.events.api.Subscriber;

/**
 * Represents a detached subscriber, designed for inline use of {@link EventRegistry#subscribeOne(Object, Subscriber, Class)}
 * without the need for {@linkplain commons.events.api.Subscribe subscriber candidate} methods.
 * <p><b>NOTE</b>: retain a <i>strong reference</i> (i.e. storing this instance in a field) to prevent it from being garbage collected and thereby being auto-unsubscribed
 * @see EventRegistry#subscribeOne(Object, Subscriber, Class)
 */
public interface EventSubscriber {

	/**
	 * Begin listening for events.
	 * @param registry the {@link EventRegistry} to subscribe on
	 */
	void bind(EventRegistry registry);

	/**
	 * Cease listening for events.
	 * @param registry the {@link EventRegistry} that was subscribed on
	 */
	void unbind(EventRegistry registry);

}
