package originmc.packets.type;

import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import org.bukkit.entity.Player;
import originmc.packets.PacketEvent;

public class PacketPlayOutBlockBreakImpl extends PacketEvent<PacketPlayOutBlockAction> {
	public PacketPlayOutBlockBreakImpl(Player player, PacketPlayOutBlockAction packet) {
		super(player, packet);
	}
}