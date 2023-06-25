package enderchests.block;

import commons.Owned;
import enderchests.ChestNetwork;

/**
 * @author vadim
 */
public interface LinkedChest extends FakeBlock, Owned {

	ChestNetwork getNetwork();

}
