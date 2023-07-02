package enchants.impl.item;

import enchants.EnchantKey;
import enchants.item.EnchantBuilder;
import enchants.item.EnchantFactory;
import enchants.item.EnchantedItem;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public class OriginEnchantFactory implements EnchantFactory {

	@Override
	public EnchantedItem newEnchantedItem(Consumer<ItemStackBuilder> builder) {
		ItemStack stack = ItemStackBuilder.of(Material.STONE_AXE).build();
		builder.accept(ItemStackBuilder.of(stack));
		final EnchantedItem item = new EnchantedItemImpl(stack);
		item.makeEnchantable();
		return item;
	}

	@Override
	public EnchantedItem wrapItemStack(ItemStack item) {
		return item == null ? null : new EnchantedItemImpl(item);
	}

	@Override
	public EnchantBuilder newEnchantBuilder(EnchantKey key) {
		return new OriginEnchantBuilder(key);
	}


}
