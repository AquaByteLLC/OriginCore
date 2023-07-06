package settings.builder;

import org.bukkit.inventory.ItemStack;
import settings.Setting;
import settings.option.SettingsOption;

public interface SettingBuilder {

	SettingBuilder setName(String name);

	SettingBuilder setMenuItem(ItemStack stack);

	SettingBuilder setDescription(String... description);

	SettingBuilder addOptions(SettingsOption... options);

	SettingBuilder setDefaultOption(int optionIndex);

	Setting build();

}
