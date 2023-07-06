package settings.registry;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import settings.section.SettingSection;

public interface SectionRegistry {

	void createSection(JavaPlugin plugin, SettingSection section);

	void deleteSection(JavaPlugin plugin);

	@Nullable SettingSection getForPlugin(JavaPlugin plugin);

	SettingSection[] getAllSections();

	/**
	 * Reset the {@linkplain SettingsHolder holder} to the {@linkplain SettingsRegistry#getDefaultSettings() default settings} for each {@linkplain SettingSection section}.
	 *
	 * @param holder the {@link SettingsHolder} to flash
	 */
	void flash(SettingsHolder holder);

}