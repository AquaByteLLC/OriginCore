package settings.impl.registry;

import api.Setting;
import api.option.SettingsOption;
import lombok.Setter;
import settings.impl.SettingsPlugin;

import java.util.HashMap;
import java.util.List;

public class PlayerSettingsRegistry implements api.registry.PlayerSettingsRegistry {

	@Setter private HashMap<Setting, SettingsOption> playerSettings;

	public PlayerSettingsRegistry() {
		this.playerSettings = new HashMap<>();
		initSettings();
	}

	@Override
	public HashMap<Setting, SettingsOption> getPlayerSettings() {
		return this.playerSettings;
	}

	@Override
	public void updateSetting(Setting setting, SettingAction action) {
		final List<SettingsOption> options = setting.getOptions();
		final SettingsOption currentOption = playerSettings.get(setting);
		final int currentIndex = options.indexOf(currentOption);
		final int nextIndex = currentIndex + 1;
		final int prevIndex = currentIndex - 1;

		switch (action) {
			case NEXT -> {
				if (nextIndex <= options.size()) return;
				playerSettings.replace(setting, options.get(nextIndex));
			}
			case PREV -> {
				if (prevIndex <= options.size()) return;
				playerSettings.replace(setting, options.get(prevIndex));
			}
			case DEFAULT -> playerSettings.replace(setting, setting.getDefaultOption());
		}
	}

	@Override
	public void initSettings() {
		SettingsPlugin.getSectionRegistry().getSectionRegistry()
				.forEach((jp, settingSection) -> playerSettings.putAll(settingSection.getRegistry().getDefaultSettings()));
	}
}
