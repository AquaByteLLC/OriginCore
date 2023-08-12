package tools.impl.attribute;

import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface AttributeFactory<T, C> {
	T newAttributeItem(Consumer<ItemStackBuilder> builder);

	T wrapItemStack(ItemStack item);

	C newAttributeBuilder(AttributeKey key);
}
