package commons.events.impl.impl;

import commons.events.api.ContextBuilder;
import commons.events.api.EventContext;
import commons.events.api.EventExecutionException;
import commons.events.api.EventRegistry;
import commons.events.impl.EventListener;
import commons.util.ReflectUtil;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

/**
 * Listener that {@linkplain EventRegistry#publish(EventContext, Object) publishes} Bukkit events.
 * @author vadim
 */
public class BukkitEventListener<T extends Event> implements EventListener, EventExecutor, Listener {

	private final Class<T> clazz;

	public BukkitEventListener(Class<T> clazz) {
		this.clazz = clazz;
	}

	private EventRegistry events;

	@Override
	public void startListen(Plugin plugin, EventRegistry events) {
		this.events = events;
		plugin.getServer().getPluginManager().registerEvent(clazz, this, EventPriority.NORMAL, this, plugin, false);
	}

	@Override
	public void ceaseListen() {
		HandlerList.unregisterAll(this);
		this.events = null;
	}

	@Override
	@SneakyThrows
	public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
		if(!clazz.isInstance(event)) return; // Paper impl has this =P
		try {
			if (events != null) {
				ContextBuilder builder = events.prepareContext();
				if (event instanceof Cancellable cancellable)
					builder.withInitialCancelledState(cancellable.isCancelled());

				MethodHandle[] getters = ReflectUtil.getPublicMethodsByReturnType(event.getClass(), Player.class); // this method is cached
				if (getters.length >= 1) // involves a player, let's try to get it
					builder.withPlayer((Player) getters[0].invoke(event));

				EventContext context = builder.build();
				events.publish(context, event);
//				System.out.println(event.getClass().getCanonicalName() + " was cancelled " );
				if (event instanceof Cancellable cancellable)
					cancellable.setCancelled(context.isCancelled());
			}
		} catch (EventExecutionException e) {
			ReflectUtil.serr("WARN: exception while processing event " + event.getClass().getCanonicalName());
			ReflectUtil.serr(e);
		} catch (Exception e) {
			ReflectUtil.serr("SEVERE: exception in event thread");
			ReflectUtil.serr(e);
			throw e;
		}
	}

}
