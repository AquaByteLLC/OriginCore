package blocks.impl.registry;

import blocks.block.BlockRegistry;
import blocks.block.builder.OriginBlockBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BlockRegistryImpl implements BlockRegistry {

	@Getter private final HashMap<String, OriginBlockBuilder> blockMap;

	public BlockRegistryImpl() {
		this.blockMap = new HashMap<>();
	}

	@Override
	public void createBlock(OriginBlockBuilder block) {
		blockMap.put(block.getName(), block);
	}

	@Override
	public void deleteBlock(OriginBlockBuilder block) {
		blockMap.remove(block.getName());
	}

	@Override
	public @NotNull HashMap<String, OriginBlockBuilder> getBlocks() {
		return blockMap;
	}
}
