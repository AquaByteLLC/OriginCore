package blocks.impl.illusions;

import blocks.block.aspects.location.BlockLike;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * @author vadim
 */
public abstract class BlockAdapter implements BlockLike {

	private final Location location;
	public BlockAdapter(Location location) {
		this.location = location.getBlock().getLocation();
	}

	@Override
	public Block getBlock() {
		return location.getBlock();
	}

	@Override
	public Location getBlockLocation() {
		return location.clone();
	}

}
