package blocks.impl.protect;

import blocks.block.protect.ProtectedBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * @author vadim
 */
class ProtBlock extends ProtObj implements ProtectedBlock {

	private final int x, y, z;

	ProtBlock(Block block) {
		super(block.getWorld());
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
	}

	@Override
	public boolean protects(Block block) {
		return block != null && block.getWorld().getUID().equals(getWorld().getUID()) && block.getX() == x && block.getY() == y && block.getZ() == z;
	}

	@Override
	public Block getBlock() {
		return getWorld().getBlockAt(x, y, z);
	}

	@Override
	public Location getBlockLocation() {
		return new Location(getWorld(), x, y, z);
	}

}
