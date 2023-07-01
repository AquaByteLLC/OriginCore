package enderchests.impl.data;


import com.j256.ormlite.table.DatabaseTable;
import commons.data.AbstractAccount;
import enderchests.ChestRegistry;
import enderchests.NetworkColor;
import enderchests.impl.LinkedEnderChest;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author vadim
 */
@DatabaseTable
public class EnderChestAccount extends AbstractAccount {

	ChestRegistry registry;

	private EnderChestAccount() { // ORMLite
		super(null);
	}

	EnderChestAccount(UUID uuid, ChestRegistry registry, ConfigurationProvider conf) {
		super(uuid);

		this.registry = registry;
	}

	public NetworkColor temp = NetworkColor.AQUA;

	public LinkedEnderChest currentLinkedInventory;

	public boolean isViewingLinkedInventory(){
		return currentLinkedInventory != null;
	}

}