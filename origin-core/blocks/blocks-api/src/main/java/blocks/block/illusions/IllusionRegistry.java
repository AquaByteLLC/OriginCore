package blocks.block.illusions;

import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface IllusionRegistry {

	void register(FakeBlock block);

	void unregister(FakeBlock block);

	@Nullable FakeBlock getBlockAt(Location location);

}
