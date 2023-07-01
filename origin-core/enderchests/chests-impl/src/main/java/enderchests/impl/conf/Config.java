package enderchests.impl.conf;

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

public class Config extends YamlFile {

	public static final int CHEST_SIZE = 9 * 3;

	public Config(ResourceProvider resourceProvider) {
		super("config.yml", resourceProvider);
		setDefaultTemplate();
	}

	private ItemStack getItem(String path) {
		ConfigurationAccessor conf = getConfigurationAccessor().getPath(path);

		String   name = conf.getString("name");
		String[] lore = conf.getStringArray("lore");
		Material type = Material.matchMaterial(conf.getString("type"));

		if (name == null || lore == null || type == null)
			logError(resourceProvider.getLogger(), path, "item element");
		assert type != null;

		return ItemBuilder.create(type).displayName(name).lore(lore).build();
	}

	private UnformattedItem getUnformatted(String path) {
		ConfigurationAccessor conf = getConfigurationAccessor().getPath(path);
		Material type = null;
		if(conf.has("type")) {
			type = Material.matchMaterial(conf.getString("type"));
			if(type == null)
				logError(resourceProvider.getLogger(), path + ".type", "item type");
		}
		return new UnformattedItem(type, conf.getPlaceholder("name"), Arrays.stream(conf.getStringArray("lore")).map(UnformattedMessage::new).map(PlaceholderMessage.class::cast).toList());
	}

	public ItemStack getMenuNext() {
		return getItem("menu.button.next");
	}

	public ItemStack getMenuBack() {
		return getItem("menu.button.back");
	}

	public ItemStack getMenuDone() {
		return getItem("menu.button.done");
	}

	public PlaceholderMessage getChestMenuTitle() {
		return getConfigurationAccessor().getPlaceholder("linked_inventory.title");
	}

	public static final class UnformattedItem {

		private final Material material;
		private final PlaceholderMessage name;
		private final List<PlaceholderMessage> lore;

		private UnformattedItem(Material material, PlaceholderMessage name, List<PlaceholderMessage> lore) {
			this.material = material;
			this.name     = name;
			this.lore     = lore;
		}

		public ItemBuilder format(Placeholder placeholder) {
			if(material == null)
				throw new UnsupportedOperationException("type unset, call #format(Material, Placeholder)");
			return format(material, placeholder);
		}

		public ItemBuilder format(Material material, Placeholder placeholder) {
			return ItemBuilder.create(material)
							  .displayName(name.format(placeholder))
							  .lore(lore.stream().map(msg -> msg.format(placeholder)).toList());
		}

	}

}
