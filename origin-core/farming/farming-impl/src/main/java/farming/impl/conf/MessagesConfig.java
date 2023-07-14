package farming.impl.conf;

import commons.conf.BukkitConfig;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.wrapper.OptionalMessage;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessagesConfig extends BukkitConfig {
	public MessagesConfig(ResourceProvider resourceProvider) {
		super("messages.yml", resourceProvider);
	}

	@Override
	protected YamlConfiguration getConfiguration() {
		return super.getConfiguration();
	}

	public OptionalMessage getRegionRegister() {
		return getOptional("regions.register");
	}

	public OptionalMessage getRegionRemove() {
		return getOptional("regions.remove");
	}

	public OptionalMessage getBlockNotFound() {
		return getOptional("errors.blockNotFound");
	}

	public OptionalMessage getRegionNotRegistered() {
		return getOptional("errors.regionNotRegistered");
	}

	public OptionalMessage getNoWgRegion() {
		return getOptional("errors.wgRegionNotFound");
	}

	public OptionalMessage getEnchantProcced(String enchantKey) {
		return getOptional("enchants." + enchantKey + ".activation");
	}


}
