package generators.impl.wrapper;

import generators.GeneratorRegistry;
import generators.wrapper.Generator;
import generators.wrapper.Tier;
import generators.wrapper.Upgrade;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
public class TierUp implements Upgrade {

	private final Tier next;
	private final double price;

	public TierUp(Tier next, double price) {
		this.next = next;
		this.price = price;
	}

	@Override
	public Tier getNextTier() {
		return next;
	}

	@Override
	public double getPrice() {
		return price;
	}

}
