package farming.impl;
/*
import blocks.BlocksAPI;
import blocks.old.regeneration.BlockRegeneration;
import blocks.old.registry.BlockRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import farming.impl.conf.BlocksConfig;
import farming.impl.conf.GeneralConfig;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class FarmingPlugin extends ExtendedJavaPlugin implements ResourceProvider {

	private static Injector injector;
	private BlockRegistry registry;
	private LiteConfig lfc;

	@Override
	protected void enable() {
		EventRegistry events = CommonsPlugin.commons().getEventRegistry();
		lfc = new LiteConfig(this);
		injector = Guice.createInjector(new FarmingPluginModule(this, new BlocksAPI(this, lfc)));
		registry = BlocksAPI.getBlockRegistry();

		lfc.register(GeneralConfig.class, GeneralConfig::new);
		lfc.register(BlocksConfig.class, BlocksConfig::new);
		lfc.reload();
	}
	@Override
	protected void disable() {
		lfc.save();
		BlockRegeneration.cancelRegenerations();
	}

	public BlocksConfig getBlocksConfig() {
		return lfc.open(BlocksConfig.class);
	}

	public GeneralConfig getGeneralConfig() {
		return lfc.open(GeneralConfig.class);
	}

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The FarmingPlugin hasn't been initialized.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	static class FarmingPluginModule extends AbstractModule {
		private final JavaPlugin plugin;
		private final BlocksAPI blocksAPI;

		FarmingPluginModule(JavaPlugin plugin, BlocksAPI blocksApi) {
			this.plugin = plugin;
			this.blocksAPI = blocksApi;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(BlocksAPI.class).toInstance(blocksAPI);
		}
	}
}


 */