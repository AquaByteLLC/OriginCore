package commons.events.api.impl;

import commons.events.api.*;
import commons.util.ReflectUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class PluginEventRegistry implements EventRegistry {

	private static class Subscription {

		WeakReference<Object> listener;
		Set<Class<?>> events;
		Map<Class<?>, List<Subscriber<?>>> callbacks;

	}

	private final Map<Class<?>, List<Subscription>> subscriptions = new ConcurrentHashMap<>();

	private final List<Consumer<Class<?>>> subscriptionHooks = new CopyOnWriteArrayList<>();

	@Override
	public void addSubscriptionHook(Consumer<Class<?>> onSubscribeEvent) {
		if(onSubscribeEvent == null) throw new IllegalArgumentException("Callback may not be null.");
		subscriptionHooks.add(onSubscribeEvent);
	}

	@Override
	public void subscribeAll(Object listener) {
		if (listener == null) throw new IllegalArgumentException("Listener may not be null.");
		unsubscribe(listener);

		Map<Class<?>, List<Method>> methodsByEventType = new HashMap<>();
		for (Method method : listener.getClass().getDeclaredMethods()) {
			Subscribe annotation = method.getAnnotation(Subscribe.class);
			if (annotation == null) continue;

			// methods should be one of:
			//void onEvent(EventContext context, Event event)
			//void onEvent(Event event)
			Class<?>[] params = method.getParameterTypes();
			if (params.length > 2) continue;
			if (params.length == 2 && !EventContext.class.isAssignableFrom(params[0])) continue;

			method.setAccessible(true);
			methodsByEventType.computeIfAbsent(params[params.length == 1 ? 0 : 1], x -> new ArrayList<>()).add(method);
		}

		// streams api is not fucking with this map
		Map<Class<?>, List<Subscriber<?>>> callbacks = new HashMap<>();
		for (Map.Entry<Class<?>, List<Method>> entry : methodsByEventType.entrySet()) {
			List<Subscriber<?>> funcs = new ArrayList<>();
			for (Method method : entry.getValue())
				funcs.add(new MethodSubscriber(listener, method));
			callbacks.put(entry.getKey(), funcs);
		}

		Subscription subscription = new Subscription();
		subscription.listener  = new WeakReference<>(listener);
		subscription.events    = Collections.unmodifiableSet(methodsByEventType.keySet());
		subscription.callbacks = Collections.unmodifiableMap(callbacks);

		for (Class<?> event : subscription.events)
			getSubscriptions(event).add(subscription);

		invokeSubscriptionHooks(subscription);
	}

	@Override
	public <T> void subscribeOne(Object listener, Subscriber<T> subscriber, Class<?> eventClass) {
		if (listener == null) throw new IllegalArgumentException("Listener may not be null.");
		if (subscriber == null) throw new IllegalArgumentException("Subscriber may not be null.");
		if (eventClass == null) throw new IllegalArgumentException("Event class may not be null.");

		Subscription subscription = new Subscription();
		subscription.listener  = new WeakReference<>(listener);
		subscription.events    = Collections.singleton(eventClass);
		subscription.callbacks = Map.of(eventClass, Collections.singletonList(subscriber));

		getSubscriptions(eventClass).add(subscription);
		invokeSubscriptionHooks(subscription);
	}

	@Override
	public void unsubscribe(Object listener) {
		if (listener == null) throw new IllegalArgumentException("Listener may not be null.");

		for (List<Subscription> list : subscriptions.values())
			list.removeIf(subscription -> subscription.listener.refersTo(listener));
	}

	@Override
	@SuppressWarnings("rawtypes,unchecked")
	public <T> void publish(EventContext context, T event) throws EventExecutionException {
		if (context == null) throw new IllegalArgumentException("Context may not be null.");
		if (event == null) throw new IllegalArgumentException("Event may not be null.");

		Class<?>           clazz = event.getClass();
		List<Subscription> subs  = subscriptions.get(clazz);
		if (subs != null) {
			subs.removeIf(sub -> sub.listener.refersTo(null));
			for (Subscription sub : subs)
				for (Subscriber subscriber : sub.callbacks.get(clazz))
					try {
						subscriber.process(context, event);
					} catch (Throwable t) {
						Object listener = sub.listener.get();
						ReflectUtil.serr("problem processing event " + event.getClass().getCanonicalName() +
										 " in class " + (listener == null ? "<garbage collected>" : listener.getClass().getCanonicalName()));
						throw new EventExecutionException(t);
					}
		}
	}

	@Override
	public ContextBuilder prepareContext() {
		return new EventContextBuilder();
	}

	private void invokeSubscriptionHooks(Subscription subscription) {
		for (Consumer<Class<?>> hook : subscriptionHooks)
			for (Class<?> event : subscription.events)
				hook.accept(event);
	}

	private List<Subscription> getSubscriptions(Class<?> eventClass) {
		return subscriptions.computeIfAbsent(eventClass, x -> new CopyOnWriteArrayList<>());
	}

}