package settings.registry;

import settings.Setting;

public interface SettingsRegistry {

	void createSetting(Setting setting);

	void deleteSetting(Setting setting);

}
