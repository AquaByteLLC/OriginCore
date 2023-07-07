package settings.impl.setting;

import commons.util.collection.MapStream;
import me.vadim.util.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import settings.Settings;
import settings.setting.Setting;
import settings.setting.SettingOption;
import settings.setting.SettingSection;
import settings.setting.key.GlobalKey;
import settings.setting.key.SettingKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuSection extends Keyed<GlobalKey> implements SettingSection {

	private final String name;
	private final ItemStack stack;
	private final List<String> description;
	private final Map<GlobalKey, Setting> settings = new HashMap<>();

	public MenuSection(GlobalKey key, String name, ItemStack stack, List<String> description) {
		super(key);
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
		settings.put(setting.getKey(), setting);
	}

	@Override
	public void deleteSetting(Setting setting) {
		Settings.api().getGlobal().deleteSetting(setting);
		settings.remove(setting.getKey());
	}

	@Override
	public @Nullable Setting querySetting(GlobalKey key) {
		return settings.get(key);
	}

	@Override
	public @NotNull Map<Setting, SettingOption> getDefaultSettings() {
		return MapStream.of(settings).mapKey(this::querySetting).mapValue(Setting::getDefaultOption).toMap();
	}

}
