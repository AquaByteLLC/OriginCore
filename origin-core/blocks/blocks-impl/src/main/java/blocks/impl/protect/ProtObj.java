package blocks.impl.protect;

import blocks.block.protect.ProtectedObject;
import blocks.block.protect.strategy.ProtectionStrategies;
import blocks.block.protect.strategy.ProtectionStrategy;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * @author vadim
 */
abstract class ProtObj implements ProtectedObject {

	private final World world;

	ProtObj(World world) {
		this.world = world;
	}

	@Override
	public @NotNull World getWorld() {
		return world;
	}

	private ProtectionStrategy strategy = ProtectionStrategies.DEFAULT;

	@Override
	public @NotNull ProtectionStrategy getProtectionStrategy() {
		return strategy;
	}

	@Override
	public void setProtectionStrategy(@NotNull ProtectionStrategy strategy) {
		if(strategy == null)
			throw new NullPointerException("illegal strategy argument");
		this.strategy = strategy;
	}

}
