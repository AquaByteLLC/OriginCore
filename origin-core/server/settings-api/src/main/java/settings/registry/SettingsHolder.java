package settings.registry;

import settings.setting.Setting;
import settings.setting.SettingOption;
import settings.setting.SettingSection;

import java.util.List;

public interface SettingsHolder {

	List<Setting> getLocalSettings(SettingSection section);

	void updateOption(Setting setting, SettingAction action);

	void setOption(Setting setting, SettingOption option);

	SettingOption getOption(Setting setting);

	enum SettingAction {
		PREV,
		NEXT,
		DEFAULT
	}

}
