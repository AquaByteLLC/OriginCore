package settings.setting.builder;

import org.bukkit.inventory.ItemStack;
import settings.setting.Setting;
import settings.setting.SettingSection;

public interface SectionBuilder {

	SectionBuilder setMenuItem(ItemStack menuItem);

	SectionBuilder setName(String name);

	SectionBuilder setDescription(String... description);

	SectionBuilder addSetting(Setting setting);

	SettingSection build();

}
