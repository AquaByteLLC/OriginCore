package enchants.item.builder;

import enchants.item.EnchantedItem;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface SpecialItemFactory {
	static EnchantedItem create(Consumer<ItemStackBuilder> builder) {
		ItemStack stack = ItemStackBuilder.of(Material.STONE_AXE).build();
		builder.accept(ItemStackBuilder.of(stack));
		final EnchantedItem item = new EnchantedItem(stack);
		item.makeEnchantable();
		return item;
	}
}
