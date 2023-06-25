package enderchests;

import enderchests.block.BlockHighlight;
import enderchests.block.FakeBlock;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * @author vadim
 */
public interface IllusionRegistry {

	void register(FakeBlock block);
	void unregister(FakeBlock block);

	void register(BlockHighlight highlight);
	void unregister(BlockHighlight highlight);

	@Nullable FakeBlock getBlockAt(Location location);
	@Nullable BlockHighlight getHighlightAt(Location location);

}
