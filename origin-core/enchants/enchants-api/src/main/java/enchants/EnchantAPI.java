package enchants;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class EnchantAPI {
	private static Injector injector;

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The EnchantAPI hasn't been initialized anywhere. Create a new instance of the EnchantAPI class in the 'onEnable' method.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	private static YamlConfiguration generalConfig;

	public static YamlConfiguration getGeneralConfig() {
		if (generalConfig == null) {
			try {
				throw new Exception("The EnchantAPI hasn't been initialized anywhere. Create a new instance of the EnchantAPI class in the 'onEnable' method.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return generalConfig;
	}


	public EnchantAPI(final JavaPlugin javaPlugin) {
		injector = Guice.createInjector(new EnchantModule(javaPlugin));
		generalConfig = YamlConfiguration.loadConfiguration(new File(javaPlugin.getDataFolder(), "general.yml"));
	}

	static class EnchantModule extends AbstractModule {
		private final JavaPlugin plugin;

		EnchantModule(final JavaPlugin plugin) {
			this.plugin = plugin;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
		}
	}
}
