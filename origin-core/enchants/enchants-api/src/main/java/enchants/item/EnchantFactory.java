package enchants.item;

import enchants.records.OriginEnchant;
import org.bukkit.inventory.ItemStack;

public interface EnchantFactory {
	EnchantFactory updateEnchant(OriginEnchant enchant);
	EnchantFactory removeEnchant(OriginEnchant enchantName);
	EnchantFactory addEnchant(OriginEnchant enchantName);
	OriginEnchant getEnchant(ItemStack item);
	boolean hasEnchant(ItemStack itemStack, OriginEnchant enchant);
}
