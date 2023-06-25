package enderchests.impl;

import commons.PackUtil;
import enderchests.ChestNetwork;
import enderchests.ChestRegistry;
import enderchests.block.LinkedChest;
import enderchests.NetworkColor;
import enderchests.impl.block.EnderChestNetwork;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author vadim
 */
public class EnderChestRegistry implements ChestRegistry {

	private final Map<Long, LinkedChest> chests = new HashMap<>();
	private final Map<UUID, Map<NetworkColor, ChestNetwork>> networks = new HashMap<>();

	@Override
	public @Nullable LinkedChest getChestAt(Location location) {
		return chests.get(PackUtil.packLoc(location.getBlock().getLocation()));
	}

	@Override
	public @NotNull ChestNetwork getNetwork(NetworkColor color, Player player) {
		return networks.computeIfAbsent(player.getUniqueId(), x -> new HashMap<>()).computeIfAbsent(color, c -> new EnderChestNetwork(player.getUniqueId(), c));
	}

	@Override
	public @NotNull LinkedChest createChest(ChestNetwork network, Location location) {
		return ((EnderChestNetwork) network).newChest(location);
	}

	@Override
	public void deleteChest(LinkedChest chest) {
		((EnderChestNetwork) chest.getNetwork()).delChest(chest);
		chests.remove(PackUtil.packLoc(chest.getBlockLocation()));
	}

}
