package enchants.item;

import enchants.EnchantKey;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public interface EnchantFactory {

	EnchantedItem newEnchantedItem(Consumer<ItemStackBuilder> builder);

	@Contract("null -> null; !null -> !null")
	EnchantedItem wrapItemStack(ItemStack item);

	EnchantBuilder newEnchantBuilder(EnchantKey key);

}
