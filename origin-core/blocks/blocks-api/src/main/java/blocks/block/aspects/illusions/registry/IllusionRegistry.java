package blocks.block.aspects.illusions.registry;

import blocks.block.aspects.illusions.FakeBlock;
import blocks.block.aspects.overlay.Overlayable;
import blocks.block.aspects.overlay.registry.OverlayLocationRegistry;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public interface IllusionRegistry {

	void register(FakeBlock block);
	void unregister(FakeBlock block);
	@Nullable FakeBlock getBlockAt(Location location);
	@Nullable Overlayable getOverlayAt(Location location);
	@NotNull HashMap<Location, FakeBlock> getFakeBlocks();
	OverlayLocationRegistry getOverlayRegistry();
}
