package enchants.impl.conf;

import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;

public class GeneralConfig extends YamlFile {
	public GeneralConfig(ResourceProvider resourceProvider) {
		super("general.yml", resourceProvider);
		setDefaultTemplate();
	}
}
