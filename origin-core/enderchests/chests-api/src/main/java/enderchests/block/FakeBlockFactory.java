package enderchests.block;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

/**
 * @author vadim
 */
public interface FakeBlockFactory {

	FakeBlock newFakeBlock(Location location, BlockData projected);

}
