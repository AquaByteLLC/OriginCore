package generators.wrapper;

import generators.GeneratorRegistry;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
public interface Upgrade {

	Tier getNextTier();

	double getPrice();

}
