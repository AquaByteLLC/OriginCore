package tools.impl.attribute.augments;

import commons.events.impl.EventSubscriber;
import org.bukkit.inventory.ItemStack;
import tools.impl.target.ToolTarget;

public interface AugmentBuilder {

	AugmentBuilder setMinimumBoost(long boost);

	AugmentBuilder setMaximumBoost(long boost);

	AugmentBuilder setInformation(String... information);

	AugmentBuilder setAugmentStack(ItemStack stack);

	AugmentBuilder setAppliedLore(String lore);

	Augment build(EventSubscriber handleEnchant, ToolTarget... targets);
}
