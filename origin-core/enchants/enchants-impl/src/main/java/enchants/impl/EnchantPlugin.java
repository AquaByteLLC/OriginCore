package enchants.impl;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import enchants.EnchantAPI;
import enchants.EnchantKey;
import enchants.EnchantRegistry;
import enchants.conf.EnchantmentConfiguration;
import enchants.impl.commands.EnchantCommand;
import enchants.impl.conf.GeneralConfig;
import enchants.impl.item.OriginEnchantFactory;
import enchants.impl.menu.EnchantMenu;
import enchants.item.Enchant;
import enchants.item.EnchantFactory;
import enchants.item.EnchantedItem;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantPlugin extends ExtendedJavaPlugin implements ResourceProvider {
	private static Injector injector;

	private LiteConfig lfc;
	private EnchantRegistry registry;
	private EnchantFactory factory;

	public EnchantRegistry getRegistry() {
		return registry;
	}

	public EnchantFactory getFactory() {
		return factory;
	}

	@Override
	protected void enable() {
		EventRegistry events = CommonsPlugin.commons().getEventRegistry();
		registry = new OriginEnchantRegistry(events);
		factory = new OriginEnchantFactory();

		events.subscribeAll(this);

		injector = Guice.createInjector(new EnchantPluginModule(this, registry, factory, this));

		lfc = new LiteConfig(this);
		lfc.register(GeneralConfig.class, GeneralConfig::new);
		lfc.reload();

		EnchantTypes.init(registry, factory);

		PaperCommandManager commands = new PaperCommandManager(this);
		commands.getCommandContexts().registerContext(EnchantKey.class, c -> registry.keyFromName(c.popFirstArg()));
		commands.getCommandCompletions().registerCompletion("enchants", c -> {
			Material holding = c.getPlayer().getItemInHand().getType();
			return registry.getAllEnchants().stream().filter(e -> e.targetsItem(holding)).map(Enchant::getKey).map(EnchantKey::getName).toList();
		});
		commands.registerCommand(new EnchantCommand(this, factory, registry));
	}

	@Override
	protected void disable() {
		EnchantmentConfiguration.save();
		lfc.save();
	}

	@Subscribe
	void onRtClick(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		switch (event.getAction()) {
			case LEFT_CLICK_AIR, PHYSICAL, LEFT_CLICK_BLOCK -> {
				return;
			}
			default -> {
			}
		}

		EnchantedItem item = factory.wrapItemStack(event.getItem());
		if (item == null || !item.isEnchantable()) return;

		Menu menu = new EnchantMenu(this, item).getMenu();
		menu.regen();
		menu.open(event.getPlayer());
	}

	public GeneralConfig getGeneralConfig() {
		return lfc.open(GeneralConfig.class);
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
		private final EnchantPlugin enchantPlugin;

		EnchantPluginModule(JavaPlugin plugin, EnchantRegistry registry, EnchantFactory factory, EnchantPlugin enchantPlugin) {
			this.plugin = plugin;
			this.enchantPlugin = enchantPlugin;
			this.enchantAPI = new EnchantAPI(plugin, registry, factory);
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(EnchantAPI.class).toInstance(enchantAPI);
			this.bind(EnchantPlugin.class).toInstance(enchantPlugin);
		}
	}
}
