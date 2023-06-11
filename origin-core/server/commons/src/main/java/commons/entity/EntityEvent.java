package commons.entity;

import commons.entity.subscription.EventSubscription;
import commons.entity.subscription.EventSubscriptions;

import java.util.function.Consumer;

public abstract class EntityEvent<T> {

	private final Consumer<T> entityEventConsumer;
	public EntityEvent(Consumer<T> entityEventConsumer) {
		this.entityEventConsumer = entityEventConsumer;
		EventSubscriptions.instance.subscribe(this, getClass());
	}

	@EventSubscription
	public void handle(T event) {
		this.entityEventConsumer.accept(event);
	}
}
