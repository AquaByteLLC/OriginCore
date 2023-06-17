package commons;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import commons.data.AccountStorage;
import commons.data.AccountStorageHandler;
import me.lucko.helper.Events;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author vadim
 */
public class CommonsPlugin extends ExtendedJavaPlugin implements Listener {

	private final ExecutorService       loader = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("[AccountLoader]").build());
	private final AccountStorageHandler storage = new AccountStorageHandler();

	public void registerAccountLoader(AccountStorage<?> accounts) {
		storage.track(accounts);
	}

	@Override
	protected void load() {
		getLogger().info("(load) commons plugin awake");
	}

	@Override
	public void enable() {
		getLogger().info("(enable) commons plugin hello");
		Events.subscribe(AsyncPlayerPreLoginEvent.class).handler(this::onJoin);
		Events.subscribe(PlayerQuitEvent.class).handler(this::onQuit);
	}

	@Override
	public void disable() {
		getLogger().info("(disable) commons plugin goodbye");
		storage.saveAll();
		loader.shutdownNow();
	}

	void onJoin(AsyncPlayerPreLoginEvent event) {
		loader.submit(() -> {
			try {
				storage.loadOne(event.getUniqueId());
			} catch (Exception e) {
				getLogger().severe("Problem loading accounts for " + event.getUniqueId());
				e.printStackTrace();
			}
		});
	}

	void onQuit(PlayerQuitEvent event) {
		loader.submit(() -> {
			try {
				storage.saveOne(event.getPlayer().getUniqueId());
			} catch (Exception e) {
				getLogger().severe("Problem saving accounts for "+event.getPlayer().getUniqueId());
				e.printStackTrace();
			}
		});
	}

}
