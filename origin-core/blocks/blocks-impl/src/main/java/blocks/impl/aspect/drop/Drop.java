package blocks.impl.aspect.drop;

import blocks.block.aspects.AspectType;
import blocks.block.aspects.BlockAspect;
import blocks.block.aspects.drop.Dropable;
import blocks.block.builder.AspectHolder;
import blocks.impl.aspect.BaseAspect;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Drop extends BaseAspect implements Dropable {

	private final List<ItemStack> droppedItems;

	public Drop(AspectHolder editor) {
		super(editor, AspectType.DROPABLE);
		droppedItems = new ArrayList<>();
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

	@Override
	public BlockAspect copy(AspectHolder newHolder) {
		Drop drop = new Drop(newHolder);
		drop.droppedItems.addAll(droppedItems.stream().map(ItemStack::clone).toList());
		return drop;
	}

}
