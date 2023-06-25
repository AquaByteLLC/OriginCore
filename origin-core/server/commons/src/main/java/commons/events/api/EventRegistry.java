package commons.events.api;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public interface EventRegistry {

	void addSubscriptionHook(Consumer<Class<?>> onSubscribeEvent);

	void subscribeAll(Object listener);

	<C extends EventContext, E> void subscribeOne(Object listener, Subscriber<C, E> subscriber, Class<E> eventClass);

	void unsubscribe(Object listener);

	<T> EventContext publish(T event);

	<T> PlayerEventContext publish(Player player, T event);

}