package blocks.block.protect.strategy;

import blocks.block.protect.ProtectedObject;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the potential modification of a protected object.
 */
public interface ProtectedAction {

	/**
	 * @return the {@link Entity} involved in this action, may be {@code null} to indicate that no entity was involved
	 */
	@Nullable Entity getEntity();

	/**
	 * @return the {@link Block} involved in this action
	 */
	@NotNull Block getBlock();

	/**
	 * @return the {@link ProtectedObject} that is currently responsible for handing this action
	 */
	@NotNull ProtectedObject getObject();

	/**
	 * @param <E> the type of event
	 * @return the {@link Event} responsible for triggering this action
	 */
	@NotNull <E extends Event & Cancellable> E getEvent();

}
