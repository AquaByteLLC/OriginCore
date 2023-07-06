package api.builder;

import api.option.SettingsOption;

public interface OptionsBuilder {
	OptionsBuilder setName(String name);

	OptionsBuilder setDescription(String description);

	SettingsOption build();
}
