package tools.impl.attribute.augments.impl.listeners;

import commons.events.api.EventRegistry;
import commons.events.impl.impl.DetachedSubscriber;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
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
import tools.impl.attribute.augments.impl.item.AugmentApplier;
import tools.impl.registry.AttributeRegistry;
import tools.impl.tool.builder.typed.impl.UniqueItemBuilder;
import tools.impl.tool.impl.AugmentedTool;
import tools.impl.tool.impl.EnchantedTool;
import tools.impl.tool.impl.SkinnedTool;

public class AugmentEvents implements Listener {
	private final EventRegistry registry;
	private final AttributeRegistry<Augment> augmentRegistry;
	private final DetachedSubscriber<ApplyAugmentEvent> applyAugmentEventDetachedSubscriber;
	private final DetachedSubscriber<RemoveAugmentEvent> removeAugmentEventDetachedSubscriber;
	private final DetachedSubscriber<InventoryClickEvent> inventoryClickEventDetachedSubscriber;

	public AugmentEvents(EventRegistry registry) {
		this.registry = registry;
		this.augmentRegistry = ToolsPlugin.getPlugin().getAugmentRegistry();

		this.inventoryClickEventDetachedSubscriber = new DetachedSubscriber<>(InventoryClickEvent.class, ((context, event) -> {
			final ItemStack cursor = event.getCursor();
			final ItemStack clicked = event.getCurrentItem();

			if (!hasMeta(clicked, cursor)) return;
			final PersistentDataContainer cursorPdc = cursor.getItemMeta().getPersistentDataContainer();

			if (!cursorPdc.has(AugmentedTool.applierKey)) return;
			final String type = cursorPdc.get(AugmentedTool.applierKey, PersistentDataType.STRING);

			if (type.isBlank()) return;
			if (type.isEmpty()) return;

			final AugmentedTool clickedAugmentedTool = new AugmentedTool(clicked);
			final AttributeKey key = augmentRegistry.keyFromName(type);

			if (key == null) return;

			if (!(event.getWhoClicked() instanceof Player who)) return;
			if (!(event.getClickedInventory() instanceof PlayerInventory)) return;
			System.out.println("Here Number 3");

			if (event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) {
				if (isApplicable(clicked, cursor)) {
					System.out.println("Here Number 5");

					final ApplyAugmentEvent applyEvent = new ApplyAugmentEvent("augments", key, who, clicked, cursor);
					applyEvent.callEvent();
					event.setCancelled(true);
				}
			}
		}));

		this.applyAugmentEventDetachedSubscriber = new DetachedSubscriber<>(ApplyAugmentEvent.class, ((context, event) -> {
			final ItemStack clicked = event.getAppliedStack();
			final ItemStack cursor = event.getApplierStack();

			final PersistentDataContainer clickedPdc = clicked.getItemMeta().getPersistentDataContainer();
			final PersistentDataContainer cursorPdc = cursor.getItemMeta().getPersistentDataContainer();

			final String type = cursorPdc.get(AugmentedTool.applierKey, PersistentDataType.STRING);
			final long boost = cursorPdc.get(AugmentedTool.applierData, PersistentDataType.LONG);
			final AugmentedTool clickedAugmentedTool = new AugmentedTool(clicked);
			final AttributeKey key = augmentRegistry.keyFromName(type);

			System.out.println("Here Number 1");
			if (isApplicable(clicked, cursor)) {
				clickedAugmentedTool.addAugment(key, boost);
				System.out.println("Here Number 2");

				final UniqueItemBuilder temp = UniqueItemBuilder.fromStack(clicked);
				final EnchantedTool tool = new EnchantedTool(clicked);
				final SkinnedTool anotherTool = new SkinnedTool(clicked);

				UniqueItemBuilder.updateItem(clicked, StringPlaceholder.builder()
						.set("enchants", String.join("\n", tool.getEnchants()))
						.set("augments", String.join("\n", clickedAugmentedTool.getAugments()))
						.set("skin", (anotherTool.getApplied("&cNo Skin")))
						.set("blocks", String.valueOf(temp.getData("gtb", "blocks", PersistentDataType.INTEGER)))
						.build()
				);

			}
		}));

		this.removeAugmentEventDetachedSubscriber = new DetachedSubscriber<>(RemoveAugmentEvent.class, ((context, event) -> {
			final ItemStack clicked = event.getRemovedStack();

			final PersistentDataContainer clickedPdc = clicked.getItemMeta().getPersistentDataContainer();
			final AugmentedTool clickedAugmentedTool = new AugmentedTool(clicked);

			final AttributeKey key = event.getAugmentKey();

			clickedAugmentedTool.removeAugment(key);
			final UniqueItemBuilder temp = UniqueItemBuilder.fromStack(clicked);
			final EnchantedTool tool = new EnchantedTool(clicked);
			final SkinnedTool anotherTool = new SkinnedTool(clicked);

			UniqueItemBuilder.updateItem(clicked, StringPlaceholder.builder()
					.set("enchants", String.join("\n", tool.getEnchants()))
					.set("augments", String.join("\n", clickedAugmentedTool.getAugments()))
					.set("skin", (anotherTool.getApplied("&cNo Skin")))
					.set("blocks", String.valueOf(temp.getData("gtb", "blocks", PersistentDataType.INTEGER)))
					.build()
			);
		}));

		this.inventoryClickEventDetachedSubscriber.bind(registry);
		this.applyAugmentEventDetachedSubscriber.bind(registry);
		this.removeAugmentEventDetachedSubscriber.bind(registry);
	}

	private boolean isApplicable(ItemStack clicked, ItemStack cursor) {
		if (!hasMeta(clicked, cursor)) return false;
		final PersistentDataContainer clickedPdc = clicked.getItemMeta().getPersistentDataContainer();
		final PersistentDataContainer cursorPdc = cursor.getItemMeta().getPersistentDataContainer();

		final AugmentedTool clickedAugmentedTool = new AugmentedTool(clicked);
		if (!cursorPdc.has(AugmentedTool.applierKey)) return false;

		final String type = cursorPdc.get(AugmentedTool.applierKey, PersistentDataType.STRING);

		if (clickedAugmentedTool.isAugmentable()) {
			if (clickedAugmentedTool.hasOpenSlot()) {
				System.out.println(type);
				return (new AugmentApplier().hasKeys(type, cursor));
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
