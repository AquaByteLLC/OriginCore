package commons.conf;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

import static commons.util.ReflectUtil.sneaky;

public class SettableConfig {
	@Getter private final File file, parentFolder;
	private final YamlConfiguration fileConfiguration;


	public SettableConfig(String fileName, String parent, JavaPlugin plugin) {
		this.parentFolder = new File(plugin.getDataFolder(), parent);
		this.file = new File(parentFolder, fileName);
		this.fileConfiguration = YamlConfiguration.loadConfiguration(file);
		create();
	}

	public YamlConfiguration getFileConfiguration() {
		return this.fileConfiguration;
	}

	public void create() {
		try {
			if (!this.file.exists()) {
				File parent = this.file.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}

				this.file.createNewFile();
			}
		} catch (IOException var2) {
			sneaky(var2);
		}

	}
}
