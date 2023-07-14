package enchants.item;

import commons.events.impl.EventSubscriber;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author vadim
 */
public interface EnchantBuilder {

	EnchantBuilder setLore(String lore);

	EnchantBuilder setInfo(List<String> information);

	EnchantBuilder setMaxLevel(int maxLevel);

	EnchantBuilder setMenuItem(ItemStack menuItem);

	EnchantBuilder setStartCost(BigDecimal startCost);

	EnchantBuilder setMaxCost(BigDecimal maxCost);

	EnchantBuilder setStartChance(BigDecimal startChance);

	EnchantBuilder setMaxChance(BigDecimal maxChance);

	EnchantBuilder setChanceType(Enchant.ProgressionType type);

	EnchantBuilder setCostType(Enchant.ProgressionType type);

	Enchant build(EventSubscriber handleEnchant, EnchantTarget... targets);

}
