package blocks.impl.events;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BreakEvent extends Event {
	private final Block block;
	private final Player player;
	private boolean isCancelled;
	@Getter private boolean calledFromEnchant;

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	@Getter private final String calling;

	public BreakEvent(String calling, Block block, Player player, boolean calledFromEnchant) {
		this.block = block;
		this.player = player;
		this.calling = calling;
		this.calledFromEnchant = calledFromEnchant;
	}

	public Player getPlayer() {
		return player;
	}

	public Block getBlock() {
		return block;
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
