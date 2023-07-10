package settings.impl.registry;

import commons.impl.data.PlayerOwned;
import settings.Settings;
import settings.setting.Setting;
import settings.setting.SettingOption;
import settings.registry.SettingsHolder;
import settings.setting.SettingSection;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerSettingsHolder extends PlayerOwned implements SettingsHolder {

	private final Map<Setting, SettingOption> playerSettings = new HashMap<>();

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
		final List<SettingOption> options       = setting.getOptions();
		final SettingOption       currentOption = playerSettings.get(setting);

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
	public void setOption(Setting setting, SettingOption option) {
		playerSettings.put(setting, option);
	}

	@Override
	public SettingOption getOption(Setting setting) {
		return playerSettings.get(setting);
	}

}