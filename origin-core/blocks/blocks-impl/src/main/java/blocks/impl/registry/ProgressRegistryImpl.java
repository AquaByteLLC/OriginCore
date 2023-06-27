package blocks.impl.registry;

import blocks.block.progress.registry.ProgressRegistry;
import net.minecraft.core.BlockPosition;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;

public class ProgressRegistryImpl implements ProgressRegistry {
	private final ConcurrentHashMap<BlockPosition, Integer> randomIntegers;
	private final ConcurrentHashMap<BlockPosition, Boolean> blocksBreaking;
	private final ConcurrentHashMap<BlockPosition, Double> blockProgress;
	private final ConcurrentHashMap<BlockPosition, Double> oldBlockProgress;

	public ProgressRegistryImpl() {
		this.randomIntegers = new ConcurrentHashMap<>();
		this.blocksBreaking = new ConcurrentHashMap<>();
		this.blockProgress = new ConcurrentHashMap<>();
		this.oldBlockProgress = new ConcurrentHashMap<>();
	}

	public boolean getBlockBreak(BlockPosition pos) {
		return blocksBreaking.containsKey(pos);
	}

	public void copyOldData(BlockPosition pos) {
		if (!blockProgress.containsKey(pos)) {
			return;
		}

		oldBlockProgress.remove(pos);
		oldBlockProgress.put(pos, blockProgress.get(pos));
	}
	@Nonnull
	@Override
	public ConcurrentHashMap<BlockPosition, Double> getBlockProgress() {
		return blockProgress;
	}
	@Nonnull
	@Override
	public ConcurrentHashMap<BlockPosition, Double> getOldBlockProgress() {
		return oldBlockProgress;
	}
	@Nonnull
	@Override
	public ConcurrentHashMap<BlockPosition, Boolean> getBlocksBreaking() {
		return blocksBreaking;
	}

	@Nonnull
	@Override
	public ConcurrentHashMap<BlockPosition, Integer> getRandomIntegers() {
		return randomIntegers;
	}

	public void resetAll(BlockPosition position) {
		getBlocksBreaking().remove(position);
		getBlockProgress().remove(position);
		getOldBlockProgress().remove(position);
	}
}
