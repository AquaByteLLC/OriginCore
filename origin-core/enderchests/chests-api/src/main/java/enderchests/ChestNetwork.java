package enderchests;

import commons.data.Owned;
import org.bukkit.inventory.Inventory;

/**
 * @author vadim
 */
public interface ChestNetwork extends Owned {

	NetworkColor getColor();

	Inventory getInventory();

	LinkedChest[] getChests();

	int getSlotsUsed();

}
