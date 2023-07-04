package mining;

import blocks.impl.anim.block.BlockAnimHelper;
import blocks.impl.registry.ProgressRegistryImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.lucko.helper.Schedulers;
import me.lucko.helper.bossbar.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MiningAPI {
	private static Injector injector;
	private static ProgressRegistryImpl progressRegistry;
	private static YamlConfiguration generalConfig;
	private static BlockAnimHelper helper;

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The MiningAPI hasn't been initialized.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	public static YamlConfiguration getGeneralConfig() {
		if (generalConfig == null)
			throw new RuntimeException("The MiningAPI hasn't been initialized anywhere. Create a new instance of the MiningAPI class in the 'onEnable' method.");
		return generalConfig;
	}

	public static BlockAnimHelper getHelper() {
		if (helper == null)
			throw new RuntimeException("The MiningAPI hasn't been initialized correctly. Create a new instance of the MiningAPI class in the 'onEnable' method and map the helper.");
		return helper;
	}

	public MiningAPI(JavaPlugin javaPlugin) {
		injector = Guice.createInjector(new MiningModule(javaPlugin));
		generalConfig = YamlConfiguration.loadConfiguration(new File(javaPlugin.getDataFolder(), "general.yml"));
	}

	public static void map(BossBar bossBar) {
		helper = new BlockAnimHelper();
		Schedulers.bukkit().runTaskTimer(injector.getInstance(JavaPlugin.class), $ -> {
			helper.progression();
		}, 1, 1);
	}

	protected static class MiningModule extends AbstractModule {
		private final JavaPlugin plugin;

		MiningModule(JavaPlugin plugin) {
			this.plugin = plugin;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
		}
	}
}

