package commons;

import co.aikar.commands.PaperCommandManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.logger.Level;
import commons.cmd.EconCommand;
import commons.data.AccountStorage;
import commons.data.AccountStorageHandler;
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
import me.vadim.util.menu.Menus;
import me.vadim.util.menu.MenusKt;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.*;

/**
 * @author vadim
 */
public class CommonsPlugin extends ExtendedJavaPlugin implements Listener {

	private static CommonsPlugin instance;
	@Getter
	private final EntityRegistry entityRegistry = new EntityRegistry();

	public static CommonsPlugin commons() {
		if (instance == null)
			throw new NullPointerException("Not initialized! Did you accidentally shade in the entire commons lib?");
		return instance;
	}

	public static SchedulerManager scheduler() {
		return commons().getScheduler();
	}

	private final ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("[AccountStorage]").build());

	private final AccountStorageHandler storage = new AccountStorageHandler();

	public void registerAccountLoader(AccountStorage<?> accounts) {
		storage.track(accounts);
	}

	private AccountStorage<PlayerDefaultAccount> dataStorage;

	public AccountStorage<PlayerDefaultAccount> getDataStorage() {
		return dataStorage;
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

		dataStorage = new PlayerDefaultAccountStorage(getDatabase());
		registerAccountLoader(dataStorage);

		events = new PluginEventWrapper(this);
		events.enable();

		getEventRegistry().subscribeAll(this);

		scheduler = new Scheduler4Plugin(this);

		commands = new PaperCommandManager(this);
		commands.registerCommand(new EconCommand(dataStorage));
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
	private void onJoin(EventContext context, PlayerJoinEvent event) {
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
	private void onQuit(EventContext context, PlayerQuitEvent event) {
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
