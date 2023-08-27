package tools.impl.conf.attr;

import commons.conf.BukkitConfig;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.wrapper.PlaceholderMessage;

public class AugmentMenuConfig extends BukkitConfig {
	public AugmentMenuConfig(ResourceProvider resourceProvider) {
		super("augmentMenuConfig.yml", resourceProvider);
		setDefaultTemplate();
	}

	public BukkitConfig.UnformattedItem getMenuApplied() {
		return getUnformatted("menu.button.applied");
	}

	public BukkitConfig.UnformattedItem getMenuLocked() {
		return getUnformatted("menu.button.locked");
	}

	public BukkitConfig.UnformattedItem getMenuNotApplied() {
		return getUnformatted("menu.button.notApplied");
	}

	public PlaceholderMessage getAugmentMenuTitle() {
		return getConfigurationAccessor().getObject("menu").getPlaceholder("title");
	}
}
