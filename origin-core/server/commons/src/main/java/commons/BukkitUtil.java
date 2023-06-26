package commons;

import me.lucko.helper.text3.Text;
import me.vadim.util.conf.wrapper.Placeholder;
import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author vadim
 */
public class BukkitUtil {

	@SuppressWarnings("DataFlowIssue")
	public static void formatItem(Placeholder pl, ItemStack item) {
		item.editMeta(meta -> {
			if (meta.hasDisplayName())
				meta.setDisplayName(Text.colorize(pl.format(meta.getDisplayName())));
			if (meta.hasLore())
				meta.setLore(meta.getLore().stream().map(pl::format).map(Text::colorize).toList());
		});
	}

	public static void sendPacket(Player player, Packet<?> packet) {
		((CraftPlayer) player).getHandle().b.a(packet);
	}

}
