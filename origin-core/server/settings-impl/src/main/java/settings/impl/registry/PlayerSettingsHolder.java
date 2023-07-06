package settings.impl.registry;

import commons.impl.PlayerOwned;
import settings.Setting;
import settings.Settings;
import settings.option.SettingsOption;
import settings.registry.SettingsHolder;
import settings.section.SettingSection;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerSettingsHolder extends PlayerOwned implements SettingsHolder {

	private final Map<Setting, SettingsOption> playerSettings = new HashMap<>();

	public PlayerSettingsHolder(UUID uuid) {
		super(uuid);
	}

	@Override
	public List<Setting> getLocalSettings(SettingSection section) {
		Set<Setting> inRegistry = section.getDefaultSettings().keySet();
		return playerSettings.keySet().stream().filter(inRegistry::contains).collect(Collectors.toList());
	}

	@Override
	public void updateOption(Setting setting, SettingAction action) {
		final List<SettingsOption> options = setting.getOptions();
		final SettingsOption currentOption = playerSettings.get(setting);

		int c = options.indexOf(currentOption);
		int i = -1;

		switch (action) {
			case NEXT -> {
				if ((i = c + 1) >= options.size())
					i = 0;
			}
			case PREV -> {
				if ((i = c - 1) < 0)
					i = options.size() - 1;
			}
			case DEFAULT -> playerSettings.put(setting, setting.getDefaultOption());
		}

		if (i != -1)
			playerSettings.put(setting, options.get(i));
	}

	@Override
	public void setOption(Setting setting, SettingsOption option) {
		playerSettings.put(setting, option);
	}

	@Override
	public SettingsOption getOption(Setting setting) {
		return playerSettings.get(setting);
	}

}