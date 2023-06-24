package enchants.item;

import enchants.EnchantKey;
import enchants.item.EnchantedItem;
import enchants.item.EnchantBuilder;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public interface EnchantFactory {

	EnchantedItem newEnchantedItem(Consumer<ItemStackBuilder> builder);

	EnchantedItem wrapItemStack(ItemStack item);

	EnchantBuilder newEnchantBuilder(EnchantKey key);

	NamespacedKey getEnchantableKey();

	boolean canEnchant(ItemStack item);

	boolean canEnchant(PersistentDataContainer container);

	void setCanEnchant(ItemStack item, boolean canEnchant);

	void setCanEnchant(PersistentDataContainer container, boolean canEnchant);

}
