package commons.events.api;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public interface EventRegistry {

	void addSubscriptionHook(Consumer<Class<?>> onSubscribeEvent);

	void subscribeAll(Object listener);

	<T> void subscribeOne(Object listener, Subscriber<T> subscriber, Class<T> eventClass);

	void unsubscribe(Object listener);

	<T> EventContext publish(Player player, T event);

}