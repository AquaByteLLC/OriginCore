package blocks.impl.illusions.impl;

import blocks.block.util.PacketReceiver;
import commons.util.BukkitUtil;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
class ServerPacketReceiver implements PacketReceiver {

	@Override
	public boolean appliesTo(Player player) {
		return player.isOnline();
	}

	@Override
	public void sendPackets(Packet<?>... packets) {
		for (Player player : Bukkit.getOnlinePlayers())
			for (Packet<?> packet : packets)
				BukkitUtil.sendPacket(player, packet);
	}

}
