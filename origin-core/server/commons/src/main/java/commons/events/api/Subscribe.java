package commons.events.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a candidate for {@link EventRegistry#subscribeAll(Object) event subscription}.
 * Subscriber methods must conform to one of the following schemas:
 * <ul>
 * <li>{@code void onEvent(Event event)}</li>
 * <li>{@code void onEvent(}{@link EventContext} {@code context, Event event)}</li>
 * </ul>
 * <p>Note that while the {@link EventContext} parameter is optional, it must be the <i>first</i> parameter.
 * <p>The event must be the only other parameter, positioned <i>last</i> (or by itself if {@link EventContext} is omitted).
 * <p>
 * <p>If a method fails to pass any of the checks required to become a subscription, then it will do so silently and without warning.
 * @author vadim
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

	// I do not care about EventPriority

}