package tools.impl.ability.cache;

import tools.impl.attribute.ExpiringAttribute;

public interface CachedAttribute<T extends ExpiringAttribute> {
	T getAttribute();
}
