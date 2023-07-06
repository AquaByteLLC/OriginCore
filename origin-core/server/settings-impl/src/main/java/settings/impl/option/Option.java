package settings.impl.option;

import settings.option.SettingsOption;

public class Option implements SettingsOption {

	private final String optionName;
	private final String optionDescription;

	public Option(String optionName, String optionDescription) {
		this.optionName        = optionName;
		this.optionDescription = optionDescription;
	}

	@Override
	public String getName() {
		return this.optionName;
	}

	@Override
	public String getDescription() {
		return this.optionDescription;
	}

	public String getOptionName() {
		return optionName;
	}

}
