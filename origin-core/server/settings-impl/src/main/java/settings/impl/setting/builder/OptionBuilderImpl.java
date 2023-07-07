package settings.impl.setting.builder;

import settings.impl.setting.MenuOption;
import settings.impl.setting.key.LKey;
import settings.setting.key.GlobalKey;
import settings.setting.SettingOption;
import settings.setting.builder.OptionBuilder;

public class OptionBuilderImpl implements OptionBuilder {

	private String name;
	private String description;

	OptionBuilderImpl() {}

	@Override
	public OptionBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public OptionBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public SettingOption build() {
		return new MenuOption(LKey.convert(name), name, description);
	}

}
