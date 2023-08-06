package settings.setting.key;

import org.jetbrains.annotations.Nullable;
import settings.Settings;

public non-sealed interface GlobalKey extends SettingKey {

	/**
	 * Factory method that leniently constructs a new {@link GlobalKey} from {@code path}.
	 */
	static GlobalKey fromString(String path) {
		return Settings.api().globalKey(path);
	}

	/**
	 * @return the combined path of this key
	 */
	String full();

	/**
	 * @return the {@linkplain #full() full path} split into segments, exluding the {@linkplain #getTail() tail}
	 */
	String[] parts();

	/**
	 * @return the parent key or {@code null} if this is a {@linkplain #isRootKey() root key}
	 */
	@Nullable GlobalKey getParent();

	/**
	 * @return {@code true} if this is a top-level key, {@code false} otherwise
	 */
	boolean isRootKey();

	/**
	 * @return whether or not this key ends with a {@linkplain LocalKey local key}
	 */
	boolean hasTail();

	/**
	 * @return the trailing {@link LocalKey}, or {@code null} if this key is {@linkplain #hasTail() not local}
	 */
	@Nullable LocalKey getTail();

	/**
	 * Append {@code keys} to this key.
	 * @param keys the array of {@link GlobalKey}s to append to this {@linkplain GlobalKey key}
	 * @return a new {@link GlobalKey} representing {@code this + keys}
	 */
	GlobalKey append(GlobalKey... keys);

	/**
	 * Append a {@link LocalKey} to this key. If this key {@linkplain #hasTail() already has} a {@linkplain #getTail() tail}, then this method will replace the {@linkplain #getTail() tail}.
	 * @param end the {@link LocalKey} to append
	 * @return a new {@link GlobalKey} with an identical {@linkplain #full() path} to this key, but with {@code end} as its {@linkplain #getTail() tail}
	 */
	GlobalKey withTail(LocalKey end);

}