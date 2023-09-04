package tools.impl.ability.cache;

import tools.impl.attribute.BaseAttribute;

public interface CachedAttribute<T extends BaseAttribute> {
	T getAttribute();
}
