package enchants.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import commons.impl.account.PlayerDefaultAccount;
import enchants.EnchantAPI;
import enchants.impl.config.EnchantsConfig;
import enchants.impl.type.EnchantTypes;
import enchants.item.EnchantedItem;
import me.lucko.helper.Commands;
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

		lfc = new LiteConfig(this);
		lfc.register(EnchantsConfig.class, EnchantsConfig::new);
		lfc.reload();

		EventRegistry registry = CommonsPlugin.commons().getEventRegistry();
		new EnchantTypes(this, registry);

		Commands.create().assertPermission("test.giveEnchant").assertPlayer().handler(handler -> {
			final String arg1 = handler.arg(0).parseOrFail(String.class);
			final int level = handler.arg(1).parseOrFail(Integer.class);

			final EnchantedItem item = new EnchantedItem(handler.sender().getInventory().getItemInMainHand());
			item.addEnchant(EnchantTypes.SPEED_ENCHANT_KEY, level);

			final CommonsPlugin plugin = CommonsPlugin.commons();
			final PlayerDefaultAccount account = plugin.getDataStorage().getAccount(handler.sender().getUniqueId());
			account.tokenCount += 10;
			System.out.println(account.tokenCount);
		}).register("origintest");
	}

	@Override
	protected void disable() {
		saveConfig();

	}

	public static EnchantsConfig getEnchantsConfig() {
		return lfc.open(EnchantsConfig.class);
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
