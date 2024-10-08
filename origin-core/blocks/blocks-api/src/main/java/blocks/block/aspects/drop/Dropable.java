package blocks.block.aspects.drop;

import blocks.block.aspects.BlockAspect;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Dropable extends BlockAspect {

	List<ItemStack> getDrops();

	Dropable addDrop(ItemStack itemStack);

	Dropable removeDrop(ItemStack itemStack);

}
