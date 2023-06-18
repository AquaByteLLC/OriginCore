package commons.events.impl;

import commons.events.api.EventRegistry;
import org.bukkit.plugin.Plugin;

/**
 * @author vadim
 */
public interface EventSubscriber {

	void bind(Plugin plugin, EventRegistry registry);

	void unbind(EventRegistry registry);

}
