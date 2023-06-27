package blocks.impl.aspect.drop;

import blocks.block.aspects.drop.Dropable;
import blocks.block.builder.OriginBlockBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Drop implements Dropable {

	private final OriginBlockBuilder builder;
	private final List<ItemStack> droppedItems;

	public Drop(OriginBlockBuilder builder) {
		this.builder = builder;
		droppedItems = new ArrayList<>();
	}

	@Override
	public OriginBlockBuilder getBuilder() {
		return this.builder;
	}

	@Override
	public List<ItemStack> getDrops() {
		return droppedItems;
	}

	@Override
	public Dropable addDrop(ItemStack itemStack) {
		droppedItems.add(itemStack);
		return this;
	}

	@Override
	public Dropable removeDrop(ItemStack itemStack) {
		droppedItems.remove(itemStack);
		return this;
	}
}
