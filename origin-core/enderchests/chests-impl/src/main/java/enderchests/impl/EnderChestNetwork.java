package enderchests.impl;

import commons.data.account.AccountProvider;
import commons.impl.data.PlayerOwned;
import commons.util.StringUtil;
import enderchests.ChestNetwork;
import enderchests.ChestRegistry;
import enderchests.LinkedChest;
import enderchests.NetworkColor;
import enderchests.impl.conf.Config;
import enderchests.impl.data.EnderChestAccount;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author vadim
 */
public class EnderChestNetwork extends PlayerOwned implements ChestNetwork {

	private final NetworkColor color;
	private final Inventory inventory;
	private final List<LinkedChest> chests = new ArrayList<>();
	private final AccountProvider<EnderChestAccount> accounts;
	private final ChestRegistry registry;
	private final ConfigurationProvider conf;

	public EnderChestNetwork(UUID uuid, NetworkColor color, ConfigurationProvider conf, AccountProvider<EnderChestAccount> accounts, ChestRegistry registry) {
		super(uuid);
		this.color = color;
		this.inventory = Bukkit.createInventory(null, Config.CHEST_SIZE,
				StringUtil.colorize(conf.open(Config.class)
						.getLinkedInventoryTitle().format(StringPlaceholder.of("color",
								color.chatColor.toString() +
										StringUtil.convertToUserFriendlyCase(color.name()) +
										"&r"))));
		this.accounts = accounts;
		this.registry = registry;
		this.conf     = conf;
	}

	public void defineInventory(Inventory inventory) {
		this.inventory.clear();
		this.inventory.setContents(inventory.getContents());
	}

	public LinkedChest newChest(Location location, BlockFace face) {
		LinkedChest chest = new LinkedEnderChest(location, accounts, face, this, registry, conf);
		chests.add(chest);
		return chest;
	}

	public void delChest(LinkedChest chest) {
		chests.remove(chest);
	}

	@Override
	public NetworkColor getColor() {
		return color;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public LinkedChest[] getChests() {
		return chests.toArray(LinkedChest[]::new);
	}

	@Override
	public int getSlotsUsed() {
		return chests.size();
	}

}
