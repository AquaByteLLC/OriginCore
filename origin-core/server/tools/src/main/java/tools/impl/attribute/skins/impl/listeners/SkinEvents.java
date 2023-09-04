package tools.impl.attribute.skins.impl.listeners;

import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.skins.Skin;
import tools.impl.attribute.skins.impl.events.ApplySkinEvent;
import tools.impl.attribute.skins.impl.events.RemoveSkinEvent;
import tools.impl.registry.AttributeRegistry;
import tools.impl.tool.builder.typed.impl.UniqueItemBuilder;
import tools.impl.tool.impl.AugmentedTool;
import tools.impl.tool.impl.SkinnedTool;
import tools.impl.tool.type.ISkinnedTool;

import java.util.List;

public class SkinEvents implements Listener {
	private final EventRegistry registry;
	private final AttributeRegistry<Skin> skinRegistry;

	public SkinEvents(EventRegistry registry) {
		this.registry = registry;
		this.skinRegistry = ToolsPlugin.getPlugin().getSkinRegistry();
		registry.subscribeAll(this);
	}

	@Subscribe
	public void onAttach(InventoryClickEvent event) {
		final ItemStack cursor = event.getCursor();
		final ItemStack clicked = event.getCurrentItem();

		if (!hasMeta(clicked, cursor)) return;
		final PersistentDataContainer cursorPdc = cursor.getItemMeta().getPersistentDataContainer();

		final String type = cursorPdc.get(SkinnedTool.applierKey, PersistentDataType.STRING);

		final SkinnedTool clickedSkinnedTool = new SkinnedTool(clicked);
		final AttributeKey key = skinRegistry.keyFromName(type);

		if (!(event.getWhoClicked() instanceof Player who)) return;
		if (!(event.getClickedInventory() instanceof PlayerInventory)) return;

		if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
			if (isApplicable(clicked, cursor)) {
				final ApplySkinEvent applyEvent = new ApplySkinEvent("skins", key, who, clicked, cursor);
				applyEvent.callEvent();
				event.setCancelled(true);
			}
		}

	}

	@Subscribe
	public void onApply(ApplySkinEvent event) {
		final ItemStack clicked = event.getAppliedStack();
		final ItemStack cursor = event.getApplierStack();

		final PersistentDataContainer clickedPdc = clicked.getItemMeta().getPersistentDataContainer();
		final PersistentDataContainer cursorPdc = cursor.getItemMeta().getPersistentDataContainer();

		final SkinnedTool clickedSkinnedTool = new SkinnedTool(clicked);

		if (isApplicable(clicked, cursor)) {
			clickedSkinnedTool.addSkin(event.getSkinKey());
			UniqueItemBuilder.updateItem(clicked, List.of(), "");
		}
	}

	@Subscribe
	public void onRemove(RemoveSkinEvent event) {
		final ItemStack clicked = event.getRemovedStack();

		final PersistentDataContainer clickedPdc = clicked.getItemMeta().getPersistentDataContainer();
		final SkinnedTool clickedSkinnedTool = new SkinnedTool(clicked);

		clickedSkinnedTool.removeSkin();
	}

	private boolean isApplicable(ItemStack clicked, ItemStack cursor) {
		if (!hasMeta(clicked, cursor)) return false;
		final PersistentDataContainer clickedPdc = clicked.getItemMeta().getPersistentDataContainer();
		final PersistentDataContainer cursorPdc = cursor.getItemMeta().getPersistentDataContainer();

		final SkinnedTool clickedSkinnedTool = new SkinnedTool(clicked);
		if (!cursorPdc.has(AugmentedTool.applierKey)) return false;
		final String type = cursorPdc.get(SkinnedTool.applierKey, PersistentDataType.STRING);

		if (clickedSkinnedTool.isSkinnable()) {
			if (clickedSkinnedTool.getSkin() == null) {
				return cursor == ISkinnedTool.makeApplier(type);
			}
		}
		return false;
	}

	private boolean hasMeta(ItemStack clicked, ItemStack cursor) {
		if (clicked == null || cursor == null) return false;
		if (clicked.getType().isAir() || cursor.getType().isAir()) return false;
		if (clicked.getItemMeta() == null || cursor.getItemMeta() == null) return false;
		return clicked.hasItemMeta() && cursor.hasItemMeta();
	}
}
