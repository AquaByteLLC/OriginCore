package tools.impl.sched;

import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import org.bukkit.Bukkit;
import tools.impl.ToolsPlugin;

import java.util.ArrayList;
import java.util.List;

public class CacheInvalidator<A> {
	private final List<ExpiringShelf<A>> expiringShelves;

	public CacheInvalidator() {
		this.expiringShelves = new ArrayList<>();
	}

	public void add(ExpiringShelf<A> shelf) {
		this.expiringShelves.add(shelf);
	}

	public void activate() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(ToolsPlugin.getPlugin(), () -> expiringShelves.forEach(ExpiringShelf::invalidate), 20, 0);
	}
}
