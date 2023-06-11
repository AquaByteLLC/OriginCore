package enchants.records;

import com.google.inject.Inject;
import commons.entity.EntityEvent;
import enchants.EnchantAPI;
import enchants.config.EnchantConfig;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @param name Name of the enchantment
 * @param information Information about the enchant.
 * @param lore The lore which shows on the item.
 *
 * @param handleEnchant The executor for the enchant.
 */
public record OriginEnchant(String name,
                          String information,
                          String lore,
						  ItemStack menuItem,
                          EntityEvent<?> handleEnchant) {

	static EnchantConfig config;
	static JavaPlugin plugin = EnchantAPI.get().getInstance(JavaPlugin.class);

	/**
	 *
	 * @return {@link EnchantChance} this is used to map buffs to chances.
	 */
	public EnchantChance createChance() {
		if (config == null) {
			try {
				throw new Exception("The config for this enchant seems to be null, please run the 'createConfig(javaPlugin)' method.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return new EnchantChance(config);
	}

	@Inject
	public EnchantConfig createConfig() {
		config = new EnchantConfig(this, plugin);
		return config;
	}
}
