package blocks.block;

import blocks.block.builder.OriginBlockBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface BlockRegistry {
	void createBlock(OriginBlockBuilder block);
	void deleteBlock(OriginBlockBuilder block);
	@NotNull HashMap<String, OriginBlockBuilder> getBlocks();
}
