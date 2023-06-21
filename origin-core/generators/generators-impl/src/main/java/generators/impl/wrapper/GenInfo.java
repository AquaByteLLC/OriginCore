package generators.impl.wrapper;

import commons.StringUtil;
import generators.impl.conf.Config;
import generators.wrapper.Drop;
import generators.wrapper.Tier;
import generators.wrapper.Upgrade;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author vadim
 */
public class GenInfo implements Tier {

	private final int       index;
	private final String    name;
	private final Material  block;
	private final Upgrade   upgrade;
	private final Drop      drop;
	private final ItemStack menuItem;

	public GenInfo(int index, String name, Material block, Upgrade upgrade, Drop drop, ConfigurationProvider prov) {
		this.index    = index;
		this.name     = name;
		this.block    = block;
		this.upgrade  = upgrade;
		this.drop     = drop;
		this.menuItem = prov.open(Config.class).getGeneratorMenuItem()
							.upgrade(upgrade)
							.format(block, StringPlaceholder.builder()
															.set("drop_name", StringUtil.convertToUserFriendlyCase(drop.getDropType().name()))
															.set("drop_price", String.valueOf(drop.getSellPrice()))
															.build())
							.build();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public String getName() {
		return name;
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

}
