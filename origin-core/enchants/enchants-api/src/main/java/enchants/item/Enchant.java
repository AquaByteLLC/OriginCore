package enchants.item;

import commons.events.impl.EventSubscriber;
import enchants.EnchantKey;
import enchants.conf.EnchantmentConfiguration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author vadim
 */
public interface Enchant {

	EnchantKey getKey();

	List<EnchantTarget> getEnchantTargets();

	boolean targetsItem(Material type);

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

	EventSubscriber getHandleEnchant();

	EnchantmentConfiguration getConfig();

	enum ProgressionType {
		EXPONENTIAL,
		LOGARITHMIC
	}
}
