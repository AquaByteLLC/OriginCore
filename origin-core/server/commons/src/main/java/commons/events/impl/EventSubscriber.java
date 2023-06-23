package commons.events.impl;

import commons.events.api.EventRegistry;
import org.bukkit.plugin.Plugin;

/**
 * @author vadim
 */
public interface EventSubscriber {

	void bind(EventRegistry registry);

	void unbind(EventRegistry registry);

}
