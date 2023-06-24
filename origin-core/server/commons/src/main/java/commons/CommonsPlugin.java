package commons;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.logger.Level;
import commons.data.AccountStorage;
import commons.data.AccountStorageHandler;
import commons.data.SessionProvider;
import commons.data.impl.LocationPersister;
import commons.data.impl.SQLiteSession;
import commons.entity.registry.EntityRegistry;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import commons.events.impl.PluginEventWrapper;
import commons.impl.account.PlayerDefaultAccount;
import commons.impl.account.PlayerDefaultAccountStorage;
import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.menu.Menus;
import me.vadim.util.menu.MenusKt;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author vadim
 */
public class CommonsPlugin extends ExtendedJavaPlugin implements Listener {

	private static CommonsPlugin instance;
	@Getter private final EntityRegistry entityRegistry = new EntityRegistry();

	public static CommonsPlugin commons() {
		if(instance == null)
			throw new NullPointerException("Not initialized! Did you accidentally shade in the entire commons lib?");
		return instance;
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

	public SessionProvider getDatabase(){
		// todo
//		return PostgreSQLSession::new;
		return () -> new SQLiteSession(getDataFolder());
	}

	@Override
	protected void load() {
		instance = this;
		MenusKt.makesMeCry = this;
		getLogger().info("(load) commons plugin awaken");
		com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING); // supress spam from TableUtils class
		DataPersisterManager.registerDataPersisters(new LocationPersister());
		getDataFolder().mkdirs();
	}

	@Override
	public void enable() {
		Menus.enable();
		getLogger().info("(enable) commons plugin hello");

		dataStorage = new PlayerDefaultAccountStorage(getDatabase());
		registerAccountLoader(dataStorage);

		events = new PluginEventWrapper(this);
		events.enable();

		getEventRegistry().subscribeAll(this);
	}

	@Override
	public void disable() {
		Menus.disable();
		getLogger().info("(disable) commons plugin goodbye");
		storage.saveAll();
		pool.shutdownNow();
		events.disable();
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
