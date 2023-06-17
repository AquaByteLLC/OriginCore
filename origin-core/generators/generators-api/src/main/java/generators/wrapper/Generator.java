package generators.wrapper;

import commons.Owned;
import org.bukkit.Location;

/**
 * @author vadim
 */
public interface Generator extends Owned {

	Tier getCurrentTier();

	Location getBlockLocation();

}