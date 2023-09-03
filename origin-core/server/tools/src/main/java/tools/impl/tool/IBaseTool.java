package tools.impl.tool;

import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;

public interface IBaseTool {
	ItemStack getItemStack();

	ItemStack formatMenuItemFor(AttributeKey key);

}
