package commons.entity.bukkit;

import commons.entity.EntityEvent;
import org.bukkit.event.Event;

import java.util.function.Consumer;

public class BukkitEntityEvent<T extends Event> extends EntityEvent<T> {
	public BukkitEntityEvent(Consumer<T> entityEventConsumer) {
		super(entityEventConsumer);
	}
}
