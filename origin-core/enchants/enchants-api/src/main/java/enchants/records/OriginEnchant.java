package enchants.records;

import commons.events.impl.EventSubscriber;
import enchants.EnchantAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public record OriginEnchant(String name,
                          String information,
                          String lore,
						  ItemStack menuItem,
						  int maxLevel,
						  double startCost,
						  double maxCost,
						  double startChance,
						  double maxChance,
						    EnchantProgressionType chanceType,
						    EnchantProgressionType costType,
                          EventSubscriber handleEnchant) {

	public NamespacedKey getKey() {
		final JavaPlugin plugin = EnchantAPI.get().getInstance(JavaPlugin.class);
		return new NamespacedKey(plugin, name);
	}

	public enum EnchantProgressionType {
		EXPONENTIAL,
		LOGARITHMIC
	}
}
