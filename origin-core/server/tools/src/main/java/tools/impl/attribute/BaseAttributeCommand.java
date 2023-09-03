package tools.impl.attribute;

import tools.impl.registry.AttributeRegistry;

public interface BaseAttributeCommand<T extends BaseAttribute, C extends AttributeFactory<?, ?>> {
	AttributeRegistry<T> getRegistry();
	C getFactory();
}
