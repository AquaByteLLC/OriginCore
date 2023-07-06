package settings.builder;

import org.bukkit.inventory.ItemStack;
import settings.Setting;
import settings.registry.SettingsRegistry;
import settings.section.SettingSection;

import java.util.List;

public interface SectionBuilder {

	SectionBuilder setMenuItem(ItemStack menuItem);

	SectionBuilder setName(String name);

	SectionBuilder setDescription(String... description);

	SectionBuilder addSetting(Setting setting);

	SettingSection build();

}
