package commons.events.api;

import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * An event registry handles the subscription to and publication of unspecified event objects.
 * @author vadim
 */
public interface EventRegistry {

	/**
	 * Add a subscription hook that fires every time an event class is subscribed to.
	 * <p>This method has no value to users outside of the implementing library.
	 * @param onSubscribeEvent the callback accepting the class that has been subscribed to
	 */
	@ApiStatus.Internal
	void addSubscriptionHook(Consumer<Class<?>> onSubscribeEvent);

	/**
	 * Attempt to register all {@link Subscribe subscriber candidates} inside of a class.
	 * <p>Note that methods must be <i>directly declared or overridden</i> in the {@code listener}'s class
	 * @param listener the listener object housing {@link Subscribe subscriber candidates}
	 */
	void subscribeAll(Object listener);

	/**
	 * Registers a detached subscriber.
	 *
	 * <p><b>VERY IMPORTANT:</b>
	 * The {@linkplain EventRegistry event registry} will hold a {@link java.lang.ref.WeakReference} to the {@code listener} object, and automatically
	 * {@link #unsubscribe(Object) unsubscribe} the {@code listener} when it becomes unavailable. Users must hold <i>at least</i> a
	 * <a href="https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/package-summary.html#reachability">soft reference</a>
	 * to the {@code listener} to prevent it from being garbage collected, however, maintaining a
	 * <a href="https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/package-summary.html#reachability">strong reference</a>
	 * is advised (due to simplicity).
	 *
	 * <p><b>TL;DR</b>: retain a <i>strong reference</i> (i.e. storing your {@code listener} in a field) to prevent your {@code listener} from being garbage collected
	 * @param listener the listener object in question
	 * @param subscriber the (presumably) detached {@linkplain Subscriber subscriber callable}
	 * @param eventClass the event class being registered
	 * @param <T> the event's type
	 */
	<T> void subscribeOne(Object listener, Subscriber<T> subscriber, Class<?> eventClass);

	/**
	 * Unsubscribe all subscribers registered with the {@code listener}.
	 * Unsubscribed listeners will no longer receive {@linkplain #publish(Object) published} {@linkplain #publish(Player, Object) events}.
	 * @param listener the listener object in question
	 */
	void unsubscribe(Object listener);

	/**
	 * Publishes an event, firing all registered subscribers.
	 * @param event the event to be fired
	 * @return the resulting {@link EventContext}, as processed by the subscribers
	 * @param <T> the event's type
	 * @throws EventExecutionException if any exception occurs during the firing of any of the subscribers
	 */
	<T> EventContext publish(T event) throws EventExecutionException;

	/**
	 * Publishes a {@linkplain EventContext#getPlayer() player event}, firing all registered subscribers.
	 * @param event the event to be fired
	 * @return the resulting {@link EventContext}, as processed by the subscribers
	 * @param <T> the event's type
	 * @throws EventExecutionException if any exception occurs during the firing of any of the subscribers
	 */
	<T> EventContext publish(Player player, T event) throws EventExecutionException;

}