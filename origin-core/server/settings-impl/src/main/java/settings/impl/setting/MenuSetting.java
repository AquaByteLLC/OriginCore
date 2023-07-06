package settings.impl.setting;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import settings.Setting;
import settings.option.SettingsOption;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantValue")
public class MenuSetting implements Setting {

	private final String settingName;
	private final ItemStack menuItem;
	private final List<SettingsOption> options;
	private final List<String> description;

	public MenuSetting(String settingName, ItemStack menuItem, List<String> description) {
		this.settingName = settingName;
		this.menuItem    = menuItem;
		this.description = description;
		this.options     = new ArrayList<>();
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

	private SettingsOption defaultOption;

	@Override
	public SettingsOption getDefaultOption() {
		return this.defaultOption;
	}

	@Override
	public void setDefaultOption(@NotNull SettingsOption option) {
		if(option == null)
			throw new NullPointerException("option");
		this.defaultOption = option;
	}

}
