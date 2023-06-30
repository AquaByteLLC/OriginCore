package commons.events.api;

/**
 * Represents a callable event subscription.
 * @author vadim
 */
@FunctionalInterface
public interface Subscriber<T> {

	void process(EventContext context, T event) throws Exception;

}
