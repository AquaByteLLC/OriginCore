package settings.impl.registry;

import api.Setting;
import api.option.SettingsOption;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SettingsRegistry implements api.registry.SettingsRegistry {

	private final HashMap<Setting, SettingsOption> settings;

	public SettingsRegistry() {
		settings = new HashMap<>();
	}

	@Override
	public void createSetting(Setting setting) {
		settings.put(setting, setting.getDefaultOption());
	}

	@Override
	public void deleteSetting(Setting setting) {
		settings.remove(setting);
	}

	@Override
	public @NotNull HashMap<Setting, SettingsOption> getDefaultSettings() {
		return this.settings;
	}
}
