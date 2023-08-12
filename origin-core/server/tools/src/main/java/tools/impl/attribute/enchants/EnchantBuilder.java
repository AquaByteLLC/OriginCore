package tools.impl.attribute.enchants;

import commons.events.impl.EventSubscriber;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import tools.impl.progression.ProgressionType;
import tools.impl.target.ToolTarget;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

public interface EnchantBuilder {
	EnchantBuilder setLore(String lore);

	EnchantBuilder setInfo(List<String> information);

	EnchantBuilder setMaxLevel(int maxLevel);

	EnchantBuilder setMenuItem(ItemStack menuItem);

	EnchantBuilder setStartCost(BigDecimal startCost);

	EnchantBuilder setMaxCost(BigDecimal maxCost);

	EnchantBuilder setStartChance(BigDecimal startChance);

	EnchantBuilder setMaxChance(BigDecimal maxChance);

	EnchantBuilder setChanceType(ProgressionType type);

	EnchantBuilder setCostType(ProgressionType type);

	Enchant build(EventSubscriber handleEnchant, Consumer<FileConfiguration> writer, ToolTarget... targets);
}
