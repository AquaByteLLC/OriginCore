package blocks.block.factory;

import blocks.block.builder.OriginBlockBuilder;

public interface BlockFactory {
	OriginBlockBuilder newBlock();
}
