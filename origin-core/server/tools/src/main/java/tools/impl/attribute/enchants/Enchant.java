package tools.impl.attribute.enchants;

import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.BaseAttribute;
import tools.impl.progression.ProgressionType;

import java.math.BigDecimal;
import java.util.List;

public interface Enchant extends BaseAttribute {
	List<String> getInformation();

	String getLore();

	ItemStack getMenuItem();

	long getMaxLevel();

	BigDecimal getStartCost();

	BigDecimal getMaxCost();

	BigDecimal getStartChance();

	BigDecimal getMaxChance();

	ProgressionType getChanceType();

	ProgressionType getCostType();

}
