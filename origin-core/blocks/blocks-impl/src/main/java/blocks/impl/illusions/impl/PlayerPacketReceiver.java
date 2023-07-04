package blocks.impl.illusions.impl;

import blocks.block.util.PacketReceiver;
import commons.util.BukkitUtil;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author vadim
 */
class PlayerPacketReceiver implements PacketReceiver {

	private final UUID uuid;

	PlayerPacketReceiver(UUID uuid) {
		if(uuid == null)
			throw new NullPointerException("uuid");
		this.uuid = uuid;
	}

	@Override
	public boolean appliesTo(Player player) {
		return this.uuid.equals(player.getUniqueId());
	}

	@Override
	public void sendPackets(Packet<?>... packets) {
		Player player = Bukkit.getPlayer(uuid);
		if(player != null)
			for (Packet<?> packet : packets)
				BukkitUtil.sendPacket(player, packet);
	}

}
