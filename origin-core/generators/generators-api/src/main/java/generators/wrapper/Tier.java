package generators.wrapper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 * @author vadim
 */
public interface Tier {

	int getIndex();

	double getBuyPrice();

	Material getBlock();

	Upgrade getNextUpgrade();

	/**
	 * @return is there another upgrade?
	 */
	boolean isMaxed();

	Drop getDrop();

	ItemStack getMenuItem();

	ItemStack getGeneratorItem(OfflinePlayer owner);

	Generator toGenerator(OfflinePlayer owner, Location location);

}
