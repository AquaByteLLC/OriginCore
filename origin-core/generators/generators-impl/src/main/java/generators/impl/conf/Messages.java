package generators.impl.conf;

import commons.conf.BukkitConfig;
import commons.conf.wrapper.OptionalMessage;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;
import me.vadim.util.conf.wrapper.PlaceholderMessage;

/**
 * @author vadim
 */
public class Messages extends BukkitConfig {

	public Messages(ResourceProvider resourceProvider) {
		super("messages.yml", resourceProvider);
		setDefaultTemplate();
	}

	public OptionalMessage getCreatedGen() {
		return getOptional("gen.created");
	}

	public OptionalMessage getUpgradedGen() {
		return getOptional("gen.upgraded");
	}

	public OptionalMessage getDestroyedGen() {
		return getOptional("gen.destroyed");
	}

	public OptionalMessage getDestroyedGenAdmin() {
		return getOptional("gen.destroyed_admin");
	}

	public OptionalMessage getAtLimit() {
		return getOptional("gen.slot_limit_reached");
	}

	public OptionalMessage getInvalidLocation() {
		return getOptional("gen.err_invalid_location");
	}

}
