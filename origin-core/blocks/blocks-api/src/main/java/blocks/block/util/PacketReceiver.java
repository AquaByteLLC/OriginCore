package blocks.block.util;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;

public interface PacketReceiver {

	/**
	 * @return does this receiver send to {@code player}?
	 */
	boolean appliesTo(Player player);

	void sendPackets(Packet<?>... packets);

}
