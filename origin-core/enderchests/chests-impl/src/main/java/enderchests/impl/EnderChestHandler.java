package enderchests.impl;

import blocks.BlocksAPI;
import blocks.block.illusions.IllusionsAPI;
import commons.data.AccountProvider;
import commons.events.api.Subscribe;
import enderchests.ChestNetwork;
import enderchests.ChestRegistry;
import enderchests.LinkedChest;
import enderchests.NetworkColor;
import enderchests.impl.data.EnderChestAccount;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author vadim
 */
public class EnderChestHandler implements Listener {

	private final JavaPlugin plugin;
	private final ChestRegistry registry;
	private final IllusionsAPI illusions;
	private final AccountProvider<EnderChestAccount> accounts;

	public EnderChestHandler(JavaPlugin plugin, ChestRegistry registry, AccountProvider<EnderChestAccount> accounts) {
		this.plugin    = plugin;
		this.registry  = registry;
		this.illusions = BlocksAPI.getInstance().getIllusions();
		this.accounts  = accounts;
	}

	@Subscribe
	void onMove(InventoryMoveItemEvent event) {
		InventoryHolder holder = event.getDestination().getHolder();
		if (!(holder instanceof Container container)) return;

		LinkedChest chest = registry.getChestAt(container.getLocation());
		if (chest == null) return;
	}

	@Subscribe
	void onClose(InventoryCloseEvent event) {
		Entity entity = event.getPlayer();
		if (!(entity instanceof Player player)) return;

		EnderChestAccount account = accounts.getAccount(player);
		if (!account.isViewingLinkedInventory()) return;

		account.currentLinkedInventory.close(player);
		account.currentLinkedInventory = null;
	}

	@Subscribe
	void onPlace(BlockPlaceEvent event) {
		Player    player = event.getPlayer();
		Block     block  = event.getBlockPlaced();
		BlockData data   = block.getBlockData().clone();

		EnderChestAccount account = accounts.getAccount(player);
		if (event.getItemInHand().getType() == Material.ENDER_CHEST) {
			//todo: add item
			ChestNetwork net   = registry.getNetwork(account.temp, player);
			LinkedChest  chest = registry.createChest(net, block.getLocation(), ((Directional) data).getFacing());
			illusions.registry().register(chest);
			block.setType(Material.CHEST);
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				player.sendBlockChange(block.getLocation(), chest.getProjectedBlockData());
			}, 1L);
		}
	}

}
