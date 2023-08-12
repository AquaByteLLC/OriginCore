package tools.impl.attribute.enchants.impl;

import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import tools.impl.attribute.AttributeFactory;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.enchants.EnchantBuilder;
import tools.impl.tool.type.IEnchantedTool;
import tools.impl.tool.impl.EnchantedTool;

import java.util.function.Consumer;

public class CustomEnchantFactory implements AttributeFactory<IEnchantedTool, EnchantBuilder> {
	@Override
	public CustomEnchantBuilder newAttributeBuilder(AttributeKey key) {
		return new CustomEnchantBuilder(key);
	}

	@Override
	public IEnchantedTool newAttributeItem(Consumer<ItemStackBuilder> builder) {
		ItemStack stack = ItemStackBuilder.of(Material.STONE_AXE).build();
		builder.accept(ItemStackBuilder.of(stack));
		final IEnchantedTool item = new EnchantedTool(stack);
		item.makeEnchantable();
		return item;
	}

	@Override
	@Contract("null -> null; !null -> !null")
	public IEnchantedTool wrapItemStack(ItemStack item) {
		return item == null ? null : new EnchantedTool(item);
	}
}
