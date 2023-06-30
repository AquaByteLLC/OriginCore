package commons.util.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author vadim
 */
@SuppressWarnings("rawtypes")
class MethodHandleFieldAccess implements FieldAccess {

	private final boolean isStatic;
	private final String descriptor;
	private final MethodHandle set, get;

	private final RuntimeException err(String s, Throwable t) {
		return new RuntimeException("unable to "+s+" for field "+descriptor, t);
	}

	MethodHandleFieldAccess(Field field) {
		field.setAccessible(true);
		this.isStatic = Modifier.isStatic(field.getModifiers());
		this.descriptor = field.getDeclaringClass().getCanonicalName() + (isStatic ? '.' : '#') + field.getName();

		MethodHandles.Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
		} catch (Exception e) {
			throw err("create a private lookup", e);
		}

		try {
			set = lookup.unreflectSetter(field);
		} catch (Exception e) {
			throw err("unreflect setter", e);
		}

		try {
			get = lookup.unreflectGetter(field);
		} catch (Exception e) {
			throw err("unreflect getter", e);
		}
	}

	public Object get(Object instance) {
		if(isStatic)
			throw new UnsupportedOperationException("attempt to call virtual getter on static method");
		try {
			return get.invoke(instance);
		} catch (Throwable t) {
			throw err("invokevirtual getter", t);
		}
	}

	public void set(Object instance, Object value) {
		if(isStatic)
			throw new UnsupportedOperationException("attempt to call virtual setter on static method");
		try {
			set.invoke(instance, value);
		} catch (Throwable t) {
			throw err("invokevirtual setter", t);
		}
	}

	@Override
	public Object getstatic() {
		if(!isStatic)
			throw new UnsupportedOperationException("attempt to call static getter on virtual method");
		try {
			return get.invoke();
		} catch (Throwable t) {
			throw err("invokestatic getter", t);
		}
	}

	@Override
	public void setstatic(Object value) {
		if(!isStatic)
			throw new UnsupportedOperationException("attempt to call static setter on virtual method");
		try {
			set.invoke(value);
		} catch (Throwable t) {
			throw err("invokestatic setter", t);
		}
	}

}
