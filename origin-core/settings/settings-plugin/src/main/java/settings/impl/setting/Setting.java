package settings.impl.setting;

import api.option.SettingsOption;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Setting implements api.Setting {
	private final String settingName;
	private final ItemStack menuItem;
	private final List<SettingsOption> options;
	private final List<String> description;
	private SettingsOption defaultOption;

	public Setting(String settingName, ItemStack menuItem, List<String> description) {
		this.settingName = settingName;
		this.menuItem = menuItem;
		this.description = description;
		this.options = new ArrayList<>();
	}


	@Override
	public String getName() {
		return this.settingName;
	}

	@Override
	public ItemStack getMenuItem() {
		return this.menuItem;
	}

	@Override
	public List<String> getDescription() {
		return this.description;
	}

	@Override
	public List<SettingsOption> getOptions() {
		return this.options;
	}

	@Override
	public SettingsOption getDefaultOption() {
		return this.defaultOption;
	}

	@Override
	public void setDefaultOption(SettingsOption option) {
		this.defaultOption = option;
	}
}
