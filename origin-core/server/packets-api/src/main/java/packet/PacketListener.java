package packet;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author vadim
 */
public interface PacketListener<P extends Packet<?>> {

	@NotNull
	Class<P> getType();

	@Nullable
	P mutate(Player player, P packet);

}