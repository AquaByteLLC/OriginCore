package generators.wrapper;

import blocks.block.aspects.location.BlockLike;
import commons.data.Owned;
import generators.GeneratorRegistry;
import generators.wrapper.result.DestroyResult;
import generators.wrapper.result.UpgradeResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author vadim
 */
public interface Generator extends Owned, BlockLike {

	Tier getCurrentTier();

	ItemStack asItem();

	UpgradeResult upgrade(GeneratorRegistry registry);

	DestroyResult destroy(GeneratorRegistry registry, Player by);

}