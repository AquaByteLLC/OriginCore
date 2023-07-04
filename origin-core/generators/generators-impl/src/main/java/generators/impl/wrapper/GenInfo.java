package generators.impl.wrapper;

import commons.util.StringUtil;
import generators.impl.conf.Config;
import generators.wrapper.Drop;
import generators.wrapper.Generator;
import generators.wrapper.Tier;
import generators.wrapper.Upgrade;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 * @author vadim
 */
public class GenInfo implements Tier {

	public static Placeholder placeholdersForTier(Tier tier) {
		Drop drop = tier.getDrop();
		return StringPlaceholder.builder()
				.set("drop_name", StringUtil.convertToUserFriendlyCase(drop.getDropType().name()))
				.set("drop_price", StringUtil.formatNumber(drop.getSellPrice()))
				.set("buy_price", StringUtil.formatNumber(tier.getBuyPrice()))
				.set("gen_tier", StringUtil.formatNumber(tier.getIndex() + 1))
				.build();
	}

	private final int index;
	private final Material block;
	private final double buy;
	private final Upgrade upgrade;
	private final Drop drop;
	private final ItemStack menuItem;
	private final ItemStack genItem;

	public GenInfo(int index, double buy, Material block, Upgrade upgrade, Drop drop, ConfigurationProvider prov) {
		this.index = index;
		this.buy = buy;
		this.block = block;
		this.upgrade = upgrade;
		this.drop = drop;
		Placeholder pl = placeholdersForTier(this);
		this.menuItem = prov.open(Config.class).getBuyMenuTierItem()
				.upgrade(upgrade)
				.format(block, pl)
				.build();
		this.genItem = prov.open(Config.class).getGeneratorItem()
				.upgrade(upgrade)
				.format(block, pl)
				.build();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public double getBuyPrice() {
		return buy;
	}

	@Override
	public Material getBlock() {
		return block;
	}

	@Override
	public Upgrade getNextUpgrade() {
		return upgrade;
	}

	@Override
	public boolean isMaxed() {
		return upgrade == null;
	}

	@Override
	public Drop getDrop() {
		return drop;
	}

	@Override
	public ItemStack getMenuItem() {
		return menuItem.clone();
	}

	@Override
	public ItemStack getGeneratorItem(OfflinePlayer owner) {
		ItemStack item = genItem.clone();
		PDCUtil.setGenOwner(item, owner);
		return item;
	}

	@Override
	public Generator toGenerator(OfflinePlayer owner, Location location) {
		return new Gen(owner, this, location);
	}

}
