package originmc.packets.event;

import commons.entity.EntityEvent;
import originmc.packets.PacketEvent;

import java.util.function.Consumer;

public class PacketEntityEvent<T extends PacketEvent<?>> extends EntityEvent<T> {

	public PacketEntityEvent(Class<T> clazz, Consumer<T> entityEventConsumer) {
		super(clazz, entityEventConsumer);
	}
}