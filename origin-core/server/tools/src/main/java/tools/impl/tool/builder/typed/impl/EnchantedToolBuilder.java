package tools.impl.tool.builder.typed.impl;

import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.tool.builder.typed.IEnchantedToolBuilder;
import tools.impl.tool.impl.EnchantedTool;

public class EnchantedToolBuilder implements IEnchantedToolBuilder {
	private EnchantedTool tool;
	private ItemStack stack;

	public EnchantedToolBuilder(ItemStack stack) {
		this.stack = stack;
	}

	public EnchantedToolBuilder() {}

	@Override
	public EnchantedToolBuilder setStack(ItemStack stack) {
		this.stack = stack;
		this.tool = new EnchantedTool(stack);
		return this;
	}

	@Override
	public IEnchantedToolBuilder addEnchant(AttributeKey key, int level) {
		this.tool.addEnchant(key, level);
		return this;
	}

	@Override
	public EnchantedTool build() {
		return this.tool;
	}
}
