package blocks.impl.illusions.impl;

import blocks.block.util.PacketReceiver;
import commons.util.BukkitUtil;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

/**
 * @author vadim
 */
class PlayerPacketReceiver implements PacketReceiver {

	private final WeakReference<Player> player;

	PlayerPacketReceiver(Player player) {
		this.player = new WeakReference<>(player);
	}

	@Override
	public boolean appliesTo(Player player) {
		return this.player.refersTo(player);
	}

	@Override
	public void sendPackets(Packet<?>... packets) {
		if(!player.refersTo(null))
			for (Packet<?> packet : packets)
				BukkitUtil.sendPacket(player.get(), packet);
	}

}
