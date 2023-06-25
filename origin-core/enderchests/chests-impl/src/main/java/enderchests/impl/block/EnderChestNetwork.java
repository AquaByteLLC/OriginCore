package enderchests.impl.block;

import commons.impl.PlayerOwned;
import enderchests.ChestNetwork;
import enderchests.block.LinkedChest;
import enderchests.NetworkColor;
import enderchests.impl.block.LinkedEnderChest;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author vadim
 */
public class EnderChestNetwork extends PlayerOwned implements ChestNetwork {

	private final NetworkColor color;
	private final List<LinkedChest> chests = new ArrayList<>();

	public EnderChestNetwork(UUID uuid, NetworkColor color) {
		super(uuid);
		this.color = color;
	}

	public LinkedChest newChest(Location location) {
		LinkedChest chest = new LinkedEnderChest(location, this);
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
	public LinkedChest[] getChests() {
		return chests.toArray(LinkedChest[]::new);
	}

}
