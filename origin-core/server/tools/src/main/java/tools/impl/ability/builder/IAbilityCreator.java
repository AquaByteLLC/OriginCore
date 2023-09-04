package tools.impl.ability.builder;

import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import org.bukkit.event.Event;
import tools.impl.ability.cache.CachedAttribute;
import tools.impl.attribute.BaseAttribute;

import java.util.function.Consumer;

public interface IAbilityCreator<T extends BaseAttribute, A extends CachedAttribute<T>> {
	IAbilityCreator<T, A> setExpiringShelf(ExpiringShelf<A> cache);

	<R extends Event> IAbilityCreator<T, A> setWhileInCache(Class<R> event, Consumer<R> whileIn);

	<R extends Event> IAbilityCreator<T, A> setWhileNotInCache(Class<R> event, Consumer<R> whileOut);

	IAbilityCreator<T, A> setExpirationHandler(ExpiringShelf.ExpirationHandler<A> handler);

	void build();
}
