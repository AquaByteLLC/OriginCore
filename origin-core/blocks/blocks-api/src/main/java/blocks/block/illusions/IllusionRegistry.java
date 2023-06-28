package blocks.block.illusions;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public interface IllusionRegistry {
	void register(FakeBlock block);
	void unregister(FakeBlock block);
	@Nullable FakeBlock getBlockAt(Location location);
}
