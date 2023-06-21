package generators.impl;

import co.aikar.commands.PaperCommandManager;
import com.j256.ormlite.field.DataPersisterManager;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import generators.impl.conf.Tiers;
import generators.impl.cmd.GenCommand;
import generators.impl.conf.Config;
import generators.impl.conf.Messages;
import generators.impl.data.GenAccountStorage;
import generators.impl.data.GenStorage;
import generators.impl.data.TierPersister;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author vadim
 */
public class GensPlugin extends JavaPlugin implements ResourceProvider {

	private LiteConfig          lfc;
	private PaperCommandManager commands;
	private GenRegistry         registry;
	private GenHandler          handler;
	private GenAccountStorage   accountStorage;
	private GenStorage          genStorage;

	public Config config() {
		return lfc.open(Config.class);
	}

	public Messages messages() {
		return lfc.open(Messages.class);
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

		CommonsPlugin commons = CommonsPlugin.commons();
		EventRegistry events  = commons.getEventRegistry();

		registry       = new GenRegistry(lfc);
		accountStorage = new GenAccountStorage(registry, lfc, commons.getDatabase());
		handler        = new GenHandler(lfc, events, registry, accountStorage);
		genStorage     = new GenStorage(commons.getDatabase(), registry);

		commands = new PaperCommandManager(this);
		commands.registerCommand(new GenCommand(this, genStorage, registry, lfc, accountStorage));

		commons.registerAccountLoader(accountStorage);

		genStorage.load();

		long auto = config().getAutosaveIntervalTicks();
		getServer().getScheduler().runTaskTimerAsynchronously(this, genStorage::save, auto, auto);
	}

	@Override
	public void onDisable() {
		handler.shutdown();
		genStorage.save();
	}

}
