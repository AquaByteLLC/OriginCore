package originmc.injector;


import originmc.handler.OriginChannelDuplexHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelPromise;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import originmc.subscription.EventSubscriptions;

import java.util.concurrent.ConcurrentHashMap;

public class OriginInjector {

	public static ConcurrentHashMap<Player, OriginChannelDuplexHandler> playerHandler;

	static {
		playerHandler = new ConcurrentHashMap<>();
	}

	public OriginInjector() {
		EventSubscriptions.instance.subscribe(this, getClass());
	}

	public OriginChannelDuplexHandler inject(Player player) {
		System.out.println("Starting Injection");
		if (!playerHandler.containsKey(player)) {
			OriginChannelDuplexHandler handler = new OriginChannelDuplexHandler(player);
			startListen(player, handler);
			playerHandler.put(player, handler);
			return handler;
		} else {
			return playerHandler.get(player);
		}
	}

	public void unInject(Player player) {
		if (playerHandler.containsKey(player)) {
			stopListen(player);
			playerHandler.remove(player);
		}

	}

	public static void sendPacket(Player player, Object packet) {
		OriginChannelDuplexHandler handler = playerHandler.get(player);
		if (handler == null) {
			return;
		}

		try {
			handler.write(handler.getCtx(), packet, new DefaultChannelPromise(handler.getPromise().channel()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startListen(Player player, OriginChannelDuplexHandler duplexHandler) {
		ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().b.a.k.pipeline();
		pipeline.addBefore("packet_handler", player.getName(), duplexHandler);
	}

	private void stopListen(Player player) {
		Channel channel = ((CraftPlayer) player).getHandle().b.a.k;

		if (channel == null) return;

		channel.eventLoop().submit(() -> {
			channel.pipeline().remove(player.getName());
		});
	}

}
