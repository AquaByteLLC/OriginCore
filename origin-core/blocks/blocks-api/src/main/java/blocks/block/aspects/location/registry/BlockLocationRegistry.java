package blocks.block.aspects.location.registry;

import blocks.block.builder.OriginBlockBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface BlockLocationRegistry {
	void createBlock(OriginBlockBuilder builder, Location block);
	void deleteBlock(OriginBlockBuilder builder, Location block);
	@NotNull HashMap<Location, OriginBlockBuilder> getBlocks();
}
