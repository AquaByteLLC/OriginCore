package blocks.impl.event;

import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class OriginBreakEvent extends Event {
	private final Block block;
	private final Player player;
	private final EventRegistry registry = CommonsPlugin.commons().getEventRegistry();
	private boolean isCancelled;

	public OriginBreakEvent(Block block, Player player) {
		this.block = block;
		this.player = player;
	}

	private static final HandlerList HANDLERS_LIST = new HandlerList();

	// these were not actually implemented
//	@Override
//	public boolean isCancelled() {
//		return isCancelled;
//	}
//
//	@Override
//	public void setCancelled(boolean cancel) {
//		this.isCancelled = cancel;
//	}

	public Player getPlayer() {
		return player;
	}

	public Block getBlock() {
		return block;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

}
