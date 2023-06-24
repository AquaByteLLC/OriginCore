package enchants.impl;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import enchants.EnchantAPI;
import enchants.item.EnchantFactory;
import enchants.EnchantKey;
import enchants.impl.commands.EnchantCommand;
import enchants.EnchantRegistry;
import enchants.impl.conf.GeneralConfig;
import enchants.impl.item.OriginEnchantFactory;
import enchants.item.Enchant;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantPlugin extends ExtendedJavaPlugin implements ResourceProvider {
	private static Injector injector;

	private LiteConfig lfc;
	private EnchantRegistry registry;
	private EnchantFactory factory;

	@Override
	protected void enable() {
		EventRegistry events = CommonsPlugin.commons().getEventRegistry();
		registry = new OriginEnchantRegistry(events);
		factory = new OriginEnchantFactory();

		injector = Guice.createInjector(new EnchantPluginModule(this, registry, factory));

		lfc = new LiteConfig(this);
		lfc.register(GeneralConfig.class, GeneralConfig::new);
		lfc.reload();

		EnchantTypes.init(registry, factory);

		PaperCommandManager commands = new PaperCommandManager(this);
		commands.getCommandContexts().registerContext(EnchantKey.class, c -> EnchantTypes.fromName(c.popFirstArg()));
		commands.getCommandCompletions().registerCompletion("enchants", c -> registry.getAllEnchants().stream().map(Enchant::getKey).map(EnchantKey::getName).toList());
		commands.registerCommand(new EnchantCommand(factory, registry));
	}

	@Override
	protected void disable() {
		lfc.save();
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

		EnchantPluginModule(JavaPlugin plugin, EnchantRegistry registry, EnchantFactory factory) {
			this.plugin = plugin;
			this.enchantAPI = new EnchantAPI(plugin, registry, factory);
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(EnchantAPI.class).toInstance(enchantAPI);
		}
	}
}
