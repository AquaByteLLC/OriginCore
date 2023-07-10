package enderchests.impl;

import blocks.block.protect.ProtectedBlock;
import blocks.block.protect.ProtectedObject;
import blocks.block.protect.ProtectionRegistry;
import commons.data.account.AccountProvider;
import enderchests.ChestNetwork;
import enderchests.ChestRegistry;
import enderchests.LinkedChest;
import enderchests.NetworkColor;
import enderchests.impl.data.EnderChestAccount;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
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
	private final ProtectionRegistry registry;

	public EnderChestRegistry(ConfigurationProvider conf, AccountProvider<EnderChestAccount> accounts, ProtectionRegistry registry) {
		this.conf     = conf;
		this.accounts = accounts;
		this.registry = registry;
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
	public @NotNull ChestNetwork getNetwork(NetworkColor color, OfflinePlayer player) {
		return networks.computeIfAbsent(player.getUniqueId(), x -> new HashMap<>(10)).computeIfAbsent(color, c -> new EnderChestNetwork(player.getUniqueId(), c, conf, accounts, this));
	}

	@Override
	public @NotNull LinkedChest createChest(ChestNetwork network, Location location, BlockFace face) {
		LinkedChest chest = ((EnderChestNetwork) network).newChest(location, face);

		chest.validateBlock();

		chests.put(chest.getBlockLocation(), chest);
		return chest;
	}

	@Override
	public void deleteChest(LinkedChest chest) {
		((EnderChestNetwork) chest.getNetwork()).delChest(chest);

		ProtectedObject object = registry.getActiveProtection(chest.getBlock());
		if(object instanceof ProtectedBlock block)
			registry.release(block);

		chests.remove(chest.getBlockLocation());
	}

	public void defineNetwork(NetworkColor color, UUID owner, Inventory inventory) {
		EnderChestNetwork network = new EnderChestNetwork(owner, color, conf, accounts, this);
		network.defineInventory(inventory);
		networks.computeIfAbsent(owner, x -> new HashMap<>(10)).put(color, network);
	}

	public Map<UUID, Map<NetworkColor, ChestNetwork>> getNetworks() {
		return networks;
	}

}
