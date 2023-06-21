package generators.impl.conf;

import generators.wrapper.Upgrade;
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

/**
 * @author vadim
 */
public class Config extends YamlFile {

	public Config(ResourceProvider resourceProvider) {
		super("config.yml", resourceProvider);
		setDefaultTemplate();
	}

	public long getDropRateTicks() { return Math.round(getConfigurationAccessor().getDouble("drop_rate_seconds") * 20.0); }

	public int getDefaultMaxSlots() { return getConfigurationAccessor().getInt("default_slots"); }

	public int getAutosaveIntervalTicks() { return getConfigurationAccessor().getInt("autosave_interval_minutes") * 60 * 20; }

	public UnformattedItem getGeneratorDrop() {
		ConfigurationAccessor conf = getConfigurationAccessor().getObject("drop_item");
		return new UnformattedItem(conf.getPlaceholder("name"), Arrays.stream(conf.getStringArray("lore")).map(UnformattedMessage::new).map(PlaceholderMessage.class::cast).toList());
	}

	public UnformattedItem getGeneratorMenuItem() {
		ConfigurationAccessor conf = getConfigurationAccessor().getObject("gen_menu_item");
		return new UnformattedItem(conf.getPlaceholder("name"), Arrays.stream(conf.getStringArray("lore")).map(UnformattedMessage::new).map(PlaceholderMessage.class::cast).toList());
	}

	private String getGenMenuItemMaxLevel() {
		return getConfigurationAccessor().getObject("gen_menu_item").getString("upgrade_price_max");
	}

	private String getGenMenuItemPriceSymbol() {
		return getConfigurationAccessor().getObject("gen_menu_item").getString("price_symbol");
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

	public ItemStack getMenuNext() {
		return getItem("menu.button.next");
	}

	public ItemStack getMenuBack() {
		return getItem("menu.button.back");
	}

	public ItemStack getMenuDone() {
		return getItem("menu.button.done");
	}

	public final class UnformattedItem {

		private final PlaceholderMessage       name;
		private final List<PlaceholderMessage> lore;

		private UnformattedItem(PlaceholderMessage name, List<PlaceholderMessage> lore) {
			this.name = name;
			this.lore = lore;
		}

		private double upgradePrice;

		public UnformattedItem asMaxLevel() {
			upgradePrice = -1;
			return this;
		}

		public UnformattedItem withUpgradePrice(double price) {
			upgradePrice = price;
			return this;
		}

		public UnformattedItem upgrade(Upgrade upgrade) {
			if (upgrade == null)
				return asMaxLevel();
			else
				return withUpgradePrice(upgrade.getPrice());
		}

		public ItemBuilder format(Material material, Placeholder placeholder) {
			Placeholder pl = StringPlaceholder.builder()
											  .set("price_symbol", upgradePrice == -1 ? "" : getGenMenuItemPriceSymbol())
											  .set("upgrade_price", upgradePrice == -1 ? getGenMenuItemMaxLevel() : String.valueOf(upgradePrice))
											  .build();
			return ItemBuilder.create(material)
							  .displayName(pl.format(name.format(placeholder)))
							  .lore(lore.stream().map(msg -> pl.format(msg.format(placeholder))).toList());
		}

	}

}
