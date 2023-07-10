package blocks.impl.protect;

import blocks.block.protect.strategy.ProtectedAction;
import blocks.block.protect.ProtectedObject;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author vadim
 */
class ProtAction implements ProtectedAction {

	Entity entity;
	Block block;
	ProtectedObject prot;
	Event event;

	ProtAction(Event event) {
		this.event = event;
	}

	@Override
	public @Nullable Entity getEntity() {
		return entity;
	}

	@Override
	public @NotNull Block getBlock() {
		return block;
	}

	@Override
	public @NotNull ProtectedObject getObject() {
		return prot;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends Event & Cancellable> @NotNull E getEvent() {
		return (E) event;
	}

}
