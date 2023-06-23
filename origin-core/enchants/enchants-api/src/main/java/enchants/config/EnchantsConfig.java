package enchants.config;

import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;

import java.util.Arrays;
import java.util.List;

/**
 * @author vadim
 */
public class EnchantsConfig extends YamlFile {

	public EnchantsConfig(ResourceProvider resourceProvider) {
		super("config.yml", resourceProvider);
	}

	public List<String> getEnchantHeader(){
		return Arrays.asList(getConfigurationAccessor().getStringArray("enchantHeader"));
	}

}
