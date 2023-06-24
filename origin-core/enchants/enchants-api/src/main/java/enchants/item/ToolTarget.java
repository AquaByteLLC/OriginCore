package enchants.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.RegExp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author vadim
 */
public enum ToolTarget {

	HOE(),
	ROD(Material.FISHING_ROD),
	AXE(),
	PICK("_PICKAXE"),
	SWORD(),
	SPADE("_SHOVEL");
	
	private final List<Material> allowed;

	ToolTarget() {
		this.allowed = Arrays.stream(Material.values()).filter(it -> it.name().endsWith('_' + name())).toList();
	}

	ToolTarget(Material... allowed) {
		this.allowed = List.of(allowed);
	}

	ToolTarget(@RegExp String pattern) {
		this.allowed = Arrays.stream(Material.values()).filter(it -> it.name().endsWith(pattern)).toList();
	}

	ToolTarget(List<Material> allowed) {
		this.allowed = Collections.unmodifiableList(allowed);
	}

	public boolean appliesToItem(ItemStack item) {
		return item != null && allowed.contains(item.getType());
	}

}
