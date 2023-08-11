package tools.impl.attribute.augments;

import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.BaseAttribute;

import java.util.List;

public interface Augment extends BaseAttribute {
	long getMinimumBoost();

	long getMaximumBoost();

	String getAppliedLore();

	List<String> getInformation();

	ItemStack getAugmentStack();
}
