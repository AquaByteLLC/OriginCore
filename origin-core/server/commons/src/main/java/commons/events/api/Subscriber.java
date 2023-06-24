package commons.events.api;

/**
 * @author vadim
 */
@FunctionalInterface
public interface Subscriber<T> {

	void process(EventContext context, T event) throws NoSuchFieldException, IllegalAccessException;

}
