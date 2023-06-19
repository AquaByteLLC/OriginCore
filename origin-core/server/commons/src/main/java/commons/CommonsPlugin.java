package commons;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.j256.ormlite.logger.Level;
import commons.data.AccountStorage;
import commons.data.AccountStorageHandler;
import commons.data.SessionProvider;
import commons.data.impl.PostgreSQLSession;
import commons.data.impl.SQLiteSession;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import commons.events.api.impl.PlayerEventRegistry;
import commons.events.impl.bukkit.BukkitEventListener;
import commons.events.impl.packet.PacketEventListener;
import lombok.SneakyThrows;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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

	private final EventRegistry       events      = new PlayerEventRegistry();
	private final PacketEventListener packetsImpl = new PacketEventListener();

	public EventRegistry getEventRegistry() {
		return events;
	}

	public SessionProvider getDatabase(){
		// todo
//		return PostgreSQLSession::new;
		return () -> new SQLiteSession(getDataFolder());
	}

	@Override
	protected void load() {
		instance = this;
		getLogger().info("(load) commons plugin awaken");
		com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING); // supress spam from TableUtils class
		getDataFolder().mkdirs();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void enable() {
		getLogger().info("(enable) commons plugin hello");

		// special publishers for EventRegistry
		// the underlying impls work differently
		// - for Bukkit events you have to register it per event class
		// - for Packet events it's best to only register one Injector per plyaer
		events.addSubscriptionHook(event ->  {
			if(org.bukkit.event.Event.class.isAssignableFrom(event)) {
				new BukkitEventListener<>((Class<Event>) event).startListen(this, events);
				int len = ReflectUtil.getPublicMethodsByReturnType(event, Player.class).length;
				if(len < 1)
					throw new IllegalArgumentException("Bukkit event class " + event.getCanonicalName() + " does not involve a player!");
				if(len > 1)
					ReflectUtil.serr("WARN: Bukkit event class " + event.getCanonicalName() + " involves multiple players! EventContext does not gurantee which player will be selected.");
			}
		});
		packetsImpl.startListen(this, events);
		//

		events.subscribeAll(this);
	}

	@Override
	public void disable() {
		getLogger().info("(disable) commons plugin goodbye");
		storage.saveAll();
		pool.shutdownNow();
		packetsImpl.ceaseListen();
	}

	@Subscribe
	private void onJoin(EventContext context, PlayerJoinEvent event) {
		System.out.println("(temp) commons onJoin");
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
		System.out.println("(temp) commons onQuit");
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
