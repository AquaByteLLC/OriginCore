package tools.impl.attribute.skins.impl;

import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.conf.AttributeConfiguration;

import java.util.List;

import static tools.impl.attribute.skins.impl.SkinConfiguration.SkinConfigPaths.*;

public class SkinConfiguration extends AttributeConfiguration {
	public interface SkinConfigPaths {

		// BASES
		String skins = "Skins";
		String base = skins + ".%key%";
		String menuSectionPath = base + ".menuItem";
		String modelSectionPath = base + ".model";

		// PATHS
		String lorePath = base + ".appliedLore";
		String descriptionPath = base + ".description";

		// MODEL SECTION STUFF
		String modelDataPath = modelSectionPath + ".data";

		// MENU SECTION STUFF
		String itemDisplayNamePath = menuSectionPath + ".displayName";
		String itemTypePath = menuSectionPath + ".type";
		String itemLorePath = menuSectionPath + ".lore";
	}

	private final FileConfiguration configuration;

	public SkinConfiguration(AttributeKey key) {
		super(key, "skins");
		this.configuration = getConf().getFileConfiguration();

		final String menuSectionReplaced = getAndReplace(menuSectionPath, key.getName());
		final String modelSectionReplaced = getAndReplace(modelSectionPath, key.getName());
		final String baseSectionReplaced = getAndReplace(base, key.getName());
		final String skinsSectionReplaced = skins;

		if (configuration.isConfigurationSection(skinsSectionReplaced)) return;

		writeAndSave(file -> {
			final ConfigurationSection skinsSection = getOrCreate(configuration, skinsSectionReplaced);
			final ConfigurationSection baseSection = getOrCreate(configuration, baseSectionReplaced);
			final ConfigurationSection modelSection = getOrCreate(configuration, modelSectionReplaced);
			final ConfigurationSection menuSection = getOrCreate(configuration, menuSectionReplaced);

			baseSection.set(getAsRelative(lorePath), "&7[&f{name} &eis currently applied &7]");
			baseSection.set(getAsRelative(descriptionPath), List.of("Description of the skin", "This skin is super cool"));

			modelSection.set(getAsRelative(modelDataPath), 50);

			menuSection.set(getAsRelative(itemTypePath), "DIAMOND_BLOCK");
			menuSection.set(getAsRelative(itemDisplayNamePath), "&c&lApply item: &f{name}");
			menuSection.set(getAsRelative(itemLorePath), List.of("&f{name} &f {information}%"));
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

	public int getModelData() {
		final String modelData = getAndReplace(modelDataPath, getKey().getName());
		return configuration.getInt(modelData);
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
