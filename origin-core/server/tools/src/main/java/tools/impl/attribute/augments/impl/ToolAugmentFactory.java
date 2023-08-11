package tools.impl.attribute.augments.impl;

import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeFactory;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.augments.AugmentBuilder;
import tools.impl.tool.type.IAugmentedTool;
import tools.impl.tool.impl.AugmentedTool;
import java.util.function.Consumer;

public class ToolAugmentFactory implements AttributeFactory<IAugmentedTool, AugmentBuilder> {

	@Override
	public AugmentBuilder newAttributeBuilder(AttributeKey key) {
		return new ToolAugmentBuilder(key);
	}

	@Override
	public IAugmentedTool newAttributeItem(Consumer<ItemStackBuilder> builder) {
		ItemStack stack = ItemStackBuilder.of(Material.STONE_AXE).build();
		builder.accept(ItemStackBuilder.of(stack));
		final IAugmentedTool item = new AugmentedTool(stack);
		item.makeAugmentable(1);
		return item;
	}

	@Override
	public IAugmentedTool wrapItemStack(ItemStack item) {
		return item == null ? null : new AugmentedTool(item);
	}
}
