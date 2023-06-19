package enchants.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import enchants.EnchantAPI;
import enchants.item.EnchantedItem;
import me.lucko.helper.Commands;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class EnchantPlugin extends ExtendedJavaPlugin {
	private static Injector injector;

	@Override
	protected void enable() {
		injector = Guice.createInjector(new EnchantPluginModule(this));

		EventRegistry registry = CommonsPlugin.commons().getEventRegistry();
		EnchantTypes.bind(this, registry);

		Commands.create().assertPermission("test.giveEnchant").assertPlayer().handler(handler -> {
			final String arg1 = handler.arg(0).parseOrFail(String.class);
			final int level = handler.arg(1).parseOrFail(Integer.class);

			final ItemStackBuilder builder = ItemStackBuilder.of(Material.STONE_AXE).transformMeta(itemMeta -> itemMeta.setLore(List.of("TESTING")));

			final EnchantedItem item = new EnchantedItem(builder.build());

			item.addEnchant(EnchantTypes.valueOf(arg1).getEnchant());
			item.updateEnchant(EnchantTypes.valueOf(arg1).getEnchant(), level);

			item.giveItem(handler.sender());

		}).register("origintest");
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
