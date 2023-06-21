package enchants.records;

import commons.events.impl.EventSubscriber;
import enchants.EnchantAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public record OriginEnchant(String name,
                          String[] information,
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

	public static HashMap<NamespacedKey, OriginEnchant> enchantRegistry = new HashMap<>();

	public NamespacedKey getKey() {
		final JavaPlugin plugin = EnchantAPI.get().getInstance(JavaPlugin.class);
		return new NamespacedKey(plugin, name);
	}

	public OriginEnchant addToRegistry() {
		enchantRegistry.put(getKey(), this);
		return this;
	}

	public enum EnchantProgressionType {
		EXPONENTIAL,
		LOGARITHMIC
	}
}
