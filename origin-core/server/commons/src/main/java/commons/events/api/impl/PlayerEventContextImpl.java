package commons.events.api.impl;

import commons.events.api.EventContext;
import commons.events.api.PlayerEventContext;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
class PlayerEventContextImpl extends EventContextImpl implements PlayerEventContext {

	private final Player player;

	PlayerEventContextImpl(Object event, Player player) {
		super(event);
		this.player = player;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

}
