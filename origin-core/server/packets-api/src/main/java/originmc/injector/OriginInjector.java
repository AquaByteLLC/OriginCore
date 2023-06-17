package originmc.injector;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import commons.entity.subscription.EventSubscription;
import commons.entity.subscription.EventSubscriptions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelPromise;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import originmc.PacketAPI;
import originmc.handler.OriginChannelDuplexHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OriginInjector {

	private static final ConcurrentHashMap<Player, OriginChannelDuplexHandler> playerHandler = new ConcurrentHashMap<>();

	public OriginInjector() {
		EventSubscriptions.instance.subscribe(this, getClass());
	}

	public OriginChannelDuplexHandler inject(Player player) {
		if (!playerHandler.containsKey(player)) {
			OriginChannelDuplexHandler handler = new OriginChannelDuplexHandler(player);
			startListen(player, handler);
			playerHandler.put(player, handler);
			return handler;
		} else {
			return playerHandler.get(player);
		}
	}

	@EventSubscription
	public void playerJoinEvent(PlayerJoinEvent event) {
		System.out.println("Injected");
		inject(event.getPlayer());
	}

	@EventSubscription
	public void playerQuitEvent(PlayerQuitEvent event) {
		unInject(event.getPlayer());
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
		ChannelPipeline pipeline = PacketAPI.getChannel(player).pipeline();
		pipeline.addBefore("packet_handler", player.getName(), duplexHandler);
	}

	private void stopListen(Player player) {
		Channel channel = PacketAPI.getChannel(player);

		if (channel == null) return;

		channel.eventLoop().submit(() -> {
			channel.pipeline().remove(player.getName());
		});
	}

}
