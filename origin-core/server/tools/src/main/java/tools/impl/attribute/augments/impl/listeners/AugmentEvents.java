package tools.impl.attribute.augments.impl.listeners;

import com.mojang.datafixers.util.Pair;
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
import tools.impl.attribute.augments.Augment;
import tools.impl.attribute.augments.impl.events.ApplyAugmentEvent;
import tools.impl.attribute.augments.impl.events.RemoveAugmentEvent;
import tools.impl.attribute.registry.impl.BaseAttributeRegistry;
import tools.impl.tool.builder.typed.impl.UniqueItemBuilder;
import tools.impl.tool.impl.AugmentedTool;
import tools.impl.tool.type.IAugmentedTool;

import java.util.List;

public class AugmentEvents implements Listener {
	private final EventRegistry registry;
	private final BaseAttributeRegistry<Augment> augmentRegistry;

	public AugmentEvents(EventRegistry registry) {
		this.registry = registry;
		this.augmentRegistry = ToolsPlugin.getPlugin().getAugmentRegistry();
		registry.subscribeAll(this);
	}

	@Subscribe
	public void onAttach(InventoryClickEvent event) {
		final ItemStack cursor = event.getCursor();
		final ItemStack clicked = event.getCurrentItem();

		final PersistentDataContainer cursorPdc = cursor.getItemMeta().getPersistentDataContainer();

		final String type = cursorPdc.get(AugmentedTool.applierKey, PersistentDataType.STRING);

		final AugmentedTool clickedAugmentedTool = new AugmentedTool(clicked);
		final AttributeKey key = augmentRegistry.keyFromName(type);

		if (!(event.getWhoClicked() instanceof Player who)) return;
		if (!(event.getClickedInventory() instanceof PlayerInventory)) return;

		if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
			if (isApplicable(clicked, cursor)) {
				final ApplyAugmentEvent applyEvent = new ApplyAugmentEvent("augments", key, who, clicked, cursor);
				applyEvent.callEvent();
				event.setCancelled(true);
			}
		}

	}

	@Subscribe
	public void onApply(ApplyAugmentEvent event) {
		final ItemStack clicked = event.getAppliedStack();
		final ItemStack cursor = event.getApplierStack();

		final PersistentDataContainer clickedPdc = clicked.getItemMeta().getPersistentDataContainer();
		final PersistentDataContainer cursorPdc = cursor.getItemMeta().getPersistentDataContainer();

		final String type = cursorPdc.get(AugmentedTool.applierKey, PersistentDataType.STRING);
		final long boost = cursorPdc.get(AugmentedTool.applierData, PersistentDataType.LONG);
		final AugmentedTool clickedAugmentedTool = new AugmentedTool(clicked);

		final AttributeKey key = augmentRegistry.keyFromName(type);
		final String uid = clickedPdc.get(UniqueItemBuilder.uniqueIdentifier, PersistentDataType.STRING);

		final Pair<String, List<String>> initData = UniqueItemBuilder.initial.get(uid);
		final String displayName = initData.getFirst();
		final List<String> lore = initData.getSecond();

		if (isApplicable(clicked, cursor)) {
			clickedAugmentedTool.addAugment(key, boost);
			UniqueItemBuilder.updateItem(clicked, lore, displayName);
		}
	}

	@Subscribe
	public void onRemove(RemoveAugmentEvent event) {
		final ItemStack clicked = event.getRemovedStack();

		final PersistentDataContainer clickedPdc = clicked.getItemMeta().getPersistentDataContainer();
		final AugmentedTool clickedAugmentedTool = new AugmentedTool(clicked);
		final String uid = clickedPdc.get(UniqueItemBuilder.uniqueIdentifier, PersistentDataType.STRING);

		final Pair<String, List<String>> initData = UniqueItemBuilder.initial.get(uid);
		final String displayName = initData.getFirst();
		final List<String> lore = initData.getSecond();

		final AttributeKey key = event.getAugmentKey();

		clickedAugmentedTool.removeAugment(key);
		UniqueItemBuilder.updateItem(clicked, lore, displayName);
	}

	private boolean isApplicable(ItemStack clicked, ItemStack cursor) {
		if (!hasMeta(clicked, cursor)) return false;
		final PersistentDataContainer clickedPdc = clicked.getItemMeta().getPersistentDataContainer();
		final PersistentDataContainer cursorPdc = cursor.getItemMeta().getPersistentDataContainer();

		final AugmentedTool clickedAugmentedTool = new AugmentedTool(clicked);
		final String type = cursorPdc.get(AugmentedTool.applierKey, PersistentDataType.STRING);

		if (clickedAugmentedTool.isAugmentable()) {
			if (clickedAugmentedTool.hasOpenSlot()) {
				return cursor == IAugmentedTool.makeApplier(type);
			}
		}

		return false;
	}

	private boolean hasMeta(ItemStack clicked, ItemStack cursor) {
		return clicked.hasItemMeta() && cursor.hasItemMeta();
	}
}
