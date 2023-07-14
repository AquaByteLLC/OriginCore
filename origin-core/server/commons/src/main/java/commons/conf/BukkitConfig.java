package commons.conf;

import me.vadim.util.conf.ConfigurationAccessor;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.YamlFile;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import me.vadim.util.conf.wrapper.impl.UnformattedMessage;
import me.vadim.util.item.ItemBuilder;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

/**
 * @author vadim
 */
public abstract class BukkitConfig extends YamlFile {

	public BukkitConfig(String file, ResourceProvider resourceProvider) {
		super(file, resourceProvider);
	}

	// for custom UnformattedItem impls
	private UnformattedItemFactory ufif = UnformattedItem::new;

	protected void setUnformattedItemFactory(UnformattedItemFactory factory) {
		if (factory != null)
			this.ufif = factory;
	}

	@SuppressWarnings("unchecked")
	protected <U extends UnformattedItem> U getUnformatted(String path) {
		ConfigurationAccessor conf = getConfigurationAccessor().getPath(path);

		Material type = null;
		if (conf.has("type")) {
			type = Material.matchMaterial(conf.getString("type"));
			if (type == null)
				logError(resourceProvider.getLogger(), path + ".type", "item type");
		}
		return (U) ufif.newUnformattedItem(type, conf.getPlaceholder("name"), Arrays.stream(conf.getStringArray("lore")).map(UnformattedMessage::new).map(PlaceholderMessage.class::cast).toList());
	}

	protected interface UnformattedItemFactory {

		UnformattedItem newUnformattedItem(Material material, PlaceholderMessage name, List<PlaceholderMessage> lore);

	}

	public static class UnformattedItem {

		protected final Material material;
		protected final PlaceholderMessage name;
		protected final List<PlaceholderMessage> lore;

		public UnformattedItem(Material material, PlaceholderMessage name, List<PlaceholderMessage> lore) {
			this.material = material;
			this.name     = name;
			this.lore     = lore;
		}

		public ItemBuilder format(Placeholder placeholder) {
			if (material == null)
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
