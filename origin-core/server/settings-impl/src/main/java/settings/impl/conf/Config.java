package settings.impl.conf;

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

	public Config(ResourceProvider resourceProvider) {
		super("config.yml", resourceProvider);
		setDefaultTemplate();
	}

	public PlaceholderMessage getTitle() {
		return getConfigurationAccessor().getPlaceholder("menu.title");
	}

	public String getSelectedOptionPrefix() {
		return getConfigurationAccessor().getString("menu.option_prefix.selected");
	}

	public String getOtherOptionPrefix() {
		return getConfigurationAccessor().getString("menu.option_prefix.other");
	}

}
