package blocks.impl.handler;

import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;

public class BlocksConfig extends YamlFile {

	public BlocksConfig(ResourceProvider resourceProvider) {
		super("blocks.yml", resourceProvider);
	}
}
