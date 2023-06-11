package originmc.packets.event;

import originmc.packets.PacketEvent;
import originmc.subscription.EventSubscription;
import originmc.subscription.EventSubscriptions;

import java.util.function.Consumer;

public class EntityEvent<T extends PacketEvent<?>> {

	private final Consumer<T> packetConsumer;

	/**
	 *
	 * @param packetConsumer you'll
	 *
	 */
	public EntityEvent(final Consumer<T> packetConsumer) {
		this.packetConsumer = packetConsumer;
		EventSubscriptions.instance.subscribe(this, getClass());
	}

	/**
	 *
	 * @param event this is the event which is being used, similar to the bukkit event system, however this one is auto registering
	 *              and works with packets.
	 */
	@EventSubscription
	private void handle(final T event) {
		this.packetConsumer.accept(event);
	}
}
