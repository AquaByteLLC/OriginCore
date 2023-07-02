package blocks.impl.illusions.impl;

import blocks.block.util.ClickCallback;
import blocks.block.illusions.BlockOverlay;
import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/**
 * @author vadim
 */
@Deprecated(forRemoval = true)
public class IllusionFactoryImpl implements IllusionFactory {

	@Override
	public FakeBlock newFakeBlock(Location location, BlockData projected) {
		return new PacketBasedFakeBlock(location, projected);
	}
	@Override
	public FakeBlock newOverlayedBlock(Location location, BlockOverlay overlay) {
		return new PacketBasedFakeBlock(location, overlay);
	}

	@Override
	public FakeBlock newOverlayedBlock(Location location, BlockData overlay, ClickCallback onInteract) {
		return new PacketBasedFakeBlock(location, new FallingBlockOverlay(location, overlay, onInteract));
	}

	@Override
	public FakeBlock newHighlightedBlock(Location location, ChatColor color, ClickCallback onInteract) {
		return new PacketBasedFakeBlock(location, new FallingBlockOverlay(location, color, Material.GLASS.createBlockData(), onInteract));
	}

	@Override
	public FakeBlock newCustomBlock(Location location, BlockData projected, BlockOverlay overlay) {
		return new PacketBasedFakeBlock(location, projected, (FallingBlockOverlay) overlay);
	}

	@Override
	public FakeBlock newCustomBlock(Location location, BlockData projected, ChatColor color, BlockData overlay, ClickCallback onInteract) {
		return new PacketBasedFakeBlock(location, projected, new FallingBlockOverlay(location, color, overlay, onInteract));
	}

	@Override
	public BlockOverlay newHighlightedOverlay(Location location, ChatColor color, ClickCallback onInteract) {
		return new FallingBlockOverlay(location, color, Material.GLASS.createBlockData(), onInteract);
	}


	@Override
	public BlockOverlay newBlockOverlay(Location location, BlockData data, ClickCallback onInteract) {
		return new FallingBlockOverlay(location, data, onInteract);
	}


	@Override
	public BlockOverlay newCustomOverlay(Location location, ChatColor color, BlockData data, ClickCallback onInteract) {
		return new FallingBlockOverlay(location, color, data, onInteract);
	}
}
