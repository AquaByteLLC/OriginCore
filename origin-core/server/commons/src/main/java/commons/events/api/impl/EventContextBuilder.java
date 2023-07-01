package commons.events.api.impl;

import commons.events.api.ContextBuilder;
import commons.events.api.EventContext;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
class EventContextBuilder implements ContextBuilder {

	private Player player = null;
	private boolean isCancelled = false; // explicitly define initial state

	@Override
	public ContextBuilder withPlayer(Player player) {
		this.player = player;
		return this;
	}

	@Override
	public ContextBuilder withInitialCancelledState(boolean isCancelled) {
		this.isCancelled = isCancelled;
		return this;
	}

	@Override
	public EventContext build() {
		EventContext context = new PlayerEventContext(player);
		context.setCancelled(isCancelled);
		return context;
	}

}
