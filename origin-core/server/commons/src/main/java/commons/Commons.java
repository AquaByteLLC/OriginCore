package commons;

import commons.conf.CommonsConfig;
import commons.data.sql.SessionProvider;
import commons.events.api.EventRegistry;
import commons.sched.SchedulerManager;

import java.util.logging.Logger;

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

	public static EventRegistry events() {
		return commons().getEventRegistry();
	}

	public static SessionProvider db() {
		return commons().getDatabase();
	}

	public static Logger logger() {
		return commons().getLogger();
	}

}
