package enchants.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import commons.events.impl.bukkit.BukkitEventSubscriber;
import enchants.EnchantAPI;
import enchants.item.EnchantedItem;
import me.lucko.helper.Commands;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.text3.Text;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class EnchantPlugin extends ExtendedJavaPlugin {
	private static Injector injector;

	@Override
	protected void enable() {
		injector = Guice.createInjector(new EnchantPluginModule(this));

		EventRegistry registry = CommonsPlugin.commons().getEventRegistry();
		EnchantTypes.bind(this, registry);

		new BukkitEventSubscriber<>(PlayerJoinEvent.class, event -> {
			final ItemStackBuilder builder = ItemStackBuilder.of(Material.STONE_AXE);
			builder.transformMeta(meta -> {
				meta.setLore(List.of(Text.colorize("&c&fTesting the lore")));
				meta.addEnchant(Enchantment.ARROW_INFINITE, 2, true);
			});
			event.getPlayer().getInventory().addItem(builder.build());
		}).bind(this, registry);

		Commands.create().assertPermission("test.giveEnchant").assertPlayer().handler(handler -> {
			final String arg1 = handler.arg(0).parseOrFail(String.class);
			final int level = handler.arg(1).parseOrFail(Integer.class);
			final EnchantedItem item = new EnchantedItem(handler.sender().getInventory().getItemInMainHand());
			item.addEnchant(EnchantTypes.SPEED_ENCHANT.getEnchant().getKey(), level);
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
