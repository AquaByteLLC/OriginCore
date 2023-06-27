package blocks.block.progress.registry;

import net.minecraft.core.BlockPosition;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public interface ProgressRegistry {
	@NotNull ConcurrentHashMap<BlockPosition, Integer> getRandomIntegers();
	@NotNull ConcurrentHashMap<BlockPosition, Boolean> getBlocksBreaking();
	@NotNull ConcurrentHashMap<BlockPosition, Double> getBlockProgress();
	@NotNull ConcurrentHashMap<BlockPosition, Double> getOldBlockProgress();

	boolean getBlockBreak(BlockPosition pos);

	void copyOldData(BlockPosition pos);

	void resetAll(BlockPosition position);
}
