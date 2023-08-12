package tools.impl.ability.cache;

import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import tools.impl.attribute.ExpiringAttribute;

public interface IAttributeCache<A extends ExpiringAttribute, T extends CachedAttribute<A>> {
	ExpiringShelf<T> getCache();

	void add(T cached);

	void remove(T cached);

	void clear();
}
