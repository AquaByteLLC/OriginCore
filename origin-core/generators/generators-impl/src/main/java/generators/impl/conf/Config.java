package generators.impl.conf;

import commons.StringUtil;
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

import java.util.ArrayList;
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
		return getUnformatted("drop_item");
	}

	public UnformattedItem getGeneratorItem() {
		return getUnformatted("gen_item");
	}

	public UnformattedItem getBuyMenuTierItem() {
		return getUnformatted("buy_menu.tier_item");
	}

	public String getBuyMenuTitle() {
		return getConfigurationAccessor().getObject("buy_menu").getString("title");
	}

	public String getManageMenuTitle() {
		return getConfigurationAccessor().getObject("manage_menu").getString("title");
	}

	public ItemStack getBulkUpgradePlus1() {
		return getItem("manage_menu.list_view.bulk_upgrade_plus1");
	}

	public ItemStack getBulkUpgradeMax() {
		return getItem("manage_menu.list_view.bulk_upgrade_max");
	}

	public List<String> getBulkUpgradeAd() {
		return Arrays.asList(getConfigurationAccessor().getObject("manage_menu").getObject("list_view").getStringArray("bulk_upgrade_ad"));
	}

	public UnformattedItem getManageMenuIndividualUpgrade() {
		return getUnformatted("manage_menu.individual_view.upgrade");
	}

	public UnformattedItem getManageMenuIndividualDelete() {
		return getUnformatted("manage_menu.individual_view.delete");
	}

	private String getGenMenuItemMaxLevel() {
		return getConfigurationAccessor().getObject("gen_item").getString("upgrade_price_max");
	}

	private String getGenMenuItemPriceSymbol() {
		return getConfigurationAccessor().getObject("gen_item").getString("price_symbol");
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

		private final Material material;
		private final PlaceholderMessage       name;
		private final List<PlaceholderMessage> lore;

		private UnformattedItem(Material material, PlaceholderMessage name, List<PlaceholderMessage> lore) {
			this.material = material;
			this.name     = name;
			this.lore     = lore;
		}

		private double upgradePrice;

		public UnformattedItem asMaxLevel() {
			upgradePrice = Double.NaN;
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

		public ItemBuilder format(Placeholder placeholder) {
			if(material == null)
				throw new UnsupportedOperationException("type unset, call #format(Material, Placeholder)");
			return format(material, placeholder);
		}

		public ItemBuilder format(Material material, Placeholder placeholder) {
			Placeholder pl = StringPlaceholder.builder()
											  .set("price_symbol", Double.isNaN(upgradePrice) ? "" : getGenMenuItemPriceSymbol())
											  .set("upgrade_price", Double.isNaN(upgradePrice) ? getGenMenuItemMaxLevel() : StringUtil.formatNumber(upgradePrice))
											  .build();

			return ItemBuilder.create(material)
							  .displayName(pl.format(name.format(placeholder)))
							  .lore(lore.stream().map(msg -> pl.format(msg.format(placeholder))).toList());
		}

	}

}
