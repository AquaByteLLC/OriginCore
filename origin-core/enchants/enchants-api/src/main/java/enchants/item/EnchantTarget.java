package enchants.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author vadim
 */
public enum EnchantTarget {

	HOE(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE),
	ROD(Material.FISHING_ROD),
	AXE(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE),
	PICK(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE),
	SWORD(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
	SPADE(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL);

	private final List<Material> allowed;

	EnchantTarget(Material... allowed) {
		this.allowed = List.of(allowed);
	}

	public boolean appliesToItem(ItemStack item) {
		return item != null && allowed.contains(item.getType());
	}

	public boolean appliesToType(Material type) {
		return type != null && allowed.contains(type);
	}

	public static EnchantTarget[] tools() {
		return new EnchantTarget[]{HOE, AXE, PICK, SPADE};
	}

	public static EnchantTarget[] weapons() {
		return new EnchantTarget[]{SWORD};
	}

	public static EnchantTarget[] special() {
		return new EnchantTarget[]{ROD};
	}

	public static EnchantTarget[] all() {
		return values();
	}

}
