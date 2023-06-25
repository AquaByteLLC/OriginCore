package enderchests.impl.block;

import enderchests.block.FakeBlock;
import enderchests.block.FakeBlockFactory;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

/**
 * @author vadim
 */
public class PacketBasedFakeBlockFactory implements FakeBlockFactory {

	@Override
	public FakeBlock newFakeBlock(Location location, BlockData projected) {
		return new PacketBasedFakeBlock(location, projected);
	}

}
