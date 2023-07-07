package settings.registry;

import settings.setting.Setting;

public interface SettingsRegistry {

	void createSetting(Setting setting);

	void deleteSetting(Setting setting);

}
