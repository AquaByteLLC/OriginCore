package enderchests.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author vadim
 */
public interface FakeBlock extends BlockLike {

	BlockData getProjectedBlockData();

}
