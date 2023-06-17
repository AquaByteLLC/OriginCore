package originmc;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.entity.subscription.EventSubscriptions;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import originmc.injector.OriginInjector;

import java.lang.reflect.Field;

public class PacketAPI {
	@Getter private final EventSubscriptions eventSubscriptions;
	@Getter private final OriginInjector packetInjector;
	@Getter private final Injector injector;

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


	public PacketAPI(JavaPlugin javaPlugin) {
		injector = Guice.createInjector(new PacketModule(javaPlugin));
		this.eventSubscriptions = new EventSubscriptions(javaPlugin);
		this.packetInjector = new OriginInjector();
	}

	@SneakyThrows
	public static Channel getChannel(Player player) {
		return ((NetworkManager) h.get(((CraftPlayer) player).getHandle().b)).m;
	}

	static class PacketModule extends AbstractModule {
		private final JavaPlugin plugin;

		PacketModule(final JavaPlugin plugin) {
			this.plugin = plugin;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
		}
	}
}

