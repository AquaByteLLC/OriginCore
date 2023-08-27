package tools.impl.conf.attr;

import commons.conf.BukkitConfig;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.wrapper.PlaceholderMessage;

public class EnchantMenuConfig extends BukkitConfig {
	public EnchantMenuConfig(ResourceProvider resourceProvider) {
		super("enchantMenuConfig.yml", resourceProvider);
		setDefaultTemplate();
	}

	public UnformattedItem getMenuUpgrade() {
		return getUnformatted("menu.button.upgrade");
	}

	public PlaceholderMessage getEnchantMenuTitle() {
		return getConfigurationAccessor().getObject("menu").getPlaceholder("title");
	}

}
