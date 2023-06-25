package enderchests;

import enderchests.block.LinkedChest;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author vadim
 */
public interface ChestRegistry {

	@Nullable LinkedChest getChestAt(Location location);

	@NotNull ChestNetwork getNetwork(NetworkColor color, Player player);

	@NotNull LinkedChest createChest(ChestNetwork network, Location location);

	void deleteChest(LinkedChest chest);

}
