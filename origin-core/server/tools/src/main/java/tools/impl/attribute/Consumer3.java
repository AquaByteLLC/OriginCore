package tools.impl.attribute;

import commons.events.api.EventContext;

public interface Consumer3<T> {
	void consume(AttributeKey key, EventContext context, T event);
}
