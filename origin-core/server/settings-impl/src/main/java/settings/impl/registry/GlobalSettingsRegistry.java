package settings.impl.registry;

import settings.setting.Setting;
import settings.setting.SettingOption;
import settings.registry.SettingsRegistry;

import java.util.HashMap;
import java.util.Map;

public class GlobalSettingsRegistry implements SettingsRegistry {

	private final Map<Setting, SettingOption> settings = new HashMap<>();

	@Override
	public void createSetting(Setting setting) {
		settings.put(setting, setting.getDefaultOption());
	}

	@Override
	public void deleteSetting(Setting setting) {
		settings.remove(setting);
	}

}
