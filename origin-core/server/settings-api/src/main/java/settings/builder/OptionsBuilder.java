package settings.builder;

import settings.option.SettingsOption;

public interface OptionsBuilder {

	OptionsBuilder setName(String name);

	OptionsBuilder setDescription(String description);

	SettingsOption build();

}
