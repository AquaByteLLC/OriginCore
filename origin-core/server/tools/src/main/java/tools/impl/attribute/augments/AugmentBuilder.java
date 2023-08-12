package tools.impl.attribute.augments;

import commons.events.impl.EventSubscriber;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import tools.impl.target.ToolTarget;

import java.util.function.Consumer;

public interface AugmentBuilder {

	AugmentBuilder setMinimumBoost(long boost);

	AugmentBuilder setMaximumBoost(long boost);

	AugmentBuilder setInformation(String... information);

	AugmentBuilder setAugmentStack(ItemStack stack);

	AugmentBuilder setAppliedLore(String lore);

	Augment build(EventSubscriber handleEnchant, Consumer<FileConfiguration> writer, ToolTarget... targets);
}
