package blocks.impl.illusions.impl;

import blocks.block.illusions.BlockOverlay;
import blocks.block.illusions.FakeBlock;
import blocks.block.util.PacketReceiver;
import blocks.impl.illusions.BlockAdapter;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;

/**
 * @author vadim
 */
public class PacketBasedFakeBlock extends BlockAdapter implements FakeBlock {

	private final BlockData fakeData;
	private final FallingBlockOverlay overlay;

	@Deprecated(forRemoval = true)
	public PacketBasedFakeBlock(Location location) {
		this(location, null, null);
	}

	@Deprecated(forRemoval = true)
	public PacketBasedFakeBlock(Location location, BlockData fakeData) {
		this(location, fakeData, null);
	}

	@Deprecated(forRemoval = true)
	public PacketBasedFakeBlock(Location location, BlockOverlay overlay) {
		this(location, null, (FallingBlockOverlay) overlay);
	}

	public PacketBasedFakeBlock(Location location, BlockData fakeData, FallingBlockOverlay overlay) {
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
	public FallingBlockOverlay getOverlay() {
		return overlay;
	}

	@Override
	public boolean hasOverlay() {
		return overlay != null;
	}

	@Override
	public void send(PacketReceiver receiver) {
		if(hasProjection())
			receiver.sendPackets(new PacketPlayOutBlockChange(CraftLocation.toBlockPosition(getBlockLocation()), ((CraftBlockData) fakeData).getState()));
		if(hasOverlay())
			getOverlay().send(receiver);
	}

	@Override
	public void remove(PacketReceiver receiver) {
		receiver.sendPackets(new PacketPlayOutBlockChange(CraftLocation.toBlockPosition(getBlockLocation()), ((CraftBlockData) getBlock().getBlockData()).getState()));
		if(hasOverlay())
			getOverlay().remove(receiver);
	}

}
