package tools.impl.tool.builder;

import org.bukkit.inventory.ItemStack;
import tools.impl.tool.IBaseTool;

public interface ISpecialToolBuilder<T extends IBaseTool> {

	ISpecialToolBuilder<T> setStack(ItemStack stack);
	T build();
}
