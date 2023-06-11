package enchants.config;

import enchants.records.OriginEnchant;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class EnchantConfig {
    private final FileConfiguration config;

    /**
     *
     * @param enchant {@link OriginEnchant} record.
     * @param plugin The projects main class.
     *
     * This constructor creates the configuration for the enchantment.
     *
     */
    public EnchantConfig(OriginEnchant enchant, JavaPlugin plugin) {
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
