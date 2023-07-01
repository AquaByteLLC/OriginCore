package enderchests;

import commons.Owned;
import org.bukkit.inventory.Inventory;

/**
 * @author vadim
 */
public interface ChestNetwork extends Owned {

	NetworkColor getColor();

	Inventory getInventory();

	LinkedChest[] getChests();

}
