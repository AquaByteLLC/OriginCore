package settings.impl.builder;

import api.option.SettingsOption;
import settings.impl.option.Option;

public class OptionsBuilder implements api.builder.OptionsBuilder {

	private String name, description;

	@Override
	public api.builder.OptionsBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public api.builder.OptionsBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public SettingsOption build() {
		return new Option(name, description);
	}
}
