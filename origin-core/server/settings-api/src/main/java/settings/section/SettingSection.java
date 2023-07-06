package settings.section;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import settings.Setting;
import settings.option.SettingsOption;
import settings.registry.SettingsRegistry;

import java.util.List;
import java.util.Map;

public interface SettingSection {

	String getName();

	ItemStack getMenuItem();

	List<String> getDescription();

	void createSetting(Setting setting);

	void deleteSetting(Setting setting);

	@NotNull Map<Setting, SettingsOption> getDefaultSettings();

}
