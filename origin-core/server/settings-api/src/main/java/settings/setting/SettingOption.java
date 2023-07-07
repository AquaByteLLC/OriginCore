package settings.setting;

import settings.setting.key.KeyedSetting;
import settings.setting.key.LocalKey;

public interface SettingOption extends KeyedSetting {

	@Override
	LocalKey getKey();

	String getName();

	String getDescription();

}