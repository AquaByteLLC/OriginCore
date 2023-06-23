package enchants;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import enchants.config.EnchantsConfig;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.plugin.java.JavaPlugin;

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

	public EnchantAPI(final JavaPlugin javaPlugin, ConfigurationProvider conf) {
		injector = Guice.createInjector(new EnchantModule(javaPlugin, conf));
	}

	static class EnchantModule extends AbstractModule {
		private final JavaPlugin plugin;
		private final ConfigurationProvider conf;

		EnchantModule(final JavaPlugin plugin, ConfigurationProvider conf) {
			this.plugin = plugin;
			this.conf = conf;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(EnchantsConfig.class).toProvider(() -> conf.open(EnchantsConfig.class));
		}
	}
}
