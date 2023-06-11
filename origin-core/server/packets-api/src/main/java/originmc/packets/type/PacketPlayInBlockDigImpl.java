package originmc.packets.type;


import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import org.bukkit.entity.Player;
import originmc.packets.PacketEvent;

public class PacketPlayInBlockDigImpl extends PacketEvent<PacketPlayInBlockDig> {

	public PacketPlayInBlockDigImpl(Player player, PacketPlayInBlockDig packet) {
		super(player, packet);
	}
}
