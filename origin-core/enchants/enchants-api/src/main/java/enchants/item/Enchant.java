package enchants.item;

import commons.events.impl.EventSubscriber;
import enchants.EnchantKey;
import enchants.conf.EnchantmentConfiguration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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

	int getMaxLevel();

	double getStartCost();

	double getMaxCost();

	double getStartChance();

	double getMaxChance();

	ProgressionType getChanceType();

	ProgressionType getCostType();

	EventSubscriber getHandleEnchant();

	EnchantmentConfiguration getConfig();

	enum ProgressionType {
		EXPONENTIAL,
		LOGARITHMIC
	}
}
