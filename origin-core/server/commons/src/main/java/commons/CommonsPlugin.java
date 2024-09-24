package commons;

import co.aikar.commands.PaperCommandManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.logger.Level;
import commons.cmd.ColorCommand;
import commons.cmd.EconCommand;
import commons.cmd.ReloadModuleCommand;
import commons.cmd.SaveModuleCommand;
import commons.conf.CommonsConfig;
import commons.data.account.AccountStorage;
import commons.data.sql.SessionProvider;
import commons.data.sql.impl.SQLiteSession;
import commons.econ.BankAccount;
import commons.entity.registry.EntityRegistry;
import commons.events.api.EventRegistry;
import commons.events.impl.PluginEventWrapper;
import commons.impl.data.account.AccountStorageHandler;
import commons.impl.data.account.PlayerDefaultAccount;
import commons.impl.data.account.PlayerDefaultAccountStorage;
import commons.impl.data.account.ServerAccount;
import commons.impl.data.sql.LocationPersister;
import commons.sched.SchedulerManager;
import commons.sched.impl.Scheduler4Plugin;
import commons.util.StringUtil;
import me.lucko.helper.messaging.bungee.BungeeCord;
import me.lucko.helper.messaging.bungee.BungeeCordImpl;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.ConfigurationManager;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.menu.Menus;
import me.vadim.util.menu.MenusKt;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static commons.hologram.InterpolatedHologram.disposeSafely;

/**
 * @author vadim
 */
public class CommonsPlugin extends ExtendedJavaPlugin implements OriginModule, Listener {

	private static CommonsPlugin instance;

	/**
	 * Avoid implementing ResourceProvider so that CommonsPlugins class remains visible to subprojects that do not have the LiteConfig dependency.
	 */

	private final ResourceProvider rp = new ResourceProvider() {
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
	};

	private final LiteConfig lfc = new LiteConfig(rp);

	public static CommonsPlugin commons() {
		if (instance == null)
			throw new NullPointerException("Not initialized! Did you accidentally shade in the entire commons lib?");
		return instance;
	}

	public CommonsConfig config() {
		return lfc.open(CommonsConfig.class);
	}

	@Override
	public ConfigurationManager getConfigurationManager() {
		return lfc;
	}

	private final EntityRegistry entityRegistry = new EntityRegistry();

	public EntityRegistry getEntityRegistry() {
		return entityRegistry;
	}

	private AccountStorageHandler storage;

	private AccountStorage<PlayerDefaultAccount> accounts;

	@Override
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

	private BungeeCord bungeeCord;

	public BungeeCord getBungeeCord() {
		return bungeeCord;
	}

	@Deprecated
	public void registerReloadHook(JavaPlugin plugin, ConfigurationManager manager) {
		if (plugin instanceof OriginModule module)
			registerModule(module);
	}

	@Deprecated
	public void registerAccountLoader(AccountStorage<?> accounts) {
		getLogger().info("Registering account loader for " + accounts.getAccountClass().getCanonicalName());
		throw new RuntimeException("Please use `Commons.commons().registerModule(this);`!");
	}

	private final Map<String, OriginModule> modules = new HashMap<>();

	public void registerModule(OriginModule module) {
		System.out.println("[modules] Registered module " + StringUtil.formatModuleName(module) + ".");
		modules.put(StringUtil.formatModuleName(module), module);
	}

	private BukkitTask autosave;

	private void autosave() {
		for (OriginModule module : modules.values()) {
			try {
				AccountStorage<?> storage = module.getAccounts();
				if (storage != null)
					storage.flushAndSave();
			} catch (Exception e) {
				getLogger().severe("Failure to autosave accounts for module " + StringUtil.formatModuleName(module));
				e.printStackTrace();
			}

			try {
				module.onSave();
			} catch (Exception e) {
				getLogger().severe("Failure to autosave data for module " + StringUtil.formatModuleName(module));
				e.printStackTrace();
			}
		}
	}


	@Override
	protected void load() {
		instance           = this;
		MenusKt.makesMeCry = this;
		getLogger().info("(load) commons plugin awaken");
		com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING); // supress spam from TableUtils class
		DataPersisterManager.registerDataPersisters(new LocationPersister());
		getDataFolder().mkdirs();

		
	}

	private PaperCommandManager commands;

	@Override
	public void enable() {
		Menus.enable();
		getLogger().info("(enable) commons plugin hello");

		lfc.register(CommonsConfig.class, CommonsConfig::new);
		lfc.reload();

		accounts = new PlayerDefaultAccountStorage(getDatabase());

		events = new PluginEventWrapper(this);

		events.enable();
		disposeSafely();

		scheduler = new Scheduler4Plugin(this);
		storage   = new AccountStorageHandler(modules, scheduler, getEventRegistry(), rp);
		bungeeCord = new BungeeCordImpl(this);

		autosave = scheduler.getBukkitAsync().runTimer(this::autosave, config().getAutosaveInvervalTicks());

		commands = new PaperCommandManager(this);
		commands.registerCommand(new ColorCommand());
		commands.registerCommand(new EconCommand(accounts));
		commands.registerCommand(new SaveModuleCommand(modules));
		commands.registerCommand(new ReloadModuleCommand(modules));

		commands.getCommandCompletions().registerCompletion("modules", c -> new ArrayList<>(modules.keySet()));

		getEventRegistry().subscribeAll(this);

		registerModule(this);
	}

	@Override
	public void disable() {
		Menus.disable();
		getLogger().info("(disable) commons plugin goodbye");
		autosave.cancel();
		// will trust modules to save on their own
		storage.shutdown();
		events.disable();
		scheduler.shutdown();
	}

}
