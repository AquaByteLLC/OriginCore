package commons.util.reflect;

import java.lang.reflect.Field;

/**
 * @author vadim
 */
@SuppressWarnings("unchecked")
public final class Reflection {

	private Reflection() {}

	/**
	 * @return {@link FieldAccess} for {@code field}
	 */
	public static <T> FieldAccess<T> unreflectFieldAccess(Field field) {
		return new MethodHandleFieldAccess(field);
	}

	/**
	 * @return {@link FieldAccess} for the <i>declared field</i> named {@code name} in {@code clazz}
	 */
	public static <T> FieldAccess<T> unreflectFieldAccess(Class<?> clazz, String name) {
		Field f;
		try {
			f = clazz.getDeclaredField(name);
		} catch (Exception e) {
			throw new RuntimeException("Field "+name+" not found in "+clazz.getCanonicalName()+".", e);
		}
		return new MethodHandleFieldAccess(f);
	}

}
