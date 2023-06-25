package commons.events.impl.impl;

import commons.events.api.PlayerEventContext;
import commons.events.api.Subscriber;

/**
 * @author vadim
 */
public class PlayerEventSubscriber<T> extends GenericEventSubscriber<T> {

	public PlayerEventSubscriber(Class<T> clazz, Subscriber<PlayerEventContext, T> subscriber) {
		super(clazz, subscriber);
	}

}
