package commons.events.api.impl;

import commons.events.api.EventContext;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
class EventContextImpl implements EventContext {

	private Object event;

	EventContextImpl(Object event) {
		this.event = event;
	}

	@Override
	public Object getEvent() {
		return event;
	}

	@Override
	public void mutate(Object event) {
		this.event = event;
	}

	private boolean cancelled;

	@Override
	public boolean isCancelled() {
		return cancelled || event == null;
	}

	@Override
	public void setCancelled(boolean toCancel) {
		this.cancelled = toCancel;
	}

}
