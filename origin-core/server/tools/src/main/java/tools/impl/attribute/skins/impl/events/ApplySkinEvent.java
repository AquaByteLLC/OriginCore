package tools.impl.attribute.skins.impl.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import tools.impl.attribute.AttributeKey;

public class ApplySkinEvent extends Event {
	private final Player player;
	private final AttributeKey skinKey;
	private final ItemStack appliedStack;
	private final ItemStack applierStack;
	private boolean isCancelled;

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	@Getter
	private final String calling;

	public ApplySkinEvent(String calling, AttributeKey skinKey, Player player, ItemStack appliedStack, ItemStack applierStack) {
		this.skinKey = skinKey;
		this.player = player;
		this.appliedStack = appliedStack;
		this.applierStack = applierStack;
		this.calling = calling;
	}

	public Player getPlayer() {
		return player;
	}

	public AttributeKey getSkinKey() {
		return skinKey;
	}

	public ItemStack getAppliedStack() {
		return appliedStack;
	}

	public ItemStack getApplierStack() {
		return applierStack;
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
