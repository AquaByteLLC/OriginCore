package enderchests.impl.block;

import enderchests.block.FakeBlock;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

/**
 * @author vadim
 */
public class PacketBasedFakeBlock extends BlockAdapter implements FakeBlock {

	private final BlockData fakeData;

	public PacketBasedFakeBlock(Location location, BlockData fakeData) {
		super(location);
		this.fakeData = fakeData;
	}

	@Override
	public BlockData getProjectedBlockData() {
		return fakeData.clone();
	}

}
