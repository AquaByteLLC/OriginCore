package enchants.item;

import enchants.EnchantKey;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * @author vadim
 */
public interface EnchantedItem {

	ItemStack getItemStack();

	void addEnchant(EnchantKey enchantKey, int level);

	void removeEnchant(EnchantKey enchantKey);

	void removeAllEnchants();

	boolean hasEnchant(EnchantKey enchantKey);

	void makeEnchantable();

	boolean isEnchantable();

	Set<EnchantKey> getAllEnchants();

	double getChance(EnchantKey enchantKey);

	double getCost(EnchantKey enchantKey);

	int getLevel(EnchantKey enchantKey);

	boolean activate(EnchantKey enchantKey);

}