package tools.impl.ability.builder;

import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import org.bukkit.event.Event;
import tools.impl.ability.cache.CachedAttribute;
import tools.impl.attribute.ExpiringAttribute;

import java.util.function.Consumer;

public interface IAbilityCreator<T extends ExpiringAttribute, A extends CachedAttribute<T>> {
	IAbilityCreator<T, A> setExpiringShelf(ExpiringShelf<A> cache);

	IAbilityCreator<T, A> setWhileInCache(Consumer<A> whileIn);

	IAbilityCreator<T, A> setWhileNotInCache(Consumer<A> whileOut);

	IAbilityCreator<T, A> setExpirationHandler(ExpiringShelf.ExpirationHandler<A> handler);

	<R extends Event> void create(Class<R> clazz, A attribute);
}
