package commons.events.api.impl;

import commons.events.api.EventContext;
import commons.events.api.Subscriber;
import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * This class <b>only</b> supports method signatures matching:
 * <p>{@code void onEvent(EventContext context, Event event)}
 * <p>{@code void onEvent(Event event)}
 * @author vadim
 */
class MethodSubscriber implements Subscriber<EventContext, Object> {

	private final WeakReference<Object> listener;
	private final MethodHandle handle;
	private final boolean needsContext;

	@SneakyThrows
	MethodSubscriber(WeakReference<Object> listener, Method method) {
		this.listener = listener;
		this.handle = MethodHandles.lookup().unreflect(method);
		this.needsContext = method.getParameterCount() == 2; // it is up to the caller to verify the method parameters
	}

	@Override
	@SneakyThrows
	public void process(EventContext context, Object event) {
		if (!listener.refersTo(null))
			if (needsContext)
				handle.invoke(listener.get(), context, event);
			else
				handle.invoke(listener.get(), event);
	}

}