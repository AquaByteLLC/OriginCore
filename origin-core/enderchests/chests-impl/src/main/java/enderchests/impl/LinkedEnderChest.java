package enderchests.impl.block;

import enderchests.ChestNetwork;
import enderchests.block.LinkedChest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import java.util.UUID;

/**
 * @author vadim
 */
public class LinkedEnderChest extends PacketBasedFakeBlock implements LinkedChest {

	private final ChestNetwork network;

	LinkedEnderChest(Location location, ChestNetwork network) {
		super(location, Material.ENDER_CHEST.createBlockData());
		this.network  = network;
	}

	@Override
	public ChestNetwork getNetwork() {
		return network;
	}

	/* delegate impl to network */

	@Override
	public OfflinePlayer getOfflineOwner() {
		return network.getOfflineOwner();
	}

	@Override
	public UUID getOwnerUUID() {
		return network.getOwnerUUID();
	}

	@Override
	public boolean isOwnedBy(OfflinePlayer test) {
		return network.isOwnedBy(test);
	}

	@Override
	public boolean isOwnedBy(UUID test) {
		return network.isOwnedBy(test);
	}

}
