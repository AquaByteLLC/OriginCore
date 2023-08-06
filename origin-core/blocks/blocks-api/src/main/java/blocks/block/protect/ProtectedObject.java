package blocks.block.protect;

import blocks.block.protect.strategy.ProtectionStrategy;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a {@linkplain ProtectedBlock block} or {@linkplain ProtectedRegion region} in a world.
 */
public interface ProtectedObject {

	/**
	 * @return the {@link World} this object resides in
	 */
	@NotNull World getWorld();

	/**
	 * @return whether or not {@code block} is protected by this {@link ProtectedObject}
	 */
	boolean protects(@Nullable Block block);

	/**
	 * @return the {@link ProtectionStrategy} currently in use
	 */
	@NotNull ProtectionStrategy getProtectionStrategy();

	/**
	 * Set the new {@link ProtectionStrategy} to use.
	 * @param strategy the new {@link ProtectionStrategy}
	 */
	void setProtectionStrategy(@NotNull ProtectionStrategy strategy);

}
