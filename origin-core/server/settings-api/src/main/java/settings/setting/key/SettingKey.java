package settings.setting.key;

public sealed interface SettingKey permits GlobalKey, LocalKey {

	/**
	 * @return the full path of this key
	 */
	String toString();

}