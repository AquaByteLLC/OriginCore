package enchants.impl.config;

import enchants.records.OriginEnchant;
import lombok.Getter;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.text3.Text;
import me.vadim.util.conf.ConfigurationAccessor;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class EnchantsConfig extends YamlFile {

	@Getter private final ConfigurationAccessor provider = getConfigurationAccessor();
	@Getter private final YamlConfiguration configuration = yaml;

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

		// ENCHANTS STUFF
		String loreHeaderPath = "Enchants.loreHeader";


		static String getAndReplace(String current, String enchantKey) {
			return current.replaceAll("%key%", enchantKey);
		}

		static void createSections(String enchantKey, YamlConfiguration configuration, EnchantsConfig config) {
			final String chanceSectionReplaced = getAndReplace(chanceSectionPath, enchantKey);
			final String menuSectionReplaced = getAndReplace(menuSectionPath, enchantKey);
			final String costSectionReplaced = getAndReplace(costSectionPath, enchantKey);
			final String baseSectionReplaced = getAndReplace(base, enchantKey);
			final String enchantsSectionReplaced = "Enchants";

			System.out.println("I'm here");
			final ConfigurationSection enchantsSection = configuration.isConfigurationSection(enchantsSectionReplaced) ? configuration.getConfigurationSection(enchantsSectionReplaced) : configuration.createSection(enchantsSectionReplaced);
			assert enchantsSection != null;
			System.out.println("I'm here");

			final ConfigurationSection baseSection = enchantsSection.isConfigurationSection(baseSectionReplaced) ? enchantsSection.getConfigurationSection(baseSectionReplaced) : enchantsSection.createSection(baseSectionReplaced);
			assert baseSection != null;

			final ConfigurationSection costSection = baseSection.isConfigurationSection(costSectionReplaced) ? baseSection.getConfigurationSection(costSectionReplaced) : baseSection.createSection(costSectionReplaced);
			final ConfigurationSection menuSection = baseSection.isConfigurationSection(menuSectionReplaced) ? baseSection.getConfigurationSection(menuSectionReplaced) : baseSection.createSection(menuSectionReplaced);
			final ConfigurationSection chanceSection = baseSection.isConfigurationSection(chanceSectionReplaced) ? baseSection.getConfigurationSection(chanceSectionReplaced) : baseSection.createSection(chanceSectionReplaced);

			assert costSection != null;
			assert menuSection != null;
			assert chanceSection != null;

			baseSection.set("lore", "{name} -> {level}");
			baseSection.set("description", List.of("Description of the enchant", "This enchant is super cool"));
			baseSection.set("maxLevel", 10);

			chanceSection.set("type", "EXPONENTIAL");
			chanceSection.set("start", 10.0);
			chanceSection.set("max", 100.0);

			costSection.set("type", "EXPONENTIAL");
			costSection.set("start", 100.0);
			costSection.set("max", 10000.0);

			menuSection.set("type", "DIAMOND_BLOCK");
			menuSection.set("displayName", "&c&lMenuItem: &f{name}");
			menuSection.set("lore", List.of("&b&lItems lore for the enchant", "{maxLevel}", "{maxChance}", "{currentChance}", "{currentLevel}", "{maxCost}", "{currentCost}"));

			config.reload();
		}
	}

	public EnchantsConfig(ResourceProvider resourceProvider) {
		super("enchantsConfig.yml", resourceProvider);
		setDefaultTemplate();
	}

	public String getEnchantLore(String enchantKey) {
		final String path = EnchantConfigPaths.lorePath;
		final String lorePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return getConfigurationAccessor().getString(lorePath);
	}

	public String[] getDescription(String enchantKey) {
		final String path = EnchantConfigPaths.descriptionPath;
		final String descriptionPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return getConfigurationAccessor().getStringArray(descriptionPath);
	}
	public OriginEnchant.EnchantProgressionType getChanceType(String enchantKey) {
		final String path = EnchantConfigPaths.chanceTypePath;
		final String chanceTypePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		final String type = getConfigurationAccessor().getString(chanceTypePath);
		return OriginEnchant.EnchantProgressionType.valueOf(type);
	}

	public OriginEnchant.EnchantProgressionType getCostType(String enchantKey) {
		final String path = EnchantConfigPaths.costTypePath;
		final String costTypePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		final String type = getConfigurationAccessor().getString(costTypePath);
		return OriginEnchant.EnchantProgressionType.valueOf(type);
	}

	public int getMaxLevel(String enchantKey) {
		final String path = EnchantConfigPaths.maxLevelPath;
		final String maxLevelPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return getConfigurationAccessor().getInt(maxLevelPath);
	}

	public double getMaxCost(String enchantKey) {
		final String path = EnchantConfigPaths.maxCostPath;
		final String maxCostPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return getConfigurationAccessor().getDouble(maxCostPath);
	}

	public double getStartCost(String enchantKey) {
		final String path = EnchantConfigPaths.startCostPath;
		final String startCostPath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return getConfigurationAccessor().getDouble(startCostPath);
	}

	public double getMaxChance(String enchantKey) {
		final String path = EnchantConfigPaths.maxChancePath;
		final String maxChancePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return getConfigurationAccessor().getDouble(maxChancePath);
	}

	public double getStartChance(String enchantKey) {
		final String path = EnchantConfigPaths.startChancePath;
		final String startChancePath = EnchantConfigPaths.getAndReplace(path, enchantKey);
		return getConfigurationAccessor().getDouble(startChancePath);
	}

	public String[] getEnchantLoreHeader() {
		final String path = EnchantConfigPaths.loreHeaderPath;
		return getConfigurationAccessor().getStringArray(path);
	}

	public ItemStack getMenuItem(String enchantKey) {
		final String nPath = EnchantConfigPaths.itemDisplayNamePath;
		final String tPath = EnchantConfigPaths.itemTypePath;
		final String lPath = EnchantConfigPaths.itemLorePath;

		final String namePath = EnchantConfigPaths.getAndReplace(nPath, enchantKey);
		final String typePath = EnchantConfigPaths.getAndReplace(tPath, enchantKey);
		final String lorePath = EnchantConfigPaths.getAndReplace(lPath, enchantKey);

		final String name = Text.colorize(getConfigurationAccessor().getString(namePath));
		final Material type = Material.matchMaterial(getConfigurationAccessor().getString(typePath));
		final String[] lore = getConfigurationAccessor().getStringArray(lorePath);

		if (name == null || lore == null || type == null) logError(resourceProvider.getLogger(), "Enchants." + enchantKey, "item element");

		assert type != null;
		assert lore != null;

		return ItemStackBuilder.of(type)
				.name(name)
				.lore(Text.colorize(Arrays.toString(lore)))
				.flag(ItemFlag.HIDE_ENCHANTS)
				.enchant(Enchantment.ARROW_INFINITE)
				.build();
	}

}