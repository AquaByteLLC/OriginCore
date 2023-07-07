package enchants.impl.conf;

import commons.conf.BukkitConfig;
import enchants.item.Enchant;
import me.vadim.util.conf.ConfigurationAccessor;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import me.vadim.util.conf.wrapper.impl.UnformattedMessage;
import me.vadim.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class GeneralConfig extends BukkitConfig {
	public GeneralConfig(ResourceProvider resourceProvider) {
		super("general.yml", resourceProvider);
		setDefaultTemplate();
	}

	public UnformattedItem getMenuUpgrade() {
		return getUnformatted("menu.button.upgrade");
	}

	public PlaceholderMessage getEnchantMenuTitle() {
		return getConfigurationAccessor().getObject("menu").getPlaceholder("title");
	}

}
