package tools.impl.attribute;

import tools.impl.attribute.registry.impl.BaseAttributeRegistry;

public interface BaseAttributeCommand<T extends BaseAttribute, C extends AttributeFactory<?, ?>> {
	BaseAttributeRegistry<T> getRegistry();
	C getFactory();
}
