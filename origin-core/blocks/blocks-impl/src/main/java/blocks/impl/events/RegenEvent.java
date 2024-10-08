package blocks.impl.events;

import blocks.BlocksAPI;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.illusions.IllusionsAPI;
import commons.Commons;
import commons.events.api.EventRegistry;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegenEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private final EventRegistry registry = Commons.events();
	private boolean isCancelled;
	@Getter
	private final Regenable regenable;
	@Getter
	private final Player player;
	@Getter
	private final Block block;
	@Getter
	private final IllusionsAPI illusionsAPI;
	@Getter
	private final long end;
	@Getter private final String calling;

	public RegenEvent(String calling, Regenable regenable, Player player, Block block, long end) {
		this.regenable = regenable;
		this.player = player;
		this.calling = calling;
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
