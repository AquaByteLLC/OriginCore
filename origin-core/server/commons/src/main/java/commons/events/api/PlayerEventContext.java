package commons.events.api;

import org.bukkit.entity.Player;

/**
 * @author vadim
 */
public interface PlayerEventContext extends EventContext {

	Player getPlayer();

}
