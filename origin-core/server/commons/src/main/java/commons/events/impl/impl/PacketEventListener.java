package commons.events.impl.impl;

import commons.events.api.EventContext;
import commons.events.api.EventExecutionException;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import commons.events.impl.EventListener;
import commons.util.ReflectUtil;
import commons.util.reflect.FieldAccess;
import commons.util.reflect.Reflection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Packet interceptor that {@linkplain EventRegistry#publish(EventContext, Object) publishes} Netty events.
 */
public final class PacketEventListener implements EventListener {

	private static final String PIPELINE_INCOMING = "incoming.PacketPublisher";
	private static final String PIPELINE_OUTGOING = "outgoing.PacketPublisher";

	private EventRegistry events;

	@Override
	public void startListen(Plugin plugin, EventRegistry events) {
		this.events = events;
		Bukkit.getOnlinePlayers().forEach(this::inject);
		events.subscribeAll(this);
	}

	@Override
	public void ceaseListen() {
		events.unsubscribe(this);
		Bukkit.getOnlinePlayers().forEach(this::uninject);
		this.events = null;
	}

	@Subscribe
	private void addPlayer(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		inject(player);
	}

	@Subscribe
	private void delPlayer(PlayerQuitEvent event) {
		final Player player = event.getPlayer();

		uninject(player);
	}

	private void inject(Player player) {
		final Channel channel = getChannel(player);

		PacketInterceptor interceptor;

		// inject IncomingInterceptor BEFORE the server's packet handler
		interceptor = (PacketInterceptor) channel.pipeline().get(PIPELINE_INCOMING);
		if (interceptor == null) {
			interceptor = new IncomingInterceptor(player);
			channel.pipeline().addBefore("packet_handler", PIPELINE_INCOMING, interceptor);
		}

		// inject OutoingInterceptor AFTER the server's packet handler
		interceptor = (PacketInterceptor) channel.pipeline().get(PIPELINE_OUTGOING);
		if (interceptor == null) {
			interceptor = new OutgoingInterceptor(player);
			channel.pipeline().addAfter("packet_handler", PIPELINE_OUTGOING, interceptor);
		}
	}

	private void uninject(Player player) {
		final Channel channel = getChannel(player);

		// safely remove both PacketInterceptors
		channel.eventLoop().execute(() -> {
			if(channel.pipeline().get(PIPELINE_INCOMING) != null)
				channel.pipeline().remove(PIPELINE_INCOMING);

			if(channel.pipeline().get(PIPELINE_OUTGOING) != null)
				channel.pipeline().remove(PIPELINE_OUTGOING);
		});
	}

	/**
	 * Abstract event publisher, to be registered either before or after the server's ChannelDuplexHandler.
	 */
	private abstract class PacketInterceptor extends ChannelDuplexHandler {

		final Player player;

		PacketInterceptor(Player player) {
			this.player = player;
		}

		private static final FieldAccess<Iterable<?>> BundlePacket_a = Reflection.unreflectFieldAccess(BundlePacket.class, "a");

		/**
		 * @return {@code true} to pass along {@code msg}, {@code false} to discard {@code msg}
		 */
		protected boolean publish(Object msg) {
			if(events == null) return true;
			try {
				if (msg instanceof BundlePacket<?> bundle) { // fire each bundle event separately
					final List<Object> packets = new ArrayList<>();
					for (Packet<?> a : bundle.a()) {
						EventContext context = events.prepareContext().withPlayer(player).build();
						events.publish(context, a);
						if (!context.isCancelled())
							packets.add(a);
					}
					BundlePacket_a.set(bundle, packets);
					return !packets.isEmpty();
				} else {
					EventContext context = events.prepareContext().withPlayer(player).withInitialCancelledState(false).build();
					events.publish(context, msg);
					return !context.isCancelled();
				}
			} catch (EventExecutionException e) {
				ReflectUtil.serr("WARN: exception while processing event " + msg.getClass().getCanonicalName());
				ReflectUtil.serr(e);
				return true;
			} catch (Exception e) {
				ReflectUtil.serr("SEVERE: exception in netty thread");
				ReflectUtil.serr(e);
				return false;
			}
		}
	}

	/**
	 * ChannelDuplexHandler added <i>before</i> the server's.
	 */
	private final class IncomingInterceptor extends PacketInterceptor {

		IncomingInterceptor(Player player) {
			super(player);
		}

		@Override
		public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
			if (publish(msg))
				super.channelRead(ctx, msg);
		}

	}

	/**
	 * ChannelDuplexHandler added <i>after</i> the server's.
	 */
	private final class OutgoingInterceptor extends PacketInterceptor {

		OutgoingInterceptor(Player player) {
			super(player);
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			if (publish(msg))
				super.write(ctx, msg, promise);
		}
	}

	private static final FieldAccess<NetworkManager> PlayerConnection_h = Reflection.unreflectFieldAccess(PlayerConnection.class, "h");

	public static Channel getChannel(Player player) {
		return PlayerConnection_h.get(((CraftPlayer) player).getHandle().b).m;
	}

	public static void sendPacket(Player player, Packet<?> packet) {
		((CraftPlayer) player).getHandle().b.a(packet);
	}

}