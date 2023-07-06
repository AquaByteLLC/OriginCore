package api.registry;

import api.section.SettingSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;

public interface SectionRegistry {

	void createSection(JavaPlugin plugin, SettingSection section);

	void deleteSection(JavaPlugin plugin);

	SettingsRegistry get(JavaPlugin plugin);

	@NotNull IdentityHashMap<JavaPlugin, SettingSection> getSectionRegistry();
}
