package tools.impl.conf;

import commons.conf.SettableConfig;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.AttributeKey;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

public class AttributeConfiguration {

	@Getter private static final HashMap<AttributeKey, FileConfiguration> configurations = new HashMap<>();
	@Getter private final SettableConfig conf;
	@Getter private final AttributeKey key;

	public AttributeConfiguration(AttributeKey key, String parent) {
		this.key = key;
		this.conf = new SettableConfig(key.getName() + ".yml", parent, ToolsPlugin.getPlugin());
		configurations.put(key, conf.getFileConfiguration());
	}

	public void writeAndSave(Consumer<FileConfiguration> consumer) {
		consumer.accept(conf.getFileConfiguration());

		try {
			conf.getFileConfiguration().save(conf.getFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public ConfigurationSection createAndGetSection(String sectionPath) {
		final String replaced = getAndReplace(sectionPath, key.getName());
		return getOrCreate(conf.getFileConfiguration(), replaced);
	}

	public static String getAndReplace(String current, String key) {
		return current.replaceAll("%key%", key);
	}

	public static String getAsRelative(String global) {
		return global.substring(global.lastIndexOf('.') + 1);
	}

	public static ConfigurationSection getOrCreate(ConfigurationSection section, String path) {
		return section.isConfigurationSection(path) ? section.getConfigurationSection(path) : section.createSection(path);
	}

	public void setAndSave(ConfigurationSection section, String path, Object data) {
		section.set(path, data);

		try {
			conf.getFileConfiguration().save(conf.getFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
