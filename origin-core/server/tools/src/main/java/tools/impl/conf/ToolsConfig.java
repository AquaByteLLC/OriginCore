package tools.impl.conf;

import commons.conf.BukkitConfig;
import me.vadim.util.conf.ResourceProvider;

public class ToolsConfig extends BukkitConfig {

	public ToolsConfig(ResourceProvider resourceProvider) {
		super("tools.yml", resourceProvider);
	}

}
