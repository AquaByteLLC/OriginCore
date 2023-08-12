package tools.impl.attribute.skins;

import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.ExpiringAttribute;

import java.util.List;

public interface Skin extends ExpiringAttribute {
	List<String> getInformation();

	String getAppliedLore();

	int getModelData();

	ItemStack getSkinStack();
}
