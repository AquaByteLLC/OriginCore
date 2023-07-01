package enderchests.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * @author vadim
 */
class ItemUtil {

	static final NamespacedKey BLANK_KEY = new NamespacedKey("enderchests", "item.empty");
	static final String BLANK_VALUE = "placeholder";

	static void mark(ItemStack item) {
		if (item != null && item.hasItemMeta())
			item.editMeta(meta -> {
				meta.getPersistentDataContainer().set(BLANK_KEY, PersistentDataType.STRING, BLANK_VALUE);
			});
	}

	static void unmark(ItemStack item) {
		if (item != null && item.hasItemMeta())
			item.editMeta(meta -> {
				meta.getPersistentDataContainer().remove(BLANK_KEY);
			});
	}

	static boolean isMarked(ItemStack item) {
		if (item != null && item.hasItemMeta()) {
			PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
			return pdc.has(BLANK_KEY) && BLANK_VALUE.equals(pdc.get(BLANK_KEY, PersistentDataType.STRING));
		}
		return false;
	}

}
