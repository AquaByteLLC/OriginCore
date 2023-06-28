package blocks.block.aspects.location.registry;

import blocks.block.builder.AspectHolder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface BlockLocationRegistry {

	void createBlock(AspectHolder editor, Location block);

	void deleteBlock(AspectHolder editor, Location block);

	@NotNull HashMap<Location, AspectHolder> getBlocks();

}
