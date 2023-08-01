package farming.impl.conf;

import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class GeneralConfig extends YamlFile {
	public GeneralConfig(ResourceProvider resourceProvider) {
		super("general.yml", resourceProvider);
		setDefaultTemplate();
	}

	@Override
	public YamlConfiguration getConfiguration() {
		return super.getConfiguration();
	}

	public ItemStack getHoe() {
		return getItem("hoe.item");
	}
}
