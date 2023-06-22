package enchants.records;

import commons.events.impl.EventSubscriber;
import enchants.EnchantAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public record OriginEnchant(String name,
                          List<String> information,
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
	public static HashMap<NamespacedKey, YamlConfiguration> enchantConfiguration = new HashMap<>();

	public NamespacedKey getKey() {
		final JavaPlugin plugin = EnchantAPI.get().getInstance(JavaPlugin.class);
		return new NamespacedKey(plugin, name);
	}

	public void addToRegistry(YamlConfiguration config) {
		enchantRegistry.put(getKey(), this);
		enchantConfiguration.put(getKey(), config);
	}

	public enum EnchantProgressionType {
		EXPONENTIAL,
		LOGARITHMIC
	}
}
