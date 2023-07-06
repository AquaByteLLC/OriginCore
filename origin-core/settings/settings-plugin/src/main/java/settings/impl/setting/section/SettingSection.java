package settings.impl.setting.section;

import api.registry.SettingsRegistry;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SettingSection implements api.section.SettingSection {

	private final String name;
	private final ItemStack stack;
	private final List<String> description;
	private final SettingsRegistry registry;

	public SettingSection(String name, ItemStack stack, List<String> description, SettingsRegistry registry) {
		this.name = name;
		this.stack = stack;
		this.description = description;
		this.registry = registry;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ItemStack getMenuItem() {
		return this.stack;
	}

	@Override
	public List<String> getDescription() {
		return this.description;
	}

	@Override
	public SettingsRegistry getRegistry() {
		return this.registry;
	}
}
