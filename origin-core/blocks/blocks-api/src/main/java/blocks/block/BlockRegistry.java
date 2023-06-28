package blocks.block;

import blocks.block.builder.AspectHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface BlockRegistry {
	void createBlock(AspectHolder block);
	void deleteBlock(AspectHolder block);
	@NotNull HashMap<String, AspectHolder> getBlocks();
}
