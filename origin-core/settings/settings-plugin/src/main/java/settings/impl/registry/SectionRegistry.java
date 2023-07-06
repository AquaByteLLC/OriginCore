package settings.impl.registry;

import api.registry.SettingsRegistry;
import api.section.SettingSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;

public class SectionRegistry implements api.registry.SectionRegistry {

	private final IdentityHashMap<JavaPlugin, SettingSection> sectionMap;

	public SectionRegistry() {
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
	public SettingsRegistry get(JavaPlugin plugin) {
		return this.sectionMap.get(plugin).getRegistry();
	}

	@Override
	public @NotNull IdentityHashMap<JavaPlugin, SettingSection> getSectionRegistry() {
		return this.sectionMap;
	}
}
