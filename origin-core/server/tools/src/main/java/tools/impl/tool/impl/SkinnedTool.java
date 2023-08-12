package tools.impl.tool.impl;

import commons.util.BukkitUtil;
import commons.util.StringUtil;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.skins.Skin;
import tools.impl.attribute.skins.impl.ToolSkinFactory;
import tools.impl.registry.impl.BaseAttributeRegistry;
import tools.impl.tool.IBaseTool;
import tools.impl.tool.type.ISkinnedTool;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SkinnedTool implements ISkinnedTool {
	private final ItemStack itemStack;

	private static BaseAttributeRegistry<Skin> getRegistry() {
		return ToolsPlugin.getPlugin().getSkinRegistry();
	}

	private static ToolSkinFactory getFactory() {
		return ToolsPlugin.getPlugin().getSkinFactory();
	}

	public SkinnedTool(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public ItemStack getItemStack() {
		return this.itemStack;
	}

	@Override
	public ItemStack formatMenuItemFor(AttributeKey key) {
		final Skin skin = getRegistry().getByKey(key);

		final Placeholder pl = StringPlaceholder.builder()
				.set("name", key.getName())
				.set("information", StringUtil.colorize(String.join("\n", skin.getInformation())))
				.set("appliedLore", StringUtil.colorize(skin.getAppliedLore()))
				.set("targets", StringUtil.colorize(skin.getAttributeTargets().stream().map(Enum::name).collect(Collectors.joining("\n"))))
				.build();

		writeContainer(pdc -> {
			pdc.set(applierKey, PersistentDataType.STRING, key.getName());
		});

		final ItemStack item = skin.getSkinStack();

		item.editMeta(meta -> {
			meta.setCustomModelData(skin.getModelData());
		});

		BukkitUtil.formatItem(pl, item);
		return item;
	}

	@Override
	public void addSkin(AttributeKey skinKey) {
		final Skin skin = getRegistry().getByKey(skinKey);
		if (!skin.targetsItem(itemStack.getType()))
			return;

		writeContainer(pdc -> {
			if (canSkin(pdc)) {
				if (getSkin() != null) return;

				pdc.set(hasSkin, PersistentDataType.STRING, skinKey.getName());

				itemStack.editMeta(meta -> {
					meta.setCustomModelData(skin.getModelData());
				});

			}
		});

	}

	@Override
	public void removeSkin() {
		if (getSkin() == null) return;
		writeContainer(pdc -> pdc.remove(hasSkin));
	}

	@Override
	public void makeSkinnable() {
		writeContainer(pdc -> setCanSkin(pdc, true));
	}

	@Override
	public boolean isSkinnable() {
		return canSkin(readContainer());
	}

	@Override
	public AttributeKey getSkin() {
		return getRegistry().keyFromName(readContainer().get(hasSkin, PersistentDataType.STRING));
	}

	private PersistentDataContainer readContainer() {
		return this.itemStack.getItemMeta().getPersistentDataContainer();
	}

	private void writeContainer(Consumer<PersistentDataContainer> consumer) {
		IBaseTool.writeContainer(this.itemStack, consumer);
	}

	@Override
	public boolean hasSkin(AttributeKey skinKey) {
		return readContainer().get(hasSkin, PersistentDataType.STRING).equals(skinKey.getName());
	}

	public static boolean canSkin(ItemStack item) {
		return item.hasItemMeta() && canSkin(item.getItemMeta().getPersistentDataContainer());
	}

	@SuppressWarnings("DataFlowIssue")
	public static boolean canSkin(PersistentDataContainer container) {
		return container.has(reqKey) && container.get(reqKey, PersistentDataType.STRING).equals(reqValue);
	}

	public static void setCanSkin(ItemStack item, boolean canAugment) {
		if (!item.hasItemMeta())
			return;
		item.editMeta(meta -> setCanSkin(meta.getPersistentDataContainer(), canAugment));
	}

	public static void setCanSkin(PersistentDataContainer container, boolean canSkin) {
		if (canSkin)
			container.set(reqKey, PersistentDataType.STRING, reqValue);
		else
			container.remove(reqKey);
	}
}

