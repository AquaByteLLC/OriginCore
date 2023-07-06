package settings.impl.registry;

import org.jetbrains.annotations.NotNull;
import settings.Setting;
import settings.Settings;
import settings.option.SettingsOption;
import settings.registry.SettingsRegistry;

import java.util.HashMap;
import java.util.Map;

public class GlobalSettingsRegistry implements SettingsRegistry {

	private final Map<Setting, SettingsOption> settings = new HashMap<>();

	@Override
	public void createSetting(Setting setting) {
		settings.put(setting, setting.getDefaultOption());
	}

	@Override
	public void deleteSetting(Setting setting) {
		settings.remove(setting);
	}

}
