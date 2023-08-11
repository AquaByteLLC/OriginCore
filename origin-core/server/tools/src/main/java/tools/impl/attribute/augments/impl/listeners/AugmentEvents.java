package tools.impl.attribute.augments.impl.listeners;

import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.augments.Augment;
import tools.impl.attribute.registry.impl.BaseAttributeRegistry;
import tools.impl.tool.impl.AugmentedTool;
import tools.impl.tool.type.IAugmentedTool;

public class AugmentEvents implements Listener {
	private EventRegistry registry;

	public AugmentEvents(EventRegistry registry) {
		this.registry = registry;
		registry.subscribeAll(this);
	}

	@Subscribe
	public void onAttach(InventoryClickEvent event) {
		final ItemStack cursor = event.getCursor();
		final ItemStack clicked = event.getCurrentItem();
		final Inventory inventory = event.getInventory();

		if (event.getWhoClicked() instanceof Player who) {

		}
	}

	private boolean isApplicable(ItemStack clicked, ItemStack cursor) {

		final BaseAttributeRegistry<Augment> registry = ToolsPlugin.getPlugin().getAugmentRegistry();

		if (!hasMeta(clicked, cursor)) return false;
		final PersistentDataContainer clickedPdc = clicked.getItemMeta().getPersistentDataContainer();
		final PersistentDataContainer cursorPdc = cursor.getItemMeta().getPersistentDataContainer();

		final AugmentedTool clickedAugmentedTool = new AugmentedTool(clicked);

		final String type = cursorPdc.get(AugmentedTool.applierKey, PersistentDataType.STRING);
		final long boost = cursorPdc.get(AugmentedTool.applierData, PersistentDataType.LONG);

		if (clickedAugmentedTool.isAugmentable())
			if (clickedAugmentedTool.hasOpenSlot())
				if (cursor == IAugmentedTool.makeApplier(type)) {
					final AttributeKey key = registry.keyFromName(type);
					clickedAugmentedTool.addAugment(key, boost);
				}


		return false;
	}

	private boolean hasMeta(ItemStack clicked, ItemStack cursor) {
		return clicked.hasItemMeta() && cursor.hasItemMeta();
	}

	private void applyAugment() {

	}

	private void removeAugment() {

	}
}
