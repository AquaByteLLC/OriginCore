package enchants.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import enchants.EnchantAPI;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.plugin.java.JavaPlugin;
import originmc.PacketAPI;

public class EnchantPlugin extends ExtendedJavaPlugin {
	private static Injector injector;

	@Override
	protected void enable() {
		injector = Guice.createInjector(new EnchantPluginModule(this));
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
		private final PacketAPI packetAPI;
		private final EnchantAPI enchantAPI;

		@SuppressWarnings("all")
		public EnchantPluginModule(final JavaPlugin plugin) {
			this.plugin = plugin;
			this.packetAPI = new PacketAPI(plugin);
			this.enchantAPI = new EnchantAPI(plugin);
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(EnchantAPI.class).toInstance(enchantAPI);
			this.bind(PacketAPI.class).toInstance(packetAPI);
		}
	}
}
