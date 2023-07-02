package enchants.conf;

import enchants.item.Enchant;
import lombok.Getter;
import lombok.SneakyThrows;
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
	@Getter
	private final YamlConfiguration configuration;
	private static final HashMap<String, YamlConfiguration> configurations = new HashMap<>();
	private static final HashMap<String, File> enchantFiles = new HashMap<>();

	private final String enchantKey;

	public EnchantmentConfiguration(JavaPlugin plugin, String enchantKey) throws IOException {
		this.enchantKey = enchantKey;
		final File enchantFile = new File(plugin.getDataFolder(), enchantKey + ".yml");
		configuration = YamlConfiguration.loadConfiguration(enchantFile);
		EnchantConfigPaths.createSections(enchantKey, configuration);
		enchantFiles.put(enchantKey, enchantFile);
		configurations.put(enchantKey, configuration);
	}

	@SneakyThrows
	public static void save() {
		for (Map.Entry<String, YamlConfiguration> entry : configurations.entrySet()) {
			final YamlConfiguration config = entry.getValue();
			final String key = entry.getKey();
			config.save(enchantFiles.get(key));
		}
	}

	public interface EnchantConfigPaths {

		// BASES
		String enchants = "Enchants";
		String base = enchants + ".%key%";
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

		static String getAsRelative(String global) {
			return global.substring(global.lastIndexOf('.') + 1);
		}

		static ConfigurationSection getOrCreate(ConfigurationSection section, String path) {
			return section.isConfigurationSection(path) ? section.getConfigurationSection(path) : section.createSection(path);
		}

		static void createSections(String enchantKey, YamlConfiguration configuration) {
			final String chanceSectionReplaced = getAndReplace(chanceSectionPath, enchantKey);
			final String menuSectionReplaced = getAndReplace(menuSectionPath, enchantKey);
			final String costSectionReplaced = getAndReplace(costSectionPath, enchantKey);
			final String baseSectionReplaced = getAndReplace(base, enchantKey);
			final String enchantsSectionReplaced = enchants;

			if (configuration.isConfigurationSection(enchantsSectionReplaced)) return;

			final ConfigurationSection enchantsSection = getOrCreate(configuration, enchantsSectionReplaced);
			final ConfigurationSection baseSection = getOrCreate(configuration, baseSectionReplaced);
			final ConfigurationSection costSection = getOrCreate(configuration, costSectionReplaced);
			final ConfigurationSection menuSection = getOrCreate(configuration, menuSectionReplaced);
			final ConfigurationSection chanceSection = getOrCreate(configuration, chanceSectionReplaced);

			assert enchantsSection != null;
			assert baseSection != null;
			assert costSection != null;
			assert menuSection != null;
			assert chanceSection != null;

			baseSection.set(getAsRelative(lorePath), "%name% -> %level%");
			baseSection.set(getAsRelative(descriptionPath), List.of("Description of the enchant", "This enchant is super cool"));
			baseSection.set(getAsRelative(maxLevelPath), 10);

			chanceSection.set(getAsRelative(chanceTypePath), "EXPONENTIAL");
			chanceSection.set(getAsRelative(startChancePath), 10.0);
			chanceSection.set(getAsRelative(maxChancePath), 100.0);

			costSection.set(getAsRelative(costTypePath), "EXPONENTIAL");
			costSection.set(getAsRelative(startCostPath), 100.0);
			costSection.set(getAsRelative(maxCostPath), 10000.0);

			menuSection.set(getAsRelative(itemTypePath), "DIAMOND_BLOCK");
			menuSection.set(getAsRelative(itemDisplayNamePath), "&c&lMenuItem: &f%name%");
			menuSection.set(getAsRelative(itemLorePath), List.of("&b&lItems lore for the enchant", "%maxLevel%", "%maxChance%", "%currentChance%", "%currentLevel%", "%maxCost%", "%currentCost%"));

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

	public String getEnchantLore() {
		final String path = EnchantConfigPaths.lorePath;
		final String lorePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getString(lorePath);
	}

	public List<String> getDescription() {
		final String path = EnchantConfigPaths.descriptionPath;
		final String descriptionPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getStringList(descriptionPath);
	}

	public Enchant.ProgressionType getChanceType() {
		final String path = EnchantConfigPaths.chanceTypePath;
		final String chanceTypePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		final String type = configuration.getString(chanceTypePath, "EXPONENTIAL");
		return Enchant.ProgressionType.valueOf(type);
	}

	public Enchant.ProgressionType getCostType() {
		final String path = EnchantConfigPaths.costTypePath;
		final String costTypePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		final String type = configuration.getString(costTypePath, "EXPONENTIAL");
		return Enchant.ProgressionType.valueOf(type);
	}

	public int getMaxLevel() {
		final String path = EnchantConfigPaths.maxLevelPath;
		final String maxLevelPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getInt(maxLevelPath);
	}

	public double getMaxCost() {
		final String path = EnchantConfigPaths.maxCostPath;
		final String maxCostPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getDouble(maxCostPath);
	}

	public double getStartCost() {
		final String path = EnchantConfigPaths.startCostPath;
		final String startCostPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getDouble(startCostPath);
	}

	public double getMaxChance() {
		final String path = EnchantConfigPaths.maxChancePath;
		final String maxChancePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getDouble(maxChancePath);
	}

	public double getStartChance() {
		final String path = EnchantConfigPaths.startChancePath;
		final String startChancePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return configuration.getDouble(startChancePath);
	}

	public ItemStack getMenuItem() {
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
				.build();
	}
}
