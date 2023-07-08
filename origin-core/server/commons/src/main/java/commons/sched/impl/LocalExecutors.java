package commons.sched.impl;

import commons.sched.ExecutorServiceProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author vadim
 */
class LocalExecutors implements ExecutorServiceProvider {

	@Override
	public ExecutorService newExtendedThreadPool() {
		return new AsyncExecutorService(0, Integer.MAX_VALUE,
									  60L, TimeUnit.SECONDS,
									  new SynchronousQueue<Runnable>());
	}

	@Override
	public ExecutorService newExtendedThreadPool(ThreadFactory threadFactory) {
		return new AsyncExecutorService(0, Integer.MAX_VALUE,
									  60L, TimeUnit.SECONDS,
									  new SynchronousQueue<Runnable>(),
									  threadFactory);
	}

}
