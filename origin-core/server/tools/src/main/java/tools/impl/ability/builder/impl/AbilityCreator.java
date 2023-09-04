package tools.impl.ability.builder.impl;

import commons.Commons;
import commons.events.impl.impl.UnexpiringSubscriber;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import lombok.Getter;
import org.bukkit.event.Event;
import tools.impl.ability.builder.IAbilityCreator;
import tools.impl.ability.cache.CachedAttribute;
import tools.impl.attribute.BaseAttribute;
import tools.impl.sched.CacheInvalidator;

import java.util.function.Consumer;

public class AbilityCreator<T extends BaseAttribute, A extends CachedAttribute<T>> implements IAbilityCreator<T, A> {

	@Getter private ExpiringShelf<A> cache;
	private final CacheInvalidator<A> invalidator;
	private Consumer<? extends Event> whileIn;
	private Consumer<? extends Event> whileOut;
	private ExpiringShelf.ExpirationHandler<A> expirationHandler;

	public AbilityCreator() {
		this.invalidator = new CacheInvalidator<>();
	}

	@Override
	public IAbilityCreator<T, A> setExpiringShelf(ExpiringShelf<A> cache) {
		this.cache = cache;
		return this;
	}

	@Override
	public <R extends Event> IAbilityCreator<T, A> setWhileInCache(Class<R> event, Consumer<R> whileIn) {
		this.whileIn = whileIn;
		new UnexpiringSubscriber<>(event, (ctx, e) -> {
				whileIn.accept(e);
		}).bind(Commons.events());
		return this;
	}

	@Override
	public <R extends Event> IAbilityCreator<T, A> setWhileNotInCache(Class<R> event, Consumer<R> whileOut) {
		this.whileOut = whileOut;
		new UnexpiringSubscriber<>(event, (ctx, e) -> {
			whileOut.accept(e);
		}).bind(Commons.events());
		return this;
	}

	@Override
	public IAbilityCreator<T, A> setExpirationHandler(ExpiringShelf.ExpirationHandler<A> handler) {
		this.expirationHandler = handler;
		return this;
	}

	public void build() {
		this.cache.onExpire(this.expirationHandler);
		this.invalidator.add(this.cache);
		this.invalidator.activate();
	}
}
