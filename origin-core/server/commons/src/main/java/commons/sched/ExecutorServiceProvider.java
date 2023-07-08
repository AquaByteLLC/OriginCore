package commons.sched;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author vadim
 */
public interface ExecutorServiceProvider {

	/**
	 * @return a better version of {@link java.util.concurrent.ThreadPoolExecutor} which logs exceptions upon execution
	 * @see Executors#newCachedThreadPool()
	 */
	ExecutorService newExtendedThreadPool();

	/**
	 * @return a better version of {@link java.util.concurrent.ThreadPoolExecutor} which logs exceptions upon execution
	 * @see Executors#newCachedThreadPool(ThreadFactory)
	 */
	ExecutorService newExtendedThreadPool(ThreadFactory threadFactory);

}
