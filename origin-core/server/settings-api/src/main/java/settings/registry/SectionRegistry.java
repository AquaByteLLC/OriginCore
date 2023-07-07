package settings.registry;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import settings.setting.SettingSection;
import settings.setting.key.GlobalKey;

public interface SectionRegistry {

	void createSection(JavaPlugin plugin, SettingSection section);

	void deleteSection(JavaPlugin plugin);

	@Nullable SettingSection getForPlugin(JavaPlugin plugin);

	@Nullable SettingSection getByKey(GlobalKey key);

	SettingSection[] getSections();

	/**
	 * Reset the {@linkplain SettingsHolder holder} to the {@linkplain SettingSection#getDefaultSettings() default settings} for each {@linkplain SettingSection section}.
	 *
	 * @param holder the {@link SettingsHolder} to flash
	 */
	void flash(SettingsHolder holder);

}