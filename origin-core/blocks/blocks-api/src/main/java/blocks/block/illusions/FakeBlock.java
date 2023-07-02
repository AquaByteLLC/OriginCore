package blocks.block.illusions;

import blocks.block.aspects.location.BlockLike;
import org.bukkit.block.data.BlockData;

public interface FakeBlock extends BlockLike, Illusion {

	BlockData getProjectedBlockData();

	boolean hasProjection();

	BlockOverlay getOverlay();

	boolean hasOverlay();

}
