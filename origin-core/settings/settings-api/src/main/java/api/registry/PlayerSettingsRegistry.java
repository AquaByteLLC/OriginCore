package api.registry;

import api.Setting;
import api.option.SettingsOption;

import java.util.HashMap;

public interface PlayerSettingsRegistry {
	HashMap<Setting, SettingsOption> getPlayerSettings();

	void updateSetting(Setting setting, SettingAction action);

	void initSettings();

	public enum SettingAction {
		PREV,
		NEXT,
		DEFAULT
	}
}
