package generators.impl.conf;

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
public class Config extends YamlFile {

	public Config(ResourceProvider resourceProvider) {
		super("config.yml", resourceProvider);
		setDefaultTemplate();
	}

	public long getDropRateTicks() { return Math.round(getConfigurationAccessor().getDouble("drop_rate_seconds") / 20.0); }

	public int getDefaultMaxSlots() { return getConfigurationAccessor().getInt("default_slots"); }

	public UnformattedItem getGeneratorDrop() {
		ConfigurationAccessor    conf = getConfigurationAccessor().getObject("drop_item");
		return new UnformattedItem(conf.getPlaceholder("name"), Arrays.stream(conf.getStringArray("lore")).map(UnformattedMessage::new).map(PlaceholderMessage.class::cast).toList());
	}

	public static final class UnformattedItem {

		private final PlaceholderMessage       name;
		private final List<PlaceholderMessage> lore;

		private UnformattedItem(PlaceholderMessage name, List<PlaceholderMessage> lore) {
			this.name = name;
			this.lore = lore;
		}

		public ItemBuilder format(Material material, Placeholder placeholder) {
			return ItemBuilder.create(material).displayName(name.format(placeholder)).lore(lore.stream().map(msg -> msg.format(placeholder)).toList());
		}

	}

}
