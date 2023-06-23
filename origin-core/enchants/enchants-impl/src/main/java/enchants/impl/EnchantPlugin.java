package enchants.impl;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import enchants.EnchantAPI;
import enchants.config.EnchantsConfig;
import enchants.impl.commands.EnchantCommands;
import enchants.impl.type.EnchantTypes;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantPlugin extends ExtendedJavaPlugin implements ResourceProvider {
	private static Injector injector;

	private LiteConfig lfc;

	@Override
	protected void load() {
		lfc = new LiteConfig(this);
		injector = Guice.createInjector(new EnchantPluginModule(this, lfc));
	}

	@Override
	protected void enable() {
		lfc.register(EnchantsConfig.class, EnchantsConfig::new);
		lfc.reload();

		EventRegistry registry = CommonsPlugin.commons().getEventRegistry();
		EnchantTypes.registerAll(registry);

		PaperCommandManager commands = new PaperCommandManager(this);
		commands.registerCommand(new EnchantCommands(this));
	}

	@Override
	protected void disable() {

	}

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The EnchantPlugin hasn't been initialized.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	static class EnchantPluginModule extends AbstractModule {
		private final JavaPlugin plugin;
		private final EnchantAPI enchantAPI;

		@SuppressWarnings("all")
		public EnchantPluginModule(final JavaPlugin plugin, ConfigurationProvider conf) {
			this.plugin = plugin;
			this.enchantAPI = new EnchantAPI(plugin, conf);
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(EnchantAPI.class).toInstance(enchantAPI);
		}
	}
}
