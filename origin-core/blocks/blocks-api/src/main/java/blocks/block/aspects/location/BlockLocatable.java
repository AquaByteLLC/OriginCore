package blocks.block.aspects.location;

import blocks.block.aspects.GeneralAspect;
import org.bukkit.block.Block;

public interface BlockLocatable extends GeneralAspect, Locatable {
	Block getBlock();
	BlockLocatable setBlock(Block block);

}
