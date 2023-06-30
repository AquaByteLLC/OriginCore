package commons.events.impl.impl;

import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.impl.EventListener;
import commons.util.ReflectUtil;
import commons.util.reflect.FieldAccess;
import commons.util.reflect.Reflection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.SneakyThrows;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vadim
 */
public final class PacketEventListener implements EventListener, Listener {

	private static final String PIPELINE = "PacketInjector";

	private EventRegistry events;

	@Override
	public void startListen(Plugin plugin, EventRegistry events) {
		this.events = events;
		Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getOnlinePlayers().forEach(this::inject));
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public static void sendPacket(Player player, Packet<?> packet) {
		((CraftPlayer) player).getHandle().b.a(packet);
	}

	@Override
	public void ceaseListen() {
		Bukkit.getOnlinePlayers().forEach(this::uninject);
		HandlerList.unregisterAll(this);
		this.events = null;
	}

	@EventHandler
	private void addPlayer(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		inject(player);
	}

	// this does not work :/
//	@EventHandler
//	private void delPlayer(PlayerQuitEvent event) {
//		final Player player = event.getPlayer();
//
//		uninject(player);
//	}

	private void inject(Player player) {
		final Channel channel = getChannel(player);

		PacketInterceptor interceptor = (PacketInterceptor) channel.pipeline().get(PIPELINE);

		if (interceptor == null) {
			interceptor = new PacketInterceptor(player);
			channel.pipeline().addBefore("packet_handler", PIPELINE, interceptor);
		}
	}

	private void uninject(Player player) {
		final Channel channel = getChannel(player);

		channel.eventLoop().execute(() -> channel.pipeline().remove(PIPELINE));
	}

	@SuppressWarnings("NullableProblems")
	private final class PacketInterceptor extends ChannelDuplexHandler {

		final Player player;

		PacketInterceptor(Player player) {
			this.player = player;
		}

		private static final FieldAccess<Iterable<?>> BundlePacket_a = Reflection.unreflectFieldAccess(BundlePacket.class, "a");

		private void unwrap(BundlePacket<?> packet) throws Exception {
			final List<Object> packets = new ArrayList<>();
			for (Packet<?> msg : packet.a()) {
				EventContext context = events.publish(player, msg);
				if (!context.isCancelled())
					packets.add(msg);
			}
			BundlePacket_a.set(packet, packets);
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			try {
				if (msg instanceof BundlePacket<?> bundle) // fire each bundle event separately
					unwrap(bundle);

				EventContext context = events.publish(player, msg); // fire event, agnostic of whether is it is a bundle or not
				if (!context.isCancelled())
					super.write(ctx, msg, promise);
			} catch (Exception e) {
				ReflectUtil.serr("SEVERE: exception in netty thread");
				ReflectUtil.serr(e);
			}
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			try {
				if (msg instanceof BundlePacket<?> bundle) // fire each bundle event separately
					unwrap(bundle);

				EventContext context = events.publish(player, msg); // fire event, agnostic of whether is it is a bundle or not
				if (!context.isCancelled())
					super.channelRead(ctx, msg);
			} catch (Exception e) {
				ReflectUtil.serr("SEVERE: exception in netty thread");
				ReflectUtil.serr(e);
			}
		}

	}

	private static final FieldAccess<NetworkManager> h = Reflection.unreflectFieldAccess(PlayerConnection.class, "h");

	@SneakyThrows
	public static Channel getChannel(Player player) {
		return h.get(((CraftPlayer) player).getHandle().b).m;
	}

}