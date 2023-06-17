package generators.impl.wrapper;

import commons.impl.PlayerOwned;
import generators.wrapper.Generator;
import generators.wrapper.Tier;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

/**
 * @author vadim
 */
public class Gen extends PlayerOwned implements Generator {

	private final Tier tier;
	private final Location location;

	public Gen(OfflinePlayer owner, Tier tier, Location location) {
		super(owner.getUniqueId());
		this.tier     = tier;
		this.location = location.getBlock().getLocation().clone();
	}

	@Override
	public Tier getCurrentTier() {
		return tier;
	}

	@Override
	public Location getBlockLocation() {
		return location.clone();
	}

}
