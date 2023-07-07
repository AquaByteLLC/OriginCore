package commons;

import co.aikar.commands.PaperCommandManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.logger.Level;
import commons.cmd.EconCommand;
import commons.cmd.SaveCommand;
import commons.conf.CommonsConfig;
import commons.data.AccountStorage;
import commons.impl.account.AccountStorageHandler;
import commons.data.SessionProvider;
import commons.data.impl.LocationPersister;
import commons.data.impl.SQLiteSession;
import commons.econ.BankAccount;
import commons.entity.registry.EntityRegistry;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import commons.events.impl.PluginEventWrapper;
import commons.impl.account.PlayerDefaultAccount;
import commons.impl.account.PlayerDefaultAccountStorage;
import commons.impl.account.ServerAccount;
import commons.sched.SchedulerManager;
import commons.sched.impl.Scheduler4Plugin;
import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.menu.Menus;
import me.vadim.util.menu.MenusKt;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * @author vadim
 */
public class CommonsPlugin extends ExtendedJavaPlugin implements Listener {

	private static CommonsPlugin instance;

	/**
	 * Avoid implementing ResourceProvider so that CommonsPlugins class remains visible to subprojects that do not have the LiteConfig dependency.
	 */
	private final LiteConfig lfc = new LiteConfig(new ResourceProvider() {
		@Override
		public File getDataFolder() {
			return CommonsPlugin.this.getDataFolder();
		}

		@Override
		public InputStream getResource(String name) {
			return CommonsPlugin.this.getResource(name);
		}

		@Override
		public Logger getLogger() {
			return CommonsPlugin.this.getLogger();
		}
	});

	public CommonsConfig config() {
		return lfc.open(CommonsConfig.class);
	}

	private final EntityRegistry entityRegistry = new EntityRegistry();

	public EntityRegistry getEntityRegistry() {
		return entityRegistry;
	}

	public static CommonsPlugin commons() {
		if (instance == null)
			throw new NullPointerException("Not initialized! Did you accidentally shade in the entire commons lib?");
		return instance;
	}

	@Deprecated(forRemoval = true)
	public static SchedulerManager scheduler() {
		return commons().getScheduler();
	}

	private final ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("[AccountStorage]").build());

	private final AccountStorageHandler storage = new AccountStorageHandler();

	public void registerAccountLoader(AccountStorage<?> accounts) {
		getLogger().info("Registering account loader for "+accounts.getAccountClass().getCanonicalName());
		storage.track(accounts);
	}

	private AccountStorage<PlayerDefaultAccount> accounts;

	@Deprecated
	public AccountStorage<PlayerDefaultAccount> getDataStorage() {
		return accounts;
	}

	public AccountStorage<PlayerDefaultAccount> getAccounts() {
		return accounts;
	}

	private PluginEventWrapper events;

	public EventRegistry getEventRegistry() {
		return events.getEventRegistry();
	}

	public SessionProvider getDatabase() {
		// todo
//		return PostgreSQLSession::new;
		return () -> new SQLiteSession(getDataFolder());
	}

	private Scheduler4Plugin scheduler;

	public SchedulerManager getScheduler() {
		return scheduler;
	}

	private final BankAccount bank = new ServerAccount();

	public BankAccount getBank() {
		return bank;
	}

	@Override
	protected void load() {
		instance = this;
		MenusKt.makesMeCry = this;
		getLogger().info("(load) commons plugin awaken");
		com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING); // supress spam from TableUtils class
		DataPersisterManager.registerDataPersisters(new LocationPersister());
		getDataFolder().mkdirs();

		// 'FUCK YOU' LIST:
		// Fuck you, underscore11code, inventor of this GAY SHIT: https://github.com/PaperMC/Paper/commit/ed1dc272e65a367cf1e405f9208a42911e4e19ba
		// Fuck anyone who contributed to this obscenity:
		//  - Proximyst
		//	- TheLukeGuy
		//	- electronicboy
		//	- jpenilla
		//	- Machine-Maker
		//	- kennytv
		// == Complete list of motherfuckers as of 2023-07-02
		class gayness_remover extends PrintStream {
			gayness_remover(@NotNull OutputStream out) {
				super(out);
			}
		}
		System.setOut(new gayness_remover(System.out));
		System.setErr(new gayness_remover(System.err));
		System.out.println("If this message is not prepended by [STDOUT], then the gayass paper logger has been disabled.");
	}

	private PaperCommandManager commands;

	@Override
	public void enable() {
		Menus.enable();
		getLogger().info("(enable) commons plugin hello");

		lfc.register(CommonsConfig.class, CommonsConfig::new);
		lfc.reload();

		accounts = new PlayerDefaultAccountStorage(getDatabase());
		registerAccountLoader(accounts);

		events = new PluginEventWrapper(this);
		events.enable();

		getEventRegistry().subscribeAll(this);

		scheduler = new Scheduler4Plugin(this);

		commands = new PaperCommandManager(this);
		commands.registerCommand(new EconCommand(accounts));
		commands.registerCommand(new SaveCommand(storage));
	}

	@Override
	public void disable() {
		Menus.disable();
		getLogger().info("(disable) commons plugin goodbye");
		storage.saveAll();
		pool.shutdownNow();
		events.disable();
		scheduler.shutdown();
	}

	@Subscribe
	private void onJoin(PlayerJoinEvent event) {
		pool.submit(() -> {
			try {
				storage.loadOne(event.getPlayer().getUniqueId());
			} catch (Exception e) {
				getLogger().severe("Problem loading accounts for " + event.getPlayer().getUniqueId());
				e.printStackTrace();
			}
		});
	}

	@Subscribe
	private void onQuit(PlayerQuitEvent event) {
		pool.submit(() -> {
			try {
				storage.saveOne(event.getPlayer().getUniqueId());
			} catch (Exception e) {
				getLogger().severe("Problem saving accounts for " + event.getPlayer().getUniqueId());
				e.printStackTrace();
			}
		});
	}

}
