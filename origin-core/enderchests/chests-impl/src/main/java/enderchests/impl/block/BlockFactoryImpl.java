package enderchests.impl.block;

import enderchests.block.BlockHighlight;
import enderchests.block.FakeBlock;
import enderchests.block.BlockFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public class BlockFactoryImpl implements BlockFactory {

	@Override
	public FakeBlock newFakeBlock(Location location, BlockData projected) {
		return new PacketBasedFakeBlock(location, projected);
	}

	@Override
	public BlockHighlight newBlockHighlight(Location location, ChatColor color, Consumer<Player> onInteract) {
		return new FallingBlockHighlight(location, color, onInteract);
	}

}
