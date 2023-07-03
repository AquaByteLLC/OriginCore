package commons.util;

import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vadim
 */
public class ReflectUtil {

	private static final Map<Class<?>, Map<Class<?>, List<MethodHandle>>> publicMethodsByReturnType = new HashMap<>();

	/**
	 * this method caches the method handles for faster subsequent lookups
 	 */
	@SneakyThrows
	public static MethodHandle[] getPublicMethodsByReturnType(Class<?> clazz, Class<?> returnType) {
		Map<Class<?>, List<MethodHandle>> byReturnType = publicMethodsByReturnType.computeIfAbsent(clazz, x -> new HashMap<>());

		List<MethodHandle> handles;
		if (!byReturnType.containsKey(returnType)) {
			MethodHandles.Lookup lookup = MethodHandles.lookup();

			handles = new ArrayList<>();
			for (Method method : clazz.getMethods())
				if (returnType.isAssignableFrom(method.getReturnType()))
					handles.add(lookup.unreflect(method));

			byReturnType.put(clazz, handles);
		} else
			handles = byReturnType.get(returnType);

		return handles.toArray(MethodHandle[]::new);
	}

	public static void serr(String string) {
		synchronized (System.err) {
			for (char c : string.toCharArray())
				System.err.print(c);
			System.err.println();
		}
	}

	public static void serr(Throwable throwable) {
		synchronized (System.err) {
			throwable.printStackTrace(System.err);
		}
	}

	public static void sout(String string) {
		synchronized (System.out) {
			for (char c : string.toCharArray())
				System.out.print(c);
			System.out.println();
		}
	}

	/**
	 * @deprecated incorrect usage of {@link VarHandle}
	 * @see commons.util.reflect.FieldAccess
	 */
	@Deprecated
	public static VarHandle unreflectVarHandle(Class<?> clazz, String name) {
		Field field;
		try {
			field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return MethodHandles.privateLookupIn(clazz, MethodHandles.lookup()).unreflectVarHandle(field);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static MethodHandle unreflectMethodHandle(Class<?> clazz, String name, Class<?>... params) {
		Method method;
		try {
			method = clazz.getDeclaredMethod(name, params);
			method.setAccessible(true);
			return MethodHandles.privateLookupIn(clazz, MethodHandles.lookup()).unreflect(method);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
