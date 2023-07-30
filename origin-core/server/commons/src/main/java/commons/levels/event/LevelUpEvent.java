package commons.levels.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LevelUpEvent extends Event {
	private final int level;
	private final Player player;
	private boolean isCancelled;
	@Getter
	private boolean calledFromEnchant;

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	@Getter private final String calling;

	public LevelUpEvent(String calling, Player player, int level, boolean calledFromEnchant) {
		this.level = level;
		this.player = player;
		this.calling = calling;
		this.calledFromEnchant = calledFromEnchant;
	}

	public Player getPlayer() {
		return player;
	}

	public int getLevel() {
		return level;
	}

	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

	@Override
	public boolean callEvent() {
		return super.callEvent();
	}
}
