package mining.impl.conf;

import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;
import org.bukkit.configuration.file.YamlConfiguration;

public class BlocksConfig extends YamlFile {

	public BlocksConfig(ResourceProvider resourceProvider) {
		super("blocks.yml", resourceProvider);
		setDefaultTemplate();
	}

	@Override
	public YamlConfiguration getConfiguration() {
		return super.getConfiguration();
	}

}
