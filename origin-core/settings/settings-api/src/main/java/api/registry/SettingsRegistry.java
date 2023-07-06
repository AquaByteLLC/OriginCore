package api.registry;

import api.Setting;
import api.option.SettingsOption;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface SettingsRegistry {
	void createSetting(Setting setting);

	void deleteSetting(Setting setting);

	@NotNull HashMap<Setting, SettingsOption> getDefaultSettings();
}
