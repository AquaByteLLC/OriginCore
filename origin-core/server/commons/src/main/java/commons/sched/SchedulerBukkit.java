package commons.sched;

import org.bukkit.scheduler.BukkitTask;

/**
 * Better version of {@link org.bukkit.scheduler.BukkitScheduler} (whatever the fuck that is).
 * @author vadim
 */
public interface SchedulerBukkit {

	BukkitTask runTask(ExceptionalRunnable task);
	void runTask(ExceptionalConsumer<BukkitTask> task);

	BukkitTask runLater(ExceptionalRunnable task, long delayTicks);
	void runLater(ExceptionalConsumer<BukkitTask> task, long delayTicks);

	BukkitTask runTimer(ExceptionalRunnable task, long intervalTicks);
	void runTimer(ExceptionalConsumer<BukkitTask> task, long intervalTicks);

}
