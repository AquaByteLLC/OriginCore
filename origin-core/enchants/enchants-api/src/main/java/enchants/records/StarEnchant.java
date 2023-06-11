package enchants.records;

import enchants.config.EnchantConfig;
import me.lucko.helper.text3.Text;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

/**
 *
 * @param name Name of the enchantment
 * @param information Information about the enchant.
 * @param lore The lore which shows on the item.
 * @param price The price that the enchant costs.
 * @param chance The chance that the enchant has to proc.
 * @param chanceMulti The multiplier of which the chance goes up each level.
 * @param priceMulti The multiplier of which the price goes up each level.
 *
 * @param handleEnchant The executor for the enchant.
 */
public record StarEnchant(String name,
                          String information,
                          String lore,
                          double price,
                          double chance,
                          double chanceMulti,
                          double priceMulti,
                          Consumer<Event> handleEnchant) {

    /**
     *
     * @param level The level of the enchant.
     *
     * @return {@link EnchantCost} this is a record which allows us to create enchant costs.
     */
    public EnchantCost createCost(int level) {
        return new EnchantCost(this, level);
    }

    /**
     *
     * @param level The level of the enchant.
     *
     * @return {@link EnchantChance} this is a record which allows us to create enchant chances.
     */
    public EnchantChance createChance(int level) {
        return new EnchantChance(this, level);
    }

    /**
     *
     * @param plugin The plugins main class.
     *
     * @return {@link EnchantCost} this is a record which allows us to create and use the config for this enchant.
     */
    public EnchantConfig createConfig(JavaPlugin plugin) {
        return new EnchantConfig(this, plugin);
    }
}
