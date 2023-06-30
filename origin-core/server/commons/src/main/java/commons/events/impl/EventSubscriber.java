package commons.events.impl;

import commons.events.api.EventRegistry;

/**
 * @author vadim
 */
public interface EventSubscriber {

	void bind(EventRegistry registry);

	void unbind(EventRegistry registry);

}
