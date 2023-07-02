package commons.util;

import me.lucko.helper.text3.Text;
import me.vadim.util.conf.wrapper.Placeholder;
import net.minecraft.network.protocol.Packet;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.VoxelShape;

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

	//https://www.spigotmc.org/threads/how-to-check-if-a-block-is-realy-a-block.536470/#post-4314270
	public static boolean isCube(Block block) {
		VoxelShape  voxelShape  = block.getCollisionShape();
		BoundingBox boundingBox = block.getBoundingBox();
		return (voxelShape.getBoundingBoxes().size() == 1
				&& boundingBox.getWidthX() == 1.0
				&& boundingBox.getHeight() == 1.0
				&& boundingBox.getWidthZ() == 1.0
		);
	}

}
