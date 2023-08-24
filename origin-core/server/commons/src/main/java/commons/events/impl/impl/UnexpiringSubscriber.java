package commons.events.impl.impl;

import commons.events.api.EventRegistry;
import commons.events.api.Subscriber;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Extended impl of {@link commons.events.impl.impl.DetachedSubscriber} which manages the lifecycle of events internally.
 * <p>
 * <strong>NOTICE</strong>: use of this class is discouraged as it indicates a design flaw, however it is included here to support those who are less mentally capable.
 *
 * @author vadim
 */
@ApiStatus.Experimental
public class UnexpiringSubscriber<T> extends DetachedSubscriber<T> {

	private static final List<UnexpiringSubscriber<?>> GLOBAL = new ArrayList<>(100);

	public UnexpiringSubscriber(Class<T> clazz, Subscriber<T> subscriber) {
		super(clazz, subscriber);
	}


	@Override
	public void bind(EventRegistry registry) {
		synchronized (GLOBAL) {
			GLOBAL.add(this);
		}
		super.bind(registry);
	}

	@Override
	public void unbind(EventRegistry registry) {
		synchronized (GLOBAL) {
			GLOBAL.remove(this);
		}
		super.unbind(registry);
	}

	/**
	 * Unbinds all currently managed {@link commons.events.impl.EventSubscriber subscribers}.
	 */
	@Deprecated
	public static void flush(EventRegistry registry) {
		synchronized (GLOBAL) {
			GLOBAL.forEach(it -> it.unbind(registry));
			GLOBAL.clear();
		}
	}

}
