package blocks;

import blocks.registry.BlockRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class BlocksAPI {
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

	public BlocksAPI(JavaPlugin javaPlugin) {
		injector = Guice.createInjector(new BlocksModule(javaPlugin, new BlockRegistry()));
		generalConfig = YamlConfiguration.loadConfiguration(new File(javaPlugin.getDataFolder(), "general.yml"));
	}

	static class BlocksModule extends AbstractModule {
		private final JavaPlugin plugin;
		private final BlockRegistry registry;

		BlocksModule(JavaPlugin plugin, BlockRegistry registry) {
			this.plugin = plugin;
			this.registry = registry;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(BlockRegistry.class).toInstance(registry);
		}
	}
}
