package generators.impl.wrapper;

import commons.PackUtil;
import generators.wrapper.Drop;
import generators.wrapper.Generator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.swing.text.Document;

/**
 * @author vadim
 */
public final class Drops {

	private static final NamespacedKey PRICE_KEY = new NamespacedKey("generators", "drop.price");
	private static final NamespacedKey OWNER_KEY = new NamespacedKey("generators", "drop.owner");

	public static ItemStack createDrop(Generator generator) {
		Drop drop = generator.getCurrentTier().getDrop();

		ItemStack item = drop.createDrop();
		setPrice(item, drop.getSellPrice());
		setOwner(item, generator.getOfflineOwner());
		return item;
	}

	public static Double getPrice(ItemStack potentialDrop) {
		PersistentDataContainer pdc = potentialDrop.getItemMeta().getPersistentDataContainer();

		if(pdc.has(PRICE_KEY))
			return pdc.get(PRICE_KEY, PersistentDataType.DOUBLE);
		else
			return null;
	}

	public static void setPrice(ItemStack drop, Double newPrice) {
		PersistentDataContainer pdc = drop.getItemMeta().getPersistentDataContainer();

		if(newPrice != null)
			pdc.set(PRICE_KEY, PersistentDataType.DOUBLE, newPrice);
		else
			pdc.remove(PRICE_KEY);
	}

	public static OfflinePlayer getOwner(ItemStack potentialDrop) {
		PersistentDataContainer pdc = potentialDrop.getItemMeta().getPersistentDataContainer();

		if(pdc.has(OWNER_KEY))
			return Bukkit.getOfflinePlayer(PackUtil.bytes2uuid(pdc.get(OWNER_KEY, PersistentDataType.BYTE_ARRAY)));
		else
			return null;
	}

	public static void setOwner(ItemStack drop, OfflinePlayer newOwner) {
		PersistentDataContainer pdc = drop.getItemMeta().getPersistentDataContainer();

		if(newOwner != null)
			pdc.set(OWNER_KEY, PersistentDataType.BYTE_ARRAY, PackUtil.uuid2bytes(newOwner.getUniqueId()));
		else
			pdc.remove(OWNER_KEY);
	}

}
