package enderchests.impl;

import blocks.BlocksAPI;
import blocks.block.illusions.IllusionsAPI;
import commons.CommonsPlugin;
import commons.data.AccountProvider;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import enderchests.ChestNetwork;
import enderchests.ChestRegistry;
import enderchests.LinkedChest;
import enderchests.NetworkColor;
import enderchests.impl.data.EnderChestAccount;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.ByteBuffer;

/**
 * @author vadim
 */
public class EnderChestHandler implements Listener {

	private final JavaPlugin plugin;
	private final ChestRegistry registry;
	private final IllusionsAPI illusions;
	private final AccountProvider<EnderChestAccount> accounts;

	public EnderChestHandler(JavaPlugin plugin, ChestRegistry registry, AccountProvider<EnderChestAccount> accounts, EventRegistry events) {
		this.plugin    = plugin;
		this.registry  = registry;
		this.illusions = BlocksAPI.getInstance().getIllusions();
		this.accounts  = accounts;
		events.subscribeAll(this);
	}

	@Subscribe
	@SuppressWarnings("DuplicatedCode")
	void onMoveInto(InventoryMoveItemEvent event) {
		Inventory into = event.getDestination();
		Inventory from = event.getSource();

		InventoryHolder holder = into.getHolder();
		if (!(holder instanceof Chest container)) return;

		LinkedChest chest = registry.getChestAt(container.getLocation());
		if (chest == null) return;

		event.setCancelled(true);
		ItemStack item = event.getItem().clone();

		CommonsPlugin.scheduler().getBukkitSync().runLater(() -> {
			// remove single item
			from.removeItem(item);
			chest.getInventory().addItem(item);
		}, 1L);
	}

	@Subscribe
	@SuppressWarnings("DuplicatedCode")
	void onMoveFrom(InventoryMoveItemEvent event) {
		Inventory into = event.getDestination();
		Inventory from = event.getSource();

		InventoryHolder holder = from.getHolder();
		if (!(holder instanceof Chest container)) return;

		LinkedChest chest = registry.getChestAt(container.getLocation());
		if (chest == null) return;

		event.setCancelled(true);

		Inventory cI = chest.getInventory();

		if(cI.isEmpty()) return;

		CommonsPlugin.scheduler().getBukkitSync().runLater(() -> {
			// remove single item
			ItemStack item = null;
			ItemStack[] contents = cI.getContents();
			for (int i = 0; i < contents.length; i++) {
				ItemStack content = contents[i];
				if (content != null) {
					item = content.clone();
					item.setAmount(1);
					if(content.getAmount() > 1)
						content.setAmount(content.getAmount() - 1);
					else
						contents[i] = null;
					break;
				}
			}
			if(item != null) {
				cI.setContents(contents);
				into.addItem(item);
			}
		}, 1L);

	}

	@Subscribe
	void onClose(InventoryCloseEvent event) {
		Entity entity = event.getPlayer();
		if (!(entity instanceof Player player)) return;

		EnderChestAccount account = accounts.getAccount(player);
		if (!account.isViewingLinkedInventory()) return;

		account.openNewLinkedInventory(null);
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
			illusions.globalRegistry().register(chest);
			chest.ensureHopperConnectivity();
			CommonsPlugin.scheduler().getBukkitSync().runLater(() -> {
//				player.sendBlockChange(block.getLocation(), chest.getProjectedBlockData());
				System.out.println(player);
				System.out.println(block);
				System.out.println(chest);
			}, 1L);
		}
	}

	@Subscribe
	void onLeftClick(BlockBreakEvent event) {
		LinkedChest chest = registry.getChestAt(event.getBlock().getLocation());

		if(chest == null) return;
		((LinkedEnderChest) chest).leftClick(event.getPlayer());
		event.setCancelled(true);
	}

	@Subscribe
	void onLeftClick(BlockDamageEvent event) {
		LinkedChest chest = registry.getChestAt(event.getBlock().getLocation());

		if(chest == null) return;
		((LinkedEnderChest) chest).leftClick(event.getPlayer());
		event.setCancelled(true);
	}

	@Subscribe
	void onRightClick(InventoryOpenEvent event) {
		Entity entity = event.getPlayer();
		if(!(entity instanceof Player player)) return;

		InventoryHolder holder = event.getInventory().getHolder();
		if(!(holder instanceof Chest container)) return;

		LinkedChest chest = registry.getChestAt(container.getLocation());
		if(chest == null) return;
		event.setCancelled(true);
		((LinkedEnderChest) chest).rightClick(player);
	}
}
