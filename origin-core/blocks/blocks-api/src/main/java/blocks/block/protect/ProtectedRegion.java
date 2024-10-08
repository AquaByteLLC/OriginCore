package blocks.block.protect;

import blocks.block.util.Cuboid;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a protected {@link Cuboid} region in {@link #getWorld() world}.
 * <p><b>NOTICE:</b> {@linkplain ProtectedBlock protected blocks} <i>always</i> take precedence over {@linkplain ProtectedRegion protected regions}!
 */
public interface ProtectedRegion extends ProtectedObject {

	/**
	 * @return the {@link Cuboid} that this region represents
	 */
	@NotNull Cuboid getBounds();

	/**
	 * @return the {@link World} that this region is in
	 */
	@NotNull World getWorld();

	/**
	 * Precedence when priorities are equal is undefined.
	 * @return the regional priority by which to determine which region's protection will be applied
	 */
	int getPriority();

	/**
	 * Precedence when priorities are equal is undefined.
	 * @param prio the regional priority by which to determine which region's protection will be applied
	 */
	void setPriority(int prio);

}
