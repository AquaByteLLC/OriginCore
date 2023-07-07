package enderchests.impl.conf;

import commons.conf.BukkitConfig;
import me.vadim.util.conf.ConfigurationAccessor;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import me.vadim.util.conf.wrapper.impl.UnformattedMessage;
import me.vadim.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Config extends BukkitConfig {

	public static final int CHEST_SIZE = 9 * 3;

	public Config(ResourceProvider resourceProvider) {
		super("config.yml", resourceProvider);
		setDefaultTemplate();
	}

	public PlaceholderMessage getChestMenuTitle() {
		return getConfigurationAccessor().getPlaceholder("linked_inventory.title");
	}

}
