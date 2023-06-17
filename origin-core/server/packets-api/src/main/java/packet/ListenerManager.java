package packet;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiFunction;

/**
 * @author vadim
 */
public interface ListenerManager extends Listener {

	/**
	 * Convenience method to start sending packets through the provided listener.
	 *
	 * @param clazz   the packet to listen for
	 * @param mutator the lambda argument that acts as the listener
	 *
	 * @return the created {@link PacketListener} instance
	 *
	 * @see PacketListener
	 */
	<P extends Packet<?>> PacketListener<P> register(Class<P> clazz, BiFunction<Player, P, P> mutator);

	/**
	 * Start sending packets through the provided listener.
	 *
	 * @param listener the listener to register
	 */
	<P extends Packet<?>> void register(PacketListener<P> listener);

	/**
	 * Stop sending packets through the provided listener.
	 *
	 * @param listener the listener to unregister
	 *
	 * @return {@literal true} if the listener was present, {@literal false} otherwise
	 */
	<P extends Packet<?>> boolean unregister(PacketListener<P> listener);

	/**
	 * Begin listening for packets for all currently online players.<p>
	 * <p>
	 * This method only needs to be called once by the plugin owning this {@link ListenerManager} instance, but is safe to be called multiple times.
	 * You are intended to call this {@link JavaPlugin#onEnable()}.<p>
	 * <p>
	 * Note that this is reversible and calling {@link #drop(Plugin)} will stop listening for packets again.
	 *
	 * @param plugin the plugin, used to register a task and an event handler
	 */
	void listen(Plugin plugin);

	/**
	 * Stop listening for packets for all currently online players.<p>
	 * <p>
	 * This method only needs to be called once by the plugin owning this {@link ListenerManager} instance, but is safe to be called multiple times.
	 * You <b>must</b> call this {@link JavaPlugin#onDisable()} to prevent issues upon reload.<p>
	 * <p>
	 * Note that this is reversible and calling {@link #listen(Plugin)} will begin listening for packets again.
	 *
	 * @param plugin the plugin, used to register a task
	 */
	void drop(Plugin plugin);
}