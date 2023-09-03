package tools.impl.attribute.augments.impl.item;

import commons.util.BukkitUtil;
import commons.util.StringUtil;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.augments.Augment;
import tools.impl.registry.AttributeRegistry;
import tools.impl.tool.IBaseTool;

import java.util.Random;
import java.util.stream.Collectors;

import static tools.impl.tool.type.IAugmentedTool.*;

public class AugmentApplier {

	public ItemStack stack(String type) {
		final AttributeRegistry<Augment> registry = ToolsPlugin.getPlugin().getAugmentRegistry();
		final AttributeKey               key      = registry.keyFromName(type);
		final Augment                    augment  = registry.getByKey(key);

		Random random = new Random(System.currentTimeMillis());
		long min = augment.getMinimumBoost();
		long max = augment.getMaximumBoost();
		long boost = random.nextLong(min + 1, max);

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

	public boolean hasKeys(String type, ItemStack stack) {
		PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
		return container.has(applierKey) && container.get(applierKey, PersistentDataType.STRING).equals(type);
	}
}
