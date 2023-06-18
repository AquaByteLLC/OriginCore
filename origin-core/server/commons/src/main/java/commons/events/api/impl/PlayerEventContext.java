package commons.events.api.impl;

import commons.events.api.EventContext;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
class PlayerEventContext implements EventContext {

	private final Player player;

	private boolean cancelled;

	PlayerEventContext(Player player) {
		this.player = player;
	}

	@Override
	public Player getPlayer() {
		return player;
	}


	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean toCancel) {
		this.cancelled = toCancel;
	}

}
