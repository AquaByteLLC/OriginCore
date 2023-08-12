package tools.impl.attribute.enchants.impl;

import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.conf.AttributeConfiguration;
import tools.impl.progression.ProgressionType;

import java.math.BigDecimal;
import java.util.List;

import static tools.impl.attribute.enchants.impl.EnchantConfiguration.EnchantConfigPaths.*;

public class EnchantConfiguration extends AttributeConfiguration {

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
	}

	private final FileConfiguration configuration;

	public EnchantConfiguration(AttributeKey key) {
		super(key, "enchants");
		this.configuration = getConf().getFileConfiguration();

		final String chanceSectionReplaced = getAndReplace(chanceSectionPath, key.getName());
		final String menuSectionReplaced = getAndReplace(menuSectionPath, key.getName());
		final String costSectionReplaced = getAndReplace(costSectionPath, key.getName());
		final String baseSectionReplaced = getAndReplace(base, key.getName());
		final String enchantsSectionReplaced = enchants;

		if (configuration.isConfigurationSection(enchantsSectionReplaced)) return;

		writeAndSave(file -> {
			final ConfigurationSection enchantsSection = getOrCreate(configuration, enchantsSectionReplaced);
			final ConfigurationSection baseSection = getOrCreate(configuration, baseSectionReplaced);
			final ConfigurationSection costSection = getOrCreate(configuration, costSectionReplaced);
			final ConfigurationSection menuSection = getOrCreate(configuration, menuSectionReplaced);
			final ConfigurationSection chanceSection = getOrCreate(configuration, chanceSectionReplaced);

			baseSection.set(getAsRelative(lorePath), " &d&l| &d{name} &f-> &5{level}");
			baseSection.set(getAsRelative(descriptionPath), List.of("Description of the enchant", "This enchant is super cool"));
			baseSection.set(getAsRelative(maxLevelPath), 10);

			chanceSection.set(getAsRelative(chanceTypePath), "EXPONENTIAL");
			chanceSection.set(getAsRelative(startChancePath), 10.0);
			chanceSection.set(getAsRelative(maxChancePath), 100.0);

			costSection.set(getAsRelative(costTypePath), "EXPONENTIAL");
			costSection.set(getAsRelative(startCostPath), 100.0);
			costSection.set(getAsRelative(maxCostPath), 10000.0);

			menuSection.set(getAsRelative(itemTypePath), "DIAMOND_BLOCK");
			menuSection.set(getAsRelative(itemDisplayNamePath), "&c&lMenuItem: &f{name}");
			menuSection.set(getAsRelative(itemLorePath), List.of("&b&lItems lore for the enchant", "%maxLevel%", "%maxChance%", "%currentChance%", "%currentLevel%", "%maxCost%", "%currentCost%"));
		});
	}

	public String getEnchantLore() {
		final String path = lorePath;
		final String lorePath = getAndReplace(path, getKey().getName());
		return configuration.getString(lorePath);
	}

	public List<String> getDescription() {
		final String path = descriptionPath;
		final String descriptionPath = getAndReplace(path, getKey().getName());
		return configuration.getStringList(descriptionPath);
	}

	public ProgressionType getChanceType() {
		final String path = chanceTypePath;
		final String chanceTypePath = getAndReplace(path, getKey().getName());
		final String type = configuration.getString(chanceTypePath, "EXPONENTIAL");
		return ProgressionType.valueOf(type);
	}

	public ProgressionType getCostType() {
		final String path = costTypePath;
		final String costTypePath = getAndReplace(path, getKey().getName());
		final String type = configuration.getString(costTypePath, "EXPONENTIAL");
		return ProgressionType.valueOf(type);
	}

	public int getMaxLevel() {
		final String path = maxLevelPath;
		final String maxLevelPath = getAndReplace(path, getKey().getName());
		return configuration.getInt(maxLevelPath);
	}

	public BigDecimal getMaxCost() {
		final String path = maxCostPath;
		final String maxCostPath = getAndReplace(path, getKey().getName());
		return BigDecimal.valueOf(configuration.getDouble(maxCostPath));
	}

	public BigDecimal getStartCost() {
		final String path = startCostPath;
		final String startCostPath = getAndReplace(path, getKey().getName());
		return BigDecimal.valueOf(configuration.getDouble(startCostPath));
	}

	public BigDecimal getMaxChance() {
		final String path = maxChancePath;
		final String maxChancePath = getAndReplace(path, getKey().getName());
		return BigDecimal.valueOf(configuration.getDouble(maxChancePath));
	}

	public BigDecimal getStartChance() {
		final String path = startChancePath;
		final String startChancePath = getAndReplace(path, getKey().getName());
		return BigDecimal.valueOf(configuration.getDouble(startChancePath));
	}

	public ItemStack getMenuItem() {
		final String namePath = getAndReplace(itemDisplayNamePath, getKey().getName());
		final String typePath = getAndReplace(itemTypePath, getKey().getName());
		final String lorePath = getAndReplace(itemLorePath, getKey().getName());

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
