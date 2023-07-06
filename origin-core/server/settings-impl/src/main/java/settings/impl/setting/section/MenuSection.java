package settings.impl.setting.section;

import me.vadim.util.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import settings.Setting;
import settings.Settings;
import settings.option.SettingsOption;
import settings.section.SettingSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuSection implements SettingSection {

	private final String name;
	private final ItemStack stack;
	private final List<String> description;
	private final Map<Setting, SettingsOption> settings = new HashMap<>();

	public MenuSection(String name, ItemStack stack, List<String> description) {
		this.name        = name;
		this.stack       = ItemBuilder.copy(stack).displayName(name).lore(description).build();
		this.description = description;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ItemStack getMenuItem() {
		return this.stack;
	}

	@Override
	public List<String> getDescription() {
		return this.description;
	}

	@Override
	public void createSetting(Setting setting) {
		Settings.api().getGlobal().createSetting(setting);
		settings.put(setting, setting.getDefaultOption());
	}

	@Override
	public void deleteSetting(Setting setting) {
		Settings.api().getGlobal().deleteSetting(setting);
		settings.remove(setting);
	}

	@Override
	public @NotNull Map<Setting, SettingsOption> getDefaultSettings() {
		return new HashMap<>(settings);
	}

}
