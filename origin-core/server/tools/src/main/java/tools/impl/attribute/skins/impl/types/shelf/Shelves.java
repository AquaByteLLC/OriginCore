package tools.impl.attribute.skins.impl.types.shelf;

import dev.oop778.shelftor.api.expiring.policy.implementation.TimedExpiringPolicy;
import dev.oop778.shelftor.api.shelf.Shelf;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import tools.impl.ability.cache.types.impl.PlayerCachedAttribute;
import tools.impl.attribute.skins.Skin;

import java.util.concurrent.TimeUnit;

public interface Shelves {
	ExpiringShelf<PlayerCachedAttribute<Skin>> flamingoShelf = Shelf.<PlayerCachedAttribute<Skin>>builder()
			.concurrent()
			.expiring()
			.usePolicy(TimedExpiringPolicy.create(10, TimeUnit.SECONDS, false))
			.expireCheckInterval(2)
			.build();

	static void init() {}
}
