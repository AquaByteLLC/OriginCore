package commons.impl.data.account;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import commons.OriginModule;
import commons.data.account.AccountStorage;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import commons.sched.SchedulerManager;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class AccountStorageHandler {

	private final Map<String, OriginModule> modulesView;
	private final Logger logger;
	private final ExecutorService pool;

	public AccountStorageHandler(Map<String, OriginModule> modulesView, SchedulerManager scheduler, EventRegistry events, ResourceProvider provider) {
		this.modulesView = modulesView;
		this.logger = provider.getLogger();
		this.pool = scheduler.getServiceProvider().newExtendedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("[AccountStorage]").build());
		events.subscribeAll(this);
	}

	public Set<AccountStorage<?>> getStorages() {
		return modulesView.values().stream().map(OriginModule::getAccounts).collect(Collectors.toSet());
	}

	public void saveAll() {
		getStorages().forEach(AccountStorage::flushAndSave);
	}

	public void saveOne(UUID player) {
		getStorages().forEach(s -> s.savePlayer(player));
	}

	public void loadOne(UUID player) {
		getStorages().forEach(s -> s.loadPlayer(player));
	}

	public void shutdown() {
		saveAll();
		pool.shutdown();
	}

	@Subscribe
	private void onJoin(PlayerJoinEvent event) {
		pool.submit(() -> {
			try {
				loadOne(event.getPlayer().getUniqueId());
			} catch (Exception e) {
				logger.severe("Problem loading accounts for " + event.getPlayer().getUniqueId());
				e.printStackTrace();
			}
		});
	}

	@Subscribe
	private void onQuit(PlayerQuitEvent event) {
		pool.submit(() -> {
			try {
				saveOne(event.getPlayer().getUniqueId());
			} catch (Exception e) {
				logger.severe("Problem saving accounts for " + event.getPlayer().getUniqueId());
				e.printStackTrace();
			}
		});
	}

}
