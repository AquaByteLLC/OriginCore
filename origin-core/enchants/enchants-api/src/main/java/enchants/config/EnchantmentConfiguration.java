package enchants.config;

import enchants.item.Enchant;
import lombok.Getter;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentConfiguration {
	@Getter private final YamlConfiguration configuration;
	private static final HashMap<String, YamlConfiguration> configurations;
	private static final HashMap<String, File> enchantFiles;


	static {
		configurations = new HashMap<>();
		enchantFiles = new HashMap<>();
	}

	public EnchantmentConfiguration(JavaPlugin plugin, String enchantKey) throws IOException {
		final File enchantFile = new File(plugin.getDataFolder(), enchantKey + ".yml");
		configuration = YamlConfiguration.loadConfiguration(enchantFile);
		EnchantConfigPaths.createSections(enchantKey, configuration);
		enchantFiles.put(enchantKey, enchantFile);
		configurations.put(enchantKey, configuration);
	}

	public static void save() throws IOException {
		for (Map.Entry<String, YamlConfiguration> entry : configurations.entrySet()) {
			final YamlConfiguration config = entry.getValue();
			final String key = entry.getKey();
			config.save(enchantFiles.get(key));
		}
	}

	public interface EnchantConfigPaths {

		// BASES
		String base = "Enchants." + "%key%";
		String menuSectionPath = base + ".menuItem";
		String chanceSectionPath = base + ".chance";
		String costSectionPath = base + ".cost";


		// PATHS
		String lorePath = base + ".lore";
		String descriptionPath = base + ".description";
		String maxLevelPath = base + ".maxLevel";

		// COST SECTION STUFF
		String costTypePath = costSectionPath + ".type";
		String maxCostPath = costSectionPath + ".max";
		String startCostPath = costSectionPath + ".start";

		// MENU SECTION STUFF
		String itemDisplayNamePath = menuSectionPath + ".displayName";
		String itemTypePath = menuSectionPath + ".type";
		String itemLorePath = menuSectionPath + ".lore";

		// CHANCE SECTION STUFF
		String chanceTypePath = chanceSectionPath + ".type";
		String maxChancePath = chanceSectionPath + ".max";
		String startChancePath = chanceSectionPath + ".start";
		static String getAndReplace(String current, String enchantKey) {
			return current.replaceAll("%key%", enchantKey);
		}

		static void createSections(String enchantKey, YamlConfiguration configuration) {
			final String chanceSectionReplaced = getAndReplace(chanceSectionPath, enchantKey);
			final String menuSectionReplaced = getAndReplace(menuSectionPath, enchantKey);
			final String costSectionReplaced = getAndReplace(costSectionPath, enchantKey);
			final String baseSectionReplaced = getAndReplace(base, enchantKey);
			final String enchantsSectionReplaced = "Enchants";

			if (configuration.isConfigurationSection(enchantsSectionReplaced)) return;


			final ConfigurationSection enchantsSection = configuration.isConfigurationSection(enchantsSectionReplaced) ? configuration.getConfigurationSection(enchantsSectionReplaced) : configuration.createSection(enchantsSectionReplaced);
			assert enchantsSection != null;

			final ConfigurationSection baseSection = configuration.isConfigurationSection(baseSectionReplaced) ? configuration.getConfigurationSection(baseSectionReplaced) : configuration.createSection(baseSectionReplaced);
			assert baseSection != null;

			final ConfigurationSection costSection = configuration.isConfigurationSection(costSectionReplaced) ? configuration.getConfigurationSection(costSectionReplaced) : configuration.createSection(costSectionReplaced);
			final ConfigurationSection menuSection = configuration.isConfigurationSection(menuSectionReplaced) ? configuration.getConfigurationSection(menuSectionReplaced) : configuration.createSection(menuSectionReplaced);
			final ConfigurationSection chanceSection = configuration.isConfigurationSection(chanceSectionReplaced) ? configuration.getConfigurationSection(chanceSectionReplaced) : configuration.createSection(chanceSectionReplaced);

			assert costSection != null;
			assert menuSection != null;
			assert chanceSection != null;

			baseSection.set("lore", "%name% -> %level%");
			baseSection.set("description", List.of("Description of the enchant", "This enchant is super cool"));
			baseSection.set("maxLevel", 10);

			chanceSection.set("type", "EXPONENTIAL");
			chanceSection.set("start", 10.0);
			chanceSection.set("max", 100.0);

			costSection.set("type", "EXPONENTIAL");
			costSection.set("start", 100.0);
			costSection.set("max", 10000.0);

			menuSection.set("type", "DIAMOND_BLOCK");
			menuSection.set("displayName", "&c&lMenuItem: &f%name%");
			menuSection.set("lore", List.of("&b&lItems lore for the enchant", "%maxLevel%", "%maxChance%", "%currentChance%", "%currentLevel%", "%maxCost%", "%currentCost%"));

			/*
			%currentLevel%
			%maxLevel%
			%maxChance%
			%currentChance%
			%description%
			%maxCost%
			%currentCost%
			%costType%
			%chanceType%
			 */
		}
	}

	public String getEnchantLore(String enchantKey) {
		final String path = EnchantConfigPaths.lorePath;
		final String lorePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getString(lorePath);
	}

	public List<String> getDescription(String enchantKey) {
		final String path = EnchantConfigPaths.descriptionPath;
		final String descriptionPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getStringList(descriptionPath);
	}
	public Enchant.ProgressionType getChanceType(String enchantKey) {
		final String path = EnchantConfigPaths.chanceTypePath;
		final String chanceTypePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		final String type = configuration.getString(chanceTypePath, "EXPONENTIAL");
		return Enchant.ProgressionType.valueOf(type);
	}

	public Enchant.ProgressionType getCostType(String enchantKey) {
		final String path = EnchantConfigPaths.costTypePath;
		final String costTypePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		final String type = configuration.getString(costTypePath, "EXPONENTIAL");
		return Enchant.ProgressionType.valueOf(type);
	}

	public int getMaxLevel(String enchantKey) {
		final String path = EnchantConfigPaths.maxLevelPath;
		final String maxLevelPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getInt(maxLevelPath);
	}

	public double getMaxCost(String enchantKey) {
		final String path = EnchantConfigPaths.maxCostPath;
		final String maxCostPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getDouble(maxCostPath);
	}

	public double getStartCost(String enchantKey) {
		final String path = EnchantConfigPaths.startCostPath;
		final String startCostPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getDouble(startCostPath);
	}

	public double getMaxChance(String enchantKey) {
		final String path = EnchantConfigPaths.maxChancePath;
		final String maxChancePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getDouble(maxChancePath);
	}

	public double getStartChance(String enchantKey) {
		final String path = EnchantConfigPaths.startChancePath;
		final String startChancePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getDouble(startChancePath);
	}

	public ItemStack getMenuItem(String enchantKey) {
		final String nPath = EnchantConfigPaths.itemDisplayNamePath;
		final String tPath = EnchantConfigPaths.itemTypePath;
		final String lPath = EnchantConfigPaths.itemLorePath;

		final String namePath = EnchantConfigPaths.getAndReplace(nPath, enchantKey);
		final String typePath = EnchantConfigPaths.getAndReplace(tPath, enchantKey);
		final String lorePath = EnchantConfigPaths.getAndReplace(lPath, enchantKey);

		final String name = configuration.getString(namePath);
		final Material type = Material.matchMaterial(configuration.getString(typePath, "STONE"));
		final List<String> lore = configuration.getStringList(lorePath);

		assert type != null;
		assert name != null;

		return ItemStackBuilder.of(type)
				.name(name)
				.lore(lore)
				.flag(ItemFlag.HIDE_ENCHANTS)
				.enchant(Enchantment.ARROW_INFINITE)
				.build();
	}
}
