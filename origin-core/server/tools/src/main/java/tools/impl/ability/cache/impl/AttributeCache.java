package tools.impl.ability.cache.impl;

import dev.oop778.shelftor.api.expiring.policy.implementation.TimedExpiringPolicy;
import dev.oop778.shelftor.api.shelf.Shelf;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import tools.impl.ability.cache.CachedAttribute;
import tools.impl.ability.cache.IAttributeCache;
import tools.impl.attribute.ExpiringAttribute;
import tools.impl.sched.CacheInvalidator;

import java.util.concurrent.TimeUnit;

public class AttributeCache<T extends ExpiringAttribute, A extends CachedAttribute<T>> implements IAttributeCache<T, A> {

	private final ExpiringShelf<A> shelf;
	private final CacheInvalidator<A> cacheInvalidator;

	@SuppressWarnings("all")
	public AttributeCache() {
		this.shelf = Shelf.<A>builder()
				.concurrent()
				.expiring().
				usePolicy(TimedExpiringPolicy.create(($) -> {
					final TimeUnit unit = $.getAttribute().getTimeUnit();
					System.out.println(unit.toString() + " UNIT!");
					final long time = $.getAttribute().getAmount();
					System.out.println(time + " TIME!");
					return new TimedExpiringPolicy.TimedExpirationData(unit, time, false);
				}))
				.build();

		this.cacheInvalidator = new CacheInvalidator<>();
		this.cacheInvalidator.add(this.shelf);
		this.cacheInvalidator.activate();
	}

	@Override
	public ExpiringShelf<A> getCache() {
		return this.shelf;
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
