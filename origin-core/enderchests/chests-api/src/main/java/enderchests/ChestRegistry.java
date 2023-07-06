package enderchests;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author vadim
 */
public interface ChestRegistry {

	LinkedChest[] getAllChests();

	@Nullable LinkedChest getChestAt(Location location);

	@NotNull ChestNetwork getNetwork(NetworkColor color, Player player);

	@NotNull LinkedChest createChest(ChestNetwork network, Location location, BlockFace face);

	void deleteChest(LinkedChest chest);
}
