package blocks.impl.event;

import blocks.BlocksAPI;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.illusions.IllusionsAPI;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class OriginRegenerationEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private final EventRegistry registry = CommonsPlugin.commons().getEventRegistry();
	private boolean isCancelled;
	private final Regenable regenable;
	private final Player player;
	private final Block block;
	private final IllusionsAPI illusionsAPI;
	private final long end;

	public OriginRegenerationEvent(Regenable regenable, Player player, Block block, long end) {
		this.regenable = regenable;
		this.player = player;
		this.block = block;
		this.end = end;
		illusionsAPI = BlocksAPI.getInstance().getIllusions();
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}
