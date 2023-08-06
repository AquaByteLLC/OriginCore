package blocks.block.illusions;

import blocks.block.util.ClickCallback;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public interface IllusionFactory {

	FakeBlock newFakeBlock(Location location, BlockData projected);

	FakeBlock newOverlayedBlock(Location location, BlockOverlay overlay);

	FakeBlock newOverlayedBlock(Location location, BlockData overlay, ClickCallback onInteract);

	FakeBlock newHighlightedBlock(Location location, ChatColor color, ClickCallback onInteract);

	FakeBlock newCustomBlock(Location location, BlockData projected, BlockOverlay overlay);

	FakeBlock newCustomBlock(Location location, BlockData projected, ChatColor color, BlockData overlay, ClickCallback onInteract);

	BlockOverlay newHighlightedOverlay(Location location, ChatColor color, ClickCallback onInteract);

	BlockOverlay newBlockOverlay(Location location, BlockData data, ClickCallback onInteract);

	BlockOverlay newCustomOverlay(Location location, ChatColor color, BlockData data, ClickCallback onInteract);

}
