package generators.impl;

import co.aikar.commands.PaperCommandManager;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.j256.ormlite.field.DataPersisterManager;
import commons.CommonsPlugin;
import commons.data.AccountStorage;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import commons.events.impl.EventSubscriber;
import commons.events.impl.bukkit.BukkitEventSubscriber;
import generators.GeneratorRegistry;
import generators.impl.conf.Tiers;
import generators.impl.cmd.GenCommand;
import generators.impl.conf.Config;
import generators.impl.conf.Messages;
import generators.impl.data.GenAccount;
import generators.impl.data.GenAccountStorage;
import generators.impl.data.GenStorage;
import generators.impl.data.TierPersister;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

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

	public ConfigurationProvider getConfiguration() {
		return lfc;
	}

	public AccountStorage<GenAccount> getAccounts() {
		return accountStorage;
	}

	public GeneratorRegistry getRegistry() {
		return registry;
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
		commands.registerCommand(new GenCommand(this, genStorage));

		commons.registerAccountLoader(accountStorage);

		genStorage.load();

		long auto = lfc.open(Config.class).getAutosaveIntervalTicks();
		getServer().getScheduler().runTaskTimerAsynchronously(this, genStorage::save, auto, auto);
	}

	@Override
	public void onDisable() {
		handler.shutdown();
		genStorage.save();
	}

}
