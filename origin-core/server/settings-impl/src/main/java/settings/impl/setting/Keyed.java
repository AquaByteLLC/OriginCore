package settings.impl.setting;

import settings.setting.key.KeyedSetting;
import settings.setting.key.SettingKey;

abstract class Keyed<K extends SettingKey> implements KeyedSetting {

	private final K key;

	Keyed(K key) {
		this.key = key;
	}

	@Override
	public K getKey() {
		return this.key;
	}

}
