package commons.events.impl.bukkit;

import commons.events.impl.EventListener;
import commons.events.api.EventRegistry;
import commons.events.impl.EventSubscriber;
import org.bukkit.RegionAccessor;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public class BukkitEventSubscriber<T extends Event> implements EventSubscriber {

	private final Consumer<T> consumer;
	private final Class<T> clazz;

	public BukkitEventSubscriber(Class<T> clazz, Consumer<T> consumer) {
		this.clazz = clazz;
		this.consumer = consumer;
	}

	@Override
	public void bind(EventRegistry registry) {
		registry.subscribeOne(this, (ctx, event) -> consumer.accept(event), clazz);
	}

	@Override
	public void unbind(EventRegistry registry) {
		registry.unsubscribe(this);
	}

}
