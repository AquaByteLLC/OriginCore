package commons.events.api;

import org.bukkit.entity.Player;

/**
 * Builder for {@link EventContext}.
 */
public interface ContextBuilder {

	/**
	 * If this method remains uncalled, then {@link EventContext#hasPlayer()} will return {@code false}.
	 * @param player the {@linkplain EventContext#getPlayer() player} associated with events fired alongside the resulting {@link EventContext}
	 */
	ContextBuilder withPlayer(Player player);

	/**
	 * @param isCancelled the initial {@linkplain EventContext#isCancelled() cancelled state}
	 */
	ContextBuilder withInitialCancelledState(boolean isCancelled);

	/**
	 * @return the constructed {@link EventContext}
	 */
	EventContext build();

}
