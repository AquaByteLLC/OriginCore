package blocks.impl.illusions;

import blocks.block.illusions.BlockOverlay;
import blocks.block.illusions.FakeBlock;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

/**
 * @author vadim
 */
public class PacketBasedFakeBlock extends BlockAdapter implements FakeBlock {

	private final BlockData fakeData;
	private final BlockOverlay overlay;

	public PacketBasedFakeBlock(Location location) {
		this(location, null, null);
	}

	public PacketBasedFakeBlock(Location location, BlockData fakeData) {
		this(location, fakeData, null);
	}

	public PacketBasedFakeBlock(Location location, BlockOverlay overlay) {
		this(location, null, overlay);
	}

	public PacketBasedFakeBlock(Location location, BlockData fakeData, BlockOverlay overlay) {
		super(location);
		this.fakeData = fakeData == null ? null : fakeData.clone();
		this.overlay  = overlay;
	}

	@Override
	public BlockData getProjectedBlockData() {
		if(!hasProjection()) throw new UnsupportedOperationException("no projection set");
		return fakeData.clone();
	}

	@Override
	public boolean hasProjection() {
		return fakeData != null;
	}

	@Override
	public BlockOverlay getOverlay() {
		return overlay;
	}

	@Override
	public boolean hasOverlay() {
		return overlay != null;
	}

}
