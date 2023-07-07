package settings.impl.registry;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import settings.registry.SectionRegistry;
import settings.registry.SettingsHolder;
import settings.setting.SettingSection;
import settings.setting.key.GlobalKey;
import settings.setting.key.SettingKey;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class PluginSectionRegistry implements SectionRegistry {

	private final Map<JavaPlugin, SettingSection> byPlugin = new IdentityHashMap<>();
	private final Map<SettingKey, SettingSection> byKey = new HashMap<>();

	@Override
	public void createSection(JavaPlugin plugin, SettingSection section) {
		this.byPlugin.put(plugin, section);
		this.byKey.put(section.getKey(), section);
	}

	@Override
	public void deleteSection(JavaPlugin plugin) {
		SettingSection section = this.byPlugin.remove(plugin);
		if(section != null)
			this.byKey.remove(section.getKey());
	}

	@Override
	public @Nullable SettingSection getForPlugin(JavaPlugin plugin) {
		return this.byPlugin.get(plugin);
	}

	@Override
	public @Nullable SettingSection getByKey(GlobalKey key) {
		return this.byKey.get(key);
	}

	@Override
	public SettingSection[] getSections() {
		return byPlugin.values().toArray(SettingSection[]::new);
	}

	@Override
	public void flash(SettingsHolder holder) {
		byPlugin.forEach((pl, ss) -> ss.getDefaultSettings().forEach(holder::setOption));
	}

}
