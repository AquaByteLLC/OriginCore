package commons.events.impl;

import commons.events.api.EventRegistry;
import org.bukkit.plugin.Plugin;

public interface EventListener {

	void startListen(Plugin plugin, EventRegistry events);

	void ceaseListen();

}
