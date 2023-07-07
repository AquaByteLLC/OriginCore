package commons.sched.impl;

import commons.sched.ExceptionalConsumer;
import commons.sched.ExceptionalRunnable;
import commons.sched.SchedulerBukkit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author vadim
 */
class SyncSchedulerBukkit implements SchedulerBukkit {

	private final JavaPlugin plugin;

	SyncSchedulerBukkit(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public BukkitTask runTask(ExceptionalRunnable task) {
		return Bukkit.getScheduler().runTask(plugin, ExceptionalRunnable.wrap(task));
	}

	@Override
	public void runTask(ExceptionalConsumer<BukkitTask> task) {
		Bukkit.getScheduler().runTask(plugin, ExceptionalConsumer.wrap(task));
	}

	@Override
	public BukkitTask runLater(ExceptionalRunnable task, long delayTicks) {
		return Bukkit.getScheduler().runTaskLater(plugin, ExceptionalRunnable.wrap(task), delayTicks);
	}

	@Override
	public void runLater(ExceptionalConsumer<BukkitTask> task, long delayTicks) {
		Bukkit.getScheduler().runTaskLater(plugin, ExceptionalConsumer.wrap(task), delayTicks);
	}

	@Override
	public BukkitTask runTimer(ExceptionalRunnable task, long intervalTicks) {
		return Bukkit.getScheduler().runTaskTimer(plugin, ExceptionalRunnable.wrap(task), intervalTicks, intervalTicks);
	}

	@Override
	public void runTimer(ExceptionalConsumer<BukkitTask> task, long intervalTicks) {
		Bukkit.getScheduler().runTaskTimer(plugin, ExceptionalConsumer.wrap(task), intervalTicks, intervalTicks);
	}

}
