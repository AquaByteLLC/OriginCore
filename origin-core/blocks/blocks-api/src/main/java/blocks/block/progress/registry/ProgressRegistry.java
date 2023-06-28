package blocks.block.progress.registry;

import net.minecraft.core.BlockPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ProgressRegistry {

	@NotNull Map<BlockPosition, Integer> getRandomIntegers();

	@NotNull Map<BlockPosition, Boolean> getBlocksBreaking();

	@NotNull Map<BlockPosition, Double> getBlockProgress();

	@NotNull Map<BlockPosition, Double> getOldBlockProgress();

	boolean getBlockBreak(BlockPosition pos);

	void copyOldData(BlockPosition pos);

	void resetAll(BlockPosition position);

}
