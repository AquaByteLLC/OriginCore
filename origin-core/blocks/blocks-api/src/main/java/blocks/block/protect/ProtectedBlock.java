package blocks.block.protect;

import blocks.block.aspects.location.BlockLike;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface ProtectedBlock extends ProtectedObject, BlockLike {

	/**
	 * @return the {@link World} that this block is in
	 */
	@NotNull World getWorld();

}