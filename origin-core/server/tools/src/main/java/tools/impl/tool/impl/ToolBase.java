package tools.impl.tool.impl;

import commons.util.BukkitUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import tools.impl.attribute.AttributeBuilder;
import tools.impl.attribute.AttributeFactory;
import tools.impl.attribute.BaseAttribute;
import tools.impl.registry.AttributeRegistry;
import tools.impl.tool.IBaseTool;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public abstract class ToolBase<A extends BaseAttribute, T extends IBaseTool, B extends AttributeBuilder> implements IBaseTool {

	protected final ItemStack itemStack;

	public ToolBase(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public ItemStack getItemStack() {
		return this.itemStack;
	}

	protected PersistentDataContainer readContainer() {
		return BukkitUtil.readContainer(itemStack);
	}

	protected void writeContainer(Consumer<PersistentDataContainer> consumer) {
		BukkitUtil.writeContainer(itemStack, consumer);
	}


	protected abstract AttributeRegistry<A> getRegistry();

	protected abstract AttributeFactory<T, B> getFactory();

}
