package tools.impl.attribute.skins.impl;

import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeFactory;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.skins.SkinBuilder;
import tools.impl.tool.impl.SkinnedTool;
import tools.impl.tool.type.ISkinnedTool;

import java.util.function.Consumer;

public class ToolSkinFactory implements AttributeFactory<ISkinnedTool, SkinBuilder> {
	@Override
	public ISkinnedTool newAttributeItem(Consumer<ItemStackBuilder> builder) {
		ItemStack stack = ItemStackBuilder.of(Material.STONE_AXE).build();
		builder.accept(ItemStackBuilder.of(stack));
		final ISkinnedTool item = new SkinnedTool(stack);
		item.makeSkinnable();
		return item;
	}

	@Override

	public ISkinnedTool wrapItemStack(ItemStack item) {
		return item == null ? null : new SkinnedTool(item);
	}

	@Override
	public SkinBuilder newAttributeBuilder(AttributeKey key) {
		return new ToolSkinBuilder(key);
	}
}
