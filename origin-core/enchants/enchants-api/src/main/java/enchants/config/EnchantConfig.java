package enchants.config;

import enchants.records.StarEnchant;
import enchants.registry.EnchantRegistry;
import me.lucko.helper.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class EnchantConfig {
    private FileConfiguration config;

    /**
     *
     * @param enchant {@link StarEnchant} record.
     * @param plugin The projects main class.
     *
     * This constructor creates the configuration for the enchantment.
     *
     */
    public EnchantConfig(StarEnchant enchant, JavaPlugin plugin) {
        if (!EnchantRegistry.get().isRegistered(enchant)) return;

        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder()
                + File.separator
                + "enchants"
                + File.separator
                + enchant.name() + ".yml"));
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
