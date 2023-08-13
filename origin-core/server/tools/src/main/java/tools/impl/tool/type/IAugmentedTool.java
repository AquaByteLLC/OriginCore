package tools.impl.tool.type;

import commons.math.MathUtils;
import commons.util.BukkitUtil;
import commons.util.StringUtil;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.augments.Augment;
import tools.impl.registry.impl.BaseAttributeRegistry;
import tools.impl.tool.IBaseTool;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IAugmentedTool extends IBaseTool {

	NamespacedKey reqKey = new NamespacedKey("augments", "_augmentable");
	NamespacedKey amountKey = new NamespacedKey("augments", "slots_open");
	NamespacedKey applierKey = new NamespacedKey("augments", "applicable");
	NamespacedKey applierData = new NamespacedKey("augments", "applicable_amount");

	String reqValue = "isAugmentable";

	ItemStack formatMenuItemFor(AttributeKey key);

	void addAugment(AttributeKey enchantKey, long boost);

	void removeAugmentSlot(int amount);

	void removeAugment(AttributeKey augmentKey);

	void removeAllAugments();

	boolean hasAugment(AttributeKey augmentKey);

	void makeAugmentable(int startingSlots);

	boolean isAugmentable();

	boolean hasOpenSlot();

	void setOpenSlots(int slots);

	Set<AttributeKey> getAllAugments();

	long getBoost(AttributeKey augmentKey);

	List<String> getAugments();

	boolean activate(AttributeKey augmentKey);

	public class Applier {
		public long random(AttributeKey key) {
			final BaseAttributeRegistry<Augment> registry = ToolsPlugin.getPlugin().getAugmentRegistry();
			final Augment augment = registry.getByKey(key);
			final long min = augment.getMinimumBoost();
			final long max = augment.getMaximumBoost();
			return MathUtils.random(min, max);
		}

		public ItemStack stack(String type) {
			final BaseAttributeRegistry<Augment> registry = ToolsPlugin.getPlugin().getAugmentRegistry();
			final AttributeKey key = registry.keyFromName(type);
			final Augment augment = registry.getByKey(key);
			final long boost = random(key);

			final Placeholder pl = StringPlaceholder.builder()
					.set("boost", StringUtil.formatNumber(boost))
					.set("name", key.getName()).set("information", StringUtil.colorize(String.join("\n", augment.getInformation())))
					.set("appliedLore", StringUtil.colorize(augment.getAppliedLore()))
					.set("targets", StringUtil.colorize(augment.getAttributeTargets().stream().map(Enum::name).collect(Collectors.joining("\n")))).build();

			final ItemStack item = augment.getAugmentStack();
			IBaseTool.writeContainer(item, pdc -> pdc.set(applierKey, PersistentDataType.STRING, type));
			IBaseTool.writeContainer(item, pdc -> pdc.set(applierData, PersistentDataType.LONG, boost));

			BukkitUtil.formatItem(pl, item);

			return item;
		}
	}
}
