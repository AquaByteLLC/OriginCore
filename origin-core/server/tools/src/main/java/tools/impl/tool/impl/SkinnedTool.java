package tools.impl.tool.impl;

import commons.util.BukkitUtil;
import commons.util.StringUtil;
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
import tools.impl.attribute.AttributeFactory;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.skins.Skin;
import tools.impl.attribute.skins.SkinBuilder;
import tools.impl.registry.AttributeRegistry;
import tools.impl.tool.type.ISkinnedTool;

import java.util.stream.Collectors;

/**
 * What needs to be done as of now for skins is to create a general cooldown handler,
 * this will allow for handling custom abilitites and only allowing the events to be called when the ability is off
 * cooldown. n
 * <p>
 * Perhaps add an ability registry which can then be binded to a skin? Need to think of a nice way to do a builder for abilitites.
 */
public class SkinnedTool extends ToolBase<Skin, ISkinnedTool, SkinBuilder> implements ISkinnedTool {

	public SkinnedTool(ItemStack itemStack) {
		super(itemStack);
	}

	@Override
	protected AttributeRegistry<Skin> getRegistry() {
		return ToolsPlugin.getPlugin().getSkinRegistry();
	}

	@Override
	protected AttributeFactory<ISkinnedTool, SkinBuilder> getFactory() {
		return ToolsPlugin.getPlugin().getSkinFactory();
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

		if (canSkin(this.itemStack)) {
			if (getSkin() != null) return;
			writeContainer(pdc -> pdc.set(hasSkin, PersistentDataType.STRING, skinKey.getName()));
			this.itemStack.editMeta(meta -> meta.setCustomModelData(skin.getModelData()));
		}

	}

	@Override
	public void removeSkin() {
		if (getSkin() == null) return;

		this.itemStack.editMeta(meta -> meta.setCustomModelData(0));


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

	public static boolean canSkin(PersistentDataContainer container) {
		return container.has(reqKey) && reqValue.equals(container.get(reqKey, PersistentDataType.STRING));
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

