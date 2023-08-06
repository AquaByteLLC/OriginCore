package settings.setting.key;

import settings.Settings;

/**
 * A local {@link GlobalKey}, or, a {@link GlobalKey} but with only one {@linkplain GlobalKey#parts() path element}
 * @see GlobalKey#hasTail()
 * @see GlobalKey#getTail()
 * @see GlobalKey#withTail(LocalKey)
 */
public non-sealed interface LocalKey extends SettingKey {

	/**
	 * Factory method that leniently constructs a new {@link LocalKey} from {@code identifier}.
	 */
	static LocalKey fromString(String identifier) {
		return Settings.api().localKey(identifier);
	}

	/**
	 * @return the identifier of this local key
	 */
	String identifier();

}