package commons.sched;

import java.util.concurrent.ExecutorService;

/**
 * @author vadim
 */
public interface SchedulerManager {

	ExecutorService getAsyncExecutor();

	ExecutorService getSyncExecutor();

	SchedulerBukkit getBukkitSync();

	SchedulerBukkit getBukkitAsync();

}
