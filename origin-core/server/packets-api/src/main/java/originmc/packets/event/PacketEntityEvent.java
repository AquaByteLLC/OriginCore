package originmc.packets.event;

import commons.entity.EntityEvent;
import commons.entity.subscription.EventSubscription;
import commons.entity.subscription.EventSubscriptions;
import net.minecraft.network.protocol.Packet;
import originmc.packets.PacketEvent;
import java.util.function.Consumer;

public class PacketEntityEvent<T extends PacketEvent<?>> extends EntityEvent<T> {

	public PacketEntityEvent(Consumer<T> entityEventConsumer) {
		super(entityEventConsumer);
	}
}
