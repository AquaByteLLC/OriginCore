package generators.wrapper;

import org.bukkit.Material;

/**
 * @author vadim
 */
public interface Tier {

	String getName();

	Material getBlock();

	Upgrade getNextUpgrade();

	/**
	 * @return is there another upgrade?
	 */
	boolean isMaxed();

	Drop getDrop();

}
