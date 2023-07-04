package blocks.impl.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AbstractBreakEvent extends Event {
	private final Block block;
	private final Player player;
	private boolean isCancelled;
	private static final HandlerList HANDLERS_LIST = new HandlerList();


	public AbstractBreakEvent(Block block, Player player) {
		this.block = block;
		this.player = player;
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

}
