package settings.setting.builder;

import org.bukkit.inventory.ItemStack;
import settings.setting.Setting;
import settings.setting.SettingOption;

public interface SettingBuilder {

	SettingBuilder setName(String name);

	SettingBuilder setMenuItem(ItemStack stack);

	SettingBuilder setDescription(String... description);

	SettingBuilder addOptions(SettingOption... options);

	SettingBuilder setDefaultOption(int optionIndex);

	Setting build();

}
