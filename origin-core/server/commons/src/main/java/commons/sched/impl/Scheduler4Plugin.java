package commons.sched.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import commons.sched.ExecutorServiceProvider;
import commons.sched.SchedulerBukkit;
import commons.sched.SchedulerManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author vadim
 */
public class Scheduler4Plugin implements SchedulerManager {

	private final SchedulerBukkit basync, bsync;
	private final ExecutorService easync;
	private final SyncExecutorService esync;
	private final BukkitTask esynct;
	private final ExecutorServiceProvider esp = new LocalExecutors();

	public Scheduler4Plugin(JavaPlugin plugin) {
		this.basync = new AsyncSchedulerBukkit(plugin);
		this.bsync  = new SyncSchedulerBukkit(plugin);

		this.easync = esp.newExtendedThreadPool(new ThreadFactoryBuilder().setNameFormat("scheduler-async-worker %d").setDaemon(false).build());
		this.esync  = new SyncExecutorService(1000);

		esynct = bsync.runTimer(esync::executeBatch, 1);
	}

	private void checkShutdown() {
		synchronized (lock) {
			if (isShutdown)
				throw new UnsupportedOperationException("Already shutdown.");
		}
	}

	@Override
	public ExecutorService getAsyncExecutor() {
		checkShutdown();
		return easync;
	}

	@Override
	public ExecutorService getSyncExecutor() {
		checkShutdown();
		return esync;
	}

	@Override
	public SchedulerBukkit getBukkitSync() {
		checkShutdown();
		return bsync;
	}

	@Override
	public SchedulerBukkit getBukkitAsync() {
		checkShutdown();
		return basync;
	}

	private boolean isShutdown = false;
	private final Object lock = new Object();

	public void shutdown() {
		synchronized (lock) {
			if (!isShutdown) {
				esynct.cancel();
				esync.shutdown();
				easync.shutdown();
			}
			isShutdown = true;
		}
	}

	@Override
	public ExecutorServiceProvider getServiceProvider() {
		return esp;
	}

}
