package enderchests.impl;

import commons.data.account.AccountProvider;
import enderchests.ChestNetwork;
import enderchests.ChestRegistry;
import enderchests.LinkedChest;
import enderchests.NetworkColor;
import enderchests.impl.data.EnderChestAccount;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
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

	private final Map<Location, LinkedChest> chests = new HashMap<>();
	private final Map<UUID, Map<NetworkColor, ChestNetwork>> networks = new HashMap<>();
	private final ConfigurationProvider conf;
	private final AccountProvider<EnderChestAccount> accounts;

	public EnderChestRegistry(ConfigurationProvider conf, AccountProvider<EnderChestAccount> accounts) {
		this.conf = conf;
		this.accounts = accounts;
	}

	@Override
	public LinkedChest[] getAllChests() {
		return chests.values().toArray(LinkedChest[]::new);
	}

	@Override
	public @Nullable LinkedChest getChestAt(Location location) {
		return chests.get(location.getBlock().getLocation());
	}

	@Override
	public @NotNull ChestNetwork getNetwork(NetworkColor color, Player player) {
		return networks.computeIfAbsent(player.getUniqueId(), x -> new HashMap<>()).computeIfAbsent(color, c -> new EnderChestNetwork(player.getUniqueId(), c, conf, accounts));
	}

	@Override
	public @NotNull LinkedChest createChest(ChestNetwork network, Location location, BlockFace face) {
		LinkedChest chest = ((EnderChestNetwork) network).newChest(location, face);
		chests.put(chest.getBlockLocation(), chest);
		return chest;
	}

	@Override
	public void deleteChest(LinkedChest chest) {
		((EnderChestNetwork) chest.getNetwork()).delChest(chest);
		chests.remove(chest.getBlockLocation());
	}

}
