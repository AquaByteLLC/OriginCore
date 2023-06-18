package commons.events.api.impl;

import commons.events.api.EventContext;
import commons.events.api.Subscriber;
import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * @author vadim
 */
class MethodSubscriber implements Subscriber<Object> {

	private final WeakReference<Object> listener;
	private final MethodHandle handle;

	@SneakyThrows
	MethodSubscriber(WeakReference<Object> listener, Method method) {
		this.listener = listener;
		this.handle = MethodHandles.lookup().unreflect(method);
	}

	@Override
	@SneakyThrows
	public void process(EventContext context, Object event) {
		if(!listener.refersTo(null))
			handle.invoke(listener.get(), context, event);
	}

}