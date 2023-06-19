package generators.impl;

import co.aikar.commands.PaperCommandManager;
import commons.CommonsPlugin;
import commons.StringUtil;
import commons.data.AccountStorage;
import commons.events.api.EventRegistry;
import generators.impl.conf.Tiers;
import generators.impl.data.GenAccount;
import generators.impl.cmd.GenCommand;
import generators.impl.conf.Config;
import generators.impl.conf.Messages;
import generators.impl.data.GenAccountStorage;
import me.lucko.helper.text3.adapter.bukkit.SpigotTextAdapter;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author vadim
 */
public class GensPlugin extends JavaPlugin implements ResourceProvider {

	private LiteConfig          lfc;
	private PaperCommandManager commands;
	private GenRegistry         registry;
	private GenHandler          handler;
	private AccountStorage<GenAccount> storage;

	public Config config() {
		return lfc.open(Config.class);
	}

	public Messages messages() {
		return lfc.open(Messages.class);
	}

	@Override
	public void onEnable() {
		lfc = new LiteConfig(this);
		lfc.register(Config.class, Config::new);
		lfc.register(Messages.class, Messages::new);
		lfc.register(Tiers.class, Tiers::new);
		lfc.reload();

		CommonsPlugin commons = CommonsPlugin.commons();

		EventRegistry events = commons.getEventRegistry();

		storage = new GenAccountStorage(registry, lfc, commons.getDatabase());

		registry = new GenRegistry(lfc);
		handler  = new GenHandler(lfc, events, registry);

		commands = new PaperCommandManager(this);
		commands.registerCommand(new GenCommand(storage));

		commons.registerAccountLoader(storage);
	}

	@Override
	public void onDisable() {
		handler.shutdown();
		registry.flushAndSave();
	}

}
