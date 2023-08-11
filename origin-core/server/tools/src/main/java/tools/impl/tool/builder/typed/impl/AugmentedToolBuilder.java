package tools.impl.tool.builder.typed.impl;

import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.tool.builder.typed.IAugmentedToolBuilder;
import tools.impl.tool.impl.AugmentedTool;

public class AugmentedToolBuilder implements IAugmentedToolBuilder {
	private AugmentedTool tool;
	private ItemStack stack;

	public AugmentedToolBuilder(ItemStack stack) {
		this.stack = stack;
	}

	public AugmentedToolBuilder() {}

	@Override
	public AugmentedToolBuilder setStack(ItemStack stack) {
		this.stack = stack;
		this.tool = new AugmentedTool(stack);
		return this;
	}

	@Override
	public IAugmentedToolBuilder addAugment(AttributeKey key, long boost) {
		this.tool.addAugment(key, boost);
		return this;
	}

	@Override
	public IAugmentedToolBuilder setOpenSlots(int slots) {
		this.tool.setOpenSlots(slots);
		return this;
	}

	@Override
	public AugmentedTool build() {
		return this.tool;
	}
}
