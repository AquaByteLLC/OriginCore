package commons.sched.impl;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
class SyncedExecutorService extends AbstractExecutorService {

	private static void run(Runnable task) {
		try {
			task.run();
		}  catch (Exception e) {
			System.err.println("WARN: Exception while executing future task:");
			e.printStackTrace();
		}
	}

	private final int batchSize;

	public SyncedExecutorService(int batchSize) {
		this.batchSize = batchSize;
	}

	private final Queue<RunnableFuture<?>> tasks = new ConcurrentLinkedQueue<>();
	private volatile boolean isShutdown;

	@Override
	public void shutdown() {
		isShutdown = true;
		for (RunnableFuture<?> task : tasks)
			run(task);
	}

	@NotNull
	@Override
	public List<Runnable> shutdownNow() {
		return tasks.stream().map(Runnable.class::cast).collect(Collectors.toList()); // holy gay
	}

	@Override
	public boolean isShutdown() {
		return isShutdown;
	}

	@Override
	public boolean isTerminated() {
		return isShutdown && tasks.isEmpty();
	}

	@Override
	public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException("Scheduler synced to Bukkit's main thread cannot awaitTermination.");
	}

	@Override
	public void execute(@NotNull Runnable command) {
		if (command instanceof RunnableFuture<?> task)
			tasks.add(task);
		else if (command instanceof Callable<?> callable)
			tasks.add(new FutureTask<>(callable));
		else
			tasks.add(new FutureTask<>(command, null));
	}

	void executeBatch() {
		int batch = 0;

		RunnableFuture<?> task;
		while ((task = tasks.poll()) != null) {
			run(task);
			if(batch++ >= batchSize && batchSize > 0)
				break;
		}
	}

}