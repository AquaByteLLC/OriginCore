package tools.impl.tool.type;

import org.bukkit.NamespacedKey;
import tools.impl.attribute.AttributeKey;
import tools.impl.tool.IBaseTool;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface IEnchantedTool extends IBaseTool {

	NamespacedKey reqKey = new NamespacedKey("enchants", "_enchantable");
	String reqValue = "isEnchantable";

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
