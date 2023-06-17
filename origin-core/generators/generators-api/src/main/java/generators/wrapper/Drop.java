package generators.wrapper;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

/**
 * @author vadim
 */
public interface Drop {

	double getSellPrice();

	Material getDropType();

	ItemStack createDrop();

}
