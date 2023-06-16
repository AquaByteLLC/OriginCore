package commons;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import commons.data.AccountStorage;
import commons.data.AccountStorageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author vadim
 */
public class CommonsPlugin extends JavaPlugin implements Listener {

	private final ExecutorService       loader = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("[AccountLoader]").build());
	private final AccountStorageHandler storage = new AccountStorageHandler();

	public void registerAccountLoader(AccountStorage<?> accounts) {
		storage.track(accounts);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		storage.saveAll();
		loader.shutdownNow();
	}

	@EventHandler
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

	@EventHandler
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
