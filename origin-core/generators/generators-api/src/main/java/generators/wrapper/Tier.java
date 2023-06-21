package generators.wrapper;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author vadim
 */
public interface Tier {

	int getIndex();

	String getName();

	Material getBlock();

	Upgrade getNextUpgrade();

	/**
	 * @return is there another upgrade?
	 */
	boolean isMaxed();

	Drop getDrop();

	ItemStack getMenuItem();

}
