package enderchests;

import blocks.block.illusions.FakeBlock;
import commons.Owned;
import org.bukkit.inventory.Inventory;

/**
 * @author vadim
 */
public interface LinkedChest extends FakeBlock, Owned {

	ChestNetwork getNetwork();

	Inventory getInventory();

}
