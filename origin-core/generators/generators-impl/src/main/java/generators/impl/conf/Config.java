package generators.impl.conf;

import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;

/**
 * @author vadim
 */
public class Config extends YamlFile {

	public Config(ResourceProvider resourceProvider) {
		super("config.yml", resourceProvider);
		setDefaultTemplate();
	}

	public long getDropRateTicks() { return Math.round(getConfigurationAccessor().getDouble("drop_rate_seconds") / 20.0);}

	public int getDefaultMaxSlots() { return getConfigurationAccessor().getInt("default_slots"); }



}
