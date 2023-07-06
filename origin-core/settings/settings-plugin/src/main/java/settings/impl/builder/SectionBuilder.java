package settings.impl.builder;

import api.registry.SettingsRegistry;
import api.section.SettingSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SectionBuilder implements api.builder.SectionBuilder {
	private String name;
	private ItemStack menuItem;
	private List<String> description;
	private SettingsRegistry registry;

	@Override
	public api.builder.SectionBuilder setMenuItem(ItemStack menuItem) {
		this.menuItem = menuItem;
		return this;
	}

	@Override
	public api.builder.SectionBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public api.builder.SectionBuilder setDescription(List<String> description) {
		this.description = description;
		return this;
	}

	@Override
	public api.builder.SectionBuilder setRegistry(SettingsRegistry settingsRegistry) {
		this.registry = settingsRegistry;
		return this;
	}

	@Override
	public SettingSection build() {
		return new settings.impl.setting.section.SettingSection(name, menuItem, description, registry);
	}
}
