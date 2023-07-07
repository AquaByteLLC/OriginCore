package enchants.item;

import enchants.EnchantKey;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author vadim
 */
public interface EnchantedItem {

	ItemStack getItemStack();

	ItemStack formatMenuItemFor(EnchantKey key);

	void addEnchant(EnchantKey enchantKey, long level);

	void removeEnchant(EnchantKey enchantKey);

	void removeAllEnchants();

	boolean hasEnchant(EnchantKey enchantKey);

	void makeEnchantable();

	boolean isEnchantable();

	Set<EnchantKey> getAllEnchants();

	BigDecimal getChance(EnchantKey enchantKey);

	BigDecimal getCost(EnchantKey enchantKey);

	long getLevel(EnchantKey enchantKey);

	boolean activate(EnchantKey enchantKey);

}