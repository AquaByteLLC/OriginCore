package blocks.block.protect;

import blocks.block.util.Cuboid;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Transient {@linkplain ProtectedBlock block} and {@linkplain ProtectedRegion region} protection API.
 * @author vadim
 */
public interface ProtectionRegistry {

	/**
	 * @return all {@linkplain ProtectedObject protected objects} that apply to the given location
	 */
	@NotNull ProtectedObject[] getProtectionAt(Block block);

	/**
	 * @return whether or not {@code block} is protected by {@linkplain #getProtectionAt(Block) any} {@linkplain ProtectedObject protected objects}.
	 * @see #getProtectionAt(Block)
	 */
	boolean isProtected(Block block);

	/**
	 * @return a {@link ProtectedObject} that applies to {@code block}
	 */
	ProtectedBlock protectBlock(Block block);

	/**
	 * If multiple regions intersect, then protection will be determined by their {@linkplain ProtectedRegion#getPriority() priority}.
	 * @return a {@link ProtectedObject} that applies to {@code cuboid}
	 */
	ProtectedRegion protectRegion(World world, Cuboid cuboid);

	/**
	 * Unregister an {@link ProtectedObject}.
	 */
	void removeProtection(ProtectedObject object);

}
