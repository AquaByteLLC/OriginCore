package api.builder;

import api.registry.SettingsRegistry;
import api.section.SettingSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface SectionBuilder {
	SectionBuilder setMenuItem(ItemStack menuItem);

	SectionBuilder setName(String name);

	SectionBuilder setDescription(List<String> description);

	SectionBuilder setRegistry(SettingsRegistry settingsRegistry);

	SettingSection build();
}
