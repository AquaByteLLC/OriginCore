package tools.impl.ability.cache.impl;

import dev.oop778.shelftor.api.expiring.policy.implementation.TimedExpiringPolicy;
import dev.oop778.shelftor.api.shelf.Shelf;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import tools.impl.ability.cache.IAttributeCache;
import tools.impl.ability.cache.CachedAttribute;
import tools.impl.attribute.ExpiringAttribute;

import java.util.concurrent.TimeUnit;

public class AttributeCache<T extends ExpiringAttribute, A extends CachedAttribute<T>> implements IAttributeCache<T, A> {

	@Override
	public ExpiringShelf<A> getCache() {
		return Shelf.<A>builder()
				.concurrent()
				.weak()
				.expiring().
				usePolicy(TimedExpiringPolicy.create(($) -> {
					final TimeUnit unit = $.getAttribute().getTimeUnit();
					final long time = $.getAttribute().getAmount();
					return new TimedExpiringPolicy.TimedExpirationData(unit, time, false);
				}))
				.build();
	}

	@Override
	public void add(A cached) {
		getCache().add(cached);
	}

	@Override
	public void remove(A cached) {
		getCache().remove(cached);
	}

	@Override
	public void clear() {
		getCache().forEach(this::remove);
	}
}
