package commons.events.api.impl;

import commons.events.api.EventContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

class PlayerEventContext implements EventContext {

	private final @Nullable Player player;

	PlayerEventContext(@Nullable Player player) {
		this.player = player;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean hasPlayer() {
		return player != null;
	}

	private boolean cancelled;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean toCancel) {
		this.cancelled = toCancel;
	}

}
