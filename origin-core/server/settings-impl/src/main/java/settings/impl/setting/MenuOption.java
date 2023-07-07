package settings.impl.setting;

import settings.setting.SettingOption;
import settings.setting.key.LocalKey;

public class MenuOption extends Keyed<LocalKey> implements SettingOption {

	private final String optionName;
	private final String optionDescription;

	public MenuOption(LocalKey key, String optionName, String optionDescription) {
		super(key);
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
