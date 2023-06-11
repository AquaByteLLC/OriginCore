package originmc;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.entity.subscription.EventSubscriptions;
import originmc.injector.OriginInjector;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import originmc.packets.PacketEvent;
import originmc.packets.event.PacketEntityEvent;
import originmc.packets.type.PacketPlayOutBlockBreakImpl;

import java.util.function.Consumer;

public class PacketAPI {
	@Getter private final EventSubscriptions eventSubscriptions;
	@Getter private final OriginInjector packetInjector;
	private static Injector injector;

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The PacketAPI hasn't been initialized anywhere. Create a new instance of the PacketAPI class in the 'onEnable' method.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	public PacketAPI(JavaPlugin javaPlugin) {
		injector = Guice.createInjector(new PacketModule(javaPlugin));
		this.eventSubscriptions = new EventSubscriptions(javaPlugin);
		this.packetInjector = new OriginInjector();
	}

	public <T extends PacketEvent<?>> PacketEntityEvent<T> mapEvent(final Class<T> packetEvent, final Consumer<T> packetConsumer) {
		return new PacketEntityEvent<>(packetConsumer);
	}

	static class PacketModule extends AbstractModule {
		private final JavaPlugin plugin;

		public PacketModule(final JavaPlugin plugin) {
			this.plugin = plugin;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
		}
	}
}

