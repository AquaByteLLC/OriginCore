package enchants.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.entity.subscription.EventSubscription;
import enchants.EnchantAPI;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import originmc.PacketAPI;
import originmc.packets.event.PacketEntityEvent;
import originmc.packets.type.PacketPlayInBlockDigImpl;
import packet.ListenerManager;
import packet.impl.PacketInjector;

public class EnchantPlugin extends ExtendedJavaPlugin {
	private static Injector injector;

	private PacketEntityEvent<?> STRONGreference;
	private ListenerManager packets;
	@Override
	protected void enable() {
		injector = Guice.createInjector(new EnchantPluginModule(this));
		System.out.println("Hello World");

		packets = new PacketInjector();
		packets.listen(this);


		packets.register(PacketPlayInBlockDig.class, (player, packet) -> {
			System.out.println(packet.a().v());
			return packet;
		});

//		STRONGreference = new PacketEntityEvent<>(PacketPlayInBlockDigImpl.class, packet -> {
//			System.out.println(packet.getPacket().a().v());
//			System.out.println("packeg play in block brake");
//		});

//		BukkitEntityEvent<BlockBreakEvent> bk = new BukkitEntityEvent<>(BlockBreakEvent.class, event -> {
//			System.out.println(event.getBlock().getX());
//		});
	}

	@Override
	protected void disable() {
		saveConfig();
		packets.drop(this);
	}

	@EventSubscription
	public void onBlockBreak(BlockBreakEvent event) {
		event.getPlayer().sendMessage("This is working: " + event.getBlock().getX());
	}

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The EnchantPlugin hasn't been initialized.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	static class EnchantPluginModule extends AbstractModule {
		private final JavaPlugin plugin;
		private final PacketAPI packetAPI;
		private final EnchantAPI enchantAPI;

		@SuppressWarnings("all")
		public EnchantPluginModule(final JavaPlugin plugin) {
			this.plugin = plugin;
			this.packetAPI = new PacketAPI(plugin);
			this.enchantAPI = new EnchantAPI(plugin);
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(EnchantAPI.class).toInstance(enchantAPI);
			this.bind(PacketAPI.class).toInstance(packetAPI);
		}
	}
}
