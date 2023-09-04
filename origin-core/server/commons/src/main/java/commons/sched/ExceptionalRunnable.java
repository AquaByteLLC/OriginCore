package commons.sched;

import commons.util.reflect.ReflectUtil;
import lombok.SneakyThrows;

/**
 * {@linkplain Runnable} that may throw an exception.
 *
 * @author vadim
 */
@FunctionalInterface
public interface ExceptionalRunnable {

	void run() throws Throwable;

	@SuppressWarnings("Convert2Lambda")
	static Runnable wrap(ExceptionalRunnable exceptional) {
		return new Runnable() {
			@Override
			@SneakyThrows
			public void run() {
				try {
					exceptional.run();
				} catch (Throwable t) {
					ReflectUtil.sneaky(t);
				}
			}
		};
	}

}