package packet.impl;

import io.netty.channel.*;
import lombok.SneakyThrows;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import packet.ListenerManager;
import packet.PacketListener;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author vadim
 */
public final class PacketInjector implements ListenerManager {

	private static final String                  PIPELINE  = "PacketInjector";
	private final        Set<ListenerWrapper<?>> listeners = new HashSet<>();

	@Override
	public <P extends Packet<?>> PacketListener<P> register(final Class<P> clazz, final BiFunction<Player, P, P> mutator) {
		final PacketListener<P> listener = new PacketListener<>() {
			@Override
			public @NotNull Class<P> getType() {
				return clazz;
			}

			@Nullable
			@Override
			public P mutate(final Player player, final P packet) {
				return mutator.apply(player, packet);
			}
		};

		register(listener);

		return listener;
	}

	@Override
	public <P extends Packet<?>> void register(final PacketListener<P> listener) { listeners.add(new ListenerWrapper<>(listener)); }

	@Override
	public <P extends Packet<?>> boolean unregister(final PacketListener<P> listener) { return listeners.remove(new ListenerWrapper<>(listener)); }

	@Override
	public void listen(Plugin plugin) {
		Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getOnlinePlayers().forEach(this::inject));
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void drop(final Plugin plugin) {
		Bukkit.getOnlinePlayers().forEach(this::uninject);
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void addPlayer(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		inject(player);
	}

	@EventHandler
	private void removePlayer(final PlayerQuitEvent event){
		final Player player = event.getPlayer();

		uninject(player);
	}

	private void inject(final Player player) {
		final Channel channel = getChannel(player);

		PacketInterceptor interceptor = (PacketInterceptor) channel.pipeline().get(PIPELINE);

		if (interceptor == null) {
			interceptor = new PacketInterceptor(player);
			channel.pipeline().addBefore("packet_handler", PIPELINE, interceptor);
		}
	}

	private void uninject(final Player player) {
		final Channel channel = getChannel(player);

		channel.eventLoop().execute(() -> channel.pipeline().remove(PIPELINE));
	}

	private final class PacketInterceptor extends ChannelDuplexHandler {

		final Player player;

		PacketInterceptor(final Player player) {
			this.player = player;
		}

		@Override
		public void write(final ChannelHandlerContext ctx, Object msg, final ChannelPromise promise) throws Exception {
			for (final ListenerWrapper<?> listener : listeners) { msg = listener.mutate(player, msg); }

			if (msg != null) { super.write(ctx, msg, promise); }
		}

		@Override
		public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
			for (final ListenerWrapper<?> listener : listeners) { msg = listener.mutate(player, msg); }

			if (msg != null) { super.channelRead(ctx, msg); }
		}
	}

	private static final class ListenerWrapper<P extends Packet<?>> {

		final PacketListener<P> listener;

		ListenerWrapper(final PacketListener<P> listener) {
			this.listener = listener;
		}

		Object mutate(final Player player, final Object msg) {
			if (msg != null && listener.getType().isAssignableFrom(msg.getClass())) {
				return listener.mutate(player, (P) msg);
			} else {
				return msg;
			}
		}

		@Override
		public int hashCode() {
			return listener.hashCode();
		}

	}

	private static final Field h;

	static {
		Field field;
		try {
			field = PlayerConnection.class.getDeclaredField("h");
			field.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		h = field;
	}

	@SneakyThrows
	public static Channel getChannel(Player player) {
		return ((NetworkManager) h.get(((CraftPlayer) player).getHandle().b)).m;
	}

}