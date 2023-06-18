package commons.events.api;

import org.bukkit.entity.Player;

/**
 * @author vadim
 */
public interface EventContext {

	Player getPlayer();

	boolean isCancelled();

	void setCancelled(boolean toCancel);

}
