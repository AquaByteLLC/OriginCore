package enchants.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import enchants.EnchantAPI;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantPlugin extends ExtendedJavaPlugin {
	private static Injector injector;

	private EventsDemo demo;
	@Override
	protected void enable() {
		injector = Guice.createInjector(new EnchantPluginModule(this));

		EventRegistry registry = CommonsPlugin.commons().getEventRegistry();
		demo = new EventsDemo(registry); // make sure you save the reference, because if it gets GC'd then the registry will auto-remove your subscription
		EventsDemoEnum.bind(this, registry);
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
