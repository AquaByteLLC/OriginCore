package generators.impl.wrapper;

import generators.wrapper.Tier;
import generators.wrapper.Upgrade;

/**
 * @author vadim
 */
public class TierUp implements Upgrade {

	private final Tier   next;
	private final double price;

	public TierUp(Tier next, double price) {
		this.next  = next;
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
