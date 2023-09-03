package tools.impl.tool.type;

import org.bukkit.NamespacedKey;
import tools.impl.attribute.AttributeKey;
import tools.impl.tool.IBaseTool;

import java.util.List;
import java.util.Set;

public interface IAugmentedTool extends IBaseTool {

	NamespacedKey reqKey = new NamespacedKey("augments", "_augmentable");
	NamespacedKey amountKey = new NamespacedKey("augments", "slots_open");
	NamespacedKey maxSlotsKey = new NamespacedKey("augments", "slots_max");
	NamespacedKey applierKey = new NamespacedKey("augments", "applicable");
	NamespacedKey applierData = new NamespacedKey("augments", "applicable_amount");

	String reqValue = "isAugmentable";

	void addAugment(AttributeKey enchantKey, long boost);

	int getOpenSlots();

	void removeAugmentSlot(int amount);

	void removeAugment(AttributeKey augmentKey);

	void removeAllAugments();

	boolean hasAugment(AttributeKey augmentKey);

	void makeAugmentable(int startingSlots);

	boolean isAugmentable();

	boolean hasOpenSlot();

	void setOpenSlots(int slots);

	int getMaxSlots();

	void setMaxSlots(int max);

	Set<AttributeKey> getAllAugments();

	long getBoost(AttributeKey augmentKey);

	List<String> getAugments();

	boolean activate(AttributeKey augmentKey);

}
