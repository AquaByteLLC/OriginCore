package enderchests.impl.block;

import enderchests.block.BlockLike;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.UUID;

/**
 * @author vadim
 */
abstract class BlockAdapter implements BlockLike {

	private final Location location;

	BlockAdapter(Location location) {
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
