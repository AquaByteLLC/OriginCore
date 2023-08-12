package tools.impl.tool.type;

import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.tool.IBaseTool;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface IEnchantedTool extends IBaseTool {
	ItemStack formatMenuItemFor(AttributeKey key);

	void addEnchant(AttributeKey enchantKey, long level);

	void removeEnchant(AttributeKey enchantKey);

	void removeAllEnchants();

	boolean hasEnchant(AttributeKey enchantKey);

	void makeEnchantable();

	boolean isEnchantable();

	Set<AttributeKey> getAllEnchants();

	BigDecimal getChance(AttributeKey enchantKey);

	BigDecimal getCost(AttributeKey enchantKey);

	long getLevel(AttributeKey enchantKey);

	List<String> getEnchants();

	boolean activate(AttributeKey enchantKey);
}
