package enderchests.impl;

import commons.util.PackUtil;
import enderchests.ChestNetwork;
import enderchests.ChestRegistry;
import enderchests.LinkedChest;
import enderchests.NetworkColor;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
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
	private final ConfigurationProvider conf;

	public EnderChestRegistry(ConfigurationProvider conf) {
		this.conf = conf;
	}

	@Override
	public @Nullable LinkedChest getChestAt(Location location) {
		return chests.get(PackUtil.packLoc(location.getBlock().getLocation()));
	}

	@Override
	public @NotNull ChestNetwork getNetwork(NetworkColor color, Player player) {
		return networks.computeIfAbsent(player.getUniqueId(), x -> new HashMap<>()).computeIfAbsent(color, c -> new EnderChestNetwork(player.getUniqueId(), c, conf));
	}

	@Override
	public @NotNull LinkedChest createChest(ChestNetwork network, Location location, BlockFace face) {
		return ((EnderChestNetwork) network).newChest(location, face);
	}

	@Override
	public void deleteChest(LinkedChest chest) {
		((EnderChestNetwork) chest.getNetwork()).delChest(chest);
		chests.remove(PackUtil.packLoc(chest.getBlockLocation()));
	}

}
