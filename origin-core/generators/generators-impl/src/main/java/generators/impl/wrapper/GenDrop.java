package generators.impl.wrapper;

import generators.wrapper.Drop;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author vadim
 */
public class GenDrop implements Drop {

	private final double sellPrice;
	private final ItemStack drop;

	public GenDrop(double sellPrice, ItemStack drop) {
		this.sellPrice = sellPrice;
		this.drop      = drop.clone();
	}

	@Override
	public double getSellPrice() {
		return sellPrice;
	}

	@Override
	public Material getDropType() {
		return drop.getType();
	}

	@Override
	public ItemStack createDrop() {
		return drop.clone();
	}

}
