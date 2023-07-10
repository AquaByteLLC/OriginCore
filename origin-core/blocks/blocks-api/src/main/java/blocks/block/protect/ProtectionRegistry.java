package blocks.block.protect;

import blocks.block.util.Cuboid;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Transient {@linkplain ProtectedBlock block} and {@linkplain ProtectedRegion region} protection API.<p>
 * This class is <u>not guaranteed</u> to protect from <i>every event</i>. It implements <i>most</i> events that pertain to block modifications.
 * Namely, almost all concrete children of {@link org.bukkit.event.block.BlockEvent}.
 * @author vadim
 */
public interface ProtectionRegistry {

	/**
	 * @return whether or not {@code block} is protected by {@linkplain #getAllProtection(Block) any} {@linkplain ProtectedObject protected objects}.
	 * @see #getAllProtection(Block)
	 * @see #getActiveProtection(Block)
	 */
	boolean isProtected(Block block);

	/**
	 * @return the highest priority {@link ProtectedObject} that applies to {@code block} or {@code null}
	 */
	@Nullable ProtectedObject getActiveProtection(Block block);

	/**
	 * @return all {@linkplain ProtectedObject protected objects} that apply to the given location
	 */
	@NotNull ProtectedObject[] getAllProtection(Block block);

	/**
	 * This method will return the same object when called multiple times with the same arguments.
	 * @return a {@link ProtectedObject} that applies to {@code block}
	 */
	ProtectedBlock defineBlock(Block block);

	/**
	 * This method will return the same object when called multiple times with the same arguments.<p>
	 * If multiple regions intersect, then protection will be determined by their {@linkplain ProtectedRegion#getPriority() priority}.
	 * However, {@linkplain ProtectedBlock blocks} will always take priority over {@linkplain ProtectedRegion regions}.
	 * @return a {@link ProtectedObject} that applies to {@code cuboid}
	 */
	ProtectedRegion defineRegion(World world, Cuboid cuboid);

	/**
	 * Unregister an {@link ProtectedObject}.
	 */
	void release(ProtectedObject object);

}
