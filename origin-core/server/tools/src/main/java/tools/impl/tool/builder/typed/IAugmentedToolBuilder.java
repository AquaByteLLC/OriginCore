package tools.impl.tool.builder.typed;

import tools.impl.attribute.AttributeKey;
import tools.impl.tool.builder.ISpecialToolBuilder;
import tools.impl.tool.impl.AugmentedTool;

public interface IAugmentedToolBuilder extends ISpecialToolBuilder<AugmentedTool> {
	IAugmentedToolBuilder addAugment(AttributeKey key, long boost);
	IAugmentedToolBuilder setOpenSlots(int slots);
}
