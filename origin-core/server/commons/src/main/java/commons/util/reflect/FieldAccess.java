package commons.util.reflect;

/**
 * Provides fast reflective access to a {@link java.lang.reflect.Field}.
 * @author vadim
 */
public interface FieldAccess<T> {

	/**
	 * Get the current value of the field on {@code instance}.
	 * @throws UnsupportedOperationException if the field is static
	 */
	T get(Object instance);

	/**
	 * Set the new value of the field on {@code instance} to {@code value}.
	 * @throws UnsupportedOperationException if the field is static
	 */
	void set(Object instance, T value);

	/**
	 * Get the current value of the field.
	 * @throws UnsupportedOperationException if the field is virtual
	 */
	T getstatic();

	/**
	 * Set the new value of the field to {@code value}.
	 * @throws UnsupportedOperationException if the field is virtual
	 */
	void setstatic(T value);

}
