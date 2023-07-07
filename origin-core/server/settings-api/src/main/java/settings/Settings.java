package settings;

import org.bukkit.entity.Player;
import settings.registry.SettingsHolder;
import settings.setting.builder.SettingsFactory;
import settings.registry.SectionRegistry;
import settings.registry.SettingsRegistry;
import settings.setting.key.GlobalKey;
import settings.setting.key.LocalKey;

import java.util.function.Function;

/**
 * @author vadim
 */
public class Settings {

	private static Settings api;

	public static Settings api() {
		if (api == null)
			throw new NullPointerException("Not initialized! Did you accidentally shade in the entire settings lib?");
		return api;
	}

	private final SectionRegistry registry;
	private final SettingsRegistry global;
	private final SettingsFactory factory;
	private final Function<Player, SettingsHolder> getAccount;
	private final Function<String, LocalKey> newLocalKey;
	private final Function<String, GlobalKey> newGlobalKey;

	public Settings(SectionRegistry registry, SettingsRegistry global, SettingsFactory factory, Function<Player, SettingsHolder> getAccount, Function<String, settings.setting.key.LocalKey> newLocalKey, Function<String, GlobalKey> newGlobalKey) {
		if (api != null)
			throw new UnsupportedOperationException("use static method #api()");

		this.registry     = registry;
		this.global       = global;
		this.factory      = factory;
		this.getAccount   = getAccount;
		this.newLocalKey  = newLocalKey;
		this.newGlobalKey = newGlobalKey;

		api = this; // personally I dislike this but it's lighyears ahead of whatever you were doing with guice
	}

	public SectionRegistry getSections() {
		return registry;
	}

	public SettingsRegistry getGlobal() {
		return global;
	}

	public SettingsFactory getFactory() {
		return factory;
	}

	public SettingsHolder getSettings(Player player) {
		return getAccount.apply(player);
	}

	public LocalKey localKey(String identifier) {
		return newLocalKey.apply(identifier);
	}

	public GlobalKey globalKey(String path) {
		return newGlobalKey.apply(path);
	}

}
