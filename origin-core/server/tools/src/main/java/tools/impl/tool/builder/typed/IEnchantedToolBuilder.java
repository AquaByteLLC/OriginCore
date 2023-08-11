package tools.impl.tool.builder.typed;

import tools.impl.attribute.AttributeKey;
import tools.impl.tool.builder.ISpecialToolBuilder;
import tools.impl.tool.impl.EnchantedTool;

public interface IEnchantedToolBuilder extends ISpecialToolBuilder<EnchantedTool> {
	IEnchantedToolBuilder addEnchant(AttributeKey key, int level);
}
