package settings.setting.builder;

import settings.setting.SettingOption;

public interface OptionBuilder {

	OptionBuilder setName(String name);

	OptionBuilder setDescription(String description);

	SettingOption build();

}
