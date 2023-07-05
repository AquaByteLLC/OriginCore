package commons.sched;

import commons.util.ReflectUtil;
import lombok.SneakyThrows;

import java.util.function.Consumer;

/**
 * {@linkplain java.util.function.Consumer} that may throw an exception.
 *
 * @author vadim
 */
@FunctionalInterface
public interface ExceptionalConsumer<T> {

	void accept(T t) throws Throwable;

	@SuppressWarnings("Convert2Lambda")
	static <T> Consumer<T> wrap(ExceptionalConsumer<T> exceptional) {
		return new Consumer<T>() {
			@Override
			@SneakyThrows
			public void accept(T param) {
				try {
					exceptional.accept(param);
				} catch (Throwable t) {
					ReflectUtil.sneaky(t);
				}
			}
		};
	}

}