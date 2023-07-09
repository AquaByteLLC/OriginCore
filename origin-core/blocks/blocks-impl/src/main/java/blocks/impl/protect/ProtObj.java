package blocks.impl.protect;

import blocks.block.protect.ProtectedObject;
import blocks.block.protect.ProtectionStrategy;
import org.bukkit.World;

/**
 * @author vadim
 */
abstract class ProtObj implements ProtectedObject {

	private final World world;

	ProtObj(World world) {
		this.world = world;
	}

	@Override
	public World getWorld() {
		return world;
	}

	private ProtectionStrategy strategy = ProtectionStrategy.DEFAULT;

	@Override
	public ProtectionStrategy getProtectionStrategy() {
		return strategy;
	}

	@Override
	public void setProtectionStrategy(ProtectionStrategy strategy) {
		this.strategy = strategy;
	}

}
