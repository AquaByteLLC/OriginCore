package enchants.impl;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import enchants.EnchantAPI;
import enchants.impl.commands.EnchantCommands;
import enchants.impl.type.EnchantTypes;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantPlugin extends ExtendedJavaPlugin implements ResourceProvider {
	private static Injector injector;
	private static LiteConfig lfc;

	@Override
	protected void enable() {
		injector = Guice.createInjector(new EnchantPluginModule(this));

		EventRegistry registry = CommonsPlugin.commons().getEventRegistry();
		new EnchantTypes(this, registry);

		PaperCommandManager commands = new PaperCommandManager(this);
		commands.registerCommand(new EnchantCommands());
	}

	@Override
	protected void disable() {
		saveConfig();
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
		public EnchantPluginModule(final JavaPlugin plugin) {
			this.plugin = plugin;
			this.enchantAPI = new EnchantAPI(plugin);
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(EnchantAPI.class).toInstance(enchantAPI);
		}
	}
}
