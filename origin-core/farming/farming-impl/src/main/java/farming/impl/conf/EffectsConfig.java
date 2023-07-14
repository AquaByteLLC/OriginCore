package farming.impl.conf;

import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;
import org.bukkit.configuration.file.YamlConfiguration;

public class EffectsConfig extends YamlFile {

	public EffectsConfig(ResourceProvider resourceProvider) {
		super("effects.yml", resourceProvider);
		setDefaultTemplate();
	}

	@Override
	public YamlConfiguration getConfiguration() {
		return super.getConfiguration();
	}

}
