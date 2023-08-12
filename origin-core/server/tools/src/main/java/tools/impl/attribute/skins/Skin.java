package tools.impl.attribute.skins;

import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.BaseAttribute;

import java.util.List;

public interface Skin extends BaseAttribute {
	List<String> getInformation();

	String getAppliedLore();

	int getModelData();

	ItemStack getSkinStack();
}
