package tools.impl.tool.builder.typed;

import tools.impl.attribute.AttributeKey;
import tools.impl.tool.builder.ISpecialToolBuilder;
import tools.impl.tool.impl.SkinnedTool;

public interface ISkinnedToolBuilder extends ISpecialToolBuilder<SkinnedTool> {
	ISkinnedToolBuilder setSkin(AttributeKey key);
	ISkinnedToolBuilder removeSkin();
}
