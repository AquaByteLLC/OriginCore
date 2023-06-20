package generators.impl.wrapper;

import commons.PackUtil;
import generators.wrapper.Drop;
import generators.wrapper.Generator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * @author vadim
 */
public final class PDCUtil {

	/* drops */

	private static final NamespacedKey DROP_PRICE = new NamespacedKey("generators", "drop.price");
	private static final NamespacedKey DROP_OWNER = new NamespacedKey("generators", "drop.owner");

	public static ItemStack createDrop(Generator generator) {
		Drop drop = generator.getCurrentTier().getDrop();

		ItemStack item = drop.createDrop();
		setDropPrice(item, drop.getSellPrice());
		setDropOwner(item, generator.getOfflineOwner());
		return item;
	}

	public static Double getDropPrice(ItemStack potentialDrop) {
		if(!potentialDrop.hasItemMeta()) return null;
		PersistentDataContainer pdc = potentialDrop.getItemMeta().getPersistentDataContainer();

		if(pdc.has(DROP_PRICE))
			return pdc.get(DROP_PRICE, PersistentDataType.DOUBLE);
		else
			return null;
	}

	public static void setDropPrice(ItemStack drop, Double newPrice) {
		if(!drop.hasItemMeta()) throw new IllegalArgumentException("drop");
		drop.editMeta(meta -> {
			PersistentDataContainer pdc = meta.getPersistentDataContainer();

			if(newPrice != null)
				pdc.set(DROP_PRICE, PersistentDataType.DOUBLE, newPrice);
			else
				pdc.remove(DROP_PRICE);
		});
	}

	public static OfflinePlayer getDropOwner(ItemStack potentialDrop) {
		if(!potentialDrop.hasItemMeta()) return null;
		PersistentDataContainer pdc = potentialDrop.getItemMeta().getPersistentDataContainer();

		if(pdc.has(DROP_OWNER))
			return Bukkit.getOfflinePlayer(PackUtil.bytes2uuid(pdc.get(DROP_OWNER, PersistentDataType.BYTE_ARRAY)));
		else
			return null;
	}

	public static void setDropOwner(ItemStack drop, OfflinePlayer newOwner) {
		if(!drop.hasItemMeta()) throw new IllegalArgumentException("drop");
		drop.editMeta(meta -> {
			PersistentDataContainer pdc = meta.getPersistentDataContainer();

			if (newOwner != null)
				pdc.set(DROP_OWNER, PersistentDataType.BYTE_ARRAY, PackUtil.uuid2bytes(newOwner.getUniqueId()));
			else
				pdc.remove(DROP_OWNER);
		});
	}

	public static boolean isDrop(ItemStack potentialDrop) {
		return potentialDrop.hasItemMeta() && potentialDrop.getItemMeta().getPersistentDataContainer().has(DROP_OWNER);
	}

	/* gens */

	private static final NamespacedKey GEN_TIER = new NamespacedKey("generators", "gen.tier");
	private static final NamespacedKey GEN_OWNER = new NamespacedKey("generators", "gen.owner");

	public static Integer getGenTier(ItemStack potentialGen) {
		if(!potentialGen.hasItemMeta()) return null;
		PersistentDataContainer pdc = potentialGen.getItemMeta().getPersistentDataContainer();

		if(pdc.has(GEN_TIER))
			return pdc.get(GEN_TIER, PersistentDataType.INTEGER);
		else
			return null;
	}

	public static void setGenTier(ItemStack gen, Integer newTier) {
		if (!gen.hasItemMeta()) throw new IllegalArgumentException("gen");
		gen.editMeta(meta -> {
			PersistentDataContainer pdc = meta.getPersistentDataContainer();

			if (newTier != null)
				pdc.set(GEN_TIER, PersistentDataType.INTEGER, newTier);
			else
				pdc.remove(GEN_TIER);
		});
	}

	public static OfflinePlayer getGenOwner(ItemStack potentialGen) {
		if(!potentialGen.hasItemMeta()) return null;
		PersistentDataContainer pdc = potentialGen.getItemMeta().getPersistentDataContainer();

		if(pdc.has(GEN_OWNER))
			return Bukkit.getOfflinePlayer(PackUtil.bytes2uuid(pdc.get(GEN_OWNER, PersistentDataType.BYTE_ARRAY)));
		else
			return null;
	}

	public static void setGenOwner(ItemStack gen, OfflinePlayer newOwner) {
		if(!gen.hasItemMeta()) throw new IllegalArgumentException("gen");
		gen.editMeta(meta -> {
			PersistentDataContainer pdc = meta.getPersistentDataContainer();

			if (newOwner != null)
				pdc.set(GEN_OWNER, PersistentDataType.BYTE_ARRAY, PackUtil.uuid2bytes(newOwner.getUniqueId()));
			else
				pdc.remove(GEN_OWNER);
		});
	}

}
