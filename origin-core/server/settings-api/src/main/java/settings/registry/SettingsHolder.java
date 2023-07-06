package settings.registry;

import settings.Setting;
import settings.option.SettingsOption;
import settings.section.SettingSection;

import java.util.List;

public interface SettingsHolder {

	List<Setting> getLocalSettings(SettingSection section);

	void updateOption(Setting setting, SettingAction action);

	void setOption(Setting setting, SettingsOption option);

	SettingsOption getOption(Setting setting);

	enum SettingAction {
		PREV,
		NEXT,
		DEFAULT
	}

}
