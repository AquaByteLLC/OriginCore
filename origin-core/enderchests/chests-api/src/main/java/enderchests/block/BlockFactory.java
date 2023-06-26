package enderchests.block;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public interface BlockFactory {

	FakeBlock newFakeBlock(Location location, BlockData projected);

	BlockHighlight newBlockHighlight(Location location, ChatColor color, Consumer<Player> onInteract);

}
