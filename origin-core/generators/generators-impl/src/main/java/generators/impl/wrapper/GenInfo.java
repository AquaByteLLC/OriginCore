package generators.impl.wrapper;

import generators.wrapper.Drop;
import generators.wrapper.Tier;
import generators.wrapper.Upgrade;
import org.bukkit.Material;

/**
 * @author vadim
 */
public class GenInfo implements Tier {

	private final String name;
	private final Material block;
	private final Upgrade upgrade;
	private final Drop drop;

	public GenInfo(String name, Material block, Upgrade upgrade, Drop drop) {
		this.name    = name;
		this.block   = block;
		this.upgrade = upgrade;
		this.drop    = drop;
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

}
