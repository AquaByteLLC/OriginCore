package generators.impl.conf;

import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;

/**
 * @author vadim
 */
public class Messages extends YamlFile {

	public Messages(ResourceProvider resourceProvider) {
		super("messages.yml", resourceProvider);
		setDefaultTemplate();
	}

}
