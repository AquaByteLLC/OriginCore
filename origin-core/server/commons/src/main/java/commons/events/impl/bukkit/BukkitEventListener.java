package commons.events.impl.bukkit;

import commons.events.api.EventRegistry;
import commons.events.impl.EventListener;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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
	public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
		if (events != null) {
			if (event instanceof PlayerEvent playerEvent) {
				events.publish(playerEvent.getPlayer(), playerEvent);
			}
		}
	}

}
