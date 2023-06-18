package enchants.impl;

import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author vadim
 */
public class EventsDemo {

	public EventsDemo(EventRegistry events) {
		events.subscribeAll(this);
	}

	@Subscribe
	private void onBlockDig(EventContext context, PacketPlayInBlockDig packet) {
		System.out.println(context.getPlayer().getName() + " broke a block at Y-level: "+ packet.a().v());
	}

	@Subscribe
	private void onJoin(EventContext context, PlayerJoinEvent event) {
		System.out.println(event.getPlayer() + " has triggered onJoin()");
	}

}
