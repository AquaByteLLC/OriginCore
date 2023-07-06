package settings.impl.registry;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import settings.registry.SectionRegistry;
import settings.registry.SettingsHolder;
import settings.section.SettingSection;

import java.util.IdentityHashMap;

public class PluginSectionRegistry implements SectionRegistry {

	private final IdentityHashMap<JavaPlugin, SettingSection> sectionMap;

	public PluginSectionRegistry() {
		sectionMap = new IdentityHashMap<>();
	}

	@Override
	public void createSection(JavaPlugin plugin, SettingSection section) {
		this.sectionMap.put(plugin, section);
	}

	@Override
	public void deleteSection(JavaPlugin plugin) {
		this.sectionMap.remove(plugin);
	}

	@Override
	public @Nullable SettingSection getForPlugin(JavaPlugin plugin) {
		return this.sectionMap.get(plugin);
	}

	@Override
	public SettingSection[] getAllSections() {
		return sectionMap.values().toArray(SettingSection[]::new);
	}

	@Override
	public void flash(SettingsHolder holder) {
		sectionMap.forEach((pl, ss) -> ss.getDefaultSettings().forEach(holder::setOption));
	}

}
