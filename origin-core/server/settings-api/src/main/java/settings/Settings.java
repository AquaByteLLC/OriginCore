package settings;

import settings.builder.SettingsFactory;
import settings.registry.SectionRegistry;
import settings.registry.SettingsRegistry;

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

	public Settings(SectionRegistry registry, SettingsRegistry global, SettingsFactory factory) {
		if (api != null)
			throw new UnsupportedOperationException("use static method #api()");

		this.registry = registry;
		this.global   = global;
		this.factory  = factory;

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

}
