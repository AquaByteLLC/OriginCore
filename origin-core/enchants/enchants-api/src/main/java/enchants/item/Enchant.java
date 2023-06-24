package enchants.item;

import commons.events.impl.EventSubscriber;
import enchants.EnchantKey;
import enchants.config.EnchantmentConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;

/**
 * @author vadim
 */
public interface Enchant {

	EnchantKey getKey();

	String getName();

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
