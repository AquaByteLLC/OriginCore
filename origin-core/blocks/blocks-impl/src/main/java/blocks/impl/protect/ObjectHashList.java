package blocks.impl.protect;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * Extension of {@link ObjectArrayList} that uses {@link Objects#hashCode(Object)} instead of {@link Objects#equals(Object, Object)} for equality checks in {@link #indexOf(Object)} and {@link #lastIndexOf(Object)}.
 * @author vadim
 */
public class ObjectHashList<T> extends ObjectArrayList<T> {

	public ObjectHashList(int capacity) {
		super(capacity);
	}

	public ObjectHashList() {
	}

	public ObjectHashList(Collection<? extends T> c) {
		super(c);
	}

	public ObjectHashList(ObjectCollection<? extends T> c) {
		super(c);
	}

	public ObjectHashList(ObjectList<? extends T> l) {
		super(l);
	}

	public ObjectHashList(T[] a) {
		super(a);
	}

	public ObjectHashList(T[] a, int offset, int length) {
		super(a, offset, length);
	}

	public ObjectHashList(Iterator<? extends T> i) {
		super(i);
	}

	public ObjectHashList(ObjectIterator<? extends T> i) {
		super(i);
	}

	@Override
	public int indexOf(Object k) {
		if (k == null) return -1;
		int hash = k.hashCode();
		for (int i = 0; i < size; i++) if (hash == Objects.hashCode(a[i])) return i;
		return -1;
	}

	@Override
	public int lastIndexOf(Object k) {
		if (k == null) return -1;
		int hash = k.hashCode();
		for (int i = size; i-- != 0;) if (hash == Objects.hashCode(a[i])) return i;
		return -1;
	}

}
