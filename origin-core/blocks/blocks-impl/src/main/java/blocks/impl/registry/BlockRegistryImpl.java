package blocks.impl.registry;

import blocks.block.BlockRegistry;
import blocks.block.builder.AspectHolder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BlockRegistryImpl implements BlockRegistry {

	@Getter private final HashMap<String, AspectHolder> blockMap;

	public BlockRegistryImpl() {
		this.blockMap = new HashMap<>();
	}

	@Override
	public void createBlock(AspectHolder block) {
		blockMap.put(block.getName(), block);
	}

	@Override
	public void deleteBlock(AspectHolder block) {
		blockMap.remove(block.getName());
	}

	@Override
	public @NotNull HashMap<String, AspectHolder> getBlocks() {
		return blockMap;
	}
}
