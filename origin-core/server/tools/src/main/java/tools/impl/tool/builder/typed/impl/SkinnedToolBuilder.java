package tools.impl.tool.builder.typed.impl;

import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.tool.builder.typed.ISkinnedToolBuilder;
import tools.impl.tool.impl.SkinnedTool;

public class SkinnedToolBuilder implements ISkinnedToolBuilder {
	private SkinnedTool tool;
	private ItemStack stack;

	public SkinnedToolBuilder(ItemStack stack) {
		this.stack = stack;
	}

	public SkinnedToolBuilder() {}

	@Override
	public SkinnedToolBuilder setStack(ItemStack stack) {
		this.stack = stack;
		this.tool = new SkinnedTool(stack);
		return this;
	}

	@Override
	public ISkinnedToolBuilder setSkin(AttributeKey key) {
		this.tool.addSkin(key);
		return this;
	}

	@Override
	public ISkinnedToolBuilder removeSkin() {
		this.tool.removeSkin();
		return this;
	}

	@Override
	public SkinnedTool build() {
		return this.tool;
	}
}
