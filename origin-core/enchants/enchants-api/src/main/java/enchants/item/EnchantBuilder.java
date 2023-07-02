package enchants.item;

import commons.events.impl.EventSubscriber;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author vadim
 */
public interface EnchantBuilder {

	EnchantBuilder setLore(String lore);

	EnchantBuilder setInfo(List<String> information);

	EnchantBuilder setMaxLevel(int maxLevel);

	EnchantBuilder setMenuItem(ItemStack menuItem);

	EnchantBuilder setStartCost(double startCost);

	EnchantBuilder setMaxCost(double maxCost);

	EnchantBuilder setStartChance(double startChance);

	EnchantBuilder setMaxChance(double maxChance);

	EnchantBuilder setChanceType(Enchant.ProgressionType type);

	EnchantBuilder setCostType(Enchant.ProgressionType type);

	Enchant build(EventSubscriber handleEnchant, EnchantTarget... targets);

}
