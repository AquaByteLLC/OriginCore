package commons.events.api;

/**
 * @author vadim
 */
@FunctionalInterface
public interface Subscriber<C extends EventContext,E> {

	void process(C context, E event) throws Exception;

}
