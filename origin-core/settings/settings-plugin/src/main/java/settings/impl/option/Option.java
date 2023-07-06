package settings.impl.option;

import api.option.SettingsOption;

public class Option implements SettingsOption {
	private final String optionName;
	private final String optionDescription;
	private boolean isActive;

	public Option(String optionName, String optionDescription) {
		this.optionName = optionName;
		this.optionDescription = optionDescription;
		this.isActive = false;
	}

	@Override
	public String getName() {
		return this.optionName;
	}

	@Override
	public String getDescription() {
		return this.optionDescription;
	}

	@Override
	public void activate() {
		this.isActive = true;
	}

	@Override
	public void deactivate() {
		this.isActive = false;
	}

	public String getOptionName() {
		return optionName;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}
}
