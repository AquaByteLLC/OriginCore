package commons.events.impl.bukkit;

import commons.ReflectUtil;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.impl.EventListener;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

/**
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
		if (events != null) {
			MethodHandle[] getters = ReflectUtil.getPublicMethodsByReturnType(event.getClass(), Player.class);
			if (getters.length >= 1) {
				EventContext context = events.publish((Player) getters[0].invoke(event), event);

				if (event instanceof Cancellable cancellable)
					if(context.isCancelled()) // todo: document this behavior
						cancellable.setCancelled(true);
			}
		}
	}

}
