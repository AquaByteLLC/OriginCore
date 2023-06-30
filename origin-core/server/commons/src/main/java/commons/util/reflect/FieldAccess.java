package commons.util.reflect;

/**
 * @author vadim
 */
public interface FieldAccess<T> {

	T get(Object instance);

	void set(Object instance, T value);

	T getstatic();

	void setstatic(T value);

}
