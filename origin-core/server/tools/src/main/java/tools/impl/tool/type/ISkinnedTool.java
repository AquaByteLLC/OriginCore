package tools.impl.tool.type;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.tool.IBaseTool;

public interface ISkinnedTool extends IBaseTool {

	NamespacedKey reqKey = new NamespacedKey("skins", "_skinnable");
	NamespacedKey hasSkin = new NamespacedKey("skins", "hasSkin");
	NamespacedKey applierKey = new NamespacedKey("skins", "applicable");

	String reqValue = "isSkinnable";

	ItemStack formatMenuItemFor(AttributeKey key);

	void addSkin(AttributeKey enchantKey);

	void removeSkin();

	void makeSkinnable();

	boolean isSkinnable();

	AttributeKey getSkin();

	boolean hasSkin(AttributeKey enchantKey);
}
