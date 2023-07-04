package enchants;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import enchants.item.EnchantFactory;
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
		if (generalConfig == null)
			throw new RuntimeException("The EnchantAPI hasn't been initialized anywhere. Create a new instance of the EnchantAPI class in the 'onEnable' method.");
		return generalConfig;
	}

	public EnchantAPI(JavaPlugin javaPlugin, EnchantRegistry registry, EnchantFactory factory) {
		injector = Guice.createInjector(new EnchantModule(javaPlugin, registry, factory));
		generalConfig = YamlConfiguration.loadConfiguration(new File(javaPlugin.getDataFolder(), "general.yml"));
	}

	static class EnchantModule extends AbstractModule {
		private final JavaPlugin plugin;
		private final EnchantRegistry registry;
		private final EnchantFactory factory;

		EnchantModule(JavaPlugin plugin, EnchantRegistry registry, EnchantFactory factory) {
			this.plugin = plugin;
			this.registry = registry;
			this.factory = factory;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(EnchantRegistry.class).toInstance(registry);
			this.bind(EnchantFactory.class).toInstance(factory);
		}
	}
}
