package blocks.impl.protect;

import blocks.block.protect.ProtectedRegion;
import blocks.block.util.Cuboid;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;

/**
 * @author vadim
 */
class ProtRegion extends ProtObj implements ProtectedRegion {

	private final Cuboid cuboid;

	ProtRegion(World world, Cuboid cuboid) {
		super(world);
		this.cuboid = cuboid;
	}

	@Override
	public boolean protects(Block block) {
		return block != null && block.getWorld().getUID().equals(getWorld().getUID()) && cuboid.contains(block.getX(), block.getY(), block.getZ());
	}

	@Override
	public @NotNull Cuboid getBounds() {
		return cuboid;
	}

	private int prio = 0;

	@Override
	public int getPriority() {
		return prio;
	}

	@Override
	public void setPriority(int prio) {
		this.prio = prio;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ProtectedRegion region)) return false;
		return region.getWorld().getUID().equals(getWorld().getUID()) && region.getBounds().equals(cuboid) && region.getPriority() == prio;
	}

	@Override
	public int hashCode() {
		int result = cuboid.hashCode();
		result = 7907 * result + getWorld().getUID().hashCode();
		result = 7907 * result + prio;
		return result;
	}

}
