package blocks.events;

import commons.events.impl.EventSubscriber;
import commons.events.impl.bukkit.BukkitEventSubscriber;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class OriginBreakEvent extends Event implements Cancellable {
	private final Block block;
	private final Player player;
	private static EventSubscriber subscriber;
	private boolean isCancelled;
	public OriginBreakEvent(Block block, Player player) {
		this.block = block;
		this.player = player;
	}

	private static final HandlerList HANDLERS_LIST = new HandlerList();

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.isCancelled = cancel;
	}

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

	public static void bind(Consumer<OriginBreakEvent> eventConsumer) {
		if (subscriber != null) return;
		subscriber = new BukkitEventSubscriber<>(OriginBreakEvent.class, ((context, event) -> eventConsumer.accept(event)));
	}

	public static void call() {
		if (subscriber == null) return;
		Bukkit.getPluginManager().callEvent((Event) subscriber);
	}
}
