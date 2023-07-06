package api.builder;

import api.Setting;
import api.option.SettingsOption;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface SettingsBuilder {
	SettingsBuilder setName(String name);

	SettingsBuilder setMenuItem(ItemStack stack);

	SettingsBuilder setDescription(List<String> description);

	SettingsBuilder setOptions(List<SettingsOption> options);

	SettingsBuilder setDefaultOption(SettingsOption option);

	Setting build();
}
