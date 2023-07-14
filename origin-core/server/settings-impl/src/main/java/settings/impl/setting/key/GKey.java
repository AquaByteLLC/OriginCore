package settings.impl.setting.key;

import commons.util.StringUtil;
import org.jetbrains.annotations.Nullable;
import settings.setting.key.GlobalKey;
import settings.setting.key.LocalKey;

import java.util.Arrays;
import java.util.Objects;

import static settings.impl.setting.key.SKey.*;

/**
 * @author vadim
 */
public class GKey implements GlobalKey {

	private final String[] path;

	private final LocalKey tail;

	public GKey(String... path) {
		if (path.length < 1)
			throw new IllegalArgumentException("Invalid path of length " + path.length);

		LocalKey tail = null;

		String last = path[path.length-1];
		if(last != null && last.contains(":")) { // assign tail from last element
			String[] split = last.split(":");
			if(split.length == 2)
				tail = LKey.of(split[1]);
			else
				throw new IllegalArgumentException("Invalid path element at index "+(path.length-1)+": "+last);
			path[path.length-1] = split[0]; // pass along to the verifier
		}

		for (int i = 0; i < path.length; i++) // verify each path element
			if (path[i] == null || !LEGAL.matcher(path[i]).matches())
				throw new IllegalArgumentException("Invalid path element at index " + i + ": " + path[i]);

		this.path = Arrays.copyOf(path, path.length, String[].class);
		this.tail = tail;
	}

	private GKey(String[] path, LocalKey tail) {
		this.path = path;
		this.tail = tail;
	}

	public static GlobalKey of(String path) {
		if(path == null) throw new NullPointerException();
		return new GKey(path.split("\\" + DELIM));
	}

	public static GlobalKey convert(String illegal) {
		if(illegal == null) throw new NullPointerException();
		illegal = StringUtil.stripColor(illegal);
		System.out.println(illegal + " : First");
		illegal = illegal.toLowerCase().replace(' ', '_');
		System.out.println(illegal + " : Second");
		illegal = ILLEGAL.matcher(illegal).replaceAll("");
		System.out.println(illegal + " : Third");
		if(illegal.isBlank())
			throw new IllegalArgumentException("Key is blank after conversion.");
		return of(illegal);
	}

	@Override
	public String full() {
		StringBuilder builder = new StringBuilder(String.join(String.valueOf(DELIM), path));
		if(hasTail())
			builder.append(TAIL).append(tail.identifier());
		return builder.toString();
	}

	@Override
	public String[] parts() {
		return Arrays.copyOf(path, path.length, String[].class);
	}

	@Override
	public @Nullable GlobalKey getParent() {
		return isRootKey() ? null : new GKey(Arrays.copyOfRange(path, 0, path.length - 1));
	}

	@Override
	public boolean isRootKey() {
		return path.length == 1;
	}

	@Override
	public boolean hasTail() {
		return tail != null;
	}

	@Override
	public @Nullable LocalKey getTail() {
		return tail;
	}

	@Override
	public GlobalKey append(GlobalKey... keys) {
		for (int i = 0; i < path.length; i++)
			if (path[i] == null)
				throw new IllegalArgumentException("Invalid key at index " + i + ": " + path[i]);

		StringBuilder builder = new StringBuilder(full());
		for (GlobalKey key : keys)
			builder.append(DELIM).append(key.full());
		return of(builder.toString());
	}

	@Override
	public GlobalKey withTail(LocalKey end) {
		return new GKey(path, end);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof GlobalKey key)) return false;
		if(hasTail() != key.hasTail()) return false;
		return Arrays.equals(path, key.parts()) && Objects.equals(tail, key.getTail());
	}

	@Override
	public int hashCode() {
		int result = Objects.hashCode(tail);
		result = 31 * result + Arrays.hashCode(path);
		return result;
	}

	@Override
	public String toString() {
		return full();
	}

}
