package settings.impl.builder;

import settings.builder.OptionsBuilder;
import settings.impl.option.Option;
import settings.option.SettingsOption;

public class OptionsBuilderImpl implements OptionsBuilder {

	private String name, description;

	OptionsBuilderImpl() { }

	@Override
	public OptionsBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public OptionsBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public SettingsOption build() {
		return new Option(name, description);
	}

}
