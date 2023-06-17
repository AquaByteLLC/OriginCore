package commons.entity;


import commons.entity.subscription.EventSubscription;
import commons.entity.subscription.EventSubscriptions;
import lombok.Getter;

import java.util.function.Consumer;

public class EntityEvent<T> {

	@Getter private final Consumer<T> entityEventConsumer;
	public EntityEvent(Class<T> clazz, Consumer<T> entityEventConsumer) {
		this.entityEventConsumer = entityEventConsumer;
		EventSubscriptions.instance.subscribe(this, getClass(), clazz);
	}

	@EventSubscription
	public void handle(T event) {
		this.entityEventConsumer.accept(event);
	}
}