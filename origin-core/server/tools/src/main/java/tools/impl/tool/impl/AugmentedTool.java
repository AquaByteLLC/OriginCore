package tools.impl.tool.impl;

import commons.util.BukkitUtil;
import commons.util.StringUtil;
import me.lucko.helper.text3.Text;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.augments.Augment;
import tools.impl.attribute.augments.impl.ToolAugmentFactory;
import tools.impl.registry.impl.BaseAttributeRegistry;
import tools.impl.tool.IBaseTool;
import tools.impl.tool.type.IAugmentedTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AugmentedTool implements IAugmentedTool {

	private final ItemStack itemStack;

	private static BaseAttributeRegistry<Augment> getRegistry() {
		return ToolsPlugin.getPlugin().getAugmentRegistry();
	}

	private static ToolAugmentFactory getFactory() {
		return ToolsPlugin.getPlugin().getAugmentFactory();
	}

	public AugmentedTool(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public ItemStack getItemStack() {
		return this.itemStack;
	}

	@Override
	public ItemStack formatMenuItemFor(AttributeKey key) {
		final Augment augment = getRegistry().getByKey(key);

		final Placeholder pl = StringPlaceholder.builder()
				.set("maxBoost", StringUtil.formatNumber(augment.getMaximumBoost()))
				.set("minBoost", StringUtil.formatNumber(augment.getMinimumBoost()))
				.set("name", key.getName())
				.set("currentBoost", StringUtil.formatNumber(getBoost(key)))
				.set("information", StringUtil.colorize(String.join("\n", augment.getInformation())))
				.set("appliedLore", StringUtil.colorize(augment.getAppliedLore()))
				.set("targets", StringUtil.colorize(augment.getAttributeTargets().stream().map(Enum::name).collect(Collectors.joining("\n"))))
				.build();

		final ItemStack item = augment.getAugmentStack();
		BukkitUtil.formatItem(pl, item);
		return item;
	}

	private PersistentDataContainer readContainer() {
		return this.itemStack.getItemMeta().getPersistentDataContainer();
	}

	private void writeContainer(Consumer<PersistentDataContainer> consumer) {
		IBaseTool.writeContainer(this.itemStack, consumer);
	}

	@Override
	public void addAugment(AttributeKey augmentKey, long boost) {
		final Augment augment = getRegistry().getByKey(augmentKey);
		if (!augment.targetsItem(itemStack.getType()))
			return;
		writeContainer(pdc -> {
			if (canAugment(pdc))
				if (hasOpenSlot()) {
					pdc.set(augmentKey.getNamespacedKey(), PersistentDataType.LONG, boost);
					removeAugmentSlot(1);
				}
		});
	}

	@Override
	public void removeAugmentSlot(int amount) {
		setOpenSlots(Math.max((getOpenSlots() - amount), 0));
	}

	@Override
	public void removeAugment(AttributeKey augmentKey) {
		if (!hasAugment(augmentKey)) return;
		writeContainer(pdc -> pdc.remove(augmentKey.getNamespacedKey()));
		setOpenSlots(getOpenSlots() + 1);
	}

	@Override
	public void removeAllAugments() {
		final PersistentDataContainer pdc = readContainer();
		if (canAugment(pdc)) {
			final Set<AttributeKey> keys = getAllAugments();
			if (!keys.isEmpty()) keys.forEach(this::removeAugment);
		}
	}

	@Override
	public boolean hasAugment(AttributeKey augmentKey) {
		return readContainer().has(augmentKey.getNamespacedKey(), PersistentDataType.LONG);
	}

	@Override
	public void makeAugmentable(int startingSlots) {
		writeContainer(pdc -> {
			setCanAugment(pdc, true);
			setOpenSlots(startingSlots);
		});
	}

	@Override
	public boolean isAugmentable() {
		return canAugment(readContainer());
	}

	@Override
	public boolean hasOpenSlot() {
		return (readContainer().has(amountKey) && getOpenSlots() > 0);
	}

	@Override
	public void setOpenSlots(int slots) {
		writeContainer(pdc -> pdc.set(amountKey, PersistentDataType.INTEGER, slots));
	}

	public int getOpenSlots() {
		return readContainer().get(amountKey, PersistentDataType.INTEGER);
	}

	@Override
	public Set<AttributeKey> getAllAugments() {
		return null;
	}

	@Override
	public long getBoost(AttributeKey augmentKey) {
		if (!hasAugment(augmentKey)) return 0L;
		return readContainer().get(augmentKey.getNamespacedKey(), PersistentDataType.LONG);
	}

	@Override
	public List<String> getAugments() {
		final List<String> augmentList = new ArrayList<>();
		{
			final Set<AttributeKey> keys = getAllAugments();

			for (AttributeKey key : keys) {
				final Augment augment = getRegistry().getByKey(key);
				final long boost = getBoost(key);

				final Placeholder placeholder = StringPlaceholder.builder()
						.set("boost", StringUtil.formatNumber(boost))
						.set("name", augment.getKey().getName())
						.build();

				augmentList.add(Text.colorize(placeholder.format(augment.getAppliedLore())));
			}

		}
		return augmentList;
	}

	@Override
	public boolean activate(AttributeKey augmentKey) {
		return hasAugment(augmentKey);
	}

	public static boolean canAugment(ItemStack item) {
		return item.hasItemMeta() && canAugment(item.getItemMeta().getPersistentDataContainer());
	}

	@SuppressWarnings("DataFlowIssue")
	public static boolean canAugment(PersistentDataContainer container) {
		return container.has(reqKey) && container.get(reqKey, PersistentDataType.STRING).equals(reqValue);
	}

	public static void setCanAugment(ItemStack item, boolean canAugment) {
		if (!item.hasItemMeta())
			return;
		item.editMeta(meta -> setCanAugment(meta.getPersistentDataContainer(), canAugment));
	}

	public static void setCanAugment(PersistentDataContainer container, boolean canAugment) {
		if (canAugment)
			container.set(reqKey, PersistentDataType.STRING, reqValue);
		else
			container.remove(reqKey);
	}
}
