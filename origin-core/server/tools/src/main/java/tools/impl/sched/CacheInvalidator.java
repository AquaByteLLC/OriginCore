package tools.impl.sched;

import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import org.bukkit.Bukkit;
import tools.impl.ToolsPlugin;

import java.util.LinkedList;
import java.util.List;

public interface CacheInvalidator {
	List<ExpiringShelf<?>> SHELVES = new LinkedList<>();

	static void add(ExpiringShelf<?> shelf) {
		SHELVES.add(shelf);
	}

	static void init() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(ToolsPlugin.getPlugin(), () -> SHELVES.forEach(ExpiringShelf::invalidate), 0, 0);
	}

}
