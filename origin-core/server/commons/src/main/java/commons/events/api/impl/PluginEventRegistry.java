package commons.events.api.impl;

import commons.ReflectUtil;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import commons.events.api.Subscribe;
import commons.events.api.Subscriber;
import org.bukkit.event.EventException;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author vadim
 */
public class PlayerEventRegistry implements EventRegistry {

	private static class Subscription {

		WeakReference<Object>              listener;
		Set<Class<?>>                      events;
		Map<Class<?>, List<Subscriber<?>>> callbacks;

	}

	private final Map<Class<?>, List<Subscription>> subscriptions = new ConcurrentHashMap<>();

	private final List<Consumer<Class<?>>> subscriptionHooks = new ArrayList<>();

	@Override
	public void addSubscriptionHook(Consumer<Class<?>> onSubscribeEvent) {
		subscriptionHooks.add(onSubscribeEvent);
	}

	@Override
	public void subscribeAll(Object listener) {
		Map<Class<?>, List<Method>> methods = map(listener.getClass());

		WeakReference<Object> reference = new WeakReference<>(listener);

		// streams api is not fucking with this map
		Map<Class<?>, List<Subscriber<?>>> callbacks = new HashMap<>();
		for (Map.Entry<Class<?>, List<Method>> entry : methods.entrySet()) {
			List<Subscriber<?>> funcs = new ArrayList<>();
			for (Method method : entry.getValue())
				funcs.add(new MethodSubscriber(reference, method));
			callbacks.put(entry.getKey(), funcs);
		}

		Subscription subscription = new Subscription();
		subscription.listener  = reference;
		subscription.events    = Collections.unmodifiableSet(methods.keySet());
		subscription.callbacks = callbacks;

		for (Class<?> event : subscription.events)
			subscriptions.computeIfAbsent(event, x -> new ArrayList<>()).add(subscription);

		invokeSubscriptionHooks(subscription);
	}

	@Override
	public <T> void subscribeOne(Object listener, Subscriber<T> subscriber, Class<T> eventClass) {
		Subscription subscription = new Subscription();
		subscription.listener  = new WeakReference<>(listener);
		subscription.events    = Collections.singleton(eventClass);
		subscription.callbacks = Map.of(eventClass, Collections.singletonList(subscriber));

		subscriptions.computeIfAbsent(eventClass, x -> new ArrayList<>()).add(subscription);
		invokeSubscriptionHooks(subscription);
	}

	@Override
	public void unsubscribe(Object listener) {
		if (listener == null)
			throw new IllegalArgumentException("Null listener in #unsubscribe.");

		for (List<Subscription> list : subscriptions.values())
			list.removeIf(subscription -> subscription.listener.refersTo(listener));
	}

	@Override
	@SuppressWarnings("rawtypes,unchecked")
	@SneakyThrows
	public <T> EventContext publish(Player player, T event) {
		Class<?>     clazz   = event.getClass();
		EventContext context = new PlayerEventContext(player);

		List<Subscription> subs = subscriptions.get(clazz);
		if (subs != null) {
			subs.removeIf(sub -> sub.listener.refersTo(null));
			for (Subscription sub : subs)
				for (Subscriber subscriber : sub.callbacks.get(clazz))
					try {
						subscriber.process(context, event);
					} catch (Exception e) {
						ReflectUtil.serr("problem processing event " + event.getClass().getCanonicalName() + " in class " + sub.listener.get().getClass().getCanonicalName());
						throw new EventException(e);
					}
		}

		return context;
	}

	private Map<Class<?>, List<Method>> map(Class<?> target) {
		Map<Class<?>, List<Method>> methodsByEventType = new HashMap<>();
		for (Method method : target.getDeclaredMethods()) {
			Subscribe annotation = method.getAnnotation(Subscribe.class);
			if (annotation == null) continue;

			Class<?>[] params = method.getParameterTypes();
			if (params.length != 2) continue;
			if (!EventContext.class.isAssignableFrom(params[0])) continue;

			method.setAccessible(true);
			methodsByEventType.computeIfAbsent(params[1], x -> new ArrayList<>()).add(method);
		}
		return methodsByEventType;
	}

	private void invokeSubscriptionHooks(Subscription subscription){
		for (Consumer<Class<?>> hook : subscriptionHooks)
			for (Class<?> event : subscription.events)
				hook.accept(event);
	}

}