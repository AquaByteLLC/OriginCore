package generators.impl;

import co.aikar.commands.PaperCommandManager;
import com.j256.ormlite.field.DataPersisterManager;
import commons.Commons;
import commons.OriginModule;
import commons.data.account.AccountStorage;
import commons.data.sql.SessionProvider;
import commons.events.api.EventRegistry;
import generators.GeneratorRegistry;
import generators.impl.cmd.GenCommand;
import generators.impl.conf.Config;
import generators.impl.conf.GensSettings;
import generators.impl.conf.Messages;
import generators.impl.conf.Tiers;
import generators.impl.data.GenAccount;
import generators.impl.data.GenAccountStorage;
import generators.impl.data.GenStorage;
import generators.impl.data.TierPersister;
import me.vadim.util.conf.ConfigurationManager;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author vadim
 */
public class GensPlugin extends JavaPlugin implements ResourceProvider, OriginModule {

	private LiteConfig lfc;
	private PaperCommandManager commands;
	private GenRegistry registry;
	private GenHandler handler;
	private GenAccountStorage accountStorage;
	private GenStorage genStorage;

	@Override
	public AccountStorage<GenAccount> getAccounts() {
		return accountStorage;
	}

	public GeneratorRegistry getRegistry() {
		return registry;
	}

	@Override
	public ConfigurationManager getConfigurationManager() {
		return lfc;
	}

	@Override
	public void afterReload() throws Exception {
		handler.restart();
	}

	@Override
	public void onSave() throws Exception {
		genStorage.save();
	}

	@Override
	public void onLoad() {
		lfc = new LiteConfig(this);
		DataPersisterManager.registerDataPersisters(new TierPersister(lfc));
	}

	@Override
	public void onEnable() {
		lfc.register(Config.class, Config::new);
		lfc.register(Messages.class, Messages::new);
		lfc.register(Tiers.class, (rp) -> new Tiers(rp, lfc));
		lfc.reload();

		Commons.commons().registerModule(this);

		SessionProvider db = Commons.db();
		EventRegistry events  = Commons.events();

		registry       = new GenRegistry(lfc);
		accountStorage = new GenAccountStorage(registry, lfc, db);
		handler        = new GenHandler(lfc, events, registry, accountStorage);
		genStorage     = new GenStorage(db, registry, lfc);

		commands = new PaperCommandManager(this);
		commands.registerCommand(new GenCommand(this, genStorage));

		GensSettings.init(this);

		System.out.println("Loading gens...");
		genStorage.load();

		handler.startup();

		long auto = lfc.open(Config.class).getAutosaveIntervalTicks();
		getServer().getScheduler().runTaskTimerAsynchronously(this, genStorage::save, auto, auto);
	}

	@Override
	public void onDisable() {
		handler.shutdown();
		genStorage.save();
	}

}
