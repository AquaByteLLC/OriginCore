package commons;

import commons.conf.CommonsConfig;
import commons.sched.SchedulerManager;

/**
 * @author vadim
 */
public class Commons {

	public static CommonsPlugin commons() {
		return CommonsPlugin.commons();
	}

	public static SchedulerManager scheduler() {
		return commons().getScheduler();
	}

	public static CommonsConfig config() {
		return commons().config();
	}

}
