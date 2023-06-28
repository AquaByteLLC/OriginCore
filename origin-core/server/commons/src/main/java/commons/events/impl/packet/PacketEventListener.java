package commons.events.impl.packet;

import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.impl.EventListener;
import commons.util.ReflectUtil;
import io.netty.channel.*;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.network.NetworkManager;
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

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

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

	private final class PacketInterceptor extends ChannelDuplexHandler {

		final Player player;

		PacketInterceptor(Player player) {
			this.player = player;
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			try {
				EventContext context = events.publish(player, msg);
				if (events == null || !context.isCancelled())
					super.write(ctx, context.getEvent(), promise);
			} catch (Exception e) {
				ReflectUtil.serr("SEVERE: exception in netty thread");
				ReflectUtil.serr(e);
			}
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			try {
				EventContext context = events.publish(player, msg);
				if (events == null || !context.isCancelled())
					super.channelRead(ctx, context.getEvent());
			} catch (Exception e) {
				ReflectUtil.serr("SEVERE: exception in netty thread");
				ReflectUtil.serr(e);
			}
		}
	}

	private static final VarHandle h;

	static {
		Field     field;
		VarHandle handle;
		try {
			field = PlayerConnection.class.getDeclaredField("h");
			field.setAccessible(true);
			handle = MethodHandles.lookup().unreflectVarHandle(field);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		h = handle;
	}

	@SneakyThrows
	public static Channel getChannel(Player player) {
		return ((NetworkManager) h.get(((CraftPlayer) player).getHandle().b)).m;
	}

}