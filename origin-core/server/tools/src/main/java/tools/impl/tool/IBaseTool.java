package tools.impl.tool;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.function.Consumer;

public interface IBaseTool {
	ItemStack getItemStack();

	static void writeContainer(ItemStack stack, Consumer<PersistentDataContainer> consumer) {
		stack.editMeta(meta -> consumer.accept(meta.getPersistentDataContainer()));
	}
}
