package tools.impl.tool.impl;

import commons.util.BukkitUtil;
import commons.util.StringUtil;
import me.lucko.helper.item.ItemStackBuilder;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import tools.impl.ToolsPlugin;
import tools.impl.ability.cache.impl.AttributeCache;
import tools.impl.ability.cache.types.PlayerBasedCachedAttribute;
import tools.impl.ability.cache.types.impl.PlayerCachedAttribute;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.skins.Skin;
import tools.impl.attribute.skins.impl.ToolSkinFactory;
import tools.impl.registry.impl.BaseAttributeRegistry;
import tools.impl.tool.IBaseTool;
import tools.impl.tool.type.ISkinnedTool;

import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * What needs to be done as of now for skins is to create a general cooldown handler,
 * this will allow for handling custom abilitites and only allowing the events to be called when the ability is off
 * cooldown. n
 * <p>
 * Perhaps add an ability registry which can then be binded to a skin? Need to think of a nice way to do a builder for abilitites.
 */
public class SkinnedTool implements ISkinnedTool {

	private ItemStack itemStack;

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
				System.out.println("We are setting it here");

				pdc.set(hasSkin, PersistentDataType.STRING, skinKey.getName());

				this.itemStack = ItemStackBuilder.of(itemStack).transformMeta(meta -> {
					meta.setCustomModelData(getRegistry().getByKey(skinKey).getModelData());
				}).build();

			}
		});

	}

	@Override
	public void removeSkin() {
		if (getSkin() == null) return;

		this.itemStack = ItemStackBuilder.of(itemStack).transformMeta(meta -> {
			meta.setCustomModelData(0);
		}).build();

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
	public @Nullable AttributeKey getSkin() {
		if (readContainer().get(hasSkin, PersistentDataType.STRING) == null) {
			return null;
		}
		return getRegistry().keyFromName(readContainer().get(hasSkin, PersistentDataType.STRING));
	}

	public String getApplied(String replacementForNull) {
		Skin skin = getRegistry().getByKey(getSkin());
		return String.format(getSkin() == null ? replacementForNull : StringPlaceholder.builder().set("name", skin.getKey().getName()).build().format(skin.getAppliedLore()));
	}

	private PersistentDataContainer readContainer() {
		return this.itemStack.getItemMeta().getPersistentDataContainer();
	}

	private void writeContainer(Consumer<PersistentDataContainer> consumer) {
		IBaseTool.writeContainer(this.itemStack, consumer);
	}

	@Override
	public boolean hasSkin(AttributeKey skinKey) {
		if (!itemStack.getItemMeta().hasCustomModelData()) return false;
		return readContainer().get(hasSkin, PersistentDataType.STRING).equals(skinKey.getName()) && (itemStack.getItemMeta().getCustomModelData() == getRegistry().getByKey(skinKey).getModelData());
	}

	public boolean activate(PlayerCachedAttribute<Skin> playerCachedAttribute, AttributeKey skinKey) {
		if (hasSkin(skinKey)) {
			final AttributeCache<Skin, PlayerBasedCachedAttribute<Skin>> cache = ToolsPlugin.getPlugin().getSkinCache();
			final Skin skin = ToolsPlugin.getPlugin().getSkinRegistry().getByKey(skinKey);
			System.out.println(!cache.getCache().contains(playerCachedAttribute) + " Testing It");
			return !cache.getCache().contains(playerCachedAttribute);
		}
		return false;
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
		System.out.println("Required Key: " + reqKey);
		System.out.println("Container: " + container);
		if (canSkin)
			container.set(reqKey, PersistentDataType.STRING, reqValue);
		else
			container.remove(reqKey);
	}
}

