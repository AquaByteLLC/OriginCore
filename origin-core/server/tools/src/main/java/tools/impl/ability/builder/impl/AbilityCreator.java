package tools.impl.ability.builder.impl;

import commons.events.impl.impl.DetachedSubscriber;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import org.bukkit.event.Event;
import tools.impl.ability.builder.IAbilityCreator;
import tools.impl.ability.cache.CachedAttribute;
import tools.impl.attribute.ExpiringAttribute;

import java.util.function.Consumer;

public class AbilityCreator<T extends ExpiringAttribute, A extends CachedAttribute<T>> implements IAbilityCreator<T, A> {

	private ExpiringShelf<A>  cache;
	private Consumer<A> whileIn;
	private Consumer<A> whileOut;
	private DetachedSubscriber<?> subscriber;
	private ExpiringShelf.ExpirationHandler<A> expirationHandler;

	@Override
	public IAbilityCreator<T, A> setExpiringShelf(ExpiringShelf<A> cache) {
		this.cache = cache;
		return this;
	}

	@Override
	public IAbilityCreator<T, A> setWhileInCache(Consumer<A> whileIn) {
		this.whileIn = whileIn;
		return this;
	}

	@Override
	public IAbilityCreator<T, A> setWhileNotInCache(Consumer<A> whileOut) {
		this.whileOut = whileOut;
		return this;
	}

	@Override
	public IAbilityCreator<T, A> setExpirationHandler(ExpiringShelf.ExpirationHandler<A> handler) {
		this.expirationHandler = handler;
		return this;
	}

	@Override
	public <R extends Event> void create(Class<R> clazz, A attribute) {
		this.cache.onExpire(expirationHandler);

		this.subscriber = new DetachedSubscriber<>(clazz, ((context, event) -> {
			if (cache.contains(attribute)) whileIn.accept(attribute);
		    else whileOut.accept(attribute);
		}));
	}

}
