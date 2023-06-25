package enderchests;

import commons.Owned;
import enderchests.block.LinkedChest;

/**
 * @author vadim
 */
public interface ChestNetwork extends Owned {

	NetworkColor getColor();

	LinkedChest[] getChests();

}
