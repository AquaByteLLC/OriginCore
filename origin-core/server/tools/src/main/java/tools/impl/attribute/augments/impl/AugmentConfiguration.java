package tools.impl.attribute.augments.impl;

import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.conf.AttributeConfiguration;

import java.util.List;

import static tools.impl.attribute.augments.impl.AugmentConfiguration.AugmentConfigPaths.*;

public class AugmentConfiguration extends AttributeConfiguration {

	public interface AugmentConfigPaths {

		// BASES
		String augments = "Augments";
		String base = augments + ".%key%";
		String menuSectionPath = base + ".menuItem";
		String boostSectionPath = base + ".boost";

		// PATHS
		String lorePath = base + ".appliedLore";
		String descriptionPath = base + ".description";

		// BOOST SECTION STUFF
		String maxBoostPath = boostSectionPath + ".max";
		String minBoostPath = boostSectionPath + ".min";

		// MENU SECTION STUFF
		String itemDisplayNamePath = menuSectionPath + ".displayName";
		String itemTypePath = menuSectionPath + ".type";
		String itemLorePath = menuSectionPath + ".lore";
	}

	private final FileConfiguration configuration;

	public AugmentConfiguration(AttributeKey key) {
		super(key, "augments");
		this.configuration = getConf().getFileConfiguration();

		final String menuSectionReplaced = getAndReplace(menuSectionPath, key.getName());
		final String boostSectionReplaced = getAndReplace(boostSectionPath, key.getName());
		final String baseSectionReplaced = getAndReplace(base, key.getName());
		final String enchantsSectionReplaced = augments;

		if (configuration.isConfigurationSection(enchantsSectionReplaced)) return;

		buildAndSave(file -> {
			final ConfigurationSection enchantsSection = getOrCreate(configuration, enchantsSectionReplaced);
			final ConfigurationSection baseSection = getOrCreate(configuration, baseSectionReplaced);
			final ConfigurationSection boostSection = getOrCreate(configuration, boostSectionReplaced);
			final ConfigurationSection menuSection = getOrCreate(configuration, menuSectionReplaced);

			baseSection.set(getAsRelative(lorePath), "&7[&e{name} &f &e{boost}&7]");
			baseSection.set(getAsRelative(descriptionPath), List.of("Description of the augment", "This augment is super cool"));

			boostSection.set(getAsRelative(minBoostPath), 0);
			boostSection.set(getAsRelative(maxBoostPath), 50);

			menuSection.set(getAsRelative(itemTypePath), "DIAMOND_BLOCK");
			menuSection.set(getAsRelative(itemDisplayNamePath), "&c&lApply item: &f{name}");
			menuSection.set(getAsRelative(itemLorePath), List.of("&f{name} &f {boost}%"));
		});
	}

	public String getAugmentAppliedLore() {
		final String path = lorePath;
		final String lorePath = getAndReplace(path, getKey().getName());
		return configuration.getString(lorePath);
	}

	public List<String> getDescription() {
		final String path = descriptionPath;
		final String descriptionPath = getAndReplace(path, getKey().getName());
		return configuration.getStringList(descriptionPath);
	}

	public long getMaxBoost() {
		final String path = maxBoostPath;
		final String maxBoostPath = getAndReplace(path, getKey().getName());
		return configuration.getLong(maxBoostPath);
	}

	public long getMinBoost() {
		final String path = minBoostPath;
		final String minBoostPath = getAndReplace(path, getKey().getName());
		return configuration.getLong(minBoostPath);
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
