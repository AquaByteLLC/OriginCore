package generators.impl.conf;

import commons.conf.BukkitConfig;
import commons.util.StringUtil;
import generators.wrapper.Upgrade;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.wrapper.EffectGroup;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import me.vadim.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * @author vadim
 */
public class Config extends BukkitConfig {

	@SuppressWarnings("DataFlowIssue")
	public Config(ResourceProvider resourceProvider) {
		super("config.yml", resourceProvider);
		setDefaultTemplate();
		yaml.getDefaults().set("effects", null); // remove defaults for effects so it won't auto-replace deleted fields
		setUnformattedItemFactory(GensItem::new);
	}

	public long getDropRateTicks() {
		return Math.round(getConfigurationAccessor().getDouble("drop_rate_seconds") * 20.0);
	}

	public int getDefaultMaxSlots() {
		return getConfigurationAccessor().getInt("default_slots");
	}

	public int getAutosaveIntervalTicks() {
		return getConfigurationAccessor().getInt("autosave_interval_minutes") * 60 * 20;
	}

	public GensItem getGeneratorDrop() {
		return getUnformatted("drop_item");
	}

	public GensItem getGeneratorItem() {
		return getUnformatted("gen_item");
	}

	public GensItem getBuyMenuTierItem() {
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

	public GensItem getManageMenuIndividualUpgrade() {
		return getUnformatted("manage_menu.individual_view.upgrade");
	}

	public GensItem getManageMenuIndividualDelete() {
		return getUnformatted("manage_menu.individual_view.delete");
	}

	public EffectGroup getBuyEffect() {
		return getEffect("effects.buy");
	}

	public EffectGroup getCreateEffect() {
		return getEffect("effects.create");
	}

	public EffectGroup getUpgradeEffect() {
		return getEffect("effects.upgrade");
	}

	public EffectGroup getDestroyEffect() {
		return getEffect("effects.destroy");
	}

	public EffectGroup getErrorEffect() {
		return getEffect("effects.error");
	}
	private String getGenMenuItemMaxLevel() {
		return getConfigurationAccessor().getObject("gen_item").getString("upgrade_price_max");
	}

	private String getGenMenuItemPriceSymbol() {
		return getConfigurationAccessor().getObject("gen_item").getString("price_symbol");
	}

	public final class GensItem extends UnformattedItem {

		public GensItem(Material material, PlaceholderMessage name, List<PlaceholderMessage> lore) {
			super(material, name, lore);
		}

		private double upgradePrice;

		public GensItem asMaxLevel() {
			upgradePrice = Double.NaN;
			return this;
		}

		public GensItem withUpgradePrice(double price) {
			upgradePrice = price;
			return this;
		}

		public GensItem upgrade(Upgrade upgrade) {
			if (upgrade == null)
				return asMaxLevel();
			else
				return withUpgradePrice(upgrade.getPrice());
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
