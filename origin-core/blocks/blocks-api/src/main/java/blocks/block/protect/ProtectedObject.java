package blocks.block.protect;

import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Represents a {@linkplain ProtectedBlock block} or {@linkplain ProtectedRegion region} in a world.
 * @author vadim
 */
public interface ProtectedObject {

	/**
	 * @return the {@link World} this object resides in
	 */
	World getWorld();

	/**
	 * @return whether or not {@code block} is protected by this {@link ProtectedObject}
	 */
	boolean protects(Block block);

	/**
	 * @return the {@link ProtectionStrategy} currently in use
	 */
	ProtectionStrategy getProtectionStrategy();

	/**
	 * Set the new {@link ProtectionStrategy} to use.
	 * @param strategy the new {@link ProtectionStrategy}
	 */
	void setProtectionStrategy(ProtectionStrategy strategy);

}
