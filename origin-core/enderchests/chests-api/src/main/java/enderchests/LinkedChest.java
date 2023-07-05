package enderchests;

import blocks.block.illusions.FakeBlock;
import commons.Owned;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

/**
 * @author vadim
 */
public interface LinkedChest extends FakeBlock, Owned {

	ChestNetwork getNetwork();

	Inventory getInventory();

	/**
	 * @param isOpen the new open animation state
	 */
	void setOpen(boolean isOpen);

	/**
	 * Updates the animation state based on the empty status of {@link Inventory#getViewers()}.
	 * @see #getInventory()
	 * @see #setOpen(boolean)
	 */
	void updateAnimation();

	/**
	 * Ensures that hoppers will continue to function with this chest by verifiying that the block is still a {@link Material#CHEST} on the server.
	 */
	void ensureHopperConnectivity();

}
