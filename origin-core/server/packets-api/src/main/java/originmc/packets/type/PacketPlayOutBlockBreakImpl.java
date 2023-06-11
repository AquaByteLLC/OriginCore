package originmc.packets.type;

import net.minecraft.network.protocol.game.PacketPlayOutBlockBreak;
import org.bukkit.entity.Player;
import originmc.packets.PacketEvent;

public class PacketPlayOutBlockBreakImpl extends PacketEvent<PacketPlayOutBlockBreak> {
	public PacketPlayOutBlockBreakImpl(Player player, PacketPlayOutBlockBreak packet) {
		super(player, packet);
	}
}