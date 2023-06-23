package enchants.records;

import commons.events.impl.EventSubscriber;
import enchants.EnchantAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public record OriginEnchant(String name,
							List<String> information,
							String lore, ItemStack menuItem,
							int maxLevel, double startCost, double maxCost, double startChance, double maxChance,
							EnchantProgressionType chanceType, EnchantProgressionType costType,
							EventSubscriber handleEnchant) {

	public static final HashMap<NamespacedKey, OriginEnchant> enchantRegistry = new HashMap<>();
	public static final HashMap<NamespacedKey, YamlConfiguration> enchantConfiguration = new HashMap<>();
	public static final NamespacedKey requiredKey = new NamespacedKey(EnchantAPI.get().getInstance(JavaPlugin.class), "CUSTOM_ENCHANT_KEY");

	public NamespacedKey getKey() {
		final JavaPlugin plugin = EnchantAPI.get().getInstance(JavaPlugin.class);
		return new NamespacedKey(plugin, name);
	}

	public void addToRegistry(YamlConfiguration config) {
		enchantRegistry.put(getKey(), this);
		enchantConfiguration.put(getKey(), config);
	}

	public static boolean canEnchant(PersistentDataContainer container) {
		return container.has(requiredKey);
	}

	public enum EnchantProgressionType {
		EXPONENTIAL,
		LOGARITHMIC
	}
}
